package service;

import model.Advert;
import model.Photo;
import org.apache.commons.lang3.StringUtils;
import repository.interops.PhotoRepositoryJv;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class PhotoService {

    @Inject
    private PhotoRepositoryJv photoMapper;

    private ImagePHash imagePHash = new ImagePHash();

    public List<Photo> getPhotos(Advert advert) {
        return photoMapper.getPhotos(advert.id());
    }

    public Map<Integer, Photo> getMainPhotos(List<Advert> adverts) {
        if (adverts.isEmpty()) {
            return Collections.emptyMap();
        }
        return photoMapper.getMainPhotos(adverts
                .stream()
                .map(Advert::id)
                .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(v -> v.advertId(), v -> v));
    }

    public <T> T searchForSame(Map<Long, T> hashesMap, long hash) {
        if (hashesMap == null) {
            return null;
        }
        Optional<Map.Entry<Long, T>> matchedEntry = hashesMap.entrySet().parallelStream()
                .filter(entry -> imagePHash.isTheSame(hash, entry.getKey()))
                .findAny();
        if (matchedEntry.isPresent()) {
            return matchedEntry.get().getValue();
        }
        return null;
    }

    public long calculateHash(BufferedImage bufferedImage) {
        return imagePHash.getHash(bufferedImage);
    }


    /*
     * pHash-like image hash.
     * Author: Elliot Shepherd (elliot@jarofworms.com
     * Based On: http://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
     */
    public static class ImagePHash {

        private int DISTANCE_MAX = 3;

        private int size = 32;
        private int smallerSize = 8;

        public ImagePHash() {
            initCoefficients();
        }

        public ImagePHash(int size, int smallerSize) {
            this.size = size;
            this.smallerSize = smallerSize;

            initCoefficients();
        }

        public boolean isTheSame(long val1, long val2) {
            return distance(val1, val2) <= DISTANCE_MAX;
        }

        private String appendZeros(String value) {
            int count = 64 - value.length();
            if (count > 0) {
                return StringUtils.repeat('0', count) + value;
            }
            return value;
        }

        public int distance(long val1, long val2) {
            String s1 = appendZeros(Long.toBinaryString(val1));
            String s2 = appendZeros(Long.toBinaryString(val2));

            int counter = 0;
            for (int k = 0; k < s1.length(); k++) {
                if (s1.charAt(k) != s2.charAt(k)) {
                    counter++;
                }
            }
            return counter;
        }

        // Returns a 'binary string' (like. 001010111011100010) which is easy to do a hamming distance on.
        public long getHash(BufferedImage img) {

                /* 1. Reduce size.
                 * Like Average Hash, pHash starts with a small image.
                 * However, the image is larger than 8x8; 32x32 is a good size.
                 * This is really done to simplify the DCT computation and not
                 * because it is needed to reduce the high frequencies.
                 */
            img = resize(img, size, size);

                /* 2. Reduce color.
                 * The image is reduced to a grayscale just to further simplify
                 * the number of computations.
                 */
            img = grayscale(img);

            double[][] vals = new double[size][size];

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    vals[x][y] = getBlue(img, x, y);
                }
            }

                /* 3. Compute the DCT.
                 * The DCT separates the image into a collection of frequencies
                 * and scalars. While JPEG uses an 8x8 DCT, this algorithm uses
                 * a 32x32 DCT.
                 */
            long start = System.currentTimeMillis();
            double[][] dctVals = applyDCT(vals);

                /* 4. Reduce the DCT.
                 * This is the magic step. While the DCT is 32x32, just keep the
                 * top-left 8x8. Those represent the lowest frequencies in the
                 * picture.
                 */
                /* 5. Compute the average value.
                 * Like the Average Hash, compute the mean DCT value (using only
                 * the 8x8 DCT low-frequency values and excluding the first term
                 * since the DC coefficient can be significantly different from
                 * the other values and will throw off the average).
                 */
            double total = 0;

            for (int x = 0; x < smallerSize; x++) {
                for (int y = 0; y < smallerSize; y++) {
                    total += dctVals[x][y];
                }
            }
            total -= dctVals[0][0];

            double avg = total / (double) ((smallerSize * smallerSize) - 1);

                /* 6. Further reduce the DCT.
                 * This is the magic step. Set the 64 hash bits to 0 or 1
                 * depending on whether each of the 64 DCT values is above or
                 * below the average value. The result doesn't tell us the
                 * actual low frequencies; it just tells us the very-rough
                 * relative scale of the frequencies to the mean. The result
                 * will not vary as long as the overall structure of the image
                 * remains the same; this can survive gamma and color histogram
                 * adjustments without a problem.
                 */
            String hash = "";

            for (int x = 0; x < smallerSize; x++) {
                for (int y = 0; y < smallerSize; y++) {
                    if (x != 0 && y != 0) {
                        hash += (dctVals[x][y] > avg ? "1" : "0");
                    }
                }
            }

            return Long.valueOf(hash, 2);
        }

        private BufferedImage resize(BufferedImage image, int width, int height) {
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
            return resizedImage;
        }

        private ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

        private BufferedImage grayscale(BufferedImage img) {
            colorConvert.filter(img, img);
            return img;
        }

        private int getBlue(BufferedImage img, int x, int y) {
            return (img.getRGB(x, y)) & 0xff;
        }

        // DCT function stolen from http://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java

        private double[] c;

        private void initCoefficients() {
            c = new double[size];

            for (int i = 1; i < size; i++) {
                c[i] = 1;
            }
            c[0] = 1 / Math.sqrt(2.0);
        }

        private double[][] applyDCT(double[][] f) {
            int N = size;

            double[][] F = new double[N][N];
            for (int u = 0; u < N; u++) {
                for (int v = 0; v < N; v++) {
                    double sum = 0.0;
                    for (int i = 0; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI) * Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * (f[i][j]);
                        }
                    }
                    sum *= ((c[u] * c[v]) / 4.0);
                    F[u][v] = sum;
                }
            }
            return F;
        }

    }

}

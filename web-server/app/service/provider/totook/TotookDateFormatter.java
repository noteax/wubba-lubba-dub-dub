package service.provider.totook;


import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Singleton
public class TotookDateFormatter {

    private ZoneId zoneId = ZoneId.of("UTC+03:00");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public long getTimestamp(String date) {
        return parseDateTime(toStrictFormat(date)).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    String toStrictFormat(String date) {
        LocalDate now = LocalDate.now(zoneId);

        String dateInLowerCase = date.toLowerCase().trim();
        if (dateInLowerCase.startsWith("сегодня")) {
            int dayOfMonth = now.getDayOfMonth();
            int monthValue = now.getMonthValue();
            return String.format("%s %02d.%02d.%d", dateInLowerCase.split(" ")[2], dayOfMonth, monthValue,
                    now.getYear());
        } else if (dateInLowerCase.startsWith("вчера")) {
            int dayOfMonth = now.minusDays(1).getDayOfMonth();
            int monthValue = now.minusDays(1).getMonthValue();
            return String.format("%s %02d.%02d.%d", dateInLowerCase.split(" ")[2], dayOfMonth, monthValue,
                    now.getYear());
        }

        return String.format("00:00 %s", dateInLowerCase);
    }


    LocalDateTime parseDateTime(String dateTimeText) {
        // TODO: do this elegant
        return LocalDateTime.parse(dateTimeText, dateTimeFormatter).minusHours(3);
    }
}
FROM openjdk:8

# install prereq
RUN \
  apt-get update && \
  apt-get install -y --no-install-recommends libfreetype6 libfreetype6-dev && \
  apt-get install -y --no-install-recommends libfontconfig1 libfontconfig1-dev

# install google chrome
RUN \
  wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
  echo "deb http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list && \
  apt-get update && \
  apt-get install -y google-chrome-stable xvfb && \
  rm -rf /var/lib/apt/lists/*

# add built application
ADD target/universal/breezy-web-server-1.0-SNAPSHOT.zip /app.zip
RUN \
    unzip -o app.zip && \
    rm app.zip

ENV abc=hello

# run app
ADD run-app.sh /run-app.sh
CMD sh /run-app.sh
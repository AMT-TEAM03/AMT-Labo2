FROM maven:3-openjdk-18

LABEL Jeremie Melly "melly.jeremie@gmail.com"

COPY . /app 

WORKDIR /app

RUN mvn install

CMD mvn exec:java
FROM maven:3-openjdk-18

LABEL Jeremie Melly "melly.jeremie@gmail.com"

COPY . /app 

ARG aws_access_key_id
ARG aws_secret_access_key

ENV AWS_ACCESS_KEY_ID $aws_access_key_id
ENV AWS_SECRET_ACCESS_KEY $aws_secret_access_key
ENV AWS_REGION eu-west-2

WORKDIR /app

EXPOSE 9090

RUN mvn install

CMD mvn exec:java
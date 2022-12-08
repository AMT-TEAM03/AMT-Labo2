FROM maven:3-openjdk-18

LABEL Thibault Seem "thibault.seem@heig-vd.ch"

COPY . /app

ARG aws_access_key_id
ARG aws_secret_access_key

ENV AWS_ACCESS_KEY_ID $aws_access_key_id
ENV AWS_SECRET_ACCESS_KEY $aws_secret_access_key
ENV AWS_REGION eu-west-2

WORKDIR /app

RUN mvn install

CMD mvn exec:java
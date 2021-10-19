FROM adoptopenjdk/openjdk11:alpine-jre


EXPOSE 8080

COPY ./target/kira_telegram-1.0-SNAPSHOT.jar /usr/src/kira_bot/kira_bot.jar

RUN chmod +x -R /usr/src/kira_bot


ARG TELEBOT_BOT_TOKEN=""
ARG PASSWORD=""
ARG MARKS_CHAT_ID=""
ARG USERNAME=""

ENV TELEBOT_BOT_TOKEN "$TELEBOT_BOT_TOKEN"
ENV PASSWORD "$PASSWORD"
ENV MARKS_CHAT_ID "$MARKS_CHAT_ID"
ENV USERNAME "$USERNAME"


WORKDIR /usr/src/kira_bot

CMD ["java","-jar","kira_bot.jar"]

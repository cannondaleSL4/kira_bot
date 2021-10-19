FROM openjdk:11

EXPOSE 8080

COPY ./target/kira_telegram-1.0-SNAPSHOT.jar /usr/src/kira_bot.jar

WORKDIR /usr/src/kira_bot

RUN javac StartApplication.java

CMD ["java", "StartApplication"]
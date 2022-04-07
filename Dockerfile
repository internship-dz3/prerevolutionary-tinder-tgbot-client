FROM openjdk:11
EXPOSE 8081
ADD target/prerevolutionary-tinder-tgbot-client-docker.jar prerevolutionary-tinder-tgbot-client-docker.jar
ENTRYPOINT ["java", "-jar", "/prerevolutionary-tinder-tgbot-client-docker.jar"]
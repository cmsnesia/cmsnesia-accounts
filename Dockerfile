FROM maven:3.6.1-jdk-8-alpine AS builder

WORKDIR /workspace
COPY . .
RUN mvn -e -B clean package -DskipTests

#FROM openjdk:14-jdk-alpine
FROM openjdk:8-jre-alpine

LABEL APP="cmsnesia-accounts-web"
LABEL DOMAIN="cmsnesia-accounts"

RUN apk add --no-cache tzdata
RUN echo "Asia/Jakarta" > /etc/timezone

RUN addgroup -S cmsnesia-accounts && adduser -S cmsnesia-accounts-web -G cmsnesia-accounts
USER cmsnesia-accounts-web:cmsnesia-accounts

WORKDIR /app

COPY --from=builder /workspace/cmsnesia-accounts-web/target/cmsnesia-accounts-web-*.jar /app/cmsnesia-accounts-web.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/cmsnesia-accounts-web.jar"]
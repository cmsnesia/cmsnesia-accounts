FROM adoptopenjdk/openjdk11 AS builder

WORKDIR /workspace
COPY . .
RUN ./mvnw -e -B clean package -DskipTests

FROM adoptopenjdk/openjdk11:alpine-jre

LABEL APP="cmsnesia-accounts-web"
LABEL DOMAIN="cmsnesia-accounts"

RUN apk add --no-cache tzdata
RUN echo "Asia/Jakarta" > /etc/timezone

RUN addgroup -S cmsnesia-accounts && adduser -S cmsnesia-accounts-web -G cmsnesia-accounts
USER cmsnesia-accounts-web:cmsnesia-accounts

WORKDIR /app

COPY --from=builder /workspace/cmsnesia-accounts-web/target/cmsnesia-accounts-web-*.jar /app/cmsnesia-accounts-web.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/cmsnesia-accounts-web.jar"]
FROM azul/zulu-openjdk-alpine:21 AS builder

WORKDIR /oracle-bot
COPY . .
# add natives made for musl (default natives are for glibc)
#COPY natives ./src/main/resources/natives/
RUN ./gradlew --no-daemon build

FROM azul/zulu-openjdk-alpine:21-jre


WORKDIR /oracle-bot
COPY --from=builder /oracle-bot/build/libs/io-*shadow.jar ./io.jar
CMD ["java", "-jar", "io.jar"]

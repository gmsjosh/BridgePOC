# our base image
FROM openjdk:11.0.8-jre-slim-buster

# set deployment directory
WORKDIR /stream-transformer

# copy over the built artifact from the maven image
COPY target/stream-transformer-1.0-jar-with-dependencies.jar stream-transformer.jar

# set the startup command to run your binary
CMD java -jar stream-transformer.jar ${LEFT_TOPIC} ${RIGHT_TOPIC} ${OUTPUT_TOPIC} ${COMMON_KEY} ${BROKER} ${SCHEMA_REGISTRY} ${APPLICATION_ID} ${AUTO_OFFSET_RESET_CONFIG}
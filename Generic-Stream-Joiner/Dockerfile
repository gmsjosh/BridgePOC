FROM openjdk:11.0.8-jre-slim
WORKDIR /
ADD stream-transformer.jar .
CMD java -jar stream-transformer.jar ${LEFT_TOPIC} ${RIGHT_TOPIC} ${OUTPUT_TOPIC} ${COMMON_KEY} ${BROKER} ${SCHEMA_REGISTRY} ${GROUP_ID} ${APPLICATION_ID} ${AUTO_OFFSET_RESET_CONFIG}







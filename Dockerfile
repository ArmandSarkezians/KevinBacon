FROM maven:3.6.3-openjdk-16

WORKDIR /root/.m2/repository
COPY . ./
RUN mvn verify clean --fail-never
RUN mvn compile; sleep 15
ENTRYPOINT [ "mvn","exec:java" ]
EXPOSE 8000

## Assumptions
1. headers in the data feed are always present
2. Kafka is expected to run in localhost:9092
3. Expected uri for Mongodb: mongodb://localhost:27017
4. Java 11 is installed
5. Feedme docker image is up and running

## Choice of technology
1. Angular is used for UI and Java Spring boot is used for services because of prior familiarity.
2. MongoDb is used for NoSQL
3. Kafka is used for messaging because of prior familiarity.

## Instruction to run. Please run in the following order
1. Run Zookeeper
2. Run Kafka
3. Clone feedme-tech application
4. Run feedme-tech application
    1. Inside  feedme-tech folder run:
       mvn clean install
    2. This application can be run either from IDE or run JAR in the feedme-tech\target folder using the following command:
       java -jar feedme-service.jar
5. Clone feedme-tech-consumer application
6. Run feedme-tech-consumer application
    1. Inside the feedme-tech folder run:
       mvn clean install
    2. This application can be run either from IDE or run JAR in the feedme-tech-consumer\target folder using the following command:
       java -jar feedme-consumer.jar
7. Clone sbg-tech-ui
8. Run sbg-tech-ui (UI):
    1. Install Angular CLI: npm install -g @angular/cli
    2. In the sbg-tech-ui folder run the following command:
        1. npm install
        2. ng serve
    3. Now the application should be available on localhost:4200

Note:
1. Please refresh to view the UI loaded with data.
2. Dockerfile have been added to dockerize these apps.
3. Endpoints :
    1. http://localhost:8085/data/extractData (to extract data from tcp and push into kafka)
    2. http://localhost:8085/data/getDataFromDb (to view data pushed into Mongo database)

These end points are generally not needed to be accessed manually, it should be automatically triggered through UI.
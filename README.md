## MicroProfile Service Mesh Sample Service A
This is the serviceA component of the microprofile service mesh sample.

### Requirements
* [Docker](https://www.docker.com/)
* [Maven](https://maven.apache.org/install.html)
* [Java 8]: Any compliant JDK should work.
  * [Java 8 JDK from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * [Java 8 JDK from IBM (AIX, Linux, z/OS, IBM i)](http://www.ibm.com/developerworks/java/jdk/),
    or [Download a Liberty server package](https://developer.ibm.com/assets/wasdev/#filter/assetTypeFilters=PRODUCT)
    that contains the IBM JDK (Windows, Linux)

### Build

    mvn install

### Package and run the service

The pom currently contains a *liberty* profile which you can use to test and run the service.

### Run the service locally on Open Liberty

    mvn -P liberty install liberty:run-server

The service will be accessible at http://localhost:8080/mp-servicemesh-sample/serviceA

### Run the service locally in an Open Liberty Docker container

    docker build -t servicea -f src/main/profiles/liberty/Dockerfile .
    docker run -p 8080:8080 servicea

The service will be accessible at http://localhost:8080/mp-servicemesh-sample/serviceA

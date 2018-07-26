# MicroProfile Service Mesh Sample Service A
This is the serviceA component of the microprofile service mesh sample.

## Requirements
* [Docker](https://www.docker.com/)
* [Maven](https://maven.apache.org/install.html)
* [Java 8]: Any compliant JDK should work.

## Build

    mvn install

## Package and run the service

The pom is designed to contain application server profiles with which you can test and run the service. Currently the *liberty* profile is provided.

You may want to deploy multiple versions of serviceA into the mesh so it's important to consider the uniqueness of image names when building the docker containers. A simple strategy would be to include the profile in the image name. For example, a suitable image name for a Liberty container could be &lt;docker id&gt;/serviceb-liberty. Using an image name of this form will allow you to use the script supplied in the [microprofile-service-mesh](https://github.com/eclipse/microprofile-service-mesh) repository to deploy your services into a cluster.

## Open Liberty Profile (liberty)

### Run the service locally

    mvn -P liberty install liberty:run-server

The service will be accessible at http://localhost:8080/mp-servicemesh-sample/serviceA

### Package the service in a Docker image

    mvn -P liberty install
    docker build -t <docker id>/servicea-liberty -f src/main/profiles/liberty/Dockerfile .

### Run the Docker image locally

    docker run -p 8080:9080 <docker id>/servicea-liberty

The service will be accessible at http://localhost:9080/mp-servicemesh-sample/serviceA

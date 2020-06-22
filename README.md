[![Build Status](https://travis-ci.org/AntonioDell/GameBackendServices.svg?branch=dev)](https://travis-ci.org/AntonioDell/GameBackendServices)
# GameBackendServices
My shot at creating a collection of microservices used to manage a games business logic (users, friends, saved games, ...). 
Let's see how deep the rabbit hole goes.

## What you may learn from this project
- Build interdependent Spring WebFlux microservices
- WebFlux with MongoDB
- Webflux with JsonPatch
- Validating MongoDB transactions using spring-boot-starter-validation (with WebFlux) 
- Service discovery with Spring-Eureka
- Fault tolerance in a WebFlux microservice using spring-cloud-starter-circuitbreaker-reactor-resilience4j
- Testing fault tolerance
- Mocking dependent services using Wiremock
- Everything Kotlin

## How to run it
Start a local mongodb in a docker container (fe. with [this docker image](https://hub.docker.com/_/mongo/)).
Configure the application.properties/.yml configuration if your DB connection is different.
Start the individual microservices by running their Application classes (annotated with @SpringBootApplication).

## What works
- friend-info-service 
- user-info-service
- user-page-service
- discovery-server

## What doesn't work
- validation -> No specs yet

## Contributing
If you spot any weirdness going on in the code, please feel free to point it out. 
I'm grateful for every little thing I can learn.

Ideas for which features would fit in well? More than happy for any suggestions!

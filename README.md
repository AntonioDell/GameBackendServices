# GameBackendServices
My shot at creating a collection of microservices used to manage a games business logic (users, friends, saved games, ...). Let's see how deep the rabbit hole goes

# How to run it
Start a local mongodb in a docker container (fe with this docker image [](https://hub.docker.com/_/mongo/)).
Configure the application.properties/.yml configuration if your DB connection is different.
Start the individual microservices by running their Application classes (annotated with @SpringBootApplication).

# What works
- friend-info-service 

Learning Spring Boot
=============

In order to compile and run, type the following commands on your terminal:

```bash
./gradlew clean bootRun
```

In order to debug:
```bash
./gradlew clean build
java -jar build/libs/learning-spring-boot-0.0.1-SNAPSHOT.jar --debug
```

In order to post an image using curl:
```bash
curl -v -X POST -F file=@/Users/juanprado/Downloads/image.jpg localhost:8080/images
```

In order to do ssh into the app
```bash
ssh -p 2000 user@localhost
```

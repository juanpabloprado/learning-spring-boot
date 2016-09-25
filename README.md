Learning Spring Boot
=============

In order to compile and run, type the following commands on your terminal:

```bash
./gradlew bootRun
```

In order to post an image using curl
```bash
curl -v -X POST -F file=@/Users/juanprado/Downloads/image.jpg localhost:8080/images
```
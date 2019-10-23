# spring-boot-compensation

Framework to handle compensation in SAGA choreographed solutions.

## Getting started

### Prerequites
* Spring Boot
* Quartz

### Installing

1. Clone
git clone https://github.com/luiztoscano/spring-boot-compensation

2. Build
```
mvn clean install
```
3. Add dependency to your project
```xml
<dependency>
	<groupId>org.saga</groupId>
	<artifactId>spring-boot-starter-compensation</artifactId>
</dependency>
```

### Usage

Parameters:
* index - Parameter index to be used as scheduling key
* el - Spring EL expression to be evaluated on key parameter
* fallbackBean - Fallback bean name
* fallbackClass - Fallback bean class
* traceEnabled - Open tracing enabled

Example:

```java
@Compensate(index = 1, ttl = 2, unit = ChronoUnit.SECONDS, fallbackName = "transferFallback", fallbackClass = TransferFallback.class, traceEnabled = true)
```

## Built with

* Maven - https://maven.apache.org
* Spring Boot - https://spring.io/projects/spring-boot
* Quartz - http://www.quartz-scheduler.org
* Sleuth - https://spring.io/projects/spring-cloud-sleuth
* Zipkin - https://zipkin.io/
* Micrometer - https://micrometer.io/

## Versioning

## Authors

* Luiz Toscano - Initial work

See also the list of contributors who participated in this project.

## License

This project is licensed under the MIT License - see the LICENSE.md file for details

# spring-boot-compensation

Framework to handle compensation in SAGA choreographed solutions.

## Getting started

### Prerequites
* Spring Boot
* Quartz

### Installing

```
mvn clean install
```

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
* Quartz - http://www.quartz-scheduler.org/

## Versioning

## Authors

* Luiz Toscano - Initial work

See also the list of contributors who participated in this project.

## License

This project is licensed under the MIT License - see the LICENSE.md file for details

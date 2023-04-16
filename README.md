To run this, you must provide an "application.properties" file in for Spring configuration.

The `application.properties` file must list the following properties:

```java
twilio.accountSid = "<Twilio Account Sid>"
twilio.authToken = "<Twilio Auth Token>"
twilio.fromPhoneNumber = "<Twilio Phone Number>"
spring.rabbitmq.queueName = "device_event_stream"
server.port = <Spring Server Port>

spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa

spring.profiles.active=no_rabbit // this property is used for testing non-RabbitMQ-related classes
```

FROM java:8
 
MAINTAINER Safiot
 
ADD config-service.jar /app.jar
 
EXPOSE 8888
 
CMD java -Djava.security.egd=file:/dev/./urandom -jar /app.jar
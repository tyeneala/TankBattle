## Instructions for installing the game on machines on Campus

### Get IP addres for Mac
ifconfig | grep 'inet '

### Install
TanksServer % mvn clean install
TanksClient % mvn clean package

### Compilation with dependencies into an executable jar archive
mvn clean compile assembly:single

### Launch
open target/TanksClient-1.0.jar

java -jar target/TanksClient-1.0.jar

  
### Find out the installed versions of java
/usr/libexec/java_home -V

### Assigning the default Java version
export JAVA_HOME=`/usr/libexec/java_home -v 1.8`

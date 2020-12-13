# Multi-threaded file-based Web Server

## Prerequisites
1. Java 11
2. Maven

## Steps to run the server

1. Run mvn install to build the application
2. Run java -jar target/web-sever-jar-with-dependencies.jar to start the server with default options
3. Run java -jar target/web-sever-jar-with-dependencies.jar --help to see available options to run the server

## References

1. https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/ServerSocket.html
2. https://www.net.t-labs.tu-berlin.de/teaching/computer_networking/ap01.htm (Got some general ideas of the project from this link. Implemented from scratch based on those ideas.)
3. https://www.baeldung.com/
4. https://stackoverflow.com/

## Dependencies

1. Argparse4j - https://argparse4j.github.io/ - for parsing the commandline args
2. Apache tike - https://tika.apache.org/ - for content type detection
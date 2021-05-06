# ClientServer

This projects is a Client/Server implemented in Java. Server creates a new communication thread and allocates a new port for each Client.

## Setup

The project was made with Maven (unused) and developed with IntelliJ Ultimate.


## Usage

```
cd src/main/java

javac ./*.java # compiles everything in the main folder
java Server p # loads the server on port p
java Client localhost p # connects the client to the server on port p
```

If Client inputs "end", he is disconnected from the server.
Also, the server redirects the client at his first connexion to a new allocated port.
The is a maximum number of clients for the server defined in the Server.java file.

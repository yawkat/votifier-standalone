votifier-standalone
===================

Custom votifier server API based on netty that does not require a bukkit server to run.

Usage
-----

```java
VotifierServerBuilder builder = new VotifierServerBuilder();
builder.port(8201) // votifier listen port, defaults to 8192
       .key(VotifierKeyPair.read(new File("key.json"))) // key file, can be generated using the generator
       .voteListener(vote -> {
           System.out.println(vote.getUsername() + " voted!");
       });
builder.start();
```

Generator
---------

```
java -jar generator.jar generate key.json
```

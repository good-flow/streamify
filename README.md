## Make everything Reactive Streams ready!

[![Build Status](https://travis-ci.org/good-flow/streamify.svg?branch=master)](https://travis-ci.org/good-flow/streamify)

streamify is a Java library to convert various kind of data source to [Reactive Streams](http://www.reactive-streams.org/) Publisher.

## Examples

### From File

```java
File file = new File("dataset.csv");
FileLinesAsIsResource input = new FileLinesAsIsResource(file, StandardCharsets.UTF_8);
Executor publisherExecutor = Executors.newFixedThreadPool(3);
AsyncPublisher<String> publisher = AsyncPublisher.file(input).executor(publisherExecutor).build();

Executor subscriberExecutor = Executors.newFixedThreadPool(3);
AsyncSubscriber<String> subscriber = new AsyncSubscriber<String>(subscriberExecutor) {
  @Override
  protected boolean whenNext(String line) {
    log.info("line: {}", line);
    return true;
  }
};
publisher.subscribe(subscriber);
```

### From JDBC

```java
PreparedStatement statement = conn.prepareStatement("select id, name, description from people");
JdbcResource<Person> resource = new JdbcResource<>(statement, (rs) -> {
  try {
    return new Person(
      rs.getInt("id"),
      rs.getString("name"),
      rs.getString("description")
    );
  } catch (SQLException e) {
    throw new RuntimeException(e);
  }
});
Executor publisherExecutor = Executors.newFixedThreadPool(3);
AsyncPublisher<Person> publisher = AsyncPublisher.jdbc(resource).executor(publisherExecutor).build();

Executor subscriberExecutor = Executors.newFixedThreadPool(3);
AsyncSubscriber<Person> subscriber = new AsyncSubscriber<Person>(subscriberExecutor) {
  @Override
  protected boolean whenNext(Person person) {
    log.info("person: {}", person);
    return true;
  }
};
publisher.subscribe(subscriber);
```

## License

CC0 1.0 Universal (CC0 1.0) Public Domain Dedication

https://creativecommons.org/publicdomain/zero/1.0/


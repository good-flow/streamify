package somewhere;

import com.goodflow.streamify.publisher.AsyncPublisher;
import com.goodflow.streamify.publisher.resource.JdbcResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.reactivestreams.example.unicast.AsyncSubscriber;
import org.reactivestreams.example.unicast.SyncSubscriber;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@RunWith(JUnit4.class)
public class JdbcResourceTest {

    Connection acquireConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:mem:hello", "user", "pass");
    }

    void prepareTable(Connection conn) throws SQLException, ClassNotFoundException {
        conn.prepareStatement("create table people (id int not null, name varchar(100), description varchar(100));").execute();
        conn.prepareStatement("insert into people values (1, 'Alice', null);").execute();
        conn.prepareStatement("insert into people values (2, 'Bob', 'Bobby');").execute();
        conn.prepareStatement("insert into people values (3, 'Chris', null);").execute();
    }

    @Test
    public void sync() throws Exception {
        try (Connection conn = acquireConnection()) {
            prepareTable(conn);

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

            SyncSubscriber<Person> subscriber = new SyncSubscriber<Person>() {
                @Override
                protected boolean foreach(Person person) {
                    log.info("person (sync): {}", person);
                    return true;
                }
            };
            publisher.subscribe(subscriber);
            Thread.sleep(1000);
        }

    }

    @Test
    public void async() throws Exception {
        try (Connection conn = acquireConnection()) {
            prepareTable(conn);

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
                    log.info("person (async): {}", person);
                    return true;
                }
            };

            publisher.subscribe(subscriber);
            Thread.sleep(1000);
        }

    }

}

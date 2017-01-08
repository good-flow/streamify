package somewhere;

import com.goodflow.streamify.concurrent.DaemonExecutorService;
import com.goodflow.streamify.concurrent.DaemonExecutors;
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
        DaemonExecutorService publisherExecutor = DaemonExecutors.newFixedThreadPool(3);
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
            AsyncPublisher<Person> publisher = AsyncPublisher.jdbc(resource).executor(publisherExecutor).build();

            SyncSubscriber<Person> subscriber = new SyncSubscriber<Person>() {
                @Override
                protected boolean foreach(Person person) {
                    log.info("person (sync): {}", person);
                    return true;
                }

                @Override
                public void onComplete() {
                    publisherExecutor.shutdownNow();
                }
            };
            publisher.subscribe(subscriber);
        }

    }

    @Test
    public void async() throws Exception {
        DaemonExecutorService publisherExecutor = DaemonExecutors.newFixedThreadPool(3);
        DaemonExecutorService subscriberExecutor = DaemonExecutors.newFixedThreadPool(3);
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

            AsyncPublisher<Person> publisher = AsyncPublisher.jdbc(resource).executor(publisherExecutor).build();

            AsyncSubscriber<Person> subscriber = new AsyncSubscriber<Person>(subscriberExecutor) {
                @Override
                protected boolean whenNext(Person person) {
                    log.info("person (async): {}", person);
                    return true;
                }

                @Override
                protected void whenComplete() {
                    publisherExecutor.shutdownNow();
                    subscriberExecutor.shutdownNow();
                }
            };

            publisher.subscribe(subscriber);
        }
    }

}

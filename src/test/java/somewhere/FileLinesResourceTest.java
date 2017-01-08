package somewhere;

import com.goodflow.streamify.concurrent.DaemonExecutorService;
import com.goodflow.streamify.concurrent.DaemonExecutors;
import com.goodflow.streamify.publisher.AsyncPublisher;
import com.goodflow.streamify.publisher.resource.FileLinesAsIsResource;
import com.goodflow.streamify.publisher.resource.FileLinesResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.reactivestreams.example.unicast.AsyncSubscriber;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RunWith(JUnit4.class)
public class FileLinesResourceTest {

    @Test
    public void fileAsIs() throws Exception {
        DaemonExecutorService publisherExecutor = DaemonExecutors.newFixedThreadPool(3);
        DaemonExecutorService subscriberExecutor = DaemonExecutors.newFixedThreadPool(3);

        File file = new File("src/test/resources/dataset.csv");
        FileLinesAsIsResource inputResource = new FileLinesAsIsResource(file, StandardCharsets.UTF_8);
        AsyncPublisher<String> publisher = AsyncPublisher.file(inputResource).executor(publisherExecutor).build();

        AsyncSubscriber<String> subscriber = new AsyncSubscriber<String>(subscriberExecutor) {
            private final List<String> lines = new ArrayList<>();

            @Override
            protected boolean whenNext(String line) {
                log.info("line: {}", line);
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

    @Test
    public void file() throws Exception {
        DaemonExecutorService publisherExecutor = DaemonExecutors.newFixedThreadPool(3);
        DaemonExecutorService subscriberExecutor = DaemonExecutors.newFixedThreadPool(3);

        File file = new File("src/test/resources/dataset.csv");
        FileLinesResource<Person> inputResource = new FileLinesResource<>(file, StandardCharsets.UTF_8, (line) -> Person.fromCsv(line));
        AsyncPublisher<Person> publisher = AsyncPublisher.file(inputResource).executor(publisherExecutor).build();

        AsyncSubscriber<Person> subscriber = new AsyncSubscriber<Person>(subscriberExecutor) {
            @Override
            protected boolean whenNext(Person person) {
                log.info("person: {}", person);
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

    @Test
    public void fileCancellation() throws Exception {
        DaemonExecutorService publisherExecutor = DaemonExecutors.newFixedThreadPool(3);
        DaemonExecutorService subscriberExecutor = DaemonExecutors.newFixedThreadPool(3);

        File file = new File("src/test/resources/dataset.csv");
        FileLinesResource<Person> inputResource = new FileLinesResource<>(file, StandardCharsets.UTF_8, (line) -> Person.fromCsv(line));
        AsyncPublisher<Person> publisher = AsyncPublisher.file(inputResource).executor(publisherExecutor).build();

        AtomicInteger count = new AtomicInteger(0);
        AsyncSubscriber<Person> subscriber = new AsyncSubscriber<Person>(subscriberExecutor) {
            @Override
            protected boolean whenNext(Person person) {
                log.info("person: {}", person);
                boolean needMore = count.incrementAndGet() < 10;
                if (needMore == false) {
                    publisherExecutor.shutdownNow();
                    subscriberExecutor.shutdownNow();
                }
                return needMore;
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

package somewhere;

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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RunWith(JUnit4.class)
public class FileLinesResourceTest {

    @Test
    public void fileAsIs() throws Exception {
        File file = new File("streamify/src/test/resources/dataset.csv");
        FileLinesAsIsResource inputResource = new FileLinesAsIsResource(file, StandardCharsets.UTF_8);
        Executor publisherExecutor = Executors.newFixedThreadPool(3);
        AsyncPublisher<String> publisher = AsyncPublisher.file(inputResource).executor(publisherExecutor).build();

        Executor subscriberExecutor = Executors.newFixedThreadPool(3);
        AsyncSubscriber<String> subscriber = new AsyncSubscriber<String>(subscriberExecutor) {
            @Override
            protected boolean whenNext(String person) {
                log.info("line: {}", person);
                return true;
            }
        };

        publisher.subscribe(subscriber);
        Thread.sleep(1000);
    }

    @Test
    public void file() throws Exception {
        File file = new File("streamify/src/test/resources/dataset.csv");
        FileLinesResource<Person> inputResource = new FileLinesResource<>(file, StandardCharsets.UTF_8, (line) -> Person.fromCsv(line));
        Executor publisherExecutor = Executors.newFixedThreadPool(3);
        AsyncPublisher<Person> publisher = AsyncPublisher.file(inputResource).executor(publisherExecutor).build();

        Executor subscriberExecutor = Executors.newFixedThreadPool(3);
        AsyncSubscriber<Person> subscriber = new AsyncSubscriber<Person>(subscriberExecutor) {
            @Override
            protected boolean whenNext(Person person) {
                log.info("person: {}", person);
                return true;
            }
        };

        publisher.subscribe(subscriber);
        Thread.sleep(1000);
    }

    @Test
    public void fileCancellation() throws Exception {
        File file = new File("streamify/src/test/resources/dataset.csv");
        FileLinesResource<Person> inputResource = new FileLinesResource<>(file, StandardCharsets.UTF_8, (line) -> Person.fromCsv(line));
        Executor publisherExecutor = Executors.newFixedThreadPool(3);
        AsyncPublisher<Person> publisher = AsyncPublisher.file(inputResource).executor(publisherExecutor).build();

        AtomicInteger count = new AtomicInteger(0);
        Executor subscriberExecutor = Executors.newFixedThreadPool(3);
        AsyncSubscriber<Person> subscriber = new AsyncSubscriber<Person>(subscriberExecutor) {
            @Override
            protected boolean whenNext(Person person) {
                log.info("person: {}", person);
                return count.incrementAndGet() < 10;
            }
        };

        publisher.subscribe(subscriber);
        Thread.sleep(1000);
    }

}

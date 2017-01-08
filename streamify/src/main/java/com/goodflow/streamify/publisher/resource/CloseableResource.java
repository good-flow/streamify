package com.goodflow.streamify.publisher.resource;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public interface CloseableResource<ELEMENT> extends Iterable<ELEMENT>, Closeable {

    void close() throws IOException;

    Iterator<ELEMENT> iterator();

}

package com.goodflow.streamify.publisher.resource;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Function;

@Slf4j
public class FileLinesResource<ELEMENT> implements CloseableResource<ELEMENT> {

    private final Path path;
    private final Function<String, ELEMENT> extractor;
    private BufferedReader reader;

    public FileLinesResource(File file, Charset charset, Function<String, ELEMENT> extractor) throws IOException {
        this.path = file.toPath();
        this.extractor = extractor;
        reader = Files.newBufferedReader(path, charset);
    }

    public FileLinesResource(Path path, Charset charset, Function<String, ELEMENT> extractor) throws IOException {
        this.path = path;
        this.extractor = extractor;
        reader = Files.newBufferedReader(path, charset);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public Iterator<ELEMENT> iterator() {
        return new Iterator<ELEMENT>() {
            private String nextLine;

            @Override
            public boolean hasNext() {
                if (nextLine != null) {
                    return true;
                } else {
                    try {
                        nextLine = reader.readLine();
                        return nextLine != null;
                    } catch (IOException e) {
                        return false;
                    }
                }
            }

            @Override
            public ELEMENT next() {
                try {
                    ELEMENT element = extractor.apply(nextLine);
                    nextLine = null;
                    return element;
                } catch (Exception e) {
                    log.warn("Failed to extract value from {}", nextLine, e);
                    throw e;
                }
            }
        };
    }

}
package com.goodflow.streamify.publisher.resource;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Function;

@Slf4j
public class FileLinesAsIsResource extends FileLinesResource<String> {

    private static final Function<String, String> AS_IS_EXTRACTOR = (line) -> line;

    public FileLinesAsIsResource(File file, Charset charset) throws IOException {
        super(file, charset, AS_IS_EXTRACTOR);
    }

    public FileLinesAsIsResource(Path path, Charset charset) throws IOException {
        super(path, charset, AS_IS_EXTRACTOR);
    }

}
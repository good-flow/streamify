package com.goodflow.streamify.publisher.resource;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Function;

@Slf4j
public class JdbcResource<ELEMENT> implements CloseableResource<ELEMENT> {

    private final PreparedStatement statement;
    private final Function<ResultSet, ELEMENT> extractor;

    public JdbcResource(PreparedStatement statement, Function<ResultSet, ELEMENT> extractor) {
        this.statement = statement;
        this.extractor = extractor;
    }

    @Override
    public void close() throws IOException {
        try {
            statement.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Iterator<ELEMENT> iterator() {
        try {
            final ResultSet resultSet = statement.executeQuery();
            return new Iterator<ELEMENT>() {
                private boolean consumedNext = true;

                @Override
                public boolean hasNext() {
                    try {
                        return consumedNext == false || resultSet.next();
                    } catch (SQLException e) {
                        log.warn("Failed to call ResultSet#next()", e);
                        return false;
                    }
                }

                @Override
                public ELEMENT next() {
                    try {
                        ELEMENT element = extractor.apply(resultSet);
                        consumedNext = true;
                        return element;
                    } catch (Exception e) {
                        log.warn("Failed to extract value from JDBC prepared statement", e);
                        throw e;
                    }
                }
            };

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
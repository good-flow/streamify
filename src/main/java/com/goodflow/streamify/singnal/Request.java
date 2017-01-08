package com.goodflow.streamify.singnal;

public final class Request implements Signal {

    private final long n;

    public Request(final long n) {
        this.n = n;
    }

    public long getN() {
        return n;
    }
}


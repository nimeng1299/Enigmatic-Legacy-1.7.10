package com.aizistral.enigmaticlegacy.compat;

public class Tuple<A, B> {

    private final A first;
    private final B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return this.first;
    }

    public B getSecond() {
        return this.second;
    }

    public A getA() {
        return this.first;
    }

    public B getB() {
        return this.second;
    }

}

package com.aizistral.enigmaticlegacy.compat.text;

public class TranslatableContents {

    private final String key;
    private final Object[] args;

    public TranslatableContents(String key, Object... args) {
        this.key = key;
        this.args = args;
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }

    @Override
    public String toString() {
        return this.key;
    }

}

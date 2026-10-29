package com.aizistral.enigmaticlegacy.compat.text;

public class LiteralContents {

    private final String text;

    public LiteralContents(String text) {
        this.text = text;
    }

    public String text() {
        return this.text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return this.text;
    }

}

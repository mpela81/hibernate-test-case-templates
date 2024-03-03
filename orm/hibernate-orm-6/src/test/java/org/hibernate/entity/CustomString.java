package org.hibernate.entity;

public class CustomString {
    private final String s;

    public CustomString(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}

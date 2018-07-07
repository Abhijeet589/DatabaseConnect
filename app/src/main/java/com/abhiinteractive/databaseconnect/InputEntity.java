package com.abhiinteractive.databaseconnect;

public class InputEntity {

    String input;
    int sync;

    public InputEntity(String input, int sync) {
        this.input = input;
        this.sync = sync;
    }

    public String getInput() {
        return input;
    }

    public int getSync() {
        return sync;
    }
}

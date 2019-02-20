package com.methk.robocar;

public class Command {

    private int command;
    private long duration;

    public Command(int command, long duration) {
        this.command = command;
        this.duration = duration;
    }

    public int getCommand() { return command; }

    public long getDuration() { return duration; }

    @Override
    public String toString() {
        return command + ":" + duration;
    }
}

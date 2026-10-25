package com.bms.triangle.cli;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.exit(TriangleClassifierCommand.run(args, System.out, System.err).code());
    }
}

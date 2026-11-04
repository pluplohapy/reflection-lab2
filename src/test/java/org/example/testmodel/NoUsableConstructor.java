package org.example.testmodel;

public class NoUsableConstructor {

    private final Thread worker;

    public NoUsableConstructor(Thread worker) {
        this.worker = worker;
    }

    public Thread getWorker() {
        return worker;
    }
}

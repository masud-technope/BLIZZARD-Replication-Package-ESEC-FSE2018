package org.eclipse.ecf.tests.sync;

import org.eclipse.ecf.sync.IModelChangeMessage;

public class SimpleMessageQueue {

    private IModelChangeMessage[] contents;

    private boolean available = false;

    public synchronized IModelChangeMessage[] get() {
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        available = false;
        notify();
        return contents;
    }

    public synchronized void put(IModelChangeMessage[] message) {
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        contents = message;
        available = true;
        notify();
    }
}

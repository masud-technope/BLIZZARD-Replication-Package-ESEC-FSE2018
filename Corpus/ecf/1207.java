package org.eclipse.ecf.tests.sync;

import java.util.LinkedList;
import java.util.List;

public class SimpleQueue {

    List list;

    boolean stopped;

    public  SimpleQueue() {
        list = new LinkedList();
        stopped = false;
    }

    public synchronized boolean enqueue(Object obj) {
        if (isStopped() || obj == null) {
            return false;
        }
        // Add item to the list
        list.add(obj);
        // Notify waiting thread. Dequeue should only be read by one thread, so
        // only need
        // notify() rather than notifyAll().
        notify();
        return true;
    }

    public synchronized Object dequeue() {
        Object val = peekQueue();
        if (val != null) {
            removeHead();
        }
        return val;
    }

    public synchronized Object peekQueue() {
        while (isEmpty()) {
            if (stopped)
                return null;
            try {
                wait();
            } catch (Exception e) {
                return null;
            }
        }
        return list.get(0);
    }

    public synchronized Object peekQueue(long waitMS) {
        if (waitMS == 0)
            return peekQueue();
        if (stopped) {
            return null;
        }
        try {
            wait(waitMS);
        } catch (Exception e) {
            return null;
        }
        if (isEmpty())
            return null;
        return list.get(0);
    }

    public synchronized Object removeHead() {
        if (list.isEmpty())
            return null;
        return list.remove(0);
    }

    public synchronized boolean isEmpty() {
        return list.isEmpty();
    }

    public synchronized void stop() {
        stopped = true;
    }

    public synchronized boolean isStopped() {
        return stopped;
    }

    public synchronized int size() {
        return list.size();
    }

    public synchronized Object[] flush() {
        Object[] out = list.toArray();
        list.clear();
        close();
        return out;
    }

    public synchronized void close() {
        stop();
        notifyAll();
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("SimpleFIFOQueue[");
        //$NON-NLS-1$
        sb.append(list).append("]");
        return sb.toString();
    }
}

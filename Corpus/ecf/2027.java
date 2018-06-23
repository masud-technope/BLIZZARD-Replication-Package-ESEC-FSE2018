package org.eclipse.ecf.tests.sync;

import java.util.Random;
import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.IModelSynchronizationStrategy;
import org.eclipse.ecf.sync.ModelUpdateException;
import org.eclipse.ecf.sync.SerializationException;
import org.eclipse.ecf.sync.doc.DocumentChangeMessage;
import org.eclipse.ecf.sync.doc.IDocumentChange;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

public class SharedDocClient extends Thread {

    private String name;

    private IDocument document;

    private SimpleQueue localQueue;

    private SimpleQueue otherQueue;

    private IModelSynchronizationStrategy syncStrategy;

    private Random random = new Random();

    public  SharedDocClient(String name, IModelSynchronizationStrategy syncStrategy, String startText) {
        this.name = name;
        this.syncStrategy = syncStrategy;
        this.document = new Document(startText);
        this.localQueue = new SimpleQueue();
    }

    public void start() {
        Thread localThread = new Thread(new Runnable() {

            public void run() {
                while (!localQueue.isStopped()) {
                    sleep(500, 2000);
                    localQueue.enqueue(new Runnable() {

                        public void run() {
                            processLocalChange(getLocalDocumentChange());
                        }
                    });
                }
            }
        });
        localThread.setDaemon(true);
        localThread.start();
        super.start();
    }

    public SimpleQueue getQueue() {
        return this.localQueue;
    }

    public void setOtherQueue(SimpleQueue otherQueue) {
        this.otherQueue = otherQueue;
    }

    public String getDocumentText() {
        return document.get();
    }

    private void sleep(int minimum, int max) {
        try {
            Thread.sleep(minimum + random.nextInt(max - minimum));
        } catch (InterruptedException e) {
        }
    }

    public void run() {
        while (true) {
            Object o = localQueue.dequeue();
            if (o == null)
                return;
            if (o instanceof Runnable)
                ((Runnable) o).run();
            if (o instanceof byte[]) {
                processRemoteMessage((byte[]) o);
            }
        }
    }

    private void processLocalChange(IDocumentChange localChange) {
        if (localChange != null) {
            synchronized (this.getClass()) {
                applyChangeToLocalDocument(true, localChange);
                // Then register with local synchronizer
                IModelChangeMessage[] changeMessages = syncStrategy.registerLocalChange(localChange);
                // Then 'send' to other
                deliverChangeToOther(changeMessages);
            }
        }
    }

    private void applyChangeToLocalDocument(boolean local, IDocumentChange change) {
        System.out.println(name + ";doc=" + document.get());
        System.out.println(name + (local ? ";localChange" : ";remoteChange") + ";" + change);
        try {
            change.applyToModel(document);
        } catch (ModelUpdateException e) {
            e.printStackTrace();
        }
        System.out.println(name + ";doc=" + document.get());
    }

    private void processRemoteMessage(byte[] msg) {
        if (msg != null) {
            try {
                synchronized (this.getClass()) {
                    IModelChange change = syncStrategy.deserializeRemoteChange(msg);
                    System.out.println(name + ";received=" + change);
                    IDocumentChange[] documentChanges = (IDocumentChange[]) syncStrategy.transformRemoteChange(change);
                    for (int i = 0; i < documentChanges.length; i++) {
                        applyChangeToLocalDocument(false, documentChanges[i]);
                    }
                }
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
    }

    private void deliverChangeToOther(IModelChangeMessage[] changeMessages) {
        for (int i = 0; i < changeMessages.length; i++) {
            try {
                System.out.println(name + ";sending=" + changeMessages[i]);
                otherQueue.enqueue(changeMessages[i].serialize());
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
    }

    private IDocumentChange getLocalDocumentChange() {
        final int offset = random.nextInt(document.getLength());
        final int length = random.nextInt(2);
        final String text = (random.nextInt(2) == 0) ? "" : ">";
        return new DocumentChangeMessage(offset, length, text);
    }

    public void close() {
        localQueue.close();
    }
}

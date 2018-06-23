package org.eclipse.ecf.tests.sync;

import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.IModelSynchronizationStrategy;
import org.eclipse.ecf.sync.doc.IDocumentChange;
import org.eclipse.jface.text.Document;

public class Receiver extends Thread {

    private IModelSynchronizationStrategy receiver;

    private SimpleMessageQueue queue;

    private SimpleMessageQueue initiatorQueue;

    private Document fDocument;

    public  Receiver(IModelSynchronizationStrategy receiver, Document document) {
        this.setReceiver(receiver);
        this.queue = new SimpleMessageQueue();
        fDocument = document;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            IModelChangeMessage[] changes = queue.get();
            for (int j = 0; j < changes.length; j++) {
                IDocumentChange docs[] = (IDocumentChange[]) receiver.transformRemoteChange((IModelChange) changes[j]);
                for (int k = 0; k < docs.length; k++) {
                    String text = fDocument.get();
                    String newText = ">";
                    text = text.concat(newText);
                    fDocument.set(text);
                }
            }
        }
    }

    protected void setReceiver(IModelSynchronizationStrategy receiver) {
        this.receiver = receiver;
    }

    public IModelSynchronizationStrategy getReceiver() {
        return receiver;
    }

    public SimpleMessageQueue getQueue() {
        return queue;
    }

    public void setInitiatorQueue(SimpleMessageQueue initiatorQueue) {
        this.initiatorQueue = initiatorQueue;
    }

    public SimpleMessageQueue getInitiatorQueue() {
        return initiatorQueue;
    }

    public Document getDocument() {
        return fDocument;
    }

    public void setDocument(Document document) {
        fDocument = document;
    }
}

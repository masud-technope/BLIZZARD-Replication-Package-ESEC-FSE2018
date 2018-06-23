/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Mustafa K. Isik
 *****************************************************************************/
package org.eclipse.ecf.sync.doc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.internal.sync.Activator;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.ModelUpdateException;
import org.eclipse.ecf.sync.SerializationException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * Document change message of communicating document change 
 * events to remote models.
 * 
 * @since 2.1
 */
public class DocumentChangeMessage implements IDocumentChange, IModelChangeMessage, Serializable {

    private static final long serialVersionUID = -3195542805471664496L;

    public static DocumentChangeMessage deserialize(byte[] bytes) throws SerializationException {
        try {
            final ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
            final ObjectInputStream oins = new ObjectInputStream(bins);
            return (DocumentChangeMessage) oins.readObject();
        } catch (final Exception e) {
            throw new SerializationException("Exception deserializing DocumentChangeMessage", e);
        }
    }

    private String text;

    private int offset;

    private int length;

    /**
	 * Create document change message for given offset, length of replacement, and text to replace.
	 * 
	 * @param offset the offset (number of characters) in the document where change is to occur.
	 * @param length the length (number of characters) that are to be replace in existing document.
	 * @param text the text to actually replace.
	 */
    public  DocumentChangeMessage(int offset, int length, String text) {
        this.offset = offset;
        this.length = length;
        this.text = text;
    }

    /**
	 * Returns the modification index of the operation resembled by this
	 * message.
	 * 
	 * @return modification index
	 */
    public int getOffset() {
        return offset;
    }

    public void setOffset(int updatedOffset) {
        this.offset = updatedOffset;
    }

    /**
	 * Returns the length of replaced text.
	 * 
	 * @return length of replaced text
	 */
    public int getLengthOfReplacedText() {
        return length;
    }

    public void setLengthOfReplacedText(int length) {
        this.length = length;
    }

    /**
	 * @return text
	 */
    public String getText() {
        return text;
    }

    /**
	 * 
	 * @return the length of the inserted text
	 */
    public int getLengthOfInsertedText() {
        return this.text.length();
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("DocumentChangeMessage[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("offset=").append(offset);
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";length=").append(length).append(";text=").append(text).append("]");
        return buf.toString();
    }

    private byte[] serializeLocal() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        return bos.toByteArray();
    }

    /**
	 * Serialize the current message
	 */
    public byte[] serialize() throws SerializationException {
        try {
            return serializeLocal();
        } catch (final IOException e) {
            throw new SerializationException("Exception serializing DocumentChangeMessage", e);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        IAdapterManager manager = Activator.getDefault().getAdapterManager();
        if (manager == null)
            return null;
        return manager.loadAdapter(this, adapter.getName());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.sync.IModelChange#applyToModel(java.lang.Object)
	 */
    public void applyToModel(Object model) throws ModelUpdateException {
        if (model == null)
            throw new ModelUpdateException("Model cannot be null", this, null);
        if (model instanceof IDocument) {
            try {
                ((IDocument) model).replace(getOffset(), getLengthOfReplacedText(), getText());
            } catch (BadLocationException e) {
                throw new ModelUpdateException("Exception applying change to document", this, model);
            }
        } else
            throw new ModelUpdateException("Incorrect type of model to apply change", this, model);
    }
}

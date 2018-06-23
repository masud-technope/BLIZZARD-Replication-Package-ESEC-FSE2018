/**
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 */
package org.eclipse.ecf.example.pubsub;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.eclipse.ecf.pubsub.ISubscription;
import org.eclipse.ecf.pubsub.model.IReplicaModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class SubscriptionView extends ViewPart implements IAppendableListListener {

    public static final String VIEW_ID = "org.eclipse.ecf.example.pubsub.subscription";

    protected ISubscription subscription;

    protected AppendableList model;

    protected Text text;

    public synchronized void setSubscription(ISubscription subscription) {
        this.subscription = subscription;
        setPartName("Subscription: " + subscription.getID());
        Object object = subscription.getSubscribedService();
        if (object instanceof IReplicaModel) {
            Object data = ((IReplicaModel) object).getData();
            if (data instanceof AppendableList) {
                model = (AppendableList) data;
                model.addListener(this);
                Object[] values = model.getValues();
                StringWriter buf = new StringWriter();
                PrintWriter writer = new PrintWriter(buf);
                for (int i = 0; i < values.length; ++i) writer.println(values[i]);
                writer.close();
                text.setText(buf.toString());
            }
        }
    }

    public void createPartControl(Composite parent) {
        text = new Text(parent, SWT.WRAP | SWT.READ_ONLY);
    }

    public void setFocus() {
        text.setFocus();
    }

    public void dispose() {
        if (model != null)
            model.removeListener(this);
        if (subscription != null)
            subscription.dispose();
        super.dispose();
    }

    public synchronized void appended(AppendableList list, final Object value) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                text.append(String.valueOf(value) + System.getProperty("line.separator"));
            }
        });
    }
}

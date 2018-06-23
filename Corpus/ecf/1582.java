/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Angelo ZERR <angelo.zerr@gmail.com>. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.springframework.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} that creates ECF identity {@link ID} with String ID by
 * using {@link IIDFactory#createStringID(String)}.
 * 
 */
public class StringIDFactoryBean extends AbstractIDFactoryBean {

    private String stringID = null;

    public void setStringID(String stringID) {
        this.stringID = stringID;
    }

    protected ID createIdentity() {
        IIDFactory idFactory = getIdFactory();
        return idFactory.createStringID(stringID);
    }

    public void destroy() throws Exception {
        super.destroy();
        this.stringID = null;
    }
}

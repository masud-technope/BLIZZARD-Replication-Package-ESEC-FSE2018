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
 * {@link FactoryBean} that creates ECF identity {@link ID} with Long ID by
 * using {@link IIDFactory#createLongID(long)}.
 * 
 */
public class LongIDFactoryBean extends AbstractIDFactoryBean {

    private long longID;

    public void setLongID(long longID) {
        this.longID = longID;
    }

    protected ID createIdentity() {
        IIDFactory idFactory = getIdFactory();
        return idFactory.createLongID(longID);
    }
}

/****************************************************************************
* Copyright (c) 2004 Composent, Inc. and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Composent, Inc. - initial API and implementation
*****************************************************************************/
package org.eclipse.ecf.provider.generic.gmm;

public class MemberChanged {

    Member member;

    boolean added;

    public  MemberChanged(Member member, boolean added) {
        this.member = member;
        this.added = added;
    }

    public Member getMember() {
        return member;
    }

    public boolean getAdded() {
        return added;
    }
}

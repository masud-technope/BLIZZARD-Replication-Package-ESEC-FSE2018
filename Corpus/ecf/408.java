/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rest.twitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserStatus implements IUserStatus {

    private String text;

    private String source;

    private Date createDate;

    public  UserStatus(String createdString, String source, String text) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            createDate = format.parse(createdString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.source = source;
        this.text = text;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getSource() {
        return source;
    }

    public String getText() {
        return text;
    }
}

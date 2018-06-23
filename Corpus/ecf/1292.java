/*******************************************************************************
 * Copyright (c) 2007 Chris Aniszczyk and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chris Aniszczyk <caniszczyk@gmail.com> - initial API and implementation
 *    Jörn Dinkla <devnull@dinkla.com> - bug 192574
 ******************************************************************************/
package org.eclipse.ecf.internal.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CategoryPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public  CategoryPreferencePage() {
    // nothing
    }

    public  CategoryPreferencePage(String title) {
        super(title);
    }

    public  CategoryPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }

    protected Control createContents(Composite parent) {
        noDefaultAndApplyButton();
        return null;
    }

    public void init(IWorkbench workbench) {
    // nothing
    }
}

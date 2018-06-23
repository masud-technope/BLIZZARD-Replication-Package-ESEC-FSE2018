/****************************************************************************
 * Copyright (c) 2007 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.bittorrent.ui;

import java.io.File;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

final class BitTorrentConnectWizardPage extends WizardPage {

    private Text torrentText;

    private Text targetText;

    private Button browseTorrentBtn;

    private Button browseTargetBtn;

    private String torrentFile;

     BitTorrentConnectWizardPage() {
        super("");
        setTitle(Messages.getString("BitTorrentConnectWizardPage.File_Sharing"));
        setDescription(Messages.getString("BitTorrentConnectWizardPage.File_Sharing.Description"));
        setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/filewiz_download.png"));
        setPageComplete(false);
    }

     BitTorrentConnectWizardPage(String torrentFile) {
        this();
        this.torrentFile = torrentFile;
    }

    private void addListeners() {
        torrentText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                final String file = torrentText.getText().trim();
                if (//$NON-NLS-1$
                file.equals("")) {
                    setErrorMessage(Messages.getString("BitTorrentConnectWizardPage.file_must_be_entered"));
                } else {
                    final File torrent = new File(file);
                    if (torrent.isDirectory()) {
                        setErrorMessage(Messages.getString("BitTorrentConnectWizardPage.path_is_mapped"));
                    } else if (!torrent.canRead()) {
                        setErrorMessage(Messages.getString("BitTorrentConnectWizardPage.file_cannot_read"));
                    } else {
                        setErrorMessage(null);
                    }
                }
            }
        });
        targetText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                final String target = torrentText.getText().trim();
                if (//$NON-NLS-1$
                target.equals("")) {
                    //setErrorMessage("A destination must be set.");
                    setErrorMessage(Messages.getString("BitTorrentConnectWizardPage.destination_must_set"));
                } else {
                    setErrorMessage(null);
                }
            }
        });
        browseTorrentBtn.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                final FileDialog dialog = new FileDialog(browseTorrentBtn.getShell(), SWT.OPEN);
                //$NON-NLS-1$
                dialog.setFilterExtensions(//$NON-NLS-1$
                new String[] { "*.torrent" });
                if (torrentFile != null) {
                    int lastIndex = torrentFile.lastIndexOf('/');
                    if (lastIndex == -1)
                        lastIndex = torrentFile.lastIndexOf('\\');
                    if (lastIndex != -1)
                        dialog.setFilterPath(torrentFile.substring(0, lastIndex));
                }
                final String torrent = dialog.open();
                if (torrent != null) {
                    torrentText.setText(torrent);
                }
            }
        });
        browseTargetBtn.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                final FileDialog dialog = new FileDialog(browseTorrentBtn.getShell(), SWT.OPEN);
                final String target = dialog.open();
                if (target != null) {
                    targetText.setText(target);
                }
            }
        });
    }

    public void createControl(Composite parent) {
        parent = new Composite(parent, SWT.NONE);
        parent.setLayout(new GridLayout(3, false));
        final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        Label label = new Label(parent, SWT.LEFT);
        label.setText(Messages.getString("BitTorrentConnectWizardPage.Torrent"));
        torrentText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        torrentText.setLayoutData(data);
        browseTorrentBtn = new Button(parent, SWT.PUSH);
        browseTorrentBtn.setText(Messages.getString("BitTorrentConnectWizardPage.Browse1"));
        label = new Label(parent, SWT.LEFT);
        label.setText(Messages.getString("BitTorrentConnectWizardPage.Target_Path"));
        targetText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        targetText.setLayoutData(data);
        browseTargetBtn = new Button(parent, SWT.PUSH);
        browseTargetBtn.setText(Messages.getString("BitTorrentConnectWizardPage.Browse2"));
        if (torrentFile != null) {
            torrentText.setText(torrentFile);
            targetText.setFocus();
        }
        addListeners();
        org.eclipse.jface.dialogs.Dialog.applyDialogFont(parent);
        setControl(parent);
    }

    String getTorrentName() {
        return torrentText.getText();
    }

    String getTargetName() {
        return targetText.getText();
    }

    public void setErrorMessage(String message) {
        super.setErrorMessage(message);
        setPageComplete(message == null);
    }
}

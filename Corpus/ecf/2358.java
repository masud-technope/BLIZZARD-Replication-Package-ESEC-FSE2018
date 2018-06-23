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
package org.eclipse.ecf.filetransfer.ui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.eclipse.ecf.filetransfer.*;
import org.eclipse.ecf.internal.filetransfer.ui.Messages;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

public class FileTransfersView extends ViewPart {

    public static final Map transfers = new HashMap();

    //$NON-NLS-1$
    public static final String ID = "org.eclipse.ecf.filetransfer.ui.FileTransfersView";

    private static final String[] COLUMNS = { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    Messages.getString("FileTransfersView_COLUMN_NAME"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    Messages.getString("FileTransfersView_COLUMN_DOWNLOAD"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    Messages.getString("FileTransfersView_COLUMN_UPLOAD"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    Messages.getString("FileTransfersView_COLUMN_LOCAL_FILE"), Messages.getString("FileTransfersView_COLUMN_DONE"), Messages.getString("FileTransfersView_COLUMN_START_TIME"), Messages.getString("FileTransfersView_COLUMN_END_TIME"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    Messages.getString("FileTransfersView_COLUMN_RATE") };

    private static final int[] WIDTHS = { 225, 70, 70, 325, 50, 90, 90, 75 };

    //$NON-NLS-1$
    private static final String DATEFORMAT = "HH:mm:ss";

    static final SimpleDateFormat SDF = new SimpleDateFormat(DATEFORMAT);

    static final Object[] EMPTY_ARRAY = new Object[0];

    private static final double GIGABYTE = Math.pow(2, 30);

    private static final double MEGABYTE = Math.pow(2, 20);

    private static final double KILOBYTE = Math.pow(2, 10);

    private static final int NAME = 0;

    private static final int DOWNLOADED = NAME + 1;

    private static final int UPLOADED = DOWNLOADED + 1;

    private static final int FILENAME = UPLOADED + 1;

    private static final int DONE = FILENAME + 1;

    private static final int STARTTIME = DONE + 1;

    private static final int ENDTIME = STARTTIME + 1;

    private static final int RATE = ENDTIME + 1;

    static class FileTransferEntry {

        IFileTransfer fileTransfer;

        String localFileName;

        long startTime;

        public  FileTransferEntry(IFileTransfer fileTransfer, String localFileName) {
            this.fileTransfer = fileTransfer;
            this.localFileName = localFileName;
            this.startTime = System.currentTimeMillis();
        }

        public  FileTransferEntry(IFileTransfer fileTransfer) {
            this(fileTransfer, null);
        }

        public IFileTransfer getFileTransfer() {
            return fileTransfer;
        }

        public String getLocalFileName() {
            return localFileName;
        }

        public long getStartTime() {
            return startTime;
        }
    }

    public static FileTransfersView addTransfer(IFileTransfer transfer) {
        FileTransferEntry entry = new FileTransferEntry(transfer, null);
        transfers.put(transfer, entry);
        if (instance != null) {
            instance.add(entry);
        }
        return instance;
    }

    public static FileTransfersView addTransfer(IFileTransfer transfer, String localFileName) {
        FileTransferEntry entry = new FileTransferEntry(transfer, localFileName);
        transfers.put(transfer, entry);
        if (instance != null) {
            instance.add(entry);
        }
        return instance;
    }

    TableViewer viewer;

    private Table table;

    private Action resumeAction;

    private Action pauseAction;

    private Action removeAction;

    private Action launchAction;

    static String getTwoDigitNumber(long value) {
        if (value > GIGABYTE) {
            double num = value / GIGABYTE;
            //$NON-NLS-1$ //$NON-NLS-2$
            return Double.toString(Math.floor(num * 100) / 100) + " " + Messages.getString("FileTransfersView_GB");
        } else if (value > MEGABYTE) {
            double num = value / MEGABYTE;
            //$NON-NLS-1$ //$NON-NLS-2$
            return Double.toString(Math.floor(num * 100) / 100) + " " + Messages.getString("FileTransfersView_MB");
        } else if (value > KILOBYTE) {
            double num = value / KILOBYTE;
            //$NON-NLS-1$ //$NON-NLS-2$
            return Double.toString(Math.floor(num * 100) / 100) + " " + Messages.getString("FileTransfersView_KB");
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        return value + " " + Messages.getString("FileTransfersView_BYTES");
    }

    private static FileTransfersView instance;

    public  FileTransfersView() {
        instance = this;
    }

    public void dispose() {
        instance = null;
        super.dispose();
    }

    public void createPartControl(Composite parent) {
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        //viewer.setSorter(new ViewerSorter());
        viewer.setInput(getViewSite());
        table = viewer.getTable();
        for (int i = 0; i < WIDTHS.length; i++) {
            TableColumn col = new TableColumn(table, SWT.LEFT);
            col.setText(COLUMNS[i]);
            col.setAlignment(SWT.CENTER);
            col.setWidth(WIDTHS[i]);
        }
        Iterator iterator = transfers.keySet().iterator();
        while (iterator.hasNext()) {
            IFileTransfer fileTransfer = (IFileTransfer) iterator.next();
            add((FileTransferEntry) transfers.get(fileTransfer));
        }
        makeActions();
        hookContextMenu();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setSize(1000, 1000);
    }

    private void add(FileTransferEntry transferEntry) {
        if (table != null && !table.isDisposed()) {
            viewer.add(transferEntry);
        }
    }

    public void update(IFileTransfer transfer) {
        if (table != null && !table.isDisposed()) {
            FileTransferEntry entry = (FileTransferEntry) transfers.get(transfer);
            viewer.update(entry, COLUMNS);
        }
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
                enableActions();
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        table.setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    void fillContextMenu(IMenuManager manager) {
        manager.add(launchAction);
        manager.add(new Separator());
        manager.add(resumeAction);
        manager.add(pauseAction);
        manager.add(removeAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void makeActions() {
        resumeAction = new Action() {

            public void run() {
                ISelection sel = viewer.getSelection();
                if (!(sel instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection ssel = (IStructuredSelection) sel;
                Object o = ssel.getFirstElement();
                if (o instanceof FileTransferEntry) {
                    FileTransferEntry entry = (FileTransferEntry) o;
                    IFileTransfer transfer = entry.getFileTransfer();
                    IFileTransferPausable pausable = (IFileTransferPausable) transfer.getAdapter(IFileTransferPausable.class);
                    if (pausable != null) {
                        pausable.resume();
                    }
                }
            }
        };
        //$NON-NLS-1$
        resumeAction.setId("resume");
        //$NON-NLS-1$
        resumeAction.setText(Messages.getString("FileTransfersView_MENU_RESUME_TEXT"));
        pauseAction = new Action() {

            public void run() {
                ISelection sel = viewer.getSelection();
                if (!(sel instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection ssel = (IStructuredSelection) sel;
                Object o = ssel.getFirstElement();
                if (o instanceof FileTransferEntry) {
                    FileTransferEntry entry = (FileTransferEntry) o;
                    IFileTransfer transfer = entry.getFileTransfer();
                    IFileTransferPausable pausable = (IFileTransferPausable) transfer.getAdapter(IFileTransferPausable.class);
                    if (pausable != null) {
                        pausable.pause();
                    }
                }
            }
        };
        //$NON-NLS-1$
        pauseAction.setText(Messages.getString("FileTransfersView_MENU_PAUSE_TEXT"));
        removeAction = new Action() {

            public void run() {
                ISelection sel = viewer.getSelection();
                if (!(sel instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection ssel = (IStructuredSelection) sel;
                Object o = ssel.getFirstElement();
                if (o instanceof FileTransferEntry) {
                    FileTransferEntry entry = (FileTransferEntry) o;
                    IFileTransfer transfer = entry.getFileTransfer();
                    transfer.cancel();
                    viewer.remove(entry);
                    transfers.remove(entry);
                }
            }
        };
        //$NON-NLS-1$
        removeAction.setText(Messages.getString("FileTransfersView_MENU_REMOVE_TEXT"));
        removeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        launchAction = new Action() {

            public void run() {
                ISelection sel = viewer.getSelection();
                if (!(sel instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection ssel = (IStructuredSelection) sel;
                Object o = ssel.getFirstElement();
                if (o != null && o instanceof FileTransferEntry) {
                    FileTransferEntry entry = (FileTransferEntry) o;
                    String fileName = entry.getLocalFileName();
                    Program.launch(fileName);
                }
            }
        };
        //$NON-NLS-1$
        launchAction.setText(Messages.getString("FileTransfersView_MENU_LAUNCH_TEXT"));
    }

    void enableActions() {
        ISelection is = viewer.getSelection();
        resumeAction.setEnabled(false);
        pauseAction.setEnabled(false);
        if (is instanceof IStructuredSelection) {
            IStructuredSelection iss = (IStructuredSelection) is;
            removeAction.setEnabled(!iss.isEmpty());
            // Enable for launch if first element is download, and is done
            Object o = iss.getFirstElement();
            if (o instanceof FileTransferEntry) {
                FileTransferEntry entry = (FileTransferEntry) o;
                IFileTransfer transfer = entry.getFileTransfer();
                String localFileName = entry.getLocalFileName();
                if (transfer instanceof IIncomingFileTransfer && transfer.isDone() && transfer.getException() == null && localFileName != null) {
                    launchAction.setEnabled(true);
                }
                IFileTransferPausable pausable = (IFileTransferPausable) transfer.getAdapter(IFileTransferPausable.class);
                if (!transfer.isDone() && pausable != null) {
                    resumeAction.setEnabled(true);
                    pauseAction.setEnabled(true);
                    return;
                }
            }
        }
    }

    public void setFocus() {
        table.setFocus();
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        String endTime = null;

        public String getColumnText(Object obj, int index) {
            if (!(obj instanceof FileTransferEntry)) {
                return getText(obj);
            }
            FileTransferEntry entry = (FileTransferEntry) obj;
            IFileTransfer transfer = entry.getFileTransfer();
            switch(index) {
                case NAME:
                    return transfer.getID().getName();
                case DOWNLOADED:
                    if (transfer instanceof IIncomingFileTransfer)
                        return getTwoDigitNumber(((IIncomingFileTransfer) transfer).getBytesReceived());
                    return Messages.getString("FileTransfersView_NA");
                case UPLOADED:
                    if (transfer instanceof IOutgoingFileTransfer)
                        return getTwoDigitNumber(((IOutgoingFileTransfer) transfer).getBytesSent());
                    return Messages.getString("FileTransfersView_NA");
                case FILENAME:
                    String fileName = entry.getLocalFileName();
                    return //$NON-NLS-1$
                    (fileName == null) ? //$NON-NLS-1$
                    "" : //$NON-NLS-1$
                    fileName;
                case DONE:
                    if (transfer.isDone()) {
                        Exception e = transfer.getException();
                        //$NON-NLS-1$ //$NON-NLS-2$
                        return (e == null) ? Messages.getString("FileTransfersView_YES") : Messages.getString("FileTransfersView_ERROR");
                    }
                    double percentComplete = transfer.getPercentComplete();
                    return Double.toString(percentComplete + '%');
                case STARTTIME:
                    return SDF.format(new Date(entry.getStartTime()));
                case ENDTIME:
                    if (transfer.isDone()) {
                        if (endTime == null)
                            endTime = SDF.format(new Date());
                        return endTime;
                    }
                    return Messages.getString("FileTransfersView_IN_PROGRESS");
                case RATE:
                    long fileLength = transfer.getFileLength();
                    if (fileLength == -1)
                        return Messages.getString("FileTransfersView_NA");
                    return showTransferRate(entry.getStartTime(), fileLength * transfer.getPercentComplete());
            }
            return getText(obj);
        }

        public Image getColumnImage(Object obj, int index) {
            return null;
        }
    }

    class ViewContentProvider implements IStructuredContentProvider {

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        // nothing to do in this case
        }

        public void dispose() {
        // nothing to do in this case
        }

        public Object[] getElements(Object inputElement) {
            return EMPTY_ARRAY;
        }
    }

    String showTransferRate(long startTime, double bytesReceived) {
        double convertedSize;
        String unit;
        double size = (bytesReceived / ((System.currentTimeMillis() + 1 - startTime) / 1000.0));
        if (size / (1024 * 1024 * 1024) >= 1) {
            convertedSize = size / (1024 * 1024 * 1024);
            //$NON-NLS-1$
            unit = Messages.getString("FileTransfersView_GB");
        } else if (size / (1024 * 1024) >= 1) {
            convertedSize = size / (1024 * 1024);
            //$NON-NLS-1$
            unit = Messages.getString("FileTransfersView_MB");
        } else if (size / 1024 >= 1) {
            convertedSize = size / 1024;
            //$NON-NLS-1$
            unit = Messages.getString("FileTransfersView_KB");
        } else {
            convertedSize = size;
            //$NON-NLS-1$
            unit = Messages.getString("FileTransfersView_BYTES");
        }
        //$NON-NLS-1$
        DecimalFormat df = new DecimalFormat(NLS.bind("0.00 {0}/s", unit));
        return df.format(convertedSize);
    }
}

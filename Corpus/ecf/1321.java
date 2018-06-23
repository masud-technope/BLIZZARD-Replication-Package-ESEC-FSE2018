/*******************************************************************************
 * Copyright (c) 2006 IBM, Inc and Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Chris Aniszczyk <zx@us.ibm.com> - initial API and implementation
 * 				 Ken Gilmer <kgilmer@gmail.com>
 ******************************************************************************/
package org.eclipse.ecf.tutorial.scribbleshare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.AbstractTool;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.Box;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.DrawSettings;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.Line;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.ListContentProvider;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.Oval;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.Pencil;
import org.eclipse.ecf.tutorial.scribbleshare.toolbox.ToolboxLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class ScribbleView extends ViewPart {

    private Display display;

    private Canvas canvas;

    private AbstractTool currentTool;

    private DrawSettings drawSettings = new DrawSettings();

    private List tools;

    // Default color is black
    int red = 0;

    int blue = 0;

    int green = 0;

    // Channel to send data on
    IChannel channel = null;

    public  ScribbleView() {
        tools = new ArrayList();
    }

    public void setUserColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void setChannel(IChannel channel) {
        this.channel = channel;
    }

    /**
	 * This is called when a remote client calls <code>sendTool</code>.
	 * 
	 * @param message
	 */
    public void handleDrawLine(byte[] message) {
        ByteArrayInputStream bins = new ByteArrayInputStream(message);
        // DataInputStream dins = new DataInputStream(bins);
        try {
            ObjectInputStream ois = new ObjectInputStream(bins);
            AbstractTool tool = (AbstractTool) ois.readObject();
            // Apply the tool to the local canvas.
            tool.draw(canvas);
            tools.add(tool);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void sendTool(AbstractTool tool) {
        if (channel != null && currentTool != null) {
            try {
                ByteArrayOutputStream bouts = new ByteArrayOutputStream();
                // create a byte array from serialized Tool
                ObjectOutputStream douts = new ObjectOutputStream(bouts);
                douts.writeObject(tool);
                // send serialized tool to other clients.
                channel.sendMessage(bouts.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createPartControl(Composite parent) {
        Composite backgroundComposite = new Composite(parent, SWT.NONE);
        GridLayout backgroundGridLayout = new GridLayout(3, false);
        backgroundGridLayout.marginHeight = 0;
        backgroundGridLayout.marginBottom = 0;
        backgroundGridLayout.marginLeft = 0;
        backgroundGridLayout.marginRight = 0;
        backgroundGridLayout.marginWidth = 0;
        backgroundGridLayout.horizontalSpacing = 0;
        backgroundComposite.setLayout(backgroundGridLayout);
        backgroundComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite paletteComposite = new Composite(backgroundComposite, SWT.NONE);
        backgroundGridLayout = new GridLayout();
        backgroundGridLayout.marginHeight = 0;
        backgroundGridLayout.marginBottom = 0;
        backgroundGridLayout.marginLeft = 0;
        backgroundGridLayout.marginRight = 0;
        backgroundGridLayout.marginWidth = 0;
        backgroundGridLayout.horizontalSpacing = 0;
        paletteComposite.setLayout(backgroundGridLayout);
        GridData toolboxGridData = new GridData(GridData.FILL_VERTICAL);
        toolboxGridData.widthHint = 60;
        paletteComposite.setLayoutData(toolboxGridData);
        final TableViewer toolbox = new TableViewer(paletteComposite, SWT.FLAT | SWT.FULL_SELECTION);
        toolboxGridData = new GridData(GridData.FILL_BOTH);
        toolbox.getTable().setLayoutData(toolboxGridData);
        toolbox.setLabelProvider(new ToolboxLabelProvider());
        toolbox.setContentProvider(new ListContentProvider());
        toolbox.setInput(createTools());
        toolbox.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                currentTool = (AbstractTool) ((StructuredSelection) toolbox.getSelection()).getFirstElement();
                // Apply the drawSettings to the currently selected tool.
                currentTool.setDrawSettings(drawSettings);
            }
        });
        // Create the UI widgets to modify the DrawSettings instance.
        createSettings(paletteComposite);
        Label separator = new Label(backgroundComposite, SWT.SEPARATOR | /* SWT.NONE */
        SWT.VERTICAL);
        separator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        canvas = new Canvas(backgroundComposite, SWT.NONE);
        canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
        display = parent.getDisplay();
        canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        canvas.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                for (Iterator i = tools.iterator(); i.hasNext(); ) {
                    AbstractTool at = (AbstractTool) i.next();
                    at.draw(canvas);
                }
            }
        });
        Listener listener = new Listener() {

            public void handleEvent(Event event) {
                if (currentTool != null) {
                    // Have the tool interpret the mouse events.
                    currentTool.handleUIEvent(event, canvas);
                    // other clients for rendering.
                    if (currentTool.isComplete()) {
                        tools.add(currentTool);
                        sendTool(currentTool);
                        // Only do this once per Tool.
                        currentTool.setComplete(false);
                    }
                /*else {
						if (currentTool instanceof Pencil) {
							tools.add(currentTool);
						}
					}*/
                }
            }
        };
        canvas.addListener(SWT.MouseDown, listener);
        canvas.addListener(SWT.MouseMove, listener);
        canvas.addListener(SWT.MouseUp, listener);
    }

    private void createSettings(Composite paletteComposite) {
        // Size of Pen (drawWidth) set on the GC.
        {
            Label l = new Label(paletteComposite, SWT.NONE);
            l.setText("Pen Size");
            final Text t = new Text(paletteComposite, SWT.BORDER);
            t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            t.setText("1");
            t.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    drawSettings.setPenWidth(Integer.parseInt(t.getText()));
                    currentTool.setDrawSettings(drawSettings);
                }
            });
        }
        // Toggles the antialias property on the GC.
        {
            final Button b = new Button(paletteComposite, SWT.CHECK);
            b.setText("Antialias");
            b.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent e) {
                    drawSettings.setAntialias(b.getSelection());
                    currentTool.setDrawSettings(drawSettings);
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
    }

    /**
	 * Create the list of tools available to be used. Add new subclasses of
	 * AbstractTool here.
	 * 
	 * @return
	 */
    private List createTools() {
        List toolList = new ArrayList();
        toolList.add(new Pencil());
        toolList.add(new Box());
        toolList.add(new Line());
        toolList.add(new Oval());
        return toolList;
    }

    public void setFocus() {
        canvas.setFocus();
    }
}

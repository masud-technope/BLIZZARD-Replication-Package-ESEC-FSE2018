/*******************************************************************************
 *  Copyright (c) 2005, 2014 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.internal.core.iproduct.IArgumentsInfo;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ArgumentsInfo extends ProductObject implements IArgumentsInfo {

    private static final long serialVersionUID = 1L;

    private String[] fProgramArgs = new String[8];

    private String[] fProgramArgsLin = new String[8];

    private String[] fProgramArgsMac = new String[8];

    private String[] fProgramArgsSol = new String[8];

    private String[] fProgramArgsWin = new String[8];

    private String[] fVMArgs = new String[8];

    private String[] fVMArgsLin = new String[8];

    private String[] fVMArgsMac = new String[8];

    private String[] fVMArgsSol = new String[8];

    private String[] fVMArgsWin = new String[8];

    public  ArgumentsInfo(IProductModel model) {
        super(model);
        this.initializeArgs(fProgramArgs);
        this.initializeArgs(fProgramArgsLin);
        this.initializeArgs(fProgramArgsMac);
        this.initializeArgs(fProgramArgsSol);
        this.initializeArgs(fProgramArgsWin);
        this.initializeArgs(fVMArgs);
        this.initializeArgs(fVMArgsLin);
        this.initializeArgs(fVMArgsMac);
        this.initializeArgs(fVMArgsSol);
        this.initializeArgs(fVMArgsWin);
    }

    private void initializeArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            //$NON-NLS-1$
            args[i] = "";
        }
    }

    @Override
    public void setProgramArguments(String args, int platform) {
        setProgramArguments(args, platform, L_ARGS_ARCH_ALL);
    }

    @Override
    public void setProgramArguments(String args, int platform, int arch) {
        String old;
        if (args == null)
            //$NON-NLS-1$
            args = "";
        switch(platform) {
            case L_ARGS_ALL:
                old = fProgramArgs[arch];
                fProgramArgs[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_PROG_ARGS, old, fProgramArgs[arch]);
                break;
            case L_ARGS_LINUX:
                old = fProgramArgsLin[arch];
                fProgramArgsLin[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_PROG_ARGS_LIN, old, fProgramArgsLin[arch]);
                break;
            case L_ARGS_MACOS:
                old = fProgramArgsMac[arch];
                fProgramArgsMac[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_PROG_ARGS_MAC, old, fProgramArgsMac[arch]);
                break;
            case L_ARGS_SOLAR:
                old = fProgramArgsSol[arch];
                fProgramArgsSol[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_PROG_ARGS_SOL, old, fProgramArgsSol[arch]);
                break;
            case L_ARGS_WIN32:
                old = fProgramArgsWin[arch];
                fProgramArgsWin[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_PROG_ARGS_WIN, old, fProgramArgsWin[arch]);
                break;
        }
    }

    @Override
    public String getProgramArguments(int platform) {
        return getProgramArguments(platform, L_ARGS_ARCH_ALL);
    }

    @Override
    public String getProgramArguments(int platform, int arch) {
        switch(platform) {
            case L_ARGS_ALL:
                return fProgramArgs[arch];
            case L_ARGS_LINUX:
                return fProgramArgsLin[arch];
            case L_ARGS_MACOS:
                return fProgramArgsMac[arch];
            case L_ARGS_SOLAR:
                return fProgramArgsSol[arch];
            case L_ARGS_WIN32:
                return fProgramArgsWin[arch];
        }
        //$NON-NLS-1$
        return "";
    }

    @Override
    public String getCompleteProgramArguments(String os) {
        //$NON-NLS-1$
        return getCompleteProgramArguments(os, "");
    }

    @Override
    public String getCompleteProgramArguments(String os, String arch) {
        int archIndex = L_ARGS_ARCH_ALL;
        if (arch != null && arch.length() > 0) {
            if (Platform.ARCH_X86.equals(arch)) {
                archIndex = L_ARGS_ARCH_X86;
            } else if (Platform.ARCH_X86_64.equals(arch)) {
                archIndex = L_ARGS_ARCH_X86_64;
            } else if (Platform.ARCH_PPC.equals(arch)) {
                archIndex = L_ARGS_ARCH_PPC;
            } else if (Platform.ARCH_IA64.equals(arch)) {
                archIndex = L_ARGS_ARCH_IA_64;
            } else if (Platform.ARCH_IA64_32.equals(arch)) {
                archIndex = L_ARGS_ARCH_IA_64_32;
            } else if (Platform.ARCH_PA_RISC.equals(arch)) {
                archIndex = L_ARGS_ARCH_PA_RISC;
            } else if (Platform.ARCH_SPARC.equals(arch)) {
                archIndex = L_ARGS_ARCH_SPARC;
            }
        }
        //$NON-NLS-1$
        String archArgsAllPlatforms = archIndex > 0 ? getProgramArguments(L_ARGS_ALL, archIndex) : "";
        String archArgs;
        if (Platform.OS_WIN32.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getProgramArguments(L_ARGS_WIN32, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getProgramArguments(L_ARGS_WIN32), fProgramArgs[L_ARGS_ARCH_ALL]);
        } else if (Platform.OS_LINUX.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getProgramArguments(L_ARGS_LINUX, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getProgramArguments(L_ARGS_LINUX), fProgramArgs[L_ARGS_ARCH_ALL]);
        } else if (Platform.OS_MACOSX.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getProgramArguments(L_ARGS_MACOS, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getProgramArguments(L_ARGS_MACOS), fProgramArgs[L_ARGS_ARCH_ALL]);
        } else if (Platform.OS_SOLARIS.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getProgramArguments(L_ARGS_SOLAR, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getProgramArguments(L_ARGS_SOLAR), fProgramArgs[L_ARGS_ARCH_ALL]);
        } else {
            //$NON-NLS-1$
            return getCompleteArgs(archArgsAllPlatforms, "", fProgramArgs[L_ARGS_ALL]);
        }
    }

    @Override
    public void setVMArguments(String args, int platform) {
        setVMArguments(args, platform, L_ARGS_ARCH_ALL);
    }

    @Override
    public void setVMArguments(String args, int platform, int arch) {
        String old;
        if (args == null)
            //$NON-NLS-1$
            args = "";
        switch(platform) {
            case L_ARGS_ALL:
                old = fVMArgs[arch];
                fVMArgs[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_VM_ARGS, old, fVMArgs[arch]);
                break;
            case L_ARGS_LINUX:
                old = fVMArgsLin[arch];
                fVMArgsLin[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_VM_ARGS_LIN, old, fVMArgsLin[arch]);
                break;
            case L_ARGS_MACOS:
                old = fVMArgsMac[arch];
                fVMArgsMac[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_VM_ARGS_MAC, old, fVMArgsMac[arch]);
                break;
            case L_ARGS_SOLAR:
                old = fVMArgsSol[arch];
                fVMArgsSol[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_VM_ARGS_SOL, old, fVMArgsSol[arch]);
                break;
            case L_ARGS_WIN32:
                old = fVMArgsWin[arch];
                fVMArgsWin[arch] = args;
                if (isEditable())
                    firePropertyChanged(P_VM_ARGS_WIN, old, fVMArgsWin[arch]);
                break;
        }
    }

    @Override
    public String getVMArguments(int platform) {
        return getVMArguments(platform, L_ARGS_ARCH_ALL);
    }

    @Override
    public String getVMArguments(int platform, int arch) {
        switch(platform) {
            case L_ARGS_ALL:
                return fVMArgs[arch];
            case L_ARGS_LINUX:
                return fVMArgsLin[arch];
            case L_ARGS_MACOS:
                return fVMArgsMac[arch];
            case L_ARGS_SOLAR:
                return fVMArgsSol[arch];
            case L_ARGS_WIN32:
                return fVMArgsWin[arch];
        }
        //$NON-NLS-1$
        return "";
    }

    @Override
    public String getCompleteVMArguments(String os) {
        //$NON-NLS-1$
        return getCompleteVMArguments(os, "");
    }

    @Override
    public String getCompleteVMArguments(String os, String arch) {
        int archIndex = L_ARGS_ARCH_ALL;
        if (arch != null && arch.length() > 0) {
            if (Platform.ARCH_X86.equals(arch)) {
                archIndex = L_ARGS_ARCH_X86;
            } else if (Platform.ARCH_X86_64.equals(arch)) {
                archIndex = L_ARGS_ARCH_X86_64;
            } else if (Platform.ARCH_PPC.equals(arch)) {
                archIndex = L_ARGS_ARCH_PPC;
            } else if (Platform.ARCH_IA64.equals(arch)) {
                archIndex = L_ARGS_ARCH_IA_64;
            } else if (Platform.ARCH_IA64_32.equals(arch)) {
                archIndex = L_ARGS_ARCH_IA_64_32;
            } else if (Platform.ARCH_PA_RISC.equals(arch)) {
                archIndex = L_ARGS_ARCH_PA_RISC;
            } else if (Platform.ARCH_SPARC.equals(arch)) {
                archIndex = L_ARGS_ARCH_SPARC;
            }
        }
        //$NON-NLS-1$
        String archArgsAllPlatforms = archIndex > 0 ? getVMArguments(L_ARGS_ALL, archIndex) : "";
        String archArgs;
        if (Platform.OS_WIN32.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getVMArguments(L_ARGS_WIN32, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getVMArguments(L_ARGS_WIN32), fVMArgs[L_ARGS_ARCH_ALL]);
        } else if (Platform.OS_LINUX.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getVMArguments(L_ARGS_LINUX, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getVMArguments(L_ARGS_LINUX), fVMArgs[L_ARGS_ARCH_ALL]);
        } else if (Platform.OS_MACOSX.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getVMArguments(L_ARGS_MACOS, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getVMArguments(L_ARGS_MACOS), fVMArgs[L_ARGS_ARCH_ALL]);
        } else if (Platform.OS_SOLARIS.equals(os)) {
            //$NON-NLS-1$
            archArgs = archIndex > 0 ? getVMArguments(L_ARGS_SOLAR, archIndex) + " " + archArgsAllPlatforms : archArgsAllPlatforms;
            return getCompleteArgs(archArgs, getVMArguments(L_ARGS_SOLAR), fVMArgs[L_ARGS_ARCH_ALL]);
        } else {
            //$NON-NLS-1$
            return getCompleteArgs(archArgsAllPlatforms, "", fVMArgs[L_ARGS_ARCH_ALL]);
        }
    }

    private String getCompleteArgs(String archArgs, String platformArgs, String univArgs) {
        String args = archArgs;
        if (platformArgs.length() > 0)
            //$NON-NLS-1$
            args = platformArgs + " " + args;
        if (univArgs.length() > 0)
            //$NON-NLS-1$
            args = univArgs + " " + args;
        return args.trim();
    }

    @Override
    public void parse(Node node) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node child = list.item(i);
            String[] parentArgs = fProgramArgs;
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(P_PROG_ARGS)) {
                    parentArgs = fProgramArgs;
                    fProgramArgs[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_PROG_ARGS_LIN)) {
                    parentArgs = fProgramArgsLin;
                    fProgramArgsLin[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_PROG_ARGS_MAC)) {
                    parentArgs = fProgramArgsMac;
                    fProgramArgsMac[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_PROG_ARGS_SOL)) {
                    parentArgs = fProgramArgsSol;
                    fProgramArgsSol[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_PROG_ARGS_WIN)) {
                    parentArgs = fProgramArgsWin;
                    fProgramArgsWin[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_VM_ARGS)) {
                    parentArgs = fVMArgs;
                    fVMArgs[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_VM_ARGS_LIN)) {
                    parentArgs = fVMArgsLin;
                    fVMArgsLin[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_VM_ARGS_MAC)) {
                    parentArgs = fVMArgsMac;
                    fVMArgsMac[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_VM_ARGS_SOL)) {
                    parentArgs = fVMArgsSol;
                    fVMArgsSol[L_ARGS_ARCH_ALL] = getText(child).trim();
                } else if (child.getNodeName().equals(P_VM_ARGS_WIN)) {
                    parentArgs = fVMArgsWin;
                    fVMArgsWin[L_ARGS_ARCH_ALL] = getText(child).trim();
                }
                // Look for child nodes which would be arch specific.
                NodeList childNodes = child.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node arch = childNodes.item(j);
                    if (arch.getNodeType() == Node.ELEMENT_NODE) {
                        if (arch.getNodeName().equals(P_ARGS_ARCH_X86)) {
                            parentArgs[L_ARGS_ARCH_X86] = getText(arch).trim();
                        } else if (arch.getNodeName().equals(P_ARGS_ARCH_X86_64)) {
                            parentArgs[L_ARGS_ARCH_X86_64] = getText(arch).trim();
                        } else if (arch.getNodeName().equals(P_ARGS_ARCH_PPC)) {
                            parentArgs[L_ARGS_ARCH_PPC] = getText(arch).trim();
                        } else if (arch.getNodeName().equals(P_ARGS_ARCH_IA_64)) {
                            parentArgs[L_ARGS_ARCH_IA_64] = getText(arch).trim();
                        } else if (arch.getNodeName().equals(P_ARGS_ARCH_IA_64_32)) {
                            parentArgs[L_ARGS_ARCH_IA_64_32] = getText(arch).trim();
                        } else if (arch.getNodeName().equals(P_ARGS_ARCH_PA_RISC)) {
                            parentArgs[L_ARGS_ARCH_PA_RISC] = getText(arch).trim();
                        } else if (arch.getNodeName().equals(P_ARGS_ARCH_SPARC)) {
                            parentArgs[L_ARGS_ARCH_SPARC] = getText(arch).trim();
                        }
                    }
                }
            }
        }
    }

    private String getText(Node node) {
        node.normalize();
        Node text = node.getFirstChild();
        if (text != null && text.getNodeType() == Node.TEXT_NODE) {
            return text.getNodeValue();
        }
        //$NON-NLS-1$
        return "";
    }

    @Override
    public void write(String indent, java.io.PrintWriter writer) {
        //$NON-NLS-1$
        writer.println(indent + "<launcherArgs>");
        //$NON-NLS-1$
        String subIndent = indent + "   ";
        if (hasArgs(fProgramArgs)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_PROG_ARGS + ">");
            if (fProgramArgs[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fProgramArgs[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fProgramArgs, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_PROG_ARGS + ">");
        }
        if (hasArgs(fProgramArgsLin)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_PROG_ARGS_LIN + ">");
            if (fProgramArgsLin[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fProgramArgsLin[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fProgramArgsLin, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_PROG_ARGS_LIN + ">");
        }
        if (hasArgs(fProgramArgsMac)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_PROG_ARGS_MAC + ">");
            if (fProgramArgsMac[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fProgramArgsMac[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fProgramArgsMac, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_PROG_ARGS_MAC + ">");
        }
        if (hasArgs(fProgramArgsSol)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_PROG_ARGS_SOL + ">");
            if (fProgramArgsSol[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fProgramArgsSol[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fProgramArgsSol, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_PROG_ARGS_SOL + ">");
        }
        if (hasArgs(fProgramArgsWin)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_PROG_ARGS_WIN + ">");
            if (fProgramArgsWin[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fProgramArgsWin[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fProgramArgsWin, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_PROG_ARGS_WIN + ">");
        }
        if (hasArgs(fVMArgs)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_VM_ARGS + ">");
            if (fVMArgs[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fVMArgs[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fVMArgs, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_VM_ARGS + ">");
        }
        if (hasArgs(fVMArgsLin)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_VM_ARGS_LIN + ">");
            if (fVMArgsLin[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fVMArgsLin[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fVMArgsLin, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_VM_ARGS_LIN + ">");
        }
        if (hasArgs(fVMArgsMac)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_VM_ARGS_MAC + ">");
            if (fVMArgsMac[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fVMArgsMac[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fVMArgsMac, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_VM_ARGS_MAC + ">");
        }
        if (hasArgs(fVMArgsSol)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_VM_ARGS_SOL + ">");
            if (fVMArgsSol[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fVMArgsSol[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fVMArgsSol, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_VM_ARGS_SOL + ">");
        }
        if (hasArgs(fVMArgsWin)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(subIndent + "<" + P_VM_ARGS_WIN + ">");
            if (fVMArgsWin[L_ARGS_ARCH_ALL].length() > 0) {
                writer.print(getWritableString(fVMArgsWin[L_ARGS_ARCH_ALL]));
            }
            writer.println();
            writeArchArgs(fVMArgsWin, subIndent, writer);
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(subIndent + "</" + P_VM_ARGS_WIN + ">");
        }
        //$NON-NLS-1$
        writer.println(indent + "</launcherArgs>");
    }

    private boolean hasArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].length() > 0) {
                return true;
            }
        }
        return false;
    }

    private void writeArchArgs(String[] args, String indent, java.io.PrintWriter writer) {
        if (args[L_ARGS_ARCH_X86].length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 		}
            writer.println(indent + "   " + "<" + P_ARGS_ARCH_X86 + ">" + getWritableString(args[L_ARGS_ARCH_X86]) + "</" + P_ARGS_ARCH_X86 + ">");
        }
        if (args[L_ARGS_ARCH_X86_64].length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 		}
            writer.println(indent + "   " + "<" + P_ARGS_ARCH_X86_64 + ">" + getWritableString(args[L_ARGS_ARCH_X86_64]) + "</" + P_ARGS_ARCH_X86_64 + ">");
        }
        if (args[L_ARGS_ARCH_PPC].length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 		}
            writer.println(indent + "   " + "<" + P_ARGS_ARCH_PPC + ">" + getWritableString(args[L_ARGS_ARCH_PPC]) + "</" + P_ARGS_ARCH_PPC + ">");
        }
        if (args[L_ARGS_ARCH_IA_64].length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 		}
            writer.println(indent + "   " + "<" + P_ARGS_ARCH_IA_64 + ">" + getWritableString(args[L_ARGS_ARCH_IA_64]) + "</" + P_ARGS_ARCH_IA_64 + ">");
        }
        if (args[L_ARGS_ARCH_IA_64_32].length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 		}
            writer.println(indent + "   " + "<" + P_ARGS_ARCH_IA_64_32 + ">" + getWritableString(args[L_ARGS_ARCH_IA_64_32]) + "</" + P_ARGS_ARCH_IA_64_32 + ">");
        }
        if (args[L_ARGS_ARCH_PA_RISC].length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 		}
            writer.println(indent + "   " + "<" + P_ARGS_ARCH_PA_RISC + ">" + getWritableString(args[L_ARGS_ARCH_PA_RISC]) + "</" + P_ARGS_ARCH_PA_RISC + ">");
        }
        if (args[L_ARGS_ARCH_SPARC].length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 		}
            writer.println(indent + "   " + "<" + P_ARGS_ARCH_SPARC + ">" + getWritableString(args[L_ARGS_ARCH_SPARC]) + "</" + P_ARGS_ARCH_SPARC + ">");
        }
    }
}

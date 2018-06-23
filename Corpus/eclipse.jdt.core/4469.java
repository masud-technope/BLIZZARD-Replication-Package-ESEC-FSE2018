/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;

public class GenerateBuildScript {

    //$NON-NLS-1$
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String HEADER = //$NON-NLS-1$
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + LINE_SEPARATOR + "<project name=\"export-executable\" default=\"build_executable\">" + //$NON-NLS-1$
    LINE_SEPARATOR + "    <target name=\"build_executable\">" + //$NON-NLS-1$
    LINE_SEPARATOR;

    private static final String SOURCE_FILES = //$NON-NLS-1$
    "	    <echo message=\"compiling sources      -> .o\"/>" + LINE_SEPARATOR + "        <apply failonerror=\"true\" executable=\"$'{'gcc-path'}'/bin/{0}\" dest=\"{1}\" parallel=\"false\">" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"--verbose\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"--classpath={1}\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-O2\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-c\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-fassume-compiled\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-march=pentium4\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-mfpmath=sse\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <srcfile/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-o\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <targetfile/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <fileset dir=\"{1}\" includes=\"**/*.java\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <mapper type=\"glob\" from=\"*.java\" to=\"*.o\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "        </apply>" + LINE_SEPARATOR + //$NON-NLS-1$
    LINE_SEPARATOR;

    private static final String FOOTER = //$NON-NLS-1$
    "        <echo message=\"linking .o -> $'{'binaryname'}'\"/>" + LINE_SEPARATOR + "        <apply failonerror=\"true\" executable=\"$'{'gcc-path'}'/bin/{0}\" parallel=\"true\">" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"--verbose\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg line =\"-o $'{'dest'}'/$'{'binaryname'}'\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-fassume-compiled\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-march=pentium4\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg value=\"-mfpmath=sse\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <arg line=\"--main=org.eclipse.jdt.internal.compiler.batch.Main\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "            <fileset dir=\"{1}\" includes=\"**/*.o\"/>" + //$NON-NLS-1$
    LINE_SEPARATOR + "       </apply>" + //$NON-NLS-1$
    LINE_SEPARATOR + "    </target>" + //$NON-NLS-1$
    LINE_SEPARATOR + "</project>" + //$NON-NLS-1$
    LINE_SEPARATOR;

    private static void collectAllFiles(File root, ArrayList collector, FileFilter fileFilter) {
        File[] files = root.listFiles(fileFilter);
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                collectAllFiles(files[i], collector, fileFilter);
            } else {
                String newElement = files[i].getAbsolutePath();
                newElement = newElement.replace('\\', '/');
                collector.add(newElement);
            }
        }
    }

    private static void dumpAllProperties(Writer writer, File sourceDir, ArrayList collector, String gcj_exe, String dest_dir) throws IOException {
        writer.write("        <echo message=\"compiling resources   -> .o\"/>" + LINE_SEPARATOR);
        for (int i = 0, max = collector.size(); i < max; i++) {
            String absolutePath = (String) collector.get(i);
            String fileName = absolutePath.substring(sourceDir.getAbsolutePath().length() + 1);
            writer.write(MessageFormat.format("  		<exec dir=\"{1}\" executable=\"$'{'gcc-path'}'/bin/{0}\">" + LINE_SEPARATOR, new Object[] { gcj_exe, dest_dir }));
            writer.write("  		<arg line=\"--resource ");
            writer.write(fileName + " " + fileName + " -c -o " + getObjectName(fileName) + "\"/>" + LINE_SEPARATOR);
            writer.write("  		</exec>" + LINE_SEPARATOR);
        }
    }

    private static void dumpAllClassFiles(Writer writer, File sourceDir, ArrayList collector, String gcj_exe, String dest_dir) throws IOException {
        writer.write("        <echo message=\"compiling class files   -> .o\"/>" + LINE_SEPARATOR);
        writer.write(MessageFormat.format("        <apply failonerror=\"true\" executable=\"$'{'gcc-path'}'/bin/{0}\" dest=\"{1}\" parallel=\"false\">" + LINE_SEPARATOR + "  			 <arg value=\"--verbose\"/>" + LINE_SEPARATOR + "            <arg value=\"--classpath={1}\"/>" + LINE_SEPARATOR + "            <arg value=\"-O2\"/>" + LINE_SEPARATOR + "            <arg value=\"-c\"/>" + LINE_SEPARATOR + "            <arg value=\"-fassume-compiled\"/>" + LINE_SEPARATOR + "            <arg value=\"-march=pentium4\"/>" + LINE_SEPARATOR + "            <arg value=\"-mfpmath=sse\"/>" + LINE_SEPARATOR + "            <srcfile/>" + LINE_SEPARATOR + "            <arg value=\"-o\"/>" + LINE_SEPARATOR + "            <targetfile/>" + LINE_SEPARATOR + "            <fileset dir=\"{1}\" includes=\"**/*.class\"/>" + LINE_SEPARATOR + "            <mapper type=\"glob\" from=\"*.class\" to=\"*.o\"/>" + LINE_SEPARATOR + "        </apply>" + LINE_SEPARATOR + LINE_SEPARATOR, new Object[] { gcj_exe, dest_dir }));
    }

    private static String getObjectName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.')) + ".o";
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: script_name directory gcj_exe_name dest_dir source/bin");
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[0])));
            writer.write(HEADER);
            File sourceDir = new File(args[1]);
            if (sourceDir.exists()) {
                ArrayList collector = new ArrayList();
                collectAllFiles(sourceDir, collector, new FileFilter() {

                    public boolean accept(File pathname) {
                        String fileName = pathname.getAbsolutePath();
                        return pathname.isDirectory() || fileName.endsWith(".rsc") || fileName.endsWith(".properties");
                    }
                });
                dumpAllProperties(writer, sourceDir, collector, args[2], args[3]);
                if ("source".equals(args[4])) {
                    writer.write(MessageFormat.format(SOURCE_FILES, new Object[] { args[2], args[3] }));
                } else {
                    collector = new ArrayList();
                    collectAllFiles(sourceDir, collector, new FileFilter() {

                        public boolean accept(File pathname) {
                            String fileName = pathname.getAbsolutePath();
                            return pathname.isDirectory() || fileName.endsWith(".class");
                        }
                    });
                    dumpAllClassFiles(writer, sourceDir, collector, args[2], args[3]);
                }
            }
            writer.write(MessageFormat.format(FOOTER, new Object[] { args[2], args[3] }));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

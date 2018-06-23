/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Tromey - Contribution for bug 125961
 *     Tom Tromey - Contribution for bug 159641
 *     Benjamin Muskalla - Contribution for bug 239066
 *     Stephan Herrmann  - Contributions for 
 *     							bug 236385 - [compiler] Warn for potential programming problem if an object is created but not used
 *     							bug 295551 - Add option to automatically promote all warnings to errors
 *     							bug 359721 - [options] add command line option for new warning token "resource"
 *								bug 365208 - [compiler][batch] command line options for annotation based null analysis
 *								bug 374605 - Unreasonable warning for enum-based switch statements
 *								bug 375366 - ECJ ignores unusedParameterIncludeDocCommentReference unless enableJavadoc option is set
 *								bug 388281 - [compiler][null] inheritance of null annotations as an option
 *								bug 381443 - [compiler][null] Allow parameter widening from @NonNull to unannotated
 *								Bug 440477 - [null] Infrastructure for feeding external annotations into compilation
 *								Bug 440687 - [compiler][batch][null] improve command line option for external annotations
 *								Bug 408815 - [batch][null] Add CLI option for COMPILER_PB_SYNTACTIC_NULL_ANALYSIS_FOR_FIELDS
 *     Jesper S Moller   - Contributions for
 *								bug 407297 - [1.8][compiler] Control generation of parameter names by option
 *    Mat Booth - Contribution for bug 405176 
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.batch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;
import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.AccessRule;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.CompilerStats;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.GenericXMLWriter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Main implements ProblemSeverities, SuffixConstants {

    public static class Logger {

        private PrintWriter err;

        private PrintWriter log;

        private Main main;

        private PrintWriter out;

        private HashMap parameters;

        int tagBits;

        //$NON-NLS-1$
        private static final String CLASS = "class";

        //$NON-NLS-1$
        private static final String CLASS_FILE = "classfile";

        //$NON-NLS-1$
        private static final String CLASSPATH = "classpath";

        //$NON-NLS-1$
        private static final String CLASSPATH_FILE = "FILE";

        //$NON-NLS-1$
        private static final String CLASSPATH_FOLDER = "FOLDER";

        //$NON-NLS-1$
        private static final String CLASSPATH_ID = "id";

        //$NON-NLS-1$
        private static final String CLASSPATH_JAR = "JAR";

        //$NON-NLS-1$
        private static final String CLASSPATHS = "classpaths";

        //$NON-NLS-1$
        private static final String COMMAND_LINE_ARGUMENT = "argument";

        //$NON-NLS-1$
        private static final String COMMAND_LINE_ARGUMENTS = "command_line";

        //$NON-NLS-1$
        private static final String COMPILER = "compiler";

        //$NON-NLS-1$
        private static final String COMPILER_COPYRIGHT = "copyright";

        //$NON-NLS-1$
        private static final String COMPILER_NAME = "name";

        //$NON-NLS-1$
        private static final String COMPILER_VERSION = "version";

        public static final int EMACS = 2;

        //$NON-NLS-1$
        private static final String ERROR = "ERROR";

        //$NON-NLS-1$
        private static final String ERROR_TAG = "error";

        //$NON-NLS-1$
        private static final String WARNING_TAG = "warning";

        //$NON-NLS-1$
        private static final String EXCEPTION = "exception";

        //$NON-NLS-1$
        private static final String EXTRA_PROBLEM_TAG = "extra_problem";

        //$NON-NLS-1$
        private static final String EXTRA_PROBLEMS = "extra_problems";

        private static final HashtableOfInt FIELD_TABLE = new HashtableOfInt();

        //$NON-NLS-1$
        private static final String KEY = "key";

        //$NON-NLS-1$
        private static final String MESSAGE = "message";

        //$NON-NLS-1$
        private static final String NUMBER_OF_CLASSFILES = "number_of_classfiles";

        //$NON-NLS-1$
        private static final String NUMBER_OF_ERRORS = "errors";

        //$NON-NLS-1$
        private static final String NUMBER_OF_LINES = "number_of_lines";

        //$NON-NLS-1$
        private static final String NUMBER_OF_PROBLEMS = "problems";

        //$NON-NLS-1$
        private static final String NUMBER_OF_TASKS = "tasks";

        //$NON-NLS-1$
        private static final String NUMBER_OF_WARNINGS = "warnings";

        //$NON-NLS-1$
        private static final String OPTION = "option";

        //$NON-NLS-1$
        private static final String OPTIONS = "options";

        //$NON-NLS-1$
        private static final String OUTPUT = "output";

        //$NON-NLS-1$
        private static final String PACKAGE = "package";

        //$NON-NLS-1$
        private static final String PATH = "path";

        //$NON-NLS-1$
        private static final String PROBLEM_ARGUMENT = "argument";

        //$NON-NLS-1$
        private static final String PROBLEM_ARGUMENT_VALUE = "value";

        //$NON-NLS-1$
        private static final String PROBLEM_ARGUMENTS = "arguments";

        //$NON-NLS-1$
        private static final String PROBLEM_CATEGORY_ID = "categoryID";

        //$NON-NLS-1$
        private static final String ID = "id";

        //$NON-NLS-1$
        private static final String PROBLEM_ID = "problemID";

        //$NON-NLS-1$
        private static final String PROBLEM_LINE = "line";

        //$NON-NLS-1$
        private static final String PROBLEM_OPTION_KEY = "optionKey";

        //$NON-NLS-1$
        private static final String PROBLEM_MESSAGE = "message";

        //$NON-NLS-1$
        private static final String PROBLEM_SEVERITY = "severity";

        //$NON-NLS-1$
        private static final String PROBLEM_SOURCE_END = "charEnd";

        //$NON-NLS-1$
        private static final String PROBLEM_SOURCE_START = "charStart";

        //$NON-NLS-1$
        private static final String PROBLEM_SUMMARY = "problem_summary";

        //$NON-NLS-1$
        private static final String PROBLEM_TAG = "problem";

        //$NON-NLS-1$
        private static final String PROBLEMS = "problems";

        //$NON-NLS-1$
        private static final String SOURCE = "source";

        //$NON-NLS-1$
        private static final String SOURCE_CONTEXT = "source_context";

        //$NON-NLS-1$
        private static final String SOURCE_END = "sourceEnd";

        //$NON-NLS-1$
        private static final String SOURCE_START = "sourceStart";

        //$NON-NLS-1$
        private static final String SOURCES = "sources";

        //$NON-NLS-1$
        private static final String STATS = "stats";

        //$NON-NLS-1$
        private static final String TASK = "task";

        //$NON-NLS-1$
        private static final String TASKS = "tasks";

        //$NON-NLS-1$
        private static final String TIME = "time";

        //$NON-NLS-1$
        private static final String VALUE = "value";

        //$NON-NLS-1$
        private static final String WARNING = "WARNING";

        public static final int XML = 1;

        //$NON-NLS-1$
        private static final String XML_DTD_DECLARATION = "<!DOCTYPE compiler PUBLIC \"-//Eclipse.org//DTD Eclipse JDT 3.2.004 Compiler//EN\" \"http://www.eclipse.org/jdt/core/compiler_32_004.dtd\">";

        static {
            try {
                Class c = IProblem.class;
                Field[] fields = c.getFields();
                for (int i = 0, max = fields.length; i < max; i++) {
                    Field field = fields[i];
                    if (field.getType().equals(Integer.TYPE)) {
                        Integer value = (Integer) field.get(null);
                        int key2 = value.intValue() & IProblem.IgnoreCategoriesMask;
                        if (key2 == 0) {
                            key2 = Integer.MAX_VALUE;
                        }
                        Logger.FIELD_TABLE.put(key2, field.getName());
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public  Logger(Main main, PrintWriter out, PrintWriter err) {
            this.out = out;
            this.err = err;
            this.parameters = new HashMap();
            this.main = main;
        }

        public String buildFileName(String outputPath, String relativeFileName) {
            char fileSeparatorChar = File.separatorChar;
            String fileSeparator = File.separator;
            outputPath = outputPath.replace('/', fileSeparatorChar);
            // To be able to pass the mkdirs() method we need to remove the extra file separator at the end of the outDir name
            StringBuffer outDir = new StringBuffer(outputPath);
            if (!outputPath.endsWith(fileSeparator)) {
                outDir.append(fileSeparator);
            }
            StringTokenizer tokenizer = new StringTokenizer(relativeFileName, fileSeparator);
            String token = tokenizer.nextToken();
            while (tokenizer.hasMoreTokens()) {
                outDir.append(token).append(fileSeparator);
                token = tokenizer.nextToken();
            }
            // token contains the last one
            return outDir.append(token).toString();
        }

        public void close() {
            if (this.log != null) {
                if ((this.tagBits & Logger.XML) != 0) {
                    endTag(Logger.COMPILER);
                    flush();
                }
                this.log.close();
            }
        }

        /**
		 *
		 */
        public void compiling() {
            //$NON-NLS-1$
            printlnOut(this.main.bind("progress.compiling"));
        }

        private void endLoggingExtraProblems() {
            endTag(Logger.EXTRA_PROBLEMS);
        }

        /**
		 * Used to stop logging problems.
		 * Only use in xml mode.
		 */
        private void endLoggingProblems() {
            endTag(Logger.PROBLEMS);
        }

        public void endLoggingSource() {
            if ((this.tagBits & Logger.XML) != 0) {
                endTag(Logger.SOURCE);
            }
        }

        public void endLoggingSources() {
            if ((this.tagBits & Logger.XML) != 0) {
                endTag(Logger.SOURCES);
            }
        }

        public void endLoggingTasks() {
            if ((this.tagBits & Logger.XML) != 0) {
                endTag(Logger.TASKS);
            }
        }

        private void endTag(String name) {
            if (this.log != null) {
                ((GenericXMLWriter) this.log).endTag(name, true, true);
            }
        }

        private String errorReportSource(CategorizedProblem problem, char[] unitSource, int bits) {
            //extra from the source the innacurate     token
            //and "highlight" it using some underneath ^^^^^
            //put some context around too.
            //this code assumes that the font used in the console is fixed size
            //sanity .....
            int startPosition = problem.getSourceStart();
            int endPosition = problem.getSourceEnd();
            if (unitSource == null) {
                if (problem.getOriginatingFileName() != null) {
                    try {
                        unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
                    } catch (IOException e) {
                    }
                }
            }
            int length;
            if ((startPosition > endPosition) || ((startPosition < 0) && (endPosition < 0)) || (unitSource == null) || (length = unitSource.length) == 0)
                return Messages.problem_noSourceInformation;
            StringBuffer errorBuffer = new StringBuffer();
            if ((bits & Main.Logger.EMACS) == 0) {
                errorBuffer.append(' ').append(Messages.bind(Messages.problem_atLine, String.valueOf(problem.getSourceLineNumber())));
                errorBuffer.append(Util.LINE_SEPARATOR);
            }
            errorBuffer.append('\t');
            char c;
            final char SPACE = ' ';
            final char MARK = '^';
            final char TAB = '\t';
            //the next code tries to underline the token.....
            //it assumes (for a good display) that token source does not
            //contain any \r \n. This is false on statements !
            //(the code still works but the display is not optimal !)
            // expand to line limits
            int begin;
            int end;
            for (begin = startPosition >= length ? length - 1 : startPosition; begin > 0; begin--) {
                if ((c = unitSource[begin - 1]) == '\n' || c == '\r')
                    break;
            }
            for (end = endPosition >= length ? length - 1 : endPosition; end + 1 < length; end++) {
                if ((c = unitSource[end + 1]) == '\r' || c == '\n')
                    break;
            }
            // trim left and right spaces/tabs
            while ((c = unitSource[begin]) == ' ' || c == '\t') begin++;
            //while ((c = unitSource[end]) == ' ' || c == '\t') end--; TODO (philippe) should also trim right, but all tests are to be updated
            // copy source
            errorBuffer.append(unitSource, begin, end - begin + 1);
            //$NON-NLS-1$
            errorBuffer.append(Util.LINE_SEPARATOR).append("\t");
            // compute underline
            for (int i = begin; i < startPosition; i++) {
                errorBuffer.append((unitSource[i] == TAB) ? TAB : SPACE);
            }
            for (int i = startPosition; i <= (endPosition >= length ? length - 1 : endPosition); i++) {
                errorBuffer.append(MARK);
            }
            return errorBuffer.toString();
        }

        private void extractContext(CategorizedProblem problem, char[] unitSource) {
            //sanity .....
            int startPosition = problem.getSourceStart();
            int endPosition = problem.getSourceEnd();
            if (unitSource == null) {
                if (problem.getOriginatingFileName() != null) {
                    try {
                        unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
                    } catch (IOException e) {
                    }
                }
            }
            int length;
            if ((startPosition > endPosition) || ((startPosition < 0) && (endPosition < 0)) || (unitSource == null) || ((length = unitSource.length) <= 0) || (endPosition > length)) {
                this.parameters.put(Logger.VALUE, Messages.problem_noSourceInformation);
                //$NON-NLS-1$
                this.parameters.put(//$NON-NLS-1$
                Logger.SOURCE_START, //$NON-NLS-1$
                "-1");
                //$NON-NLS-1$
                this.parameters.put(//$NON-NLS-1$
                Logger.SOURCE_END, //$NON-NLS-1$
                "-1");
                printTag(Logger.SOURCE_CONTEXT, this.parameters, true, true);
                return;
            }
            char c;
            //the next code tries to underline the token.....
            //it assumes (for a good display) that token source does not
            //contain any \r \n. This is false on statements !
            //(the code still works but the display is not optimal !)
            // expand to line limits
            int begin, end;
            for (begin = startPosition >= length ? length - 1 : startPosition; begin > 0; begin--) {
                if ((c = unitSource[begin - 1]) == '\n' || c == '\r')
                    break;
            }
            for (end = endPosition >= length ? length - 1 : endPosition; end + 1 < length; end++) {
                if ((c = unitSource[end + 1]) == '\r' || c == '\n')
                    break;
            }
            // trim left and right spaces/tabs
            while ((c = unitSource[begin]) == ' ' || c == '\t') begin++;
            while ((c = unitSource[end]) == ' ' || c == '\t') end--;
            // copy source
            StringBuffer buffer = new StringBuffer();
            buffer.append(unitSource, begin, end - begin + 1);
            this.parameters.put(Logger.VALUE, String.valueOf(buffer));
            this.parameters.put(Logger.SOURCE_START, Integer.toString(startPosition - begin));
            this.parameters.put(Logger.SOURCE_END, Integer.toString(endPosition - begin));
            printTag(Logger.SOURCE_CONTEXT, this.parameters, true, true);
        }

        public void flush() {
            this.out.flush();
            this.err.flush();
            if (this.log != null) {
                this.log.flush();
            }
        }

        private String getFieldName(int id) {
            int key2 = id & IProblem.IgnoreCategoriesMask;
            if (key2 == 0) {
                key2 = Integer.MAX_VALUE;
            }
            return (String) Logger.FIELD_TABLE.get(key2);
        }

        // find out an option name controlling a given problemID
        private String getProblemOptionKey(int problemID) {
            int irritant = ProblemReporter.getIrritant(problemID);
            return CompilerOptions.optionKeyFromIrritant(irritant);
        }

        public void logAverage() {
            Arrays.sort(this.main.compilerStats);
            long lineCount = this.main.compilerStats[0].lineCount;
            final int length = this.main.maxRepetition;
            long sum = 0;
            long parseSum = 0, resolveSum = 0, analyzeSum = 0, generateSum = 0;
            for (int i = 1, max = length - 1; i < max; i++) {
                CompilerStats stats = this.main.compilerStats[i];
                sum += stats.elapsedTime();
                parseSum += stats.parseTime;
                resolveSum += stats.resolveTime;
                analyzeSum += stats.analyzeTime;
                generateSum += stats.generateTime;
            }
            long time = sum / (length - 2);
            long parseTime = parseSum / (length - 2);
            long resolveTime = resolveSum / (length - 2);
            long analyzeTime = analyzeSum / (length - 2);
            long generateTime = generateSum / (length - 2);
            printlnOut(this.main.bind("compile.averageTime", new String[] { String.valueOf(lineCount), String.valueOf(time), String.valueOf(((int) (lineCount * 10000.0 / time)) / 10.0) }));
            if ((this.main.timing & Main.TIMING_DETAILED) != 0) {
                printlnOut(this.main.bind("compile.detailedTime", new String[] { String.valueOf(parseTime), String.valueOf(((int) (parseTime * 1000.0 / time)) / 10.0), String.valueOf(resolveTime), String.valueOf(((int) (resolveTime * 1000.0 / time)) / 10.0), String.valueOf(analyzeTime), String.valueOf(((int) (analyzeTime * 1000.0 / time)) / 10.0), String.valueOf(generateTime), String.valueOf(((int) (generateTime * 1000.0 / time)) / 10.0) }));
            }
        }

        public void logClassFile(boolean generatePackagesStructure, String outputPath, String relativeFileName) {
            if ((this.tagBits & Logger.XML) != 0) {
                String fileName = null;
                if (generatePackagesStructure) {
                    fileName = buildFileName(outputPath, relativeFileName);
                } else {
                    char fileSeparatorChar = File.separatorChar;
                    String fileSeparator = File.separator;
                    // First we ensure that the outputPath exists
                    outputPath = outputPath.replace('/', fileSeparatorChar);
                    // To be able to pass the mkdirs() method we need to remove the extra file separator at the end of the outDir name
                    int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
                    if (indexOfPackageSeparator == -1) {
                        if (outputPath.endsWith(fileSeparator)) {
                            fileName = outputPath + relativeFileName;
                        } else {
                            fileName = outputPath + fileSeparator + relativeFileName;
                        }
                    } else {
                        int length = relativeFileName.length();
                        if (outputPath.endsWith(fileSeparator)) {
                            fileName = outputPath + relativeFileName.substring(indexOfPackageSeparator + 1, length);
                        } else {
                            fileName = outputPath + fileSeparator + relativeFileName.substring(indexOfPackageSeparator + 1, length);
                        }
                    }
                }
                File f = new File(fileName);
                try {
                    this.parameters.put(Logger.PATH, f.getCanonicalPath());
                    printTag(Logger.CLASS_FILE, this.parameters, true, true);
                } catch (IOException e) {
                    logNoClassFileCreated(outputPath, relativeFileName, e);
                }
            }
        }

        public void logClasspath(FileSystem.Classpath[] classpaths) {
            if (classpaths == null)
                return;
            if ((this.tagBits & Logger.XML) != 0) {
                final int length = classpaths.length;
                if (length != 0) {
                    // generate xml output
                    printTag(Logger.CLASSPATHS, null, true, false);
                    for (int i = 0; i < length; i++) {
                        String classpath = classpaths[i].getPath();
                        this.parameters.put(Logger.PATH, classpath);
                        File f = new File(classpath);
                        String id = null;
                        if (f.isFile()) {
                            if (Util.isPotentialZipArchive(classpath)) {
                                id = Logger.CLASSPATH_JAR;
                            } else {
                                id = Logger.CLASSPATH_FILE;
                            }
                        } else if (f.isDirectory()) {
                            id = Logger.CLASSPATH_FOLDER;
                        }
                        if (id != null) {
                            this.parameters.put(Logger.CLASSPATH_ID, id);
                            printTag(Logger.CLASSPATH, this.parameters, true, true);
                        }
                    }
                    endTag(Logger.CLASSPATHS);
                }
            }
        }

        public void logCommandLineArguments(String[] commandLineArguments) {
            if (commandLineArguments == null)
                return;
            if ((this.tagBits & Logger.XML) != 0) {
                final int length = commandLineArguments.length;
                if (length != 0) {
                    // generate xml output
                    printTag(Logger.COMMAND_LINE_ARGUMENTS, null, true, false);
                    for (int i = 0; i < length; i++) {
                        this.parameters.put(Logger.VALUE, commandLineArguments[i]);
                        printTag(Logger.COMMAND_LINE_ARGUMENT, this.parameters, true, true);
                    }
                    endTag(Logger.COMMAND_LINE_ARGUMENTS);
                }
            }
        }

        /**
		 * @param e the given exception to log
		 */
        public void logException(Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            printWriter.flush();
            printWriter.close();
            final String stackTrace = writer.toString();
            if ((this.tagBits & Logger.XML) != 0) {
                LineNumberReader reader = new LineNumberReader(new StringReader(stackTrace));
                String line;
                int i = 0;
                StringBuffer buffer = new StringBuffer();
                String message = e.getMessage();
                if (message != null) {
                    buffer.append(message).append(Util.LINE_SEPARATOR);
                }
                try {
                    while ((line = reader.readLine()) != null && i < 4) {
                        buffer.append(line).append(Util.LINE_SEPARATOR);
                        i++;
                    }
                    reader.close();
                } catch (IOException e1) {
                }
                message = buffer.toString();
                this.parameters.put(Logger.MESSAGE, message);
                this.parameters.put(Logger.CLASS, e.getClass());
                printTag(Logger.EXCEPTION, this.parameters, true, true);
            }
            String message = e.getMessage();
            if (message == null) {
                this.printlnErr(stackTrace);
            } else {
                this.printlnErr(message);
            }
        }

        private void logExtraProblem(CategorizedProblem problem, int localErrorCount, int globalErrorCount) {
            char[] originatingFileName = problem.getOriginatingFileName();
            if (originatingFileName == null) {
                // simplified message output
                if (problem.isError()) {
                    printErr(this.main.bind(//$NON-NLS-1$
                    "requestor.extraerror", Integer.toString(globalErrorCount)));
                } else {
                    // warning / mandatory warning / other
                    printErr(this.main.bind(//$NON-NLS-1$
                    "requestor.extrawarning", Integer.toString(globalErrorCount)));
                }
                //$NON-NLS-1$
                printErr(" ");
                this.printlnErr(problem.getMessage());
            } else {
                String fileName = new String(originatingFileName);
                if ((this.tagBits & Logger.EMACS) != 0) {
                    String result = fileName + //$NON-NLS-1$
                    ":" + problem.getSourceLineNumber() + //$NON-NLS-1$
                    ": " + //$NON-NLS-1$ //$NON-NLS-2$
                    (problem.isError() ? this.main.bind("output.emacs.error") : this.main.bind("output.emacs.warning")) + //$NON-NLS-1$
                    ": " + problem.getMessage();
                    this.printlnErr(result);
                    final String errorReportSource = errorReportSource(problem, null, this.tagBits);
                    this.printlnErr(errorReportSource);
                } else {
                    if (localErrorCount == 0) {
                        //$NON-NLS-1$
                        this.printlnErr(//$NON-NLS-1$
                        "----------");
                    }
                    printErr(problem.isError() ? this.main.bind(//$NON-NLS-1$
                    "requestor.error", Integer.toString(globalErrorCount), new String(fileName)) : this.main.bind(//$NON-NLS-1$
                    "requestor.warning", Integer.toString(globalErrorCount), new String(fileName)));
                    final String errorReportSource = errorReportSource(problem, null, 0);
                    this.printlnErr(errorReportSource);
                    this.printlnErr(problem.getMessage());
                    //$NON-NLS-1$
                    this.printlnErr(//$NON-NLS-1$
                    "----------");
                }
            }
        }

        public void loggingExtraProblems(Main currentMain) {
            ArrayList problems = currentMain.extraProblems;
            final int count = problems.size();
            int localProblemCount = 0;
            if (count != 0) {
                int errors = 0;
                int warnings = 0;
                for (int i = 0; i < count; i++) {
                    CategorizedProblem problem = (CategorizedProblem) problems.get(i);
                    if (problem != null) {
                        currentMain.globalProblemsCount++;
                        logExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
                        localProblemCount++;
                        if (problem.isError()) {
                            errors++;
                            currentMain.globalErrorsCount++;
                        } else if (problem.isWarning()) {
                            currentMain.globalWarningsCount++;
                            warnings++;
                        }
                    }
                }
                if ((this.tagBits & Logger.XML) != 0) {
                    if ((errors + warnings) != 0) {
                        startLoggingExtraProblems(count);
                        for (int i = 0; i < count; i++) {
                            CategorizedProblem problem = (CategorizedProblem) problems.get(i);
                            if (problem != null) {
                                if (problem.getID() != IProblem.Task) {
                                    logXmlExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
                                }
                            }
                        }
                        endLoggingExtraProblems();
                    }
                }
            }
        }

        public void logUnavaibleAPT(String className) {
            if ((this.tagBits & Logger.XML) != 0) {
                //$NON-NLS-1$
                this.parameters.put(//$NON-NLS-1$
                Logger.MESSAGE, //$NON-NLS-1$
                this.main.bind("configure.unavailableAPT", className));
                printTag(Logger.ERROR_TAG, this.parameters, true, true);
            }
            //$NON-NLS-1$
            this.printlnErr(this.main.bind("configure.unavailableAPT", className));
        }

        public void logIncorrectVMVersionForAnnotationProcessing() {
            if ((this.tagBits & Logger.XML) != 0) {
                //$NON-NLS-1$
                this.parameters.put(//$NON-NLS-1$
                Logger.MESSAGE, //$NON-NLS-1$
                this.main.bind("configure.incorrectVMVersionforAPT"));
                printTag(Logger.ERROR_TAG, this.parameters, true, true);
            }
            //$NON-NLS-1$
            this.printlnErr(this.main.bind("configure.incorrectVMVersionforAPT"));
        }

        /**
		 *
		 */
        public void logNoClassFileCreated(String outputDir, String relativeFileName, IOException e) {
            if ((this.tagBits & Logger.XML) != 0) {
                this.parameters.put(Logger.MESSAGE, this.main.bind("output.noClassFileCreated", new String[] { outputDir, relativeFileName, e.getMessage() }));
                printTag(Logger.ERROR_TAG, this.parameters, true, true);
            }
            this.printlnErr(//$NON-NLS-1$
            this.main.bind(//$NON-NLS-1$
            "output.noClassFileCreated", new String[] { outputDir, relativeFileName, e.getMessage() }));
        }

        /**
		 * @param exportedClassFilesCounter
		 */
        public void logNumberOfClassFilesGenerated(int exportedClassFilesCounter) {
            if ((this.tagBits & Logger.XML) != 0) {
                this.parameters.put(Logger.VALUE, Integer.valueOf(exportedClassFilesCounter));
                printTag(Logger.NUMBER_OF_CLASSFILES, this.parameters, true, true);
            }
            if (exportedClassFilesCounter == 1) {
                printlnOut(//$NON-NLS-1$
                this.main.bind("compile.oneClassFileGenerated"));
            } else {
                printlnOut(this.main.bind("compile.severalClassFilesGenerated", String.valueOf(exportedClassFilesCounter)));
            }
        }

        /**
		 * @param options the given compiler options
		 */
        public void logOptions(Map<String, String> options) {
            if ((this.tagBits & Logger.XML) != 0) {
                printTag(Logger.OPTIONS, null, true, false);
                final Set<Map.Entry<String, String>> entriesSet = options.entrySet();
                Map.Entry<String, String>[] entries = entriesSet.toArray(new Map.Entry[entriesSet.size()]);
                Arrays.sort(entries, new Comparator<Map.Entry<String, String>>() {

                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                        Map.Entry<String, String> entry1 = o1;
                        Map.Entry<String, String> entry2 = o2;
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                });
                for (int i = 0, max = entries.length; i < max; i++) {
                    Map.Entry<String, String> entry = entries[i];
                    String key = entry.getKey();
                    this.parameters.put(Logger.KEY, key);
                    this.parameters.put(Logger.VALUE, entry.getValue());
                    printTag(Logger.OPTION, this.parameters, true, true);
                }
                endTag(Logger.OPTIONS);
            }
        }

        /**
		 * @param error the given error
		 */
        public void logPendingError(String error) {
            if ((this.tagBits & Logger.XML) != 0) {
                this.parameters.put(Logger.MESSAGE, error);
                printTag(Logger.ERROR_TAG, this.parameters, true, true);
            }
            this.printlnErr(error);
        }

        /**
		 * @param message the given message
		 */
        public void logWarning(String message) {
            if ((this.tagBits & Logger.XML) != 0) {
                this.parameters.put(Logger.MESSAGE, message);
                printTag(Logger.WARNING_TAG, this.parameters, true, true);
            }
            this.printlnOut(message);
        }

        private void logProblem(CategorizedProblem problem, int localErrorCount, int globalErrorCount, char[] unitSource) {
            if ((this.tagBits & Logger.EMACS) != 0) {
                String result = (new String(problem.getOriginatingFileName()) + //$NON-NLS-1$
                ":" + problem.getSourceLineNumber() + //$NON-NLS-1$
                ": " + //$NON-NLS-1$ //$NON-NLS-2$
                (problem.isError() ? this.main.bind("output.emacs.error") : this.main.bind("output.emacs.warning")) + //$NON-NLS-1$
                ": " + problem.getMessage());
                this.printlnErr(result);
                final String errorReportSource = errorReportSource(problem, unitSource, this.tagBits);
                if (errorReportSource.length() != 0)
                    this.printlnErr(errorReportSource);
            } else {
                if (localErrorCount == 0) {
                    //$NON-NLS-1$
                    this.printlnErr(//$NON-NLS-1$
                    "----------");
                }
                printErr(problem.isError() ? this.main.bind(//$NON-NLS-1$
                "requestor.error", Integer.toString(globalErrorCount), new String(problem.getOriginatingFileName())) : this.main.bind(//$NON-NLS-1$
                "requestor.warning", Integer.toString(globalErrorCount), new String(problem.getOriginatingFileName())));
                try {
                    final String errorReportSource = errorReportSource(problem, unitSource, 0);
                    this.printlnErr(errorReportSource);
                    this.printlnErr(problem.getMessage());
                } catch (Exception e) {
                    this.printlnErr(this.main.bind("requestor.notRetrieveErrorMessage", problem.toString()));
                }
                //$NON-NLS-1$
                this.printlnErr(//$NON-NLS-1$
                "----------");
            }
        }

        public int logProblems(CategorizedProblem[] problems, char[] unitSource, Main currentMain) {
            final int count = problems.length;
            int localErrorCount = 0;
            int localProblemCount = 0;
            if (count != 0) {
                int errors = 0;
                int warnings = 0;
                int tasks = 0;
                for (int i = 0; i < count; i++) {
                    CategorizedProblem problem = problems[i];
                    if (problem != null) {
                        currentMain.globalProblemsCount++;
                        logProblem(problem, localProblemCount, currentMain.globalProblemsCount, unitSource);
                        localProblemCount++;
                        if (problem.isError()) {
                            localErrorCount++;
                            errors++;
                            currentMain.globalErrorsCount++;
                        } else if (problem.getID() == IProblem.Task) {
                            currentMain.globalTasksCount++;
                            tasks++;
                        } else {
                            currentMain.globalWarningsCount++;
                            warnings++;
                        }
                    }
                }
                if ((this.tagBits & Logger.XML) != 0) {
                    if ((errors + warnings) != 0) {
                        startLoggingProblems(errors, warnings);
                        for (int i = 0; i < count; i++) {
                            CategorizedProblem problem = problems[i];
                            if (problem != null) {
                                if (problem.getID() != IProblem.Task) {
                                    logXmlProblem(problem, unitSource);
                                }
                            }
                        }
                        endLoggingProblems();
                    }
                    if (tasks != 0) {
                        startLoggingTasks(tasks);
                        for (int i = 0; i < count; i++) {
                            CategorizedProblem problem = problems[i];
                            if (problem != null) {
                                if (problem.getID() == IProblem.Task) {
                                    logXmlTask(problem, unitSource);
                                }
                            }
                        }
                        endLoggingTasks();
                    }
                }
            }
            return localErrorCount;
        }

        /**
		 * @param globalProblemsCount
		 * @param globalErrorsCount
		 * @param globalWarningsCount
		 */
        public void logProblemsSummary(int globalProblemsCount, int globalErrorsCount, int globalWarningsCount, int globalTasksCount) {
            if ((this.tagBits & Logger.XML) != 0) {
                // generate xml
                this.parameters.put(Logger.NUMBER_OF_PROBLEMS, Integer.valueOf(globalProblemsCount));
                this.parameters.put(Logger.NUMBER_OF_ERRORS, Integer.valueOf(globalErrorsCount));
                this.parameters.put(Logger.NUMBER_OF_WARNINGS, Integer.valueOf(globalWarningsCount));
                this.parameters.put(Logger.NUMBER_OF_TASKS, Integer.valueOf(globalTasksCount));
                printTag(Logger.PROBLEM_SUMMARY, this.parameters, true, true);
            }
            if (globalProblemsCount == 1) {
                String message = null;
                if (globalErrorsCount == 1) {
                    message = this.main.bind("compile.oneError");
                } else {
                    message = this.main.bind("compile.oneWarning");
                }
                printErr(//$NON-NLS-1$
                this.main.bind("compile.oneProblem", message));
            } else {
                String errorMessage = null;
                String warningMessage = null;
                if (globalErrorsCount > 0) {
                    if (globalErrorsCount == 1) {
                        errorMessage = //$NON-NLS-1$
                        this.main.bind(//$NON-NLS-1$
                        "compile.oneError");
                    } else {
                        errorMessage = this.main.bind("compile.severalErrors", //$NON-NLS-1$
                        String.valueOf(//$NON-NLS-1$
                        globalErrorsCount));
                    }
                }
                int warningsNumber = globalWarningsCount + globalTasksCount;
                if (warningsNumber > 0) {
                    if (warningsNumber == 1) {
                        warningMessage = //$NON-NLS-1$
                        this.main.bind(//$NON-NLS-1$
                        "compile.oneWarning");
                    } else {
                        warningMessage = this.main.bind("compile.severalWarnings", //$NON-NLS-1$
                        String.valueOf(//$NON-NLS-1$
                        warningsNumber));
                    }
                }
                if (errorMessage == null || warningMessage == null) {
                    if (errorMessage == null) {
                        printErr(this.main.bind("compile.severalProblemsErrorsOrWarnings", String.valueOf(globalProblemsCount), warningMessage));
                    } else {
                        printErr(this.main.bind("compile.severalProblemsErrorsOrWarnings", String.valueOf(globalProblemsCount), errorMessage));
                    }
                } else {
                    printErr(this.main.bind("compile.severalProblemsErrorsAndWarnings", new String[] { String.valueOf(globalProblemsCount), errorMessage, warningMessage }));
                }
            }
            if ((this.tagBits & Logger.XML) == 0) {
                this.printlnErr();
            }
        }

        /**
		 *
		 */
        public void logProgress() {
            printOut('.');
        }

        /**
		 * @param i
		 *            the current repetition number
		 * @param repetitions
		 *            the given number of repetitions
		 */
        public void logRepetition(int i, int repetitions) {
            printlnOut(//$NON-NLS-1$
            this.main.bind(//$NON-NLS-1$
            "compile.repetition", String.valueOf(i + 1), String.valueOf(repetitions)));
        }

        /**
		 * @param compilerStats
		 */
        public void logTiming(CompilerStats compilerStats) {
            long time = compilerStats.elapsedTime();
            long lineCount = compilerStats.lineCount;
            if ((this.tagBits & Logger.XML) != 0) {
                this.parameters.put(Logger.VALUE, Long.valueOf(time));
                printTag(Logger.TIME, this.parameters, true, true);
                this.parameters.put(Logger.VALUE, Long.valueOf(lineCount));
                printTag(Logger.NUMBER_OF_LINES, this.parameters, true, true);
            }
            if (lineCount != 0) {
                printlnOut(this.main.bind("compile.instantTime", new String[] { String.valueOf(lineCount), String.valueOf(time), String.valueOf(((int) (lineCount * 10000.0 / time)) / 10.0) }));
            } else {
                printlnOut(this.main.bind("compile.totalTime", new String[] { String.valueOf(time) }));
            }
            if ((this.main.timing & Main.TIMING_DETAILED) != 0) {
                printlnOut(this.main.bind("compile.detailedTime", new String[] { String.valueOf(compilerStats.parseTime), String.valueOf(((int) (compilerStats.parseTime * 1000.0 / time)) / 10.0), String.valueOf(compilerStats.resolveTime), String.valueOf(((int) (compilerStats.resolveTime * 1000.0 / time)) / 10.0), String.valueOf(compilerStats.analyzeTime), String.valueOf(((int) (compilerStats.analyzeTime * 1000.0 / time)) / 10.0), String.valueOf(compilerStats.generateTime), String.valueOf(((int) (compilerStats.generateTime * 1000.0 / time)) / 10.0) }));
            }
        }

        /**
		 * Print the usage of the compiler
		 * @param usage
		 */
        public void logUsage(String usage) {
            printlnOut(usage);
        }

        /**
		 * Print the version of the compiler in the log and/or the out field
		 */
        public void logVersion(final boolean printToOut) {
            if (this.log != null && (this.tagBits & Logger.XML) == 0) {
                final String version = this.main.bind("misc.version", new String[] { //$NON-NLS-1$
                this.main.bind(//$NON-NLS-1$
                "compiler.name"), //$NON-NLS-1$
                this.main.bind(//$NON-NLS-1$
                "compiler.version"), //$NON-NLS-1$
                this.main.bind(//$NON-NLS-1$
                "compiler.copyright") });
                //$NON-NLS-1$
                this.log.println(//$NON-NLS-1$
                "# " + version);
                if (printToOut) {
                    this.out.println(version);
                    this.out.flush();
                }
            } else if (printToOut) {
                final String version = this.main.bind("misc.version", new String[] { //$NON-NLS-1$
                this.main.bind(//$NON-NLS-1$
                "compiler.name"), //$NON-NLS-1$
                this.main.bind(//$NON-NLS-1$
                "compiler.version"), //$NON-NLS-1$
                this.main.bind(//$NON-NLS-1$
                "compiler.copyright") });
                this.out.println(version);
                this.out.flush();
            }
        }

        /**
		 * Print the usage of wrong JDK
		 */
        public void logWrongJDK() {
            if ((this.tagBits & Logger.XML) != 0) {
                //$NON-NLS-1$
                this.parameters.put(//$NON-NLS-1$
                Logger.MESSAGE, //$NON-NLS-1$
                this.main.bind("configure.requiresJDK1.2orAbove"));
                printTag(Logger.ERROR, this.parameters, true, true);
            }
            //$NON-NLS-1$
            this.printlnErr(this.main.bind("configure.requiresJDK1.2orAbove"));
        }

        private void logXmlExtraProblem(CategorizedProblem problem, int globalErrorCount, int localErrorCount) {
            final int sourceStart = problem.getSourceStart();
            final int sourceEnd = problem.getSourceEnd();
            boolean isError = problem.isError();
            this.parameters.put(Logger.PROBLEM_SEVERITY, isError ? Logger.ERROR : Logger.WARNING);
            this.parameters.put(Logger.PROBLEM_LINE, Integer.valueOf(problem.getSourceLineNumber()));
            this.parameters.put(Logger.PROBLEM_SOURCE_START, Integer.valueOf(sourceStart));
            this.parameters.put(Logger.PROBLEM_SOURCE_END, Integer.valueOf(sourceEnd));
            printTag(Logger.EXTRA_PROBLEM_TAG, this.parameters, true, false);
            this.parameters.put(Logger.VALUE, problem.getMessage());
            printTag(Logger.PROBLEM_MESSAGE, this.parameters, true, true);
            extractContext(problem, null);
            endTag(Logger.EXTRA_PROBLEM_TAG);
        }

        /**
		 * @param problem
		 *            the given problem to log
		 * @param unitSource
		 *            the given unit source
		 */
        private void logXmlProblem(CategorizedProblem problem, char[] unitSource) {
            final int sourceStart = problem.getSourceStart();
            final int sourceEnd = problem.getSourceEnd();
            final int id = problem.getID();
            // ID as field name
            this.parameters.put(Logger.ID, getFieldName(id));
            // ID as numeric value
            this.parameters.put(Logger.PROBLEM_ID, Integer.valueOf(id));
            boolean isError = problem.isError();
            int severity = isError ? ProblemSeverities.Error : ProblemSeverities.Warning;
            this.parameters.put(Logger.PROBLEM_SEVERITY, isError ? Logger.ERROR : Logger.WARNING);
            this.parameters.put(Logger.PROBLEM_LINE, Integer.valueOf(problem.getSourceLineNumber()));
            this.parameters.put(Logger.PROBLEM_SOURCE_START, Integer.valueOf(sourceStart));
            this.parameters.put(Logger.PROBLEM_SOURCE_END, Integer.valueOf(sourceEnd));
            String problemOptionKey = getProblemOptionKey(id);
            if (problemOptionKey != null) {
                this.parameters.put(Logger.PROBLEM_OPTION_KEY, problemOptionKey);
            }
            int categoryID = ProblemReporter.getProblemCategory(severity, id);
            this.parameters.put(Logger.PROBLEM_CATEGORY_ID, Integer.valueOf(categoryID));
            printTag(Logger.PROBLEM_TAG, this.parameters, true, false);
            this.parameters.put(Logger.VALUE, problem.getMessage());
            printTag(Logger.PROBLEM_MESSAGE, this.parameters, true, true);
            extractContext(problem, unitSource);
            String[] arguments = problem.getArguments();
            final int length = arguments.length;
            if (length != 0) {
                printTag(Logger.PROBLEM_ARGUMENTS, null, true, false);
                for (int i = 0; i < length; i++) {
                    this.parameters.put(Logger.PROBLEM_ARGUMENT_VALUE, arguments[i]);
                    printTag(Logger.PROBLEM_ARGUMENT, this.parameters, true, true);
                }
                endTag(Logger.PROBLEM_ARGUMENTS);
            }
            endTag(Logger.PROBLEM_TAG);
        }

        /**
		 * @param problem
		 *            the given problem to log
		 * @param unitSource
		 *            the given unit source
		 */
        private void logXmlTask(CategorizedProblem problem, char[] unitSource) {
            this.parameters.put(Logger.PROBLEM_LINE, Integer.valueOf(problem.getSourceLineNumber()));
            this.parameters.put(Logger.PROBLEM_SOURCE_START, Integer.valueOf(problem.getSourceStart()));
            this.parameters.put(Logger.PROBLEM_SOURCE_END, Integer.valueOf(problem.getSourceEnd()));
            String problemOptionKey = getProblemOptionKey(problem.getID());
            if (problemOptionKey != null) {
                this.parameters.put(Logger.PROBLEM_OPTION_KEY, problemOptionKey);
            }
            printTag(Logger.TASK, this.parameters, true, false);
            this.parameters.put(Logger.VALUE, problem.getMessage());
            printTag(Logger.PROBLEM_MESSAGE, this.parameters, true, true);
            extractContext(problem, unitSource);
            endTag(Logger.TASK);
        }

        private void printErr(String s) {
            this.err.print(s);
            if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
                this.log.print(s);
            }
        }

        private void printlnErr() {
            this.err.println();
            if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
                this.log.println();
            }
        }

        private void printlnErr(String s) {
            this.err.println(s);
            if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
                this.log.println(s);
            }
        }

        private void printlnOut(String s) {
            this.out.println(s);
            if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
                this.log.println(s);
            }
        }

        /**
		 *
		 */
        public void printNewLine() {
            this.out.println();
        }

        private void printOut(char c) {
            this.out.print(c);
        }

        public void printStats() {
            final boolean isTimed = (this.main.timing & TIMING_ENABLED) != 0;
            if ((this.tagBits & Logger.XML) != 0) {
                printTag(Logger.STATS, null, true, false);
            }
            if (isTimed) {
                CompilerStats compilerStats = this.main.batchCompiler.stats;
                // also include batch initialization times
                compilerStats.startTime = this.main.startTime;
                // also include batch output times
                compilerStats.endTime = System.currentTimeMillis();
                logTiming(compilerStats);
            }
            if (this.main.globalProblemsCount > 0) {
                logProblemsSummary(this.main.globalProblemsCount, this.main.globalErrorsCount, this.main.globalWarningsCount, this.main.globalTasksCount);
            }
            if (this.main.exportedClassFilesCounter != 0 && (this.main.showProgress || isTimed || this.main.verbose)) {
                logNumberOfClassFilesGenerated(this.main.exportedClassFilesCounter);
            }
            if ((this.tagBits & Logger.XML) != 0) {
                endTag(Logger.STATS);
            }
        }

        private void printTag(String name, HashMap params, boolean insertNewLine, boolean closeTag) {
            if (this.log != null) {
                ((GenericXMLWriter) this.log).printTag(name, this.parameters, true, insertNewLine, closeTag);
            }
            this.parameters.clear();
        }

        public void setEmacs() {
            this.tagBits |= Logger.EMACS;
        }

        public void setLog(String logFileName) {
            final Date date = new Date();
            final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.getDefault());
            try {
                int index = logFileName.lastIndexOf('.');
                if (index != -1) {
                    if (//$NON-NLS-1$
                    logFileName.substring(index).toLowerCase().equals(//$NON-NLS-1$
                    ".xml")) {
                        this.log = new GenericXMLWriter(new OutputStreamWriter(new FileOutputStream(logFileName, false), Util.UTF_8), Util.LINE_SEPARATOR, true);
                        this.tagBits |= Logger.XML;
                        // insert time stamp as comment
                        //$NON-NLS-1$//$NON-NLS-2$
                        this.log.println("<!-- " + dateFormat.format(date) + " -->");
                        this.log.println(Logger.XML_DTD_DECLARATION);
                        this.parameters.put(Logger.COMPILER_NAME, //$NON-NLS-1$
                        this.main.bind(//$NON-NLS-1$
                        "compiler.name"));
                        this.parameters.put(Logger.COMPILER_VERSION, //$NON-NLS-1$
                        this.main.bind(//$NON-NLS-1$
                        "compiler.version"));
                        this.parameters.put(Logger.COMPILER_COPYRIGHT, this.main.bind("compiler.copyright"));
                        printTag(Logger.COMPILER, this.parameters, true, false);
                    } else {
                        this.log = new PrintWriter(new FileOutputStream(logFileName, false));
                        this.log.println("# " + //$NON-NLS-1$
                        dateFormat.format(//$NON-NLS-1$
                        date));
                    }
                } else {
                    this.log = new PrintWriter(new FileOutputStream(logFileName, false));
                    this.log.println("# " + //$NON-NLS-1$
                    dateFormat.format(//$NON-NLS-1$
                    date));
                }
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLog", logFileName));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLogInvalidEncoding", logFileName));
            }
        }

        private void startLoggingExtraProblems(int count) {
            this.parameters.put(Logger.NUMBER_OF_PROBLEMS, Integer.valueOf(count));
            printTag(Logger.EXTRA_PROBLEMS, this.parameters, true, false);
        }

        /**
		 * Used to start logging problems.
		 * Only use in xml mode.
		 */
        private void startLoggingProblems(int errors, int warnings) {
            this.parameters.put(Logger.NUMBER_OF_PROBLEMS, Integer.valueOf(errors + warnings));
            this.parameters.put(Logger.NUMBER_OF_ERRORS, Integer.valueOf(errors));
            this.parameters.put(Logger.NUMBER_OF_WARNINGS, Integer.valueOf(warnings));
            printTag(Logger.PROBLEMS, this.parameters, true, false);
        }

        public void startLoggingSource(CompilationResult compilationResult) {
            if ((this.tagBits & Logger.XML) != 0) {
                ICompilationUnit compilationUnit = compilationResult.compilationUnit;
                if (compilationUnit != null) {
                    char[] fileName = compilationUnit.getFileName();
                    File f = new File(new String(fileName));
                    if (fileName != null) {
                        this.parameters.put(Logger.PATH, f.getAbsolutePath());
                    }
                    char[][] packageName = compilationResult.packageName;
                    if (packageName != null) {
                        this.parameters.put(Logger.PACKAGE, new String(CharOperation.concatWith(packageName, File.separatorChar)));
                    }
                    CompilationUnit unit = (CompilationUnit) compilationUnit;
                    String destinationPath = unit.destinationPath;
                    if (destinationPath == null) {
                        destinationPath = this.main.destinationPath;
                    }
                    if (destinationPath != null && destinationPath != NONE) {
                        if (File.separatorChar == '/') {
                            this.parameters.put(Logger.OUTPUT, destinationPath);
                        } else {
                            this.parameters.put(Logger.OUTPUT, destinationPath.replace('/', File.separatorChar));
                        }
                    }
                }
                printTag(Logger.SOURCE, this.parameters, true, false);
            }
        }

        public void startLoggingSources() {
            if ((this.tagBits & Logger.XML) != 0) {
                printTag(Logger.SOURCES, null, true, false);
            }
        }

        public void startLoggingTasks(int tasks) {
            if ((this.tagBits & Logger.XML) != 0) {
                this.parameters.put(Logger.NUMBER_OF_TASKS, Integer.valueOf(tasks));
                printTag(Logger.TASKS, this.parameters, true, false);
            }
        }
    }

    /**
	 * Resource bundle factory to share bundles for the same locale
	 */
    public static class ResourceBundleFactory {

        private static HashMap Cache = new HashMap();

        public static synchronized ResourceBundle getBundle(Locale locale) {
            ResourceBundle bundle = (ResourceBundle) Cache.get(locale);
            if (bundle == null) {
                bundle = ResourceBundle.getBundle(Main.bundleName, locale);
                Cache.put(locale, bundle);
            }
            return bundle;
        }
    }

    // used with -annotationpath to declare that annotations should be read from the classpath:
    //$NON-NLS-1$
    private static final String ANNOTATION_SOURCE_CLASSPATH = "CLASSPATH";

    // javadoc analysis tuning
    boolean enableJavadocOn;

    boolean warnJavadocOn;

    boolean warnAllJavadocOn;

    public Compiler batchCompiler;

    /* Bundle containing messages */
    public ResourceBundle bundle;

    protected FileSystem.Classpath[] checkedClasspaths;

    // paths to external annotations:
    protected List<String> annotationPaths;

    protected boolean annotationsFromClasspath;

    public Locale compilerLocale;

    // read-only
    public CompilerOptions compilerOptions;

    public CompilationProgress progress;

    public String destinationPath;

    public String[] destinationPaths;

    // destination path for compilation units that get no more specific
    // one (through directory arguments or various classpath options);
    // coding is:
    // == null: unspecified, write class files close to their respective
    //          source files;
    // == Main.NONE: absorbent element, do not output class files;
    // else: use as the path of the directory into which class files must
    //       be written.
    private boolean didSpecifySource;

    private boolean didSpecifyTarget;

    public String[] encodings;

    public int exportedClassFilesCounter;

    public String[] filenames;

    public String[] classNames;

    // overrides of destinationPath on a directory argument basis
    public int globalErrorsCount;

    public int globalProblemsCount;

    public int globalTasksCount;

    public int globalWarningsCount;

    private File javaHomeCache;

    private boolean javaHomeChecked = false;

    private boolean primaryNullAnnotationsSeen = false;

    public long lineCount0;

    public String log;

    public Logger logger;

    public int maxProblems;

    public Map<String, String> options;

    public char[][] ignoreOptionalProblemsFromFolders;

    protected PrintWriter out;

    public boolean proceed = true;

    public boolean proceedOnError = false;

    public boolean produceRefInfo = false;

    public int currentRepetition, maxRepetition;

    public boolean showProgress = false;

    public long startTime;

    public ArrayList pendingErrors;

    public boolean systemExitWhenFinished = true;

    public static final int TIMING_DISABLED = 0;

    public static final int TIMING_ENABLED = 1;

    public static final int TIMING_DETAILED = 2;

    public int timing = TIMING_DISABLED;

    public CompilerStats[] compilerStats;

    public boolean verbose = false;

    private String[] expandedCommandLine;

    private PrintWriter err;

    protected ArrayList extraProblems;

    //$NON-NLS-1$
    public static final String bundleName = "org.eclipse.jdt.internal.compiler.batch.messages";

    // two uses: recognize 'none' in options; code the singleton none
    // for the '-d none' option (wherever it may be found)
    public static final int DEFAULT_SIZE_CLASSPATH = 4;

    //$NON-NLS-1$
    public static final String NONE = "none";

    /**
 * @deprecated - use {@link BatchCompiler#compile(String, PrintWriter, PrintWriter, CompilationProgress)} instead
 * 						  e.g. BatchCompiler.compile(commandLine, new PrintWriter(System.out), new PrintWriter(System.err), null);
 */
    public static boolean compile(String commandLine) {
        return new Main(new PrintWriter(System.out), new PrintWriter(System.err), false, /* systemExit */
        null, /* options */
        null).compile(tokenize(commandLine));
    }

    /**
 * @deprecated - use {@link BatchCompiler#compile(String, PrintWriter, PrintWriter, CompilationProgress)} instead
 *                       e.g. BatchCompiler.compile(commandLine, outWriter, errWriter, null);
 */
    public static boolean compile(String commandLine, PrintWriter outWriter, PrintWriter errWriter) {
        return new Main(outWriter, errWriter, false, /* systemExit */
        null, /* options */
        null).compile(tokenize(commandLine));
    }

    /*
 * Internal API for public API BatchCompiler#compile(String[], PrintWriter, PrintWriter, CompilationProgress)
 */
    public static boolean compile(String[] commandLineArguments, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress) {
        return new Main(outWriter, errWriter, false, /* systemExit */
        null, /* options */
        progress).compile(commandLineArguments);
    }

    public static File[][] getLibrariesFiles(File[] files) {
        FilenameFilter filter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return Util.isPotentialZipArchive(name);
            }
        };
        final int filesLength = files.length;
        File[][] result = new File[filesLength][];
        for (int i = 0; i < filesLength; i++) {
            File currentFile = files[i];
            if (currentFile.exists() && currentFile.isDirectory()) {
                result[i] = currentFile.listFiles(filter);
            }
        }
        return result;
    }

    public static void main(String[] argv) {
        new Main(new PrintWriter(System.out), new PrintWriter(System.err), true, /*systemExit*/
        null, /*options*/
        null).compile(argv);
    }

    public static String[] tokenize(String commandLine) {
        int count = 0;
        String[] arguments = new String[10];
        //$NON-NLS-1$
        StringTokenizer tokenizer = new StringTokenizer(commandLine, " \"", true);
        String token = Util.EMPTY_STRING;
        boolean insideQuotes = false;
        boolean startNewToken = true;
        // 'xxx/"aaa bbb";"ccc" yyy' --->  {"xxx/aaa bbb;ccc", "yyy" }
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (//$NON-NLS-1$
            token.equals(" ")) {
                if (insideQuotes) {
                    arguments[count - 1] += token;
                    startNewToken = false;
                } else {
                    startNewToken = true;
                }
            } else if (//$NON-NLS-1$
            token.equals("\"")) {
                if (!insideQuotes && startNewToken) {
                    if (count == arguments.length)
                        System.arraycopy(arguments, 0, (arguments = new String[count * 2]), 0, count);
                    arguments[count++] = Util.EMPTY_STRING;
                }
                insideQuotes = !insideQuotes;
                startNewToken = false;
            } else {
                if (insideQuotes) {
                    arguments[count - 1] += token;
                } else {
                    if (token.length() > 0 && !startNewToken) {
                        arguments[count - 1] += token;
                    } else {
                        if (count == arguments.length)
                            System.arraycopy(arguments, 0, (arguments = new String[count * 2]), 0, count);
                        String trimmedToken = token.trim();
                        if (trimmedToken.length() != 0) {
                            arguments[count++] = trimmedToken;
                        }
                    }
                }
                startNewToken = false;
            }
        }
        System.arraycopy(arguments, 0, arguments = new String[count], 0, count);
        return arguments;
    }

    /**
 * @deprecated - use {@link #Main(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. Main(outWriter, errWriter, systemExitWhenFinished, null, null)
 */
    public  Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished) {
        this(outWriter, errWriter, systemExitWhenFinished, null, /* options */
        null);
    }

    /**
 * @deprecated - use {@link #Main(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. Main(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, null)
 */
    public  Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map customDefaultOptions) {
        this(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, null);
    }

    public  Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress) {
        this.initialize(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress);
        this.relocalize();
    }

    public void addExtraProblems(CategorizedProblem problem) {
        if (this.extraProblems == null) {
            this.extraProblems = new ArrayList();
        }
        this.extraProblems.add(problem);
    }

    protected void addNewEntry(ArrayList paths, String currentClasspathName, ArrayList currentRuleSpecs, String customEncoding, String destPath, boolean isSourceOnly, boolean rejectDestinationPathOnJars) {
        int rulesSpecsSize = currentRuleSpecs.size();
        AccessRuleSet accessRuleSet = null;
        if (rulesSpecsSize != 0) {
            AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
            boolean rulesOK = true;
            Iterator i = currentRuleSpecs.iterator();
            int j = 0;
            while (i.hasNext()) {
                String ruleSpec = (String) i.next();
                char key = ruleSpec.charAt(0);
                String pattern = ruleSpec.substring(1);
                if (pattern.length() > 0) {
                    switch(key) {
                        case '+':
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 0);
                            break;
                        case '~':
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), IProblem.DiscouragedReference);
                            break;
                        case '-':
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), IProblem.ForbiddenReference);
                            break;
                        case '?':
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), IProblem.ForbiddenReference, /*keep looking for accessible type*/
                            true);
                            break;
                        default:
                            rulesOK = false;
                    }
                } else {
                    rulesOK = false;
                }
            }
            if (rulesOK) {
                accessRuleSet = new AccessRuleSet(accessRules, AccessRestriction.COMMAND_LINE, currentClasspathName);
            } else {
                if (currentClasspathName.length() != 0) {
                    // we go on anyway
                    addPendingErrors(//$NON-NLS-1$
                    this.bind(//$NON-NLS-1$
                    "configure.incorrectClasspath", //$NON-NLS-1$
                    currentClasspathName));
                }
                return;
            }
        }
        if (NONE.equals(destPath)) {
            // keep == comparison valid
            destPath = NONE;
        }
        if (rejectDestinationPathOnJars && destPath != null && Util.isPotentialZipArchive(currentClasspathName)) {
            throw new IllegalArgumentException(//$NON-NLS-1$
            this.bind(//$NON-NLS-1$
            "configure.unexpectedDestinationPathEntryFile", currentClasspathName));
        }
        FileSystem.Classpath currentClasspath = FileSystem.getClasspath(currentClasspathName, customEncoding, isSourceOnly, accessRuleSet, destPath, this.options);
        if (currentClasspath != null) {
            paths.add(currentClasspath);
        } else if (currentClasspathName.length() != 0) {
            // we go on anyway
            //$NON-NLS-1$
            addPendingErrors(this.bind("configure.incorrectClasspath", currentClasspathName));
        }
    }

    void addPendingErrors(String message) {
        if (this.pendingErrors == null) {
            this.pendingErrors = new ArrayList();
        }
        this.pendingErrors.add(message);
    }

    /*
 * Lookup the message with the given ID in this catalog
 */
    public String bind(String id) {
        return bind(id, (String[]) null);
    }

    /*
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given string.
 */
    public String bind(String id, String binding) {
        return bind(id, new String[] { binding });
    }

    /*
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given strings.
 */
    public String bind(String id, String binding1, String binding2) {
        return bind(id, new String[] { binding1, binding2 });
    }

    /*
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given string values.
 */
    public String bind(String id, String[] arguments) {
        if (id == null)
            //$NON-NLS-1$
            return "No message available";
        String message = null;
        try {
            message = this.bundle.getString(id);
        } catch (MissingResourceException e) {
            return "Missing message: " + id + " in: " + Main.bundleName;
        }
        return MessageFormat.format(message, (Object[]) arguments);
    }

    /**
 * Return true if and only if the running VM supports the given minimal version.
 *
 * <p>This only checks the major version, since the minor version is always 0 (at least for the useful cases).</p>
 * <p>The given minimalSupportedVersion is one of the constants:</p>
 * <ul>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_1</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_2</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_3</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_4</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_5</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_6</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_7</code></li>
 * </ul>
 * @param minimalSupportedVersion the given minimal version
 * @return true if and only if the running VM supports the given minimal version, false otherwise
 */
    private boolean checkVMVersion(long minimalSupportedVersion) {
        // the format of this property is supposed to be xx.x where x are digits.
        //$NON-NLS-1$
        String classFileVersion = System.getProperty("java.class.version");
        if (classFileVersion == null) {
            // by default we don't support a class file version we cannot recognize
            return false;
        }
        int index = classFileVersion.indexOf('.');
        if (index == -1) {
            // by default we don't support a class file version we cannot recognize
            return false;
        }
        int majorVersion;
        try {
            majorVersion = Integer.parseInt(classFileVersion.substring(0, index));
        } catch (NumberFormatException e) {
            return false;
        }
        switch(majorVersion) {
            case // 1.0 and 1.1
            ClassFileConstants.MAJOR_VERSION_1_1:
                return ClassFileConstants.JDK1_1 >= minimalSupportedVersion;
            case // 1.2
            ClassFileConstants.MAJOR_VERSION_1_2:
                return ClassFileConstants.JDK1_2 >= minimalSupportedVersion;
            case // 1.3
            ClassFileConstants.MAJOR_VERSION_1_3:
                return ClassFileConstants.JDK1_3 >= minimalSupportedVersion;
            case // 1.4
            ClassFileConstants.MAJOR_VERSION_1_4:
                return ClassFileConstants.JDK1_4 >= minimalSupportedVersion;
            case // 1.5
            ClassFileConstants.MAJOR_VERSION_1_5:
                return ClassFileConstants.JDK1_5 >= minimalSupportedVersion;
            case // 1.6
            ClassFileConstants.MAJOR_VERSION_1_6:
                return ClassFileConstants.JDK1_6 >= minimalSupportedVersion;
            case // 1.7
            ClassFileConstants.MAJOR_VERSION_1_7:
                return ClassFileConstants.JDK1_7 >= minimalSupportedVersion;
            case // 1.8
            ClassFileConstants.MAJOR_VERSION_1_8:
                return ClassFileConstants.JDK1_8 >= minimalSupportedVersion;
        }
        // unknown version
        return false;
    }

    /*
 *  Low-level API performing the actual compilation
 */
    public boolean compile(String[] argv) {
        // decode command line arguments
        try {
            configure(argv);
            if (this.progress != null)
                this.progress.begin(this.filenames == null ? 0 : this.filenames.length * this.maxRepetition);
            if (this.proceed) {
                //				}
                if (this.showProgress)
                    this.logger.compiling();
                for (this.currentRepetition = 0; this.currentRepetition < this.maxRepetition; this.currentRepetition++) {
                    this.globalProblemsCount = 0;
                    this.globalErrorsCount = 0;
                    this.globalWarningsCount = 0;
                    this.globalTasksCount = 0;
                    this.exportedClassFilesCounter = 0;
                    if (this.maxRepetition > 1) {
                        this.logger.flush();
                        this.logger.logRepetition(this.currentRepetition, this.maxRepetition);
                    }
                    // request compilation
                    performCompilation();
                }
                if (this.compilerStats != null) {
                    this.logger.logAverage();
                }
                if (this.showProgress)
                    this.logger.printNewLine();
            }
            if (this.systemExitWhenFinished) {
                this.logger.flush();
                this.logger.close();
                System.exit(this.globalErrorsCount > 0 ? -1 : 0);
            }
        } catch (IllegalArgumentException e) {
            this.logger.logException(e);
            if (this.systemExitWhenFinished) {
                this.logger.flush();
                this.logger.close();
                System.exit(-1);
            }
            return false;
        } catch (RuntimeException // internal compiler failure
        e) {
            this.logger.logException(e);
            if (this.systemExitWhenFinished) {
                this.logger.flush();
                this.logger.close();
                System.exit(-1);
            }
            return false;
        } finally {
            this.logger.flush();
            this.logger.close();
            if (this.progress != null)
                this.progress.done();
        }
        if (this.globalErrorsCount == 0 && (this.progress == null || !this.progress.isCanceled()))
            return true;
        return false;
    }

    /*
Decode the command line arguments
 */
    public void configure(String[] argv) {
        if ((argv == null) || (argv.length == 0)) {
            printUsage();
            return;
        }
        final int INSIDE_CLASSPATH_start = 1;
        final int INSIDE_DESTINATION_PATH = 3;
        final int INSIDE_TARGET = 4;
        final int INSIDE_LOG = 5;
        final int INSIDE_REPETITION = 6;
        final int INSIDE_SOURCE = 7;
        final int INSIDE_DEFAULT_ENCODING = 8;
        final int INSIDE_BOOTCLASSPATH_start = 9;
        final int INSIDE_MAX_PROBLEMS = 11;
        final int INSIDE_EXT_DIRS = 12;
        final int INSIDE_SOURCE_PATH_start = 13;
        final int INSIDE_ENDORSED_DIRS = 15;
        final int INSIDE_SOURCE_DIRECTORY_DESTINATION_PATH = 16;
        final int INSIDE_PROCESSOR_PATH_start = 17;
        final int INSIDE_PROCESSOR_start = 18;
        final int INSIDE_S_start = 19;
        final int INSIDE_CLASS_NAMES = 20;
        final int INSIDE_WARNINGS_PROPERTIES = 21;
        final int INSIDE_ANNOTATIONPATH_start = 22;
        final int DEFAULT = 0;
        ArrayList bootclasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
        String sourcepathClasspathArg = null;
        ArrayList sourcepathClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
        ArrayList classpaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
        ArrayList extdirsClasspaths = null;
        ArrayList endorsedDirClasspaths = null;
        this.annotationPaths = null;
        this.annotationsFromClasspath = false;
        int index = -1;
        int filesCount = 0;
        int classCount = 0;
        int argCount = argv.length;
        int mode = DEFAULT;
        this.maxRepetition = 0;
        boolean printUsageRequired = false;
        String usageSection = null;
        boolean printVersionRequired = false;
        boolean didSpecifyDeprecation = false;
        boolean didSpecifyCompliance = false;
        boolean didSpecifyDisabledAnnotationProcessing = false;
        String customEncoding = null;
        String customDestinationPath = null;
        String currentSourceDirectory = null;
        String currentArg = Util.EMPTY_STRING;
        Set specifiedEncodings = null;
        // expand the command line if necessary
        boolean needExpansion = false;
        loop: for (int i = 0; i < argCount; i++) {
            if (//$NON-NLS-1$
            argv[i].startsWith("@")) {
                needExpansion = true;
                break loop;
            }
        }
        String[] newCommandLineArgs = null;
        if (needExpansion) {
            newCommandLineArgs = new String[argCount];
            index = 0;
            for (int i = 0; i < argCount; i++) {
                String[] newArgs = null;
                String arg = argv[i].trim();
                if (//$NON-NLS-1$
                arg.startsWith("@")) {
                    try {
                        LineNumberReader reader = new LineNumberReader(new StringReader(new String(Util.getFileCharContent(new File(arg.substring(1)), null))));
                        StringBuffer buffer = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (!//$NON-NLS-1$
                            line.startsWith(//$NON-NLS-1$
                            "#")) {
                                //$NON-NLS-1$
                                buffer.append(//$NON-NLS-1$
                                line).append(" ");
                            }
                        }
                        newArgs = tokenize(buffer.toString());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(this.bind("configure.invalidexpansionargumentname", arg));
                    }
                }
                if (newArgs != null) {
                    int newCommandLineArgsLength = newCommandLineArgs.length;
                    int newArgsLength = newArgs.length;
                    System.arraycopy(newCommandLineArgs, 0, (newCommandLineArgs = new String[newCommandLineArgsLength + newArgsLength - 1]), 0, index);
                    System.arraycopy(newArgs, 0, newCommandLineArgs, index, newArgsLength);
                    index += newArgsLength;
                } else {
                    newCommandLineArgs[index++] = arg;
                }
            }
            index = -1;
        } else {
            newCommandLineArgs = argv;
            for (int i = 0; i < argCount; i++) {
                newCommandLineArgs[i] = newCommandLineArgs[i].trim();
            }
        }
        argCount = newCommandLineArgs.length;
        this.expandedCommandLine = newCommandLineArgs;
        while (++index < argCount) {
            if (customEncoding != null) {
                throw new IllegalArgumentException(//$NON-NLS-1$
                this.bind("configure.unexpectedCustomEncoding", currentArg, customEncoding));
            }
            currentArg = newCommandLineArgs[index];
            switch(mode) {
                case DEFAULT:
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-nowarn")) {
                        switch(currentArg.length()) {
                            case 7:
                                disableAll(ProblemSeverities.Warning);
                                break;
                            case 8:
                                throw new IllegalArgumentException(this.bind("configure.invalidNowarnOption", currentArg));
                            default:
                                int foldersStart = currentArg.indexOf('[') + 1;
                                int foldersEnd = currentArg.lastIndexOf(']');
                                if (foldersStart <= 8 || foldersEnd == -1 || foldersStart > foldersEnd || foldersEnd < currentArg.length() - 1) {
                                    throw new IllegalArgumentException(this.bind("configure.invalidNowarnOption", currentArg));
                                }
                                String folders = currentArg.substring(foldersStart, foldersEnd);
                                if (folders.length() > 0) {
                                    char[][] currentFolders = decodeIgnoreOptionalProblemsFromFolders(folders);
                                    if (this.ignoreOptionalProblemsFromFolders != null) {
                                        int length = this.ignoreOptionalProblemsFromFolders.length + currentFolders.length;
                                        char[][] tempFolders = new char[length][];
                                        System.arraycopy(this.ignoreOptionalProblemsFromFolders, 0, tempFolders, 0, this.ignoreOptionalProblemsFromFolders.length);
                                        System.arraycopy(currentFolders, 0, tempFolders, this.ignoreOptionalProblemsFromFolders.length, currentFolders.length);
                                        this.ignoreOptionalProblemsFromFolders = tempFolders;
                                    } else {
                                        this.ignoreOptionalProblemsFromFolders = currentFolders;
                                    }
                                } else {
                                    throw new IllegalArgumentException(this.bind("configure.invalidNowarnOption", currentArg));
                                }
                        }
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.startsWith("[")) {
                        throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                    }
                    if (//$NON-NLS-1$
                    currentArg.endsWith("]")) {
                        // look for encoding specification
                        int encodingStart = currentArg.indexOf('[') + 1;
                        if (encodingStart <= 1) {
                            throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                        }
                        int encodingEnd = currentArg.length() - 1;
                        if (encodingStart >= 1) {
                            if (encodingStart < encodingEnd) {
                                customEncoding = currentArg.substring(encodingStart, encodingEnd);
                                // ensure encoding is supported
                                try {
                                    new InputStreamReader(new ByteArrayInputStream(new byte[0]), customEncoding);
                                } catch (UnsupportedEncodingException e) {
                                    throw new IllegalArgumentException(this.bind("configure.unsupportedEncoding", customEncoding));
                                }
                            }
                            currentArg = currentArg.substring(0, encodingStart - 1);
                        }
                    }
                    if (currentArg.endsWith(SuffixConstants.SUFFIX_STRING_java)) {
                        if (this.filenames == null) {
                            this.filenames = new String[argCount - index];
                            this.encodings = new String[argCount - index];
                            this.destinationPaths = new String[argCount - index];
                        } else if (filesCount == this.filenames.length) {
                            int length = this.filenames.length;
                            System.arraycopy(this.filenames, 0, (this.filenames = new String[length + argCount - index]), 0, length);
                            System.arraycopy(this.encodings, 0, (this.encodings = new String[length + argCount - index]), 0, length);
                            System.arraycopy(this.destinationPaths, 0, (this.destinationPaths = new String[length + argCount - index]), 0, length);
                        }
                        this.filenames[filesCount] = currentArg;
                        this.encodings[filesCount++] = customEncoding;
                        // destination path cannot be specified upon an individual file
                        customEncoding = null;
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-log")) {
                        if (this.log != null)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateLog", //$NON-NLS-1$
                            currentArg));
                        mode = INSIDE_LOG;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-repeat")) {
                        if (this.maxRepetition > 0)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateRepeat", //$NON-NLS-1$
                            currentArg));
                        mode = INSIDE_REPETITION;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-maxProblems")) {
                        if (this.maxProblems > 0)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateMaxProblems", //$NON-NLS-1$
                            currentArg));
                        mode = INSIDE_MAX_PROBLEMS;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-source")) {
                        mode = INSIDE_SOURCE;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-encoding")) {
                        mode = INSIDE_DEFAULT_ENCODING;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-1.3")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateCompliance", //$NON-NLS-1$
                            currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_3);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-1.4")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateCompliance", //$NON-NLS-1$
                            currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_4);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("-1.5") || currentArg.equals("-5") || currentArg.equals("-5.0")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateCompliance", //$NON-NLS-1$
                            currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_5);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("-1.6") || currentArg.equals("-6") || currentArg.equals("-6.0")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateCompliance", //$NON-NLS-1$
                            currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_6);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("-1.7") || currentArg.equals("-7") || currentArg.equals("-7.0")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateCompliance", //$NON-NLS-1$
                            currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_7);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("-1.8") || currentArg.equals("-8") || currentArg.equals("-8.0")) {
                        if (didSpecifyCompliance) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateCompliance", //$NON-NLS-1$
                            currentArg));
                        }
                        didSpecifyCompliance = true;
                        this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_8);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-d")) {
                        if (this.destinationPath != null) {
                            StringBuffer errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if ((index + 1) < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateOutputPath", //$NON-NLS-1$
                            errorMessage.toString()));
                        }
                        mode = INSIDE_DESTINATION_PATH;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals(//$NON-NLS-1$
                    "-classpath") || //$NON-NLS-1$
                    currentArg.equals(//$NON-NLS-1$
                    "-cp")) {
                        mode = INSIDE_CLASSPATH_start;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-bootclasspath")) {
                        if (bootclasspaths.size() > 0) {
                            StringBuffer errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if ((index + 1) < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateBootClasspath", //$NON-NLS-1$
                            errorMessage.toString()));
                        }
                        mode = INSIDE_BOOTCLASSPATH_start;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-sourcepath")) {
                        if (sourcepathClasspathArg != null) {
                            StringBuffer errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if ((index + 1) < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateSourcepath", //$NON-NLS-1$
                            errorMessage.toString()));
                        }
                        mode = INSIDE_SOURCE_PATH_start;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-extdirs")) {
                        if (extdirsClasspaths != null) {
                            StringBuffer errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if ((index + 1) < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateExtDirs", //$NON-NLS-1$
                            errorMessage.toString()));
                        }
                        mode = INSIDE_EXT_DIRS;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-endorseddirs")) {
                        if (endorsedDirClasspaths != null) {
                            StringBuffer errorMessage = new StringBuffer();
                            errorMessage.append(currentArg);
                            if ((index + 1) < argCount) {
                                errorMessage.append(' ');
                                errorMessage.append(newCommandLineArgs[index + 1]);
                            }
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.duplicateEndorsedDirs", //$NON-NLS-1$
                            errorMessage.toString()));
                        }
                        mode = INSIDE_ENDORSED_DIRS;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-progress")) {
                        mode = DEFAULT;
                        this.showProgress = true;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-proceedOnError")) {
                        mode = DEFAULT;
                        int length = currentArg.length();
                        if (length > 15) {
                            if (currentArg.equals("-proceedOnError:Fatal")) {
                                this.options.put(CompilerOptions.OPTION_FatalOptionalError, CompilerOptions.ENABLED);
                            } else {
                                throw new IllegalArgumentException(this.bind("configure.invalidWarningConfiguration", currentArg));
                            }
                        } else {
                            this.options.put(CompilerOptions.OPTION_FatalOptionalError, CompilerOptions.DISABLED);
                        }
                        this.proceedOnError = true;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-time")) {
                        mode = DEFAULT;
                        this.timing = TIMING_ENABLED;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-time:detail")) {
                        mode = DEFAULT;
                        this.timing = TIMING_ENABLED | TIMING_DETAILED;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals(//$NON-NLS-1$
                    "-version") || //$NON-NLS-1$
                    currentArg.equals(//$NON-NLS-1$
                    "-v")) {
                        this.logger.logVersion(true);
                        this.proceed = false;
                        return;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-showversion")) {
                        printVersionRequired = true;
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    "-deprecation".equals(currentArg)) {
                        didSpecifyDeprecation = true;
                        this.options.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.WARNING);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$ //$NON-NLS-2$
                    currentArg.equals("-help") || currentArg.equals("-?")) {
                        printUsageRequired = true;
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-help:warn") || //$NON-NLS-1$
                    currentArg.equals(//$NON-NLS-1$
                    "-?:warn")) {
                        printUsageRequired = true;
                        //$NON-NLS-1$
                        usageSection = //$NON-NLS-1$
                        "misc.usage.warn";
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-noExit")) {
                        this.systemExitWhenFinished = false;
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-verbose")) {
                        this.verbose = true;
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-referenceInfo")) {
                        this.produceRefInfo = true;
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-inlineJSR")) {
                        mode = DEFAULT;
                        this.options.put(CompilerOptions.OPTION_InlineJsr, CompilerOptions.ENABLED);
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-parameters")) {
                        mode = DEFAULT;
                        this.options.put(CompilerOptions.OPTION_MethodParametersAttribute, CompilerOptions.GENERATE);
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-genericsignature")) {
                        mode = DEFAULT;
                        this.options.put(CompilerOptions.OPTION_LambdaGenericSignature, CompilerOptions.GENERATE);
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-g")) {
                        mode = DEFAULT;
                        String debugOption = currentArg;
                        int length = currentArg.length();
                        if (length == 2) {
                            this.options.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
                            this.options.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
                            this.options.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
                            continue;
                        }
                        if (length > 3) {
                            this.options.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.DO_NOT_GENERATE);
                            this.options.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.DO_NOT_GENERATE);
                            this.options.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.DO_NOT_GENERATE);
                            if (length == 7 && //$NON-NLS-1$
                            debugOption.equals(//$NON-NLS-1$
                            "-g:" + NONE))
                                continue;
                            StringTokenizer tokenizer = new StringTokenizer(debugOption.substring(3, //$NON-NLS-1$
                            debugOption.length()), ",");
                            while (tokenizer.hasMoreTokens()) {
                                String token = tokenizer.nextToken();
                                if (//$NON-NLS-1$
                                token.equals("vars")) {
                                    this.options.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
                                } else if (//$NON-NLS-1$
                                token.equals("lines")) {
                                    this.options.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
                                } else if (//$NON-NLS-1$
                                token.equals("source")) {
                                    this.options.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
                                } else {
                                    throw new IllegalArgumentException(this.bind("configure.invalidDebugOption", debugOption));
                                }
                            }
                            continue;
                        }
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.invalidDebugOption", //$NON-NLS-1$
                        debugOption));
                    }
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-warn")) {
                        mode = DEFAULT;
                        String warningOption = currentArg;
                        int length = currentArg.length();
                        if (length == 10 && warningOption.equals(//$NON-NLS-1$
                        "-warn:" + //$NON-NLS-1$
                        NONE)) {
                            disableAll(ProblemSeverities.Warning);
                            continue;
                        }
                        if (length <= 6) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidWarningConfiguration", //$NON-NLS-1$
                            warningOption));
                        }
                        int warnTokenStart;
                        boolean isEnabling;
                        switch(warningOption.charAt(6)) {
                            case '+':
                                warnTokenStart = 7;
                                isEnabling = true;
                                break;
                            case '-':
                                warnTokenStart = 7;
                                // specified warnings are disabled
                                isEnabling = false;
                                break;
                            default:
                                disableAll(ProblemSeverities.Warning);
                                warnTokenStart = 6;
                                isEnabling = true;
                        }
                        StringTokenizer tokenizer = new StringTokenizer(warningOption.substring(warnTokenStart, //$NON-NLS-1$
                        warningOption.length()), ",");
                        int tokenCounter = 0;
                        if (// deprecation could have also been set through -deprecation option
                        didSpecifyDeprecation) {
                            this.options.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.WARNING);
                        }
                        while (tokenizer.hasMoreTokens()) {
                            String token = tokenizer.nextToken();
                            tokenCounter++;
                            switch(token.charAt(0)) {
                                case '+':
                                    isEnabling = true;
                                    token = token.substring(1);
                                    break;
                                case '-':
                                    isEnabling = false;
                                    token = token.substring(1);
                            }
                            handleWarningToken(token, isEnabling);
                        }
                        if (tokenCounter == 0) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidWarningOption", //$NON-NLS-1$
                            currentArg));
                        }
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-err")) {
                        mode = DEFAULT;
                        String errorOption = currentArg;
                        int length = currentArg.length();
                        if (length <= 5) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidErrorConfiguration", //$NON-NLS-1$
                            errorOption));
                        }
                        int errorTokenStart;
                        boolean isEnabling;
                        switch(errorOption.charAt(5)) {
                            case '+':
                                errorTokenStart = 6;
                                isEnabling = true;
                                break;
                            case '-':
                                errorTokenStart = 6;
                                // specified errors are disabled
                                isEnabling = false;
                                break;
                            default:
                                disableAll(ProblemSeverities.Error);
                                errorTokenStart = 5;
                                isEnabling = true;
                        }
                        StringTokenizer tokenizer = new StringTokenizer(errorOption.substring(errorTokenStart, //$NON-NLS-1$
                        errorOption.length()), ",");
                        int tokenCounter = 0;
                        while (tokenizer.hasMoreTokens()) {
                            String token = tokenizer.nextToken();
                            tokenCounter++;
                            switch(token.charAt(0)) {
                                case '+':
                                    isEnabling = true;
                                    token = token.substring(1);
                                    break;
                                case '-':
                                    isEnabling = false;
                                    token = token.substring(1);
                                    break;
                            }
                            handleErrorToken(token, isEnabling);
                        }
                        if (tokenCounter == 0) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidErrorOption", //$NON-NLS-1$
                            currentArg));
                        }
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-target")) {
                        mode = INSIDE_TARGET;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-preserveAllLocals")) {
                        this.options.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-enableJavadoc")) {
                        mode = DEFAULT;
                        this.enableJavadocOn = true;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-Xemacs")) {
                        mode = DEFAULT;
                        this.logger.setEmacs();
                        continue;
                    }
                    // annotation processing
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-A")) {
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-processorpath")) {
                        mode = INSIDE_PROCESSOR_PATH_start;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-processor")) {
                        mode = INSIDE_PROCESSOR_start;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-proc:only")) {
                        this.options.put(CompilerOptions.OPTION_GenerateClassFiles, CompilerOptions.DISABLED);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-proc:none")) {
                        didSpecifyDisabledAnnotationProcessing = true;
                        this.options.put(CompilerOptions.OPTION_Process_Annotations, CompilerOptions.DISABLED);
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-s")) {
                        mode = INSIDE_S_start;
                        continue;
                    }
                    if (currentArg.equals("-XprintProcessorInfo") || //$NON-NLS-1$
                    currentArg.equals(//$NON-NLS-1$
                    "-XprintRounds")) {
                        mode = DEFAULT;
                        continue;
                    }
                    // tolerated javac options - quietly filtered out
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-X")) {
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.startsWith("-J")) {
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-O")) {
                        mode = DEFAULT;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-classNames")) {
                        mode = INSIDE_CLASS_NAMES;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-properties")) {
                        mode = INSIDE_WARNINGS_PROPERTIES;
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-missingNullDefault")) {
                        this.options.put(CompilerOptions.OPTION_ReportMissingNonNullByDefaultAnnotation, CompilerOptions.WARNING);
                        continue;
                    }
                    if (//$NON-NLS-1$
                    currentArg.equals("-annotationpath")) {
                        mode = INSIDE_ANNOTATIONPATH_start;
                        continue;
                    }
                    break;
                case INSIDE_TARGET:
                    if (this.didSpecifyTarget) {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.duplicateTarget", //$NON-NLS-1$
                        currentArg));
                    }
                    this.didSpecifyTarget = true;
                    if (//$NON-NLS-1$
                    currentArg.equals("1.1")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_1);
                    } else if (//$NON-NLS-1$
                    currentArg.equals("1.2")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_2);
                    } else if (//$NON-NLS-1$
                    currentArg.equals("1.3")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_3);
                    } else if (//$NON-NLS-1$
                    currentArg.equals("1.4")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.5") || currentArg.equals("5") || currentArg.equals("5.0")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.6") || currentArg.equals("6") || currentArg.equals("6.0")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.7") || currentArg.equals("7") || currentArg.equals("7.0")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.8") || currentArg.equals("8") || currentArg.equals("8.0")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_8);
                    } else if (//$NON-NLS-1$
                    currentArg.equals("jsr14")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_JSR14);
                    } else if (//$NON-NLS-1$
                    currentArg.equals("cldc1.1")) {
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_CLDC1_1);
                        this.options.put(CompilerOptions.OPTION_InlineJsr, CompilerOptions.ENABLED);
                    } else {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.targetJDK", //$NON-NLS-1$
                        currentArg));
                    }
                    mode = DEFAULT;
                    continue;
                case INSIDE_LOG:
                    this.log = currentArg;
                    mode = DEFAULT;
                    continue;
                case INSIDE_REPETITION:
                    try {
                        this.maxRepetition = Integer.parseInt(currentArg);
                        if (this.maxRepetition <= 0) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.repetition", //$NON-NLS-1$
                            currentArg));
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(this.bind("configure.repetition", currentArg));
                    }
                    mode = DEFAULT;
                    continue;
                case INSIDE_MAX_PROBLEMS:
                    try {
                        this.maxProblems = Integer.parseInt(currentArg);
                        if (this.maxProblems <= 0) {
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.maxProblems", //$NON-NLS-1$
                            currentArg));
                        }
                        this.options.put(CompilerOptions.OPTION_MaxProblemPerUnit, currentArg);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(this.bind("configure.maxProblems", currentArg));
                    }
                    mode = DEFAULT;
                    continue;
                case INSIDE_SOURCE:
                    if (this.didSpecifySource) {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.duplicateSource", //$NON-NLS-1$
                        currentArg));
                    }
                    this.didSpecifySource = true;
                    if (//$NON-NLS-1$
                    currentArg.equals("1.3")) {
                        this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_3);
                    } else if (//$NON-NLS-1$
                    currentArg.equals("1.4")) {
                        this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_4);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.5") || currentArg.equals("5") || currentArg.equals("5.0")) {
                        this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.6") || currentArg.equals("6") || currentArg.equals("6.0")) {
                        this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_6);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.7") || currentArg.equals("7") || currentArg.equals("7.0")) {
                        this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_7);
                    } else if (//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    currentArg.equals("1.8") || currentArg.equals("8") || currentArg.equals("8.0")) {
                        this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_8);
                    } else {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.source", //$NON-NLS-1$
                        currentArg));
                    }
                    mode = DEFAULT;
                    continue;
                case INSIDE_DEFAULT_ENCODING:
                    if (specifiedEncodings != null) {
                        // check already defined encoding
                        if (!specifiedEncodings.contains(currentArg)) {
                            if (specifiedEncodings.size() > 1) {
                                this.logger.logWarning(//$NON-NLS-1$
                                this.bind(//$NON-NLS-1$
                                "configure.differentencodings", currentArg, getAllEncodings(specifiedEncodings)));
                            } else {
                                this.logger.logWarning(//$NON-NLS-1$
                                this.bind(//$NON-NLS-1$
                                "configure.differentencoding", currentArg, getAllEncodings(specifiedEncodings)));
                            }
                        }
                    } else {
                        specifiedEncodings = new HashSet();
                    }
                    // ensure encoding is supported
                    try {
                        new InputStreamReader(new ByteArrayInputStream(new byte[0]), currentArg);
                    } catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException(this.bind("configure.unsupportedEncoding", currentArg));
                    }
                    specifiedEncodings.add(currentArg);
                    this.options.put(CompilerOptions.OPTION_Encoding, currentArg);
                    mode = DEFAULT;
                    continue;
                case INSIDE_DESTINATION_PATH:
                    setDestinationPath(currentArg.equals(NONE) ? NONE : currentArg);
                    mode = DEFAULT;
                    continue;
                case INSIDE_CLASSPATH_start:
                    mode = DEFAULT;
                    index += processPaths(newCommandLineArgs, index, currentArg, classpaths);
                    continue;
                case INSIDE_BOOTCLASSPATH_start:
                    mode = DEFAULT;
                    index += processPaths(newCommandLineArgs, index, currentArg, bootclasspaths);
                    continue;
                case INSIDE_SOURCE_PATH_start:
                    mode = DEFAULT;
                    String[] sourcePaths = new String[1];
                    index += processPaths(newCommandLineArgs, index, currentArg, sourcePaths);
                    sourcepathClasspathArg = sourcePaths[0];
                    continue;
                case INSIDE_EXT_DIRS:
                    if (//$NON-NLS-1$
                    currentArg.indexOf("[-d") != -1) {
                        throw new IllegalArgumentException(this.bind("configure.unexpectedDestinationPathEntry", //$NON-NLS-1$
                        "-extdir"));
                    }
                    StringTokenizer tokenizer = new StringTokenizer(currentArg, File.pathSeparator, false);
                    extdirsClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
                    while (tokenizer.hasMoreTokens()) extdirsClasspaths.add(tokenizer.nextToken());
                    mode = DEFAULT;
                    continue;
                case INSIDE_ENDORSED_DIRS:
                    if (//$NON-NLS-1$
                    currentArg.indexOf("[-d") != -1) {
                        throw new IllegalArgumentException(this.bind("configure.unexpectedDestinationPathEntry", //$NON-NLS-1$
                        "-endorseddirs"));
                    }
                    tokenizer = new StringTokenizer(currentArg, File.pathSeparator, false);
                    endorsedDirClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
                    while (tokenizer.hasMoreTokens()) endorsedDirClasspaths.add(tokenizer.nextToken());
                    mode = DEFAULT;
                    continue;
                case INSIDE_SOURCE_DIRECTORY_DESTINATION_PATH:
                    if (//$NON-NLS-1$
                    currentArg.endsWith("]")) {
                        customDestinationPath = currentArg.substring(0, currentArg.length() - 1);
                    } else {
                        throw new IllegalArgumentException(this.bind("configure.incorrectDestinationPathEntry", //$NON-NLS-1$
                        "[-d " + currentArg));
                    }
                    break;
                case INSIDE_PROCESSOR_PATH_start:
                    // nothing to do here. This is consumed again by the AnnotationProcessorManager
                    mode = DEFAULT;
                    continue;
                case INSIDE_PROCESSOR_start:
                    // nothing to do here. This is consumed again by the AnnotationProcessorManager
                    mode = DEFAULT;
                    continue;
                case INSIDE_S_start:
                    // nothing to do here. This is consumed again by the AnnotationProcessorManager
                    mode = DEFAULT;
                    continue;
                case INSIDE_CLASS_NAMES:
                    tokenizer = new //$NON-NLS-1$
                    StringTokenizer(//$NON-NLS-1$
                    currentArg, //$NON-NLS-1$
                    ",");
                    if (this.classNames == null) {
                        this.classNames = new String[DEFAULT_SIZE_CLASSPATH];
                    }
                    while (tokenizer.hasMoreTokens()) {
                        if (this.classNames.length == classCount) {
                            // resize
                            System.arraycopy(this.classNames, 0, (this.classNames = new String[classCount * 2]), 0, classCount);
                        }
                        this.classNames[classCount++] = tokenizer.nextToken();
                    }
                    mode = DEFAULT;
                    continue;
                case INSIDE_WARNINGS_PROPERTIES:
                    initializeWarnings(currentArg);
                    mode = DEFAULT;
                    continue;
                case INSIDE_ANNOTATIONPATH_start:
                    mode = DEFAULT;
                    if (currentArg.isEmpty() || currentArg.charAt(0) == '-')
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.missingAnnotationPath", //$NON-NLS-1$
                        currentArg));
                    if (ANNOTATION_SOURCE_CLASSPATH.equals(currentArg)) {
                        this.annotationsFromClasspath = true;
                    } else {
                        if (this.annotationPaths == null)
                            this.annotationPaths = new ArrayList<String>();
                        StringTokenizer tokens = new StringTokenizer(currentArg, File.pathSeparator);
                        while (tokens.hasMoreTokens()) this.annotationPaths.add(tokens.nextToken());
                    }
                    continue;
            }
            // default is input directory, if no custom destination path exists
            if (customDestinationPath == null) {
                if (File.separatorChar != '/') {
                    currentArg = currentArg.replace('/', File.separatorChar);
                }
                if (//$NON-NLS-1$
                currentArg.endsWith("[-d")) {
                    currentSourceDirectory = currentArg.substring(0, currentArg.length() - 3);
                    mode = INSIDE_SOURCE_DIRECTORY_DESTINATION_PATH;
                    continue;
                }
                currentSourceDirectory = currentArg;
            }
            File dir = new File(currentSourceDirectory);
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException(//$NON-NLS-1$
                this.bind("configure.unrecognizedOption", currentSourceDirectory));
            }
            String[] result = FileFinder.find(dir, SuffixConstants.SUFFIX_STRING_JAVA);
            if (NONE.equals(customDestinationPath)) {
                // ensure == comparison
                customDestinationPath = NONE;
            }
            if (this.filenames != null) {
                // some source files were specified explicitly
                int length = result.length;
                System.arraycopy(this.filenames, 0, (this.filenames = new String[length + filesCount]), 0, filesCount);
                System.arraycopy(this.encodings, 0, (this.encodings = new String[length + filesCount]), 0, filesCount);
                System.arraycopy(this.destinationPaths, 0, (this.destinationPaths = new String[length + filesCount]), 0, filesCount);
                System.arraycopy(result, 0, this.filenames, filesCount, length);
                for (int i = 0; i < length; i++) {
                    this.encodings[filesCount + i] = customEncoding;
                    this.destinationPaths[filesCount + i] = customDestinationPath;
                }
                filesCount += length;
                customEncoding = null;
                customDestinationPath = null;
                currentSourceDirectory = null;
            } else {
                this.filenames = result;
                filesCount = this.filenames.length;
                this.encodings = new String[filesCount];
                this.destinationPaths = new String[filesCount];
                for (int i = 0; i < filesCount; i++) {
                    this.encodings[i] = customEncoding;
                    this.destinationPaths[i] = customDestinationPath;
                }
                customEncoding = null;
                customDestinationPath = null;
                currentSourceDirectory = null;
            }
            mode = DEFAULT;
            continue;
        }
        // javadoc is not enabled
        if (this.enableJavadocOn) {
            this.options.put(CompilerOptions.OPTION_DocCommentSupport, CompilerOptions.ENABLED);
        } else if (this.warnJavadocOn || this.warnAllJavadocOn) {
            this.options.put(CompilerOptions.OPTION_DocCommentSupport, CompilerOptions.ENABLED);
            // override defaults: references that are embedded in javadoc are ignored
            // from the perspective of parameters and thrown exceptions usage
            this.options.put(CompilerOptions.OPTION_ReportUnusedParameterIncludeDocCommentReference, CompilerOptions.DISABLED);
            this.options.put(CompilerOptions.OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference, CompilerOptions.DISABLED);
        }
        // configure warnings for javadoc contents
        if (this.warnJavadocOn) {
            this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTags, CompilerOptions.ENABLED);
            this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsDeprecatedRef, CompilerOptions.ENABLED);
            this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsNotVisibleRef, CompilerOptions.ENABLED);
            this.options.put(CompilerOptions.OPTION_ReportMissingJavadocTagsVisibility, CompilerOptions.PRIVATE);
        }
        if (printUsageRequired || (filesCount == 0 && classCount == 0)) {
            if (usageSection == null) {
                // default
                printUsage();
            } else {
                printUsage(usageSection);
            }
            this.proceed = false;
            return;
        }
        if (this.log != null) {
            this.logger.setLog(this.log);
        } else {
            this.showProgress = false;
        }
        this.logger.logVersion(printVersionRequired);
        validateOptions(didSpecifyCompliance);
        // see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=185768
        if (!didSpecifyDisabledAnnotationProcessing && CompilerOptions.versionToJdkLevel(this.options.get(CompilerOptions.OPTION_Compliance)) >= ClassFileConstants.JDK1_6) {
            this.options.put(CompilerOptions.OPTION_Process_Annotations, CompilerOptions.ENABLED);
        }
        this.logger.logCommandLineArguments(newCommandLineArgs);
        this.logger.logOptions(this.options);
        if (this.maxRepetition == 0) {
            this.maxRepetition = 1;
        }
        if (this.maxRepetition >= 3 && (this.timing & TIMING_ENABLED) != 0) {
            this.compilerStats = new CompilerStats[this.maxRepetition];
        }
        if (filesCount != 0) {
            System.arraycopy(this.filenames, 0, (this.filenames = new String[filesCount]), 0, filesCount);
        }
        if (classCount != 0) {
            System.arraycopy(this.classNames, 0, (this.classNames = new String[classCount]), 0, classCount);
        }
        setPaths(bootclasspaths, sourcepathClasspathArg, sourcepathClasspaths, classpaths, extdirsClasspaths, endorsedDirClasspaths, customEncoding);
        if (specifiedEncodings != null && specifiedEncodings.size() > 1) {
            this.logger.logWarning(//$NON-NLS-1$
            this.bind(//$NON-NLS-1$
            "configure.multipleencodings", this.options.get(CompilerOptions.OPTION_Encoding), getAllEncodings(specifiedEncodings)));
        }
        if (this.pendingErrors != null) {
            for (Iterator iterator = this.pendingErrors.iterator(); iterator.hasNext(); ) {
                String message = (String) iterator.next();
                this.logger.logPendingError(message);
            }
            this.pendingErrors = null;
        }
    }

    private static char[][] decodeIgnoreOptionalProblemsFromFolders(String folders) {
        StringTokenizer tokenizer = new StringTokenizer(folders, File.pathSeparator);
        char[][] result = new char[tokenizer.countTokens()][];
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            String fileName = tokenizer.nextToken();
            // relative folder names are created relative to the current user dir
            File file = new File(fileName);
            if (file.exists()) {
                // if the file exists, we should try to use its canonical path
                try {
                    result[count++] = file.getCanonicalPath().toCharArray();
                } catch (IOException e) {
                    result[count++] = fileName.toCharArray();
                }
            } else {
                // if the file does not exist, use the name that was specified
                result[count++] = fileName.toCharArray();
            }
        }
        return result;
    }

    private static String getAllEncodings(Set encodings) {
        int size = encodings.size();
        String[] allEncodings = new String[size];
        encodings.toArray(allEncodings);
        Arrays.sort(allEncodings);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                //$NON-NLS-1$
                buffer.append(", ");
            }
            buffer.append(allEncodings[i]);
        }
        return String.valueOf(buffer);
    }

    private void initializeWarnings(String propertiesFile) {
        File file = new File(propertiesFile);
        if (!file.exists()) {
            //$NON-NLS-1$
            throw new IllegalArgumentException(this.bind("configure.missingwarningspropertiesfile", propertiesFile));
        }
        BufferedInputStream stream = null;
        Properties properties = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(propertiesFile));
            properties = new Properties();
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(this.bind("configure.ioexceptionwarningspropertiesfile", propertiesFile));
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
        for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            final String key = entry.getKey().toString();
            if (//$NON-NLS-1$
            key.startsWith("org.eclipse.jdt.core.compiler.")) {
                this.options.put(key, entry.getValue().toString());
            }
        }
        // when using a properties file mimic relevant defaults from JavaCorePreferenceInitializer:
        if (!properties.containsKey(CompilerOptions.OPTION_LocalVariableAttribute)) {
            this.options.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
        }
        if (!properties.containsKey(CompilerOptions.OPTION_PreserveUnusedLocal)) {
            this.options.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
        }
        if (!properties.containsKey(CompilerOptions.OPTION_DocCommentSupport)) {
            this.options.put(CompilerOptions.OPTION_DocCommentSupport, CompilerOptions.ENABLED);
        }
        if (!properties.containsKey(CompilerOptions.OPTION_ReportForbiddenReference)) {
            this.options.put(CompilerOptions.OPTION_ReportForbiddenReference, CompilerOptions.ERROR);
        }
    }

    protected void enableAll(int severity) {
        String newValue = null;
        switch(severity) {
            case ProblemSeverities.Error:
                newValue = CompilerOptions.ERROR;
                break;
            case ProblemSeverities.Warning:
                newValue = CompilerOptions.WARNING;
                break;
        }
        Map.Entry<String, String>[] entries = this.options.entrySet().toArray(new Map.Entry[this.options.size()]);
        for (int i = 0, max = entries.length; i < max; i++) {
            Map.Entry<String, String> entry = entries[i];
            if (entry.getValue().equals(CompilerOptions.IGNORE)) {
                this.options.put(entry.getKey(), newValue);
            }
        }
        this.options.put(CompilerOptions.OPTION_TaskTags, Util.EMPTY_STRING);
    }

    protected void disableAll(int severity) {
        String checkedValue = null;
        switch(severity) {
            case ProblemSeverities.Error:
                checkedValue = CompilerOptions.ERROR;
                break;
            case ProblemSeverities.Warning:
                checkedValue = CompilerOptions.WARNING;
                break;
        }
        Object[] entries = this.options.entrySet().toArray();
        for (int i = 0, max = entries.length; i < max; i++) {
            Map.Entry entry = (Map.Entry) entries[i];
            if (!(entry.getKey() instanceof String))
                continue;
            if (!(entry.getValue() instanceof String))
                continue;
            if (((String) entry.getValue()).equals(checkedValue)) {
                this.options.put((String) entry.getKey(), CompilerOptions.IGNORE);
            }
        }
    }

    public String extractDestinationPathFromSourceFile(CompilationResult result) {
        ICompilationUnit compilationUnit = result.compilationUnit;
        if (compilationUnit != null) {
            char[] fileName = compilationUnit.getFileName();
            int lastIndex = CharOperation.lastIndexOf(java.io.File.separatorChar, fileName);
            if (lastIndex != -1) {
                final String outputPathName = new String(fileName, 0, lastIndex);
                final File output = new File(outputPathName);
                if (output.exists() && output.isDirectory()) {
                    return outputPathName;
                }
            }
        }
        //$NON-NLS-1$
        return System.getProperty("user.dir");
    }

    /*
 * Answer the component to which will be handed back compilation results from the compiler
 */
    public ICompilerRequestor getBatchRequestor() {
        return new BatchCompilerRequestor(this);
    }

    /*
 *  Build the set of compilation source units
 */
    public CompilationUnit[] getCompilationUnits() {
        int fileCount = this.filenames.length;
        CompilationUnit[] units = new CompilationUnit[fileCount];
        HashtableOfObject knownFileNames = new HashtableOfObject(fileCount);
        String defaultEncoding = this.options.get(CompilerOptions.OPTION_Encoding);
        if (Util.EMPTY_STRING.equals(defaultEncoding))
            defaultEncoding = null;
        for (int i = 0; i < fileCount; i++) {
            char[] charName = this.filenames[i].toCharArray();
            if (knownFileNames.get(charName) != null)
                //$NON-NLS-1$
                throw new IllegalArgumentException(this.bind("unit.more", this.filenames[i]));
            knownFileNames.put(charName, charName);
            File file = new File(this.filenames[i]);
            if (!file.exists())
                //$NON-NLS-1$
                throw new IllegalArgumentException(this.bind("unit.missing", this.filenames[i]));
            String encoding = this.encodings[i];
            if (encoding == null)
                encoding = defaultEncoding;
            String fileName;
            try {
                fileName = file.getCanonicalPath();
            } catch (IOException e) {
                fileName = this.filenames[i];
            }
            units[i] = new CompilationUnit(null, fileName, encoding, this.destinationPaths[i], shouldIgnoreOptionalProblems(this.ignoreOptionalProblemsFromFolders, fileName.toCharArray()));
        }
        return units;
    }

    /*
 *  Low-level API performing the actual compilation
 */
    public IErrorHandlingPolicy getHandlingPolicy() {
        // passes the initial set of files to the batch oracle (to avoid finding more than once the same units when case insensitive match)
        return new IErrorHandlingPolicy() {

            public boolean proceedOnErrors() {
                // stop if there are some errors
                return Main.this.proceedOnError;
            }

            public boolean stopOnFirstError() {
                return false;
            }

            public boolean ignoreAllErrors() {
                return false;
            }
        };
    }

    /*
 * External API
 */
    public File getJavaHome() {
        if (!this.javaHomeChecked) {
            this.javaHomeChecked = true;
            this.javaHomeCache = Util.getJavaHome();
        }
        return this.javaHomeCache;
    }

    public FileSystem getLibraryAccess() {
        return new FileSystem(this.checkedClasspaths, this.filenames, this.annotationsFromClasspath && CompilerOptions.ENABLED.equals(this.options.get(CompilerOptions.OPTION_AnnotationBasedNullAnalysis)));
    }

    /*
 *  Low-level API performing the actual compilation
 */
    public IProblemFactory getProblemFactory() {
        return new DefaultProblemFactory(this.compilerLocale);
    }

    /*
 * External API
 */
    protected ArrayList handleBootclasspath(ArrayList bootclasspaths, String customEncoding) {
        final int bootclasspathsSize;
        if ((bootclasspaths != null) && ((bootclasspathsSize = bootclasspaths.size()) != 0)) {
            String[] paths = new String[bootclasspathsSize];
            bootclasspaths.toArray(paths);
            bootclasspaths.clear();
            for (int i = 0; i < bootclasspathsSize; i++) {
                processPathEntries(DEFAULT_SIZE_CLASSPATH, bootclasspaths, paths[i], customEncoding, false, true);
            }
        } else {
            bootclasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
            try {
                Util.collectRunningVMBootclasspath(bootclasspaths);
            } catch (IllegalStateException e) {
                this.logger.logWrongJDK();
                this.proceed = false;
                return null;
            }
        }
        return bootclasspaths;
    }

    /*
 * External API
 */
    protected ArrayList handleClasspath(ArrayList classpaths, String customEncoding) {
        final int classpathsSize;
        if ((classpaths != null) && ((classpathsSize = classpaths.size()) != 0)) {
            String[] paths = new String[classpathsSize];
            classpaths.toArray(paths);
            classpaths.clear();
            for (int i = 0; i < classpathsSize; i++) {
                processPathEntries(DEFAULT_SIZE_CLASSPATH, classpaths, paths[i], customEncoding, false, true);
            }
        } else {
            // no user classpath specified.
            classpaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
            //$NON-NLS-1$
            String classProp = System.getProperty("java.class.path");
            if ((classProp == null) || (classProp.length() == 0)) {
                //$NON-NLS-1$
                addPendingErrors(this.bind("configure.noClasspath"));
                //$NON-NLS-1$
                final Classpath classpath = FileSystem.getClasspath(System.getProperty("user.dir"), customEncoding, null, this.options);
                if (classpath != null) {
                    classpaths.add(classpath);
                }
            } else {
                StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
                String token;
                while (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
                    FileSystem.Classpath currentClasspath = FileSystem.getClasspath(token, customEncoding, null, this.options);
                    if (currentClasspath != null) {
                        classpaths.add(currentClasspath);
                    } else if (token.length() != 0) {
                        addPendingErrors(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.incorrectClasspath", //$NON-NLS-1$
                        token));
                    }
                }
            }
        }
        ArrayList result = new ArrayList();
        HashMap knownNames = new HashMap();
        FileSystem.ClasspathSectionProblemReporter problemReporter = new FileSystem.ClasspathSectionProblemReporter() {

            public void invalidClasspathSection(String jarFilePath) {
                addPendingErrors(//$NON-NLS-1$
                bind("configure.invalidClasspathSection", jarFilePath));
            }

            public void multipleClasspathSections(String jarFilePath) {
                addPendingErrors(//$NON-NLS-1$
                bind("configure.multipleClasspathSections", jarFilePath));
            }
        };
        while (!classpaths.isEmpty()) {
            Classpath current = (Classpath) classpaths.remove(0);
            String currentPath = current.getPath();
            if (knownNames.get(currentPath) == null) {
                knownNames.put(currentPath, current);
                result.add(current);
                List linkedJars = current.fetchLinkedJars(problemReporter);
                if (linkedJars != null) {
                    classpaths.addAll(0, linkedJars);
                }
            }
        }
        return result;
    }

    /*
 * External API
 */
    protected ArrayList handleEndorseddirs(ArrayList endorsedDirClasspaths) {
        final File javaHome = getJavaHome();
        /*
	 * Feed endorsedDirClasspath according to:
	 * - -endorseddirs first if present;
	 * - else java.endorsed.dirs if defined;
	 * - else default extensions directory for the platform. (/lib/endorsed)
	 */
        if (endorsedDirClasspaths == null) {
            endorsedDirClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
            //$NON-NLS-1$
            String endorsedDirsStr = System.getProperty("java.endorsed.dirs");
            if (endorsedDirsStr == null) {
                if (javaHome != null) {
                    //$NON-NLS-1$
                    endorsedDirClasspaths.add(//$NON-NLS-1$
                    javaHome.getAbsolutePath() + "/lib/endorsed");
                }
            } else {
                StringTokenizer tokenizer = new StringTokenizer(endorsedDirsStr, File.pathSeparator);
                while (tokenizer.hasMoreTokens()) {
                    endorsedDirClasspaths.add(tokenizer.nextToken());
                }
            }
        }
        /*
	 * Feed extdirsClasspath with the entries found into the directories listed by
	 * extdirsNames.
	 */
        if (endorsedDirClasspaths.size() != 0) {
            File[] directoriesToCheck = new File[endorsedDirClasspaths.size()];
            for (int i = 0; i < directoriesToCheck.length; i++) directoriesToCheck[i] = new File((String) endorsedDirClasspaths.get(i));
            endorsedDirClasspaths.clear();
            File[][] endorsedDirsJars = getLibrariesFiles(directoriesToCheck);
            if (endorsedDirsJars != null) {
                for (int i = 0, max = endorsedDirsJars.length; i < max; i++) {
                    File[] current = endorsedDirsJars[i];
                    if (current != null) {
                        for (int j = 0, max2 = current.length; j < max2; j++) {
                            FileSystem.Classpath classpath = FileSystem.getClasspath(current[j].getAbsolutePath(), null, null, this.options);
                            if (classpath != null) {
                                endorsedDirClasspaths.add(classpath);
                            }
                        }
                    } else if (directoriesToCheck[i].isFile()) {
                        addPendingErrors(this.bind("configure.incorrectEndorsedDirsEntry", directoriesToCheck[i].getAbsolutePath()));
                    }
                }
            }
        }
        return endorsedDirClasspaths;
    }

    /*
 * External API
 * Handle extdirs processing
 */
    protected ArrayList handleExtdirs(ArrayList extdirsClasspaths) {
        final File javaHome = getJavaHome();
        /*
	 * Feed extDirClasspath according to:
	 * - -extdirs first if present;
	 * - else java.ext.dirs if defined;
	 * - else default extensions directory for the platform.
	 */
        if (extdirsClasspaths == null) {
            extdirsClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
            //$NON-NLS-1$
            String extdirsStr = System.getProperty("java.ext.dirs");
            if (extdirsStr == null) {
                //$NON-NLS-1$
                extdirsClasspaths.add(javaHome.getAbsolutePath() + "/lib/ext");
            } else {
                StringTokenizer tokenizer = new StringTokenizer(extdirsStr, File.pathSeparator);
                while (tokenizer.hasMoreTokens()) extdirsClasspaths.add(tokenizer.nextToken());
            }
        }
        /*
	 * Feed extdirsClasspath with the entries found into the directories listed by
	 * extdirsNames.
	 */
        if (extdirsClasspaths.size() != 0) {
            File[] directoriesToCheck = new File[extdirsClasspaths.size()];
            for (int i = 0; i < directoriesToCheck.length; i++) directoriesToCheck[i] = new File((String) extdirsClasspaths.get(i));
            extdirsClasspaths.clear();
            File[][] extdirsJars = getLibrariesFiles(directoriesToCheck);
            if (extdirsJars != null) {
                for (int i = 0, max = extdirsJars.length; i < max; i++) {
                    File[] current = extdirsJars[i];
                    if (current != null) {
                        for (int j = 0, max2 = current.length; j < max2; j++) {
                            FileSystem.Classpath classpath = FileSystem.getClasspath(current[j].getAbsolutePath(), null, null, this.options);
                            if (classpath != null) {
                                extdirsClasspaths.add(classpath);
                            }
                        }
                    } else if (directoriesToCheck[i].isFile()) {
                        addPendingErrors(this.bind("configure.incorrectExtDirsEntry", directoriesToCheck[i].getAbsolutePath()));
                    }
                }
            }
        }
        return extdirsClasspaths;
    }

    /*
 * External API
 * Handle a single warning token.
*/
    protected void handleWarningToken(String token, boolean isEnabling) {
        handleErrorOrWarningToken(token, isEnabling, ProblemSeverities.Warning);
    }

    protected void handleErrorToken(String token, boolean isEnabling) {
        handleErrorOrWarningToken(token, isEnabling, ProblemSeverities.Error);
    }

    private void setSeverity(String compilerOptions, int severity, boolean isEnabling) {
        if (isEnabling) {
            switch(severity) {
                case ProblemSeverities.Error:
                    this.options.put(compilerOptions, CompilerOptions.ERROR);
                    break;
                case ProblemSeverities.Warning:
                    this.options.put(compilerOptions, CompilerOptions.WARNING);
                    break;
                default:
                    this.options.put(compilerOptions, CompilerOptions.IGNORE);
            }
        } else {
            switch(severity) {
                case ProblemSeverities.Error:
                    String currentValue = this.options.get(compilerOptions);
                    if (CompilerOptions.ERROR.equals(currentValue)) {
                        this.options.put(compilerOptions, CompilerOptions.IGNORE);
                    }
                    break;
                case ProblemSeverities.Warning:
                    currentValue = this.options.get(compilerOptions);
                    if (CompilerOptions.WARNING.equals(currentValue)) {
                        this.options.put(compilerOptions, CompilerOptions.IGNORE);
                    }
                    break;
                default:
                    this.options.put(compilerOptions, CompilerOptions.IGNORE);
            }
        }
    }

    private void handleErrorOrWarningToken(String token, boolean isEnabling, int severity) {
        if (token.length() == 0)
            return;
        switch(token.charAt(0)) {
            case 'a':
                if (//$NON-NLS-1$
                token.equals("allDeprecation")) {
                    setSeverity(CompilerOptions.OPTION_ReportDeprecation, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    this.options.put(CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("allJavadoc")) {
                    this.warnAllJavadocOn = this.warnJavadocOn = isEnabling;
                    setSeverity(CompilerOptions.OPTION_ReportInvalidJavadoc, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportMissingJavadocTags, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportMissingJavadocComments, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("assertIdentifier")) {
                    setSeverity(CompilerOptions.OPTION_ReportAssertIdentifier, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("allDeadCode")) {
                    setSeverity(CompilerOptions.OPTION_ReportDeadCode, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportDeadCodeInTrivialIfStatement, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("allOver-ann")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingOverrideAnnotation, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("all-static-method")) {
                    setSeverity(CompilerOptions.OPTION_ReportMethodCanBeStatic, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportMethodCanBePotentiallyStatic, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("all")) {
                    if (isEnabling) {
                        enableAll(severity);
                    } else {
                        disableAll(severity);
                    }
                    return;
                }
                break;
            case 'b':
                if (//$NON-NLS-1$
                token.equals("boxing")) {
                    setSeverity(CompilerOptions.OPTION_ReportAutoboxing, severity, isEnabling);
                    return;
                }
                break;
            case 'c':
                if (//$NON-NLS-1$
                token.equals("constructorName")) {
                    setSeverity(CompilerOptions.OPTION_ReportMethodWithConstructorName, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("conditionAssign")) {
                    setSeverity(CompilerOptions.OPTION_ReportPossibleAccidentalBooleanAssignment, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("compareIdentical")) {
                    setSeverity(CompilerOptions.OPTION_ReportComparingIdentical, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("charConcat")) /*|| token.equals("noImplicitStringConversion")/*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportNoImplicitStringConversion, severity, isEnabling);
                    return;
                }
                break;
            case 'd':
                if (//$NON-NLS-1$
                token.equals("deprecation")) {
                    setSeverity(CompilerOptions.OPTION_ReportDeprecation, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode, CompilerOptions.DISABLED);
                    this.options.put(CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod, CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("dep-ann")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingDeprecatedAnnotation, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("discouraged")) {
                    setSeverity(CompilerOptions.OPTION_ReportDiscouragedReference, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("deadCode")) {
                    setSeverity(CompilerOptions.OPTION_ReportDeadCode, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportDeadCodeInTrivialIfStatement, CompilerOptions.DISABLED);
                    return;
                }
                break;
            case 'e':
                if (//$NON-NLS-1$
                token.equals("enumSwitch")) {
                    setSeverity(CompilerOptions.OPTION_ReportIncompleteEnumSwitch, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("enumSwitchPedantic")) {
                    if (isEnabling) {
                        switch(severity) {
                            case ProblemSeverities.Error:
                                setSeverity(CompilerOptions.OPTION_ReportIncompleteEnumSwitch, severity, isEnabling);
                                break;
                            case ProblemSeverities.Warning:
                                if (CompilerOptions.IGNORE.equals(this.options.get(CompilerOptions.OPTION_ReportIncompleteEnumSwitch))) {
                                    setSeverity(CompilerOptions.OPTION_ReportIncompleteEnumSwitch, severity, isEnabling);
                                }
                                break;
                            // no severity update
                            default:
                        }
                    }
                    this.options.put(CompilerOptions.OPTION_ReportMissingEnumCaseDespiteDefault, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("emptyBlock")) {
                    setSeverity(CompilerOptions.OPTION_ReportUndocumentedEmptyBlock, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("enumIdentifier")) {
                    setSeverity(CompilerOptions.OPTION_ReportEnumIdentifier, severity, isEnabling);
                    return;
                }
                break;
            case 'f':
                if (//$NON-NLS-1$
                token.equals("fieldHiding")) {
                    setSeverity(CompilerOptions.OPTION_ReportFieldHiding, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("finalBound")) {
                    setSeverity(CompilerOptions.OPTION_ReportFinalParameterBound, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("finally")) {
                    setSeverity(CompilerOptions.OPTION_ReportFinallyBlockNotCompletingNormally, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("forbidden")) {
                    setSeverity(CompilerOptions.OPTION_ReportForbiddenReference, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("fallthrough")) {
                    setSeverity(CompilerOptions.OPTION_ReportFallthroughCase, severity, isEnabling);
                    return;
                }
                break;
            case 'h':
                if (//$NON-NLS-1$
                token.equals("hiding")) {
                    setSeverity(CompilerOptions.OPTION_ReportHiddenCatchBlock, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportLocalVariableHiding, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportFieldHiding, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportTypeParameterHiding, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("hashCode")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingHashCodeMethod, severity, isEnabling);
                    return;
                }
                break;
            case 'i':
                if (//$NON-NLS-1$
                token.equals("indirectStatic")) {
                    setSeverity(CompilerOptions.OPTION_ReportIndirectStaticAccess, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("inheritNullAnnot")) {
                    this.options.put(CompilerOptions.OPTION_InheritNullAnnotations, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("intfNonInherited") || token.equals("interfaceNonInherited")) /*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportIncompatibleNonInheritedInterfaceMethod, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("intfAnnotation")) {
                    setSeverity(CompilerOptions.OPTION_ReportAnnotationSuperInterface, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("intfRedundant")) /*|| token.equals("redundantSuperinterface")*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportRedundantSuperinterface, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("includeAssertNull")) {
                    this.options.put(CompilerOptions.OPTION_IncludeNullInfoFromAsserts, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("invalidJavadoc")) {
                    setSeverity(CompilerOptions.OPTION_ReportInvalidJavadoc, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTags, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsDeprecatedRef, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsNotVisibleRef, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    if (isEnabling) {
                        this.options.put(CompilerOptions.OPTION_DocCommentSupport, CompilerOptions.ENABLED);
                        this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsVisibility, CompilerOptions.PRIVATE);
                    }
                    return;
                } else if (//$NON-NLS-1$
                token.equals("invalidJavadocTag")) {
                    this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTags, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("invalidJavadocTagDep")) {
                    this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsDeprecatedRef, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("invalidJavadocTagNotVisible")) {
                    this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsNotVisibleRef, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.startsWith("invalidJavadocTagVisibility")) {
                    int start = token.indexOf('(');
                    int end = token.indexOf(')');
                    String visibility = null;
                    if (isEnabling && start >= 0 && end >= 0 && start < end) {
                        visibility = token.substring(start + 1, end).trim();
                    }
                    if (visibility != null && visibility.equals(CompilerOptions.PUBLIC) || visibility.equals(CompilerOptions.PRIVATE) || visibility.equals(CompilerOptions.PROTECTED) || visibility.equals(CompilerOptions.DEFAULT)) {
                        this.options.put(CompilerOptions.OPTION_ReportInvalidJavadocTagsVisibility, visibility);
                        return;
                    } else {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.invalidJavadocTagVisibility", //$NON-NLS-1$
                        token));
                    }
                }
                break;
            case 'j':
                if (//$NON-NLS-1$
                token.equals("javadoc")) {
                    this.warnJavadocOn = isEnabling;
                    setSeverity(CompilerOptions.OPTION_ReportInvalidJavadoc, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportMissingJavadocTags, severity, isEnabling);
                    return;
                }
                break;
            case 'l':
                if (//$NON-NLS-1$
                token.equals("localHiding")) {
                    setSeverity(CompilerOptions.OPTION_ReportLocalVariableHiding, severity, isEnabling);
                    return;
                }
                break;
            case 'm':
                if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("maskedCatchBlock") || token.equals("maskedCatchBlocks")) /*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportHiddenCatchBlock, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("missingJavadocTags")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingJavadocTags, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportMissingJavadocTagsOverriding, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    this.options.put(CompilerOptions.OPTION_ReportMissingJavadocTagsMethodTypeParameters, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    if (isEnabling) {
                        this.options.put(CompilerOptions.OPTION_DocCommentSupport, CompilerOptions.ENABLED);
                        this.options.put(CompilerOptions.OPTION_ReportMissingJavadocTagsVisibility, CompilerOptions.PRIVATE);
                    }
                    return;
                } else if (//$NON-NLS-1$
                token.equals("missingJavadocTagsOverriding")) {
                    this.options.put(CompilerOptions.OPTION_ReportMissingJavadocTagsOverriding, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("missingJavadocTagsMethod")) {
                    this.options.put(CompilerOptions.OPTION_ReportMissingJavadocTagsMethodTypeParameters, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.startsWith("missingJavadocTagsVisibility")) {
                    int start = token.indexOf('(');
                    int end = token.indexOf(')');
                    String visibility = null;
                    if (isEnabling && start >= 0 && end >= 0 && start < end) {
                        visibility = token.substring(start + 1, end).trim();
                    }
                    if (visibility != null && visibility.equals(CompilerOptions.PUBLIC) || visibility.equals(CompilerOptions.PRIVATE) || visibility.equals(CompilerOptions.PROTECTED) || visibility.equals(CompilerOptions.DEFAULT)) {
                        this.options.put(CompilerOptions.OPTION_ReportMissingJavadocTagsVisibility, visibility);
                        return;
                    } else {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.missingJavadocTagsVisibility", //$NON-NLS-1$
                        token));
                    }
                } else if (//$NON-NLS-1$
                token.equals("missingJavadocComments")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingJavadocComments, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportMissingJavadocCommentsOverriding, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    if (isEnabling) {
                        this.options.put(CompilerOptions.OPTION_DocCommentSupport, CompilerOptions.ENABLED);
                        this.options.put(CompilerOptions.OPTION_ReportMissingJavadocCommentsVisibility, CompilerOptions.PRIVATE);
                    }
                    return;
                } else if (//$NON-NLS-1$
                token.equals("missingJavadocCommentsOverriding")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingJavadocComments, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportMissingJavadocCommentsOverriding, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.startsWith("missingJavadocCommentsVisibility")) {
                    int start = token.indexOf('(');
                    int end = token.indexOf(')');
                    String visibility = null;
                    if (isEnabling && start >= 0 && end >= 0 && start < end) {
                        visibility = token.substring(start + 1, end).trim();
                    }
                    if (visibility != null && visibility.equals(CompilerOptions.PUBLIC) || visibility.equals(CompilerOptions.PRIVATE) || visibility.equals(CompilerOptions.PROTECTED) || visibility.equals(CompilerOptions.DEFAULT)) {
                        this.options.put(CompilerOptions.OPTION_ReportMissingJavadocCommentsVisibility, visibility);
                        return;
                    } else {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.missingJavadocCommentsVisibility", //$NON-NLS-1$
                        token));
                    }
                }
                break;
            case 'n':
                if (//$NON-NLS-1$
                token.equals("nls")) {
                    setSeverity(CompilerOptions.OPTION_ReportNonExternalizedStringLiteral, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("noEffectAssign")) {
                    setSeverity(CompilerOptions.OPTION_ReportNoEffectAssignment, severity, isEnabling);
                    return;
                } else /*token.equals("charConcat") ||*/
                if (//$NON-NLS-1$
                token.equals("noImplicitStringConversion")) /*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportNoImplicitStringConversion, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("null")) {
                    setSeverity(CompilerOptions.OPTION_ReportNullReference, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportPotentialNullReference, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportRedundantNullCheck, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("nullDereference")) {
                    setSeverity(CompilerOptions.OPTION_ReportNullReference, severity, isEnabling);
                    if (!isEnabling) {
                        setSeverity(CompilerOptions.OPTION_ReportPotentialNullReference, ProblemSeverities.Ignore, isEnabling);
                        setSeverity(CompilerOptions.OPTION_ReportRedundantNullCheck, ProblemSeverities.Ignore, isEnabling);
                    }
                    return;
                } else if (//$NON-NLS-1$
                token.equals("nullAnnotConflict")) {
                    setSeverity(CompilerOptions.OPTION_ReportNullAnnotationInferenceConflict, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("nullAnnotRedundant")) {
                    setSeverity(CompilerOptions.OPTION_ReportRedundantNullAnnotation, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.startsWith("nullAnnot")) {
                    String annotationNames = Util.EMPTY_STRING;
                    int start = token.indexOf('(');
                    int end = token.indexOf(')');
                    String nonNullAnnotName = null, nullableAnnotName = null, nonNullByDefaultAnnotName = null;
                    if (isEnabling && start >= 0 && end >= 0 && start < end) {
                        boolean isPrimarySet = !this.primaryNullAnnotationsSeen;
                        annotationNames = token.substring(start + 1, end).trim();
                        int separator1 = annotationNames.indexOf('|');
                        if (separator1 == -1)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidNullAnnot", //$NON-NLS-1$
                            token));
                        nullableAnnotName = annotationNames.substring(0, separator1).trim();
                        if (isPrimarySet && nullableAnnotName.length() == 0)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidNullAnnot", //$NON-NLS-1$
                            token));
                        int separator2 = annotationNames.indexOf('|', separator1 + 1);
                        if (separator2 == -1)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidNullAnnot", //$NON-NLS-1$
                            token));
                        nonNullAnnotName = annotationNames.substring(separator1 + 1, separator2).trim();
                        if (isPrimarySet && nonNullAnnotName.length() == 0)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidNullAnnot", //$NON-NLS-1$
                            token));
                        nonNullByDefaultAnnotName = annotationNames.substring(separator2 + 1).trim();
                        if (isPrimarySet && nonNullByDefaultAnnotName.length() == 0)
                            throw new IllegalArgumentException(//$NON-NLS-1$
                            this.bind(//$NON-NLS-1$
                            "configure.invalidNullAnnot", //$NON-NLS-1$
                            token));
                        if (isPrimarySet) {
                            this.primaryNullAnnotationsSeen = true;
                            this.options.put(CompilerOptions.OPTION_NullableAnnotationName, nullableAnnotName);
                            this.options.put(CompilerOptions.OPTION_NonNullAnnotationName, nonNullAnnotName);
                            this.options.put(CompilerOptions.OPTION_NonNullByDefaultAnnotationName, nonNullByDefaultAnnotName);
                        } else {
                            if (nullableAnnotName.length() > 0) {
                                String nullableList = this.options.get(CompilerOptions.OPTION_NullableAnnotationSecondaryNames);
                                nullableList = nullableList.isEmpty() ? nullableAnnotName : nullableList + ',' + nullableAnnotName;
                                this.options.put(CompilerOptions.OPTION_NullableAnnotationSecondaryNames, nullableList);
                            }
                            if (nonNullAnnotName.length() > 0) {
                                String nonnullList = this.options.get(CompilerOptions.OPTION_NonNullAnnotationSecondaryNames);
                                nonnullList = nonnullList.isEmpty() ? nonNullAnnotName : nonnullList + ',' + nonNullAnnotName;
                                this.options.put(CompilerOptions.OPTION_NonNullAnnotationSecondaryNames, nonnullList);
                            }
                            if (nonNullByDefaultAnnotName.length() > 0) {
                                String nnbdList = this.options.get(CompilerOptions.OPTION_NonNullByDefaultAnnotationSecondaryNames);
                                nnbdList = nnbdList.isEmpty() ? nonNullByDefaultAnnotName : nnbdList + ',' + nonNullByDefaultAnnotName;
                                this.options.put(CompilerOptions.OPTION_NonNullByDefaultAnnotationSecondaryNames, nnbdList);
                            }
                        }
                    }
                    this.options.put(CompilerOptions.OPTION_AnnotationBasedNullAnalysis, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    setSeverity(CompilerOptions.OPTION_ReportNullSpecViolation, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportNullAnnotationInferenceConflict, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportNullUncheckedConversion, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportRedundantNullAnnotation, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("nullUncheckedConversion")) {
                    setSeverity(CompilerOptions.OPTION_ReportNullUncheckedConversion, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("nonnullNotRepeated")) {
                    setSeverity(CompilerOptions.OPTION_ReportNonnullParameterAnnotationDropped, severity, isEnabling);
                    return;
                }
                break;
            case 'o':
                if (//$NON-NLS-1$ 
                token.equals("over-sync")) /*|| token.equals("syncOverride")*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportMissingSynchronizedOnInheritedMethod, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("over-ann")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingOverrideAnnotation, severity, isEnabling);
                    this.options.put(CompilerOptions.OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation, CompilerOptions.DISABLED);
                    return;
                }
                break;
            case 'p':
                if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("pkgDefaultMethod") || token.equals("packageDefaultMethod")) /*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportOverridingPackageDefaultMethod, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("paramAssign")) {
                    setSeverity(CompilerOptions.OPTION_ReportParameterAssignment, severity, isEnabling);
                    return;
                }
                break;
            case 'r':
                if (//$NON-NLS-1$
                token.equals("raw")) {
                    setSeverity(CompilerOptions.OPTION_ReportRawTypeReference, severity, isEnabling);
                    return;
                } else /*token.equals("intfRedundant") ||*/
                if (//$NON-NLS-1$
                token.equals("redundantSuperinterface")) {
                    setSeverity(CompilerOptions.OPTION_ReportRedundantSuperinterface, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("resource")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnclosedCloseable, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportPotentiallyUnclosedCloseable, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportExplicitlyClosedAutoCloseable, severity, isEnabling);
                    return;
                }
                break;
            case 's':
                if (//$NON-NLS-1$
                token.equals("specialParamHiding")) {
                    this.options.put(CompilerOptions.OPTION_ReportSpecialParameterHidingField, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("syntheticAccess") || token.equals("synthetic-access")) {
                    setSeverity(CompilerOptions.OPTION_ReportSyntheticAccessEmulation, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("staticReceiver")) {
                    setSeverity(CompilerOptions.OPTION_ReportNonStaticAccessToStatic, severity, isEnabling);
                    return;
                } else /*token.equals("over-sync") ||*/
                if (//$NON-NLS-1$ 
                token.equals("syncOverride")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingSynchronizedOnInheritedMethod, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("semicolon")) {
                    setSeverity(CompilerOptions.OPTION_ReportEmptyStatement, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("serial")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingSerialVersion, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("suppress")) {
                    switch(severity) {
                        case ProblemSeverities.Warning:
                            this.options.put(CompilerOptions.OPTION_SuppressWarnings, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                            this.options.put(CompilerOptions.OPTION_SuppressOptionalErrors, CompilerOptions.DISABLED);
                            break;
                        case ProblemSeverities.Error:
                            this.options.put(CompilerOptions.OPTION_SuppressWarnings, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                            this.options.put(CompilerOptions.OPTION_SuppressOptionalErrors, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    }
                    return;
                } else if (//$NON-NLS-1$
                token.equals("static-access")) {
                    setSeverity(CompilerOptions.OPTION_ReportNonStaticAccessToStatic, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportIndirectStaticAccess, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("super")) {
                    setSeverity(CompilerOptions.OPTION_ReportOverridingMethodWithoutSuperInvocation, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("static-method")) {
                    setSeverity(CompilerOptions.OPTION_ReportMethodCanBeStatic, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("switchDefault")) {
                    setSeverity(CompilerOptions.OPTION_ReportMissingDefaultCase, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("syntacticAnalysis")) {
                    this.options.put(CompilerOptions.OPTION_SyntacticNullAnalysisForFields, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                }
                break;
            case 't':
                if (//$NON-NLS-1$
                token.startsWith("tasks")) {
                    String taskTags = Util.EMPTY_STRING;
                    int start = token.indexOf('(');
                    int end = token.indexOf(')');
                    if (start >= 0 && end >= 0 && start < end) {
                        taskTags = token.substring(start + 1, end).trim();
                        taskTags = taskTags.replace('|', ',');
                    }
                    if (taskTags.length() == 0) {
                        throw new IllegalArgumentException(//$NON-NLS-1$
                        this.bind(//$NON-NLS-1$
                        "configure.invalidTaskTag", //$NON-NLS-1$
                        token));
                    }
                    this.options.put(CompilerOptions.OPTION_TaskTags, isEnabling ? taskTags : Util.EMPTY_STRING);
                    setSeverity(CompilerOptions.OPTION_ReportTasks, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("typeHiding")) {
                    setSeverity(CompilerOptions.OPTION_ReportTypeParameterHiding, severity, isEnabling);
                    return;
                }
                break;
            case 'u':
                if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("unusedLocal") || token.equals("unusedLocals")) /*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedLocal, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("unusedArgument") || token.equals("unusedArguments")) /*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedParameter, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedExceptionParam")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedExceptionParameter, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("unusedImport") || token.equals("unusedImports")) /*backward compatible*/
                {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedImport, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedAllocation")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedObjectAllocation, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedPrivate")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedPrivateMember, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedLabel")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedLabel, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("uselessTypeCheck")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnnecessaryTypeCheck, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("unchecked") || token.equals("unsafe")) {
                    setSeverity(CompilerOptions.OPTION_ReportUncheckedTypeOperation, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unnecessaryElse")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnnecessaryElse, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedThrown")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedDeclaredThrownException, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedThrownWhenOverriding")) {
                    this.options.put(CompilerOptions.OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedThrownIncludeDocComment")) {
                    this.options.put(CompilerOptions.OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedThrownExemptExceptionThrowable")) {
                    this.options.put(CompilerOptions.OPTION_ReportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                token.equals("unqualifiedField") || token.equals("unqualified-field-access")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnqualifiedFieldAccess, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unused")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedLocal, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedParameter, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedImport, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedPrivateMember, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedDeclaredThrownException, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedLabel, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedTypeArgumentsForMethodInvocation, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportRedundantSpecificationOfTypeArguments, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedTypeParameter, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedParam")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedParameter, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedTypeParameter")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedTypeParameter, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedParamIncludeDoc")) {
                    this.options.put(CompilerOptions.OPTION_ReportUnusedParameterIncludeDocCommentReference, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedParamOverriding")) {
                    this.options.put(CompilerOptions.OPTION_ReportUnusedParameterWhenOverridingConcrete, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedParamImplementing")) {
                    this.options.put(CompilerOptions.OPTION_ReportUnusedParameterWhenImplementingAbstract, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unusedTypeArgs")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnusedTypeArgumentsForMethodInvocation, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportRedundantSpecificationOfTypeArguments, severity, isEnabling);
                    return;
                } else if (//$NON-NLS-1$
                token.equals("unavoidableGenericProblems")) {
                    this.options.put(CompilerOptions.OPTION_ReportUnavoidableGenericTypeProblems, isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
                    return;
                }
                break;
            case 'v':
                if (//$NON-NLS-1$
                token.equals("varargsCast")) {
                    setSeverity(CompilerOptions.OPTION_ReportVarargsArgumentNeedCast, severity, isEnabling);
                    return;
                }
                break;
            case 'w':
                if (//$NON-NLS-1$
                token.equals("warningToken")) {
                    setSeverity(CompilerOptions.OPTION_ReportUnhandledWarningToken, severity, isEnabling);
                    setSeverity(CompilerOptions.OPTION_ReportUnusedWarningToken, severity, isEnabling);
                    return;
                }
                break;
        }
        String message = null;
        switch(severity) {
            case ProblemSeverities.Warning:
                //$NON-NLS-1$
                message = this.bind("configure.invalidWarning", token);
                break;
            case ProblemSeverities.Error:
                //$NON-NLS-1$
                message = this.bind("configure.invalidError", token);
        }
        addPendingErrors(message);
    }

    /**
 * @deprecated - use {@link #initialize(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. initialize(outWriter, errWriter, systemExit, null, null)
 */
    protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit) {
        this.initialize(outWriter, errWriter, systemExit, null, /* options */
        null);
    }

    /**
 * @deprecated - use {@link #initialize(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. initialize(outWriter, errWriter, systemExit, customDefaultOptions, null)
 */
    protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map customDefaultOptions) {
        this.initialize(outWriter, errWriter, systemExit, customDefaultOptions, null);
    }

    protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress) {
        this.logger = new Logger(this, outWriter, errWriter);
        this.proceed = true;
        this.out = outWriter;
        this.err = errWriter;
        this.systemExitWhenFinished = systemExit;
        this.options = new CompilerOptions().getMap();
        this.ignoreOptionalProblemsFromFolders = null;
        this.progress = compilationProgress;
        if (customDefaultOptions != null) {
            this.didSpecifySource = customDefaultOptions.get(CompilerOptions.OPTION_Source) != null;
            this.didSpecifyTarget = customDefaultOptions.get(CompilerOptions.OPTION_TargetPlatform) != null;
            for (Iterator<Map.Entry<String, String>> iter = customDefaultOptions.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, String> entry = iter.next();
                this.options.put(entry.getKey(), entry.getValue());
            }
        } else {
            this.didSpecifySource = false;
            this.didSpecifyTarget = false;
        }
        this.classNames = null;
    }

    protected void initializeAnnotationProcessorManager() {
        //$NON-NLS-1$
        String className = "org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager";
        try {
            Class c = Class.forName(className);
            AbstractAnnotationProcessorManager annotationManager = (AbstractAnnotationProcessorManager) c.newInstance();
            annotationManager.configure(this, this.expandedCommandLine);
            annotationManager.setErr(this.err);
            annotationManager.setOut(this.out);
            this.batchCompiler.annotationProcessorManager = annotationManager;
        } catch (ClassNotFoundException | InstantiationException e) {
            this.logger.logUnavaibleAPT(className);
            throw new org.eclipse.jdt.internal.compiler.problem.AbortCompilation();
        } catch (IllegalAccessException e) {
            throw new org.eclipse.jdt.internal.compiler.problem.AbortCompilation();
        } catch (UnsupportedClassVersionError e) {
            this.logger.logIncorrectVMVersionForAnnotationProcessing();
        }
    }

    private static boolean isParentOf(char[] folderName, char[] fileName) {
        if (folderName.length >= fileName.length) {
            return false;
        }
        if (fileName[folderName.length] != '\\' && fileName[folderName.length] != '/') {
            return false;
        }
        for (int i = folderName.length - 1; i >= 0; i--) {
            if (folderName[i] != fileName[i]) {
                return false;
            }
        }
        return true;
    }

    public void outputClassFiles(CompilationResult unitResult) {
        if (!((unitResult == null) || (unitResult.hasErrors() && !this.proceedOnError))) {
            ClassFile[] classFiles = unitResult.getClassFiles();
            String currentDestinationPath = null;
            boolean generateClasspathStructure = false;
            CompilationUnit compilationUnit = (CompilationUnit) unitResult.compilationUnit;
            if (compilationUnit.destinationPath == null) {
                if (this.destinationPath == null) {
                    currentDestinationPath = extractDestinationPathFromSourceFile(unitResult);
                } else if (this.destinationPath != NONE) {
                    currentDestinationPath = this.destinationPath;
                    generateClasspathStructure = true;
                }
            } else if (compilationUnit.destinationPath != NONE) {
                currentDestinationPath = compilationUnit.destinationPath;
                generateClasspathStructure = true;
            }
            if (currentDestinationPath != null) {
                for (int i = 0, fileCount = classFiles.length; i < fileCount; i++) {
                    ClassFile classFile = classFiles[i];
                    char[] filename = classFile.fileName();
                    int length = filename.length;
                    char[] relativeName = new char[length + 6];
                    System.arraycopy(filename, 0, relativeName, 0, length);
                    System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
                    CharOperation.replace(relativeName, '/', File.separatorChar);
                    String relativeStringName = new String(relativeName);
                    try {
                        if (this.compilerOptions.verbose)
                            this.out.println(Messages.bind(Messages.compilation_write, new String[] { String.valueOf(this.exportedClassFilesCounter + 1), relativeStringName }));
                        Util.writeToDisk(generateClasspathStructure, currentDestinationPath, relativeStringName, classFile);
                        this.logger.logClassFile(generateClasspathStructure, currentDestinationPath, relativeStringName);
                        this.exportedClassFilesCounter++;
                    } catch (IOException e) {
                        this.logger.logNoClassFileCreated(currentDestinationPath, relativeStringName, e);
                    }
                }
                this.batchCompiler.lookupEnvironment.releaseClassFiles(classFiles);
            }
        }
    }

    public void performCompilation() {
        this.startTime = System.currentTimeMillis();
        FileSystem environment = getLibraryAccess();
        this.compilerOptions = new CompilerOptions(this.options);
        this.compilerOptions.performMethodsFullRecovery = false;
        this.compilerOptions.performStatementsRecovery = false;
        this.batchCompiler = new Compiler(environment, getHandlingPolicy(), this.compilerOptions, getBatchRequestor(), getProblemFactory(), this.out, this.progress);
        this.batchCompiler.remainingIterations = this.maxRepetition - this.currentRepetition;
        String setting = System.getProperty("jdt.compiler.useSingleThread");
        this.batchCompiler.useSingleThread = setting != null && setting.equals("true");
        if (this.compilerOptions.complianceLevel >= ClassFileConstants.JDK1_6 && this.compilerOptions.processAnnotations) {
            if (checkVMVersion(ClassFileConstants.JDK1_6)) {
                initializeAnnotationProcessorManager();
                if (this.classNames != null) {
                    this.batchCompiler.setBinaryTypes(processClassNames(this.batchCompiler.lookupEnvironment));
                }
            } else {
                this.logger.logIncorrectVMVersionForAnnotationProcessing();
            }
        }
        this.compilerOptions.verbose = this.verbose;
        this.compilerOptions.produceReferenceInfo = this.produceRefInfo;
        try {
            this.logger.startLoggingSources();
            this.batchCompiler.compile(getCompilationUnits());
        } finally {
            this.logger.endLoggingSources();
        }
        if (this.extraProblems != null) {
            loggingExtraProblems();
            this.extraProblems = null;
        }
        if (this.compilerStats != null) {
            this.compilerStats[this.currentRepetition] = this.batchCompiler.stats;
        }
        this.logger.printStats();
        environment.cleanup();
    }

    protected void loggingExtraProblems() {
        this.logger.loggingExtraProblems(this);
    }

    public void printUsage() {
        printUsage("misc.usage");
    }

    private void printUsage(String sectionID) {
        this.logger.logUsage(this.bind(sectionID, new String[] { System.getProperty("path.separator"), this.bind("compiler.name"), this.bind("compiler.version"), this.bind("compiler.copyright") }));
        this.logger.flush();
    }

    private ReferenceBinding[] processClassNames(LookupEnvironment environment) {
        int length = this.classNames.length;
        ReferenceBinding[] referenceBindings = new ReferenceBinding[length];
        for (int i = 0; i < length; i++) {
            String currentName = this.classNames[i];
            char[][] compoundName = null;
            if (currentName.indexOf('.') != -1) {
                char[] typeName = currentName.toCharArray();
                compoundName = CharOperation.splitOn('.', typeName);
            } else {
                compoundName = new char[][] { currentName.toCharArray() };
            }
            ReferenceBinding type = environment.getType(compoundName);
            if (type != null && type.isValidBinding()) {
                if (type.isBinaryBinding()) {
                    referenceBindings[i] = type;
                }
            } else {
                throw new IllegalArgumentException(this.bind("configure.invalidClassName", currentName));
            }
        }
        return referenceBindings;
    }

    public void processPathEntries(final int defaultSize, final ArrayList paths, final String currentPath, String customEncoding, boolean isSourceOnly, boolean rejectDestinationPathOnJars) {
        String currentClasspathName = null;
        String currentDestinationPath = null;
        ArrayList currentRuleSpecs = new ArrayList(defaultSize);
        StringTokenizer tokenizer = new StringTokenizer(currentPath, File.pathSeparator + "[]", true);
        ArrayList tokens = new ArrayList();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        final int start = 0;
        final int readyToClose = 1;
        final int readyToCloseEndingWithRules = 2;
        final int readyToCloseOrOtherEntry = 3;
        final int rulesNeedAnotherRule = 4;
        final int rulesStart = 5;
        final int rulesReadyToClose = 6;
        final int destinationPathReadyToClose = 7;
        final int readyToCloseEndingWithDestinationPath = 8;
        final int destinationPathStart = 9;
        final int bracketOpened = 10;
        final int bracketClosed = 11;
        final int error = 99;
        int state = start;
        String token = null;
        int cursor = 0, tokensNb = tokens.size(), bracket = -1;
        while (cursor < tokensNb && state != error) {
            token = (String) tokens.get(cursor++);
            if (token.equals(File.pathSeparator)) {
                switch(state) {
                    case start:
                    case readyToCloseOrOtherEntry:
                    case bracketOpened:
                        break;
                    case readyToClose:
                    case readyToCloseEndingWithRules:
                    case readyToCloseEndingWithDestinationPath:
                        state = readyToCloseOrOtherEntry;
                        addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, currentDestinationPath, isSourceOnly, rejectDestinationPathOnJars);
                        currentRuleSpecs.clear();
                        break;
                    case rulesReadyToClose:
                        state = rulesNeedAnotherRule;
                        break;
                    case destinationPathReadyToClose:
                        throw new IllegalArgumentException(this.bind("configure.incorrectDestinationPathEntry", currentPath));
                    case bracketClosed:
                        cursor = bracket + 1;
                        state = rulesStart;
                        break;
                    default:
                        state = error;
                }
            } else if (token.equals("[")) {
                switch(state) {
                    case start:
                        currentClasspathName = "";
                    case readyToClose:
                        bracket = cursor - 1;
                    case bracketClosed:
                        state = bracketOpened;
                        break;
                    case readyToCloseEndingWithRules:
                        state = destinationPathStart;
                        break;
                    case readyToCloseEndingWithDestinationPath:
                        state = rulesStart;
                        break;
                    case bracketOpened:
                    default:
                        state = error;
                }
            } else if (token.equals("]")) {
                switch(state) {
                    case rulesReadyToClose:
                        state = readyToCloseEndingWithRules;
                        break;
                    case destinationPathReadyToClose:
                        state = readyToCloseEndingWithDestinationPath;
                        break;
                    case bracketOpened:
                        state = bracketClosed;
                        break;
                    case bracketClosed:
                    default:
                        state = error;
                }
            } else {
                switch(state) {
                    case start:
                    case readyToCloseOrOtherEntry:
                        state = readyToClose;
                        currentClasspathName = token;
                        break;
                    case rulesStart:
                        if (token.startsWith("-d ")) {
                            if (currentDestinationPath != null) {
                                throw new IllegalArgumentException(this.bind("configure.duplicateDestinationPathEntry", currentPath));
                            }
                            currentDestinationPath = token.substring(3).trim();
                            state = destinationPathReadyToClose;
                            break;
                        }
                    case rulesNeedAnotherRule:
                        if (currentDestinationPath != null) {
                            throw new IllegalArgumentException(this.bind("configure.accessRuleAfterDestinationPath", currentPath));
                        }
                        state = rulesReadyToClose;
                        currentRuleSpecs.add(token);
                        break;
                    case destinationPathStart:
                        if (!token.startsWith("-d ")) {
                            state = error;
                        } else {
                            currentDestinationPath = token.substring(3).trim();
                            state = destinationPathReadyToClose;
                        }
                        break;
                    case bracketClosed:
                        for (int i = bracket; i < cursor; i++) {
                            currentClasspathName += (String) tokens.get(i);
                        }
                        state = readyToClose;
                        break;
                    case bracketOpened:
                        break;
                    default:
                        state = error;
                }
            }
            if (state == bracketClosed && cursor == tokensNb) {
                cursor = bracket + 1;
                state = rulesStart;
            }
        }
        switch(state) {
            case readyToCloseOrOtherEntry:
                break;
            case readyToClose:
            case readyToCloseEndingWithRules:
            case readyToCloseEndingWithDestinationPath:
                addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, currentDestinationPath, isSourceOnly, rejectDestinationPathOnJars);
                break;
            case bracketOpened:
            case bracketClosed:
            default:
                if (currentPath.length() != 0) {
                    addPendingErrors(this.bind("configure.incorrectClasspath", currentPath));
                }
        }
    }

    private int processPaths(String[] args, int index, String currentArg, ArrayList paths) {
        int localIndex = index;
        int count = 0;
        for (int i = 0, max = currentArg.length(); i < max; i++) {
            switch(currentArg.charAt(i)) {
                case '[':
                    count++;
                    break;
                case ']':
                    count--;
                    break;
            }
        }
        if (count == 0) {
            paths.add(currentArg);
        } else if (count > 1) {
            throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
        } else {
            StringBuffer currentPath = new StringBuffer(currentArg);
            while (true) {
                if (localIndex >= args.length) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                }
                localIndex++;
                String nextArg = args[localIndex];
                for (int i = 0, max = nextArg.length(); i < max; i++) {
                    switch(nextArg.charAt(i)) {
                        case '[':
                            if (count > 1) {
                                throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", nextArg));
                            }
                            count++;
                            break;
                        case ']':
                            count--;
                            break;
                    }
                }
                if (count == 0) {
                    currentPath.append(' ');
                    currentPath.append(nextArg);
                    paths.add(currentPath.toString());
                    return localIndex - index;
                } else if (count < 0) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", nextArg));
                } else {
                    currentPath.append(' ');
                    currentPath.append(nextArg);
                }
            }
        }
        return localIndex - index;
    }

    private int processPaths(String[] args, int index, String currentArg, String[] paths) {
        int localIndex = index;
        int count = 0;
        for (int i = 0, max = currentArg.length(); i < max; i++) {
            switch(currentArg.charAt(i)) {
                case '[':
                    count++;
                    break;
                case ']':
                    count--;
                    break;
            }
        }
        if (count == 0) {
            paths[0] = currentArg;
        } else {
            StringBuffer currentPath = new StringBuffer(currentArg);
            while (true) {
                localIndex++;
                if (localIndex >= args.length) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                }
                String nextArg = args[localIndex];
                for (int i = 0, max = nextArg.length(); i < max; i++) {
                    switch(nextArg.charAt(i)) {
                        case '[':
                            if (count > 1) {
                                throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                            }
                            count++;
                            break;
                        case ']':
                            count--;
                            break;
                    }
                }
                if (count == 0) {
                    currentPath.append(' ');
                    currentPath.append(nextArg);
                    paths[0] = currentPath.toString();
                    return localIndex - index;
                } else if (count < 0) {
                    throw new IllegalArgumentException(this.bind("configure.unexpectedBracket", currentArg));
                } else {
                    currentPath.append(' ');
                    currentPath.append(nextArg);
                }
            }
        }
        return localIndex - index;
    }

    public void relocalize() {
        relocalize(Locale.getDefault());
    }

    private void relocalize(Locale locale) {
        this.compilerLocale = locale;
        try {
            this.bundle = ResourceBundleFactory.getBundle(locale);
        } catch (MissingResourceException e) {
            System.out.println("Missing resource : " + Main.bundleName.replace('.', '/') + ".properties for locale " + locale);
            throw e;
        }
    }

    public void setDestinationPath(String dest) {
        this.destinationPath = dest;
    }

    public void setLocale(Locale locale) {
        relocalize(locale);
    }

    protected void setPaths(ArrayList bootclasspaths, String sourcepathClasspathArg, ArrayList sourcepathClasspaths, ArrayList classpaths, ArrayList extdirsClasspaths, ArrayList endorsedDirClasspaths, String customEncoding) {
        bootclasspaths = handleBootclasspath(bootclasspaths, customEncoding);
        classpaths = handleClasspath(classpaths, customEncoding);
        if (sourcepathClasspathArg != null) {
            processPathEntries(DEFAULT_SIZE_CLASSPATH, sourcepathClasspaths, sourcepathClasspathArg, customEncoding, true, false);
        }
        extdirsClasspaths = handleExtdirs(extdirsClasspaths);
        endorsedDirClasspaths = handleEndorseddirs(endorsedDirClasspaths);
        bootclasspaths.addAll(0, endorsedDirClasspaths);
        bootclasspaths.addAll(extdirsClasspaths);
        bootclasspaths.addAll(sourcepathClasspaths);
        bootclasspaths.addAll(classpaths);
        classpaths = bootclasspaths;
        classpaths = FileSystem.ClasspathNormalizer.normalize(classpaths);
        this.checkedClasspaths = new FileSystem.Classpath[classpaths.size()];
        classpaths.toArray(this.checkedClasspaths);
        this.logger.logClasspath(this.checkedClasspaths);
        if (this.annotationPaths != null && CompilerOptions.ENABLED.equals(this.options.get(CompilerOptions.OPTION_AnnotationBasedNullAnalysis))) {
            for (FileSystem.Classpath cp : this.checkedClasspaths) {
                if (cp instanceof ClasspathJar)
                    ((ClasspathJar) cp).annotationPaths = this.annotationPaths;
            }
        }
    }

    private static boolean shouldIgnoreOptionalProblems(char[][] folderNames, char[] fileName) {
        if (folderNames == null || fileName == null) {
            return false;
        }
        for (int i = 0, max = folderNames.length; i < max; i++) {
            char[] folderName = folderNames[i];
            if (isParentOf(folderName, fileName)) {
                return true;
            }
        }
        return false;
    }

    protected void validateOptions(boolean didSpecifyCompliance) {
        if (didSpecifyCompliance) {
            Object version = this.options.get(CompilerOptions.OPTION_Compliance);
            if (CompilerOptions.VERSION_1_3.equals(version)) {
                if (!this.didSpecifySource)
                    this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_3);
                if (!this.didSpecifyTarget)
                    this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_1);
            } else if (CompilerOptions.VERSION_1_4.equals(version)) {
                if (this.didSpecifySource) {
                    Object source = this.options.get(CompilerOptions.OPTION_Source);
                    if (CompilerOptions.VERSION_1_3.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_2);
                    } else if (CompilerOptions.VERSION_1_4.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
                    }
                } else {
                    this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_3);
                    if (!this.didSpecifyTarget)
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_2);
                }
            } else if (CompilerOptions.VERSION_1_5.equals(version)) {
                if (this.didSpecifySource) {
                    Object source = this.options.get(CompilerOptions.OPTION_Source);
                    if (CompilerOptions.VERSION_1_3.equals(source) || CompilerOptions.VERSION_1_4.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
                    } else if (CompilerOptions.VERSION_1_5.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
                    }
                } else {
                    this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
                    if (!this.didSpecifyTarget)
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
                }
            } else if (CompilerOptions.VERSION_1_6.equals(version)) {
                if (this.didSpecifySource) {
                    Object source = this.options.get(CompilerOptions.OPTION_Source);
                    if (CompilerOptions.VERSION_1_3.equals(source) || CompilerOptions.VERSION_1_4.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
                    } else if (CompilerOptions.VERSION_1_5.equals(source) || CompilerOptions.VERSION_1_6.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
                    }
                } else {
                    this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_6);
                    if (!this.didSpecifyTarget)
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
                }
            } else if (CompilerOptions.VERSION_1_7.equals(version)) {
                if (this.didSpecifySource) {
                    Object source = this.options.get(CompilerOptions.OPTION_Source);
                    if (CompilerOptions.VERSION_1_3.equals(source) || CompilerOptions.VERSION_1_4.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
                    } else if (CompilerOptions.VERSION_1_5.equals(source) || CompilerOptions.VERSION_1_6.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
                    } else if (CompilerOptions.VERSION_1_7.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
                    }
                } else {
                    this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_7);
                    if (!this.didSpecifyTarget)
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
                }
            } else if (CompilerOptions.VERSION_1_8.equals(version)) {
                if (this.didSpecifySource) {
                    Object source = this.options.get(CompilerOptions.OPTION_Source);
                    if (CompilerOptions.VERSION_1_3.equals(source) || CompilerOptions.VERSION_1_4.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
                    } else if (CompilerOptions.VERSION_1_5.equals(source) || CompilerOptions.VERSION_1_6.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
                    } else if (CompilerOptions.VERSION_1_7.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
                    } else if (CompilerOptions.VERSION_1_8.equals(source)) {
                        if (!this.didSpecifyTarget)
                            this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_8);
                    }
                } else {
                    this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_8);
                    if (!this.didSpecifyTarget)
                        this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_8);
                }
            }
        } else if (this.didSpecifySource) {
            Object version = this.options.get(CompilerOptions.OPTION_Source);
            if (CompilerOptions.VERSION_1_4.equals(version)) {
                if (!didSpecifyCompliance)
                    this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_4);
                if (!this.didSpecifyTarget)
                    this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
            } else if (CompilerOptions.VERSION_1_5.equals(version)) {
                if (!didSpecifyCompliance)
                    this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_5);
                if (!this.didSpecifyTarget)
                    this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
            } else if (CompilerOptions.VERSION_1_6.equals(version)) {
                if (!didSpecifyCompliance)
                    this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_6);
                if (!this.didSpecifyTarget)
                    this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
            } else if (CompilerOptions.VERSION_1_7.equals(version)) {
                if (!didSpecifyCompliance)
                    this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_7);
                if (!this.didSpecifyTarget)
                    this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
            } else if (CompilerOptions.VERSION_1_8.equals(version)) {
                if (!didSpecifyCompliance)
                    this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_8);
                if (!this.didSpecifyTarget)
                    this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_8);
            }
        }
        final String sourceVersion = this.options.get(CompilerOptions.OPTION_Source);
        final String compliance = this.options.get(CompilerOptions.OPTION_Compliance);
        if (sourceVersion.equals(CompilerOptions.VERSION_1_8) && CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_8) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_8));
        } else if (sourceVersion.equals(CompilerOptions.VERSION_1_7) && CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_7) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_7));
        } else if (sourceVersion.equals(CompilerOptions.VERSION_1_6) && CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_6) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_6));
        } else if (sourceVersion.equals(CompilerOptions.VERSION_1_5) && CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_5) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_5));
        } else if (sourceVersion.equals(CompilerOptions.VERSION_1_4) && CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_4) {
            throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_4));
        }
        if (this.didSpecifyTarget) {
            final String targetVersion = this.options.get(CompilerOptions.OPTION_TargetPlatform);
            if (CompilerOptions.VERSION_JSR14.equals(targetVersion)) {
                if (CompilerOptions.versionToJdkLevel(sourceVersion) < ClassFileConstants.JDK1_5) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForGenericSource", targetVersion, sourceVersion));
                }
            } else if (CompilerOptions.VERSION_CLDC1_1.equals(targetVersion)) {
                if (this.didSpecifySource && CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_4) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleSourceForCldcTarget", targetVersion, sourceVersion));
                }
                if (CompilerOptions.versionToJdkLevel(compliance) >= ClassFileConstants.JDK1_5) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForCldcTarget", targetVersion, sourceVersion));
                }
            } else {
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_8 && CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_8) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, CompilerOptions.VERSION_1_8));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_7 && CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_7) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, CompilerOptions.VERSION_1_7));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_6 && CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_6) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, CompilerOptions.VERSION_1_6));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_5 && CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_5) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, CompilerOptions.VERSION_1_5));
                }
                if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_4 && CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_4) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", targetVersion, CompilerOptions.VERSION_1_4));
                }
                if (CompilerOptions.versionToJdkLevel(compliance) < CompilerOptions.versionToJdkLevel(targetVersion)) {
                    throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForTarget", this.options.get(CompilerOptions.OPTION_Compliance), targetVersion));
                }
            }
        }
    }
}

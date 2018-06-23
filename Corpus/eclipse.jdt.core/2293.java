/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class CodeStream implements OperatorIds, ClassFileConstants, Opcodes, BaseTypes, TypeConstants, TypeIds {

    public static final boolean DEBUG = false;

    // It will be responsible for the following items.
    // -> Tracking Max Stack.
    // Use Ints to keep from using extra bc when adding
    public int stackMax;

    // Use Ints to keep from using extra bc when adding
    public int stackDepth;

    public int maxLocals;

    public static final int LABELS_INCREMENT = 5;

    public byte[] bCodeStream;

    public int pcToSourceMapSize;

    public int[] pcToSourceMap = new int[24];

    // last entry recorded
    public int lastEntryPC;

    public int[] lineSeparatorPositions;

    // So when first set can be incremented
    public int position;

    public int classFileOffset;

    // I need to keep the starting point inside the byte array
    public int startingClassFileOffset;

    // The constant pool used to generate bytecodes that need to store information into the constant pool
    public ConstantPool constantPool;

    // The current classfile it is associated to.
    public ClassFile classFile;

    // local variable attributes output
    public static final int LOCALS_INCREMENT = 10;

    public LocalVariableBinding[] locals = new LocalVariableBinding[LOCALS_INCREMENT];

    static LocalVariableBinding[] noLocals = new LocalVariableBinding[LOCALS_INCREMENT];

    public LocalVariableBinding[] visibleLocals = new LocalVariableBinding[LOCALS_INCREMENT];

    static LocalVariableBinding[] noVisibleLocals = new LocalVariableBinding[LOCALS_INCREMENT];

    int visibleLocalsCount;

    public AbstractMethodDeclaration methodDeclaration;

    public ExceptionLabel[] exceptionHandlers = new ExceptionLabel[LABELS_INCREMENT];

    static ExceptionLabel[] noExceptionHandlers = new ExceptionLabel[LABELS_INCREMENT];

    public int exceptionHandlersIndex;

    public int exceptionHandlersCounter;

    public static FieldBinding[] ImplicitThis = new FieldBinding[] {};

    public boolean generateLineNumberAttributes;

    public boolean generateLocalVariableTableAttributes;

    public boolean preserveUnusedLocals;

    // store all the labels placed at the current position to be able to optimize
    // a jump to the next bytecode.
    public Label[] labels = new Label[LABELS_INCREMENT];

    static Label[] noLabels = new Label[LABELS_INCREMENT];

    public int countLabels;

    public int allLocalsCounter;

    public int maxFieldCount;

    // to handle goto_w
    public boolean wideMode = false;

    public static final CompilationResult RESTART_IN_WIDE_MODE = new CompilationResult((char[]) null, 0, 0, 0);

    // target level to manage different code generation between different target levels
    private long targetLevel;

    public  CodeStream(ClassFile classFile, long targetLevel) {
        this.targetLevel = targetLevel;
        this.generateLineNumberAttributes = (classFile.produceDebugAttributes & CompilerOptions.Lines) != 0;
        this.generateLocalVariableTableAttributes = (classFile.produceDebugAttributes & CompilerOptions.Vars) != 0;
        if (this.generateLineNumberAttributes) {
            this.lineSeparatorPositions = classFile.referenceBinding.scope.referenceCompilationUnit().compilationResult.lineSeparatorPositions;
        }
    }

    public final void aaload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taaload");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_aaload;
    }

    public final void aastore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taastore");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_aastore;
    }

    public final void aconst_null() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taconst_null");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax) {
            stackMax = stackDepth;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_aconst_null;
    }

    public final void addDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
        // Required to fix 1PR0XVS: LFRE:WINNT - Compiler: variable table for method appears incorrect
        if (!generateLocalVariableTableAttributes)
            return;
        /*	if (initStateIndex == lastInitStateIndexWhenAddingInits)
		return;
	lastInitStateIndexWhenAddingInits = initStateIndex;
	if (lastInitStateIndexWhenRemovingInits != initStateIndex){
		lastInitStateIndexWhenRemovingInits = -2; // reinitialize remove index 
		// remove(1)-add(1)-remove(1) -> ignore second remove
		// remove(1)-add(2)-remove(1) -> perform second remove
	}
	
*/
        for (int i = 0; i < visibleLocalsCount; i++) {
            LocalVariableBinding localBinding = visibleLocals[i];
            if (localBinding != null) {
                // Check if the local is definitely assigned
                if ((initStateIndex != -1) && isDefinitelyAssigned(scope, initStateIndex, localBinding)) {
                    if ((localBinding.initializationCount == 0) || (localBinding.initializationPCs[((localBinding.initializationCount - 1) << 1) + 1] != -1)) {
                        /* There are two cases:
					 * 1) there is no initialization interval opened ==> add an opened interval
					 * 2) there is already some initialization intervals but the last one is closed ==> add an opened interval
					 * An opened interval means that the value at localBinding.initializationPCs[localBinding.initializationCount - 1][1]
					 * is equals to -1.
					 * initializationPCs is a collection of pairs of int:
					 * 	first value is the startPC and second value is the endPC. -1 one for the last value means that the interval
					 * 	is not closed yet.
					 */
                        localBinding.recordInitializationStartPC(position);
                    }
                }
            }
        }
    }

    public void addLabel(Label aLabel) {
        if (countLabels == labels.length)
            System.arraycopy(labels, 0, labels = new Label[countLabels + LABELS_INCREMENT], 0, countLabels);
        labels[countLabels++] = aLabel;
    }

    public void addVisibleLocalVariable(LocalVariableBinding localBinding) {
        if (!generateLocalVariableTableAttributes)
            return;
        if (visibleLocalsCount >= visibleLocals.length)
            System.arraycopy(visibleLocals, 0, visibleLocals = new LocalVariableBinding[visibleLocalsCount * 2], 0, visibleLocalsCount);
        visibleLocals[visibleLocalsCount++] = localBinding;
    }

    public final void aload(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taload:" + iArg);
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals <= iArg) {
            maxLocals = iArg + 1;
        }
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_aload;
            writeUnsignedShort(iArg);
        } else {
            // Don't need to use the wide bytecode
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_aload;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void aload_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taload_0");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax) {
            stackMax = stackDepth;
        }
        if (maxLocals == 0) {
            maxLocals = 1;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_aload_0;
    }

    public final void aload_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taload_1");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals <= 1) {
            maxLocals = 2;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_aload_1;
    }

    public final void aload_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taload_2");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals <= 2) {
            maxLocals = 3;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_aload_2;
    }

    public final void aload_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\taload_3");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals <= 3) {
            maxLocals = 4;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_aload_3;
    }

    public final void anewarray(TypeBinding typeBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tanewarray: " + typeBinding);
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_anewarray;
        writeUnsignedShort(constantPool.literalIndex(typeBinding));
    }

    public final void areturn() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tareturn");
        countLabels = 0;
        stackDepth--;
        // the stackDepth should be equal to 0 
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_areturn;
    }

    public void arrayAt(int typeBindingID) {
        switch(typeBindingID) {
            case T_int:
                this.iaload();
                break;
            case T_byte:
            case T_boolean:
                this.baload();
                break;
            case T_short:
                this.saload();
                break;
            case T_char:
                this.caload();
                break;
            case T_long:
                this.laload();
                break;
            case T_float:
                this.faload();
                break;
            case T_double:
                this.daload();
                break;
            default:
                this.aaload();
        }
    }

    public void arrayAtPut(int elementTypeID, boolean valueRequired) {
        switch(elementTypeID) {
            case T_int:
                if (valueRequired)
                    dup_x2();
                iastore();
                break;
            case T_byte:
            case T_boolean:
                if (valueRequired)
                    dup_x2();
                bastore();
                break;
            case T_short:
                if (valueRequired)
                    dup_x2();
                sastore();
                break;
            case T_char:
                if (valueRequired)
                    dup_x2();
                castore();
                break;
            case T_long:
                if (valueRequired)
                    dup2_x2();
                lastore();
                break;
            case T_float:
                if (valueRequired)
                    dup_x2();
                fastore();
                break;
            case T_double:
                if (valueRequired)
                    dup2_x2();
                dastore();
                break;
            default:
                if (valueRequired)
                    dup_x2();
                aastore();
        }
    }

    public final void arraylength() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tarraylength");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_arraylength;
    }

    public final void astore(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tastore:" + iArg);
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= iArg) {
            maxLocals = iArg + 1;
        }
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_astore;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_astore;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void astore_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tastore_0");
        countLabels = 0;
        stackDepth--;
        if (maxLocals == 0) {
            maxLocals = 1;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_astore_0;
    }

    public final void astore_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tastore_1");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 1) {
            maxLocals = 2;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_astore_1;
    }

    public final void astore_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tastore_2");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 2) {
            maxLocals = 3;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_astore_2;
    }

    public final void astore_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tastore_3");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 3) {
            maxLocals = 4;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_astore_3;
    }

    public final void athrow() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tathrow");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_athrow;
    }

    public final void baload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tbaload");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_baload;
    }

    public final void bastore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tbastore");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_bastore;
    }

    public final void bipush(byte b) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tbipush " + b);
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 1 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 2;
        bCodeStream[classFileOffset++] = OPC_bipush;
        bCodeStream[classFileOffset++] = b;
    }

    public final void caload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tcaload");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_caload;
    }

    public final void castore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tcastore");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_castore;
    }

    public final void checkcast(TypeBinding typeBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tcheckcast:" + typeBinding);
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_checkcast;
        writeUnsignedShort(constantPool.literalIndex(typeBinding));
    }

    public final void d2f() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\td2f");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_d2f;
    }

    public final void d2i() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\td2i");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_d2i;
    }

    public final void d2l() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\td2l");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_d2l;
    }

    public final void dadd() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdadd");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dadd;
    }

    public final void daload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdaload");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_daload;
    }

    public final void dastore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdastore");
        countLabels = 0;
        stackDepth -= 4;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dastore;
    }

    public final void dcmpg() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdcmpg");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dcmpg;
    }

    public final void dcmpl() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdcmpl");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dcmpl;
    }

    public final void dconst_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdconst_0");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dconst_0;
    }

    public final void dconst_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdconst_1");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dconst_1;
    }

    public final void ddiv() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tddiv");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ddiv;
    }

    public void decrStackSize(int offset) {
        stackDepth -= offset;
    }

    public final void dload(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdload:" + iArg);
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals < iArg + 2) {
            // + 2 because it is a double
            maxLocals = iArg + 2;
        }
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_dload;
            writeUnsignedShort(iArg);
        } else {
            // Don't need to use the wide bytecode
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_dload;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void dload_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdload_0");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals < 2) {
            maxLocals = 2;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dload_0;
    }

    public final void dload_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdload_1");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals < 3) {
            maxLocals = 3;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dload_1;
    }

    public final void dload_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdload_2");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals < 4) {
            maxLocals = 4;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dload_2;
    }

    public final void dload_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdload_3");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (maxLocals < 5) {
            maxLocals = 5;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dload_3;
    }

    public final void dmul() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdmul");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dmul;
    }

    public final void dneg() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdneg");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dneg;
    }

    public final void drem() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdrem");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_drem;
    }

    public final void dreturn() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdreturn");
        countLabels = 0;
        stackDepth -= 2;
        // the stackDepth should be equal to 0 
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dreturn;
    }

    public final void dstore(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdstore:" + iArg);
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals <= iArg + 1) {
            maxLocals = iArg + 2;
        }
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_dstore;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_dstore;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void dstore_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdstore_0");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 2) {
            maxLocals = 2;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dstore_0;
    }

    public final void dstore_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdstore_1");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 3) {
            maxLocals = 3;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dstore_1;
    }

    public final void dstore_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdstore_2");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 4) {
            maxLocals = 4;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dstore_2;
    }

    public final void dstore_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdstore_3");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 5) {
            maxLocals = 5;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dstore_3;
    }

    public final void dsub() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdsub");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dsub;
    }

    public final void dup() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdup");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax) {
            stackMax = stackDepth;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dup;
    }

    public final void dup_x1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdup_x1");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dup_x1;
    }

    public final void dup_x2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdup_x2");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dup_x2;
    }

    public final void dup2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdup2");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dup2;
    }

    public final void dup2_x1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdup2_x1");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dup2_x1;
    }

    public final void dup2_x2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tdup2_x2");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_dup2_x2;
    }

    public void exitUserScope(BlockScope blockScope) {
        if (!generateLocalVariableTableAttributes)
            return;
        for (int i = 0; i < visibleLocalsCount; i++) {
            LocalVariableBinding visibleLocal = visibleLocals[i];
            if ((visibleLocal != null) && (visibleLocal.declaringScope == blockScope)) {
                // there maybe some some preserved locals never initialized
                if (visibleLocal.initializationCount > 0) {
                    visibleLocals[i].recordInitializationEndPC(position);
                }
                // this variable is no longer visible afterwards
                visibleLocals[i] = null;
            }
        }
    }

    public final void f2d() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tf2d");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_f2d;
    }

    public final void f2i() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tf2i");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_f2i;
    }

    public final void f2l() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tf2l");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_f2l;
    }

    public final void fadd() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfadd");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fadd;
    }

    public final void faload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfaload");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_faload;
    }

    public final void fastore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfaload");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fastore;
    }

    public final void fcmpg() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfcmpg");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fcmpg;
    }

    public final void fcmpl() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfcmpl");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fcmpl;
    }

    public final void fconst_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfconst_0");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fconst_0;
    }

    public final void fconst_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfconst_1");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fconst_1;
    }

    public final void fconst_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfconst_2");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fconst_2;
    }

    public final void fdiv() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfdiv");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fdiv;
    }

    public final void fload(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfload:" + iArg);
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= iArg) {
            maxLocals = iArg + 1;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_fload;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_fload;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void fload_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfload_0");
        countLabels = 0;
        stackDepth++;
        if (maxLocals == 0) {
            maxLocals = 1;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fload_0;
    }

    public final void fload_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfload_1");
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= 1) {
            maxLocals = 2;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fload_1;
    }

    public final void fload_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfload_2");
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= 2) {
            maxLocals = 3;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fload_2;
    }

    public final void fload_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfload_3");
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= 3) {
            maxLocals = 4;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fload_3;
    }

    public final void fmul() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfmul");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fmul;
    }

    public final void fneg() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfneg");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fneg;
    }

    public final void frem() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfrem");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_frem;
    }

    public final void freturn() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfreturn");
        countLabels = 0;
        stackDepth--;
        // the stackDepth should be equal to 0 
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_freturn;
    }

    public final void fstore(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfstore:" + iArg);
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= iArg) {
            maxLocals = iArg + 1;
        }
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_fstore;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_fstore;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void fstore_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfstore_0");
        countLabels = 0;
        stackDepth--;
        if (maxLocals == 0) {
            maxLocals = 1;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fstore_0;
    }

    public final void fstore_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfstore_1");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 1) {
            maxLocals = 2;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fstore_1;
    }

    public final void fstore_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfstore_2");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 2) {
            maxLocals = 3;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fstore_2;
    }

    public final void fstore_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfstore_3");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 3) {
            maxLocals = 4;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fstore_3;
    }

    public final void fsub() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tfsub");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_fsub;
    }

    /**
 * Macro for building a class descriptor object
 */
    public void generateClassLiteralAccessForType(TypeBinding accessedType, FieldBinding syntheticFieldBinding) {
        Label endLabel;
        ExceptionLabel anyExceptionHandler;
        int saveStackSize;
        if (accessedType.isBaseType() && accessedType != NullBinding) {
            this.getTYPE(accessedType.id);
            return;
        }
        if (this.targetLevel >= ClassFileConstants.JDK1_5) {
            // generation using the new ldc_w bytecode
            this.ldc(accessedType);
        } else {
            endLabel = new Label(this);
            if (// non interface case
            syntheticFieldBinding != null) {
                this.getstatic(syntheticFieldBinding);
                this.dup();
                this.ifnonnull(endLabel);
                this.pop();
            }
            /* Macro for building a class descriptor object... using or not a field cache to store it into...
		this sequence is responsible for building the actual class descriptor.
		
		If the fieldCache is set, then it is supposed to be the body of a synthetic access method
		factoring the actual descriptor creation out of the invocation site (saving space).
		If the fieldCache is nil, then we are dumping the bytecode on the invocation site, since
		we have no way to get a hand on the field cache to do better. */
            // Wrap the code in an exception handler to convert a ClassNotFoundException into a NoClassDefError
            anyExceptionHandler = new ExceptionLabel(this, BaseTypes.NullBinding);
            //$NON-NLS-1$
            this.ldc(accessedType == BaseTypes.NullBinding ? "java.lang.Object" : String.valueOf(accessedType.constantPoolName()).replace('/', '.'));
            this.invokeClassForName();
            /* See https://bugs.eclipse.org/bugs/show_bug.cgi?id=37565
		if (accessedType == BaseTypes.NullBinding) {
			this.ldc("java.lang.Object"); //$NON-NLS-1$
		} else if (accessedType.isArrayType()) {
			this.ldc(String.valueOf(accessedType.constantPoolName()).replace('/', '.'));
		} else {
			// we make it an array type (to avoid class initialization)
			this.ldc("[L" + String.valueOf(accessedType.constantPoolName()).replace('/', '.') + ";"); //$NON-NLS-1$//$NON-NLS-2$
		}
		this.invokeClassForName();
		if (!accessedType.isArrayType()) { // extract the component type, which doesn't initialize the class
			this.invokeJavaLangClassGetComponentType();
		}	
		*/
            /* We need to protect the runtime code from binary inconsistencies
		in case the accessedType is missing, the ClassNotFoundException has to be converted
		into a NoClassDefError(old ex message), we thus need to build an exception handler for this one. */
            anyExceptionHandler.placeEnd();
            if (// non interface case
            syntheticFieldBinding != null) {
                this.dup();
                this.putstatic(syntheticFieldBinding);
            }
            this.goto_(endLabel);
            // Generate the body of the exception handler
            saveStackSize = stackDepth;
            stackDepth = 1;
            /* ClassNotFoundException on stack -- the class literal could be doing more things
		on the stack, which means that the stack may not be empty at this point in the
		above code gen. So we save its state and restart it from 1. */
            anyExceptionHandler.place();
            // Transform the current exception, and repush and throw a 
            // NoClassDefFoundError(ClassNotFound.getMessage())
            this.newNoClassDefFoundError();
            this.dup_x1();
            this.swap();
            // Retrieve the message from the old exception
            this.invokeThrowableGetMessage();
            // Send the constructor taking a message string as an argument
            this.invokeNoClassDefFoundErrorStringConstructor();
            this.athrow();
            stackDepth = saveStackSize;
            endLabel.place();
        }
    }

    /**
 * This method generates the code attribute bytecode
 */
    public final void generateCodeAttributeForProblemMethod(String problemMessage) {
        newJavaLangError();
        dup();
        ldc(problemMessage);
        invokeJavaLangErrorConstructor();
        athrow();
    }

    public void generateConstant(Constant constant, int implicitConversionCode) {
        int targetTypeID = implicitConversionCode >> 4;
        switch(targetTypeID) {
            case T_boolean:
                generateInlinedValue(constant.booleanValue());
                break;
            case T_char:
                generateInlinedValue(constant.charValue());
                break;
            case T_byte:
                generateInlinedValue(constant.byteValue());
                break;
            case T_short:
                generateInlinedValue(constant.shortValue());
                break;
            case T_int:
                generateInlinedValue(constant.intValue());
                break;
            case T_long:
                generateInlinedValue(constant.longValue());
                break;
            case T_float:
                generateInlinedValue(constant.floatValue());
                break;
            case T_double:
                generateInlinedValue(constant.doubleValue());
                break;
            default:
                //String or Object
                ldc(constant.stringValue());
        }
    }

    /**
 * Generates the sequence of instructions which will perform the conversion of the expression
 * on the stack into a different type (e.g. long l = someInt; --> i2l must be inserted).
 * @param implicitConversionCode int
 */
    public void generateImplicitConversion(int implicitConversionCode) {
        switch(implicitConversionCode) {
            case Float2Char:
                this.f2i();
                this.i2c();
                break;
            case Double2Char:
                this.d2i();
                this.i2c();
                break;
            case Int2Char:
            case Short2Char:
            case Byte2Char:
                this.i2c();
                break;
            case Long2Char:
                this.l2i();
                this.i2c();
                break;
            case Char2Float:
            case Short2Float:
            case Int2Float:
            case Byte2Float:
                this.i2f();
                break;
            case Double2Float:
                this.d2f();
                break;
            case Long2Float:
                this.l2f();
                break;
            case Float2Byte:
                this.f2i();
                this.i2b();
                break;
            case Double2Byte:
                this.d2i();
                this.i2b();
                break;
            case Int2Byte:
            case Short2Byte:
            case Char2Byte:
                this.i2b();
                break;
            case Long2Byte:
                this.l2i();
                this.i2b();
                break;
            case Byte2Double:
            case Char2Double:
            case Short2Double:
            case Int2Double:
                this.i2d();
                break;
            case Float2Double:
                this.f2d();
                break;
            case Long2Double:
                this.l2d();
                break;
            case Byte2Short:
            case Char2Short:
            case Int2Short:
                this.i2s();
                break;
            case Double2Short:
                this.d2i();
                this.i2s();
                break;
            case Long2Short:
                this.l2i();
                this.i2s();
                break;
            case Float2Short:
                this.f2i();
                this.i2s();
                break;
            case Double2Int:
                this.d2i();
                break;
            case Float2Int:
                this.f2i();
                break;
            case Long2Int:
                this.l2i();
                break;
            case Int2Long:
            case Char2Long:
            case Byte2Long:
            case Short2Long:
                this.i2l();
                break;
            case Double2Long:
                this.d2l();
                break;
            case Float2Long:
                this.f2l();
        }
    }

    public void generateInlinedValue(byte inlinedValue) {
        switch(inlinedValue) {
            case -1:
                this.iconst_m1();
                break;
            case 0:
                this.iconst_0();
                break;
            case 1:
                this.iconst_1();
                break;
            case 2:
                this.iconst_2();
                break;
            case 3:
                this.iconst_3();
                break;
            case 4:
                this.iconst_4();
                break;
            case 5:
                this.iconst_5();
                break;
            default:
                if ((-128 <= inlinedValue) && (inlinedValue <= 127)) {
                    this.bipush(inlinedValue);
                    return;
                }
        }
    }

    public void generateInlinedValue(char inlinedValue) {
        switch(inlinedValue) {
            case 0:
                this.iconst_0();
                break;
            case 1:
                this.iconst_1();
                break;
            case 2:
                this.iconst_2();
                break;
            case 3:
                this.iconst_3();
                break;
            case 4:
                this.iconst_4();
                break;
            case 5:
                this.iconst_5();
                break;
            default:
                if ((6 <= inlinedValue) && (inlinedValue <= 127)) {
                    this.bipush((byte) inlinedValue);
                    return;
                }
                if ((128 <= inlinedValue) && (inlinedValue <= 32767)) {
                    this.sipush(inlinedValue);
                    return;
                }
                this.ldc(inlinedValue);
        }
    }

    public void generateInlinedValue(double inlinedValue) {
        if (inlinedValue == 0.0) {
            if (Double.doubleToLongBits(inlinedValue) != 0L)
                this.ldc2_w(inlinedValue);
            else
                this.dconst_0();
            return;
        }
        if (inlinedValue == 1.0) {
            this.dconst_1();
            return;
        }
        this.ldc2_w(inlinedValue);
    }

    public void generateInlinedValue(float inlinedValue) {
        if (inlinedValue == 0.0f) {
            if (Float.floatToIntBits(inlinedValue) != 0)
                this.ldc(inlinedValue);
            else
                this.fconst_0();
            return;
        }
        if (inlinedValue == 1.0f) {
            this.fconst_1();
            return;
        }
        if (inlinedValue == 2.0f) {
            this.fconst_2();
            return;
        }
        this.ldc(inlinedValue);
    }

    public void generateInlinedValue(int inlinedValue) {
        switch(inlinedValue) {
            case -1:
                this.iconst_m1();
                break;
            case 0:
                this.iconst_0();
                break;
            case 1:
                this.iconst_1();
                break;
            case 2:
                this.iconst_2();
                break;
            case 3:
                this.iconst_3();
                break;
            case 4:
                this.iconst_4();
                break;
            case 5:
                this.iconst_5();
                break;
            default:
                if ((-128 <= inlinedValue) && (inlinedValue <= 127)) {
                    this.bipush((byte) inlinedValue);
                    return;
                }
                if ((-32768 <= inlinedValue) && (inlinedValue <= 32767)) {
                    this.sipush(inlinedValue);
                    return;
                }
                this.ldc(inlinedValue);
        }
    }

    public void generateInlinedValue(long inlinedValue) {
        if (inlinedValue == 0) {
            this.lconst_0();
            return;
        }
        if (inlinedValue == 1) {
            this.lconst_1();
            return;
        }
        this.ldc2_w(inlinedValue);
    }

    public void generateInlinedValue(short inlinedValue) {
        switch(inlinedValue) {
            case -1:
                this.iconst_m1();
                break;
            case 0:
                this.iconst_0();
                break;
            case 1:
                this.iconst_1();
                break;
            case 2:
                this.iconst_2();
                break;
            case 3:
                this.iconst_3();
                break;
            case 4:
                this.iconst_4();
                break;
            case 5:
                this.iconst_5();
                break;
            default:
                if ((-128 <= inlinedValue) && (inlinedValue <= 127)) {
                    this.bipush((byte) inlinedValue);
                    return;
                }
                this.sipush(inlinedValue);
        }
    }

    public void generateInlinedValue(boolean inlinedValue) {
        if (inlinedValue)
            this.iconst_1();
        else
            this.iconst_0();
    }

    public void generateOuterAccess(Object[] mappingSequence, ASTNode invocationSite, Binding target, Scope scope) {
        if (mappingSequence == null) {
            if (target instanceof LocalVariableBinding) {
                //TODO (philippe) should improve local emulation failure reporting
                scope.problemReporter().needImplementation();
            } else {
                scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding) target, invocationSite, false);
            }
            return;
        }
        if (mappingSequence == BlockScope.NoEnclosingInstanceInConstructorCall) {
            scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding) target, invocationSite, true);
            return;
        } else if (mappingSequence == BlockScope.NoEnclosingInstanceInStaticContext) {
            scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding) target, invocationSite, false);
            return;
        }
        if (mappingSequence == BlockScope.EmulationPathToImplicitThis) {
            this.aload_0();
            return;
        } else if (mappingSequence[0] instanceof FieldBinding) {
            FieldBinding fieldBinding = (FieldBinding) mappingSequence[0];
            this.aload_0();
            this.getfield(fieldBinding);
        } else {
            load((LocalVariableBinding) mappingSequence[0]);
        }
        for (int i = 1, length = mappingSequence.length; i < length; i++) {
            if (mappingSequence[i] instanceof FieldBinding) {
                FieldBinding fieldBinding = (FieldBinding) mappingSequence[i];
                this.getfield(fieldBinding);
            } else {
                this.invokestatic((MethodBinding) mappingSequence[i]);
            }
        }
    }

    /**
 * The equivalent code performs a string conversion:
 *
 * @param blockScope the given blockScope
 * @param oper1 the first expression
 * @param oper2 the second expression
 */
    public void generateStringConcatenationAppend(BlockScope blockScope, Expression oper1, Expression oper2) {
        int pc;
        if (oper1 == null) {
            /* Operand is already on the stack, and maybe nil:
		note type1 is always to  java.lang.String here.*/
            this.newStringContatenation();
            this.dup_x1();
            this.swap();
            // If argument is reference type, need to transform it 
            // into a string (handles null case)
            this.invokeStringValueOf(T_Object);
            this.invokeStringConcatenationStringConstructor();
        } else {
            pc = position;
            oper1.generateOptimizedStringConcatenationCreation(blockScope, this, oper1.implicitConversion & 0xF);
            this.recordPositionsFrom(pc, oper1.sourceStart);
        }
        pc = position;
        oper2.generateOptimizedStringConcatenation(blockScope, this, oper2.implicitConversion & 0xF);
        this.recordPositionsFrom(pc, oper2.sourceStart);
        this.invokeStringConcatenationToString();
    }

    /**
 * Code responsible to generate the suitable code to supply values for the synthetic enclosing
 * instance arguments of a constructor invocation of a nested type.
 */
    public void generateSyntheticEnclosingInstanceValues(BlockScope currentScope, ReferenceBinding targetType, Expression enclosingInstance, ASTNode invocationSite) {
        // supplying enclosing instance for the anonymous type's superclass
        ReferenceBinding checkedTargetType = targetType.isAnonymousType() ? targetType.superclass() : targetType;
        boolean hasExtraEnclosingInstance = enclosingInstance != null;
        if (hasExtraEnclosingInstance && (!checkedTargetType.isNestedType() || checkedTargetType.isStatic())) {
            currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
            return;
        }
        // perform some emulation work in case there is some and we are inside a local type only
        ReferenceBinding[] syntheticArgumentTypes;
        if ((syntheticArgumentTypes = targetType.syntheticEnclosingInstanceTypes()) != null) {
            ReferenceBinding targetEnclosingType = checkedTargetType.enclosingType();
            boolean complyTo14 = currentScope.environment().options.complianceLevel >= ClassFileConstants.JDK1_4;
            // deny access to enclosing instance argument for allocation and super constructor call (if 1.4)
            boolean ignoreEnclosingArgInConstructorCall = invocationSite instanceof AllocationExpression || (complyTo14 && ((invocationSite instanceof ExplicitConstructorCall && ((ExplicitConstructorCall) invocationSite).isSuperAccess())));
            for (int i = 0, max = syntheticArgumentTypes.length; i < max; i++) {
                ReferenceBinding syntheticArgType = syntheticArgumentTypes[i];
                if (hasExtraEnclosingInstance && syntheticArgType == targetEnclosingType) {
                    hasExtraEnclosingInstance = false;
                    enclosingInstance.generateCode(currentScope, this, true);
                    if (complyTo14) {
                        dup();
                        // will perform null check
                        invokeObjectGetClass();
                        pop();
                    }
                } else {
                    Object[] emulationPath = currentScope.getEmulationPath(syntheticArgType, /*not only exact match (that is, allow compatible)*/
                    false, ignoreEnclosingArgInConstructorCall);
                    this.generateOuterAccess(emulationPath, invocationSite, syntheticArgType, currentScope);
                }
            }
            if (hasExtraEnclosingInstance) {
                currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
            }
        }
    }

    /**
 * Code responsible to generate the suitable code to supply values for the synthetic outer local
 * variable arguments of a constructor invocation of a nested type.
 * (bug 26122) - synthetic values for outer locals must be passed after user arguments, e.g. new X(i = 1){}
 */
    public void generateSyntheticOuterArgumentValues(BlockScope currentScope, ReferenceBinding targetType, ASTNode invocationSite) {
        // generate the synthetic outer arguments then
        SyntheticArgumentBinding syntheticArguments[];
        if ((syntheticArguments = targetType.syntheticOuterLocalVariables()) != null) {
            for (int i = 0, max = syntheticArguments.length; i < max; i++) {
                LocalVariableBinding targetVariable = syntheticArguments[i].actualOuterLocalVariable;
                VariableBinding[] emulationPath = currentScope.getEmulationPath(targetVariable);
                this.generateOuterAccess(emulationPath, invocationSite, targetVariable, currentScope);
            }
        }
    }

    /**
 * @param accessBinding the access method binding to generate
 */
    public void generateSyntheticBodyForConstructorAccess(SyntheticAccessMethodBinding accessBinding) {
        initializeMaxLocals(accessBinding);
        MethodBinding constructorBinding = accessBinding.targetMethod;
        TypeBinding[] parameters = constructorBinding.parameters;
        int length = parameters.length;
        int resolvedPosition = 1;
        this.aload_0();
        if (constructorBinding.declaringClass.isNestedType()) {
            NestedTypeBinding nestedType = (NestedTypeBinding) constructorBinding.declaringClass;
            SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticEnclosingInstances();
            for (int i = 0; i < (syntheticArguments == null ? 0 : syntheticArguments.length); i++) {
                TypeBinding type;
                load((type = syntheticArguments[i].type), resolvedPosition);
                if ((type == DoubleBinding) || (type == LongBinding))
                    resolvedPosition += 2;
                else
                    resolvedPosition++;
            }
        }
        for (int i = 0; i < length; i++) {
            load(parameters[i], resolvedPosition);
            if ((parameters[i] == DoubleBinding) || (parameters[i] == LongBinding))
                resolvedPosition += 2;
            else
                resolvedPosition++;
        }
        if (constructorBinding.declaringClass.isNestedType()) {
            NestedTypeBinding nestedType = (NestedTypeBinding) constructorBinding.declaringClass;
            SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
            for (int i = 0; i < (syntheticArguments == null ? 0 : syntheticArguments.length); i++) {
                TypeBinding type;
                load((type = syntheticArguments[i].type), resolvedPosition);
                if ((type == DoubleBinding) || (type == LongBinding))
                    resolvedPosition += 2;
                else
                    resolvedPosition++;
            }
        }
        this.invokespecial(constructorBinding);
        this.return_();
    }

    public void generateSyntheticBodyForFieldReadAccess(SyntheticAccessMethodBinding accessBinding) {
        initializeMaxLocals(accessBinding);
        FieldBinding fieldBinding = accessBinding.targetReadField;
        TypeBinding type;
        if (fieldBinding.isStatic())
            this.getstatic(fieldBinding);
        else {
            this.aload_0();
            this.getfield(fieldBinding);
        }
        if ((type = fieldBinding.type).isBaseType()) {
            if (type == IntBinding)
                this.ireturn();
            else if (type == FloatBinding)
                this.freturn();
            else if (type == LongBinding)
                this.lreturn();
            else if (type == DoubleBinding)
                this.dreturn();
            else
                this.ireturn();
        } else
            this.areturn();
    }

    public void generateSyntheticBodyForFieldWriteAccess(SyntheticAccessMethodBinding accessBinding) {
        initializeMaxLocals(accessBinding);
        FieldBinding fieldBinding = accessBinding.targetWriteField;
        if (fieldBinding.isStatic()) {
            load(fieldBinding.type, 0);
            this.putstatic(fieldBinding);
        } else {
            this.aload_0();
            load(fieldBinding.type, 1);
            this.putfield(fieldBinding);
        }
        this.return_();
    }

    public void generateSyntheticBodyForMethodAccess(SyntheticAccessMethodBinding accessBinding) {
        initializeMaxLocals(accessBinding);
        MethodBinding methodBinding = accessBinding.targetMethod;
        TypeBinding[] parameters = methodBinding.parameters;
        int length = parameters.length;
        TypeBinding[] arguments = accessBinding.accessType == SyntheticAccessMethodBinding.BridgeMethodAccess ? accessBinding.parameters : null;
        int resolvedPosition;
        if (methodBinding.isStatic())
            resolvedPosition = 0;
        else {
            this.aload_0();
            resolvedPosition = 1;
        }
        for (int i = 0; i < length; i++) {
            TypeBinding parameter = parameters[i];
            if (// for bridge methods
            arguments != null) {
                TypeBinding argument = arguments[i];
                load(argument, resolvedPosition);
                if (argument != parameter)
                    checkcast(parameter);
            } else {
                load(parameter, resolvedPosition);
            }
            if ((parameter == DoubleBinding) || (parameter == LongBinding))
                resolvedPosition += 2;
            else
                resolvedPosition++;
        }
        TypeBinding type;
        if (methodBinding.isStatic())
            this.invokestatic(methodBinding);
        else {
            if (methodBinding.isConstructor() || methodBinding.isPrivate() || // qualified super "X.super.foo()" targets methods from superclass
            accessBinding.accessType == SyntheticAccessMethodBinding.SuperMethodAccess) {
                this.invokespecial(methodBinding);
            } else {
                if (methodBinding.declaringClass.isInterface()) {
                    this.invokeinterface(methodBinding);
                } else {
                    this.invokevirtual(methodBinding);
                }
            }
        }
        if ((type = methodBinding.returnType).isBaseType())
            if (type == VoidBinding)
                this.return_();
            else if (type == IntBinding)
                this.ireturn();
            else if (type == FloatBinding)
                this.freturn();
            else if (type == LongBinding)
                this.lreturn();
            else if (type == DoubleBinding)
                this.dreturn();
            else
                this.ireturn();
        else
            this.areturn();
    }

    public final byte[] getContents() {
        byte[] contents;
        System.arraycopy(bCodeStream, 0, contents = new byte[position], 0, position);
        return contents;
    }

    public final void getfield(FieldBinding fieldBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tgetfield:" + fieldBinding);
        countLabels = 0;
        if ((fieldBinding.type.id == T_double) || (fieldBinding.type.id == T_long)) {
            if (++stackDepth > stackMax)
                stackMax = stackDepth;
        }
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_getfield;
        writeUnsignedShort(constantPool.literalIndex(fieldBinding));
    }

    public final void getstatic(FieldBinding fieldBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tgetstatic:" + fieldBinding);
        countLabels = 0;
        if ((fieldBinding.type.id == T_double) || (fieldBinding.type.id == T_long))
            stackDepth += 2;
        else
            stackDepth += 1;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_getstatic;
        writeUnsignedShort(constantPool.literalIndex(fieldBinding));
    }

    public void getTYPE(int baseTypeID) {
        countLabels = 0;
        if (++stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_getstatic;
        switch(baseTypeID) {
            case T_byte:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Byte.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangByteTYPE());
                break;
            case T_short:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Short.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangShortTYPE());
                break;
            case T_char:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Character.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangCharacterTYPE());
                break;
            case T_int:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Integer.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangIntegerTYPE());
                break;
            case T_long:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Long.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangLongTYPE());
                break;
            case T_float:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Float.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangFloatTYPE());
                break;
            case T_double:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Double.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangDoubleTYPE());
                break;
            case T_boolean:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Boolean.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangBooleanTYPE());
                break;
            case T_void:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tgetstatic: java.lang.Void.TYPE");
                writeUnsignedShort(constantPool.literalIndexForJavaLangVoidTYPE());
                break;
        }
    }

    /**
 * We didn't call it goto, because there is a conflit with the goto keyword
 */
    public final void goto_(Label label) {
        if (this.wideMode) {
            this.goto_w(label);
            return;
        }
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tgoto:" + label);
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        label.inlineForwardReferencesFromLabelsTargeting(position);
        /*
	 Possible optimization for code such as:
	 public Object foo() {
		boolean b = true;
		if (b) {
			if (b)
				return null;
		} else {
			if (b) {
				return null;
			}
		}
		return null;
	}
	The goto around the else block for the first if will
	be unreachable, because the thenClause of the second if
	returns.
	See inlineForwardReferencesFromLabelsTargeting defined
	on the Label class for the remaining part of this
	optimization.
	 if (!lbl.isBranchTarget(position)) {
		switch(bCodeStream[classFileOffset-1]) {
			case OPC_return :
			case OPC_areturn:
				return;
		}
	}*/
        position++;
        bCodeStream[classFileOffset++] = OPC_goto;
        label.branch();
    }

    public final void goto_w(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tgotow:" + lbl);
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_goto_w;
        lbl.branchWide();
    }

    public final void i2b() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ti2b");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_i2b;
    }

    public final void i2c() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ti2c");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_i2c;
    }

    public final void i2d() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ti2d");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_i2d;
    }

    public final void i2f() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ti2f");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_i2f;
    }

    public final void i2l() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ti2l");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_i2l;
    }

    public final void i2s() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ti2s");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_i2s;
    }

    public final void iadd() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiadd");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iadd;
    }

    public final void iaload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiaload");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iaload;
    }

    public final void iand() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiand");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iand;
    }

    public final void iastore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiastore");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iastore;
    }

    public final void iconst_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ticonst_0");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iconst_0;
    }

    public final void iconst_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ticonst_1");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iconst_1;
    }

    public final void iconst_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ticonst_2");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iconst_2;
    }

    public final void iconst_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ticonst_3");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iconst_3;
    }

    public final void iconst_4() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ticonst_4");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iconst_4;
    }

    public final void iconst_5() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ticonst_5");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iconst_5;
    }

    public final void iconst_m1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ticonst_m1");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iconst_m1;
    }

    public final void idiv() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tidiv");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_idiv;
    }

    public final void if_acmpeq(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_acmpeq:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_acmpne, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_acmpeq;
            lbl.branch();
        }
    }

    public final void if_acmpne(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_acmpne:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_acmpeq, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_acmpne;
            lbl.branch();
        }
    }

    public final void if_icmpeq(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_cmpeq:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_icmpne, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_icmpeq;
            lbl.branch();
        }
    }

    public final void if_icmpge(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_iacmpge:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_icmplt, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_icmpge;
            lbl.branch();
        }
    }

    public final void if_icmpgt(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_iacmpgt:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_icmple, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_icmpgt;
            lbl.branch();
        }
    }

    public final void if_icmple(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_iacmple:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_icmpgt, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_icmple;
            lbl.branch();
        }
    }

    public final void if_icmplt(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_iacmplt:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_icmpge, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_icmplt;
            lbl.branch();
        }
    }

    public final void if_icmpne(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tif_iacmpne:" + lbl);
        countLabels = 0;
        stackDepth -= 2;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_if_icmpeq, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_if_icmpne;
            lbl.branch();
        }
    }

    public final void ifeq(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tifeq:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_ifne, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ifeq;
            lbl.branch();
        }
    }

    public final void ifge(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tifge:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_iflt, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ifge;
            lbl.branch();
        }
    }

    public final void ifgt(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tifgt:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_ifle, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ifgt;
            lbl.branch();
        }
    }

    public final void ifle(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tifle:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_ifgt, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ifle;
            lbl.branch();
        }
    }

    public final void iflt(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiflt:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_ifge, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_iflt;
            lbl.branch();
        }
    }

    public final void ifne(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tifne:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_ifeq, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ifne;
            lbl.branch();
        }
    }

    public final void ifnonnull(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tifnonnull:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_ifnull, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ifnonnull;
            lbl.branch();
        }
    }

    public final void ifnull(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tifnull:" + lbl);
        countLabels = 0;
        stackDepth--;
        if (this.wideMode) {
            generateWideRevertedConditionalBranch(OPC_ifnonnull, lbl);
        } else {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ifnull;
            lbl.branch();
        }
    }

    public final void iinc(int index, int value) {
        //$NON-NLS-1$ //$NON-NLS-2$
        if (DEBUG)
            System.out.println(position + "\t\tiinc:" + index + "," + value);
        countLabels = 0;
        if (// have to widen
        (index > 255) || (value < -128 || value > 127)) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_iinc;
            writeUnsignedShort(index);
            writeSignedShort(value);
        } else {
            if (classFileOffset + 2 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 3;
            bCodeStream[classFileOffset++] = OPC_iinc;
            bCodeStream[classFileOffset++] = (byte) index;
            bCodeStream[classFileOffset++] = (byte) value;
        }
    }

    public final void iload(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiload:" + iArg);
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= iArg) {
            maxLocals = iArg + 1;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_iload;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_iload;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void iload_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiload_0");
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= 0) {
            maxLocals = 1;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iload_0;
    }

    public final void iload_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiload_1");
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= 1) {
            maxLocals = 2;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iload_1;
    }

    public final void iload_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiload_2");
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= 2) {
            maxLocals = 3;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iload_2;
    }

    public final void iload_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiload_3");
        countLabels = 0;
        stackDepth++;
        if (maxLocals <= 3) {
            maxLocals = 4;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iload_3;
    }

    public final void imul() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\timul");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_imul;
    }

    public void incrementTemp(LocalVariableBinding localBinding, int value) {
        if (value == (short) value) {
            this.iinc(localBinding.resolvedPosition, value);
            return;
        }
        load(localBinding);
        this.ldc(value);
        this.iadd();
        store(localBinding, false);
    }

    public void incrStackSize(int offset) {
        if ((stackDepth += offset) > stackMax)
            stackMax = stackDepth;
    }

    public int indexOfSameLineEntrySincePC(int pc, int line) {
        for (int index = pc, max = pcToSourceMapSize; index < max; index += 2) {
            if (pcToSourceMap[index + 1] == line)
                return index;
        }
        return -1;
    }

    public final void ineg() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tineg");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ineg;
    }

    public void init(ClassFile targetClassFile) {
        this.classFile = targetClassFile;
        this.constantPool = targetClassFile.constantPool;
        this.bCodeStream = targetClassFile.contents;
        this.classFileOffset = targetClassFile.contentsOffset;
        this.startingClassFileOffset = this.classFileOffset;
        pcToSourceMapSize = 0;
        lastEntryPC = 0;
        int length = visibleLocals.length;
        if (noVisibleLocals.length < length) {
            noVisibleLocals = new LocalVariableBinding[length];
        }
        System.arraycopy(noVisibleLocals, 0, visibleLocals, 0, length);
        visibleLocalsCount = 0;
        length = locals.length;
        if (noLocals.length < length) {
            noLocals = new LocalVariableBinding[length];
        }
        System.arraycopy(noLocals, 0, locals, 0, length);
        allLocalsCounter = 0;
        length = exceptionHandlers.length;
        if (noExceptionHandlers.length < length) {
            noExceptionHandlers = new ExceptionLabel[length];
        }
        System.arraycopy(noExceptionHandlers, 0, exceptionHandlers, 0, length);
        exceptionHandlersIndex = 0;
        exceptionHandlersCounter = 0;
        length = labels.length;
        if (noLabels.length < length) {
            noLabels = new Label[length];
        }
        System.arraycopy(noLabels, 0, labels, 0, length);
        countLabels = 0;
        stackMax = 0;
        stackDepth = 0;
        maxLocals = 0;
        position = 0;
    }

    /**
 * @param methodBinding the given method binding to initialize the max locals
 */
    public void initializeMaxLocals(MethodBinding methodBinding) {
        maxLocals = (methodBinding == null || methodBinding.isStatic()) ? 0 : 1;
        // take into account the synthetic parameters
        if (methodBinding != null) {
            if (methodBinding.isConstructor() && methodBinding.declaringClass.isNestedType()) {
                ReferenceBinding enclosingInstanceTypes[];
                if ((enclosingInstanceTypes = methodBinding.declaringClass.syntheticEnclosingInstanceTypes()) != null) {
                    for (int i = 0, max = enclosingInstanceTypes.length; i < max; i++) {
                        // an enclosingInstanceType can only be a reference binding. It cannot be
                        maxLocals++;
                    // LongBinding or DoubleBinding
                    }
                }
                SyntheticArgumentBinding syntheticArguments[];
                if ((syntheticArguments = methodBinding.declaringClass.syntheticOuterLocalVariables()) != null) {
                    for (int i = 0, max = syntheticArguments.length; i < max; i++) {
                        TypeBinding argType;
                        if (((argType = syntheticArguments[i].type) == LongBinding) || (argType == DoubleBinding)) {
                            maxLocals += 2;
                        } else {
                            maxLocals++;
                        }
                    }
                }
            }
            TypeBinding[] arguments;
            if ((arguments = methodBinding.parameters) != null) {
                for (int i = 0, max = arguments.length; i < max; i++) {
                    TypeBinding argType;
                    if (((argType = arguments[i]) == LongBinding) || (argType == DoubleBinding)) {
                        maxLocals += 2;
                    } else {
                        maxLocals++;
                    }
                }
            }
        }
    }

    /**
 * This methods searches for an existing entry inside the pcToSourceMap table with a pc equals to @pc.
 * If there is an existing entry it returns -1 (no insertion required).
 * Otherwise it returns the index where the entry for the pc has to be inserted.
 * This is based on the fact that the pcToSourceMap table is sorted according to the pc.
 *
 * @param pcToSourceMap the given pcToSourceMap array
 * @param length the given length
 * @param pc the given pc
 * @return int
 */
    public static int insertionIndex(int[] pcToSourceMap, int length, int pc) {
        int g = 0;
        int d = length - 2;
        int m = 0;
        while (g <= d) {
            m = (g + d) / 2;
            // we search only on even indexes
            if ((m % 2) != 0)
                m--;
            int currentPC = pcToSourceMap[m];
            if (pc < currentPC) {
                d = m - 2;
            } else if (pc > currentPC) {
                g = m + 2;
            } else {
                return -1;
            }
        }
        if (pc < pcToSourceMap[m])
            return m;
        return m + 2;
    }

    /**
 * We didn't call it instanceof because there is a conflit with the
 * instanceof keyword
 */
    public final void instance_of(TypeBinding typeBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinstance_of:" + typeBinding);
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_instanceof;
        writeUnsignedShort(constantPool.literalIndex(typeBinding));
    }

    public void invokeClassForName() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokestatic: java.lang.Class.forName(Ljava.lang.String;)Ljava.lang.Class;");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokestatic;
        writeUnsignedShort(constantPool.literalIndexForJavaLangClassForName());
    }

    public void invokeJavaLangClassDesiredAssertionStatus() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokevirtual: java.lang.Class.desiredAssertionStatus()Z;");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        writeUnsignedShort(constantPool.literalIndexForJavaLangClassDesiredAssertionStatus());
    }

    public void invokeJavaLangClassGetComponentType() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokevirtual: java.lang.Class.getComponentType()java.lang.Class;");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        writeUnsignedShort(constantPool.literalIndexForJavaLangClassGetComponentType());
    }

    public final void invokeinterface(MethodBinding methodBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokeinterface: " + methodBinding);
        countLabels = 0;
        int argCount = 1;
        int id;
        if (classFileOffset + 4 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 3;
        bCodeStream[classFileOffset++] = OPC_invokeinterface;
        writeUnsignedShort(constantPool.literalIndex(methodBinding));
        for (int i = methodBinding.parameters.length - 1; i >= 0; i--) if (((id = methodBinding.parameters[i].id) == T_double) || (id == T_long))
            argCount += 2;
        else
            argCount += 1;
        bCodeStream[classFileOffset++] = (byte) argCount;
        // Generate a  0 into the byte array. Like the array is already fill with 0, we just need to increment
        // the number of bytes.
        bCodeStream[classFileOffset++] = 0;
        if (((id = methodBinding.returnType.id) == T_double) || (id == T_long)) {
            stackDepth += (2 - argCount);
        } else {
            if (id == T_void) {
                stackDepth -= argCount;
            } else {
                stackDepth += (1 - argCount);
            }
        }
        if (stackDepth > stackMax) {
            stackMax = stackDepth;
        }
    }

    public void invokeJavaLangErrorConstructor() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokespecial: java.lang.Error<init>(Ljava.lang.String;)V");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokespecial;
        stackDepth -= 2;
        writeUnsignedShort(constantPool.literalIndexForJavaLangErrorConstructor());
    }

    public void invokeNoClassDefFoundErrorStringConstructor() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokespecial: java.lang.NoClassDefFoundError.<init>(Ljava.lang.String;)V");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokespecial;
        stackDepth -= 2;
        writeUnsignedShort(constantPool.literalIndexForJavaLangNoClassDefFoundErrorStringConstructor());
    }

    public void invokeObjectGetClass() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokevirtual: java.lang.Object.getClass()Ljava.lang.Class;");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        writeUnsignedShort(constantPool.literalIndexForJavaLangObjectGetClass());
    }

    public final void invokespecial(MethodBinding methodBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokespecial:" + methodBinding);
        // initialized to 1 to take into account this  immediately
        countLabels = 0;
        int argCount = 1;
        int id;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokespecial;
        writeUnsignedShort(constantPool.literalIndex(methodBinding));
        if (methodBinding.isConstructor() && methodBinding.declaringClass.isNestedType()) {
            // enclosing instances
            TypeBinding[] syntheticArgumentTypes = methodBinding.declaringClass.syntheticEnclosingInstanceTypes();
            if (syntheticArgumentTypes != null) {
                for (int i = 0, max = syntheticArgumentTypes.length; i < max; i++) {
                    if (((id = syntheticArgumentTypes[i].id) == T_double) || (id == T_long)) {
                        argCount += 2;
                    } else {
                        argCount++;
                    }
                }
            }
            // outer local variables
            SyntheticArgumentBinding[] syntheticArguments = methodBinding.declaringClass.syntheticOuterLocalVariables();
            if (syntheticArguments != null) {
                for (int i = 0, max = syntheticArguments.length; i < max; i++) {
                    if (((id = syntheticArguments[i].type.id) == T_double) || (id == T_long)) {
                        argCount += 2;
                    } else {
                        argCount++;
                    }
                }
            }
        }
        for (int i = methodBinding.parameters.length - 1; i >= 0; i--) if (((id = methodBinding.parameters[i].id) == T_double) || (id == T_long))
            argCount += 2;
        else
            argCount++;
        if (((id = methodBinding.returnType.id) == T_double) || (id == T_long))
            stackDepth += (2 - argCount);
        else if (id == T_void)
            stackDepth -= argCount;
        else
            stackDepth += (1 - argCount);
        if (stackDepth > stackMax)
            stackMax = stackDepth;
    }

    public final void invokestatic(MethodBinding methodBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokestatic:" + methodBinding);
        // initialized to 0 to take into account that there is no this for
        // a static method
        countLabels = 0;
        int argCount = 0;
        int id;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokestatic;
        writeUnsignedShort(constantPool.literalIndex(methodBinding));
        for (int i = methodBinding.parameters.length - 1; i >= 0; i--) if (((id = methodBinding.parameters[i].id) == T_double) || (id == T_long))
            argCount += 2;
        else
            argCount += 1;
        if (((id = methodBinding.returnType.id) == T_double) || (id == T_long))
            stackDepth += (2 - argCount);
        else if (id == T_void)
            stackDepth -= argCount;
        else
            stackDepth += (1 - argCount);
        if (stackDepth > stackMax)
            stackMax = stackDepth;
    }

    /**
 * The equivalent code performs a string conversion of the TOS
 * @param typeID <CODE>int</CODE>
 */
    public void invokeStringConcatenationAppendForType(int typeID) {
        if (DEBUG) {
            if (this.targetLevel >= JDK1_5) {
                //$NON-NLS-1$
                System.out.println(position + "\t\tinvokevirtual: java.lang.StringBuilder.append(...)");
            } else {
                //$NON-NLS-1$
                System.out.println(position + "\t\tinvokevirtual: java.lang.StringBuffer.append(...)");
            }
        }
        countLabels = 0;
        int usedTypeID;
        if (typeID == T_null) {
            usedTypeID = T_String;
        } else {
            usedTypeID = typeID;
        }
        // invokevirtual
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        if (this.targetLevel >= JDK1_5) {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBuilderAppend(typeID));
        } else {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBufferAppend(typeID));
        }
        if ((usedTypeID == T_long) || (usedTypeID == T_double)) {
            stackDepth -= 2;
        } else {
            stackDepth--;
        }
    }

    public void invokeJavaLangAssertionErrorConstructor(int typeBindingID) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokespecial: java.lang.AssertionError.<init>(typeBindingID)V");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokespecial;
        writeUnsignedShort(constantPool.literalIndexForJavaLangAssertionErrorConstructor(typeBindingID));
        stackDepth -= 2;
    }

    public void invokeJavaLangAssertionErrorDefaultConstructor() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokespecial: java.lang.AssertionError.<init>()V");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokespecial;
        writeUnsignedShort(constantPool.literalIndexForJavaLangAssertionErrorDefaultConstructor());
        stackDepth--;
    }

    public void invokeJavaUtilIteratorHasNext() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokeinterface: java.util.Iterator.hasNext()Z");
        countLabels = 0;
        if (classFileOffset + 4 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 3;
        bCodeStream[classFileOffset++] = OPC_invokeinterface;
        writeUnsignedShort(constantPool.literalIndexForJavaUtilIteratorHasNext());
        bCodeStream[classFileOffset++] = 1;
        // Generate a  0 into the byte array. Like the array is already fill with 0, we just need to increment
        // the number of bytes.
        bCodeStream[classFileOffset++] = 0;
    }

    public void invokeJavaUtilIteratorNext() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokeinterface: java.util.Iterator.next()java.lang.Object");
        countLabels = 0;
        if (classFileOffset + 4 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 3;
        bCodeStream[classFileOffset++] = OPC_invokeinterface;
        writeUnsignedShort(constantPool.literalIndexForJavaUtilIteratorNext());
        bCodeStream[classFileOffset++] = 1;
        // Generate a  0 into the byte array. Like the array is already fill with 0, we just need to increment
        // the number of bytes.
        bCodeStream[classFileOffset++] = 0;
    }

    public void invokeStringConcatenationDefaultConstructor() {
        // invokespecial: java.lang.StringBuffer.<init>()V
        if (DEBUG) {
            if (this.targetLevel >= JDK1_5) {
                //$NON-NLS-1$
                System.out.println(position + "\t\tinvokespecial: java.lang.StringBuilder.<init>()V");
            } else {
                //$NON-NLS-1$
                System.out.println(position + "\t\tinvokespecial: java.lang.StringBuffer.<init>()V");
            }
        }
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokespecial;
        if (this.targetLevel >= JDK1_5) {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBuilderDefaultConstructor());
        } else {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBufferDefaultConstructor());
        }
        stackDepth--;
    }

    public void invokeStringConcatenationStringConstructor() {
        if (DEBUG) {
            if (this.targetLevel >= JDK1_5) {
                //$NON-NLS-1$
                System.out.println(position + "\t\tjava.lang.StringBuilder.<init>(Ljava.lang.String;)V");
            } else {
                //$NON-NLS-1$
                System.out.println(position + "\t\tjava.lang.StringBuffer.<init>(Ljava.lang.String;)V");
            }
        }
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokespecial;
        if (this.targetLevel >= JDK1_5) {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBuilderConstructor());
        } else {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBufferConstructor());
        }
        stackDepth -= 2;
    }

    public void invokeStringConcatenationToString() {
        if (DEBUG) {
            if (this.targetLevel >= JDK1_5) {
                //$NON-NLS-1$
                System.out.println(position + "\t\tinvokevirtual: StringBuilder.toString()Ljava.lang.String;");
            } else {
                //$NON-NLS-1$
                System.out.println(position + "\t\tinvokevirtual: StringBuffer.toString()Ljava.lang.String;");
            }
        }
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        if (this.targetLevel >= JDK1_5) {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBuilderToString());
        } else {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBufferToString());
        }
    }

    public void invokeStringIntern() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokevirtual: java.lang.String.intern()");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        writeUnsignedShort(constantPool.literalIndexForJavaLangStringIntern());
    }

    public void invokeStringValueOf(int typeID) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokestatic: java.lang.String.valueOf(...)");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokestatic;
        writeUnsignedShort(constantPool.literalIndexForJavaLangStringValueOf(typeID));
    }

    public void invokeThrowableGetMessage() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokevirtual: java.lang.Throwable.getMessage()Ljava.lang.String;");
        countLabels = 0;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        writeUnsignedShort(constantPool.literalIndexForJavaLangThrowableGetMessage());
    }

    public final void invokevirtual(MethodBinding methodBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tinvokevirtual:" + methodBinding);
        // initialized to 1 to take into account this  immediately
        countLabels = 0;
        int argCount = 1;
        int id;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_invokevirtual;
        writeUnsignedShort(constantPool.literalIndex(methodBinding));
        for (int i = methodBinding.parameters.length - 1; i >= 0; i--) if (((id = methodBinding.parameters[i].id) == T_double) || (id == T_long))
            argCount += 2;
        else
            argCount++;
        if (((id = methodBinding.returnType.id) == T_double) || (id == T_long))
            stackDepth += (2 - argCount);
        else if (id == T_void)
            stackDepth -= argCount;
        else
            stackDepth += (1 - argCount);
        if (stackDepth > stackMax)
            stackMax = stackDepth;
    }

    public final void ior() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tior");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ior;
    }

    public final void irem() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tirem");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_irem;
    }

    public final void ireturn() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tireturn");
        countLabels = 0;
        stackDepth--;
        // the stackDepth should be equal to 0 
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ireturn;
    }

    public boolean isDefinitelyAssigned(Scope scope, int initStateIndex, LocalVariableBinding local) {
        // Dependant of UnconditionalFlowInfo.isDefinitelyAssigned(..)
        if (initStateIndex == -1)
            return false;
        if (local.isArgument) {
            return true;
        }
        int localPosition = local.id + maxFieldCount;
        MethodScope methodScope = scope.methodScope();
        // id is zero-based
        if (localPosition < UnconditionalFlowInfo.BitCacheSize) {
            // use bits
            return (methodScope.definiteInits[initStateIndex] & (1L << localPosition)) != 0;
        }
        // use extra vector
        long[] extraInits = methodScope.extraDefiniteInits[initStateIndex];
        if (extraInits == null)
            // if vector not yet allocated, then not initialized
            return false;
        int vectorIndex;
        if ((vectorIndex = (localPosition / UnconditionalFlowInfo.BitCacheSize) - 1) >= extraInits.length)
            // if not enough room in vector, then not initialized 
            return false;
        return ((extraInits[vectorIndex]) & (1L << (localPosition % UnconditionalFlowInfo.BitCacheSize))) != 0;
    }

    public final void ishl() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tishl");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ishl;
    }

    public final void ishr() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tishr");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ishr;
    }

    public final void istore(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tistore:" + iArg);
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= iArg) {
            maxLocals = iArg + 1;
        }
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_istore;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_istore;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void istore_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tistore_0");
        countLabels = 0;
        stackDepth--;
        if (maxLocals == 0) {
            maxLocals = 1;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_istore_0;
    }

    public final void istore_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tistore_1");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 1) {
            maxLocals = 2;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_istore_1;
    }

    public final void istore_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tistore_2");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 2) {
            maxLocals = 3;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_istore_2;
    }

    public final void istore_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tistore_3");
        countLabels = 0;
        stackDepth--;
        if (maxLocals <= 3) {
            maxLocals = 4;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_istore_3;
    }

    public final void isub() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tisub");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_isub;
    }

    public final void iushr() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tiushr");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_iushr;
    }

    public final void ixor() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tixor");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ixor;
    }

    public final void jsr(Label lbl) {
        if (this.wideMode) {
            this.jsr_w(lbl);
            return;
        }
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tjsr" + lbl);
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_jsr;
        lbl.branch();
    }

    public final void jsr_w(Label lbl) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tjsr_w" + lbl);
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_jsr_w;
        lbl.branchWide();
    }

    public final void l2d() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tl2d");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_l2d;
    }

    public final void l2f() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tl2f");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_l2f;
    }

    public final void l2i() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tl2i");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_l2i;
    }

    public final void ladd() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tladd");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ladd;
    }

    public final void laload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlaload");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_laload;
    }

    public final void land() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tland");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_land;
    }

    public final void lastore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlastore");
        countLabels = 0;
        stackDepth -= 4;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lastore;
    }

    public final void lcmp() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlcmp");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lcmp;
    }

    public final void lconst_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlconst_0");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lconst_0;
    }

    public final void lconst_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlconst_1");
        countLabels = 0;
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lconst_1;
    }

    public final void ldc(float constant) {
        countLabels = 0;
        int index = constantPool.literalIndex(constant);
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (index > 255) {
            //$NON-NLS-1$
            if (DEBUG)
                System.out.println(position + "\t\tldc_w:" + constant);
            // Generate a ldc_w
            if (classFileOffset + 2 >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ldc_w;
            writeUnsignedShort(index);
        } else {
            //$NON-NLS-1$
            if (DEBUG)
                System.out.println(position + "\t\tldc:" + constant);
            // Generate a ldc
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_ldc;
            bCodeStream[classFileOffset++] = (byte) index;
        }
    }

    public final void ldc(int constant) {
        countLabels = 0;
        int index = constantPool.literalIndex(constant);
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (index > 255) {
            //$NON-NLS-1$
            if (DEBUG)
                System.out.println(position + "\t\tldc_w:" + constant);
            // Generate a ldc_w
            if (classFileOffset + 2 >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ldc_w;
            writeUnsignedShort(index);
        } else {
            //$NON-NLS-1$
            if (DEBUG)
                System.out.println(position + "\t\tldc:" + constant);
            // Generate a ldc
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_ldc;
            bCodeStream[classFileOffset++] = (byte) index;
        }
    }

    public final void ldc(String constant) {
        countLabels = 0;
        int currentConstantPoolIndex = constantPool.currentIndex;
        int currentConstantPoolOffset = constantPool.currentOffset;
        int currentCodeStreamPosition = position;
        int index = constantPool.literalIndexForLdc(constant.toCharArray());
        if (index > 0) {
            // the string already exists inside the constant pool
            // we reuse the same index
            stackDepth++;
            if (stackDepth > stackMax)
                stackMax = stackDepth;
            if (index > 255) {
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tldc_w:" + constant);
                // Generate a ldc_w
                if (classFileOffset + 2 >= bCodeStream.length) {
                    resizeByteArray();
                }
                position++;
                bCodeStream[classFileOffset++] = OPC_ldc_w;
                writeUnsignedShort(index);
            } else {
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tldc:" + constant);
                // Generate a ldc
                if (classFileOffset + 1 >= bCodeStream.length) {
                    resizeByteArray();
                }
                position += 2;
                bCodeStream[classFileOffset++] = OPC_ldc;
                bCodeStream[classFileOffset++] = (byte) index;
            }
        } else {
            // the string is too big to be utf8-encoded in one pass.
            // we have to split it into different pieces.
            // first we clean all side-effects due to the code above
            // this case is very rare, so we can afford to lose time to handle it
            char[] constantChars = constant.toCharArray();
            position = currentCodeStreamPosition;
            constantPool.currentIndex = currentConstantPoolIndex;
            constantPool.currentOffset = currentConstantPoolOffset;
            constantPool.stringCache.remove(constantChars);
            constantPool.UTF8Cache.remove(constantChars);
            int i = 0;
            int length = 0;
            int constantLength = constant.length();
            byte[] utf8encoding = new byte[Math.min(constantLength + 100, 65535)];
            int utf8encodingLength = 0;
            while ((length < 65532) && (i < constantLength)) {
                char current = constantChars[i];
                // we resize the byte array immediately if necessary
                if (length + 3 > (utf8encodingLength = utf8encoding.length)) {
                    System.arraycopy(utf8encoding, 0, utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)], 0, length);
                }
                if ((current >= 0x0001) && (current <= 0x007F)) {
                    // we only need one byte: ASCII table
                    utf8encoding[length++] = (byte) current;
                } else {
                    if (current > 0x07FF) {
                        // we need 3 bytes
                        // 0xE0 = 1110 0000
                        utf8encoding[length++] = (byte) (0xE0 | ((current >> 12) & 0x0F));
                        // 0x80 = 1000 0000
                        utf8encoding[length++] = (byte) (0x80 | ((current >> 6) & 0x3F));
                        // 0x80 = 1000 0000
                        utf8encoding[length++] = (byte) (0x80 | (current & 0x3F));
                    } else {
                        // we can be 0 or between 0x0080 and 0x07FF
                        // In that case we only need 2 bytes
                        // 0xC0 = 1100 0000
                        utf8encoding[length++] = (byte) (0xC0 | ((current >> 6) & 0x1F));
                        // 0x80 = 1000 0000
                        utf8encoding[length++] = (byte) (0x80 | (current & 0x3F));
                    }
                }
                i++;
            }
            // check if all the string is encoded (PR 1PR2DWJ)
            // the string is too big to be encoded in one pass
            newStringContatenation();
            dup();
            // write the first part
            char[] subChars = new char[i];
            System.arraycopy(constantChars, 0, subChars, 0, i);
            System.arraycopy(utf8encoding, 0, utf8encoding = new byte[length], 0, length);
            index = constantPool.literalIndex(subChars, utf8encoding);
            stackDepth++;
            if (stackDepth > stackMax)
                stackMax = stackDepth;
            if (index > 255) {
                // Generate a ldc_w
                if (classFileOffset + 2 >= bCodeStream.length) {
                    resizeByteArray();
                }
                position++;
                bCodeStream[classFileOffset++] = OPC_ldc_w;
                writeUnsignedShort(index);
            } else {
                // Generate a ldc
                if (classFileOffset + 1 >= bCodeStream.length) {
                    resizeByteArray();
                }
                position += 2;
                bCodeStream[classFileOffset++] = OPC_ldc;
                bCodeStream[classFileOffset++] = (byte) index;
            }
            // write the remaining part
            invokeStringConcatenationStringConstructor();
            while (i < constantLength) {
                length = 0;
                utf8encoding = new byte[Math.min(constantLength - i + 100, 65535)];
                int startIndex = i;
                while ((length < 65532) && (i < constantLength)) {
                    char current = constantChars[i];
                    // we resize the byte array immediately if necessary
                    if (constantLength + 2 > (utf8encodingLength = utf8encoding.length)) {
                        System.arraycopy(utf8encoding, 0, utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)], 0, length);
                    }
                    if ((current >= 0x0001) && (current <= 0x007F)) {
                        // we only need one byte: ASCII table
                        utf8encoding[length++] = (byte) current;
                    } else {
                        if (current > 0x07FF) {
                            // we need 3 bytes
                            utf8encoding[length++] = (byte) (0xE0 | (// 0xE0 = 1110 0000
                            (current >> 12) & // 0xE0 = 1110 0000
                            0x0F));
                            utf8encoding[length++] = (byte) (0x80 | (// 0x80 = 1000 0000
                            (current >> 6) & // 0x80 = 1000 0000
                            0x3F));
                            utf8encoding[length++] = (byte) (// 0x80 = 1000 0000
                            0x80 | // 0x80 = 1000 0000
                            (current & 0x3F));
                        } else {
                            // we can be 0 or between 0x0080 and 0x07FF
                            // In that case we only need 2 bytes
                            utf8encoding[length++] = (byte) (0xC0 | (// 0xC0 = 1100 0000
                            (current >> 6) & // 0xC0 = 1100 0000
                            0x1F));
                            utf8encoding[length++] = (byte) (// 0x80 = 1000 0000
                            0x80 | // 0x80 = 1000 0000
                            (current & 0x3F));
                        }
                    }
                    i++;
                }
                // the next part is done
                subChars = new char[i - startIndex];
                System.arraycopy(constantChars, startIndex, subChars, 0, i - startIndex);
                System.arraycopy(utf8encoding, 0, utf8encoding = new byte[length], 0, length);
                index = constantPool.literalIndex(subChars, utf8encoding);
                stackDepth++;
                if (stackDepth > stackMax)
                    stackMax = stackDepth;
                if (index > 255) {
                    // Generate a ldc_w
                    if (classFileOffset + 2 >= bCodeStream.length) {
                        resizeByteArray();
                    }
                    position++;
                    bCodeStream[classFileOffset++] = OPC_ldc_w;
                    writeUnsignedShort(index);
                } else {
                    // Generate a ldc
                    if (classFileOffset + 1 >= bCodeStream.length) {
                        resizeByteArray();
                    }
                    position += 2;
                    bCodeStream[classFileOffset++] = OPC_ldc;
                    bCodeStream[classFileOffset++] = (byte) index;
                }
                // now on the stack it should be a StringBuffer and a string.
                invokeStringConcatenationAppendForType(T_String);
            }
            invokeStringConcatenationToString();
            invokeStringIntern();
        }
    }

    public final void ldc(TypeBinding typeBinding) {
        countLabels = 0;
        int index = constantPool.literalIndex(typeBinding);
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (index > 255) {
            //$NON-NLS-1$
            if (DEBUG)
                System.out.println(position + "\t\tldc_w:" + typeBinding);
            // Generate a ldc_w
            if (classFileOffset + 2 >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = OPC_ldc_w;
            writeUnsignedShort(index);
        } else {
            //$NON-NLS-1$
            if (DEBUG)
                System.out.println(position + "\t\tldw:" + typeBinding);
            // Generate a ldc
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_ldc;
            bCodeStream[classFileOffset++] = (byte) index;
        }
    }

    public final void ldc2_w(double constant) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tldc2_w:" + constant);
        countLabels = 0;
        int index = constantPool.literalIndex(constant);
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        // Generate a ldc2_w
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ldc2_w;
        writeUnsignedShort(index);
    }

    public final void ldc2_w(long constant) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tldc2_w:" + constant);
        countLabels = 0;
        int index = constantPool.literalIndex(constant);
        stackDepth += 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        // Generate a ldc2_w
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ldc2_w;
        writeUnsignedShort(index);
    }

    public final void ldiv() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tldiv");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_ldiv;
    }

    public final void lload(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlload:" + iArg);
        countLabels = 0;
        stackDepth += 2;
        if (maxLocals <= iArg + 1) {
            maxLocals = iArg + 2;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_lload;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_lload;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void lload_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlload_0");
        countLabels = 0;
        stackDepth += 2;
        if (maxLocals < 2) {
            maxLocals = 2;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lload_0;
    }

    public final void lload_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlload_1");
        countLabels = 0;
        stackDepth += 2;
        if (maxLocals < 3) {
            maxLocals = 3;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lload_1;
    }

    public final void lload_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlload_2");
        countLabels = 0;
        stackDepth += 2;
        if (maxLocals < 4) {
            maxLocals = 4;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lload_2;
    }

    public final void lload_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlload_3");
        countLabels = 0;
        stackDepth += 2;
        if (maxLocals < 5) {
            maxLocals = 5;
        }
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lload_3;
    }

    public final void lmul() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlmul");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lmul;
    }

    public final void lneg() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlneg");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lneg;
    }

    public final void load(LocalVariableBinding localBinding) {
        countLabels = 0;
        TypeBinding typeBinding = localBinding.type;
        int resolvedPosition = localBinding.resolvedPosition;
        // Using dedicated int bytecode
        if (typeBinding == IntBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.iload_0();
                    break;
                case 1:
                    this.iload_1();
                    break;
                case 2:
                    this.iload_2();
                    break;
                case 3:
                    this.iload_3();
                    break;
                //	break;
                default:
                    this.iload(resolvedPosition);
            }
            return;
        }
        // Using dedicated float bytecode
        if (typeBinding == FloatBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.fload_0();
                    break;
                case 1:
                    this.fload_1();
                    break;
                case 2:
                    this.fload_2();
                    break;
                case 3:
                    this.fload_3();
                    break;
                default:
                    this.fload(resolvedPosition);
            }
            return;
        }
        // Using dedicated long bytecode
        if (typeBinding == LongBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.lload_0();
                    break;
                case 1:
                    this.lload_1();
                    break;
                case 2:
                    this.lload_2();
                    break;
                case 3:
                    this.lload_3();
                    break;
                default:
                    this.lload(resolvedPosition);
            }
            return;
        }
        // Using dedicated double bytecode
        if (typeBinding == DoubleBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.dload_0();
                    break;
                case 1:
                    this.dload_1();
                    break;
                case 2:
                    this.dload_2();
                    break;
                case 3:
                    this.dload_3();
                    break;
                default:
                    this.dload(resolvedPosition);
            }
            return;
        }
        // boolean, byte, char and short are handled as int
        if ((typeBinding == ByteBinding) || (typeBinding == CharBinding) || (typeBinding == BooleanBinding) || (typeBinding == ShortBinding)) {
            switch(resolvedPosition) {
                case 0:
                    this.iload_0();
                    break;
                case 1:
                    this.iload_1();
                    break;
                case 2:
                    this.iload_2();
                    break;
                case 3:
                    this.iload_3();
                    break;
                default:
                    this.iload(resolvedPosition);
            }
            return;
        }
        // Reference object
        switch(resolvedPosition) {
            case 0:
                this.aload_0();
                break;
            case 1:
                this.aload_1();
                break;
            case 2:
                this.aload_2();
                break;
            case 3:
                this.aload_3();
                break;
            default:
                this.aload(resolvedPosition);
        }
    }

    public final void load(TypeBinding typeBinding, int resolvedPosition) {
        countLabels = 0;
        // Using dedicated int bytecode
        if (typeBinding == IntBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.iload_0();
                    break;
                case 1:
                    this.iload_1();
                    break;
                case 2:
                    this.iload_2();
                    break;
                case 3:
                    this.iload_3();
                    break;
                default:
                    this.iload(resolvedPosition);
            }
            return;
        }
        // Using dedicated float bytecode
        if (typeBinding == FloatBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.fload_0();
                    break;
                case 1:
                    this.fload_1();
                    break;
                case 2:
                    this.fload_2();
                    break;
                case 3:
                    this.fload_3();
                    break;
                default:
                    this.fload(resolvedPosition);
            }
            return;
        }
        // Using dedicated long bytecode
        if (typeBinding == LongBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.lload_0();
                    break;
                case 1:
                    this.lload_1();
                    break;
                case 2:
                    this.lload_2();
                    break;
                case 3:
                    this.lload_3();
                    break;
                default:
                    this.lload(resolvedPosition);
            }
            return;
        }
        // Using dedicated double bytecode
        if (typeBinding == DoubleBinding) {
            switch(resolvedPosition) {
                case 0:
                    this.dload_0();
                    break;
                case 1:
                    this.dload_1();
                    break;
                case 2:
                    this.dload_2();
                    break;
                case 3:
                    this.dload_3();
                    break;
                default:
                    this.dload(resolvedPosition);
            }
            return;
        }
        // boolean, byte, char and short are handled as int
        if ((typeBinding == ByteBinding) || (typeBinding == CharBinding) || (typeBinding == BooleanBinding) || (typeBinding == ShortBinding)) {
            switch(resolvedPosition) {
                case 0:
                    this.iload_0();
                    break;
                case 1:
                    this.iload_1();
                    break;
                case 2:
                    this.iload_2();
                    break;
                case 3:
                    this.iload_3();
                    break;
                default:
                    this.iload(resolvedPosition);
            }
            return;
        }
        // Reference object
        switch(resolvedPosition) {
            case 0:
                this.aload_0();
                break;
            case 1:
                this.aload_1();
                break;
            case 2:
                this.aload_2();
                break;
            case 3:
                this.aload_3();
                break;
            default:
                this.aload(resolvedPosition);
        }
    }

    public final void loadInt(int resolvedPosition) {
        // Using dedicated int bytecode
        switch(resolvedPosition) {
            case 0:
                this.iload_0();
                break;
            case 1:
                this.iload_1();
                break;
            case 2:
                this.iload_2();
                break;
            case 3:
                this.iload_3();
                break;
            default:
                this.iload(resolvedPosition);
        }
    }

    public final void loadObject(int resolvedPosition) {
        switch(resolvedPosition) {
            case 0:
                this.aload_0();
                break;
            case 1:
                this.aload_1();
                break;
            case 2:
                this.aload_2();
                break;
            case 3:
                this.aload_3();
                break;
            default:
                this.aload(resolvedPosition);
        }
    }

    public final void lookupswitch(CaseLabel defaultLabel, int[] keys, int[] sortedIndexes, CaseLabel[] casesLabel) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlookupswitch");
        countLabels = 0;
        stackDepth--;
        int length = keys.length;
        int pos = position;
        defaultLabel.placeInstruction();
        for (int i = 0; i < length; i++) {
            casesLabel[i].placeInstruction();
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lookupswitch;
        for (int i = (3 - (pos % 4)); i > 0; i--) {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = 0;
        }
        defaultLabel.branch();
        writeSignedWord(length);
        for (int i = 0; i < length; i++) {
            writeSignedWord(keys[sortedIndexes[i]]);
            casesLabel[sortedIndexes[i]].branch();
        }
    }

    public final void lor() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlor");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lor;
    }

    public final void lrem() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlrem");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lrem;
    }

    public final void lreturn() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlreturn");
        countLabels = 0;
        stackDepth -= 2;
        // the stackDepth should be equal to 0 
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lreturn;
    }

    public final void lshl() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlshl");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lshl;
    }

    public final void lshr() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlshr");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lshr;
    }

    public final void lstore(int iArg) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlstore:" + iArg);
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals <= iArg + 1) {
            maxLocals = iArg + 2;
        }
        if (// Widen
        iArg > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_lstore;
            writeUnsignedShort(iArg);
        } else {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_lstore;
            bCodeStream[classFileOffset++] = (byte) iArg;
        }
    }

    public final void lstore_0() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlstore_0");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 2) {
            maxLocals = 2;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lstore_0;
    }

    public final void lstore_1() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlstore_1");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 3) {
            maxLocals = 3;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lstore_1;
    }

    public final void lstore_2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlstore_2");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 4) {
            maxLocals = 4;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lstore_2;
    }

    public final void lstore_3() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlstore_3");
        countLabels = 0;
        stackDepth -= 2;
        if (maxLocals < 5) {
            maxLocals = 5;
        }
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lstore_3;
    }

    public final void lsub() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlsub");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lsub;
    }

    public final void lushr() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlushr");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lushr;
    }

    public final void lxor() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tlxor");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_lxor;
    }

    public final void monitorenter() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tmonitorenter");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_monitorenter;
    }

    public final void monitorexit() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tmonitorexit");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_monitorexit;
    }

    public final void multianewarray(TypeBinding typeBinding, int dimensions) {
        //$NON-NLS-1$ //$NON-NLS-2$
        if (DEBUG)
            System.out.println(position + "\t\tmultinewarray:" + typeBinding + "," + dimensions);
        countLabels = 0;
        stackDepth += (1 - dimensions);
        if (classFileOffset + 3 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 2;
        bCodeStream[classFileOffset++] = OPC_multianewarray;
        writeUnsignedShort(constantPool.literalIndex(typeBinding));
        bCodeStream[classFileOffset++] = (byte) dimensions;
    }

    /**
 * We didn't call it new, because there is a conflit with the new keyword
 */
    public final void new_(TypeBinding typeBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tnew:" + typeBinding);
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_new;
        writeUnsignedShort(constantPool.literalIndex(typeBinding));
    }

    public final void newarray(int array_Type) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tnewarray:" + array_Type);
        countLabels = 0;
        if (classFileOffset + 1 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 2;
        bCodeStream[classFileOffset++] = OPC_newarray;
        bCodeStream[classFileOffset++] = (byte) array_Type;
    }

    public void newArray(Scope scope, ArrayBinding arrayBinding) {
        TypeBinding component = arrayBinding.elementsType();
        switch(component.id) {
            case T_int:
                this.newarray(INT_ARRAY);
                break;
            case T_byte:
                this.newarray(BYTE_ARRAY);
                break;
            case T_boolean:
                this.newarray(BOOLEAN_ARRAY);
                break;
            case T_short:
                this.newarray(SHORT_ARRAY);
                break;
            case T_char:
                this.newarray(CHAR_ARRAY);
                break;
            case T_long:
                this.newarray(LONG_ARRAY);
                break;
            case T_float:
                this.newarray(FLOAT_ARRAY);
                break;
            case T_double:
                this.newarray(DOUBLE_ARRAY);
                break;
            default:
                this.anewarray(component);
        }
    }

    public void newJavaLangError() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tnew: java.lang.Error");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_new;
        writeUnsignedShort(constantPool.literalIndexForJavaLangError());
    }

    public void newJavaLangAssertionError() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tnew: java.lang.AssertionError");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_new;
        writeUnsignedShort(constantPool.literalIndexForJavaLangAssertionError());
    }

    public void newNoClassDefFoundError() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tnew: java.lang.NoClassDefFoundError");
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_new;
        writeUnsignedShort(constantPool.literalIndexForJavaLangNoClassDefFoundError());
    }

    public void newStringContatenation() {
        // new: java.lang.StringBuilder
        if (DEBUG) {
            if (this.targetLevel >= JDK1_5) {
                //$NON-NLS-1$
                System.out.println(position + "\t\tnew: java.lang.StringBuilder");
            } else {
                //$NON-NLS-1$
                System.out.println(position + "\t\tnew: java.lang.StringBuffer");
            }
        }
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax) {
            stackMax = stackDepth;
        }
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_new;
        if (this.targetLevel >= JDK1_5) {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBuilder());
        } else {
            writeUnsignedShort(constantPool.literalIndexForJavaLangStringBuffer());
        }
    }

    public void newWrapperFor(int typeID) {
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_new;
        switch(typeID) {
            case // new: java.lang.Integer
            T_int:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Integer");
                writeUnsignedShort(constantPool.literalIndexForJavaLangInteger());
                break;
            case // new: java.lang.Boolean
            T_boolean:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Boolean");
                writeUnsignedShort(constantPool.literalIndexForJavaLangBoolean());
                break;
            case // new: java.lang.Byte
            T_byte:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Byte");
                writeUnsignedShort(constantPool.literalIndexForJavaLangByte());
                break;
            case // new: java.lang.Character
            T_char:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Character");
                writeUnsignedShort(constantPool.literalIndexForJavaLangCharacter());
                break;
            case // new: java.lang.Float
            T_float:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Float");
                writeUnsignedShort(constantPool.literalIndexForJavaLangFloat());
                break;
            case // new: java.lang.Double
            T_double:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Double");
                writeUnsignedShort(constantPool.literalIndexForJavaLangDouble());
                break;
            case // new: java.lang.Short
            T_short:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Short");
                writeUnsignedShort(constantPool.literalIndexForJavaLangShort());
                break;
            case // new: java.lang.Long
            T_long:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Long");
                writeUnsignedShort(constantPool.literalIndexForJavaLangLong());
                break;
            case // new: java.lang.Void
            T_void:
                //$NON-NLS-1$
                if (DEBUG)
                    System.out.println(position + "\t\tnew: java.lang.Void");
                writeUnsignedShort(constantPool.literalIndexForJavaLangVoid());
        }
    }

    public final void nop() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tnop");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_nop;
    }

    public final void pop() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tpop");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_pop;
    }

    public final void pop2() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tpop2");
        countLabels = 0;
        stackDepth -= 2;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_pop2;
    }

    public final void putfield(FieldBinding fieldBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tputfield:" + fieldBinding);
        countLabels = 0;
        int id;
        if (((id = fieldBinding.type.id) == T_double) || (id == T_long))
            stackDepth -= 3;
        else
            stackDepth -= 2;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_putfield;
        writeUnsignedShort(constantPool.literalIndex(fieldBinding));
    }

    public final void putstatic(FieldBinding fieldBinding) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tputstatic:" + fieldBinding);
        countLabels = 0;
        int id;
        if (((id = fieldBinding.type.id) == T_double) || (id == T_long))
            stackDepth -= 2;
        else
            stackDepth -= 1;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset + 2 >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_putstatic;
        writeUnsignedShort(constantPool.literalIndex(fieldBinding));
    }

    public void record(LocalVariableBinding local) {
        if (!generateLocalVariableTableAttributes)
            return;
        if (allLocalsCounter == locals.length) {
            // resize the collection
            System.arraycopy(locals, 0, locals = new LocalVariableBinding[allLocalsCounter + LOCALS_INCREMENT], 0, allLocalsCounter);
        }
        locals[allLocalsCounter++] = local;
        local.initializationPCs = new int[4];
        local.initializationCount = 0;
    }

    public void recordPositionsFrom(int startPC, int sourcePos) {
        if (!generateLineNumberAttributes)
            return;
        if (sourcePos == 0)
            return;
        // no code generated for this node. e.g. field without any initialization
        if (position == startPC)
            return;
        // Widening an existing entry that already has the same source positions
        if (pcToSourceMapSize + 4 > pcToSourceMap.length) {
            // resize the array pcToSourceMap
            System.arraycopy(pcToSourceMap, 0, pcToSourceMap = new int[pcToSourceMapSize << 1], 0, pcToSourceMapSize);
        }
        int newLine = ClassFile.searchLineNumber(lineSeparatorPositions, sourcePos);
        // lastEntryPC represents the endPC of the lastEntry.
        if (pcToSourceMapSize > 0) {
            // in this case there is already an entry in the table
            if (pcToSourceMap[pcToSourceMapSize - 1] != newLine) {
                if (startPC < lastEntryPC) {
                    // we forgot to add an entry.
                    // search if an existing entry exists for startPC
                    int insertionIndex = insertionIndex(pcToSourceMap, pcToSourceMapSize, startPC);
                    if (insertionIndex != -1) {
                        // there is no existing entry starting with startPC.
                        int existingEntryIndex = indexOfSameLineEntrySincePC(// index for PC
                        startPC, // index for PC
                        newLine);
                        /* the existingEntryIndex corresponds to en entry with the same line and a PC >= startPC.
						in this case it is relevant to widen this entry instead of creating a new one.
						line1: this(a,
						  b,
						  c);
						with this code we generate each argument. We generate a aload0 to invoke the constructor. There is no entry for this
						aload0 bytecode. The first entry is the one for the argument a.
						But we want the constructor call to start at the aload0 pc and not just at the pc of the first argument.
						So we widen the existing entry (if there is one) or we create a new entry with the startPC.
					*/
                        if (existingEntryIndex != -1) {
                            // widen existing entry
                            pcToSourceMap[existingEntryIndex] = startPC;
                        } else if (insertionIndex < 1 || pcToSourceMap[insertionIndex - 1] != newLine) {
                            // we have to add an entry that won't be sorted. So we sort the pcToSourceMap.
                            System.arraycopy(pcToSourceMap, insertionIndex, pcToSourceMap, insertionIndex + 2, pcToSourceMapSize - insertionIndex);
                            pcToSourceMap[insertionIndex++] = startPC;
                            pcToSourceMap[insertionIndex] = newLine;
                            pcToSourceMapSize += 2;
                        }
                    } else if (// no bytecode since last entry pc
                    position != lastEntryPC) {
                        pcToSourceMap[pcToSourceMapSize++] = lastEntryPC;
                        pcToSourceMap[pcToSourceMapSize++] = newLine;
                    }
                } else {
                    // we can safely add the new entry. The endPC of the previous entry is not in conflit with the startPC of the new entry.
                    pcToSourceMap[pcToSourceMapSize++] = startPC;
                    pcToSourceMap[pcToSourceMapSize++] = newLine;
                }
            } else {
                /* the last recorded entry is on the same line. But it could be relevant to widen this entry.
			   we want to extend this entry forward in case we generated some bytecode before the last entry that are not related to any statement
			*/
                if (startPC < pcToSourceMap[pcToSourceMapSize - 2]) {
                    int insertionIndex = insertionIndex(pcToSourceMap, pcToSourceMapSize, startPC);
                    if (insertionIndex != -1) {
                        /* First we need to check if at the insertion position there is not an existing entry
					 * that includes the one we want to insert. This is the case if pcToSourceMap[insertionIndex - 1] == newLine.
					 * In this case we don't want to change the table. If not, we want to insert a new entry. Prior to insertion
					 * we want to check if it is worth doing an arraycopy. If not we simply update the recorded pc.
					 */
                        if (!((insertionIndex > 1) && (pcToSourceMap[insertionIndex - 1] == newLine))) {
                            if ((pcToSourceMapSize > 4) && (pcToSourceMap[pcToSourceMapSize - 4] > startPC)) {
                                System.arraycopy(pcToSourceMap, insertionIndex, pcToSourceMap, insertionIndex + 2, pcToSourceMapSize - 2 - insertionIndex);
                                pcToSourceMap[insertionIndex++] = startPC;
                                pcToSourceMap[insertionIndex] = newLine;
                            } else {
                                pcToSourceMap[pcToSourceMapSize - 2] = startPC;
                            }
                        }
                    }
                }
            }
            lastEntryPC = position;
        } else {
            // record the first entry
            pcToSourceMap[pcToSourceMapSize++] = startPC;
            pcToSourceMap[pcToSourceMapSize++] = newLine;
            lastEntryPC = position;
        }
    }

    /**
 * @param anExceptionLabel org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel
 */
    public void registerExceptionHandler(ExceptionLabel anExceptionLabel) {
        int length;
        if (exceptionHandlersIndex >= (length = exceptionHandlers.length)) {
            // resize the exception handlers table
            System.arraycopy(exceptionHandlers, 0, exceptionHandlers = new ExceptionLabel[length + LABELS_INCREMENT], 0, length);
        }
        // no need to resize. So just add the new exception label
        exceptionHandlers[exceptionHandlersIndex++] = anExceptionLabel;
        exceptionHandlersCounter++;
    }

    public void removeExceptionHandler(ExceptionLabel exceptionLabel) {
        for (int i = 0; i < exceptionHandlersIndex; i++) {
            if (exceptionHandlers[i] == exceptionLabel) {
                exceptionHandlers[i] = null;
                exceptionHandlersCounter--;
                return;
            }
        }
    }

    public final void removeNotDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
        // if this happens, then we must update their pc entries to reflect it in debug attributes
        if (!generateLocalVariableTableAttributes)
            return;
        /*	if (initStateIndex == lastInitStateIndexWhenRemovingInits)
		return;
		
	lastInitStateIndexWhenRemovingInits = initStateIndex;
	if (lastInitStateIndexWhenAddingInits != initStateIndex){
		lastInitStateIndexWhenAddingInits = -2;// reinitialize add index 
		// add(1)-remove(1)-add(1) -> ignore second add
		// add(1)-remove(2)-add(1) -> perform second add
	}*/
        for (int i = 0; i < visibleLocalsCount; i++) {
            LocalVariableBinding localBinding = visibleLocals[i];
            if (localBinding != null) {
                if (initStateIndex == -1 || !isDefinitelyAssigned(scope, initStateIndex, localBinding)) {
                    if (localBinding.initializationCount > 0) {
                        localBinding.recordInitializationEndPC(position);
                    }
                }
            }
        }
    }

    /**
 * @param referenceMethod org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 * @param targetClassFile org.eclipse.jdt.internal.compiler.codegen.ClassFile
 */
    public void reset(AbstractMethodDeclaration referenceMethod, ClassFile targetClassFile) {
        init(targetClassFile);
        this.methodDeclaration = referenceMethod;
        preserveUnusedLocals = referenceMethod.scope.problemReporter().options.preserveAllLocalVariables;
        initializeMaxLocals(referenceMethod.binding);
    }

    /**
 * @param targetClassFile The given classfile to reset the code stream
 */
    public void resetForProblemClinit(ClassFile targetClassFile) {
        init(targetClassFile);
        maxLocals = 0;
    }

    private final void resizeByteArray() {
        int length = bCodeStream.length;
        int requiredSize = length + length;
        if (classFileOffset > requiredSize) {
            // must be sure to grow by enough
            requiredSize = classFileOffset + length;
        }
        System.arraycopy(bCodeStream, 0, bCodeStream = new byte[requiredSize], 0, length);
    }

    public final void ret(int index) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tret:" + index);
        countLabels = 0;
        if (// Widen
        index > 255) {
            if (classFileOffset + 3 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_wide;
            bCodeStream[classFileOffset++] = OPC_ret;
            writeUnsignedShort(index);
        } else // Don't Widen
        {
            if (classFileOffset + 1 >= bCodeStream.length) {
                resizeByteArray();
            }
            position += 2;
            bCodeStream[classFileOffset++] = OPC_ret;
            bCodeStream[classFileOffset++] = (byte) index;
        }
    }

    public final void return_() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\treturn");
        countLabels = 0;
        // the stackDepth should be equal to 0 
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_return;
    }

    public final void saload() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tsaload");
        countLabels = 0;
        stackDepth--;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_saload;
    }

    public final void sastore() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tsastore");
        countLabels = 0;
        stackDepth -= 3;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_sastore;
    }

    /**
 * @param operatorConstant int
 * @param type_ID int
 */
    public void sendOperator(int operatorConstant, int type_ID) {
        switch(type_ID) {
            case T_int:
            case T_boolean:
            case T_char:
            case T_byte:
            case T_short:
                switch(operatorConstant) {
                    case PLUS:
                        this.iadd();
                        break;
                    case MINUS:
                        this.isub();
                        break;
                    case MULTIPLY:
                        this.imul();
                        break;
                    case DIVIDE:
                        this.idiv();
                        break;
                    case REMAINDER:
                        this.irem();
                        break;
                    case LEFT_SHIFT:
                        this.ishl();
                        break;
                    case RIGHT_SHIFT:
                        this.ishr();
                        break;
                    case UNSIGNED_RIGHT_SHIFT:
                        this.iushr();
                        break;
                    case AND:
                        this.iand();
                        break;
                    case OR:
                        this.ior();
                        break;
                    case XOR:
                        this.ixor();
                        break;
                }
                break;
            case T_long:
                switch(operatorConstant) {
                    case PLUS:
                        this.ladd();
                        break;
                    case MINUS:
                        this.lsub();
                        break;
                    case MULTIPLY:
                        this.lmul();
                        break;
                    case DIVIDE:
                        this.ldiv();
                        break;
                    case REMAINDER:
                        this.lrem();
                        break;
                    case LEFT_SHIFT:
                        this.lshl();
                        break;
                    case RIGHT_SHIFT:
                        this.lshr();
                        break;
                    case UNSIGNED_RIGHT_SHIFT:
                        this.lushr();
                        break;
                    case AND:
                        this.land();
                        break;
                    case OR:
                        this.lor();
                        break;
                    case XOR:
                        this.lxor();
                        break;
                }
                break;
            case T_float:
                switch(operatorConstant) {
                    case PLUS:
                        this.fadd();
                        break;
                    case MINUS:
                        this.fsub();
                        break;
                    case MULTIPLY:
                        this.fmul();
                        break;
                    case DIVIDE:
                        this.fdiv();
                        break;
                    case REMAINDER:
                        this.frem();
                }
                break;
            case T_double:
                switch(operatorConstant) {
                    case PLUS:
                        this.dadd();
                        break;
                    case MINUS:
                        this.dsub();
                        break;
                    case MULTIPLY:
                        this.dmul();
                        break;
                    case DIVIDE:
                        this.ddiv();
                        break;
                    case REMAINDER:
                        this.drem();
                }
        }
    }

    public final void sipush(int s) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tsipush:" + s);
        countLabels = 0;
        stackDepth++;
        if (stackDepth > stackMax)
            stackMax = stackDepth;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_sipush;
        writeSignedShort(s);
    }

    public static final void sort(int[] tab, int lo0, int hi0, int[] result) {
        int lo = lo0;
        int hi = hi0;
        int mid;
        if (hi0 > lo0) {
            /* Arbitrarily establishing partition element as the midpoint of
		  * the array.
		  */
            mid = tab[(lo0 + hi0) / 2];
            // loop through the array until indices cross
            while (lo <= hi) {
                /* find the first element that is greater than or equal to 
			 * the partition element starting from the left Index.
			 */
                while ((lo < hi0) && (tab[lo] < mid)) ++lo;
                /* find an element that is smaller than or equal to 
			 * the partition element starting from the right Index.
			 */
                while ((hi > lo0) && (tab[hi] > mid)) --hi;
                // if the indexes have not crossed, swap
                if (lo <= hi) {
                    swap(tab, lo, hi, result);
                    ++lo;
                    --hi;
                }
            }
            /* If the right index has not reached the left side of array
		  * must now sort the left partition.
		  */
            if (lo0 < hi)
                sort(tab, lo0, hi, result);
            /* If the left index has not reached the right side of array
		  * must now sort the right partition.
		  */
            if (lo < hi0)
                sort(tab, lo, hi0, result);
        }
    }

    public final void store(LocalVariableBinding localBinding, boolean valueRequired) {
        int localPosition = localBinding.resolvedPosition;
        // Using dedicated int bytecode
        switch(localBinding.type.id) {
            case TypeIds.T_int:
            case TypeIds.T_char:
            case TypeIds.T_byte:
            case TypeIds.T_short:
            case TypeIds.T_boolean:
                if (valueRequired)
                    this.dup();
                switch(localPosition) {
                    case 0:
                        this.istore_0();
                        break;
                    case 1:
                        this.istore_1();
                        break;
                    case 2:
                        this.istore_2();
                        break;
                    case 3:
                        this.istore_3();
                        break;
                    //	break;
                    default:
                        this.istore(localPosition);
                }
                break;
            case TypeIds.T_float:
                if (valueRequired)
                    this.dup();
                switch(localPosition) {
                    case 0:
                        this.fstore_0();
                        break;
                    case 1:
                        this.fstore_1();
                        break;
                    case 2:
                        this.fstore_2();
                        break;
                    case 3:
                        this.fstore_3();
                        break;
                    default:
                        this.fstore(localPosition);
                }
                break;
            case TypeIds.T_double:
                if (valueRequired)
                    this.dup2();
                switch(localPosition) {
                    case 0:
                        this.dstore_0();
                        break;
                    case 1:
                        this.dstore_1();
                        break;
                    case 2:
                        this.dstore_2();
                        break;
                    case 3:
                        this.dstore_3();
                        break;
                    default:
                        this.dstore(localPosition);
                }
                break;
            case TypeIds.T_long:
                if (valueRequired)
                    this.dup2();
                switch(localPosition) {
                    case 0:
                        this.lstore_0();
                        break;
                    case 1:
                        this.lstore_1();
                        break;
                    case 2:
                        this.lstore_2();
                        break;
                    case 3:
                        this.lstore_3();
                        break;
                    default:
                        this.lstore(localPosition);
                }
                break;
            default:
                // Reference object
                if (valueRequired)
                    this.dup();
                switch(localPosition) {
                    case 0:
                        this.astore_0();
                        break;
                    case 1:
                        this.astore_1();
                        break;
                    case 2:
                        this.astore_2();
                        break;
                    case 3:
                        this.astore_3();
                        break;
                    default:
                        this.astore(localPosition);
                }
        }
    }

    public final void store(TypeBinding type, int localPosition) {
        // Using dedicated int bytecode
        if ((type == IntBinding) || (type == CharBinding) || (type == ByteBinding) || (type == ShortBinding) || (type == BooleanBinding)) {
            switch(localPosition) {
                case 0:
                    this.istore_0();
                    break;
                case 1:
                    this.istore_1();
                    break;
                case 2:
                    this.istore_2();
                    break;
                case 3:
                    this.istore_3();
                    break;
                default:
                    this.istore(localPosition);
            }
            return;
        }
        // Using dedicated float bytecode
        if (type == FloatBinding) {
            switch(localPosition) {
                case 0:
                    this.fstore_0();
                    break;
                case 1:
                    this.fstore_1();
                    break;
                case 2:
                    this.fstore_2();
                    break;
                case 3:
                    this.fstore_3();
                    break;
                default:
                    this.fstore(localPosition);
            }
            return;
        }
        // Using dedicated long bytecode
        if (type == LongBinding) {
            switch(localPosition) {
                case 0:
                    this.lstore_0();
                    break;
                case 1:
                    this.lstore_1();
                    break;
                case 2:
                    this.lstore_2();
                    break;
                case 3:
                    this.lstore_3();
                    break;
                default:
                    this.lstore(localPosition);
            }
            return;
        }
        // Using dedicated double bytecode
        if (type == DoubleBinding) {
            switch(localPosition) {
                case 0:
                    this.dstore_0();
                    break;
                case 1:
                    this.dstore_1();
                    break;
                case 2:
                    this.dstore_2();
                    break;
                case 3:
                    this.dstore_3();
                    break;
                default:
                    this.dstore(localPosition);
            }
            return;
        }
        // Reference object
        switch(localPosition) {
            case 0:
                this.astore_0();
                break;
            case 1:
                this.astore_1();
                break;
            case 2:
                this.astore_2();
                break;
            case 3:
                this.astore_3();
                break;
            default:
                this.astore(localPosition);
        }
    }

    public final void storeInt(int localPosition) {
        switch(localPosition) {
            case 0:
                this.istore_0();
                break;
            case 1:
                this.istore_1();
                break;
            case 2:
                this.istore_2();
                break;
            case 3:
                this.istore_3();
                break;
            default:
                this.istore(localPosition);
        }
    }

    public final void storeObject(int localPosition) {
        switch(localPosition) {
            case 0:
                this.astore_0();
                break;
            case 1:
                this.astore_1();
                break;
            case 2:
                this.astore_2();
                break;
            case 3:
                this.astore_3();
                break;
            default:
                this.astore(localPosition);
        }
    }

    public final void swap() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\tswap");
        countLabels = 0;
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_swap;
    }

    private static final void swap(int a[], int i, int j, int result[]) {
        int T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;
        T = result[j];
        result[j] = result[i];
        result[i] = T;
    }

    public final void tableswitch(CaseLabel defaultLabel, int low, int high, int[] keys, int[] sortedIndexes, CaseLabel[] casesLabel) {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println(position + "\t\ttableswitch");
        countLabels = 0;
        stackDepth--;
        int length = casesLabel.length;
        int pos = position;
        defaultLabel.placeInstruction();
        for (int i = 0; i < length; i++) casesLabel[i].placeInstruction();
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = OPC_tableswitch;
        for (int i = (3 - (pos % 4)); i > 0; i--) {
            if (classFileOffset >= bCodeStream.length) {
                resizeByteArray();
            }
            position++;
            bCodeStream[classFileOffset++] = 0;
        }
        defaultLabel.branch();
        writeSignedWord(low);
        writeSignedWord(high);
        int i = low, j = low;
        // optimized tableswitch
        while (true) {
            int index;
            int key = keys[index = sortedIndexes[j - low]];
            if (key == i) {
                casesLabel[index].branch();
                j++;
                // if high is maxint, then avoids wrapping to minint.
                if (i == high)
                    break;
            } else {
                defaultLabel.branch();
            }
            i++;
        }
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer("( position:");
        buffer.append(position);
        //$NON-NLS-1$
        buffer.append(",\nstackDepth:");
        buffer.append(stackDepth);
        //$NON-NLS-1$
        buffer.append(",\nmaxStack:");
        buffer.append(stackMax);
        //$NON-NLS-1$
        buffer.append(",\nmaxLocals:");
        buffer.append(maxLocals);
        //$NON-NLS-1$
        buffer.append(")");
        return buffer.toString();
    }

    public void updateLastRecordedEndPC(int pos) {
        if (!generateLineNumberAttributes)
            return;
        this.lastEntryPC = pos;
        // need to update the initialization endPC in case of generation of local variable attributes.
        updateLocalVariablesAttribute(pos);
    }

    public void updateLocalVariablesAttribute(int pos) {
        // need to update the initialization endPC in case of generation of local variable attributes.
        if (generateLocalVariableTableAttributes) {
            for (int i = 0, max = locals.length; i < max; i++) {
                LocalVariableBinding local = locals[i];
                if ((local != null) && (local.initializationCount > 0)) {
                    if (local.initializationPCs[((local.initializationCount - 1) << 1) + 1] == pos) {
                        local.initializationPCs[((local.initializationCount - 1) << 1) + 1] = position;
                    }
                }
            }
        }
    }

    /**
 * Write a signed 16 bits value into the byte array
 * @param value the signed short
 */
    public final void writeSignedShort(int value) {
        // we keep the resize in here because it is used outside the code stream
        if (classFileOffset + 1 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 2;
        bCodeStream[classFileOffset++] = (byte) (value >> 8);
        bCodeStream[classFileOffset++] = (byte) value;
    }

    public final void writeSignedShort(int pos, int value) {
        int currentOffset = startingClassFileOffset + pos;
        if (currentOffset + 1 >= bCodeStream.length) {
            resizeByteArray();
        }
        bCodeStream[currentOffset] = (byte) (value >> 8);
        bCodeStream[currentOffset + 1] = (byte) value;
    }

    public final void writeSignedWord(int value) {
        // we keep the resize in here because it is used outside the code stream
        if (classFileOffset + 3 >= bCodeStream.length) {
            resizeByteArray();
        }
        position += 4;
        bCodeStream[classFileOffset++] = (byte) ((value & 0xFF000000) >> 24);
        bCodeStream[classFileOffset++] = (byte) ((value & 0xFF0000) >> 16);
        bCodeStream[classFileOffset++] = (byte) ((value & 0xFF00) >> 8);
        bCodeStream[classFileOffset++] = (byte) (value & 0xFF);
    }

    public final void writeSignedWord(int pos, int value) {
        int currentOffset = startingClassFileOffset + pos;
        if (currentOffset + 4 >= bCodeStream.length) {
            resizeByteArray();
        }
        bCodeStream[currentOffset++] = (byte) ((value & 0xFF000000) >> 24);
        bCodeStream[currentOffset++] = (byte) ((value & 0xFF0000) >> 16);
        bCodeStream[currentOffset++] = (byte) ((value & 0xFF00) >> 8);
        bCodeStream[currentOffset++] = (byte) (value & 0xFF);
    }

    /**
 * Write a unsigned 16 bits value into the byte array
 * @param value the unsigned short
 */
    protected final void writeUnsignedShort(int value) {
        position += 2;
        bCodeStream[classFileOffset++] = (byte) (value >>> 8);
        bCodeStream[classFileOffset++] = (byte) value;
    }

    /*
 * Wide conditional branch compare, improved by swapping comparison opcode
 *   ifeq WideTarget
 * becomes
 *    ifne Intermediate
 *    gotow WideTarget
 *    Intermediate:
 */
    public void generateWideRevertedConditionalBranch(byte revertedOpcode, Label wideTarget) {
        Label intermediate = new Label(this);
        if (classFileOffset >= bCodeStream.length) {
            resizeByteArray();
        }
        position++;
        bCodeStream[classFileOffset++] = revertedOpcode;
        intermediate.branch();
        this.goto_w(wideTarget);
        intermediate.place();
    }
}

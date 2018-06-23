/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Mateusz Matela <mateusz.matela@gmail.com> - [code manipulation] [dcr] toString() builder wizard - https://bugs.eclipse.org/bugs/show_bug.cgi?id=26070
 *******************************************************************************/
package org.eclipse.jdt.ui.actions;

import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Action ids for standard actions, for groups in the menu bar, and
 * for actions in context menus of JDT views.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class JdtActionConstants {

    // Navigate menu
    /**
	 * Navigate menu: name of standard Goto Type global action
	 * (value <code>"org.eclipse.jdt.ui.actions.GoToType"</code>).
	 */
    //$NON-NLS-1$
    public static final String GOTO_TYPE = "org.eclipse.jdt.ui.actions.GoToType";

    /**
	 * Navigate menu: name of standard Goto Package global action
	 * (value <code>"org.eclipse.jdt.ui.actions.GoToPackage"</code>).
	 */
    //$NON-NLS-1$
    public static final String GOTO_PACKAGE = "org.eclipse.jdt.ui.actions.GoToPackage";

    /**
	 * Navigate menu: name of standard Open global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Open"</code>).
	 */
    //$NON-NLS-1$
    public static final String OPEN = "org.eclipse.jdt.ui.actions.Open";

    /**
	 * Navigate menu: name of standard Open Implementation global action
	 * (value <code>"org.eclipse.jdt.ui.actions.OpenImplementation"</code>).
	 * @since 3.6
	 */
    //$NON-NLS-1$
    public static final String OPEN_IMPLEMENTATION = "org.eclipse.jdt.ui.actions.OpenImplementation";

    /**
	 * Navigate menu: name of standard Open Super Implementation global action
	 * (value <code>"org.eclipse.jdt.ui.actions.OpenSuperImplementation"</code>).
	 */
    //$NON-NLS-1$
    public static final String OPEN_SUPER_IMPLEMENTATION = "org.eclipse.jdt.ui.actions.OpenSuperImplementation";

    /**
	 * Navigate menu: name of standard Open Type Hierarchy global action
	 * (value <code>"org.eclipse.jdt.ui.actions.OpenTypeHierarchy"</code>).
	 */
    //$NON-NLS-1$
    public static final String OPEN_TYPE_HIERARCHY = "org.eclipse.jdt.ui.actions.OpenTypeHierarchy";

    /**
     * Navigate menu: name of standard Open Call Hierarchy global action
     * (value <code>"org.eclipse.jdt.ui.actions.OpenCallHierarchy"</code>).
     * @since 3.0
     */
    //$NON-NLS-1$
    public static final String OPEN_CALL_HIERARCHY = "org.eclipse.jdt.ui.actions.OpenCallHierarchy";

    /**
	 * Navigate menu: name of standard Open External Javadoc global action (value
	 * <code>"org.eclipse.jdt.ui.actions.OpenExternalJavaDoc"</code>).
	 * @deprecated As of 3.6, replaced by {@link #OPEN_ATTACHED_JAVA_DOC}
	 */
    @Deprecated
    public static final String //$NON-NLS-1$
    OPEN_EXTERNAL_JAVA_DOC = "org.eclipse.jdt.ui.actions.OpenExternalJavaDoc";

    /**
	 * Navigate menu: name of standard Open Attached Javadoc global action (value
	 * <code>"org.eclipse.jdt.ui.actions.OpenExternalJavaDoc"</code>).
	 * @since 3.6
	 */
    //$NON-NLS-1$
    public static final String OPEN_ATTACHED_JAVA_DOC = "org.eclipse.jdt.ui.actions.OpenExternalJavaDoc";

    /**
	 * Navigate menu: name of standard Show in Packages View global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ShowInPackagesView"</code>).
	 */
    //$NON-NLS-1$
    public static final String SHOW_IN_PACKAGE_VIEW = "org.eclipse.jdt.ui.actions.ShowInPackagesView";

    /**
	 * Navigate menu: name of standard Show in Navigator View global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ShowInNaviagtorView"</code>).
	 */
    //$NON-NLS-1$
    public static final String SHOW_IN_NAVIGATOR_VIEW = "org.eclipse.jdt.ui.actions.ShowInNaviagtorView";

    // Edit menu
    /**
	 * Edit menu: name of standard Show Javadoc global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ShowJavaDoc"</code>).
	 * @deprecated As of 3.3, replaced by {@link ITextEditorActionConstants#SHOW_INFORMATION}
	 */
    @Deprecated
    public static final String //$NON-NLS-1$
    SHOW_JAVA_DOC = "org.eclipse.jdt.ui.actions.ShowJavaDoc";

    /**
	 * Edit menu: name of standard Code Assist global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ContentAssist"</code>).
	 */
    //$NON-NLS-1$
    public static final String CONTENT_ASSIST = "org.eclipse.jdt.ui.actions.ContentAssist";

    // Source menu
    /**
	 * Source menu: name of standard Comment global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Comment"</code>).
	 */
    //$NON-NLS-1$
    public static final String COMMENT = "org.eclipse.jdt.ui.actions.Comment";

    /**
	 * Source menu: name of standard Uncomment global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Uncomment"</code>).
	 */
    //$NON-NLS-1$
    public static final String UNCOMMENT = "org.eclipse.jdt.ui.actions.Uncomment";

    /**
	 * Source menu: name of standard ToggleComment global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ToggleComment"</code>).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String TOGGLE_COMMENT = "org.eclipse.jdt.ui.actions.ToggleComment";

    /**
	 * Source menu: name of standard Block Comment global action
	 * (value <code>"org.eclipse.jdt.ui.actions.AddBlockComment"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String ADD_BLOCK_COMMENT = "org.eclipse.jdt.ui.actions.AddBlockComment";

    /**
	 * Source menu: name of standard Block Uncomment global action
	 * (value <code>"org.eclipse.jdt.ui.actions.RemoveBlockComment"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String REMOVE_BLOCK_COMMENT = "org.eclipse.jdt.ui.actions.RemoveBlockComment";

    /**
	 * Source menu: name of standard Indent global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Indent"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String INDENT = "org.eclipse.jdt.ui.actions.Indent";

    /**
	 * Source menu: name of standard Shift Right action
	 * (value <code>"org.eclipse.jdt.ui.actions.ShiftRight"</code>).
	 */
    //$NON-NLS-1$
    public static final String SHIFT_RIGHT = "org.eclipse.jdt.ui.actions.ShiftRight";

    /**
	 * Source menu: name of standard Shift Left global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ShiftLeft"</code>).
	 */
    //$NON-NLS-1$
    public static final String SHIFT_LEFT = "org.eclipse.jdt.ui.actions.ShiftLeft";

    /**
	 * Source menu: name of standard Format global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Format"</code>).
	 */
    //$NON-NLS-1$
    public static final String FORMAT = "org.eclipse.jdt.ui.actions.Format";

    /**
	 * Source menu: name of standard Format Element global action
	 * (value <code>"org.eclipse.jdt.ui.actions.FormatElement"</code>).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String FORMAT_ELEMENT = "org.eclipse.jdt.ui.actions.FormatElement";

    /**
	 * Source menu: name of standard Add Import global action
	 * (value <code>"org.eclipse.jdt.ui.actions.AddImport"</code>).
	 */
    //$NON-NLS-1$
    public static final String ADD_IMPORT = "org.eclipse.jdt.ui.actions.AddImport";

    /**
	 * Source menu: name of standard Organize Imports global action
	 * (value <code>"org.eclipse.jdt.ui.actions.OrganizeImports"</code>).
	 */
    //$NON-NLS-1$
    public static final String ORGANIZE_IMPORTS = "org.eclipse.jdt.ui.actions.OrganizeImports";

    /**
	 * Source menu: name of standard Sort Members global action (value
	 * <code>"org.eclipse.jdt.ui.actions.SortMembers"</code>).
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String SORT_MEMBERS = "org.eclipse.jdt.ui.actions.SortMembers";

    /**
	 * Source menu: name of standard Surround with try/catch block global action
	 * (value <code>"org.eclipse.jdt.ui.actions.SurroundWithTryCatch"</code>).
	 */
    //$NON-NLS-1$
    public static final String SURROUND_WITH_TRY_CATCH = "org.eclipse.jdt.ui.actions.SurroundWithTryCatch";

    /**
	 * Source menu: name of standard Surround with try/multi-catch block global action (value
	 * <code>"org.eclipse.jdt.ui.actions.SurroundWithTryMultiCatch"</code>).
	 * 
	 * @since 3.7.1
	 */
    //$NON-NLS-1$
    public static final String SURROUND_WITH_TRY_MULTI_CATCH = "org.eclipse.jdt.ui.actions.SurroundWithTryMultiCatch";

    /**
	 * Source menu: name of standard Override Methods global action
	 * (value <code>"org.eclipse.jdt.ui.actions.OverrideMethods"</code>).
	 */
    //$NON-NLS-1$
    public static final String OVERRIDE_METHODS = "org.eclipse.jdt.ui.actions.OverrideMethods";

    /**
	 * Source menu: name of standard Generate Getter and Setter global action
	 * (value <code>"org.eclipse.jdt.ui.actions.GenerateGetterSetter"</code>).
	 */
    //$NON-NLS-1$
    public static final String GENERATE_GETTER_SETTER = "org.eclipse.jdt.ui.actions.GenerateGetterSetter";

    /**
	 * Source menu: name of standard delegate methods global action (value
	 * <code>"org.eclipse.jdt.ui.actions.GenerateDelegateMethods"</code>).
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String GENERATE_DELEGATE_METHODS = "org.eclipse.jdt.ui.actions.GenerateDelegateMethods";

    /**
	 * Source menu: name of standard Add Constructor From Superclass global action
	 * (value <code>"org.eclipse.jdt.ui.actions.AddConstructorFromSuperclass"</code>).
	 */
    //$NON-NLS-1$
    public static final String ADD_CONSTRUCTOR_FROM_SUPERCLASS = "org.eclipse.jdt.ui.actions.AddConstructorFromSuperclass";

    /**
	 * Source menu: name of standard Generate Constructor using Fields global action
	 * (value <code>"org.eclipse.jdt.ui.actions.GenerateConstructorUsingFields"</code>).
	 */
    //$NON-NLS-1$
    public static final String GENERATE_CONSTRUCTOR_USING_FIELDS = "org.eclipse.jdt.ui.actions.GenerateConstructorUsingFields";

    /**
	 * Source menu: name of standard Generate hashCode() and equals() global action
	 * (value <code>"org.eclipse.jdt.ui.actions.GenerateHashCodeEquals"</code>).
	 * @since 3.2
	 */
    //$NON-NLS-1$
    public static final String GENERATE_HASHCODE_EQUALS = "org.eclipse.jdt.ui.actions.GenerateHashCodeEquals";

    /**
	 * Source menu: name of standard Generate toString() global action
	 * (value <code>"org.eclipse.jdt.ui.actions.GenerateToString"</code>).
	 * @since 3.5
	 */
    //$NON-NLS-1$
    public static final String GENERATE_TOSTRING = "org.eclipse.jdt.ui.actions.GenerateToString";

    /**
	 * Source menu: name of standard Add Javadoc Comment global action
	 * (value <code>"org.eclipse.jdt.ui.actions.AddJavaDocComment"</code>).
	 */
    //$NON-NLS-1$
    public static final String ADD_JAVA_DOC_COMMENT = "org.eclipse.jdt.ui.actions.AddJavaDocComment";

    /**
	 * Source menu: name of standard Find Strings to Externalize global action
	 * (value <code>"org.eclipse.jdt.ui.actions.FindStringsToExternalize"</code>).
	 *
	 * @deprecated Use {@link JdtActionConstants#EXTERNALIZE_STRINGS} instead
	 */
    @Deprecated
    public static final String //$NON-NLS-1$
    FIND_STRINGS_TO_EXTERNALIZE = "org.eclipse.jdt.ui.actions.FindStringsToExternalize";

    /**
	 * Source menu: name of standard Externalize Strings global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ExternalizeStrings"</code>).
	 */
    //$NON-NLS-1$
    public static final String EXTERNALIZE_STRINGS = "org.eclipse.jdt.ui.actions.ExternalizeStrings";

    /**
	 * Source menu: name of standard Convert Line Delimiters To Windows global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ConvertLineDelimitersToWindows"</code>).
	 */
    //$NON-NLS-1$
    public static final String CONVERT_LINE_DELIMITERS_TO_WINDOWS = "org.eclipse.jdt.ui.actions.ConvertLineDelimitersToWindows";

    /**
	 * Source menu: name of standard Convert Line Delimiters To UNIX global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ConvertLineDelimitersToUNIX"</code>).
	 */
    //$NON-NLS-1$
    public static final String CONVERT_LINE_DELIMITERS_TO_UNIX = "org.eclipse.jdt.ui.actions.ConvertLineDelimitersToUNIX";

    /**
	 * Source menu: name of standardConvert Line Delimiters To Mac global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ConvertLineDelimitersToMac"</code>).
	 */
    //$NON-NLS-1$
    public static final String CONVERT_LINE_DELIMITERS_TO_MAC = "org.eclipse.jdt.ui.actions.ConvertLineDelimitersToMac";

    /**
	 * Source menu: name of standard Clean up global action
	 * (value <code>"org.eclipse.jdt.ui.actions.CleanUp"</code>).
	 *
	 * @since 3.2
	 */
    //$NON-NLS-1$
    public static final String CLEAN_UP = "org.eclipse.jdt.ui.actions.CleanUp";

    // Refactor menu
    /**
	 * Refactor menu: name of standard Self Encapsulate Field global action
	 * (value <code>"org.eclipse.jdt.ui.actions.SelfEncapsulateField"</code>).
	 */
    //$NON-NLS-1$
    public static final String SELF_ENCAPSULATE_FIELD = "org.eclipse.jdt.ui.actions.SelfEncapsulateField";

    /**
	 * Refactor menu: name of standard Modify Parameters global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ModifyParameters"</code>).
	 */
    //$NON-NLS-1$
    public static final String MODIFY_PARAMETERS = "org.eclipse.jdt.ui.actions.ModifyParameters";

    /**
	 * Refactor menu: name of standard Pull Up global action
	 * (value <code>"org.eclipse.jdt.ui.actions.PullUp"</code>).
	 */
    //$NON-NLS-1$
    public static final String PULL_UP = "org.eclipse.jdt.ui.actions.PullUp";

    /**
	 * Refactor menu: name of standard Push Down global action
	 * (value <code>"org.eclipse.jdt.ui.actions.PushDown"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String PUSH_DOWN = "org.eclipse.jdt.ui.actions.PushDown";

    /**
	 * Refactor menu: name of standard Move Element global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Move"</code>).
	 */
    //$NON-NLS-1$
    public static final String MOVE = "org.eclipse.jdt.ui.actions.Move";

    /**
	 * Refactor menu: name of standard Rename Element global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Rename"</code>).
	 */
    //$NON-NLS-1$
    public static final String RENAME = "org.eclipse.jdt.ui.actions.Rename";

    /**
	 * Refactor menu: name of standard Inline Temp global action
	 * (value <code>"org.eclipse.jdt.ui.actions.InlineTemp"</code>).
	 * @deprecated Use INLINE
	 */
    @Deprecated
    public static final String //$NON-NLS-1$
    INLINE_TEMP = "org.eclipse.jdt.ui.actions.InlineTemp";

    /**
	 * Refactor menu: name of standard Extract Temp global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ExtractTemp"</code>).
	 */
    //$NON-NLS-1$
    public static final String EXTRACT_TEMP = "org.eclipse.jdt.ui.actions.ExtractTemp";

    /**
	 * Refactor menu: name of standard Extract Constant global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ExtractConstant"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String EXTRACT_CONSTANT = "org.eclipse.jdt.ui.actions.ExtractConstant";

    /**
	 * Refactor menu: name of standard Introduce Parameter global action
	 * (value <code>"org.eclipse.jdt.ui.actions.IntroduceParameter"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String INTRODUCE_PARAMETER = "org.eclipse.jdt.ui.actions.IntroduceParameter";

    /**
	 * Refactor menu: name of standard Introduce Factory global action
	 * (value <code>"org.eclipse.jdt.ui.actions.IntroduceFactory"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String INTRODUCE_FACTORY = "org.eclipse.jdt.ui.actions.IntroduceFactory";

    /**
	 * Refactor menu> name of the standard Introduce Parameter Object action
	 * (value <code>"org.eclipse.jdt.ui.actions.IntroduceParameterObject"</code>).
	 *
	 * @since 3.4
	 */
    //$NON-NLS-1$
    public static final String INTRODUCE_PARAMETER_OBJECT = "org.eclipse.jdt.ui.actions.IntroduceParameterObject";

    /**
	 * Refactor menu: name of standard Extract Method global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ExtractMethod"</code>).
	 */
    //$NON-NLS-1$
    public static final String EXTRACT_METHOD = "org.eclipse.jdt.ui.actions.ExtractMethod";

    /**
	 * Refactor menu: name of standard Replace Invocations global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReplaceInvocations"</code>).
	 *
	 * @since 3.2
	 */
    //$NON-NLS-1$
    public static final String REPLACE_INVOCATIONS = "org.eclipse.jdt.ui.actions.ReplaceInvocations";

    /**
	 * Refactor menu: name of standard Introduce Indirection global action
	 * (value <code>"org.eclipse.jdt.ui.actions.IntroduceIndirection"</code>).
	 *
	 * @since 3.2
	 */
    //$NON-NLS-1$
    public static final String INTRODUCE_INDIRECTION = "org.eclipse.jdt.ui.actions.IntroduceIndirection";

    /**
	 * Refactor menu: name of standard Inline global action
	 * (value <code>"org.eclipse.jdt.ui.actions.Inline"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String INLINE = "org.eclipse.jdt.ui.actions.Inline";

    /**
	 * Refactor menu: name of standard Extract Interface global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ExtractInterface"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String EXTRACT_INTERFACE = "org.eclipse.jdt.ui.actions.ExtractInterface";

    /**
	 * Refactor menu: name of standard Extract Class global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ExtractClass"</code>).
	 *
	 * @since 3.4
	 */
    //$NON-NLS-1$
    public static final String EXTRACT_CLASS = "org.eclipse.jdt.ui.actions.ExtractClass";

    /**
	 * Refactor menu: name of standard Generalize Declared Type global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ChangeType"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String CHANGE_TYPE = "org.eclipse.jdt.ui.actions.ChangeType";

    /**
	 * Refactor menu: name of standard global action to convert a nested type to a top level type
	 * (value <code>"org.eclipse.jdt.ui.actions.MoveInnerToTop"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String CONVERT_NESTED_TO_TOP = "org.eclipse.jdt.ui.actions.ConvertNestedToTop";

    /**
	 * Refactor menu: name of standard Use Supertype global action
	 * (value <code>"org.eclipse.jdt.ui.actions.UseSupertype"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String USE_SUPERTYPE = "org.eclipse.jdt.ui.actions.UseSupertype";

    /**
	 * Refactor menu: name of standard Infer Generic Type Arguments global action
	 * (value <code>"org.eclipse.jdt.ui.actions.InferTypeArguments"</code>).
	 *
	 * @since 3.1
	 */
    //$NON-NLS-1$
    public static final String INFER_TYPE_ARGUMENTS = "org.eclipse.jdt.ui.actions.InferTypeArguments";

    /**
	 * Refactor menu: name of standard global action to convert a local
	 * variable to a field (value <code>"org.eclipse.jdt.ui.actions.ConvertLocalToField"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String CONVERT_LOCAL_TO_FIELD = "org.eclipse.jdt.ui.actions.ConvertLocalToField";

    /**
	 * Refactor menu: name of standard Covert Anonymous to Nested global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ConvertAnonymousToNested"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String CONVERT_ANONYMOUS_TO_NESTED = "org.eclipse.jdt.ui.actions.ConvertAnonymousToNested";

    // Search Menu
    /**
	 * Search menu: name of standard Find References in Workspace global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReferencesInWorkspace"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_REFERENCES_IN_WORKSPACE = "org.eclipse.jdt.ui.actions.ReferencesInWorkspace";

    /**
	 * Search menu: name of standard Find References in Project global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReferencesInProject"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_REFERENCES_IN_PROJECT = "org.eclipse.jdt.ui.actions.ReferencesInProject";

    /**
	 * Search menu: name of standard Find References in Hierarchy global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReferencesInHierarchy"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_REFERENCES_IN_HIERARCHY = "org.eclipse.jdt.ui.actions.ReferencesInHierarchy";

    /**
	 * Search menu: name of standard Find References in Working Set global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReferencesInWorkingSet"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_REFERENCES_IN_WORKING_SET = "org.eclipse.jdt.ui.actions.ReferencesInWorkingSet";

    /**
	 * Search menu: name of standard Find Declarations in Workspace global action
	 * (value <code>"org.eclipse.jdt.ui.actions.DeclarationsInWorkspace"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_DECLARATIONS_IN_WORKSPACE = "org.eclipse.jdt.ui.actions.DeclarationsInWorkspace";

    /**
	 * Search menu: name of standard Find Declarations in Project global action
	 * (value <code>"org.eclipse.jdt.ui.actions.DeclarationsInProject"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_DECLARATIONS_IN_PROJECT = "org.eclipse.jdt.ui.actions.DeclarationsInProject";

    /**
	 * Search menu: name of standard Find Declarations in Hierarchy global action
	 * (value <code>"org.eclipse.jdt.ui.actions.DeclarationsInHierarchy"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_DECLARATIONS_IN_HIERARCHY = "org.eclipse.jdt.ui.actions.DeclarationsInHierarchy";

    /**
	 * Search menu: name of standard Find Declarations in Working Set global action
	 * (value <code>"org.eclipse.jdt.ui.actions.DeclarationsInWorkingSet"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_DECLARATIONS_IN_WORKING_SET = "org.eclipse.jdt.ui.actions.DeclarationsInWorkingSet";

    /**
	 * Search menu: name of standard Find Implementors in Workspace global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ImplementorsInWorkspace"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_IMPLEMENTORS_IN_WORKSPACE = "org.eclipse.jdt.ui.actions.ImplementorsInWorkspace";

    /**
	 * Search menu: name of standard Find Implementors in Project global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ImplementorsInProject"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_IMPLEMENTORS_IN_PROJECT = "org.eclipse.jdt.ui.actions.ImplementorsInProject";

    /**
	 * Search menu: name of standard Find Implementors in Working Set global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ImplementorsInWorkingSet"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_IMPLEMENTORS_IN_WORKING_SET = "org.eclipse.jdt.ui.actions.ImplementorsInWorkingSet";

    /**
	 * Search menu: name of standard Find Read Access in Workspace global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReadAccessInWorkspace"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_READ_ACCESS_IN_WORKSPACE = "org.eclipse.jdt.ui.actions.ReadAccessInWorkspace";

    /**
	 * Search menu: name of standard Find Read Access in Project global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReadAccessInProject"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_READ_ACCESS_IN_PROJECT = "org.eclipse.jdt.ui.actions.ReadAccessInProject";

    /**
	 * Search menu: name of standard Find Read Access in Hierarchy global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReadAccessInHierarchy"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_READ_ACCESS_IN_HIERARCHY = "org.eclipse.jdt.ui.actions.ReadAccessInHierarchy";

    /**
	 * Search menu: name of standard Find Read Access in Working Set global action
	 * (value <code>"org.eclipse.jdt.ui.actions.ReadAccessInWorkingSet"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_READ_ACCESS_IN_WORKING_SET = "org.eclipse.jdt.ui.actions.ReadAccessInWorkingSet";

    /**
	 * Search menu: name of standard Find Write Access in Workspace global action
	 * (value <code>"org.eclipse.jdt.ui.actions.WriteAccessInWorkspace"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_WRITE_ACCESS_IN_WORKSPACE = "org.eclipse.jdt.ui.actions.WriteAccessInWorkspace";

    /**
	 * Search menu: name of standard Find Write Access in Project global action
	 * (value <code>"org.eclipse.jdt.ui.actions.WriteAccessInProject"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_WRITE_ACCESS_IN_PROJECT = "org.eclipse.jdt.ui.actions.WriteAccessInProject";

    /**
	 * Search menu: name of standard Find Read Access in Hierarchy global action
	 * (value <code>"org.eclipse.jdt.ui.actions.WriteAccessInHierarchy"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_WRITE_ACCESS_IN_HIERARCHY = "org.eclipse.jdt.ui.actions.WriteAccessInHierarchy";

    /**
	 * Search menu: name of standard Find Read Access in Working Set global action
	 * (value <code>"org.eclipse.jdt.ui.actions.WriteAccessInWorkingSet"</code>).
	 */
    //$NON-NLS-1$
    public static final String FIND_WRITE_ACCESS_IN_WORKING_SET = "org.eclipse.jdt.ui.actions.WriteAccessInWorkingSet";

    /**
	 * Search menu: name of standard Occurrences in File global action (value
	 * <code>"org.eclipse.jdt.ui.actions.OccurrencesInFile"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String FIND_OCCURRENCES_IN_FILE = "org.eclipse.jdt.ui.actions.OccurrencesInFile";

    /**
	 * Search menu: name of standard Find exception occurrences global action (value
	 * <code>"org.eclipse.jdt.ui.actions.ExceptionOccurrences"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String FIND_EXCEPTION_OCCURRENCES = "org.eclipse.jdt.ui.actions.ExceptionOccurrences";

    /**
	 * Search menu: name of standard Find implement occurrences global action (value
	 * <code>"org.eclipse.jdt.ui.actions.ImplementOccurrences"</code>).
	 *
	 * @since 3.1
	 */
    //$NON-NLS-1$
    public static final String FIND_IMPLEMENT_OCCURRENCES = "org.eclipse.jdt.ui.actions.ImplementOccurrences";

    /**
	 * Search menu: name of standard method exit occurrences global action (value
	 * <code>"org.eclipse.jdt.ui.actions.MethodExitOccurrences"</code>).
	 *
	 * @since 3.4
	 */
    //$NON-NLS-1$
    public static final String FIND_METHOD_EXIT_OCCURRENCES = "org.eclipse.jdt.ui.actions.MethodExitOccurrences";

    /**
	 * Search menu: name of standard Find break/continue occurrences global action (value
	 * <code>"org.eclipse.jdt.ui.actions.BreakContinueTargetOccurrences"</code>).
	 *
	 * @since 3.4
	 */
    //$NON-NLS-1$
    public static final String FIND_BREAK_CONTINUE_TARGET_OCCURRENCES = "org.eclipse.jdt.ui.actions.BreakContinueTargetOccurrences";
}

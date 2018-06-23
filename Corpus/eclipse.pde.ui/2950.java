/*******************************************************************************
 * Copyright (c) 2008, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal;

import org.eclipse.pde.api.tools.internal.comparator.Delta;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;

/**
 * Interface containing all of the constants used in XML documents in API Tools
 *
 * @since 1.0.0
 */
public interface IApiXmlConstants {

    /**
	 * Constant representing the current version for API description files
	 */
    //$NON-NLS-1$
    public static final String API_DESCRIPTION_CURRENT_VERSION = "1.2";

    /**
	 * Constant representing the current version for API filter store files
	 */
    public static final String API_FILTER_STORE_CURRENT_VERSION = Integer.toString(FilterStore.CURRENT_STORE_VERSION);

    /**
	 * Constant representing the current version for API profile files
	 */
    //$NON-NLS-1$
    public static final String API_PROFILE_CURRENT_VERSION = "2";

    /**
	 * Constant representing the current version for API report XML file
	 */
    //$NON-NLS-1$
    public static final String API_REPORT_CURRENT_VERSION = "1";

    /**
	 * Constant representing the category attribute of an {@link IApiProblem} in
	 * XML. Value is: <code>category</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_CATEGORY = "category";

    /**
	 * Constant representing the element kind attribute of an
	 * {@link IApiProblem} in XML. Value is: <code>elementkind</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_ELEMENT_KIND = "elementkind";

    /**
	 * Constant representing the element attribute for a comment. value is:
	 * <code>comment</code>
	 *
	 * @since 1.1
	 */
    //$NON-NLS-1$
    public static final String ATTR_COMMENT = "comment";

    /**
	 * Constant representing the element severity attribute of an
	 * {@link IApiProblem} in XML. Value is: <code>severity</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_SEVERITY = "severity";

    /**
	 * Constant representing the extend attribute for a type XML node. Value is:
	 * <code>extend</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_EXTEND = "extend";

    /**
	 * Constant representing the override attribute for a method XML node. Value
	 * is: <code>override</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_OVERRIDE = "override";

    /**
	 * Constant representing the subclass attribute for a class XML node. Value
	 * is: <code>subclass</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_SUBCLASS = "subclass";

    /**
	 * Constant representing the flags attribute of an {@link IApiProblem} in
	 * XML. Value is: <code>flags</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_FLAGS = "flags";

    /**
	 * Constant representing the java element handle attribute name in XML.
	 * Value is <code>handle</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_HANDLE = "handle";

    /**
	 * Constant representing the id attribute Value is: <code>id</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_ID = "id";

    /**
	 * Constant representing the implement attribute for a type XML node. Value
	 * is: <code>implement</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_IMPLEMENT = "implement";

    /**
	 * Constant representing the instantiate attribute for a type XML node.
	 * Value is: <code>instantiate</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_INSTANTIATE = "instantiate";

    /**
	 * Constant representing the kind attribute of an {@link IApiProblem} in
	 * XML. Value is: <code>kind</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_KIND = "kind";

    /**
	 * Constant representing the location attribute name for an API profile XML
	 * file. Value is <code>location</code>
	 */
    //$NON-NLS-1$
    static final String ATTR_LOCATION = "location";

    /**
	 * Constant representing the message attribute of an {@link IApiProblem
	 * problem} or a {@link Delta delta} in XML. Value is: <code>message</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_MESSAGE = "message";

    /**
	 * Constant representing the resource modification stamp attribute name in
	 * XML. Value is <code>modificationStamp</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_MODIFICATION_STAMP = "modificationStamp";

    /**
	 * Constant representing the name attribute for component, package, type,
	 * method, field and bundle XML nodes. Value is: <code>name</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME = "name";

    /**
	 * Constant representing the compatibility attribute of a delta in XML
	 * report. Value is: <code>compatible</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME_COMPATIBLE = "compatible";

    /**
	 * Constant representing the element type attribute of a delta in XML
	 * report. Value is: <code>element_type</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME_ELEMENT_TYPE = "element_type";

    /**
	 * Constant representing the char start attribute of an {@link IApiProblem}
	 * in XML. Value is: <code>charstart</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_CHAR_START = "charstart";

    /**
	 * Constant representing the charend attribute of an {@link IApiProblem} in
	 * XML. Value is: <code>charend</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_CHAR_END = "charend";

    /**
	 * Constant representing the new modifiers attribute of a delta in XML
	 * report. Value is: <code>newModifiers</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME_NEW_MODIFIERS = "newModifiers";

    /**
	 * Constant representing the old modifiers attribute of a delta in XML
	 * report. Value is: <code>oldModifiers</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME_OLD_MODIFIERS = "oldModifiers";

    /**
	 * Constant representing the type name attribute of a delta in XML report.
	 * Value is: <code>type_name</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME_TYPE_NAME = "type_name";

    /**
	 * Constant representing the linenumber attribute of an {@link IApiProblem}
	 * in XML. Value is: <code>linenumber</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_LINE_NUMBER = "linenumber";

    /**
	 * Constant representing the message argument attribute of an
	 * {@link IApiProblem} in XML. Value is: <code>messageargs</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_MESSAGE_ARGUMENTS = "messageargs";

    /**
	 * Constant representing the path attribute of a resource in XML. Value is:
	 * <code>path</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_PATH = "path";

    /**
	 * Constant representing the reference attribute for a type XML node. Value
	 * is: <code>reference</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_REFERENCE = "reference";

    /**
	 * Constant representing the API restrictions mask attribute name in XML.
	 * Value is <code>restrictions</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_RESTRICTIONS = "restrictions";

    /**
	 * Constant representing the API profile attribute name in XML for which the
	 * element was added. Value is <code>addedprofile</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_ADDED_PROFILE = "addedprofile";

    /**
	 * Constant representing the API profile attribute name in XML for which the
	 * element was defined. Value is <code>profile</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_PROFILE = "profile";

    /**
	 * Constant representing the API profile attribute name in XML for which the
	 * element was removed. Value is <code>removedprofile</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_REMOVED_PROFILE = "removedprofile";

    /**
	 * Constant representing the superclass attribute name in XML. Value is
	 * <code>sc</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_SUPER_CLASS = "sc";

    /**
	 * Constant representing the superinterfaces attribute name in XML. Value is
	 * <code>sis</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_SUPER_INTERFACES = "sis";

    /**
	 * Constant representing the interface flag attribute name in XML. Value is
	 * <code>int</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_INTERFACE = "int";

    /**
	 * Constant representing the status of a member attribute name in XML. Value
	 * is <code>status</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_STATUS = "status";

    /**
	 * Constant representing the delta component id attribute name in XML. Value
	 * is <code>componentId</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME_COMPONENT_ID = "componentId";

    /**
	 * Constant representing the signature attribute for a method XML node.
	 * Value is: <code>signature</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_SIGNATURE = "signature";

    /**
	 * Constant representing the version attribute name for an API profile XML
	 * file. Value is <code>version</code>
	 */
    //$NON-NLS-1$
    static final String ATTR_VERSION = "version";

    /**
	 * Constant representing the visibility attribute for component, package,
	 * type, method and field XML nodes. Will be one of: "API", "private",
	 * "private_permissable", or "SPI"
	 */
    //$NON-NLS-1$
    public static final String ATTR_VISIBILITY = "visibility";

    /**
	 * Constant representing the delta element name. Value is:
	 * <code>delta</code>
	 */
    //$NON-NLS-1$
    public static final String DELTA_ELEMENT_NAME = "delta";

    /**
	 * Constant representing the deltas element name. Value is:
	 * <code>deltas</code>
	 */
    //$NON-NLS-1$
    public static final String DELTAS_ELEMENT_NAME = "deltas";

    /**
	 * Constant representing the API component node name for an API profile XML
	 * file. Value is <code>apicomponent</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_APICOMPONENT = "apicomponent";

    /**
	 * Constant representing the API profile node name for an API profile XML
	 * file. Value is <code>apiprofile</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_APIPROFILE = "apiprofile";

    /**
	 * Constant representing a component element node in XML. Value is:
	 * <code>component</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_COMPONENT = "component";

    /**
	 * Constant representing a components element node in XML. Value is:
	 * <code>components</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_COMPONENTS = "components";

    /**
	 * Constant representing a field element node in XML. Value is:
	 * <code>field</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_FIELD = "field";

    /**
	 * Constant representing a API filter element node in XML. Value is:
	 * <code>filter</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_FILTER = "filter";

    /**
	 * Constant representing a method element node in XML. Value is:
	 * <code>method</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_METHOD = "method";

    /**
	 * Constant representing a package element node in XML. Value is:
	 * <code>package</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_PACKAGE = "package";

    /**
	 * Constant representing a package fragment element node in XML. Value is:
	 * <code>package</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_PACKAGE_FRAGMENT = "fragment";

    /**
	 * Constant representing a plugin element node in XML. Value is:
	 * <code>plugin</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_PLUGIN = "plugin";

    /**
	 * Constant representing the API component pool node name for an API profile
	 * XML file. Value is <code>pool</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_POOL = "pool";

    /**
	 * Constant representing a resource element node in XML. Value is:
	 * <code>resource</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_RESOURCE = "resource";

    /**
	 * Constant representing a type element node in XML. Value is:
	 * <code>type</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_TYPE = "type";

    /**
	 * Constant representing a target element node in XML. Value is:
	 * <code>target</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_TARGET = "target";

    /**
	 * Constant representing an API problems element node in XML. Value is:
	 * <code>api_problems</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_API_PROBLEMS = "api_problems";

    /**
	 * Constant representing an API problem element node in XML. Value is:
	 * <code>api_problem</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_API_PROBLEM = "api_problem";

    /**
	 * Constant representing an extra arguments element node for an API problem
	 * element in XML. Value is: <code>extra_arguments</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_PROBLEM_EXTRA_ARGUMENTS = "extra_arguments";

    /**
	 * Constant representing an extra argument element node for an API problem
	 * element in XML. Value is: <code>extra_argument</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_PROBLEM_EXTRA_ARGUMENT = "extra_argument";

    /**
	 * Constant representing the value attribute for extra argument element node
	 * or a category node. Value is: <code>value</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_VALUE = "value";

    /**
	 * Constant representing a message arguments element node for an API problem
	 * element in XML. Value is: <code>message_arguments</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_PROBLEM_MESSAGE_ARGUMENTS = "message_arguments";

    /**
	 * Constant representing a message argument element node for an API problem
	 * element in XML. Value is: <code>message_argument</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_PROBLEM_MESSAGE_ARGUMENT = "message_argument";

    /**
	 * Constant representing the component id attribute for report element XML
	 * node. Value is: <code>componentID</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_COMPONENT_ID = "componentID";

    /**
	 * Constant representing a report element node in XML. Value is:
	 * <code>report</code>
	 */
    //$NON-NLS-1$<
    public static final String ELEMENT_API_TOOL_REPORT = "report";

    /**
	 * Constant representing the key attribute for category node. Value is:
	 * <code>key</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_KEY = "key";

    /**
	 * Constant representing the type attribute for resource node inside API
	 * filters. Value is: <code>type</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_TYPE = "type";

    /**
	 * Constant representing the type name attribute for API problem node. Value
	 * is: <code>typeName</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_TYPE_NAME = "typeName";

    /**
	 * Constant representing the bundle element for for a report element in XML.
	 * Value is: <code>bundle</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_BUNDLE = "bundle";

    /**
	 * Constant representing a message arguments element node for a delta
	 * element in XML. Value is: <code>message_arguments</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_DELTA_MESSAGE_ARGUMENTS = "message_arguments";

    /**
	 * Constant representing a message argument element node for a delta element
	 * in XML. Value is: <code>message_argument</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_DELTA_MESSAGE_ARGUMENT = "message_argument";

    //$NON-NLS-1$
    public static final String REFERENCES = "references";

    //$NON-NLS-1$
    public static final String REFERENCE_KIND = "reference_kind";

    //$NON-NLS-1$
    public static final String ATTR_REFERENCE_KIND_NAME = "reference_kind_name";

    //$NON-NLS-1$
    public static final String ATTR_ORIGIN = "origin";

    //$NON-NLS-1$
    public static final String ATTR_REFEREE = "referee";

    //$NON-NLS-1$
    public static final String ATTR_REFERENCE_COUNT = "reference_count";

    //$NON-NLS-1$
    public static final String ATTR_REFERENCE_VISIBILITY = "reference_visibility";

    //$NON-NLS-1$
    public static final String SKIPPED_DETAILS = "details";

    //$NON-NLS-1$
    public static final String EXCLUDED = "excluded";

    //$NON-NLS-1$
    public static final String ATTR_MEMBER_NAME = "member";

    /**
	 * Constant representing an alternate API component in which references were
	 * resolved. Value is: <code>alternate</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_ALTERNATE = "alternate";

    /**
	 * Constant representing the root element of a reference count XML file
	 * Value is: <code>referenceCount</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_REPORTED_COUNT = "reportedcount";

    /**
	 * XML attribute name for the total number of references or problems found
	 * Value is: <code>total</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_TOTAL = "total";

    /**
	 * XML attribute name for the total number of problems with severity
	 * 'warning' found Value is: <code>warnings</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_COUNT_WARNINGS = "warnings";

    /**
	 * XML attribute name for the total number of problems with severity 'error'
	 * found Value is: <code>errors</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_COUNT_ERRORS = "errors";

    /**
	 * XML attribute name for the total number of illegal references found Value
	 * is: <code>illegal</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_COUNT_ILLEGAL = "illegal";

    /**
	 * XML attribute name for the total number of internal references found
	 * Value is: <code>internal</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_COUNT_INTERNAL = "internal";

    /**
	 * XML attribute name for the total number of filtered illegal references found
	 * Value is: <code>filtered</code>
	 */
    //$NON-NLS-1$
    public static final String ATTR_COUNT_FILTERED = "filtered";

    /**
	 * XML element describing a resolver error found in a bundle Value is:
	 * <code>resolver_error</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_RESOLVER_ERROR = "resolver_error";

    /**
	 * XML element containing a list of components with resolver errors Value
	 * is: <code>resolver_errors</code>
	 */
    //$NON-NLS-1$
    public static final String ELEMENT_RESOLVER_ERRORS = "resolver_errors";
}

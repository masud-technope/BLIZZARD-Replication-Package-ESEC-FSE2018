/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.comparator.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator;
import org.eclipse.pde.api.tools.internal.provisional.comparator.DeltaProcessor;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;

/**
 * Delta tests for annotation
 */
public class AnnotationDeltaTests extends DeltaTestSetup {

    public static Test suite() {
        return new TestSuite(AnnotationDeltaTests.class);
    //		TestSuite suite = new TestSuite(AnnotationDeltaTests.class.getName());
    //		suite.addTest(new AnnotationDeltaTests("test9"));
    //		return suite;
    }

    public  AnnotationDeltaTests(String name) {
        super(name);
    }

    @Override
    public String getTestRoot() {
        //$NON-NLS-1$
        return "annotation";
    }

    /**
	 * Add element to annotation type
	 */
    public void test1() {
        //$NON-NLS-1$
        deployBundles("test1");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * remove element to annotation type
	 */
    public void test2() {
        //$NON-NLS-1$
        deployBundles("test2");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add element to annotation type
	 */
    public void test3() {
        //$NON-NLS-1$
        deployBundles("test3");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITHOUT_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add elements with all different types
	 */
    public void test4() {
        //$NON-NLS-1$
        deployBundles("test4");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 11, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[3];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[4];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[5];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[6];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[7];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[8];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[9];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[10];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add elements with all different types (array)
	 */
    public void test5() {
        //$NON-NLS-1$
        deployBundles("test5");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 13, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[3];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[4];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[5];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[6];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[7];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[8];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[9];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[10];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[11];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[12];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changed default values
	 */
    public void test6() {
        //$NON-NLS-1$
        deployBundles("test6");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 12, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[3];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[4];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[5];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[6];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[7];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[8];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[9];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[10];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[11];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove method with default value
	 */
    public void test7() {
        //$NON-NLS-1$
        deployBundles("test7");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove method with no default value
	 */
    public void test8() {
        //$NON-NLS-1$
        deployBundles("test8");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_WITHOUT_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add a field
	 */
    public void test9() {
        //$NON-NLS-1$
        deployBundles("test9");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added deprecation
	 */
    public void test10() {
        //$NON-NLS-1$
        deployBundles("test10");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DEPRECATION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed deprecation
	 */
    public void test11() {
        //$NON-NLS-1$
        deployBundles("test11");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DEPRECATION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }
}

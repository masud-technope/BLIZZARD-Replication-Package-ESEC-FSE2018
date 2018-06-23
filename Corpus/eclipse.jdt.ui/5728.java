/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.javaeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.internal.ui.actions.CopyQualifiedNameAction;
import org.eclipse.jdt.internal.ui.actions.FoldingActionGroup;
import org.eclipse.jdt.internal.ui.javaeditor.selectionactions.GoToNextPreviousMemberAction;
import org.eclipse.jdt.internal.ui.javaeditor.selectionactions.StructureSelectionAction;

/**
 * Common base class for action contributors for Java editors.
 */
public class BasicJavaEditorActionContributor extends BasicTextEditorActionContributor {

    private List<RetargetAction> fPartListeners = new ArrayList();

    private TogglePresentationAction fTogglePresentation;

    private ToggleMarkOccurrencesAction fToggleMarkOccurrencesAction;

    private ToggleBreadcrumbAction fToggleBreadcrumbAction;

    private RetargetTextEditorAction fGotoMatchingBracket;

    private RetargetTextEditorAction fShowOutline;

    private RetargetTextEditorAction fOpenStructure;

    private RetargetTextEditorAction fOpenHierarchy;

    private RetargetTextEditorAction fRetargetShowInformationAction;

    private RetargetTextEditorAction fStructureSelectEnclosingAction;

    private RetargetTextEditorAction fStructureSelectNextAction;

    private RetargetTextEditorAction fStructureSelectPreviousAction;

    private RetargetTextEditorAction fStructureSelectHistoryAction;

    private RetargetTextEditorAction fGotoNextMemberAction;

    private RetargetTextEditorAction fGotoPreviousMemberAction;

    private RetargetTextEditorAction fRemoveOccurrenceAnnotationsAction;

    public  BasicJavaEditorActionContributor() {
        super();
        ResourceBundle b = JavaEditorMessages.getBundleForConstructedKeys();
        //$NON-NLS-1$
        fRetargetShowInformationAction = new RetargetTextEditorAction(b, "Editor.ShowInformation.");
        fRetargetShowInformationAction.setActionDefinitionId(ITextEditorActionDefinitionIds.SHOW_INFORMATION);
        // actions that are "contributed" to editors, they are considered belonging to the active editor
        fTogglePresentation = new TogglePresentationAction();
        fToggleMarkOccurrencesAction = new ToggleMarkOccurrencesAction();
        //$NON-NLS-1$
        fGotoMatchingBracket = new RetargetTextEditorAction(b, "GotoMatchingBracket.");
        fGotoMatchingBracket.setActionDefinitionId(IJavaEditorActionDefinitionIds.GOTO_MATCHING_BRACKET);
        //$NON-NLS-1$
        fShowOutline = new RetargetTextEditorAction(JavaEditorMessages.getBundleForConstructedKeys(), "ShowOutline.");
        fShowOutline.setActionDefinitionId(IJavaEditorActionDefinitionIds.SHOW_OUTLINE);
        //$NON-NLS-1$
        fOpenHierarchy = new RetargetTextEditorAction(JavaEditorMessages.getBundleForConstructedKeys(), "OpenHierarchy.");
        fOpenHierarchy.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_HIERARCHY);
        //$NON-NLS-1$
        fOpenStructure = new RetargetTextEditorAction(JavaEditorMessages.getBundleForConstructedKeys(), "OpenStructure.");
        fOpenStructure.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_STRUCTURE);
        //$NON-NLS-1$
        fStructureSelectEnclosingAction = new RetargetTextEditorAction(b, "StructureSelectEnclosing.");
        fStructureSelectEnclosingAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.SELECT_ENCLOSING);
        //$NON-NLS-1$
        fStructureSelectNextAction = new RetargetTextEditorAction(b, "StructureSelectNext.");
        fStructureSelectNextAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.SELECT_NEXT);
        //$NON-NLS-1$
        fStructureSelectPreviousAction = new RetargetTextEditorAction(b, "StructureSelectPrevious.");
        fStructureSelectPreviousAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.SELECT_PREVIOUS);
        //$NON-NLS-1$
        fStructureSelectHistoryAction = new RetargetTextEditorAction(b, "StructureSelectHistory.");
        fStructureSelectHistoryAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.SELECT_LAST);
        //$NON-NLS-1$
        fGotoNextMemberAction = new RetargetTextEditorAction(b, "GotoNextMember.");
        fGotoNextMemberAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.GOTO_NEXT_MEMBER);
        //$NON-NLS-1$
        fGotoPreviousMemberAction = new RetargetTextEditorAction(b, "GotoPreviousMember.");
        fGotoPreviousMemberAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.GOTO_PREVIOUS_MEMBER);
        //$NON-NLS-1$
        fRemoveOccurrenceAnnotationsAction = new RetargetTextEditorAction(b, "RemoveOccurrenceAnnotations.");
        fRemoveOccurrenceAnnotationsAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.REMOVE_OCCURRENCE_ANNOTATIONS);
    }

    protected final void markAsPartListener(RetargetAction action) {
        fPartListeners.add(action);
    }

    /*
	 * @see IEditorActionBarContributor#init(IActionBars, IWorkbenchPage)
	 */
    @Override
    public void init(IActionBars bars, IWorkbenchPage page) {
        fToggleBreadcrumbAction = new ToggleBreadcrumbAction(page);
        Iterator<RetargetAction> e = fPartListeners.iterator();
        while (e.hasNext()) page.addPartListener(e.next());
        super.init(bars, page);
        bars.setGlobalActionHandler(ITextEditorActionDefinitionIds.TOGGLE_SHOW_SELECTED_ELEMENT_ONLY, fTogglePresentation);
        bars.setGlobalActionHandler(IJavaEditorActionDefinitionIds.TOGGLE_MARK_OCCURRENCES, fToggleMarkOccurrencesAction);
        bars.setGlobalActionHandler(IJavaEditorActionDefinitionIds.TOGGLE_BREADCRUMB, fToggleBreadcrumbAction);
    }

    /*
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
    @Override
    public void contributeToMenu(IMenuManager menu) {
        super.contributeToMenu(menu);
        IMenuManager editMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
        if (editMenu != null) {
            //$NON-NLS-1$
            MenuManager structureSelection = new MenuManager(JavaEditorMessages.ExpandSelectionMenu_label, "expandSelection");
            editMenu.insertAfter(ITextEditorActionConstants.SELECT_ALL, structureSelection);
            structureSelection.add(fStructureSelectEnclosingAction);
            structureSelection.add(fStructureSelectNextAction);
            structureSelection.add(fStructureSelectPreviousAction);
            structureSelection.add(fStructureSelectHistoryAction);
            editMenu.appendToGroup(ITextEditorActionConstants.GROUP_INFORMATION, fRetargetShowInformationAction);
        }
        IMenuManager navigateMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
        if (navigateMenu != null) {
            navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT, fShowOutline);
            navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT, fOpenHierarchy);
        }
        //$NON-NLS-1$
        IMenuManager gotoMenu = menu.findMenuUsingPath("navigate/goTo");
        if (gotoMenu != null) {
            //$NON-NLS-1$
            gotoMenu.add(new Separator("additions2"));
            //$NON-NLS-1$
            gotoMenu.appendToGroup("additions2", fGotoPreviousMemberAction);
            //$NON-NLS-1$
            gotoMenu.appendToGroup("additions2", fGotoNextMemberAction);
            //$NON-NLS-1$
            gotoMenu.appendToGroup("additions2", fGotoMatchingBracket);
        }
    }

    /*
	 * @see EditorActionBarContributor#setActiveEditor(IEditorPart)
	 */
    @Override
    public void setActiveEditor(IEditorPart part) {
        super.setActiveEditor(part);
        ITextEditor textEditor = null;
        if (part instanceof ITextEditor)
            textEditor = (ITextEditor) part;
        fTogglePresentation.setEditor(textEditor);
        fToggleMarkOccurrencesAction.setEditor(textEditor);
        fGotoMatchingBracket.setAction(getAction(textEditor, GotoMatchingBracketAction.GOTO_MATCHING_BRACKET));
        fShowOutline.setAction(getAction(textEditor, IJavaEditorActionDefinitionIds.SHOW_OUTLINE));
        fOpenHierarchy.setAction(getAction(textEditor, IJavaEditorActionDefinitionIds.OPEN_HIERARCHY));
        fOpenStructure.setAction(getAction(textEditor, IJavaEditorActionDefinitionIds.OPEN_STRUCTURE));
        fStructureSelectEnclosingAction.setAction(getAction(textEditor, StructureSelectionAction.ENCLOSING));
        fStructureSelectNextAction.setAction(getAction(textEditor, StructureSelectionAction.NEXT));
        fStructureSelectPreviousAction.setAction(getAction(textEditor, StructureSelectionAction.PREVIOUS));
        fStructureSelectHistoryAction.setAction(getAction(textEditor, StructureSelectionAction.HISTORY));
        fGotoNextMemberAction.setAction(getAction(textEditor, GoToNextPreviousMemberAction.NEXT_MEMBER));
        fGotoPreviousMemberAction.setAction(getAction(textEditor, GoToNextPreviousMemberAction.PREVIOUS_MEMBER));
        //$NON-NLS-1$
        fRemoveOccurrenceAnnotationsAction.setAction(getAction(textEditor, "RemoveOccurrenceAnnotations"));
        fRetargetShowInformationAction.setAction(getAction(textEditor, ITextEditorActionConstants.SHOW_INFORMATION));
        if (part instanceof JavaEditor) {
            JavaEditor javaEditor = (JavaEditor) part;
            javaEditor.getActionGroup().fillActionBars(getActionBars());
            FoldingActionGroup foldingActions = javaEditor.getFoldingActionGroup();
            if (foldingActions != null)
                foldingActions.updateActionBars();
        }
        IActionBars actionBars = getActionBars();
        IStatusLineManager manager = actionBars.getStatusLineManager();
        manager.setMessage(null);
        manager.setErrorMessage(null);
        /** The global actions to be connected with editor actions */
        IAction action = getAction(textEditor, ITextEditorActionConstants.NEXT);
        actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_NEXT_ANNOTATION, action);
        actionBars.setGlobalActionHandler(ITextEditorActionConstants.NEXT, action);
        action = getAction(textEditor, ITextEditorActionConstants.PREVIOUS);
        actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_PREVIOUS_ANNOTATION, action);
        actionBars.setGlobalActionHandler(ITextEditorActionConstants.PREVIOUS, action);
        action = getAction(textEditor, IJavaEditorActionConstants.COPY_QUALIFIED_NAME);
        actionBars.setGlobalActionHandler(CopyQualifiedNameAction.ACTION_HANDLER_ID, action);
        actionBars.setGlobalActionHandler(IJavaEditorActionDefinitionIds.SHOW_IN_BREADCRUMB, getAction(textEditor, IJavaEditorActionDefinitionIds.SHOW_IN_BREADCRUMB));
        //$NON-NLS-1$
        actionBars.setGlobalActionHandler("org.eclipse.jdt.internal.ui.actions.OpenHyperlink", getAction(textEditor, ITextEditorActionConstants.OPEN_HYPERLINK));
    }

    /*
	 * @see IEditorActionBarContributor#dispose()
	 */
    @Override
    public void dispose() {
        Iterator<RetargetAction> e = fPartListeners.iterator();
        while (e.hasNext()) getPage().removePartListener(e.next());
        fPartListeners.clear();
        setActiveEditor(null);
        fToggleBreadcrumbAction.dispose();
        super.dispose();
    }
}

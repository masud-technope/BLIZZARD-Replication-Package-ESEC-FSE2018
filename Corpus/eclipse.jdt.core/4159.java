/*******************************************************************************
 * Copyright (c) 2006, 2015 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation
 *    
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Manages context during a single round of annotation processing.
 */
public class RoundDispatcher {

    private final Set<TypeElement> _unclaimedAnnotations;

    private final RoundEnvironment _roundEnv;

    private final IProcessorProvider _provider;

    private boolean _searchForStar = false;

    private final PrintWriter _traceProcessorInfo;

    private final PrintWriter _traceRounds;

    /**
	 * Processors discovered so far.  This list may grow during the
	 * course of a round, as additional processors are discovered.
	 */
    private final List<ProcessorInfo> _processors;

    /**
	 * @param rootAnnotations a possibly empty but non-null set of annotations on the
	 * root compilation units of this round.  A local copy of the set will be made, to
	 * avoid modifying the set passed in.
	 * @param traceProcessorInfo a PrintWriter that processor trace output will be sent 
	 * to, or null if tracing is not desired.
	 * @param traceRounds 
	 */
    public  RoundDispatcher(IProcessorProvider provider, RoundEnvironment env, Set<TypeElement> rootAnnotations, PrintWriter traceProcessorInfo, PrintWriter traceRounds) {
        _provider = provider;
        _processors = provider.getDiscoveredProcessors();
        _roundEnv = env;
        _unclaimedAnnotations = new HashSet(rootAnnotations);
        _traceProcessorInfo = traceProcessorInfo;
        _traceRounds = traceRounds;
    }

    /**
	 * Handle a complete round, dispatching to all appropriate processors. 
	 */
    public void round() {
        if (null != _traceRounds) {
            StringBuilder sbElements = new StringBuilder();
            //$NON-NLS-1$
            sbElements.append("\tinput files: {");
            Iterator<? extends Element> iElements = _roundEnv.getRootElements().iterator();
            boolean hasNext = iElements.hasNext();
            while (hasNext) {
                sbElements.append(iElements.next());
                hasNext = iElements.hasNext();
                if (hasNext) {
                    sbElements.append(',');
                }
            }
            sbElements.append('}');
            _traceRounds.println(sbElements.toString());
            StringBuilder sbAnnots = new StringBuilder();
            //$NON-NLS-1$
            sbAnnots.append("\tannotations: [");
            Iterator<TypeElement> iAnnots = _unclaimedAnnotations.iterator();
            hasNext = iAnnots.hasNext();
            while (hasNext) {
                sbAnnots.append(iAnnots.next());
                hasNext = iAnnots.hasNext();
                if (hasNext) {
                    sbAnnots.append(',');
                }
            }
            sbAnnots.append(']');
            _traceRounds.println(sbAnnots.toString());
            //$NON-NLS-1$
            _traceRounds.println("\tlast round: " + _roundEnv.processingOver());
        }
        // If there are no root annotations, try to find a processor that claims "*"
        _searchForStar = _unclaimedAnnotations.isEmpty();
        // including the final round, but it may be called with an empty set of annotations.
        for (ProcessorInfo pi : _processors) {
            handleProcessor(pi);
        }
        // we have not yet run into a processor that claimed "*", continue discovery.
        while (_searchForStar || !_unclaimedAnnotations.isEmpty()) {
            ProcessorInfo pi = _provider.discoverNextProcessor();
            if (null == pi) {
                // There are no more processors to be discovered.
                break;
            }
            handleProcessor(pi);
        }
    // TODO: If !unclaimedAnnos.isEmpty(), issue a warning.
    }

    /**
	 * Evaluate a single processor.  Depending on the unclaimed annotations,
	 * the annotations this processor supports, and whether it has already been
	 * called in a previous round, possibly call its process() method.
	 */
    private void handleProcessor(ProcessorInfo pi) {
        try {
            Set<TypeElement> annotationsToProcess = new HashSet();
            boolean shouldCall = pi.computeSupportedAnnotations(_unclaimedAnnotations, annotationsToProcess);
            if (shouldCall) {
                boolean claimed = pi._processor.process(annotationsToProcess, _roundEnv);
                if (null != _traceProcessorInfo && !_roundEnv.processingOver()) {
                    StringBuilder sb = new StringBuilder();
                    //$NON-NLS-1$
                    sb.append(//$NON-NLS-1$
                    "Processor ");
                    sb.append(pi._processor.getClass().getName());
                    //$NON-NLS-1$
                    sb.append(//$NON-NLS-1$
                    " matches [");
                    Iterator<TypeElement> i = annotationsToProcess.iterator();
                    boolean hasNext = i.hasNext();
                    while (hasNext) {
                        sb.append(i.next());
                        hasNext = i.hasNext();
                        if (hasNext) {
                            sb.append(' ');
                        }
                    }
                    //$NON-NLS-1$
                    sb.append(//$NON-NLS-1$
                    "] and returns ");
                    sb.append(claimed);
                    _traceProcessorInfo.println(sb.toString());
                }
                if (claimed) {
                    // The processor claimed its annotations.
                    _unclaimedAnnotations.removeAll(annotationsToProcess);
                    if (pi.supportsStar()) {
                        _searchForStar = false;
                    }
                }
            }
        } catch (Throwable e) {
            _provider.reportProcessorException(pi._processor, new Exception(e));
        }
    }
}

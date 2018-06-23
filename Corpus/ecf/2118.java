/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.types;

import org.osgi.framework.BundleContext;

/**
 * <p>
 * Presentations for services implement this interface.
 * </p>
 * <p>
 * When the ServiceUI fetches a service that has the presentation property set,
 * it first invokes the <code>initComponent</code> method of the
 * <code>ServiceUIComponent</code> to let it initialize. Then, the
 * <code>getPanel</code> method is called that is expected to return a panel
 * that presents the service.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.3
 */
public interface ServiceUIComponent {

    /**
	 * called by the system when the component is initialized.
	 * 
	 * @param serviceObject
	 *            the service object of the service to which the
	 *            ServiceUIComponent is bound.
	 * @param context
	 *            a bundle context. Can be used if the component has to access
	 *            other OSGi services or has to interact differently with the
	 *            framework.
	 * @since 0.5
	 */
    void initComponent(final Object serviceObject, final BundleContext context);
    /**
	 * get the main panel of the presentation.
	 * 
	 * @return the panel.
	 * @since 0.5
	 */
    //	Panel getPanel();
}

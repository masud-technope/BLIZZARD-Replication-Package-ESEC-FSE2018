/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.timeservice.consumer.ds;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import com.mycorp.examples.timeservice.ITimeService;

@Component(immediate = true)
public class TimeServiceComponent {

    // Called by DS upon ITimeService discovery
    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void bindTimeService(ITimeService timeService) {
        System.out.println("Discovered ITimeService via DS.  Instance=" + timeService);
        // Call the service and print out result!
        System.out.println("Current time on remote is: " + timeService.getCurrentTime());
    }

    // Called by DS upon ITimeService undiscovery
    void unbindTimeService(ITimeService timeService) {
        System.out.println("Undiscovered ITimeService via DS.  Instance=" + timeService);
    }
}

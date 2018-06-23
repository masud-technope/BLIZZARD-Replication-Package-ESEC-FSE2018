/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package javax.servlet.jsp.tagext;

import java.util.Map;

public abstract class TagLibraryValidator {

    /**
     * Sole constructor. (For invocation by subclass constructors, 
     * typically implicit.)
     */
    public  TagLibraryValidator() {
    // NOOP by default
    }

    /**
     * Set the init data in the TLD for this validator.
     * Parameter names are keys, and parameter values are the values.
     *
     * @param map A Map describing the init parameters
     */
    public void setInitParameters(Map<String, Object> map) {
        initParameters = map;
    }

    /**
     * Get the init parameters data as an immutable Map.
     * Parameter names are keys, and parameter values are the values.
     *
     * @return The init parameters as an immutable map.
     */
    public Map<String, Object> getInitParameters() {
        return initParameters;
    }

    /**
     * Validate a JSP page.
     * This will get invoked once per unique tag library URI in the
     * XML view.  This method will return null if the page is valid; otherwise
     * the method should return an array of ValidationMessage objects.
     * An array of length zero is also interpreted as no errors.
     *
     * @param prefix the first prefix with which the tag library is 
     *     associated, in the XML view.  Note that some tags may use 
     *     a different prefix if the namespace is redefined.
     * @param uri the tag library's unique identifier
     * @param page the JspData page object
     * @return A null object, or zero length array if no errors, an array
     * of ValidationMessages otherwise.
     */
    public ValidationMessage[] validate(String prefix, String uri, PageData page) {
        return null;
    }

    /**
     * Release any data kept by this instance for validation purposes.
     */
    public void release() {
        initParameters = null;
    }

    // Private data
    private Map<String, Object> initParameters;
}

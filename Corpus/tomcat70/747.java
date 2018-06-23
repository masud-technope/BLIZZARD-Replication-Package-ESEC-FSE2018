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
package javax.servlet.jsp;

import javax.servlet.Servlet;

public interface JspPage extends Servlet {

    /**
     * The jspInit() method is invoked when the JSP page is initialized. It
     * is the responsibility of the JSP implementation (and of the class
     * mentioned by the extends attribute, if present) that at this point
     * invocations to the getServletConfig() method will return the desired
     * value.
     *
     * A JSP page can override this method by including a definition for it
     * in a declaration element.
     *
     * A JSP page should redefine the init() method from Servlet.
     */
    public void jspInit();

    /**
     * The jspDestroy() method is invoked when the JSP page is about to be
     * destroyed.
     * 
     * A JSP page can override this method by including a definition for it
     * in a declaration element.
     *
     * A JSP page should redefine the destroy() method from Servlet.
     */
    public void jspDestroy();
}

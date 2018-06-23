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

import javax.servlet.jsp.JspException;

public interface IterationTag extends Tag {

    /**
     * Request the reevaluation of some body.
     * Returned from doAfterBody.
     *
     * For compatibility with JSP 1.1, the value is carefully selected
     * to be the same as the, now deprecated, BodyTag.EVAL_BODY_TAG,
     * 
     */
    public static final int EVAL_BODY_AGAIN = 2;

    /**
     * Process body (re)evaluation.  This method is invoked by the
     * JSP Page implementation object after every evaluation of
     * the body into the BodyEvaluation object. The method is
     * not invoked if there is no body evaluation.
     *
     * <p>
     * If doAfterBody returns EVAL_BODY_AGAIN, a new evaluation of the
     * body will happen (followed by another invocation of doAfterBody).
     * If doAfterBody returns SKIP_BODY, no more body evaluations will occur,
     * and the doEndTag method will be invoked.
     *
     * <p>
     * If this tag handler implements BodyTag and doAfterBody returns
     * SKIP_BODY, the value of out will be restored using the popBody 
     * method in pageContext prior to invoking doEndTag.
     *
     * <p>
     * The method re-invocations may be lead to different actions because
     * there might have been some changes to shared state, or because
     * of external computation.
     *
     * <p>
     * The JSP container will resynchronize the values of any AT_BEGIN and
     * NESTED variables (defined by the associated TagExtraInfo or TLD) after
     * the invocation of doAfterBody().
     *
     * @return whether additional evaluations of the body are desired
     * @throws JspException if an error occurred while processing this tag
     */
    int doAfterBody() throws JspException;
}

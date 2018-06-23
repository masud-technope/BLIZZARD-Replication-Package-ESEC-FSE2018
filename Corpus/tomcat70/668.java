/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina;

import java.beans.PropertyChangeListener;

public interface Loader {

    // ------------------------------------------------------------- Properties
    /**
     * Execute a periodic task, such as reloading, etc. This method will be
     * invoked inside the classloading context of this container. Unexpected
     * throwables will be caught and logged.
     */
    public void backgroundProcess();

    /**
     * Return the Java class loader to be used by this Container.
     */
    public ClassLoader getClassLoader();

    /**
     * Return the Container with which this Loader has been associated.
     */
    public Container getContainer();

    /**
     * Set the Container with which this Loader has been associated.
     *
     * @param container The associated Container
     */
    public void setContainer(Container container);

    /**
     * Return the "follow standard delegation model" flag used to configure
     * our ClassLoader.
     */
    public boolean getDelegate();

    /**
     * Set the "follow standard delegation model" flag used to configure
     * our ClassLoader.
     *
     * @param delegate The new flag
     */
    public void setDelegate(boolean delegate);

    /**
     * Return descriptive information about this Loader implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo();

    /**
     * Return the reloadable flag for this Loader.
     */
    public boolean getReloadable();

    /**
     * Set the reloadable flag for this Loader.
     *
     * @param reloadable The new reloadable flag
     */
    public void setReloadable(boolean reloadable);

    // --------------------------------------------------------- Public Methods
    /**
     * Add a property change listener to this component.
     *
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a new repository to the set of repositories for this class loader.
     *
     * @param repository Repository to be added
     */
    public void addRepository(String repository);

    /**
     * Return the set of repositories defined for this class loader.
     * If none are defined, a zero-length array is returned.
     */
    public String[] findRepositories();

    /**
     * Has the internal repository associated with this Loader been modified,
     * such that the loaded classes should be reloaded?
     */
    public boolean modified();

    /**
     * Remove a property change listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}

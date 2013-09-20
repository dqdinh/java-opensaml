/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.metadata.resolver.impl;

import java.util.List;

import org.opensaml.saml.metadata.resolver.MetadataResolver;

/**
 * A metadata resolver that provides event notification to observers. This may be used, for example, to signal an update
 * of an internal cache of metadata allowing other subsystems to perform some action based on that.
 * 
 */
public interface ObservableMetadataResolver extends MetadataResolver {

    /**
     * Gets the list of observers for the resolver. New observers may be added to the list or old ones removed.
     * 
     * @return the list of observers
     */
    public List<Observer> getObservers();

    /**
     * An observer of metadata resolver changes.
     * 
     * <strong>NOTE:</strong> The metadata resolver that has changed is passed in to the
     * {@link #onEvent(MetadataResolver)} method. Observers should <strong>NOT</strong> keep a reference to this
     * resolver as this may prevent proper garbage collection.
     */
    public interface Observer {

        /**
         * Called when a resolver signals an event has occurred.
         * 
         * @param resolver the provider being observed
         */
        public void onEvent(MetadataResolver resolver);
    }
}
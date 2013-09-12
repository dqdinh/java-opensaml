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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract subclass for metadata resolvers that process and resolve metadata at a given point 
 * in time from a single metadata source document.
 */
public abstract class AbstractBatchMetadataResolver extends AbstractMetadataResolver {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractBatchMetadataResolver.class);
    
    /** Flag indicating whether to cache the original source metadata document. */
    private boolean cacheSourceMetadata;
    
    /** Constructor. */
    public AbstractBatchMetadataResolver() {
        super();
        setCacheSourceMetadata(true);
    }

    /**
     * Get whether to cache the original source metadata document.
     * 
     * @return true if source should be cached, false otherwise
     */
    public boolean isCacheSourceMetadata() {
        return cacheSourceMetadata;
    }
    
    /**
     * Set whether to cache the original source metadata document.
     * 
     * @param flag true if source should be cached, false otherwise
     */
    public void setCacheSourceMetadata(boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        cacheSourceMetadata = flag; 
    }
    
    /** {@inheritDoc} */
    @Nonnull protected BatchEntityBackingStore createNewBackingStore() {
        return new BatchEntityBackingStore();
    }
    
    /** {@inheritDoc} */
    @Nonnull protected BatchEntityBackingStore getBackingStore() {
        return (BatchEntityBackingStore) super.getBackingStore();
    }

    /**
     * Convenience method for getting the current effective cached original metadata.
     * 
     * <p>
     * Note: may or may not be the same as that obtained from {@link #getCachedFilteredMetadata()},
     * depending on what metadata filtering produced from the original metadata document.
     * </p>
     * 
     * @return the current effective cached metadata document
     */
    @Nullable protected XMLObject getCachedOriginalMetadata() {
       return getBackingStore().getCachedOriginalMetadata(); 
    }
    
    /**
     * Convenience method for getting the current effective cached filtered metadata.
     * 
     * <p>
     * Note: may or may not be the same as that obtained from {@link #getCachedOriginalMetadata()},
     * depending on what metadata filtering produced from the original metadata document.
     * </p>
     * 
     * @return the current effective cached metadata document
     */
    @Nullable protected XMLObject getCachedFilteredMetadata() {
       return getBackingStore().getCachedFilteredMetadata(); 
    }
    /**
     * Process the specified new metadata document, including metadata filtering 
     * and return its data in a new entity backing store instance.
     * 
     * @param root the root of the new metadata document being processed
     * 
     * @return the new backing store instance
     * 
     * @throws FilterException if there is a problem filtering the metadata
     */
    @Nonnull protected BatchEntityBackingStore preProcessNewMetadata(@Nonnull final XMLObject root) 
            throws FilterException {
        
        BatchEntityBackingStore newBackingStore = createNewBackingStore();
        
        XMLObject filteredMetadata = filterMetadata(root);
        
        if (isCacheSourceMetadata()) {
            newBackingStore.setCachedOriginalMetadata(root);
            newBackingStore.setCachedFilteredMetadata(filteredMetadata);
        } 
        
        if (filteredMetadata == null) {
            log.info("Metadata filtering process produced a null document, resulting in an empty data set");
            return newBackingStore;
        }
        
        if (filteredMetadata instanceof EntityDescriptor) {
            preProcessEntityDescriptor((EntityDescriptor)filteredMetadata, newBackingStore);
        } else if (filteredMetadata instanceof EntitiesDescriptor) {
            preProcessEntitiesDescriptor((EntitiesDescriptor)filteredMetadata, newBackingStore);
        } else {
            log.warn("Document root was neither an EntityDescriptor nor an EntitiesDescriptor: {}", 
                    root.getClass().getName());
        }
        
        return newBackingStore;
    }

    /**
     * Specialized entity backing store implementation which is capable of storing the original metadata 
     * document on which the backing store is based.
     */
    protected class BatchEntityBackingStore extends EntityBackingStore {
        
        /** The cached original source metadata document. */
        private XMLObject cachedOriginalMetadata;
        
        /** The cached original source metadata document. */
        private XMLObject cachedFilteredMetadata;
        
        /** Constructor. */
        protected BatchEntityBackingStore() {
            super();
        }

        /**
         * Get the cached original source metadata.
         * 
         * @return the cached metadata
         */
        public XMLObject getCachedOriginalMetadata() {
            return cachedOriginalMetadata;
        }

        /**
         * Set the cached original source metadata.
         * 
         * @param metadata The new cached metadata
         */
        public void setCachedOriginalMetadata(XMLObject metadata) {
            cachedOriginalMetadata = metadata;
        }
        
        /**
         * Get the cached filtered source metadata.
         * 
         * @return the cached metadata
         */
        public XMLObject getCachedFilteredMetadata() {
            return cachedFilteredMetadata;
        }

        /**
         * Set the cached filtered source metadata.
         * 
         * @param metadata The new cached metadata
         */
        public void setCachedFilteredMetadata(XMLObject metadata) {
            cachedFilteredMetadata = metadata;
        }
        
    }

}

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

package org.opensaml.xmlsec.signature.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xmlsec.signature.PGPData;
import org.opensaml.xmlsec.signature.PGPKeyID;
import org.opensaml.xmlsec.signature.PGPKeyPacket;

/**
 * Concrete implementation of {@link org.opensaml.xmlsec.signature.PGPData}.
 */
public class PGPDataImpl extends AbstractXMLObject implements PGPData {
    
    /** PGPKeyID child element value. */
    private PGPKeyID pgpKeyID;
    
    /** PGPKeyPacket child element value. */
    private PGPKeyPacket pgpKeyPacket;
    
    /** List of &lt;any&gt; wildcard XMLObject children. */
    private final IndexedXMLObjectChildrenList xmlChildren;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected PGPDataImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        xmlChildren = new IndexedXMLObjectChildrenList(this);
    }

    /** {@inheritDoc} */
    public PGPKeyID getPGPKeyID() {
        return this.pgpKeyID;
    }

    /** {@inheritDoc} */
    public void setPGPKeyID(PGPKeyID newPGPKeyID) {
        this.pgpKeyID = prepareForAssignment(this.pgpKeyID, newPGPKeyID);
    }

    /** {@inheritDoc} */
    public PGPKeyPacket getPGPKeyPacket() {
        return this.pgpKeyPacket;
    }

    /** {@inheritDoc} */
    public void setPGPKeyPacket(PGPKeyPacket newPGPKeyPacket) {
        this.pgpKeyPacket = prepareForAssignment(this.pgpKeyPacket, newPGPKeyPacket);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects() {
        return (List<XMLObject>) xmlChildren;
    }
    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects(QName typeOrName) {
        return (List<XMLObject>) xmlChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<>();
        
        if (pgpKeyID != null) {
            children.add(pgpKeyID);
        }
        if (pgpKeyPacket != null) {
            children.add(pgpKeyPacket);
        }
        children.addAll((List<XMLObject>) xmlChildren);
        
        if (children.size() == 0) {
            return null;
        }
        
        return Collections.unmodifiableList(children);
    }

}
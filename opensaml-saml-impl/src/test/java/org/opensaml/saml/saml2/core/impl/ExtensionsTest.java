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

/**
 * 
 */
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.Extensions;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.ExtensionsImpl}.
 */
public class ExtensionsTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor.
     *
     */
    public ExtensionsTest() {
       singleElementFile = "/org/opensaml/saml/saml2/core/impl/Extensions.xml";
       childElementsFile = "/org/opensaml/saml/saml2/core/impl/ExtensionsChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Extensions extensions = (Extensions) buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, extensions);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Extensions extensions = (Extensions) buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME);
        QName childQname = new QName("http://www.example.org/testObjects", "SimpleElement", "test");
        
        extensions.getUnknownXMLObjects().add(buildXMLObject(childQname));
        extensions.getUnknownXMLObjects().add(buildXMLObject(childQname));
        extensions.getUnknownXMLObjects().add(buildXMLObject(childQname));
        
        assertXMLEquals(expectedChildElementsDOM, extensions);
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Extensions extensions = (Extensions) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(extensions);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Extensions extensions = (Extensions) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(extensions);
        Assert.assertEquals(extensions.getUnknownXMLObjects().size(), 3);
    }
    
}
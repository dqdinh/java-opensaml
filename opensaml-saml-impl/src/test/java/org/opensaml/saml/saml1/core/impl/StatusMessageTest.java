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
package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.StatusMessage;

/**
 * Test for org.opensaml.saml.saml1.core.StatusMessage 
 */
public class StatusMessageTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final String contents;

    /**
     * Constructor
     *
     */
    public StatusMessageTest() {
        contents = "Nibble a Happy Warthog";
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleStatusMessage.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/FullStatusMessage.xml";
        
        qname = new QName(SAMLConstants.SAML10P_NS, StatusMessage.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        StatusMessage statusMessage = (StatusMessage) unmarshallElement(singleElementFile);
        Assert.assertNull(statusMessage.getMessage(), "Contents");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        StatusMessage statusMessage = (StatusMessage) unmarshallElement(singleElementOptionalAttributesFile);
        Assert.assertEquals(statusMessage.getMessage(), contents, "Contents");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        StatusMessage statusMessage = (StatusMessage) buildXMLObject(qname);

        statusMessage.setMessage(contents);
        assertXMLEquals(expectedOptionalAttributesDOM, statusMessage);
    }
}

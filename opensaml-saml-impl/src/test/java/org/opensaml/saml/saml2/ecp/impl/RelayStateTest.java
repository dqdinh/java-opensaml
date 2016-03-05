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

package org.opensaml.saml.saml2.ecp.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.ecp.RelayState;

/**
 * Test case for creating, marshalling, and unmarshalling {@link RelayState}.
 */
public class RelayStateTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedContent;
    
    private String expectedSOAP11Actor;
    
    private Boolean expectedSOAP11MustUnderstand;
    
    public RelayStateTest() {
        singleElementFile = "/org/opensaml/saml/saml2/ecp/impl/RelayState.xml";
    }
 
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedContent = "ThisIsSomeRelayState";
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        RelayState relayState = (RelayState) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(relayState);
        
        Assert.assertEquals(relayState.isSOAP11MustUnderstand(), expectedSOAP11MustUnderstand, "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(relayState.getSOAP11Actor(), expectedSOAP11Actor, "SOAP actor had unxpected value");
        Assert.assertEquals(relayState.getValue(), expectedContent, "Element content had unexpected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        RelayState relayState = (RelayState) buildXMLObject(RelayState.DEFAULT_ELEMENT_NAME);
        
        relayState.setSOAP11Actor(expectedSOAP11Actor);
        relayState.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        relayState.setValue(expectedContent);
        
        assertXMLEquals(expectedDOM, relayState);
    }

}

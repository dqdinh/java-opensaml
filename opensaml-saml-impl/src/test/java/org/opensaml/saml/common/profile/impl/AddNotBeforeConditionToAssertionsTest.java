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

package org.opensaml.saml.common.profile.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link AddNotBeforeConditionToAssertions} unit test. */
public class AddNotBeforeConditionToAssertionsTest  extends OpenSAMLInitBaseTestCase {

    private ProfileRequestContext prc;
    
    private AddNotBeforeConditionToAssertions action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().setOutboundMessage(
                SAML1ActionTestingSupport.buildResponse()).buildProfileRequestContext();
        
        action = new AddNotBeforeConditionToAssertions();
        action.initialize();
    }
    
    /** Test that action errors out properly if there is no response. */
    @Test
    public void testNoResponse() {
        prc.setOutboundMessageContext(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    /** Test that action works properly if there is no assertion in the response. */
    @Test
    public void testNoAssertion() {
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response.
     */
    @Test
    public void testSingleAssertion() {
        final Assertion assertion = SAML1ActionTestingSupport.buildAssertion();

        final Response response = (Response) prc.getOutboundMessageContext().getMessage();
        response.getAssertions().add(assertion);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getNotBefore());
    }

    /**
     * Test that the condition is properly added if there is a single assertion, with a Conditions element, in the
     * response.
     */
    @Test
    public void testSingleAssertionWithExistingConditions() {
        SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
        final Conditions conditions = conditionsBuilder.buildObject();

        final Assertion assertion = SAML1ActionTestingSupport.buildAssertion();
        assertion.setConditions(conditions);

        final Response response = (Response) prc.getOutboundMessageContext().getMessage();
        response.getAssertions().add(assertion);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertSame(assertion.getConditions(), conditions);
        Assert.assertNotNull(assertion.getConditions().getNotBefore());
    }

    /** Test that the condition is properly added if there are multiple assertions in the response. */
    @Test
    public void testMultipleAssertion() {
        final Response response = (Response) prc.getOutboundMessageContext().getMessage();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 3);

        for (final Assertion assertion : response.getAssertions()) {
            Assert.assertNotNull(assertion.getConditions());
            Assert.assertNotNull(assertion.getConditions().getNotBefore());
        }
    }

    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response.
     */
    @Test
    public void testSAML2Assertion() {
        final org.opensaml.saml.saml2.core.Assertion assertion = SAML2ActionTestingSupport.buildAssertion();
        final org.opensaml.saml.saml2.core.Response response = SAML2ActionTestingSupport.buildResponse();
        
        prc.getOutboundMessageContext().setMessage(response);
        response.getAssertions().add(assertion);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getNotBefore());
    }

}
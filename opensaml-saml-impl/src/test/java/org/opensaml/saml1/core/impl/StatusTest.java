/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache 
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

package org.opensaml.saml1.core.impl;

import javax.xml.namespace.QName;

import org.opensaml.common.BaseSAMLObjectProviderTestCase;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml1.core.Status;
import org.opensaml.saml1.core.StatusCode;
import org.opensaml.saml1.core.StatusMessage;

/**
 * org.opensaml.saml1.core.Status.
 */
public class StatusTest extends BaseSAMLObjectProviderTestCase {

    /** name used to generate objects. */
    private final QName qname;

    /**
     * Constructor.
     */
    public StatusTest() {
        super();

        childElementsFile = "/data/org/opensaml/saml1/impl/FullStatus.xml";
        singleElementFile = "/data/org/opensaml/saml1/impl/singleStatus.xml";

        qname = new QName(SAMLConstants.SAML10P_NS, Status.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */

    public void testSingleElementUnmarshall() {

        Status status = (Status) unmarshallElement(singleElementFile);

        assertNotNull("StatusCode", status.getStatusCode());
        assertNull("StatusMessage", status.getStatusMessage());
        assertNull("StatusDetail", status.getStatusDetail());
    }

    /**
     * Test an Response file with children.
     */
    public void testChildElementsUnmarshall() {
        Status status = (Status) unmarshallElement(childElementsFile);

        assertNotNull("StatusCode", status.getStatusCode());
        assertNotNull("StatusMessage", status.getStatusMessage());
    }

    /** {@inheritDoc} */

    public void testSingleElementMarshall() {
        Status status = (Status) buildXMLObject(qname);

        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);

        assertEquals(expectedDOM, status);
    }

    /** {@inheritDoc} */

    public void testChildElementsMarshall() {
        Status status = (Status) buildXMLObject(qname);

        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);

        StatusMessage statusMessage = (StatusMessage) buildXMLObject(StatusMessage.DEFAULT_ELEMENT_NAME);
        status.setStatusMessage(statusMessage);

        assertEquals(expectedChildElementsDOM, status);
    }
}
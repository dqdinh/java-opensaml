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

package org.opensaml.saml.saml2.metadata.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.opensaml.saml.saml2.metadata.AttributeService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;

/**
 * A thread safe unmarshaller for {@link org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor}s.
 */
public class AttributeAuthorityDescriptorUnmarshaller extends RoleDescriptorUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentElement, XMLObject childElement) throws UnmarshallingException {
        AttributeAuthorityDescriptor descriptor = (AttributeAuthorityDescriptor) parentElement;

        if (childElement instanceof AttributeService) {
            descriptor.getAttributeServices().add((AttributeService) childElement);
        } else if (childElement instanceof AssertionIDRequestService) {
            descriptor.getAssertionIDRequestServices().add((AssertionIDRequestService) childElement);
        } else if (childElement instanceof NameIDFormat) {
            descriptor.getNameIDFormats().add((NameIDFormat) childElement);
        } else if (childElement instanceof AttributeProfile) {
            descriptor.getAttributeProfiles().add((AttributeProfile) childElement);
        } else if (childElement instanceof Attribute) {
            descriptor.getAttributes().add((Attribute) childElement);
        } else {
            super.processChildElement(parentElement, childElement);
        }
    }
}
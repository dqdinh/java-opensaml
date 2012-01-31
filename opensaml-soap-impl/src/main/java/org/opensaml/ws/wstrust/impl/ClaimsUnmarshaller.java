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

package org.opensaml.ws.wstrust.impl;

import org.opensaml.ws.wstrust.Claims;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLObjectSupport;
import org.w3c.dom.Attr;



/**
 * Unmarshaller for the wst:Claims element.
 * 
 */
public class ClaimsUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        Claims claims = (Claims) xmlObject;
        if (Claims.DIALECT_ATTRIB_NAME.equals(attribute.getLocalName())) {
            claims.setDialect(attribute.getValue());
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(claims.getUnknownAttributes(), attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        Claims claims = (Claims) parentXMLObject;
        claims.getUnknownXMLObjects().add(childXMLObject);
    }

}

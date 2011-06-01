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

package org.opensaml.ws.wstrust.impl;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * An abstract marshaller implementation for XMLObjects from {@link org.opensaml.ws.wstrust}.
 * 
 */
public abstract class AbstractWSTrustObjectMarshaller extends AbstractXMLObjectMarshaller {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractWSTrustObjectMarshaller.class);

    /** Constructor. */
    protected AbstractWSTrustObjectMarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        log.debug("{} has no more attribute to marshall.", xmlObject.getElementQName().getLocalPart());

    }

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        log.debug("{} has no content to marshall.", xmlObject.getElementQName().getLocalPart());
    }
}

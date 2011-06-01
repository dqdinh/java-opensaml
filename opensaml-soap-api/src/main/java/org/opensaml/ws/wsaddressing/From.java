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

package org.opensaml.ws.wsaddressing;

import javax.xml.namespace.QName;

/**
 * The &lt;wsa:From&gt; element.
 * 
 * @see "WS-Addressing 1.0 - Core"
 * 
 */
public interface From extends EndpointReferenceType {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "From";

    /** Default element name. */
    public static final QName ELEMENT_NAME =
        new QName(WSAddressingConstants.WSA_NS, ELEMENT_LOCAL_NAME, WSAddressingConstants.WSA_PREFIX);
}

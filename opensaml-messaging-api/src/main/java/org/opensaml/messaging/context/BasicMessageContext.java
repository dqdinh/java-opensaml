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

package org.opensaml.messaging.context;

import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;

/**
 * A basic implementation of of {@link MessageContext}.
 * 
 * @param <MessageType> the type of message represented by the message context
 */
public class BasicMessageContext<MessageType> extends AbstractSubcontextContainer implements
        MessageContext<MessageType> {

    /** The message represented. */
    private MessageType msg;

    /** The context unique identifier. */
    private String id;

    /** The context creation timestamp. */
    private DateTime creationTime;

    /** Constructor. Generates a random context ID. */
    public BasicMessageContext() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Constructor.
     * 
     * @param contextId ID for this context, not null nor empty
     */
    public BasicMessageContext(String contextId) {
        creationTime = new DateTime();
        id = Assert.isNotNull(StringSupport.trimOrNull(contextId), "Context ID can not be null or empty");
    }

    /** {@inheritDoc} */
    public MessageType getMessage() {
        return msg;
    }

    /** {@inheritDoc} */
    public void setMessage(MessageType message) {
        msg = message;
    }

    /** {@inheritDoc} */
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    public DateTime getCreationTime() {
        return creationTime;
    }
}
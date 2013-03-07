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

package org.opensaml.saml.common.binding.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.apache.bcel.generic.LOOKUPSWITCH;
import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.util.net.BasicUrlComparator;
import org.opensaml.util.net.UriComparator;
import org.opensaml.util.net.UriException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message handler which checks the validity of the SAML protocol message receiver 
 * endpoint against requirements indicated in the message.
 */
public class ReceivedEndpointSecurityHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ReceivedEndpointSecurityHandler.class);
    
    /** The URI comparator to use in performing the validation. */
    private UriComparator uriComparator;
    
    /** Lookup strategy for resolving the HttpServletRequest. */
    private ContextDataLookupFunction<MessageContext<?>, HttpServletRequest> requestLookupStrategy;

    /**
     * Constructor.
     */
    public ReceivedEndpointSecurityHandler() {
        super();
        uriComparator = new BasicUrlComparator();
    }

    /**
     * Get the URI comparator instance to use.
     * 
     * @return the uriComparator.
     */
    @Nonnull public UriComparator getUriComparator() {
        return uriComparator;
    }

    /**
     * Set the URI comparator instance to use.
     * 
     * @param comparator the new URI comparator to use
     */
    public void setUriComparator(@Nonnull final UriComparator comparator) {
       uriComparator = Constraint.isNotNull(comparator, "UriComparator may not be null");
    }

    /**
     * Get the HttpServletRequest lookup strategy to use. 
     * 
     * @return the lookup strategy
     */
    public ContextDataLookupFunction<MessageContext<?>, HttpServletRequest> getRequestLookupStrategy() {
        return requestLookupStrategy;
    }

    /**
     * Set the HttpServletRequest lookup strategy to use. 
     * 
     * @param strategy the new lookup strategy
     */
    public void setRequestLookupStrategy(
            @Nonnull final ContextDataLookupFunction<MessageContext<?>, HttpServletRequest> strategy) {
        requestLookupStrategy = Constraint.isNotNull(strategy, "HttpServletRequest lookup strategy may no be null");
    }

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        checkEndpointUri(messageContext, getUriComparator());
    }
    
    /**
     * Compare the message endpoint URI's specified.
     * 
     * <p>The comparison is performed using the specified instance of {@link UriComparator}.</p>
     * 
     * @param messageDestination the intended message destination endpoint URI
     * @param receiverEndpoint the endpoint URI at which the message was received
     * @param comparator the comparator instance to use
     * 
     * @return true if the endpoints are equivalent, false otherwise
     * 
     * @throws UriException if one of the URI's to evaluate is invalid 
     */
    protected boolean compareEndpointUris(@Nonnull @NotEmpty final String messageDestination, 
            @Nonnull @NotEmpty final String receiverEndpoint, 
            @Nonnull final UriComparator comparator) throws UriException {
        Constraint.isNotNull(messageDestination, "Message destination URI was null");
        Constraint.isNotNull(receiverEndpoint, "Receiver endpoint URI was null");
        Constraint.isNotNull(comparator, "UriComparator was null");
        return comparator.compare(messageDestination, receiverEndpoint);
    }
    
    /**
     * Check the validity of the SAML protocol message receiver endpoint against
     * requirements indicated in the message.
     * 
     * @param messageContext current message context
     * @param comparator the URI comparator instance to use, if null an internal default will be used
     * 
     * @throws MessageHandlerException thrown if the message was received at an endpoint consistent 
     *              with message requirements, or if there is a problem decoding and processing
     *              the message Destination or receiver endpoint information
     */
    protected void checkEndpointUri(@Nonnull final MessageContext<SAMLObject> messageContext, 
            @Nullable final UriComparator comparator) throws MessageHandlerException {
        Constraint.isNotNull(comparator, "UriComparator may not be null");
        log.debug("Checking SAML message intended destination endpoint against receiver endpoint");
        
        String messageDestination;
        try {
            messageDestination = StringSupport.trimOrNull(
                    SAMLBindingSupport.getIntendedDestinationEndpointUri(messageContext));
        } catch (MessageException e) {
            throw new MessageHandlerException("Error obtaining message intended destination endpoint URI", e);
        }
        
        boolean bindingRequires = SAMLBindingSupport.isIntendedDestinationEndpointUriRequired(messageContext);
        
        if (messageDestination == null) {
            if (bindingRequires) {
                log.error("SAML message intended destination endpoint URI required by binding was empty");
                throw new MessageHandlerException("SAML message intended destination (required by binding) was not present");
            } else {
                log.debug("SAML message intended destination endpoint was empty, not required by binding, skipping");
                return;
            }
        }
        
        String receiverEndpoint;
        try {
            receiverEndpoint = StringSupport.trimOrNull(
                    SAMLBindingSupport.getActualReceiverEndpointUri(messageContext, requestLookupStrategy));
        } catch (MessageException e) {
            throw new MessageHandlerException("Error obtaining message received endpoint URI", e);
        }
        
        log.debug("Intended message destination endpoint: {}", messageDestination);
        log.debug("Actual message receiver endpoint: {}", receiverEndpoint);
        
        boolean matched;
        try {
            matched = compareEndpointUris(messageDestination, receiverEndpoint, comparator);
        } catch (UriException e) {
            throw new MessageHandlerException("Error comparing endpoint URI's", e);
        }
        if (!matched) {
            log.error("SAML message intended destination endpoint '{}' did not match the recipient endpoint '{}'",
                    messageDestination, receiverEndpoint);
            throw new MessageHandlerException("SAML message failed received endpoint check");
        } else {
            log.debug("SAML message intended destination endpoint matched recipient endpoint");
            return;
        }
    }

}
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

package org.opensaml.xmlsec.impl;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.criterion.KeyInfoGenerationProfileCriterion;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 */
public class BasicSignatureSigningParametersResolverTest extends XMLObjectBaseTestCase {
    
    private BasicSignatureSigningParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private SignatureSigningConfigurationCriterion criterion;
    
    private BasicSignatureSigningConfiguration config1, config2, config3;
    
    private Credential rsaCred, dsaCred, ecCred, hmacCred;
    
    private String defaultReferenceDigest = SignatureConstants.ALGO_ID_DIGEST_SHA1;
    
    private String defaultC14N = SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
    
    private String defaultRSAAlgo = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
    private String defaultDSAAlgo = SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1;
    private String defaultECAlgo = SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1;
    private String defaultHMACAlgo = SignatureConstants.ALGO_ID_MAC_HMAC_SHA1;
    
    private Integer defaultHMACOutputLength = 128;
     
    private NamedKeyInfoGeneratorManager defaultKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    
    //TODO test DSAParams stuff if decide to keep that
    
    @BeforeClass
    public void buildCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair rsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        rsaCred = CredentialSupport.getSimpleCredential(rsaKeyPair.getPublic(), rsaKeyPair.getPrivate());
        
        KeyPair dsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        dsaCred = CredentialSupport.getSimpleCredential(dsaKeyPair.getPublic(), dsaKeyPair.getPrivate()); 
        
        try {
            KeyPair ecKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, 256, null);
            ecCred = CredentialSupport.getSimpleCredential(ecKeyPair.getPublic(), ecKeyPair.getPrivate()); 
        } catch (NoSuchAlgorithmException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm failures here for now.
        }
        
        SecretKey hmacKey = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null);
        hmacCred = CredentialSupport.getSimpleCredential(hmacKey);
    }
    
    @BeforeMethod
    public void setUp() {
        resolver = new BasicSignatureSigningParametersResolver();
        
        config1 = new BasicSignatureSigningConfiguration();
        config2 = new BasicSignatureSigningConfiguration();
        config3 = new BasicSignatureSigningConfiguration();
        
        // Set these as defaults on the last config in the chain, just so don't have to set in every test.
        config3.setSignatureAlgorithmURIs(Lists.newArrayList(defaultRSAAlgo, defaultDSAAlgo, defaultECAlgo, defaultHMACAlgo));
        config3.setSignatureReferenceDigestMethods(Lists.newArrayList(defaultReferenceDigest));
        config3.setSignatureCanonicalizationAlgorithm(defaultC14N);
        config3.setSignatureHMACOutputLength(defaultHMACOutputLength);
        
        BasicKeyInfoGeneratorFactory basicFactory = new BasicKeyInfoGeneratorFactory();
        X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
        defaultKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
        defaultKeyInfoGeneratorManager.registerDefaultFactory(basicFactory);
        defaultKeyInfoGeneratorManager.registerDefaultFactory(x509Factory);
        config3.setKeyInfoGeneratorManager(defaultKeyInfoGeneratorManager);
        
        criterion = new SignatureSigningConfigurationCriterion(config1, config2, config3);
        
        criteriaSet = new CriteriaSet(criterion);
    }
    
    @Test
    public void testBasicRSA() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithAlgorithmOverride() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        
        config2.setSignatureAlgorithmURIs(Lists.newArrayList(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithBlacklist() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        config1.setBlacklistedAlgorithmURIs(Sets.newHashSet(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureConstants.ALGO_ID_DIGEST_SHA1));
        
        // Deliberately putting SHA-1 variants first here.  They should be filtered out.
        config2.setSignatureAlgorithmURIs(Lists.newArrayList(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        config2.setSignatureReferenceDigestMethods(Lists.newArrayList(SignatureConstants.ALGO_ID_DIGEST_SHA1, SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithWhitelist() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        config1.setWhitelistedAlgorithmURIs(Sets.newHashSet(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        // Deliberately putting SHA-1 variants first here.  They should be filtered out.
        config2.setSignatureAlgorithmURIs(Lists.newArrayList(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        config2.setSignatureReferenceDigestMethods(Lists.newArrayList(SignatureConstants.ALGO_ID_DIGEST_SHA1, SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testBasicDSA() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(dsaCred));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), dsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultDSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testBasicEC() throws ResolverException {
        // EC support isn't universal
        if (ecCred != null) {
            config1.setSigningCredentials(Lists.newArrayList(ecCred));
            
            SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
            
            Assert.assertNotNull(params);
            Assert.assertEquals(params.getSigningCredential(), ecCred);
            Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultECAlgo);
            Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
            Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
            Assert.assertNull(params.getSignatureHMACOutputLength());
            Assert.assertNotNull(params.getKeyInfoGenerator());
        }
    }
    
    @Test
    public void testBasicHMAC() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(hmacCred));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), hmacCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultHMACAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertEquals(params.getSignatureHMACOutputLength(), defaultHMACOutputLength);
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testHMACWithOverrides() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(hmacCred));
        
        config2.setSignatureAlgorithmURIs(Lists.newArrayList(SignatureConstants.ALGO_ID_MAC_HMAC_SHA256));
        config2.setSignatureReferenceDigestMethods(Lists.newArrayList(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        config2.setSignatureHMACOutputLength(160);
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), hmacCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertEquals(params.getSignatureHMACOutputLength(), new Integer(160));
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testC14NOverride() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        
        config2.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N11_WITH_COMMENTS);
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), SignatureConstants.ALGO_ID_C14N11_WITH_COMMENTS);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    
    @Test
    public void testMultipleCreds() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred, dsaCred));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
        
        config1.setSigningCredentials(Lists.newArrayList(dsaCred, rsaCred));
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), dsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultDSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
        
        // EC not supported in all JREs out-of-the-box.
        if (ecCred != null) {
            config1.setSigningCredentials(Lists.newArrayList(ecCred, dsaCred, rsaCred));
            
            params = resolver.resolveSingle(criteriaSet);
            
            Assert.assertNotNull(params);
            Assert.assertEquals(params.getSigningCredential(), ecCred);
            Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultECAlgo);
            Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
            Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
            Assert.assertNull(params.getSignatureHMACOutputLength());
            Assert.assertNotNull(params.getKeyInfoGenerator());
        }
        
        config1.setSigningCredentials(Lists.newArrayList(hmacCred, dsaCred, rsaCred));
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), hmacCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultHMACAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertEquals(params.getSignatureHMACOutputLength(), defaultHMACOutputLength);
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testCredOverrides() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(dsaCred));
        
        config2.setSigningCredentials(Lists.newArrayList(rsaCred, dsaCred, hmacCred));
        config2.setSignatureAlgorithmURIs(Lists.newArrayList(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), dsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultDSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testKeyInfoGenerationProfile() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        
        criteriaSet.add(new KeyInfoGenerationProfileCriterion("testKeyInfoProfile"));
        
        defaultKeyInfoGeneratorManager.setUseDefaultManager(true);
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getKeyInfoGenerator());
        
        defaultKeyInfoGeneratorManager.setUseDefaultManager(false);
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params.getKeyInfoGenerator());
        
        defaultKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testResolve() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        
        Iterable<SignatureSigningParameters> paramsIter = resolver.resolve(criteriaSet);
        Assert.assertNotNull(paramsIter);
        
        Iterator<SignatureSigningParameters> iterator = paramsIter.iterator();
        Assert.assertNotNull(iterator);
        
        Assert.assertTrue(iterator.hasNext());
        
        SignatureSigningParameters params =iterator.next();
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred);
        Assert.assertEquals(params.getSignatureAlgorithmURI(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
        
        Assert.assertFalse(iterator.hasNext());
    }
    
    @Test(expectedExceptions=ResolverException.class)
    public void testNoCredentials() throws ResolverException {
        resolver.resolve(criteriaSet);
    }
    
    @Test(expectedExceptions=ResolverException.class)
    public void testNoAlgorithms() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        config3.setSignatureAlgorithmURIs(new ArrayList<String>());
        resolver.resolve(criteriaSet);
    }
    
    @Test(expectedExceptions=ResolverException.class)
    public void testNoReferenceDigestMethods() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        config3.setSignatureReferenceDigestMethods(new ArrayList<String>());
        resolver.resolve(criteriaSet);
    }
    
    @Test(expectedExceptions=ResolverException.class)
    public void testNoC14NAlgorithm() throws ResolverException {
        config1.setSigningCredentials(Lists.newArrayList(rsaCred));
        config3.setSignatureCanonicalizationAlgorithm(null);
        resolver.resolve(criteriaSet);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullCriteriaSet() throws ResolverException {
        resolver.resolve(null);
    }

    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testAbsentCriterion() throws ResolverException {
        resolver.resolve(new CriteriaSet());
    }

}
/*
 * @(#)MultiSpiBuilderTest.java     5 Dec 2010
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package com.qrmedia.commons.multispi.config;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import uk.gov.mi6.LicenseToKill;

import com.qrmedia.commons.multispi.provider.AnnotationScanningProvider;
import com.qrmedia.commons.multispi.provider.MetaInfServicesProvider;
import com.qrmedia.commons.multispi.provider.ServiceClassnameAttributeProvider;
import com.qrmedia.commons.multispi.provider.ServiceImplementationProvider;
import com.qrmedia.commons.reflect.ReflectionUtils;

/**
 * Unit tests for the {@link MultiSpiBuilder}.
 * 
 * @author aphillips
 * @since 5 Dec 2010
 *
 */
public class MultiSpiBuilderTest {

    @Test
    public void getReturnsTheSameAsBuild() {
        assertEquals(new MultiSpiBuilder().withDefaults().build(), 
                     new MultiSpiBuilder().withDefaults().get());
    }
    
    @Test
    public void defaultsToMetaInfServices() throws IllegalAccessException {
        Set<ServiceImplementationProvider> providers = ReflectionUtils.getValue(
                new MultiSpiBuilder().withDefaults().build(), "providers");
        assertEquals(1, providers.size());
        ServiceImplementationProvider provider = getOnlyElement(providers);
        assertTrue(format("Expected the provider to be an instance of '%s' but was '%s'",
                MetaInfServicesProvider.class.getSimpleName(), provider),
                provider instanceof MetaInfServicesProvider);
    }
    
    private static class StubProvider implements ServiceImplementationProvider {
        public Set<String> findServiceImplementations(Class<?> serviceClass, ClassLoader classpathResourceLoader) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }
    }
    
    @Test
    public void keepsTrackOfAddedScanners() throws IllegalAccessException {
        List<ServiceImplementationProvider> providers = newArrayList(
            ReflectionUtils.<Set<ServiceImplementationProvider>>getValue(
                new MultiSpiBuilder().withAnnotationScanning(LicenseToKill.class, "uk")
                .withManifestServiceClassnameAttributeScanning().withDefaults().withMetaInfServicesScanning()
                .withProviders(new StubProvider()).build(), "providers"));
        assertEquals(4, providers.size());
        // sort on class name
        Collections.sort(providers, new Comparator<ServiceImplementationProvider>() {
                public int compare(ServiceImplementationProvider o1,
                        ServiceImplementationProvider o2) {
                    return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
                }
            });
        
        ServiceImplementationProvider provider = providers.get(0);
        assertTrue(format("Expected the provider to be an instance of '%s' but was '%s'",
                AnnotationScanningProvider.class.getSimpleName(), provider),
                provider instanceof AnnotationScanningProvider);
        assertEquals(LicenseToKill.class, ReflectionUtils.getValue(provider, "markerAnnotation"));
        
        provider = providers.get(1);
        assertTrue(format("Expected the provider to be an instance of '%s' but was '%s'",
                MetaInfServicesProvider.class.getSimpleName(), provider),
                provider instanceof MetaInfServicesProvider);
        
        provider = providers.get(2);
        assertTrue(format("Expected the provider to be an instance of '%s' but was '%s'",
                ServiceClassnameAttributeProvider.class.getSimpleName(), provider),
                provider instanceof ServiceClassnameAttributeProvider);

        provider = providers.get(3);
        assertTrue(format("Expected the provider to be an instance of '%s' but was '%s'",
                StubProvider.class.getSimpleName(), provider),
                provider instanceof StubProvider);        
    }
}

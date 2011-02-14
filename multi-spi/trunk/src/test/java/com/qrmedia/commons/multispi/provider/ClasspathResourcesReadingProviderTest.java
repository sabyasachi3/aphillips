/*
 * @(#)MetaInfServicesProviderTest.java     4 Dec 2010
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
package com.qrmedia.commons.multispi.provider;

import static com.google.common.base.Functions.constant;
import static com.google.common.collect.Iterables.elementsEqual;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import uk.gov.mi6.Agent;

/**
 * Unit tests for the {@link ClasspathResourcesReadingProvider}.
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
public class ClasspathResourcesReadingProviderTest {
    private static final String CLASSPATH_RESOURCE_NAME = "agent-directory.xml";
    
    private final ClasspathResourceCollectingProvider provider = new ClasspathResourceCollectingProvider(); 
    
    private static class ClasspathResourceCollectingProvider extends ClasspathResourcesReadingProvider<URL> { 
        private final Set<String> collectedResources = newHashSet();
        
        protected ClasspathResourceCollectingProvider() {
            super(constant(CLASSPATH_RESOURCE_NAME), new IoFunction<URL, URL>() {
                    public URL apply(URL item) throws IOException {
                        return item;
                    }
                });
        }

        @Override
        protected Set<String> processResource(URL resource, Class<?> serviceClass) {
            String url = resource.toString();
            collectedResources.add(url);
            return newHashSet(url);
        }
    }

    @Test
    public void getsResourcesFromProvidedClassLoader() throws IOException {
        ClassLoader classpathResourceLoader = createMock(ClassLoader.class);
        final List<String> expectedResources = newArrayList("http://mi5.gov.uk", "http://mi6.gov.uk"); 
        expect(classpathResourceLoader.getResources(CLASSPATH_RESOURCE_NAME))
        .andReturn(new Enumeration<URL>() {
                int index = 0;
                
                public boolean hasMoreElements() {
                    return (index < expectedResources.size());
                }
                
                public URL nextElement() {
                    try {
                        return new URL(expectedResources.get(index++));
                    } catch (MalformedURLException exception) {
                        throw new AssertionError(exception);
                    }
                }
            });
        replay(classpathResourceLoader);
        provider.findServiceImplementations(Agent.class, classpathResourceLoader);
        elementsEqual(expectedResources, provider.collectedResources);
        assertTrue(format("Expected %s to contain elements %s", provider.collectedResources,
                expectedResources), elementsEqual(expectedResources, provider.collectedResources));
    }
    
}

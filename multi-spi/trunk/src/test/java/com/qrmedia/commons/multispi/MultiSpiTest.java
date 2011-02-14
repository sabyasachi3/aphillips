/*
 * @(#)MultiSpiTest.java     5 Dec 2010
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
package com.qrmedia.commons.multispi;

import static com.google.common.collect.Sets.newHashSet;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.gov.mi6.Agent;
import uk.gov.mi6.agent.JackGiddings;
import uk.gov.mi6.agent.JamesBond;
import uk.gov.mi6.agent.StuartThomas;

import com.qrmedia.commons.multispi.MultiSpi.ClassLoaderSupplier;
import com.qrmedia.commons.multispi.provider.ServiceImplementationProvider;
import com.qrmedia.commons.reflect.ReflectionUtils;

/**
 * Unit tests for the {@link MultiSpi}.
 * 
 * @author aphillips
 * @since 5 Dec 2010
 *
 */
public class MultiSpiTest {
    private MultiSpi multiSpi;
   
    @Test
    public void callsAllProviders() {
        ClassLoader classpathResourceLoader = createNiceMock(ClassLoader.class);
        ServiceImplementationProvider provider1 = createMock(ServiceImplementationProvider.class);
        expect(provider1.findServiceImplementations(Agent.class, classpathResourceLoader))
        .andReturn(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()));
        ServiceImplementationProvider provider2 = createMock(ServiceImplementationProvider.class);
        expect(provider2.findServiceImplementations(Agent.class, classpathResourceLoader))
        .andReturn(newHashSet(JamesBond.class.getName(), StuartThomas.class.getName()));
        replay(provider1, provider2);
        
        multiSpi = new MultiSpi(newHashSet(provider1, provider2));
        assertEquals(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName(), 
                    StuartThomas.class.getName()),
                multiSpi.findImplementationNames(Agent.class, classpathResourceLoader));
        verify(provider1, provider2);
    }
    
    @Test
    public void callsProvidersWithProvidedClassloader() {
        ClassLoader classpathResourceLoader = createNiceMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, classpathResourceLoader))
        .andReturn(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()));
        replay(provider);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        assertEquals(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()),
                multiSpi.findImplementationNames(Agent.class, classpathResourceLoader));
        verify(provider);
    }
    
    @Test
    public void callsProvidersWithContextClassloaderByDefault() {
        ClassLoader contextLoader = createNiceMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, contextLoader))
        .andReturn(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()));
        replay(provider);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(contextLoader);

        multiSpi = new MultiSpi(newHashSet(provider));
        try {
            assertEquals(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()),
                    multiSpi.findImplementationNames(Agent.class));
            verify(provider);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }
    
    @Test
    public void callsAllProvidersWithSystemClassloaderForNullContextLoader() throws IllegalAccessException {
        ClassLoader systemLoader = createNiceMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, systemLoader))
        .andReturn(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()));
        ClassLoaderSupplier loaderSupplier = createNiceMock(ClassLoaderSupplier.class);
        expect(loaderSupplier.getSystemClassLoader()).andReturn(systemLoader);
        replay(provider, loaderSupplier);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);

        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderSupplier", loaderSupplier);
        try {
            assertEquals(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()),
                    multiSpi.findImplementationNames(Agent.class));
            verify(provider);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }
    
    @Test
    public void callsAllProvidersWithBootstrapClassloaderForNullSystemLoader() throws IllegalAccessException {
        ClassLoader bootstrapLoader = createNiceMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, bootstrapLoader))
        .andReturn(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()));
        
        // will return null for other loaders        
        ClassLoaderSupplier loaderSupplier = createNiceMock(ClassLoaderSupplier.class);
        expect(loaderSupplier.getBootstrapClassLoader()).andReturn(bootstrapLoader);
        replay(provider, loaderSupplier);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);

        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderSupplier", loaderSupplier);
        try {
            assertEquals(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()),
                    multiSpi.findImplementationNames(Agent.class));
            verify(provider);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsFromProvidedClassloader() throws ClassNotFoundException {
        ClassLoader implementationLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, implementationLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(implementationLoader.loadClass(JamesBond.class.getName()))
        .andReturn((Class) JamesBond.class);
        replay(provider, implementationLoader);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        assertEquals(newHashSet(JamesBond.class), 
                multiSpi.findImplementations(Agent.class, implementationLoader));
        
        verify(implementationLoader);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsFromContextClassloaderByDefault() throws ClassNotFoundException {
        ClassLoader contextLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, contextLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(contextLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        replay(provider, contextLoader);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(contextLoader);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        try {
            assertEquals(newHashSet(JamesBond.class), multiSpi.findImplementations(Agent.class));
            verify(contextLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsFromSystemClassloaderForNullContextLoader() throws ClassNotFoundException, IllegalAccessException {
        ClassLoader systemLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, systemLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(systemLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        ClassLoaderSupplier loaderSupplier = createNiceMock(ClassLoaderSupplier.class);
        expect(loaderSupplier.getSystemClassLoader()).andReturn(systemLoader);
        replay(provider, systemLoader, loaderSupplier);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderSupplier", loaderSupplier);
        try {
            assertEquals(newHashSet(JamesBond.class), multiSpi.findImplementations(Agent.class));
            verify(systemLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }
    

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsFromBootstrapClassloaderForNullSystemLoader() throws ClassNotFoundException, IllegalAccessException {
        ClassLoader bootstrapLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, bootstrapLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(bootstrapLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        
        // will return null for other loaders        
        ClassLoaderSupplier loaderSupplier = createNiceMock(ClassLoaderSupplier.class);
        expect(loaderSupplier.getBootstrapClassLoader()).andReturn(bootstrapLoader);
        replay(provider, bootstrapLoader, loaderSupplier);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderSupplier", loaderSupplier);
        try {
            assertEquals(newHashSet(JamesBond.class), multiSpi.findImplementations(Agent.class));
            verify(bootstrapLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }
    
    @Test(expected = ClassCastException.class)
    public void verifiesThatFoundClassesImplementTheService() throws ClassNotFoundException {
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(eq(Agent.class), isA(ClassLoader.class)))
        .andReturn(newHashSet(String.class.getName()));
        replay(provider);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        multiSpi.findImplementations(Agent.class);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsForInstantiationFromProvidedClassloader() throws Exception {
        ClassLoader implementationLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, implementationLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(implementationLoader.loadClass(JamesBond.class.getName()))
        .andReturn((Class) JamesBond.class);
        replay(provider, implementationLoader);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        assertEquals(newHashSet(new JamesBond()), 
                multiSpi.loadImplementations(Agent.class, implementationLoader));
        
        verify(implementationLoader);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsForInstantiationFromContextClassloaderByDefault() throws Exception {
        ClassLoader contextLoader = createMock(ClassLoader.class); 
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, contextLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(contextLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        replay(provider, contextLoader);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(contextLoader);

        multiSpi = new MultiSpi(newHashSet(provider));
        try {
            assertEquals(newHashSet(new JamesBond()), multiSpi.loadImplementations(Agent.class));
            verify(contextLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsForInstantiationFromSystemClassloaderForNullContextLoader() throws Exception {
        ClassLoader systemLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, systemLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(systemLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        ClassLoaderSupplier loaderSupplier = createNiceMock(ClassLoaderSupplier.class);
        expect(loaderSupplier.getSystemClassLoader()).andReturn(systemLoader);
        replay(provider, systemLoader, loaderSupplier);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderSupplier", loaderSupplier);
        try {
            assertEquals(newHashSet(new JamesBond()), multiSpi.loadImplementations(Agent.class));
            verify(systemLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }
    

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsForInstantiationFromBootstrapClassloaderForNullSystemLoader() throws Exception {
        ClassLoader bootstrapLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, bootstrapLoader))
        .andReturn(newHashSet(JamesBond.class.getName()));
        // hack generics
        expect(bootstrapLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);

        // will return null for other loaders
        ClassLoaderSupplier loaderSupplier = createNiceMock(ClassLoaderSupplier.class);
        expect(loaderSupplier.getBootstrapClassLoader()).andReturn(bootstrapLoader);
        replay(provider, bootstrapLoader, loaderSupplier);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderSupplier", loaderSupplier);
        try {
            assertEquals(newHashSet(new JamesBond()), multiSpi.loadImplementations(Agent.class));
            verify(bootstrapLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }    
    
    @Test(expected = InstantiationException.class)
    public void convertsClassCastExceptionDuringImplementationLoading() throws Exception {
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(eq(Agent.class), isA(ClassLoader.class)))
        .andReturn(newHashSet(String.class.getName()));
        replay(provider);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        multiSpi.loadImplementations(Agent.class);        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = InstantiationException.class)
    public void throwsInstantiationExceptionOnMissingPublicNoargConstructor() throws Exception {
        ClassLoader implementationLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, implementationLoader))
        .andReturn(newHashSet(AgentWithoutNoargConstructor.class.getName()));
        // hack generics
        expect(implementationLoader.loadClass(AgentWithoutNoargConstructor.class.getName()))
        .andReturn((Class) AgentWithoutNoargConstructor.class);
        replay(provider, implementationLoader);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        multiSpi.loadImplementations(Agent.class, implementationLoader);
    }

    public static class AgentWithoutNoargConstructor implements Agent {
        public AgentWithoutNoargConstructor(Object arg) {}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = InstantiationException.class)
    public void convertsInvocationTargetExceptionsDuringInstantiation() throws Exception {
        ClassLoader implementationLoader = createMock(ClassLoader.class);
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class, implementationLoader))
        .andReturn(newHashSet(AgentWithExceptionThrowingConstructor.class.getName()));
        // hack generics
        expect(implementationLoader.loadClass(AgentWithExceptionThrowingConstructor.class.getName()))
        .andReturn((Class) AgentWithExceptionThrowingConstructor.class);
        replay(provider, implementationLoader);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        multiSpi.loadImplementations(Agent.class, implementationLoader);               
    }
    
    public static class AgentWithExceptionThrowingConstructor implements Agent {
        public AgentWithExceptionThrowingConstructor() {
            throw new UnsupportedOperationException();
        }
    }
}

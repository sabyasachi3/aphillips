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
import static org.easymock.EasyMock.expect;
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

import com.qrmedia.commons.multispi.MultiSpi.ClassLoaderLocator;
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
        ServiceImplementationProvider provider1 = createMock(ServiceImplementationProvider.class);
        expect(provider1.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName()));
        ServiceImplementationProvider provider2 = createMock(ServiceImplementationProvider.class);
        expect(provider2.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName(), StuartThomas.class.getName()));
        replay(provider1, provider2);
        
        multiSpi = new MultiSpi(newHashSet(provider1, provider2));
        assertEquals(newHashSet(JamesBond.class.getName(), JackGiddings.class.getName(), 
                    StuartThomas.class.getName()),
                multiSpi.findImplementationNames(Agent.class));
        verify(provider1, provider2);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsFromProvidedClassloader() throws ClassNotFoundException {
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader implementationLoader = createMock(ClassLoader.class);
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
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader contextLoader = createMock(ClassLoader.class);
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
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader systemLoader = createMock(ClassLoader.class);
        // hack generics
        expect(systemLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        ClassLoaderLocator loaderLocator = createNiceMock(ClassLoaderLocator.class);
        expect(loaderLocator.getSystemClassLoader()).andReturn(systemLoader);
        replay(provider, systemLoader, loaderLocator);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderLocator", loaderLocator);
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
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader bootstrapLoader = createMock(ClassLoader.class);
        // hack generics
        expect(bootstrapLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        
        // will return null for other loaders        
        ClassLoaderLocator loaderLocator = createNiceMock(ClassLoaderLocator.class);
        expect(loaderLocator.getBootstrapClassLoader()).andReturn(bootstrapLoader);
        replay(provider, bootstrapLoader, loaderLocator);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderLocator", loaderLocator);
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
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(String.class.getName()));
        replay(provider);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        multiSpi.findImplementations(Agent.class);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void loadsImplementationsForInstantiationFromProvidedClassloader() throws Exception {
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader implementationLoader = createMock(ClassLoader.class);
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
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader contextLoader = createMock(ClassLoader.class);
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
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader systemLoader = createMock(ClassLoader.class);
        // hack generics
        expect(systemLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);
        ClassLoaderLocator loaderLocator = createNiceMock(ClassLoaderLocator.class);
        expect(loaderLocator.getSystemClassLoader()).andReturn(systemLoader);
        replay(provider, systemLoader, loaderLocator);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderLocator", loaderLocator);
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
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(JamesBond.class.getName()));
        ClassLoader bootstrapLoader = createMock(ClassLoader.class);
        // hack generics
        expect(bootstrapLoader.loadClass(JamesBond.class.getName())).andReturn((Class) JamesBond.class);

        // will return null for other loaders
        ClassLoaderLocator loaderLocator = createNiceMock(ClassLoaderLocator.class);
        expect(loaderLocator.getBootstrapClassLoader()).andReturn(bootstrapLoader);
        replay(provider, bootstrapLoader, loaderLocator);
        
        final ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        ReflectionUtils.setValue(multiSpi, "loaderLocator", loaderLocator);
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
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(String.class.getName()));
        replay(provider);
        
        multiSpi = new MultiSpi(newHashSet(provider));
        multiSpi.loadImplementations(Agent.class);        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = InstantiationException.class)
    public void throwsInstantiationExceptionOnMissingPublicNoargConstructor() throws Exception {
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(AgentWithoutNoargConstructor.class.getName()));
        ClassLoader implementationLoader = createMock(ClassLoader.class);
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
        ServiceImplementationProvider provider = createMock(ServiceImplementationProvider.class);
        expect(provider.findServiceImplementations(Agent.class))
        .andReturn(newHashSet(AgentWithExceptionThrowingConstructor.class.getName()));
        ClassLoader implementationLoader = createMock(ClassLoader.class);
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

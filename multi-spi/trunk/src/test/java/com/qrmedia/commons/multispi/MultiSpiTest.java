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
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.gov.mi6.Agent;
import uk.gov.mi6.agent.JackGiddings;
import uk.gov.mi6.agent.JamesBond;
import uk.gov.mi6.agent.StuartThomas;

import com.qrmedia.commons.multispi.provider.ServiceImplementationProvider;

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
}

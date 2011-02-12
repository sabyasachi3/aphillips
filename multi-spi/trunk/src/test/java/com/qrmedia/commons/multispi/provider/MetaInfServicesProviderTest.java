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

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;

import java.util.ServiceLoader;
import java.util.Set;

import org.junit.Test;

import uk.gov.mi6.Agent;
import uk.gov.mi6.agent.Bill;
import uk.gov.mi6.agent.JamesBond;
import uk.gov.mi6.agent.StuartThomas;

import com.google.common.base.Function;

/**
 * Unit tests for the {@link MetaInfServicesProvider}.
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
public class MetaInfServicesProviderTest {
    private final MetaInfServicesProvider provider = new MetaInfServicesProvider();
    
    @Test
    public void readsAllServicesFilesOnClasspathAndIgnoresComments() {
        assertEquals(newHashSet(JamesBond.class.getName(), StuartThomas.class.getName(),
                Bill.class.getName()), 
                provider.findServiceImplementations(Agent.class));
    }
    
    @Test
    public void matchesServiceLoader() {
        Set<String> loadedServiceNames = newHashSet(transform(ServiceLoader.load(Agent.class), 
                new Function<Agent, String>() {
                    public String apply(Agent from) {
                        return from.getClass().getName();
                    }
                }));
        assertEquals(loadedServiceNames, provider.findServiceImplementations(Agent.class));
    }
    
}

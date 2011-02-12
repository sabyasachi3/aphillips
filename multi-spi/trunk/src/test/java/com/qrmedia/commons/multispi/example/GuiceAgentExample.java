/*
 * @(#)StubItest.java     8 Apr 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.qrmedia.commons.multispi.example;

import static com.google.common.collect.Iterables.get;

import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Test;

import uk.gov.mi6.Agent;
import uk.gov.mi6.LicenseToKill;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.qrmedia.commons.multispi.MultiSpi;
import com.qrmedia.commons.multispi.config.MultiSpiBuilder;

/**
 * Using {@link MultiSpi} to create Guice providers.
 * 
 * @author anphilli
 * @since 8 Apr 2009
 *
 */
public class GuiceAgentExample extends AbstractModule {

    @Test
    public void go() {
        Injector injector = Guice.createInjector(this);
        
        Set<?> agentNames = injector.getInstance(Key.get(new TypeLiteral<Set<String>>() {},
                Names.named("agentNames")));
        System.out.format("Agent names: %s (class of items in set: '%s')%n", agentNames, 
                get(agentNames, 0).getClass());
        
        Set<?> agentClasses = injector.getInstance(Key.get(new TypeLiteral<Set<Class<? extends Agent>>>() {}));
        System.out.format("Agent classes: %s (class of items in set: '%s')%n", agentClasses, 
                get(agentClasses, 0).getClass());
        
        Set<?> agents = injector.getInstance(Key.get(new TypeLiteral<Set<Agent>>() {}));
        System.out.format("Agents: %s%n", agents);
    }
    
    @Override
    protected void configure() {
        // using @Provides
    }
    
    @Provides
    @Singleton
    @Named("agentNames")
    Set<String> provideAgentNames(MultiSpi multiSpi) {
        return multiSpi.findImplementationNames(Agent.class);
    }

    @Provides
    @Singleton
    Set<Class<? extends Agent>> provideAgentClasses(MultiSpi multiSpi) throws ClassCastException, ClassNotFoundException {
        return multiSpi.findImplementations(Agent.class);
    }
    
    @Provides
    @Singleton
    Set<Agent> provideAgents(MultiSpi multiSpi) throws ClassNotFoundException, InstantiationException {
        return multiSpi.loadImplementations(Agent.class);
    }
    
    @SuppressWarnings("unused")
    @Provides
    @Singleton
    private MultiSpi provideMultiSpi() {
        return new MultiSpiBuilder().withMetaInfServicesScanning()
        .withAnnotationScanning(LicenseToKill.class, "uk.gov").build();
    }
}

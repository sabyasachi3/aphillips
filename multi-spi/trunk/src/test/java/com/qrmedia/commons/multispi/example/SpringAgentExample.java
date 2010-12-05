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

import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.qrmedia.commons.multispi.MultiSpi;

/**
 * Using {@link MultiSpi} to create a Spring-wireable set.
 * 
 * @author anphilli
 * @since 8 Apr 2009
 *
 */
public class SpringAgentExample {
    
    @Test
    public void go() {
        Set<?> agents = (Set<?>) new XmlBeanFactory(new ClassPathResource("agentContext.xml"))
                        .getBean("agents");
        System.out.format("Agents: %s (class of items in set: '%s')%n", agents, 
                get(agents, 0).getClass());
    }
}

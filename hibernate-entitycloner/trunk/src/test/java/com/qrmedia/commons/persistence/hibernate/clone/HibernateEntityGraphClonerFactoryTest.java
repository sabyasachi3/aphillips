/*
 * @(#)HibernateEntityGraphClonerFactoryTest.java     23 Nov 2008
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
package com.qrmedia.commons.persistence.hibernate.clone;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qrmedia.commons.graph.traverser.NodeVisitor;
import com.qrmedia.commons.reflect.ReflectionUtils;

/**
 * Unit tests for the {@link HibernateEntityGraphClonerFactory}.
 * <p>
 * These shouldn't really be necessary, as they're effectively only testing
 * Spring's ability to do what it promises in <i>3.3.8.1. Lookup method injection</i> 
 * in the Spring reference.
 * 
 * @author anph
 * @since 23 Nov 2008
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:example-applicationContext.xml" })
public class HibernateEntityGraphClonerFactoryTest {
    private HibernateEntityGraphClonerFactory entityGraphClonerFactory;
    
    @Test
    public void newInstance() throws IllegalAccessException {
        HibernateEntityGraphCloner entityGraphCloner1 = entityGraphClonerFactory.newInstance();
        HibernateEntityGraphCloner entityGraphCloner2 = entityGraphClonerFactory.newInstance();
        
        assertNotSame(entityGraphCloner1, entityGraphCloner2);
        
        // check that the injected dependencies are singletons
        List<NodeVisitor<Object, Map<Object, Object>>> entityGraphCloner1Visitors =
            ReflectionUtils.getValue(entityGraphCloner1, "nodeVisitors");
        List<NodeVisitor<Object, Map<Object, Object>>> entityGraphCloner2Visitors =
            ReflectionUtils.getValue(entityGraphCloner2, "nodeVisitors");        
        assertSame(entityGraphCloner1Visitors.get(0), entityGraphCloner2Visitors.get(0));
    }

    /**
     * @param entityGraphClonerFactory the entityGraphClonerFactory to set
     */
    @Autowired
    public void setEntityGraphClonerFactory(
            HibernateEntityGraphClonerFactory entityGraphClonerFactory) {
        this.entityGraphClonerFactory = entityGraphClonerFactory;
    }

}

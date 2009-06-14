/*
 * @(#)AbstractValueAwareCollectionClonerTest.java     10 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.property;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.bag.HashBag;
import org.hibernate.util.IdentitySet;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.collections.SetUtils;
import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;
import com.qrmedia.commons.persistence.hibernate.clone.property.AbstractValueAwareCollectionCloner;

/**
 * Unit tests for the {@link AbstractValueAwareCollectionCloner}.
 * 
 * @author anph
 * @since 10 Feb 2009
 *
 */
public class AbstractValueAwareCollectionClonerTest {
    private AbstractValueAwareCollectionCloner collectionCloner;
    
    @Before
    public void prepareFixture() throws Exception {
        collectionCloner = createMock(AbstractValueAwareCollectionCloner.class,
                AbstractValueAwareCollectionCloner.class.getDeclaredMethod("cloneCollection", 
                        Object.class, Object.class, String.class, Collection.class,
                        HibernateEntityGraphCloner.class));        
    }
    
    @Test
    public void clone_wrongPropertyType() {
        
        // wrong property types should be ignored, i.e. no calls to mocks
        assertFalse(collectionCloner.cloneValue(null, null, null, "James Bond", null));
    }    
    
    @Test
    public void clone_wrongCollectionType() {
        StubHibernateEntity source = new StubHibernateEntity();
        StubHibernateEntity target = new StubHibernateEntity();
        
        Collection<String> originalSimpleCollectionBeanProperty = new ArrayList<String>();
        target.setSimpleCollectionBeanProperty(originalSimpleCollectionBeanProperty);
        
        String propertyName = "simpleCollectionBeanProperty";
        String member = "James Bond";
        
        Collection<String> value = new ArrayList<String>(1);
        value.add(member);
        
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        collectionCloner.cloneCollection(same(source), same(target), same(propertyName), 
                same(value), same(entityGraphCloner));
        expectLastCall().andReturn(null);        
        replay(collectionCloner);
        
        assertFalse(collectionCloner.cloneValue(source, target, propertyName, value, 
                    entityGraphCloner));
        
        verify(collectionCloner);
        
        // the collection was not processed, so the target should be unchanged!
        assertSame(originalSimpleCollectionBeanProperty, 
                   target.getSimpleCollectionBeanProperty());
    }        
    
    @Test
    public void cloneTest() {
        StubHibernateEntity source = new StubHibernateEntity();
        StubHibernateEntity target = new StubHibernateEntity();
        String propertyName = "simpleCollectionBeanProperty";
        String member = "James Bond";
        Collection<String> value = new ArrayList<String>(1);
        value.add(member);
        Collection<String> valueClone = new ArrayList<String>();
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        collectionCloner.cloneCollection(same(source), same(target), same(propertyName), 
                same(value), same(entityGraphCloner));
        expectLastCall().andReturn(valueClone);
        replay(collectionCloner);
        
        assertTrue(collectionCloner.cloneValue(source, target, propertyName, value, 
                   entityGraphCloner));
        
        verify(collectionCloner);
        
        // if the collection was processed, it should be linked to the target
        assertSame(valueClone, target.getSimpleCollectionBeanProperty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void newCollectionInterfaceInstance_unsupported() {
        AbstractValueAwareCollectionCloner.newCollectionInterfaceInstance(new HashBag());
    }
    
    @Test
    public void newCollectionInterfaceInstance_list() {
        List<String> list = Arrays.asList("007");
        List<String> newInstance = AbstractValueAwareCollectionCloner
                .newCollectionInterfaceInstance(list);
        
        assertNotSame(newInstance, list);
        assertTrue(newInstance.isEmpty());
    }
    
    @Test
    public void newCollectionInterfaceInstance_set() {
        Set<String> set = SetUtils.asSet("007");
        Set<String> newInstance = AbstractValueAwareCollectionCloner
                .newCollectionInterfaceInstance(set);
        
        assertNotSame(newInstance, set);
        assertTrue(newInstance.isEmpty());
        
        // *must* be an eqauls-based set
        assertFalse(newInstance instanceof IdentitySet);
    }    
    
    
}

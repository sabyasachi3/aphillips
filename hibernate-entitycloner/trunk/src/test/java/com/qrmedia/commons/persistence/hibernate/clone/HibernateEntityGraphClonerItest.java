/*
 * @(#)HibernateEntityGraphClonerWorkflowItest.java     8 Feb 2009
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

import static org.junit.Assert.*;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.naming.CompositeName;
import javax.naming.Name;
import javax.persistence.Id;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qrmedia.commons.collections.SetUtils;

/**
 * Integration test(s) for the {@link HibernateEntityGraphCloner}.
 *  
 * @author anph
 * @since 8 Feb 2009
 * @see HibernateEntityGraphClonerWorkflowItest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:example-applicationContext.xml" })
public class HibernateEntityGraphClonerItest {
    private HibernateEntityGraphCloner entityGraphCloner;

    @Test
    public void cloneTest() {
        String staticProperty = "Bond...James Bond";
        StubHibernateEntity.setStaticProperty(staticProperty);
        
        String simpleBeanProperty = "James Bond";
        State enumBeanProperty = State.NEW;
        
        // choose an attribute value that is cloneable
        Name clonableBeanProperty = new CompositeName();
        StubHibernateEntity relatedEntity1 = new StubHibernateEntity();
        
        Collection<String> simpleCollectionBeanProperty = new LinkedList<String>();
        String simpleCollectionBeanPropertyMember = "MI6";
        simpleCollectionBeanProperty.add(simpleCollectionBeanPropertyMember);
        
        Collection<StubHibernateEntity> nonSimpleCollectionBeanProperty = 
            new TreeSet<StubHibernateEntity>();
        StubHibernateEntity relatedEntity2 = new StubHibernateEntity();
        nonSimpleCollectionBeanProperty.add(relatedEntity2);
        
        StubHibernateEntity entity = new StubHibernateEntity(7L, 7, "007", 
                simpleBeanProperty, enumBeanProperty, clonableBeanProperty, 
                relatedEntity1, simpleCollectionBeanProperty, 
                nonSimpleCollectionBeanProperty);
        
        /*
         * Deliberately cast to Object to check if the correct clone method is
         * resolved - Collection<Object> also matches Object! 
         */
        Map<Object, Object> entityClones = 
            entityGraphCloner.clone(Arrays.asList((Object) entity, relatedEntity2));
        
        assertTrue(entityClones.size() == 2);
        assertEquals(SetUtils.asSet(entity, relatedEntity2), entityClones.keySet());
        
        StubHibernateEntity entityClone = (StubHibernateEntity) entityClones.get(entity);
        assertNotSame(entity, entityClone);
        
        StubHibernateEntity relatedEntity2Clone = 
            (StubHibernateEntity) entityClones.get(relatedEntity2);
        assertNotSame(relatedEntity2, relatedEntity2Clone);
        
        // check the properties of the entity
        assertSame(staticProperty, StubHibernateEntity.getStaticProperty());
        assertNull(entityClone.getId());
        assertTrue(entityClone.getVersion() == 0);
        assertNull(entityClone.getNonBeanProperty());
        assertSame(simpleBeanProperty, entityClone.getSimpleBeanProperty());
        assertSame(enumBeanProperty, entityClone.getEnumBeanProperty());
        
        assertNotSame(clonableBeanProperty, entityClone.getCloneableBeanProperty());
        assertEquals(clonableBeanProperty, entityClone.getCloneableBeanProperty());
        
        StubHibernateEntity relatedEntity1Clone = entityClone.getNonSimpleBeanProperty();
        assertNotSame(relatedEntity1, relatedEntity1Clone);
        
        Collection<String> simpleCollectionBeanPropertyClone = 
            entityClone.getSimpleCollectionBeanProperty();
        assertNotSame(simpleCollectionBeanProperty, simpleCollectionBeanPropertyClone);
        assertEquals(Arrays.asList(simpleCollectionBeanPropertyMember),
                     simpleCollectionBeanPropertyClone);
        
        Collection<StubHibernateEntity> nonSimpleCollectionBeanPropertyClone =
                entityClone.getNonSimpleCollectionBeanProperty();
        assertNotSame(nonSimpleCollectionBeanProperty, nonSimpleCollectionBeanPropertyClone);
        
        /*
         * Because StubHibernateEntity objects use reference equality, this will only
         * succeed if the object in the cloned set is the *same* as the other clone.
         */
        assertEquals(SetUtils.asSet(relatedEntity2Clone), 
                     nonSimpleCollectionBeanPropertyClone);
    }
    
    /**
     * Ensures the {@link Id @Id} properties are correctly preserved, if requested.
     */
    @Test
    public void clone_preserveId() {
        StubHibernateEntity entity = new StubHibernateEntity();
        
        Long id = 7L;
        entity.setId(id);
        
        /*
         * Deliberately cast to Object to check if the correct clone method is
         * resolved - Collection<Object> also matches Object! 
         */
        StubHibernateEntity entityClone = entityGraphCloner.clone(entity, true);
        assertNotSame(entity, entityClone);
        
        // check the ID property of the clone
        assertEquals(id, entityClone.getId());
        
        /* cloning of other properties is checked in cloneTest above */
    }    
    
    @Test
    public void clone_equalNotSame() {
        String property = "007";
        SimplePropertyEqualStubHibernateEntity entity1 = 
            new SimplePropertyEqualStubHibernateEntity(property);
        
        SimplePropertyEqualStubHibernateEntity entity2 = 
            new SimplePropertyEqualStubHibernateEntity(property);
        entity1.setNonSimpleBeanProperty(entity2);
        
        SimplePropertyEqualStubHibernateEntity entity3 = 
            new SimplePropertyEqualStubHibernateEntity(property);        
        
        Map<SimplePropertyEqualStubHibernateEntity, SimplePropertyEqualStubHibernateEntity> entityClones =
                entityGraphCloner.clone(Arrays.asList(entity1, entity3));
        SimplePropertyEqualStubHibernateEntity entity1Clone = entityClones.get(entity1); 
        
        // all entities should have been cloned, and they should all be distinct
        assertNotSame(entity1, entity1Clone);
        assertEquals(entity1, entity1Clone);
        
        SimplePropertyEqualStubHibernateEntity entity2Clone = 
            (SimplePropertyEqualStubHibernateEntity) entity1Clone.getNonSimpleBeanProperty(); 
        assertNotSame(entity2, entity2Clone);
        assertEquals(entity2, entity2Clone);
        
        SimplePropertyEqualStubHibernateEntity entity3Clone = entityClones.get(entity3);
        assertNotSame(entity3, entity3Clone);
        assertEquals(entity3, entity3Clone);        
        
        assertNotSame(entity1Clone, entity2Clone);
        assertNotSame(entity1Clone, entity3Clone);
        assertNotSame(entity2Clone, entity3Clone);
        
        // if these succeed, then also entity2Clone.equals(entity3Clone)
        assertEquals(entity1Clone, entity2Clone);
        assertEquals(entity1Clone, entity3Clone);
    }      

    @SuppressWarnings("unchecked")
    @Test
    public void clone_temporarilyEqual() {
        SimplePropertyEqualStubHibernateEntity entity1 =
            new SimplePropertyEqualStubHibernateEntity("007");
        NonSimplePropertyEqualStubHibernateEntity temporarilyEqualEntity1 = 
            new NonSimplePropertyEqualStubHibernateEntity(entity1);
        
        StubHibernateEntity entity2 = new StubHibernateEntity();
        
        // a set is required because a list or collection may contain duplicates
        entity2.setNonSimpleCollectionBeanProperty(new HashSet<StubHibernateEntity>());
        entity2.getNonSimpleCollectionBeanProperty().add(temporarilyEqualEntity1);
        
        SimplePropertyEqualStubHibernateEntity entity3 =
            new SimplePropertyEqualStubHibernateEntity("008");
        
        /*
         * The important thing here is that the clones of the two "temporarilyEqual" 
         * entities will be *equal* until their non-simple properties (which are
         * different) are also cloned and wired up.
         * In other words, during a certain period in the wiring up phase, the
         * two entities will be equal and *cannot both be added to a set*.
         * The cloner must somehow ensure that they are only added to the set once
         * they have been completely cloned, and are no longer equal.  
         */
        NonSimplePropertyEqualStubHibernateEntity temporarilyEqualEntity2 = 
            new NonSimplePropertyEqualStubHibernateEntity(entity3);
        entity2.getNonSimpleCollectionBeanProperty().add(temporarilyEqualEntity2);
        
        StubHibernateEntity entity2Clone = entityGraphCloner.clone(entity2);
        
        // all entities should have been cloned, and they should all be distinct
        assertNotSame(entity2, entity2Clone);
        
        // sort to ensure the order of the cloned entities is deterministic
        List<StubHibernateEntity> nonSimpleCollectionBeanPropertyClone =
                new ArrayList<StubHibernateEntity>(entity2Clone.getNonSimpleCollectionBeanProperty());
        Collections.<StubHibernateEntity>sort(nonSimpleCollectionBeanPropertyClone, 
                new PropertyComparator("nonSimpleBeanProperty.simpleBeanProperty", false, true));

        assertEquals(2, nonSimpleCollectionBeanPropertyClone.size());
        
        StubHibernateEntity temporarilyEqualEntity1Clone = 
            nonSimpleCollectionBeanPropertyClone.get(0);
        assertNotSame(temporarilyEqualEntity1, temporarilyEqualEntity1Clone);
        assertEquals(temporarilyEqualEntity1, temporarilyEqualEntity1Clone);
        
        StubHibernateEntity temporarilyEqualEntity2Clone = 
            nonSimpleCollectionBeanPropertyClone.get(1);
        assertNotSame(temporarilyEqualEntity2, temporarilyEqualEntity2Clone);
        assertEquals(temporarilyEqualEntity2, temporarilyEqualEntity2Clone);

        assertNotSame(temporarilyEqualEntity1Clone, temporarilyEqualEntity2Clone);
    }      
    
    @Test
    public void clone_orderedCollection() {
        SimplePropertyEqualStubHibernateEntity entity1 =
            new SimplePropertyEqualStubHibernateEntity("007");
        SimplePropertyEqualStubHibernateEntity entity2 =
            new SimplePropertyEqualStubHibernateEntity("008");
        
        StubHibernateEntity entity3 = new StubHibernateEntity();
        
        // use an *ordered* collection
        entity3.setNonSimpleCollectionBeanProperty(
                Arrays.<StubHibernateEntity>asList(entity1, entity2));
        
        StubHibernateEntity entity3Clone = entityGraphCloner.clone(entity3);
        
        // all entities should have been cloned, and they should all be distinct
        assertNotSame(entity3, entity3Clone);
        
        List<StubHibernateEntity> nonSimpleCollectionBeanPropertyClone =
                (List<StubHibernateEntity>) entity3Clone.getNonSimpleCollectionBeanProperty();
        assertEquals(Arrays.<StubHibernateEntity>asList(entity1, entity2),
                nonSimpleCollectionBeanPropertyClone);
        
        StubHibernateEntity entity1Clone = nonSimpleCollectionBeanPropertyClone.get(0);
        assertNotSame(entity1, entity1Clone);
        assertEquals(entity1, entity1Clone);
        
        StubHibernateEntity entity2Clone = nonSimpleCollectionBeanPropertyClone.get(1);
        assertNotSame(entity2, entity2Clone);
        assertEquals(entity2, entity2Clone);
    }
    
    /**
     * @param entityGraphClonerFactory the entityGraphClonerFactory to set
     */
    @Autowired
    public void setEntityGraphClonerFactory(
            HibernateEntityGraphClonerFactory entityGraphClonerFactory) {
        entityGraphCloner = entityGraphClonerFactory.newInstance();
    }
    
}

/*
 * @(#)HibernateEntityBeanClonerTest.java     8 Feb 2009
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

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.IdentityHashMap;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.graph.traverser.BreadthFirstGraphTraverser;
import com.qrmedia.commons.persistence.hibernate.clone.EntityPreserveIdFlagPair;
import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityBeanCloner;
import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.property.BeanPropertyCloner;

/**
 * Unit tests for the {@link HibernateEntityBeanCloner}.
 * 
 * @author anph
 * @since 8 Feb 2009
 *
 */
public class HibernateEntityBeanClonerTest {
    private HibernateEntityBeanCloner entityBeanCloner = new HibernateEntityBeanCloner();
    
    private BeanPropertyCloner simplePropertyCloner = createMock(BeanPropertyCloner.class); 
    private BeanPropertyCloner catchallPropertyCloner = createMock(BeanPropertyCloner.class);
    
    @Before
    public void prepareFixture() {
        entityBeanCloner.setPropertyCloners(
                Arrays.asList(simplePropertyCloner, catchallPropertyCloner));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void visitNode_wrongInitializationProcessor() {
        entityBeanCloner.visitNode(null, new BreadthFirstGraphTraverser<EntityPreserveIdFlagPair, IdentityHashMap<Object, Object>>(), 
                                   null);
    }
    
    @Test
    public void visitNode() {
        StubHibernateEntity entity = new StubHibernateEntity();
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        
        // expect calls for all non-static properties with valid getters and setters
        Capture<StubHibernateEntity> entityCloneCapture1 = new Capture<StubHibernateEntity>(); 
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture1), 
                eq("simpleBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(true);

        Capture<StubHibernateEntity> entityCloneCapture2 = new Capture<StubHibernateEntity>(); 
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture2), 
                eq("enumBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(true);

        Capture<StubHibernateEntity> entityCloneCapture3 = new Capture<StubHibernateEntity>(); 
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture3), 
                eq("cloneableBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(false);
        catchallPropertyCloner.clone(same(entity), capture(entityCloneCapture3), 
                eq("cloneableBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(true);
        
        Capture<StubHibernateEntity> entityCloneCapture4 = new Capture<StubHibernateEntity>();
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture4), 
                eq("nonSimpleBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(false);
        catchallPropertyCloner.clone(same(entity), capture(entityCloneCapture4), 
                eq("nonSimpleBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(true);
        
        Capture<StubHibernateEntity> entityCloneCapture5 = new Capture<StubHibernateEntity>();
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture5), 
                eq("simpleCollectionBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(true);
        
        Capture<StubHibernateEntity> entityCloneCapture6 = new Capture<StubHibernateEntity>();
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture6), 
                eq("nonSimpleCollectionBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(false);
        catchallPropertyCloner.clone(same(entity), capture(entityCloneCapture6), 
                eq("nonSimpleCollectionBeanProperty"), same(entityGraphCloner));
        expectLastCall().andReturn(true);
        replay(simplePropertyCloner, catchallPropertyCloner);
        
        IdentityHashMap<Object, Object> entityClones = new IdentityHashMap<Object, Object>();
        entityBeanCloner.visitNode(new EntityPreserveIdFlagPair(entity, false), entityGraphCloner,
                                   entityClones);
        
        verify(simplePropertyCloner, catchallPropertyCloner);
        
        assertTrue(entityClones.size() == 1);
        assertTrue(entityClones.containsKey(entity));
        
        StubHibernateEntity entityClone = (StubHibernateEntity) entityClones.get(entity);
        assertSame(entityClone, entityCloneCapture1.getValue());
        assertSame(entityClone, entityCloneCapture2.getValue());
        assertSame(entityClone, entityCloneCapture3.getValue());
        assertSame(entityClone, entityCloneCapture4.getValue());
        assertSame(entityClone, entityCloneCapture5.getValue());
        assertSame(entityClone, entityCloneCapture6.getValue());
    }
    
    @Test
    public void visitNode_preserveId() {
        StubHibernateEntity entity = new StubHibernateEntity();
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        
        // expect calls also for the @Id property
        Capture<StubHibernateEntity> entityCloneCapture1 = new Capture<StubHibernateEntity>(); 
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture1), 
                eq("id"), same(entityGraphCloner));
        expectLastCall().andReturn(true);
        
        // calls for the other properties (all called *Property) are not checked
        simplePropertyCloner.clone(same(entity), isA(StubHibernateEntity.class), 
                matches(".*Property"), same(entityGraphCloner));
        expectLastCall().andReturn(true).times(6);
        replay(simplePropertyCloner);
        
        IdentityHashMap<Object, Object> entityClones = new IdentityHashMap<Object, Object>();
        entityBeanCloner.visitNode(new EntityPreserveIdFlagPair(entity, true), entityGraphCloner, 
                                   entityClones);
        
        verify(simplePropertyCloner);
        
        assertEquals(1, entityClones.size());
        assertTrue("Expected the entity to be a member of " + entityClones,
                   entityClones.containsKey(entity));
        
        assertSame(entityClones.get(entity),  entityCloneCapture1.getValue());
    }   
    
    /**
     * Ensures that if property (rather than field) access is used for JPA annotations,
     * the ID is still correctly ignored.
     */
    @Test
    public void visitNode_propertyAccess() {
        StubPropertyAccessHibernateEntity entity = new StubPropertyAccessHibernateEntity(7L, 7, "007");
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        
        // expect a call *only* for the simple bean property
        Capture<StubHibernateEntity> entityCloneCapture1 = new Capture<StubHibernateEntity>(); 
        expect(simplePropertyCloner.clone(same(entity), capture(entityCloneCapture1), 
                                          eq("simpleBeanProperty"), same(entityGraphCloner)))
        .andReturn(true);
        replay(simplePropertyCloner);
        
        IdentityHashMap<Object, Object> entityClones = new IdentityHashMap<Object, Object>();
        entityBeanCloner.visitNode(new EntityPreserveIdFlagPair(entity, false), entityGraphCloner, 
                                   entityClones);
        
        verify(simplePropertyCloner);
        
        assertEquals(1, entityClones.size());
        assertTrue("Expected the entity to be a member of " + entityClones,
                   entityClones.containsKey(entity));
        
        assertSame(entityClones.get(entity),  entityCloneCapture1.getValue());
    }     
    
    /**
     * Ensures that the preserveId flag also works if property access is used.
     */    
    @Test
    public void visitNode_propertyAccess_preserveId() {
        StubPropertyAccessHibernateEntity entity = new StubPropertyAccessHibernateEntity(7L, 7, "007");
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        
        // expect a call *only* for the simple bean property
        Capture<StubHibernateEntity> entityCloneCapture1 = new Capture<StubHibernateEntity>(); 
        expect(simplePropertyCloner.clone(same(entity), capture(entityCloneCapture1), 
                                          eq("simpleBeanProperty"), same(entityGraphCloner)))
        .andReturn(true);
        
        // expect calls also for the @Id property
        Capture<StubHibernateEntity> entityCloneCapture2 = new Capture<StubHibernateEntity>(); 
        expect(simplePropertyCloner.clone(same(entity), capture(entityCloneCapture2), 
                                          eq("id"), same(entityGraphCloner)))
        .andReturn(true);
        replay(simplePropertyCloner);
        
        IdentityHashMap<Object, Object> entityClones = new IdentityHashMap<Object, Object>();
        entityBeanCloner.visitNode(new EntityPreserveIdFlagPair(entity, true), entityGraphCloner, 
                                   entityClones);
        
        verify(simplePropertyCloner);
        
        assertEquals(1, entityClones.size());
        assertTrue("Expected the entity to be a member of " + entityClones,
                   entityClones.containsKey(entity));
        
        assertSame(entityClones.get(entity),  entityCloneCapture1.getValue());
        assertSame(entityClones.get(entity),  entityCloneCapture2.getValue());        
    }      
    
    // "pretend" to be a CGLIB class 
    private static class StubHibernateEntity$$EnhancerByCGLIB$$ extends StubHibernateEntity { }
    
    private static class ChildStubHibernateEntity$$EnhancerByCGLIB$$ 
        extends StubHibernateEntity$$EnhancerByCGLIB$$ { }
    
    /**
     * Checks that, if the entity is a &quot;magic&quot; CGLIB class, the
     * clone is in fact an instance of the <i>super</i>class.
     */
    @Test
    public void visitNode_cglibClass() {
        StubHibernateEntity entity = new ChildStubHibernateEntity$$EnhancerByCGLIB$$();
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        
        // expect calls also for the @Id property
        Capture<StubHibernateEntity> entityCloneCapture1 = new Capture<StubHibernateEntity>(); 
        simplePropertyCloner.clone(same(entity), capture(entityCloneCapture1), 
                eq("id"), same(entityGraphCloner));
        expectLastCall().andReturn(true);
        
        // calls for the other properties (all called *Property) are not checked
        simplePropertyCloner.clone(same(entity), isA(StubHibernateEntity.class), 
                matches(".*Property"), same(entityGraphCloner));
        expectLastCall().andReturn(true).times(6);
        replay(simplePropertyCloner);
        
        IdentityHashMap<Object, Object> entityClones = new IdentityHashMap<Object, Object>();
        entityBeanCloner.visitNode(new EntityPreserveIdFlagPair(entity, true), entityGraphCloner, 
                                   entityClones);
        
        verify(simplePropertyCloner);
        
        assertEquals(1, entityClones.size());
        assertTrue("Expected the entity to be a member of " + entityClones,
                   entityClones.containsKey(entity));
        
        Object entityClone = entityClones.get(entity);
        assertSame(entityClone,  entityCloneCapture1.getValue());
        
        // ensure the clone is *not* the CGLIB class, but the "real" class
        assertEquals(StubHibernateEntity.class, entityClone.getClass());
    }       
    
}

/*
 * @(#)HibernateEntityGraphClonerTest.java     8 Feb 2009
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
import static org.junit.Assert.fail;

import java.rmi.AccessException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.collections.MapUtils;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.GraphPostProcessingCommand;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.GraphWiringCommand;
import com.qrmedia.commons.reflect.ReflectionUtils;


/**
 * Unit tests for the {@link HibernateEntityGraphCloner}.
 * 
 * @author anph
 * @since 8 Feb 2009
 *
 */
public class HibernateEntityGraphClonerTest {
    private HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
    
    private HibernateEntityBeanCloner entityBeanCloner = createMock(HibernateEntityBeanCloner.class);
    
    @Before
    public void prepareFixture() {
        entityGraphCloner.setEntityBeanCloner(entityBeanCloner);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void clone_entity() throws AccessException {
        final StubHibernateEntity entity = new StubHibernateEntity();
        final StubHibernateEntity clone = new StubHibernateEntity();
        entityBeanCloner.visitNode(eq(new EntityPreserveIdFlagPair(entity, false)), 
                                   same(entityGraphCloner), 
                                   (IdentityHashMap<Object, Object>) anyObject());
        expectLastCall().andAnswer(new HibernateEntityBeanClonerActions(entity, clone));
        replay(entityBeanCloner);
        
        assertSame(clone, entityGraphCloner.clone(entity));
        
        verify(entityBeanCloner);
        
        // check that any internal state maintained during the cloning has been cleaned up
        assertTrue(ReflectionUtils.<List<?>>getValue(entityGraphCloner, "graphWiringCommands")
                   .isEmpty());
        assertTrue(ReflectionUtils.<List<?>>getValue(entityGraphCloner, "graphPostProcessingCommands")
                   .isEmpty());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void clone_entity_preserveId() throws AccessException {
        final StubHibernateEntity entity = new StubHibernateEntity();
        final StubHibernateEntity clone = new StubHibernateEntity();
        entityBeanCloner.visitNode(eq(new EntityPreserveIdFlagPair(entity, true)), 
                                   same(entityGraphCloner), 
                                   (IdentityHashMap<Object, Object>) anyObject());
        expectLastCall().andAnswer(new HibernateEntityBeanClonerActions(entity, clone));
        replay(entityBeanCloner);
        
        assertSame(clone, entityGraphCloner.clone(entity, true));
        
        verify(entityBeanCloner);
        
        // check that any internal state maintained during the cloning has been cleaned up
        assertTrue(ReflectionUtils.<List<?>>getValue(entityGraphCloner, "graphWiringCommands")
                   .isEmpty());
        assertTrue(ReflectionUtils.<List<?>>getValue(entityGraphCloner, "graphPostProcessingCommands")
                   .isEmpty());
    }    

    private class HibernateEntityBeanClonerActions implements IAnswer<Boolean> {
        private final Object entity;
        private final Object clone;
        private final Collection<? extends Object> relatedEntites;
        private final Collection<GraphWiringCommand> graphWiringCommands;
        private final Collection<GraphPostProcessingCommand> graphPostProcessingCommands;
        
        private HibernateEntityBeanClonerActions(Object entity, Object clone) {
            this(entity, clone, null, null, null);
        }
        
        private HibernateEntityBeanClonerActions(Object entity, Object clone,
                Collection<? extends Object> relatedEntites,
                Collection<GraphWiringCommand> graphWiringCommands,
                Collection<GraphPostProcessingCommand> graphPostProcessingCommands) {
            this.entity = entity;
            this.clone = clone;
            this.relatedEntites = relatedEntites;
            this.graphWiringCommands = graphWiringCommands;
            this.graphPostProcessingCommands = graphPostProcessingCommands;
        }
        
        /* (non-Javadoc)
         * @see org.easymock.IAnswer#answer()
         */
        @SuppressWarnings("unchecked")
        public Boolean answer() throws Throwable {
            
            if (!CollectionUtils.isEmpty(relatedEntites)) {

                for (Object relatedEntity : relatedEntites) {
                    entityGraphCloner.addEntity(relatedEntity);
                }
                
            }
            
            if (!CollectionUtils.isEmpty(graphWiringCommands)) {
                
                for (GraphWiringCommand command : graphWiringCommands) {
                    entityGraphCloner.addGraphWiringCommand(command);
                }
                
            }
            
            if (!CollectionUtils.isEmpty(graphPostProcessingCommands)) {
                
                for (GraphPostProcessingCommand command : graphPostProcessingCommands) {
                    entityGraphCloner.addGraphPostProcessingCommand(command);
                }
                
            }            
            
            // the third argument is the traversal state, in which the new clone will be registered
            ((IdentityHashMap<Object, Object>) getCurrentArguments()[2]).put(entity, clone);
            
            // the visitor always returns true
            return Boolean.TRUE;
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void clone_entities() throws AccessException {
        final StubHibernateEntity entity1 = new StubHibernateEntity();
        
        String property = "007";
        final StubHibernateEntity relatedEntity = new SimplePropertyEqualStubHibernateEntity(property);
        entity1.setNonSimpleBeanProperty(relatedEntity);
        
        Set<StubHibernateEntity> nonSimpleCollectionBeanProperty = 
            new HashSet<StubHibernateEntity>();
        
        // reuse relatedEntity to check if its clone is used in both places
        nonSimpleCollectionBeanProperty.add(relatedEntity);
        entity1.setNonSimpleCollectionBeanProperty(nonSimpleCollectionBeanProperty);
        
        // the first call to the bean cloner creates a clone, adds a new entity and some commands
        final GraphWiringCommand graphWiringCommand1 = createMock(GraphWiringCommand.class);
        final GraphPostProcessingCommand graphPostProcessingCommand = 
            createMock(GraphPostProcessingCommand.class);
        final StubHibernateEntity clone1 = new StubHibernateEntity();
        entityBeanCloner.visitNode(eq(new EntityPreserveIdFlagPair(entity1, false)), 
                                   same(entityGraphCloner), 
                                   (IdentityHashMap<Object, Object>) anyObject());
        expectLastCall().andAnswer(new HibernateEntityBeanClonerActions(entity1, clone1,
                Arrays.asList(relatedEntity), Arrays.asList(graphWiringCommand1),
                Arrays.asList(graphPostProcessingCommand)));

        // note that entity2 is equal to (but not identical to) relatedEntity!
        final GraphWiringCommand graphWiringCommand2 = createMock(GraphWiringCommand.class);        
        final StubHibernateEntity entity2 = new SimplePropertyEqualStubHibernateEntity(property);
        entity2.setNonSimpleBeanProperty(entity1);
        final StubHibernateEntity clone2 = new SimplePropertyEqualStubHibernateEntity(property);
        entityBeanCloner.visitNode(eq(new EntityPreserveIdFlagPair(entity2, false)), 
                                   same(entityGraphCloner), 
                                   (IdentityHashMap<Object, Object>) anyObject());
        expectLastCall().andAnswer(new HibernateEntityBeanClonerActions(entity2, clone2,
                null, Arrays.asList(graphWiringCommand2), null));
        
        final StubHibernateEntity relatedEntityClone = new SimplePropertyEqualStubHibernateEntity(property);
        entityBeanCloner.visitNode(eq(new EntityPreserveIdFlagPair(relatedEntity, false)), 
                                   same(entityGraphCloner), 
                                   (IdentityHashMap<Object, Object>) anyObject());
        expectLastCall().andAnswer(
                new HibernateEntityBeanClonerActions(relatedEntity, relatedEntityClone));
        
        // use flags mutable for the mocks to track the order of calls
        final ThreadLocal<Integer> numGraphWiringCommandExecuted = new ThreadLocal<Integer>();
        numGraphWiringCommandExecuted.set(0);
        
        // the entity graph cloner should call the commands in the order they were added
        
        graphWiringCommand1.forEntities();
        expectLastCall().andReturn(Arrays.asList(entity1));
        graphWiringCommand1.execute(MapUtils.toMap(new IdentityHashMap<Object, Object>(),
                entity1, clone1));
        expectLastCall().andAnswer(new NumGraphWiringCommandsExecutedVerifier(
                numGraphWiringCommandExecuted, 0));
        
        graphWiringCommand2.forEntities();
        expectLastCall().andReturn(Arrays.asList(relatedEntity));
        graphWiringCommand2.execute(MapUtils.toMap(new IdentityHashMap<Object, Object>(),
                relatedEntity, relatedEntityClone));        
        expectLastCall().andAnswer(new NumGraphWiringCommandsExecutedVerifier(
                numGraphWiringCommandExecuted, 1));
        
        // this *must* be called after all the wiring commands have been completed
        graphPostProcessingCommand.execute();
        expectLastCall().andAnswer(new IAnswer<Object>() {

            public Object answer() throws Throwable {
                
                if (!(numGraphWiringCommandExecuted.get() == 2)) {
                    fail("Graph post-processing command executed before wiring was complete.");
                }
                
                return null;
            }
            
        });
        
        replay(entityBeanCloner, graphWiringCommand1, graphWiringCommand2,
                graphPostProcessingCommand);
        
        Map<StubHibernateEntity, StubHibernateEntity> clones = 
            entityGraphCloner.clone(Arrays.asList(entity1, entity2));
        assertEquals(MapUtils.<StubHibernateEntity, StubHibernateEntity>toMap(
                        entity1, clone1, entity2, clone2), 
                     clones);
        
        verify(entityBeanCloner, graphWiringCommand1, graphWiringCommand2, 
                graphPostProcessingCommand);
        
        // check that any internal state maintained during the cloning has been cleaned up
        assertTrue(ReflectionUtils.<List<?>>getValue(entityGraphCloner, "graphWiringCommands")
                   .isEmpty());
        assertTrue(ReflectionUtils.<List<?>>getValue(entityGraphCloner, "graphPostProcessingCommands")
                   .isEmpty());
        
        /*
         * The actual wiring of the objects is *not* checked because that is the function
         * of the command objects, *not* the entity graph cloner.
         * As such, this is not within the scope of a unit test.
         */
    }
    
    private class NumGraphWiringCommandsExecutedVerifier implements IAnswer<Object> {
        private final ThreadLocal<Integer> numCommandsExecutedCounter;
        private final Integer expectedNumCommandsExecuted;
        
        private NumGraphWiringCommandsExecutedVerifier(
                ThreadLocal<Integer> numCommandsExecutedCounter,
                Integer expectedNumCommandsExecuted) {
            this.numCommandsExecutedCounter = numCommandsExecutedCounter;
            this.expectedNumCommandsExecuted = expectedNumCommandsExecuted;
        }
        
        public Object answer() throws Throwable {
            Integer numCommandsExecuted = numCommandsExecutedCounter.get();
            
            if (!numCommandsExecuted.equals(expectedNumCommandsExecuted)) {
                fail("Command executed out of order. Expected " + expectedNumCommandsExecuted 
                     + " other commands to have been executed, but found " 
                     + numCommandsExecuted);
            }
            
            numCommandsExecutedCounter.set(numCommandsExecuted + 1);
            return null;
        }
        
    }
    
    /**
     * Checks that the originally specified parameter is used in constructing new 
     * entity/preserve ID flag pairs.
     */
    @Test
    public void addNode() throws AccessException {
        boolean preserveId = true;
        ReflectionUtils.setValue(entityGraphCloner, "preserveId", preserveId);
        
        StubHibernateEntity entity = new StubHibernateEntity();
        entityGraphCloner.addEntity(entity);
        
        Queue<EntityPreserveIdFlagPair> expectedQueue = new LinkedList<EntityPreserveIdFlagPair>();
        expectedQueue.add(new EntityPreserveIdFlagPair(entity, preserveId));
        assertEquals(expectedQueue, ReflectionUtils.getValue(entityGraphCloner, "nodeQueue"));
    }
    
}
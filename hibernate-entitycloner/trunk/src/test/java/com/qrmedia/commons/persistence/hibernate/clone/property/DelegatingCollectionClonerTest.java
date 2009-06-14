/*
 * @(#)DelegatingCollectionClonerTest.java     9 Feb 2009
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.easymock.Capture;
import org.hibernate.util.IdentitySet;
import org.junit.Test;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;
import com.qrmedia.commons.persistence.hibernate.clone.property.DelegatingCollectionCloner;
import com.qrmedia.commons.persistence.hibernate.clone.property.DelegatingCollectionCloner.AddToCollectionCommand;
import com.qrmedia.commons.persistence.hibernate.clone.property.DelegatingCollectionCloner.SetReplacementCommand;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.GraphPostProcessingCommand;
import com.qrmedia.commons.reflect.ReflectionUtils;

/**
 * Unit tests for the {@link DelegatingCollectionCloner}.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
public class DelegatingCollectionClonerTest {
    private DelegatingCollectionCloner propertyCloner = new DelegatingCollectionCloner();
    
    private HibernateEntityGraphCloner entityGraphCloner =
        createMock(HibernateEntityGraphCloner.class);    

    @Test
    public void clone_emptyCollection() {
        Collection<String> valueClone = propertyCloner.cloneCollection(null, null, null, 
                new ArrayList<String>(), entityGraphCloner);
        
        assertTrue(valueClone instanceof List);
        assertTrue(valueClone.isEmpty());
    }
    
    @Test
    public void clone_nonSet() {
        Collection<StubHibernateEntity> value = new LinkedList<StubHibernateEntity>();
        StubHibernateEntity member = new StubHibernateEntity();
        value.add(member);
        
        // the members of the nonSimpleCollectionBeanProperty need to be cloned and added
        entityGraphCloner.addEntity(member);
        expectLastCall();

        StubHibernateEntity target = new StubHibernateEntity();
        String propertyName = "nonSimpleCollectionBeanProperty";
        entityGraphCloner.addGraphWiringCommand(
                new AddToCollectionCommand(target, propertyName, member));
        expectLastCall();
        replay(entityGraphCloner);
        
        Collection<StubHibernateEntity> valueClone = propertyCloner.cloneCollection(null, target, 
                propertyName, value, entityGraphCloner);
        
        verify(entityGraphCloner);
        
        assertTrue(valueClone + " is not a List", valueClone instanceof List);
        assertTrue(valueClone + " non-empty", valueClone.isEmpty());
    }    
    
    @SuppressWarnings("unchecked")
    @Test
    public void clone_set() throws AccessException {
        Collection<StubHibernateEntity> value = new HashSet<StubHibernateEntity>();
        StubHibernateEntity member = new StubHibernateEntity();
        value.add(member);
        
        // the members of the nonSimpleCollectionBeanProperty need to be cloned and added
        entityGraphCloner.addEntity(member);
        expectLastCall();

        StubHibernateEntity target = new StubHibernateEntity();
        String propertyName = "nonSimpleCollectionBeanProperty";
        entityGraphCloner.addGraphWiringCommand(
                new AddToCollectionCommand(target, propertyName, member));
        expectLastCall();
        
        Capture<GraphPostProcessingCommand> graphPostProcessingCommandCapture =
            new Capture<GraphPostProcessingCommand>();
        entityGraphCloner.addGraphPostProcessingCommand(
                capture(graphPostProcessingCommandCapture));
        expectLastCall();        
        replay(entityGraphCloner);
        
        Collection<StubHibernateEntity> valueClone = propertyCloner.cloneCollection(null, target, 
                propertyName, value, entityGraphCloner);
        
        verify(entityGraphCloner);
        
        assertTrue(valueClone + " is not an IdentitySet", valueClone instanceof IdentitySet);
        assertTrue(valueClone + " non-empty", valueClone.isEmpty());
        
        /*
         * Check that a post-processing command was created to replace the IdentitySet
         * temporarily created with an equals-based set.
         */
        SetReplacementCommand<StubHibernateEntity> setReplacementCommand = 
            (SetReplacementCommand<StubHibernateEntity>) 
            graphPostProcessingCommandCapture.getValue();
        assertSame(target, 
                   ReflectionUtils.getValue(setReplacementCommand, "target"));
        assertEquals(propertyName, 
                     ReflectionUtils.getValue(setReplacementCommand, "propertyName"));
        
        // also ensures that the replacement is not the same as the original
        assertFalse(ReflectionUtils.<Object>getValue(setReplacementCommand, "replacement") 
                    instanceof IdentitySet);
    }
    
}
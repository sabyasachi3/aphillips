/*
 * @(#)SimpleCollectionClonerTest.java     9 Feb 2009
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.Thread.State;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;
import com.qrmedia.commons.persistence.hibernate.clone.property.SimpleCollectionCloner;

/**
 * Unit tests for the {@link SimpleCollectionCloner}.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
public class SimpleCollectionClonerTest {
    private SimpleCollectionCloner propertyCloner = new SimpleCollectionCloner();

    @Test
    public void clone_nonSimpleCollectionType() {
        assertNull(propertyCloner.cloneCollection(null, null, null, Arrays.asList(new StubHibernateEntity()), 
                                                  null));
    }
    
    @Test
    public void clone_emptyCollection() {
        /*
         * Returning a non-null collection ensures the cloned collection will be linked 
         * to the target object.
         */
        Collection<String> valueClone = 
            propertyCloner.cloneCollection(null, null, null, new LinkedList<String>(), null);
        
        assertTrue(valueClone instanceof List);
        assertTrue(valueClone.isEmpty());
    }        
    
    @Test
    public void cloneTest() {
        Collection<State> value = new LinkedList<State>();
        value.add(State.NEW);
        
        Collection<State> valueClone = 
            propertyCloner.cloneCollection(null, null, null, value, null);
        
        // ensure the target collection was populated to match the source
        assertEquals(value, valueClone);
    }    
    
}

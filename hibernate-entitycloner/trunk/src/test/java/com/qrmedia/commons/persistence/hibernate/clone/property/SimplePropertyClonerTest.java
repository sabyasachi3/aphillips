/*
 * @(#)SimplePropertyClonerTest.java     9 Feb 2009
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;
import com.qrmedia.commons.persistence.hibernate.clone.property.SimplePropertyCloner;

/**
 * Unit tests for the {@link SimplePropertyCloner}.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
public class SimplePropertyClonerTest {
    private SimplePropertyCloner propertyCloner = new SimplePropertyCloner();

    @Test
    public void clone_nonSimplePropertyType() {
        StubHibernateEntity target = new StubHibernateEntity();
        assertFalse(propertyCloner.cloneValue(null, target, "nonSimpleBeanProperty", 
                                         new StubHibernateEntity(), null));
        
        // non-simple property types should be ignored
        assertNull(target.getNonSimpleBeanProperty());
    }
    
    @Test
    public void clone_nullProperty() {
        StubHibernateEntity target = new StubHibernateEntity();
        
        // set a property to ensure null is really written
        target.setSimpleBeanProperty("James Bond");
        
        assertTrue(propertyCloner.cloneValue(null, target, "simpleBeanProperty", null, null));
        
        assertNull(target.getSimpleBeanProperty());
    }
    
    @Test
    public void cloneTest() {
        StubHibernateEntity target = new StubHibernateEntity();
        String value = "James Bond";
        assertTrue(propertyCloner.cloneValue(null, target, "simpleBeanProperty", value, null));
        
        assertSame(value, target.getSimpleBeanProperty());
    }    
    
}

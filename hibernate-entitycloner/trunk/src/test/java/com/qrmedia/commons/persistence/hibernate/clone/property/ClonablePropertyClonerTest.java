/*
 * @(#)ClonablePropertyClonerTest.java     11 Feb 2009
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
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.naming.CompositeName;
import javax.naming.Name;

import org.junit.Test;

import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;
import com.qrmedia.commons.persistence.hibernate.clone.property.CloneablePropertyCloner;

/**
 * Unit tests for the {@link CloneablePropertyCloner}.
 * 
 * @author anph
 * @since 11 Feb 2009
 *
 */
public class ClonablePropertyClonerTest {
    private CloneablePropertyCloner propertyCloner = new CloneablePropertyCloner();
    
    @Test
    public void clone_nonClonablePropertyType() {
        StubHibernateEntity target = new StubHibernateEntity();
        assertFalse(propertyCloner.cloneValue(null, target, "nonSimpleBeanProperty", 
                                         new StubHibernateEntity(), null));
        
        // non-simple property types should be ignored
        assertNull(target.getNonSimpleBeanProperty());
    }
    
    @Test
    public void cloneTest() {
        StubHibernateEntity target = new StubHibernateEntity();
        
        // createMock(Cloneable.class) fails because clone() isn't a method of the interface!
        Name value = createMock(Name.class);
        
        Name valueClone = new CompositeName();
        value.clone();
        expectLastCall().andReturn(valueClone);
        replay(value);
        
        assertTrue(propertyCloner.cloneValue(null, target, "cloneableBeanProperty", value, null));
        
        verify(value);
        
        assertSame(valueClone, target.getCloneableBeanProperty());        
    }
}

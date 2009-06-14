/*
 * @(#)AbstractNonNullValueAwarePropertyClonerTest.java     9 Feb 2009
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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;
import com.qrmedia.commons.persistence.hibernate.clone.property.AbstractNonNullValueAwarePropertyCloner;

/**
 * Unit tests for the {@link AbstractNonNullValueAwarePropertyClonerTest}.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
public class AbstractNonNullValueAwarePropertyClonerTest {
    private AbstractNonNullValueAwarePropertyCloner propertyCloner;
    
    @Before
    public void prepareFixture() throws Exception {
        propertyCloner = createMock(AbstractNonNullValueAwarePropertyCloner.class,
                AbstractNonNullValueAwarePropertyCloner.class.getDeclaredMethod(
                        "cloneNonNullValue", Object.class, Object.class, String.class, 
                        Object.class, HibernateEntityGraphCloner.class));        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void clone_nullValue() {
        propertyCloner.cloneValue(null, null, null, null, null);
    }
    
    @Test
    public void clone_successful() {
        StubHibernateEntity source = new StubHibernateEntity();
        String value = "James Bond";
        StubHibernateEntity target = new StubHibernateEntity();
        String propertyName = "simpleBeanProperty";
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        propertyCloner.cloneNonNullValue(source, target, propertyName, value, entityGraphCloner);
        expectLastCall().andReturn(true);
        replay(propertyCloner);
        
        assertTrue(propertyCloner.cloneValue(source, target, propertyName, value, 
                                             entityGraphCloner));
        
        verify(propertyCloner);
    }
    
    @Test
    public void clone_unsuccessful() {
        StubHibernateEntity source = new StubHibernateEntity();
        String value = "James Bond";
        StubHibernateEntity target = new StubHibernateEntity();
        String propertyName = "simpleBeanProperty";
        HibernateEntityGraphCloner entityGraphCloner = new HibernateEntityGraphCloner();
        propertyCloner.cloneNonNullValue(source, target, propertyName, value, entityGraphCloner);
        expectLastCall().andReturn(false);
        replay(propertyCloner);
        
        assertFalse(propertyCloner.cloneValue(source, target, propertyName, value, 
                                              entityGraphCloner));
        
        verify(propertyCloner);
    }    

}

/*
 * @(#)DirtyCheckableUserTypeTest.java     8 Apr 2009
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
package com.qrmedia.commons.persistence.hibernate.usertype;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link DirtyCheckableUserType}.
 * 
 * @author aphillips
 * @since 8 Apr 2009
 *
 */
public class DirtyCheckableUserTypeTest {
    private DirtyCheckableUserType userType;
    
    @Before
    public void prepareFixture() throws Exception {
        userType = createNiceMock(DirtyCheckableUserType.class, 
                DirtyCheckableUserType.class.getDeclaredMethod("isDirty", Object.class));
    }
    
    @Test
    public void cleanEqualObjectsAreEqual() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(false);

        Object y = "James";
        userType.isDirty(y);
        expectLastCall().andReturn(false);
        replay(userType);
        
        assertTrue(userType.equals(x, y));
        
        verify(userType);
    }

    @Test
    public void cleanUnequalObjectsAreNotEqual() {
        Object x = "James";
        userType.isDirty(x);

        /*
         * stubReturn here because it doesn't matter if the dirty state is checked
         * as long as it is realized that the items are not equal!
         */
        expectLastCall().andStubReturn(false);
        
        Object y = "Bond";
        userType.isDirty(y);
        expectLastCall().andStubReturn(false);
        replay(userType);
        
        assertFalse(userType.equals(x, y));
        
        verify(userType);
    }

    @Test
    public void dirtyXMeansNotEqual() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(true);
        
        Object y = "James";
        userType.isDirty(y);
        expectLastCall().andStubReturn(false);
        replay(userType);
        
        assertFalse(userType.equals(x, y));
        
        verify(userType);
    }
    
    @Test
    public void dirtyYMeansNotEqual() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andStubReturn(false);
        
        Object y = "James";
        userType.isDirty(y);
        expectLastCall().andReturn(true);
        replay(userType);
        
        assertFalse(userType.equals(x, y));
        
        verify(userType);
    }

    private static class NeverEquals {
        
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            fail(String.format("equals(%s) called on %s", obj, this));
            return false;
        }

    }
    
    @Test
    public void dirtyXMeansNoCallToEquals() {
        NeverEquals x = new NeverEquals();
        userType.isDirty(same(x));
        expectLastCall().andReturn(true);
        
        NeverEquals y = new NeverEquals();
        userType.isDirty(same(y));
        expectLastCall().andStubReturn(false);
        replay(userType);
        
        assertFalse(userType.equals(x, y));
        
        verify(userType);
    }
    
    @Test
    public void dirtyYMeansNoCallToEqual() {
        NeverEquals x = new NeverEquals();
        
        // isDirty(x) (i.e. isDirty(eq(x))) would result in a call to equals
        userType.isDirty(same(x));
        expectLastCall().andStubReturn(false);

        NeverEquals y = new NeverEquals();
        userType.isDirty(same(y));
        expectLastCall().andReturn(true);
        
        replay(userType);
        
        assertFalse(userType.equals(x, y));
        
        verify(userType);
    }
    
    @Test
    public void cleanEqualObjectsHaveTheSameHashCode() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(false).times(2);
        replay(userType);
        
        assertEquals(userType.hashCode(x), userType.hashCode(x));
        
        verify(userType);
    }

    @Test
    public void dirtyXMeansDifferentHashCode() {
        // now it's dirty...
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(true);
        
        // ...now it's not
        Object y = x;
        userType.isDirty(y);
        expectLastCall().andReturn(false);
        replay(userType);
        
        assertFalse(userType.hashCode(x) == userType.hashCode(y));
        
        verify(userType);
    }
    
    @Test
    public void assemble() {
        Serializable cached = new Integer(0);
        Serializable value = new Integer(0);
        userType.deepCopy(cached);
        expectLastCall().andReturn(value);
        replay(userType);
        
        assertSame(value, userType.assemble(cached, null));
        
        verify(userType);
    }

    @Test
    public void disassemble() {
        Serializable value = new Integer(0);
        Serializable cacheable = new Integer(0);
        userType.deepCopy(value);
        expectLastCall().andReturn(cacheable);
        replay(userType);
        
        assertSame(cacheable, userType.disassemble(value));
        
        verify(userType);
    }

    @Test
    public void replace() {
        Serializable value = new Integer(0);
        Serializable replacement = new Integer(0);
        userType.deepCopy(value);
        expectLastCall().andReturn(replacement);
        replay(userType);
        
        assertSame(replacement, userType.replace(value, null, null));
        
        verify(userType);
    }

}

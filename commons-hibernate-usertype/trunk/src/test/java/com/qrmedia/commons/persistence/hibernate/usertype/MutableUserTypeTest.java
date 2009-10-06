/*
 * @(#)MutableUserTypeTest.java     8 Apr 2009
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
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link MutableUserTypeTest}.
 * 
 * @author aphillips
 * @since 8 Apr 2009
 *
 */
public class MutableUserTypeTest {
    private MutableUserType userType;
    
    @Before
    public void prepareFixture() throws Exception {
        userType = createMock(MutableUserType.class, 
                MutableUserType.class.getDeclaredMethod("isDirty", Object.class),
                MutableUserType.class.getMethod("deepCopy", Object.class));
    }
    
    @Test
    public void equals() {
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
    public void equals_unequalObjects() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(false);
        
        Object y = "Bond";
        userType.isDirty(y);
        expectLastCall().andReturn(false);
        replay(userType);
        
        assertFalse(userType.equals(x, y));
        
        verify(userType);
    }

    @Test
    public void equals_dirtyX() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(true);
        
        // that's enough - they can't be equal
        replay(userType);
        
        assertFalse(userType.equals(x, "Bond"));
        
        verify(userType);
    }
    
    @Test
    public void equals_dirtyY() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(false);
        
        Object y = "James";
        userType.isDirty(y);
        expectLastCall().andReturn(true);
        replay(userType);
        
        assertFalse(userType.equals(x, y));
        
        verify(userType);
    }
    @Test
    public void hashCode_equalObjects() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(false).times(2);
        replay(userType);
        
        assertEquals(userType.hashCode(x), userType.hashCode(x));
        
        verify(userType);
    }

    @Test
    public void hashCode_unequalObjects() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(false);

        Object y = "Bond";
        userType.isDirty(y);
        expectLastCall().andReturn(false);
        replay(userType);
        
        assertFalse(userType.hashCode(x) == userType.hashCode(y));
        
        verify(userType);
    }
    
    @Test
    public void hashCode_equalObjects_dirty() {
        Object x = "James";
        userType.isDirty(x);
        expectLastCall().andReturn(true);
        
        userType.isDirty(x);
        expectLastCall().andReturn(false);
        replay(userType);
        
        assertFalse(userType.hashCode(x) == userType.hashCode(x));
        
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

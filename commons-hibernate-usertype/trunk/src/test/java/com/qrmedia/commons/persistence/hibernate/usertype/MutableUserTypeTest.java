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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.*;

import java.io.Serializable;

import org.hibernate.type.SerializationException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link MutableUserType}.
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
                MutableUserType.class.getMethod("deepCopy", Object.class));
    }
    
    @Test
    public void equalObjectsAreEqual() {
        assertTrue(userType.equals("James", new String("James")));
    }

    @Test
    public void unequalObjectsAreNotEqual() {
        assertFalse(userType.equals("James", "Bond"));
    }

    @Test
    public void equalObjectsHaveSameHashCode() {
        assertEquals(userType.hashCode("James"), userType.hashCode(new String("James")));
    }

    @Test
    public void assembleReturnsDeepCopy() {
        Serializable cached = new Integer(0);
        Serializable value = new Integer(0);
        userType.deepCopy(cached);
        expectLastCall().andReturn(value);
        replay(userType);
        
        assertSame(value, userType.assemble(cached, null));
        
        verify(userType);
    }

    @Test
    public void disassembleThrowsExceptionIfDeepCopyIsNotSerializable() {
        Object value = new Object();
        expect(userType.deepCopy(value)).andReturn(new Object());
        replay(userType);
        
        try {
            userType.disassemble(value);
            fail("Expected an SerializationException");
        } catch (SerializationException exception) {
            // expected
        }
        
        verify(userType);        
    }

    @Test
    public void disassembleReturnsDeepCopy() {
        Serializable value = new Integer(0);
        Serializable cacheable = new Integer(0);
        userType.deepCopy(value);
        expectLastCall().andReturn(cacheable);
        replay(userType);
        
        assertSame(cacheable, userType.disassemble(value));
        
        verify(userType);
    }

    @Test
    public void replaceReturnsDeepCopy() {
        Serializable value = new Integer(0);
        Serializable replacement = new Integer(0);
        userType.deepCopy(value);
        expectLastCall().andReturn(replacement);
        replay(userType);
        
        assertSame(replacement, userType.replace(value, null, null));
        
        verify(userType);
    }

}

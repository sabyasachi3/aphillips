/*
 * @(#)AbstractMutableCollectionReturningUserTypeTest.java     24 Feb 2009
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
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link AbstractCollectionReturningUserType}.
 * 
 * @author anph
 * @since 24 Feb 2009
 *
 */
public class AbstractCollectionReturningUserTypeTest {
    private AbstractCollectionReturningUserType userType;
    
    @Before
    public void prepareFixture() throws Exception {
        userType = createMock(AbstractCollectionReturningUserType.class,
            AbstractCollectionReturningUserType.class.getDeclaredMethod("deepCopyValue", 
                    Object.class));
    }
    
    @Test
    public void deepCopy_nonCollection() {
        Object value = new Object();
        Object valueCopy = new Object();
        userType.deepCopyValue(same(value));
        expectLastCall().andReturn(valueCopy);
        replay(userType);
        
        assertSame(valueCopy, userType.deepCopy(value));
        
        verify(userType);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void deepCopy_collection() {
        Object member = new Object();
        List<Object> list = Arrays.asList(member);
        
        expect(userType.deepCopyValue(member)).andReturn(member);
        replay(userType);
        
        List<Object> listClone = (List<Object>) userType.deepCopy(list);
        assertNotSame(list, listClone);
        assertEquals(list, listClone);
        
        verify(userType);
    }

}

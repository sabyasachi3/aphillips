/*
 * @(#)AbstractPropertyModifyingCommandTest.java     9 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.wiring;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityBeanCloner;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.AbstractPropertyModifyingCommand;

/**
 * Unit tests for the {@link AbstractPropertyModifyingCommand}.
 * <p>
 * Note: <code>AbstractPropertyModifyingCommand</code> is a member class of 
 * {@link HibernateEntityBeanCloner}.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
@RunWith(value = Parameterized.class)
public class AbstractPropertyModifyingCommandTest {
    private AbstractPropertyModifyingCommand command1;
    private AbstractPropertyModifyingCommand command2;
    private boolean expectedEquality;
    
    private static class PropertyModifyingCommand1 extends AbstractPropertyModifyingCommand {

        private PropertyModifyingCommand1(Object target, String propertyName,
                Object originalValue) {
            super(target, propertyName, originalValue);
        }
        
        @Override
        protected void wireUpProperty(Object target, String propertyName,
                Object originalEntityClone) { }
        
    }
    
    private static class PropertyModifyingCommand2 extends AbstractPropertyModifyingCommand {

        private PropertyModifyingCommand2(Object target, String propertyName,
                Object originalValue) {
            super(target, propertyName, originalValue);
        }
        
        @Override
        protected void wireUpProperty(Object target, String propertyName,
                Object originalEntityClone) { }
        
    }
    
    @Parameters
    public static Collection<Object[]> data() throws Exception {
        Collection<Object[]> data = new ArrayList<Object[]>();
        Object target = new Object();
        String propertyName = "name";
        Object originalEntity = "James Bond";
        
        /* command1 or command2 must be non-null (otherwise, the test is just null == null?) */
        
        // equal objects
        data.add(new Object[] { new PropertyModifyingCommand1(target, propertyName, originalEntity), 
                new PropertyModifyingCommand1(target, propertyName, originalEntity), true });
        data.add(new Object[] { new PropertyModifyingCommand2(target, propertyName, originalEntity), 
                new PropertyModifyingCommand2(target, propertyName, originalEntity), true });
        
        // unequal objects
        data.add(new Object[] { new PropertyModifyingCommand1(target, propertyName, originalEntity), 
                null, false });        
        data.add(new Object[] { new PropertyModifyingCommand2(target, propertyName, originalEntity), 
                null, false });        
        data.add(new Object[] { new PropertyModifyingCommand1(target, propertyName, originalEntity), 
                new PropertyModifyingCommand2(target, propertyName, originalEntity), false });        
        return data;
    }
    
    // called for each parameter set in the test data
    public AbstractPropertyModifyingCommandTest(AbstractPropertyModifyingCommand command1,
            AbstractPropertyModifyingCommand command2, boolean expectedEquality) {
        this.command1 = command1;
        this.command2 = command2;
        this.expectedEquality = expectedEquality;
    }

    @Test
    public void equals() {
        // one of command1 and command2 is expected to be non-null
        assertEquals(expectedEquality, ((command1 != null) && command1.equals(command2)));
        assertEquals(expectedEquality, ((command2 != null) && command2.equals(command1)));        
    }
    
    @Test
    public void hashCodeTest() {
        /*
         * Equal objects should have the same hash code. Note that one of command1 and command2
         * is expected to be non-null.
         */
        assertEquals(expectedEquality, ((command1 != null) && (command2 != null)
                                        && (command1.hashCode() == command2.hashCode())));
    }
    
}

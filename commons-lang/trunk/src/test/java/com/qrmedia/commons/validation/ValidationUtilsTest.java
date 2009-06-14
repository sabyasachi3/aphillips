/*
 * @(#)ValidationUtils.java     6 Mar 2009
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
package com.qrmedia.commons.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the {@link ValidationUtils}.
 * 
 * @author aphillips
 * @since 6 Mar 2009
 *
 */
@RunWith(value = Parameterized.class)
public class ValidationUtilsTest {
    private final Object[] objs;
    private String message;
    private final boolean exceptionExpected;
    
    @Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // no null arguments
        data.add(new Object[] { new Object[] {}, null, false });
        data.add(new Object[] { new Object[] { new Object() }, null, false });
        
        // null arguments
        data.add(new Object[] { new Object[] { null }, "error", true });
        data.add(new Object[] { new Object[] { new Object(), null }, "error", true });
        return data;
    }
    
    // called for each parameter set in the test data
    public ValidationUtilsTest(Object[] objs, String message, boolean exceptionExpected) {
        this.objs = objs;
        this.message = message;
        this.exceptionExpected = exceptionExpected;
    }

    @Test
    public void checkNotNull_null() {
        
        if (exceptionExpected) {
            
            try {
                ValidationUtils.checkNotNull(message, objs);
                fail();
            } catch (IllegalArgumentException exception) {
                // expected
                assertEquals(message, exception.getMessage());
            }
            
        }
        
    }   
    
    @Test
    public void checkNotNull_notNull() {
        
        if (!exceptionExpected) {
            ValidationUtils.checkNotNull(message, objs);
        }
        
    }  
    
}

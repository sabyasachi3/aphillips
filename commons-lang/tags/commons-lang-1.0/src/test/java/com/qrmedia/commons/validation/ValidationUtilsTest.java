/*
 * @(#)ValidationUtils.java     6 Mar 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
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

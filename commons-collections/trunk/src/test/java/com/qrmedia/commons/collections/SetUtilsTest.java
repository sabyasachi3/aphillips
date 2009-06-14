/*
 * @(#)SetUtils.java     29 Aug 2008
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
package com.qrmedia.commons.collections;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the {@link SetUtils}.
 * 
 * @author anph
 * @since 29 Aug 2008
 */
@RunWith(value = Parameterized.class)
public class SetUtilsTest {
    private Object[] objects;
    private Set<Object> expectedSet;
    
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // nulls
        data.add(new Object[] { new Object[] { null } , asSet((Object) null) });
        data.add(new Object[] { new Object[] { null, null } , asSet((Object) null) });
        
        // objects (some duplicates)
        data.add(new Object[] { new Object[] { Long.valueOf(5) } , asSet(Long.valueOf(5)) });
        data.add(new Object[] { new Object[] { Long.valueOf(5), Long.valueOf(6) } , 
                                asSet(Long.valueOf(5), Long.valueOf(6)) });
        data.add(new Object[] { new Object[] { Long.valueOf(5), Long.valueOf(5) } , 
                                asSet(Long.valueOf(5)) });        
        
        // mixed objects
        data.add(new Object[] { new Object[] { Long.valueOf(5), Integer.valueOf(6) } , 
                                asSet(Long.valueOf(5), Integer.valueOf(6)) });        
        data.add(new Object[] { new Object[] { Long.valueOf(5), Integer.valueOf(5) } , 
                                asSet(Long.valueOf(5), Integer.valueOf(5)) });
        
        return data;
    }
    
    private static Set<Object> asSet(Object... objs) {
        Set<Object> set = new HashSet<Object>(objs.length);
        
        for (Object obj : objs) {
            set.add(obj);
        }
        
        return set;
    }
    
    // called for each parameter set in the test data
    public SetUtilsTest(Object[] objects, Set<Object> expectedSet) {
        this.objects = objects;
        this.expectedSet = expectedSet;
    }

    @Test
    public void asSet() {
        assertEquals(expectedSet, SetUtils.asSet(objects));
    }
    
}
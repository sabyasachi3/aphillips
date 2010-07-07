/*
 * @(#)PairTest.java     29 Aug 2008
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
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for {@link Pair Pairs}.
 * 
 * @author anph
 * @since 29 Aug 2008
 */
@RunWith(value = Parameterized.class)
public class PairTest {
    private Pair<?, ?> pair1;
    private Pair<?, ?> pair2;
    private boolean expectedEquals;
    
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // pairs are order-insensitive, and should be generic-type-independent
        data.add(new Object[] { new Pair<Object, Object>("james", "bond"), 
                new Pair<Object, Object>("007", "007"), false });
        data.add(new Object[] { new Pair<Object, Object>("james", "bond"), 
                new Pair<Object, Object>(7, 7), false });
        data.add(new Object[] { new Pair<Object, Object>("james", "bond"), 
                new Pair<Object, Object>(null, null), false });
        
        Pair<Object, Object> pair = new Pair<Object, Object>("james", "bond");
        data.add(new Object[] { pair, pair, true });
        data.add(new Object[] { new Pair<Object, Object>("james", "bond"), 
                new Pair<Object, Object>("james", "bond"), true });
        data.add(new Object[] { new Pair<Object, Object>("james", "bond"), 
                new Pair<Object, Object>("bond", "james"), true });
        data.add(new Object[] { new Pair<Object, Object>("james", "bond"), 
                new Pair<String, String>("james", "bond"), true });
        data.add(new Object[] { new Pair<Object, Object>("james", null), 
                new Pair<String, String>(null, "james"), true });      
        data.add(new Object[] { new Pair<Object, Object>(null, null), 
                new Pair<String, Integer>(null, null), true });          
        
        return data;
    }
    
    // called for each parameter set in the test data
    public PairTest(Pair<?, ?> pair1, Pair<?, ?> pair2, boolean expectedEquals) {
        this.pair1 = pair1;
        this.pair2 = pair2;
        this.expectedEquals = expectedEquals;
    }
    
    @Test
    public void equals() {
        assertEquals(expectedEquals, pair1.equals(pair2));
        assertEquals(expectedEquals, pair2.equals(pair1));        
    }
    
    @Test
    public void hashCodeTest() {
        
        // equal objects should have the same hash code
        assertEquals(expectedEquals, (pair1.hashCode() == pair2.hashCode()));
    }    
    
}
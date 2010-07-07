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
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * Unit tests for copy constructors of {@link Pair}.
 * 
 * @author anph
 * @since 29 Aug 2008
 */
public class PairCopyTest {
    
    @Test
    public void copyReturnsEqualButDifferentPair() {
        Pair<String, String> original = PairUtils.toPair("james", "bond");
        Pair<String, String> copy = original.copy();
        assertNotSame(original, copy);
        assertEquals(original, copy);
    }
    
    @Test
    public void copyKeepFirstUsesNewSecondObject() {
        String newSecondObject = "Bond";
        assertEquals(PairUtils.toPair("james", newSecondObject), 
                PairUtils.toPair("james", "bond").copyKeepFirst(newSecondObject));
    }
    
    @Test
    public void copyKeepSecondUsesNewFirstObject() {
        String newFirstObject = "James";
        assertEquals(PairUtils.toPair(newFirstObject, "bond"), 
                PairUtils.toPair("james", "bond").copyKeepSecond(newFirstObject));
    }    
}
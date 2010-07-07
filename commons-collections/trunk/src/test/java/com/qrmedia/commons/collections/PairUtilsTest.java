/*
 * @(#)PairUtilsTest.java     29 Aug 2008
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

/**
 * Unit tests for the {@link PairUtils}.
 * 
 * @author anph
 * @since 29 Aug 2008
 */
public class PairUtilsTest {

    @Test
    public void toPairAcceptsNullFirstObject() {
        assertNull(PairUtils.toPair(null, new Object()).getFirstObject());
    }

    @Test
    public void toPairAcceptsNullSecondObject() {
        assertNull(PairUtils.toPair(new Object(), null).getSecondObject());
    }
    
    @Test
    public void toPairSetsFirstObject() {
        Object obj = new Object();
        assertEquals(obj, PairUtils.toPair(obj, null).getFirstObject());
    }

    @Test
    public void toPairSetsSecondObject() {
        Object obj = new Object();
        assertEquals(obj, PairUtils.toPair(null, obj).getSecondObject());
    }
    
    @Test
    public void toPairs_null() {
        assertTrue(PairUtils.toPairs(null, null).isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toPairs_nullSecondObject() {
        String firstObject = "007";
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList(new Pair<String, Object>(firstObject, (Object) null)),
                PairUtils.toPairs(Arrays.asList(firstObject), null)));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void toPairs() {
        String firstObject1 = "007";
        String firstObject2 = "008";   
        Object secondObject = new Object();
        
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList(new Pair<String, Object>(firstObject1, secondObject), 
                              new Pair<String, Object>(firstObject2, secondObject)),
                PairUtils.toPairs(Arrays.asList(firstObject1, firstObject2), secondObject)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void toPairs_nullSubclass() {
        PairUtils.toPairs(null, Arrays.asList("James"), "Bond");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void toPairs_invalidSubclass() {
        PairUtils.toPairs(new Pair<String, String>("", "") {}.getClass(), Arrays.asList("James"), "Bond");
    }
    
    private static class StringBooleanPair extends Pair<String, Boolean> {
        
        public StringBooleanPair(String string, Boolean bool) {
            super(string, bool);
        }
        
    }
    
    @Test
    public void toPairs_subclass_null() {
        assertTrue(PairUtils.toPairs(StringBooleanPair.class, null, null).isEmpty());
    }
    
    @Test
    public void toPairs_subclass_nullSecondObject() {
        String firstObject = "007";
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList(new StringBooleanPair(firstObject, null)),
                PairUtils.toPairs(StringBooleanPair.class, Arrays.asList(firstObject), null)));
    }    
    
    @Test
    public void toPairs_subclass() {
        String string1 = "007";
        String string2 = "008";   
        Boolean bool = Boolean.TRUE;
        
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList(new StringBooleanPair(string1, bool), 
                              new StringBooleanPair(string2, bool)),
                PairUtils.toPairs(StringBooleanPair.class, 
                                  Arrays.asList(string1, string2), bool)));
    }    
    
    @Test
    public void toPairs_inaccessibleConstructorSubclass() {
        assertTrue(CollectionUtils.isEqualCollection(
                Arrays.asList(new StubPrivateConstructorPair()),
                PairUtils.toPairs(StubPrivateConstructorPair.class, 
                                  Arrays.asList((Object) null), null)));
    }    
    
}
/*
 * @(#)MapUtilsTest.java     23 May 2009
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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

/**
 * Unit tests for the {@code MapUtils}.
 * <p>
 * Since, as documented, this class only &quot;decorates&quot; Apache's 
 * {@linkplain org.apache.commons.collections.MapUtils} the tests do not
 * cover only functionality not in the underlying class.
 * 
 * @author aphillips
 * @since 23 May 2009
 *
 */
public class MapUtilsTest {
    
    @Test
    public void toMap_null() {
        assertEquals(new TreeMap<Object, Object>(), MapUtils.toMap((Object[]) null));
    }
    
    /**
     * Because the underlying implementation deals with {@code <Object, Object>} maps,
     * invalid values can be entered without error!
     */
    @Test
    public void toMap_invalidEntries() {
        String key = "James Bond";
        Integer invalidKey = 0;
        Map<String, Integer> map = MapUtils.toMap(key, "invalid", invalidKey, 7);

        // ouch!
        Object value = map.get(key);
        assertNotNull(value);
        assertFalse(value instanceof Integer);
        
        // ouch again!
        assertTrue(map.containsKey(invalidKey));
    }  
    
    @Test
    public void toMap() {
        Map<String, Integer> expectedMap = new TreeMap<String, Integer>();
        String key = "James Bond";
        Integer value = 7;
        expectedMap.put(key, value);
        
        assertEquals(expectedMap, MapUtils.toMap(key, value));
    }

    /**
     * Check that, if an instance is provided, its existing content is preserved.
     */
    @Test
    public void toMap_providedInstance_null() {
        Map<Object, Object> providedMap = new HashMap<Object, Object>();
        providedMap.put("James Bond", "007");
        Map<Object, Object> expectedMap = new TreeMap<Object, Object>(providedMap);
        
        // two checks: that the contents are as expected and that the given *instance* is used
        assertSame(providedMap, MapUtils.toMap(providedMap, (Object[]) null));
        assertEquals(expectedMap, providedMap);
    }

    /**
     * Because the underlying implementation deals with {@code <Object, Object>} maps,
     * invalid values can be entered without error, even if the map is provided.
     */
    @Test
    public void toMap_providedInstance_invalidEntries() {
        Map<String, Integer> providedMap = new HashMap<String, Integer>();
        providedMap.put("Stuart Thomas", 5);        
        String newKey = "James Bond";
        Integer invalidNewKey = 0;
        MapUtils.toMap(providedMap, newKey, "invalid", invalidNewKey, 7);
        
        // ouch!
        Object newValue = providedMap.get(newKey);
        assertNotNull(newValue);
        assertFalse(newValue instanceof Integer);
        
        // ouch again!
        assertTrue(providedMap.containsKey(invalidNewKey));
    }  
    
    @Test
    public void toMap_providedInstance() {
        Map<String, Integer> providedMap = new HashMap<String, Integer>();
        providedMap.put("Stuart Thomas", 5);        
        Map<String, Integer> expectedMap = new TreeMap<String, Integer>(providedMap);
        
        String newKey = "James Bond";
        Integer newValue = 7;
        expectedMap.put(newKey, newValue);
        
        assertSame(providedMap, MapUtils.toMap(providedMap, newKey, newValue));
        assertEquals(expectedMap, providedMap);
    }
    
}
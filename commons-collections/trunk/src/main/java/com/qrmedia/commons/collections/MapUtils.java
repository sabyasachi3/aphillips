/*
 * @(#)MapUtils.java     24 Nov 2008
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

import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for {@link Map Maps}.
 * 
 * @author anph
 * @see org.apache.commons.collections.MapUtils
 * @since 24 Nov 2008
 *
 */
public class MapUtils {

    /**
     * Puts all the keys and values from the specified array into a new map.
     * <p>
     * An replacement for the Apache Commons {@link org.apache.commons.collections.MapUtils#putAll(Map, Object[]) MapUtils#putAll(Map, Object[])}
     * method. That one is very useful but requires a (usually empty) map as an input parameter and
     * does not support generics, leading to lots of casts and <code>@SuppressWarnings</code> 
     * declarations.
     * <p>
     * <strong>Use with care:</strong> The method may well support generics, but it is <strong>
     * <u>not runtime typesafe</u></strong>! Because the runtime type of the map is 
     * {@code <Object, Object>}, values not compatible with the generic type declaration can 
     * be inserted (without causing an error!) if the {@code keysAndValues} argument is not 
     * properly constructed.
     * <p>
     * This can lead to tricky {@code ClassCastExceptions}, potentially in very distant parts of
     * the code.
     * 
     * @param <U> the type of the keys
     * @param <V> the type of the values
     * @param keysAndValues an array of keys and values (the item at index <code>i</code> is treated
     *                      as the key, <code>i+1</code> is the value) to be added
     * @return  a new map containing the keys and values added
     * @throws cf. {@linkplain org.apache.commons.collections.MapUtils#putAll(Map, Object[])}
     * @see #toMap(Map, Object...)
     * @see org.apache.commons.collections.MapUtils#putAll(Map, Object[])
     */
    public static <U, V> Map<U, V> toMap(Object... keysAndValues) {
        return toMap(new HashMap<U, V>(), keysAndValues);
    }
    
    /**
     * Puts all the keys and values from the specified array into the target map. This
     * enables the caller to determine the map subclass used.
     * <p>
     * An replacement for the {@link org.apache.commons.collections.MapUtils#putAll(Map, Object[])}
     * method, supporting generics.
     * <p>
     * <strong>Use with care:</strong> The method may well support generics, but it is <strong>
     * <u>not runtime typesafe</u></strong>! Because the runtime type of the map is 
     * {@code <Object, Object>}, values not compatible with the generic type declaration can 
     * be inserted (without causing an error!) if the {@code keysAndValues} argument is not 
     * properly constructed.
     * <p>
     * This can lead to tricky {@code ClassCastExceptions}, potentially in very distant parts of
     * the code.

     * @param <T> the type of the target map
     * @param targetMap the map to which the key/value pairs are to be added
     * @param keysAndValues an array of keys and values (the item at index <code>i</code> is treated
     *                      as the key, <code>i+1</code> is the value) to be added
     * @return  a new map containing the keys and values added
     * @throws cf. {@linkplain org.apache.commons.collections.MapUtils#putAll(Map, Object[])}
     * @see #toMap(Map, Object...)
     * @see org.apache.commons.collections.MapUtils#putAll(Map, Object[])
     */
    @SuppressWarnings("unchecked")
    public static <T extends Map> T toMap(T targetMap, Object... keysAndValues) {
        return (T) org.apache.commons.collections.MapUtils.putAll(
                targetMap, keysAndValues);
    }    
    
}

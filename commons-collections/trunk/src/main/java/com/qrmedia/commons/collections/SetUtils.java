/*
 * @(#)SetUtils.java     18 Jul 2008
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides utility methods for sets.
 * 
 * @author anph
 * @since 18 Jul 2008
 *
 */
public class SetUtils {

    /**
     * Returns a set containing the objects passed.
     * <p>
     * If multiple equal objects are passed as input, no guarantee is made
     * regarding which one will be present in the returned set.
     * 
     * @param objects   the objects to be included in the set
     * @return  a set containing the given objects
     */
    public static <U> Set<U> asSet(U... objects) {
        return new HashSet<U>(Arrays.asList(objects));
    }
    
}

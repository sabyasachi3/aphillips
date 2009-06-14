/*
 * @(#)LoggingUtils.java     18 Feb 2009
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
package com.qrmedia.commons.log;

import java.util.Arrays;
import java.util.Collection;

/**
 * Utility methods for logging.
 * 
 * @author anph
 * @since 18 Feb 2009
 *
 */
public class LoggingUtils {
    /**
     * Defines a Java package name as repeated sequences of 
     * &quot;<code><i>lowercase-chars-or-digits-or-underscore</i>.</code>&quot;.
     * <p>
     * Unfortunately, that also matches domain names, so the expression ensures the
     * character <u>before</u> the purported package name is <b>not</b> '/'.
     * This character before the package (if available - it may have been the beginning
     * of the string) is matched and available as matching group $1.
     */
    private static final String DOMAIN_NAME_AVOIDING_PACKAGE_REGEXP = 
        "(^|[^/\\p{javaLowerCase}\\d_\\.])(?:(?:\\p{javaLowerCase}|\\d|_)+\\.)+";
    
    /**
     * Removes any package names from the given input string, or more precisely
     * any repeated sequences of the pattern <code><i>lowercase-chars-or-digits-or-underscore</i>
     * </code>.
     * <p> 
     * Any such pattern preceded by '/' is <b>not</b> removed, to ensure domain names
     * in URIs aren't garbled.
     * 
     * @param input the string from which package names are to be removed
     * @return  the package-name free input string, or <code>null</code> for
     *          <code>null</code> input
     */
    public static String toPackageNameFreeString(Object input) {
        return ((input != null) 
                ? input.toString().replaceAll(DOMAIN_NAME_AVOIDING_PACKAGE_REGEXP, "$1")
                : null);
    }
    
    /**
     * Creates a string representation of the given argument array.
     * <p>
     * Removes package names if specified, i.e. converts <code>a.b.c.Object</code>
     * in the argument string to <code>Object</code>.
     * 
     * @param objs  the array to represent 
     * @param stripPackageNames <code>true</code> if package names are to be stripped from
     *                          the result string 
     * @return  a string representation of the given array
     * @see #arrayToString(Object[], boolean, Integer)
     * @see #collectionToString(Collection, boolean)
     * @see #collectionToString(Collection, boolean, Integer)  
     */
    public static String arrayToString(Object[] objs, boolean stripPackageNames) {
        return arrayToString(objs, stripPackageNames, null);
    }
    
    /**
     * Creates a string representation of the given argument array, up to a maximum length.
     * <p>
     * Removes package names if specified, i.e. converts <code>a.b.c.Object</code>
     * in the argument string to <code>Object</code>.
     * 
     * @param objs  the array to represent 
     * @param stripPackageNames <code>true</code> if package names are to be stripped from
     *                          the result string 
     * @param maxLength the maximum length of the resulting string, or <code>null</code> if the
     *                  string should not be truncated
     * @return  a string representation of the given argument array, no longer than the given
     *          maximum length
     * @throws IllegalArgumentException if maxLength is negative
     * @see #arrayToString(Object[], boolean)        
     * @see #collectionToString(Collection, boolean)
     * @see #collectionToString(Collection, boolean, Integer)
     */
    public static String arrayToString(Object[] objs, boolean stripPackageNames, 
            Integer maxLength) throws IllegalArgumentException {
        
        if ((maxLength != null) && (maxLength < 0)) {
            throw new IllegalArgumentException("'maxLength' must be >= 0");
        }
        
        if (objs == null) {
            return null;
        }
        
        StringBuilder result = new StringBuilder(
                stripPackageNames ? toPackageNameFreeString(Arrays.toString(objs)) 
                                  : Arrays.toString(objs));
        
        // the length restriction only applies if maxLength != null
        int length = result.length();
        return (((maxLength != null) && (length > maxLength)) 
                ? result.replace(maxLength - 3, length, "...").toString()
                : result.toString());
    }    
    
    /**
     * Creates a string representation of the given argument array.
     * <p>
     * Removes package names if specified, i.e. converts <code>a.b.c.Object</code>
     * in the argument string to <code>Object</code>.
     * 
     * @param collection  the collection to represent 
     * @param stripPackageNames <code>true</code> if package names are to be stripped from
     *                          the result string 
     * @return  a string representation of the given array
     * @see #arrayToString(Object[], boolean)
     * @see #arrayToString(Object[], boolean, Integer)
     * @see #collectionToString(Collection, boolean, Integer)
     */
    public static String collectionToString(Collection<?> collection, 
            boolean stripPackageNames) {
        return collectionToString(collection, stripPackageNames, null);
    }    

    /**
     * Creates a string representation of the given argument array, up to a maximum length.
     * <p>
     * Removes package names if specified, i.e. converts <code>a.b.c.Object</code>
     * in the argument string to <code>Object</code>.
     * 
     * @param collection  the collection to represent 
     * @param stripPackageNames <code>true</code> if package names are to be stripped from
     *                          the result string 
     * @param maxLength the maximum length of the resulting string, or <code>null</code> if the
     *                  string should not be truncated
     * @return  a string representation of the given argument array, no longer than the given
     *          maximum length
     * @throws IllegalArgumentException if maxLength is negative
     * @see #arrayToString(Object[], boolean)
     * @see #arrayToString(Object[], boolean, Integer)
     * @see #collectionToString(Collection, boolean)
     */
    public static String collectionToString(Collection<?> collection, boolean stripPackageNames, 
            Integer maxLength) throws IllegalArgumentException {
        String result = arrayToString(((collection != null) ? collection.toArray() : null), 
                                      stripPackageNames, maxLength);
        
        // replace outermost "list" brackets [] with () for collections
        if (result != null) {
            result = result.replaceFirst("^\\[", "\\(").replaceFirst("\\]$", "\\)");
        }
        
        return result;
    }

}

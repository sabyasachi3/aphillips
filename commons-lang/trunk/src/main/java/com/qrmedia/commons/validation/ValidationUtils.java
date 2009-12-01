/*
 * @(#)ValidationUtils.java     11 Jun 2009
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

/**
 * Simple validation methods. See Google's <a href="http://google-collections.googlecode.com/svn/trunk/javadoc/index.html?com/google/common/base/Preconditions.html">
 * Preconditions</a> for a more extensive version.
 * 
 * @author aphillips
 * @since 11 Jun 2009
 *
 */
public final class ValidationUtils {

    /**
     * Checks that objects are not {@code null}.
     *  
     * @param errorMessage      the message of the {@link IllegalArgumentException} that will be
     *                          thrown if any of the objects is {@code null}
     * @param objs      the objects to be checked
     * @throws IllegalArgumentException if any of the objects is {@code null}
     */
    public static void checkNotNull(String errorMessage, Object... objs) {
    
        for (Object obj : objs) {
    
            if (obj == null) {
                throw new IllegalArgumentException(errorMessage);
            }
    
        }
    
    }
    
}

/*
 * @(#)ValidationUtils.java     11 Jun 2009
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

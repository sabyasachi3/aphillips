/*
 * @(#)CollectionEqualsMatcher.java     23 Jan 2009
 *
 * Copyright © 2010 Andrew Phillips.
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
package com.qrmedia.commons.test.easymock;

import static org.easymock.EasyMock.reportMatcher;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

/**
 * Matches a collection supplied by the code to test against a given collection
 * and checks if they are equal.
 *  
 * @author aphillips
 * @see EasyMock#aryEq(Object[])
 * @see CollectionUtils#isEqualCollection(java.util.Collection, java.util.Collection)
 * @since 23 Jan 2009
 *
 */
public class CollectionEqualsMatcher implements IArgumentMatcher {
    private final Collection<?> expectedCollection;
    
    private CollectionEqualsMatcher(Collection<?> expectedCollection) {
        
        if (expectedCollection == null) {
            throw new IllegalArgumentException("Expected collection may not be null");
        }
        
        this.expectedCollection = expectedCollection;
    }
    
    /* (non-Javadoc)
     * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
     */
    public void appendTo(StringBuffer buffer) {
        buffer.append("colEq(").append(expectedCollection).append(")");
    }

    /* (non-Javadoc)
     * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
     */
    public boolean matches(Object object) {
        /*
         * The expected collection is not null, so a null input should return false. Note that
         * instanceof will fail for null input.
         */
        return ((object instanceof Collection<?>) 
                && CollectionUtils.isEqualCollection(expectedCollection, (Collection<?>) object));
    }
    
    /**
     * Factory method to register a matcher which will compare the collection passed
     * by the code under test against the expected collection.
     * 
     * @param expectedCollection    the (non-<code>null</code>) collection expected
     * @return  the given expected collection
     * @see #colEq(Object...)
     */
    public static <T> Collection<T> colEq(Collection<T> expectedCollection) {
        reportMatcher(new CollectionEqualsMatcher(expectedCollection));
        return expectedCollection;
    }

    /**
     * Factory method to register a matcher which will compare the vararg collection passed
     * by the code under test against the expected collection.
     * 
     * @param expectedCollection    the collection expected
     * @return  the given expected collection
     * @see #colEq(Collection)
     */
    public static <T> Collection<T> colEq(T... expectedCollection) {
        return colEq(Arrays.asList(expectedCollection));
    }
    
}

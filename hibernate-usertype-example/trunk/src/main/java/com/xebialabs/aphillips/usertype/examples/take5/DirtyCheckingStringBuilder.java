/*
 * @(#)DirtyCheckingStringBuilder.java     2 Nov 2009
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
package com.xebialabs.aphillips.usertype.examples.take5;

import java.io.Serializable;


/**
 * A stub string builder that only supports appending. Also tracks if the value
 * of the builder has been modified since creation.
 * 
 * @author aphillips
 * @since 2 Nov 2009
 *
 */
public class DirtyCheckingStringBuilder implements Serializable {
    private static final long serialVersionUID = 5014637304842303779L;

    private StringBuilder value;
    private boolean valueModified;

    /**
     * Constructs a new string builder whose initial value is the empty string.
     */
    public DirtyCheckingStringBuilder() {
        this("");
    }
    
    /**
     * Constructs a new string builder with a given initial value.
     * 
     * @param value the builder's initial (non-<code>null</code>!) value
     */
    public DirtyCheckingStringBuilder(String value) {
        assert (value != null);
        this.value = new StringBuilder(value);
        valueModified = false;
    }
    
    /**
     * Appends a value to the builder.
     * 
     * @param addition  the value to add
     * @return  this builder
     */
    public DirtyCheckingStringBuilder append(String addition) {
        value.append(addition);
        valueModified = true;
        return this;
    }
    
    /**
     * @return {@code true} iff the builder's value was modified since creation
     */
    public boolean wasModified() {
        return valueModified;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        System.out.format("EXPENSIVE: Evaluating if this (value: %s) equals %s.%n", value, obj);
        
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DirtyCheckingStringBuilder)) {
            return false;
        }
        
        return value.equals(((DirtyCheckingStringBuilder) obj).value);
    }

    /* (non-Javadoc)
     * @see java.lang.AbstractStringBuilder#toString()
     */
    @Override
    public String toString() {
        return value.toString();
    }

}
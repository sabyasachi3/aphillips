/*
 * @(#)EntityWithStringBuilderProperty.java     8 Apr 2009
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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

/**
 * An object with a property of type {@link StringBuilder}.
 * 
 * @author anphilli
 * @since 8 Apr 2009
 *
 */
@Entity
public class EntityWithStringBuilderProperty {
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @Type(type = "com.xebialabs.aphillips.usertype.examples.take5.DirtyCheckingStringBuilderUserType")
    private DirtyCheckingStringBuilder builder;
    
    /**
     * Creates an entity with the default initial value for the builder.
     */
    public EntityWithStringBuilderProperty() {
        builder = new DirtyCheckingStringBuilder();
    }
    
    /**
     * Creates an entity with an initial value for the builder.
     * 
     * @param initialValue      the builder's initial value
     */
    public EntityWithStringBuilderProperty(String initialValue) {
        builder = new DirtyCheckingStringBuilder(initialValue); 
    }
    
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the builder
     */
    public DirtyCheckingStringBuilder getBuilder() {
        return builder;
    }
    
    /**
     * @param builder the builder to set
     */
    public void setBuilder(DirtyCheckingStringBuilder builder) {
        this.builder = builder;
    }
}


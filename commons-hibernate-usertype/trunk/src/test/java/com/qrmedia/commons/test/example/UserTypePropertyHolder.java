/*
 * @(#)UserTypePropertyHolder.java     8 Apr 2009
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
package com.qrmedia.commons.test.example;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

/**
 * An object with a custom typed property.
 * 
 * @author anphilli
 * @since 8 Apr 2009
 *
 */
@Entity
public class UserTypePropertyHolder {
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @Type(type = "com.qrmedia.commons.persistence.hibernate.usertype.TypesafeObjectUserType")
    private Collection<String> userTypedProperty = new ArrayList<String>();
    
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the userTypedProperty
     */
    public Collection<String> getUserTypedProperty() {
        return userTypedProperty;
    }
}

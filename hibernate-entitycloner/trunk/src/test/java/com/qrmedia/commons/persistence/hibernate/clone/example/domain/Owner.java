/*
 * @(#)Owner.java     31 Mar 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.example.domain;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;

/**
 * The owner of one or more {@link Pet Pets}.
 * 
 * @author aphillips
 * @since 31 Mar 2009
 *
 */
@Entity
public class Owner extends AbstractEntity {
    
    @BusinessField
    @Column(unique = true)
    private String name;
    
    @Column
    private Locale nativeLanguage;
    
    @OneToMany(mappedBy = "owner")
    private Set<Pet> pets = new HashSet<Pet>();

    public Owner(String name, Locale nativeLanguage) {
        this.name = name;
        this.nativeLanguage = nativeLanguage;
    }
    
    public Owner() {}

    /* Getters and setters */

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the nativeLanguage
     */
    public Locale getNativeLanguage() {
        return nativeLanguage;
    }

    /**
     * @param nativeLanguage the nativeLanguage to set
     */
    public void setNativeLanguage(Locale nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }

    /**
     * @return the pets
     */
    public Set<Pet> getPets() {
        return pets;
    }

    /**
     * @param pets the pets to set
     */
    public void setPets(Set<Pet> pets) {
        this.pets = pets;
    }
    
}

/*
 * @(#)StubHibernateEntity.java     8 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone;

import javax.persistence.Id;
import javax.persistence.Version;

/**
 * A stub Hibernate entity object that uses a <em>property</em> (as opposed to a <em>field</em>
 * access strategy).
 * 
 * @author anph
 * @since 8 Feb 2009
 *
 */
public class StubPropertyAccessHibernateEntity {
    private Long id;
    private int version;
    protected String simpleBeanProperty;
    
    /**
     * Constructs a {@code StubPropertyAccessHibernateEntity}.
     * 
     * @param id    the entity's ID
     * @param version the entity's version
     * @param simpleBeanProperty the value of the simple bean property
     */
    public StubPropertyAccessHibernateEntity(Long id, int version, String simpleBeanProperty) {
        this.id = id;
        this.version = version;
        this.simpleBeanProperty = simpleBeanProperty;
    }
    
    /**
     * Default constructor, for convenience in generating objects that don't need
     * specific property values. 
     */
    public StubPropertyAccessHibernateEntity() { }    

    /* Getter(s) and setter(s) */
    
    /**
     * Getter for id.
     *
     * @return the id.
     */
    @Id
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for version.
     *
     * @return the version.
     */
    @Version
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * Getter for simpleBeanProperty.
     *
     * @return the simpleBeanProperty.
     */
    public String getSimpleBeanProperty() {
        return simpleBeanProperty;
    }

    /**
     * @param simpleBeanProperty the simpleBeanProperty to set
     */
    public void setSimpleBeanProperty(String simpleBeanProperty) {
        this.simpleBeanProperty = simpleBeanProperty;
    }

}

/*
 * @(#)EntityGraphLocalizerFactory.java     23 Nov 2008
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

/**
 * A factory for instances of {@link HibernateEntityGraphCloner}. 
 * <p>
 * <b>NB: Requires CGLIB!</b> This class should be final, but then Spring couldn't dynamically
 * implement the {@link #newInstance()} factory method. <br/>
 * For details, see <i>3.3.8.1. Lookup method injection</i> in the Spring reference.
 * 
 * @author anph
 * @since 23 Nov 2008
 *
 */
public abstract class HibernateEntityGraphClonerFactory {

    /**
     * Creates a new {@link HibernateEntityGraphCloner} instance.
     * <p>
     * <i>Spring magic happens here!</i>
     * 
     * @return a new instance of <code>HibernateEntityGraphCloner</code>
     */
    public abstract HibernateEntityGraphCloner newInstance();
}

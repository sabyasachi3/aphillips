/*
 * @(#)GraphWiringCommand.java     9 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.wiring;

import java.util.Collection;
import java.util.IdentityHashMap;

/**
 * An action to be executed to &quot;wire up&quot; (i.e. link) the cloned objects so
 * that their relationships, i.e. their object graph resembles that of the original 
 * entities.
 * 
 * @author anph
 * @since 9 Feb 2009
 * @see GraphPostProcessingCommand
 *
 */
public interface GraphWiringCommand {
    /*
     * Rather than executing the wiring up in commands, it might at first glance appear
     * easier to have a HibernateEntityGraphCloner.registerLink method and allow the
     * entity graph cloner to link the objects once all clones have been created.
     * 
     * This would, however, require the graph cloner to know how to link objects and thus
     * limit the types of possible links to those supported by the cloner.
     * This would represent a strong and avoidable coupling; in any case, the responsibility
     * for wiring up the clones should lie with whoever discovered the link in the first
     * place! The command pattern allows the discoverer of the link to wire up items in any
     * way necessary (by creating a suitable subclass of GraphWiringCommand).
     */
    
    /**
     * Indicates the list of original entities whose clones the command needs to be
     * able to perform its task. 
     * <p>
     * For instance, if the task is to set a property to the clone of the original
     * value, that original value would be returned here.
     * <p>
     * The map passed to the {@link #execute(IdentityHashMap)} method will contain an 
     * &quot;original entity/clone&quot; pair for each entity returned here.
     * 
     * @return  a collection of all entities whose clones are required in 
     *          {@link #execute(IdentityHashMap)}
     */
    Collection<Object> forEntities();
    
    /**
     * Carries out the command. An {@link IdentityHashMap} is used instead of the more
     * generic <code>Map</code> because there may be multiple original entities that are
     * <i>equal</i> but not <i>identical</i>. 
     * 
     * @param entityClones  a map of &quot;original entity/clone&quot; pairs whose keys
     *                      are the entities returned by {@link #forEntities()}
     */
    void execute(IdentityHashMap<Object, Object> entityClones);
}
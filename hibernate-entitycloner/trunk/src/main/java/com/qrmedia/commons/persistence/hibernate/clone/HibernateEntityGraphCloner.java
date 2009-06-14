/*
 * @(#)HibernateEntityGraphCloner.java     8 Feb 2009
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.qrmedia.commons.collections.Pair;
import com.qrmedia.commons.collections.PairUtils;
import com.qrmedia.commons.graph.traverser.BreadthFirstGraphTraverser;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.GraphPostProcessingCommand;
import com.qrmedia.commons.persistence.hibernate.clone.wiring.GraphWiringCommand;

/**
 * &quot;Deep&quot; clones a graph of Hibernate entities (i.e. the input objects and
 * all linked entities).
 * <p>
 * The clone(s) will, by default, not have IDs and will appear as &quot;fresh&quot; objects to 
 * Hibernate. They can be saved and/or modified without affecting the original objects. The entities'
 * IDs may be preserved, if desired.
 * <p>
 * <strong>Note:</strong> ID removal/preservation currently only supports <em>field</em>
 * annotation and <em>getter</em>, but not XML-based configuration. 
 * See the <a href="http://docs.jboss.org/hibernate/stable/annotations/reference/en/html_single/#entity-mapping-entity">
 * Hibernate documentation</a> for information on field and getter access types.
 *  
 * @author anph
 * @see HibernateEntityBeanCloner
 * @since 8 Feb 2009
 *
 */
public class HibernateEntityGraphCloner extends BreadthFirstGraphTraverser<EntityPreserveIdFlagPair, IdentityHashMap<Object, Object>> {
    private List<GraphWiringCommand> graphWiringCommands = 
        new ArrayList<GraphWiringCommand>();
    private List<GraphPostProcessingCommand> graphPostProcessingCommands = 
        new ArrayList<GraphPostProcessingCommand>();
    
    private boolean preserveId;
    
    /**
     * Deep clones an entity, ignoring IDs.
     * 
     * @param <T>   the type of the entity
     * @param entity    the non-<code>null</code> entity to clone
     * @return  a deep clone of the entity
     * @see #clone(Object, boolean)
     * @see #clone(Collection)
     * @see #clone(Collection, boolean)
     */
    public <T> T clone(T entity) {
        return clone(entity, false);
    }
    
    /**
     * Deep clones an entity, optionally preserving ID fields.
     * 
     * @param <T>   the type of the entity
     * @param entity    the non-<code>null</code> entity to clone
     * @param preserveId    <code>true</code> iff ID fields are to be preserved
     * @return  a deep clone of the entity
     * @see #clone(Object)
     * @see #clone(Collection)
     * @see #clone(Collection, boolean)
     */
    public <T> T clone(T entity, boolean preserveId) {
        return clone(asList(entity), preserveId).get(entity);
    }
    
    /**
     * Deep clones a collection of entities, ignoring IDs.
     * 
     * @param <T>   the type of the entity collection
     * @param entities  the non-<code>null</code> collection of entities to be cloned
     * @return  a map of given entities to their clones
     * @see #clone(Object)
     * @see #clone(Object, boolean)
     * @see #clone(Collection, boolean)
     */
    public <T> Map<T, T> clone(Collection<T> entities) {
        return clone(entities, false);
    }
    
    /**
     * Deep clones a collection of entities, optionally preserving ID fields.
     * 
     * @param <T>   the type of the entity collection
     * @param entities  the non-<code>null</code> collection of entities to be cloned
     * @param preserveId    <code>true</code> iff ID fields are to be preserved
     * @return  a map of given entities to their clones
     * @see #clone(Object)
     * @see #clone(Object, boolean)
     * @see #clone(Collection)
     */
    public <T> Map<T, T> clone(Collection<T> entities, boolean preserveId) {
        
        // store the ID preserve/ignore flag for use in addNode
        this.preserveId = preserveId;
        
        IdentityHashMap<Object, Object> entityClones = new IdentityHashMap<Object, Object>();
        traverseFrom(PairUtils.<EntityPreserveIdFlagPair, Object, Boolean>toPairs(
                        EntityPreserveIdFlagPair.class, entities, preserveId), 
                     entityClones);
        
        // now entityClones contains a map from all discovered objects to their clones
        
        for (GraphWiringCommand command : graphWiringCommands) {
            command.execute(extractSubset(command.forEntities(), entityClones));
        }
        
        for (GraphPostProcessingCommand command : graphPostProcessingCommands) {
            command.execute();
        }
        
        // clean up (visitedOrQueuedNodes is cleaned up by the AbstractNodeIteratingGraphTraverser)
        graphWiringCommands.clear();
        graphPostProcessingCommands.clear();
        
        return extractSubset(entities, entityClones);
    }

    @SuppressWarnings("unchecked")
    private static <T> IdentityHashMap<T, T> extractSubset(Collection<T> keys, 
            IdentityHashMap<Object, Object> map) {
        
        // returns a submap containing only the entries corresponding to the given keys
        IdentityHashMap<T, T> subset = new IdentityHashMap<T, T>();
        
        for (T key : keys) {
            assert map.containsKey(key) : new Pair<T, Object>(key, map);

            // XXX: How to assert that the map entry is also an instance of T?? 
            subset.put(key, (T) map.get(key));
        }
        
        return subset;
    }    
    
    /**
     * Registers an entity for cloning. The entity's IDs will be preserved iff the
     * the entity is being cloned as part of an entity graph for which the initial
     * <code>clone</code> was passed a &quot;preserveId&quot; value of <code>true</code>.
     * 
     * @param entity    the entity to be cloned
     * @see #clone(Object)
     * @see #clone(Object, boolean)
     */
    public void addEntity(Object entity) {
        addNode(new EntityPreserveIdFlagPair(entity, preserveId));
    }

    /**
     * Adds a command to the list of &quot;wiring-up&quot; commands. These will be
     * executed <u>before</u> all post-processing commands, in the order they were
     * added. 
     * 
     * @param command   the command to be added
     * @see #addGraphPostProcessingCommand(GraphPostProcessingCommand)
     */
    public void addGraphWiringCommand(GraphWiringCommand command) {
        graphWiringCommands.add(command);
    }
    
    /**
     * Adds a command to the list of post-processing commands. These will be
     * executed <u>after</u> all &quot;wiring-up&quot; commands.
     * 
     * @param command   the command to be added
     * @see #addGraphWiringCommand(GraphWiringCommand)
     */
    public void addGraphPostProcessingCommand(GraphPostProcessingCommand command) {
        graphPostProcessingCommands.add(command);
    }
    
    /* Getter(s) and setter(s) */
    
    /**
     * @param entityBeanCloner the entityBeanCloner to set
     */
    @Required
    public void setEntityBeanCloner(HibernateEntityBeanCloner entityBeanCloner) {
        addNodeVisitor(entityBeanCloner);
    }    

}
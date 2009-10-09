/*
 * @(#)HibernateEntityBeanCloner.java     7 Feb 2009
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Id;
import javax.persistence.Version;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Required;

import com.qrmedia.commons.collections.Pair;
import com.qrmedia.commons.graph.traverser.GraphTraverser;
import com.qrmedia.commons.graph.traverser.NodeVisitor;
import com.qrmedia.commons.lang.ClassUtils;
import com.qrmedia.commons.persistence.hibernate.clone.property.BeanPropertyCloner;

/**
 * Creates a clone of the visited Hibernate entity, using a configurable list of
 * {@link BeanPropertyCloner BeanPropertyCloners} to process individual properties.
 * <p>
 * Fields that are not valid read-/writable bean properties (i.e. do not have a public 
 * getters and setters) and static fields are ignored, and the clones will thus have default 
 * values for these fields.<br/>
 * The entities to be cloned (i.e. the visited entity and all non-simple properties) must also 
 * have a public no-argument constructor.
 * <p>
 * Property cloners are called in the order of the {@link #setPropertyCloners(List) propertyCloners}
 * list, and once a cloner indicates that is has successfully processed the property,
 * no further cloners are calls. So ensure &quot;catch-all&quot; cloners are at the
 * end of the list! 
 * <p>
 * Expects a <code>HibernateEntityGraphCloner</code> to be passed as the second argument to 
 * {@link #visitNode(EntityPreserveIdFlagPair, GraphTraverser, IdentityHashMap)}.
 * 
 * @author anph
 * @see HibernateEntityGraphCloner
 * @since 7 Feb 2009
 *
 */
public class HibernateEntityBeanCloner implements NodeVisitor<EntityPreserveIdFlagPair, IdentityHashMap<Object, Object>> {
    
    // a pattern extracting the first (uppercase) letter of the field name from the getter
    private static final Pattern GETTER_PREFIX = Pattern.compile("get(\\p{Upper})(\\p{Alpha}*)");
    
    private static final Map<Pair<Class<?>, Boolean>, Collection<String>> TARGETED_FIELD_NAME_CACHE =
        Collections.synchronizedMap(new HashMap<Pair<Class<?>, Boolean>, Collection<String>>());

    /**
     * A section of the class name that identifies CGLIB-generated classes.
     */
    /*
     * XXX: This is rather brittle, of course, and does not work with other dynamic 
     * instantiation libraries.
     */
    private static final String CGLIB_CLASS_NAME_IDENTIFIER = "$$EnhancerByCGLIB$$";
    
    private List<BeanPropertyCloner> propertyCloners;

    /* (non-Javadoc)
     * @see com.qrmedia.commons.graph.NodeVisitor#visitNode(java.lang.Object, com.qrmedia.commons.graph.GraphTraverser, java.lang.Object)
     */
    public boolean visitNode(EntityPreserveIdFlagPair entityPreserveIdFlagPair,
            GraphTraverser<EntityPreserveIdFlagPair, IdentityHashMap<Object, Object>> graphTraverser,
            IdentityHashMap<Object, Object> entityClones) {

        if (!(graphTraverser instanceof HibernateEntityGraphCloner)) {
            throw new IllegalArgumentException(graphTraverser 
                    + " is not an instance of HibernateEntityGraphCloner");            
        }
        
        Object entity = entityPreserveIdFlagPair.getEntity();
        Object clone;
        
        try {
            // ensure the "real" class is instantiated if the current instance is a CGLIB class
            clone = getEntityClass(entity).newInstance();
            
            for (String targetedFieldName 
                    : getTargetedFieldNames(entity, entityPreserveIdFlagPair.isPreserveId())) {
                cloneProperty(entity, clone, targetedFieldName, 
                              (HibernateEntityGraphCloner) graphTraverser);
            }
        
        } catch (Exception exception) {
            throw new AssertionError("Unable to clone entity " + entity + " due to " 
                    + exception.getClass().getSimpleName() + ": " + exception.getMessage());
        }
        
        entityClones.put(entity, clone);
        return true;
    }
    
    // if the actual class is a "magic" CGLIB subclass, use the "real" parent
    private static Class<?> getEntityClass(Object entity) {
        Class<?> entityClass = entity.getClass();
        
        // walk up the superclass try until a non-CGLIB class is found (will stop at Object!)
        while (entityClass.getName().contains(CGLIB_CLASS_NAME_IDENTIFIER)) {
            entityClass = entityClass.getSuperclass();
        }
        
        return entityClass;
    }

    // cache these values as they are (CGLIB and AOP magic aside) static per class
    private static Collection<String> getTargetedFieldNames(Object entity, boolean preserveIdFields) {
        Pair<Class<?>, Boolean> cacheKey = 
            new Pair<Class<?>, Boolean>(entity.getClass(), Boolean.valueOf(preserveIdFields));
        
        if (!TARGETED_FIELD_NAME_CACHE.containsKey(cacheKey)) {
            TARGETED_FIELD_NAME_CACHE.put(cacheKey, 
                    calculateTargetedFieldNames(entity, preserveIdFields));
        }
        
        return TARGETED_FIELD_NAME_CACHE.get(cacheKey);
    }
    
    private static Collection<String> calculateTargetedFieldNames(Object entity,
            boolean preserveIdFields) {
        Collection<String> targetedFieldNames = new ArrayList<String>();
        Class<?> entityClass = entity.getClass();

        for (Field field : ClassUtils.getAllDeclaredFields(entityClass)) {
            String fieldName = field.getName();

            // ignore static members and members without a valid getter and setter
            if (!Modifier.isStatic(field.getModifiers()) 
                    && PropertyUtils.isReadable(entity, fieldName) 
                    && PropertyUtils.isWriteable(entity, fieldName)) {
                targetedFieldNames.add(field.getName());
            }
                
        }
        
        /*
         * Assume that, in accordance with recommendations, entities are using *either* JPA property
         * *or* field access. Guess the access type from the location of the @Id annotation, as
         * Hibernate does.
         */
        Set<Method> idAnnotatedMethods = ClassUtils.getAnnotatedMethods(entityClass, Id.class);
        boolean usingFieldAccess = idAnnotatedMethods.isEmpty();
        
        // ignore fields annotated with @Version and, optionally, @Id
        targetedFieldNames.removeAll(usingFieldAccess 
                ? getFieldNames(ClassUtils.getAllAnnotatedDeclaredFields(entityClass, Version.class)) 
                : getPropertyNames(ClassUtils.getAnnotatedMethods(entityClass, Version.class)));

        if (!preserveIdFields) {
            targetedFieldNames.removeAll(usingFieldAccess 
                    ? getFieldNames(ClassUtils.getAllAnnotatedDeclaredFields(entityClass, Id.class))
                    : getPropertyNames(idAnnotatedMethods));
        }
        
        return targetedFieldNames;
    }
    
    private static Collection<String> getFieldNames(Collection<Field> fields) {
        Collection<String> fieldNames = new ArrayList<String>(fields.size());
        
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        
        return fieldNames;
    }
    
    private static Collection<String> getPropertyNames(Collection<Method> methods) {
        Collection<String> methodNames = new ArrayList<String>();

        /*
         * If a method is an instance method, does not return void, takes no parameters 
         * and is named "get..." (it's assumed to be public), add the corresponding field name.
         */
        for (Method method : methods) {
            assert Modifier.isPublic(method.getModifiers()) : method;
            String methodName = method.getName();
            Matcher getterNameMatcher = GETTER_PREFIX.matcher(methodName);

            if (!Modifier.isStatic(method.getModifiers())
                    && (method.getReturnType() != Void.class)
                    && (method.getParameterTypes().length == 0)
                    && getterNameMatcher.matches()) {
           
                // the first group is the (uppercase) first letter of the field name
                methodNames.add(getterNameMatcher.replaceFirst(
                        getterNameMatcher.group(1).toLowerCase() + "$2"));
            }
            
        }

        return methodNames;
    }    

    private void cloneProperty(Object source, Object target,
            String propertyName, HibernateEntityGraphCloner entityGraphCloner) {
        
        // cloners that cannot deal with the type of property will ignore it 
        for (BeanPropertyCloner propertyCloner : propertyCloners) {
            
            // stop once a cloner indicates that the property was processed
            if (propertyCloner.clone(source, target, propertyName, 
                                     (HibernateEntityGraphCloner) entityGraphCloner)) {
                return;
            }
            
        }
        
    }
    
    /* Setter(s) */

    /**
     * @param propertyCloners the propertyCloners to set
     */
    @Required
    public void setPropertyCloners(List<BeanPropertyCloner> propertyCloners) {
        this.propertyCloners = propertyCloners;
    }    

}

/*
 * @(#)MultiSpi.java     4 Dec 2010
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
package com.qrmedia.commons.multispi;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.qrmedia.commons.multispi.config.MultiSpiBuilder;
import com.qrmedia.commons.multispi.provider.ServiceImplementationProvider;


/**
 * One-stop shop for users of MultiSpi. Instances should be created via dependency
 * injection or using the {@link MultiSpiBuilder}.
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
@ThreadSafe
public final class MultiSpi {
    private final Set<ServiceImplementationProvider> providers = newHashSet();
    
    @Inject
    public MultiSpi(@Nonnull Set<ServiceImplementationProvider> providers) {
        this.providers.addAll(providers);
    }
    
    /**
     * Finds the names of classes implementing the requested service, as determined
     * by the available providers.
     * <p>
     * Note that individual providers may or may <u>not</u> guarantee that the classes
     * returned <em>actually</em> implement the interface!
     * 
     * @param serviceClass the class of the service
     * @return a set of names of classes designated as implementing the requested service
     */
    public @Nonnull Set<String> findImplementationNames(@Nonnull final Class<?> serviceClass) {
        return newHashSet(concat(transform(providers, new Function<ServiceImplementationProvider, Set<String>>() {
                public Set<String> apply(ServiceImplementationProvider from) {
                    return from.findServiceImplementations(serviceClass);
                }
            })));
    }
    
    /**
     * Shorthand for {@link #findImplementations(Class, ClassLoader) findImplementations(serviceClass, MultiSpi.class.getClassLoader())}.
     * 
     * @param <S> the type of the service
     * @param serviceClass the class of the service
     * @return a set of classes of implementations of the service
     * @throws ClassCastException if any of the classes are not actually implementations
     *                            of the requested service
     * @throws ClassNotFoundException if any of the classes cannot be loaded    
     * @see #findImplementations(Class, ClassLoader)
     */
    public @Nonnull <S> Set<Class<? extends S>> findImplementations(
            @Nonnull Class<S> serviceClass) throws ClassCastException, ClassNotFoundException {
        return findImplementations(serviceClass, MultiSpi.class.getClassLoader());
    }
    
    /**
     * Finds the classes implementing the requested service, as determined by the 
     * available providers.
     * <p>
     * This method will check whether the classes found do, in fact, implement the
     * requested service, and a {@code ClassCastException} will be thrown if any
     * of the classes does not.
     * <p>
     * If more lenient behaviour is desired (e.g. skipping any classes that cannot be
     * found, or do not actually implement the interface), use 
     * {@link #findImplementationNames(Class)} instead.
     * 
     * @param <S> the type of the service
     * @param serviceClass the class of the service
     * @param implementationLoader the class loader to be used to attempt to load the classed
     * @return a set of classes of implementations of the service
     * @throws ClassCastException if any of the classes are not actually implementations
     *                            of the requested service
     * @throws ClassNotFoundException if any of the classes cannot be loaded                           
     * @see #findImplementations(Class)
     */
    @SuppressWarnings("unchecked")
    public @Nonnull <S> Set<Class<? extends S>> findImplementations(
            @Nonnull Class<S> serviceClass, @Nonnull final ClassLoader implementationLoader) 
            throws ClassCastException, ClassNotFoundException {
        Set<Class<? extends S>> implementations = newHashSet();
        // can't do this using Iterables.transform because we want to throw the exception
        for (String name : findImplementationNames(serviceClass)) {
            implementations.add((Class<? extends S>) implementationLoader.loadClass(name));
        }
        
        if (any(implementations, not(assignableFrom(serviceClass)))) {
            throw new ClassCastException(format("Supposed implementations '%s' does/do not implement or extend service class '%s'",
                    filter(implementations, not(assignableFrom(serviceClass))), serviceClass.getName()));
        }
        return implementations;
    }
    
    private static AssignableFromPredicate assignableFrom(Class<?> superclass) {
        return new AssignableFromPredicate(superclass);
    }

    private static class AssignableFromPredicate implements Predicate<Class<?>> {
        private final Class<?> superclass;
        
        public AssignableFromPredicate(Class<?> superclass) {
            this.superclass = superclass;
        }

        public boolean apply(Class<?> input) {
            return superclass.isAssignableFrom(input);
        }
    }
}

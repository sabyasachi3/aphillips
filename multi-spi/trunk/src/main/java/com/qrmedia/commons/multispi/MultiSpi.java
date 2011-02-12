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
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static java.lang.String.format;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.qrmedia.commons.multispi.config.MultiSpiBuilder;
import com.qrmedia.commons.multispi.provider.ServiceImplementationProvider;


/**
 * One-stop shop for users of MultiSpi. Instances should be created via dependency
 * injection or using the {@link MultiSpiBuilder}.
 *
 * @author aphillips
 * @see ServiceLoader
 * @since 4 Dec 2010
 *
 */
@ThreadSafe
public final class MultiSpi {
    private final Set<ServiceImplementationProvider> providers = newHashSet();
    // ready for @Inject
    private final InstanceFactory instanceFactory = new PublicNoargConstructorInstanceFactory();
    private final ClassLoaderLocator loaderLocator = new ClassLoaderLocator();
    
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
     * Shorthand for {@link #findImplementations(Class, ClassLoader) findImplementations(serviceClass, defaultClassLoader)}
     * where {@code defaultClassLoader} is
     * <ul>
     * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()} if non-<code>null</code>
     * <li>{@link ClassLoader#getSystemClassLoader()} if non-<code>null</code>
     * <li>the bootstrap classloader otherwise
     * </ul>
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
        return findImplementations(serviceClass, findDefaultLoader());
    }
    
    private ClassLoader findDefaultLoader() {
        return find(newArrayList(Thread.currentThread().getContextClassLoader(),
                loaderLocator.getSystemClassLoader(), loaderLocator.getBootstrapClassLoader()), notNull());
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
     * @param implementationLoader the class loader to be used to attempt to load the classes
     * @return a set of instances of implementations of the service
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

    // TODO: move to Predicates
    private static class AssignableFromPredicate implements Predicate<Class<?>> {
        private final Class<?> superclass;
        
        public AssignableFromPredicate(Class<?> superclass) {
            this.superclass = superclass;
        }

        public boolean apply(Class<?> input) {
            return superclass.isAssignableFrom(input);
        }
    }
    
    /**
     * Shorthand for {@link #loadImplementations(Class, ClassLoader) loadImplementations(serviceClass, defaultClassLoader)}.
     * where {@code defaultClassLoader} is
     * <ul>
     * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()} if non-<code>null</code>
     * <li>{@link ClassLoader#getSystemClassLoader()} if non-<code>null</code>
     * <li>the bootstrap classloader otherwise
     * </ul>
     * 
     * @param <S> the type of the service
     * @param serviceClass the class of the service
     * @return a set of instances of implementations of the service
     * @throws ClassNotFoundException if any of the classes cannot be loaded 
     * @throws InstantiationException if any of the classes are not actually implementations
     *          of the requested service, or cannot be instantiated using a public no-argument 
     *          constructor                   
     * @see #loadImplementations(Class, ClassLoader)
     */
    public @Nonnull <S> Set<S> loadImplementations(@Nonnull Class<S> serviceClass) 
            throws ClassNotFoundException, InstantiationException {
        return loadImplementations(serviceClass, findDefaultLoader());
    }

    /**
     * Finds the classes implementing the requested service, as determined by the 
     * available providers, and loads an instance of each implementation.
     * <p>
     * This method will check whether the classes found do, in fact, implement the
     * requested service, and an {@code InstantiationException} will be thrown if any
     * of the classes does not.
     * <p>
     * Service classes are expected to provide a public, no-argument constructor, which
     * will be invoked to create instances of the implementation classes found. An
     * {@code InstantiationException} is thrown if the class cannot be instantiated.
     * <p>
     * If more lenient behaviour is desired (e.g. skipping any classes that cannot be
     * found, or do not actually implement the interface), use 
     * {@link #findImplementationNames(Class)} instead.
     * 
     * @param <S> the type of the service
     * @param serviceClass the class of the service
     * @param implementationLoader the class loader to be used to attempt to load the classes
     * @return a set of instances of implementations of the service
     * @throws ClassNotFoundException if any of the classes cannot be loaded 
     * @throws InstantiationException if any of the classes are not actually implementations
     *          of the requested service, or cannot be instantiated using a public no-argument 
     *          constructor                   
     * @see #loadImplementations(Class)
     */
    public @Nonnull <S> Set<S> loadImplementations(@Nonnull Class<S> serviceClass, 
            @Nonnull final ClassLoader implementationLoader) throws ClassNotFoundException, 
                InstantiationException {
        Set<Class<? extends S>> implementationClasses;
        try {
            implementationClasses = findImplementations(serviceClass, implementationLoader);
        } catch (ClassCastException exception) {
            throw new InstantiationException(format("Unable to load implementation classes for service class '%s' due to: %s",
                    serviceClass, exception.getMessage()));
        }
        
        Set<S> implementations = newHashSetWithExpectedSize(implementationClasses.size());
        // can't use Iterables.transform because we want to throw the exception
        for (Class<? extends S> implementationClass : implementationClasses) {
            implementations.add(instanceFactory.newInstance(implementationClass));
        }
        return implementations;
    }
    
    // ready to be factored out into an external interface with pluggable implementations
    private static interface InstanceFactory {
        @Nonnull <T> T newInstance(@Nonnull Class<T> clazz) throws InstantiationException;
    }
    
    private static class PublicNoargConstructorInstanceFactory implements InstanceFactory {
        public <T> T newInstance(Class<T> clazz) throws InstantiationException {
            Constructor<T> publicNoargConstructor;
            try {
                publicNoargConstructor = clazz.getConstructor();
            } catch (NoSuchMethodException exception) {
                throw new InstantiationException(format("Unable to find public no-argument constructor for class '%s' due to: %s",
                        clazz, exception.getMessage()));
            }
            
            try {
                return publicNoargConstructor.newInstance();
            } catch (InvocationTargetException exception) {
                throw new InstantiationException(format("Unable to create new instance of class '%s' due to exception in constructor '%s': %s: %s",
                        clazz, publicNoargConstructor, exception.getCause(), exception.getCause().getMessage()));
            } catch (IllegalAccessException exception) {
                // should not happen - the constructor takes no args and we're calling it with no args
                throw new AssertionError(format("Caught IllegalAccessException '%s' calling public constructor '%s' for class '%s'?!?", 
                        exception.getMessage(), publicNoargConstructor, clazz));
            } catch (IllegalArgumentException exception) {
                // should not happen - the constructor takes no args and we're calling it with no args
                throw new AssertionError(format("Caught IllegalArgumentException '%s' calling no-argument constructor '%s' for class '%s' with no arguments?!?", 
                        exception.getMessage(), publicNoargConstructor, clazz));
            }
        }
    }
    
    @VisibleForTesting
    @ThreadSafe
    static class ClassLoaderLocator {
        private static final ClassLoader BOOTSTRAP_LOADER = new BootstrapClassLoader();
        
        ClassLoader getSystemClassLoader() {
            return ClassLoader.getSystemClassLoader();
        }
        
        @Nonnull ClassLoader getBootstrapClassLoader() {
            return BOOTSTRAP_LOADER;
        }
        
        private static class BootstrapClassLoader extends ClassLoader {
            private BootstrapClassLoader() {
                /*
                 * The default classloader implementation will use the bootstrap loader
                 * if it finds a null parent.
                 */
                super(null);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return providers.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MultiSpi other = (MultiSpi) obj;
        if (!providers.equals(other.providers))
            return false;
        return true;
    }
}

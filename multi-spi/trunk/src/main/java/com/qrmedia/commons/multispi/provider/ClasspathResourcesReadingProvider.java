/*
 * @(#)ClasspathResourcesReadingProvider.java     5 Dec 2010
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
package com.qrmedia.commons.multispi.provider;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * A {@link ResourceIoPerformingProvider} that reads all resources of a specified name
 * (as returned by {@link ClassLoader#getResources(String)} and combines the results from
 * processing each one.
 * 
 * @author aphillips
 * @since 5 Dec 2010
 *
 * @param <T> the type of the resource that is read from the classpath and then processed
 */
abstract class ClasspathResourcesReadingProvider<T> extends ResourceIoPerformingProvider {
    protected final Function<? super Class<?>, String> resourceNameGenerator;
    protected final IoFunction<URL, T> resourceReader;
    
    protected static interface IoFunction<F, T> {
        T apply(@Nonnull F item) throws IOException;
    }

    protected ClasspathResourcesReadingProvider(
            @Nonnull Function<? super Class<?>, String> resourceNameGenerator,
            @Nonnull IoFunction<URL, T> resourceReader) {
        this.resourceNameGenerator = resourceNameGenerator;
        this.resourceReader = resourceReader;
    }

    /* (non-Javadoc)
     * @see com.qrmedia.commons.multispi.provider.ResourceIoPerformingProvider#findServiceImplementationsWithIo(java.lang.Class)
     */
    @Override
    protected Set<String> findServiceImplementationsWithIo(final Class<?> serviceClass)
            throws IOException {
        List<T> resources = newArrayList();
        for (Enumeration<URL> urls = ClasspathResourcesReadingProvider.class.getClassLoader()
                    .getResources(resourceNameGenerator.apply(serviceClass)); urls.hasMoreElements();) {
            resources.add(resourceReader.apply(urls.nextElement()));
        }
        return ImmutableSet.copyOf(concat(transform(resources, 
                new Function<T, Set<String>>() {
                    public Set<String> apply(T from) {
                        return processResource(from, serviceClass);
                    }
                })));        
    }

    protected abstract @Nonnull Set<String> processResource(T resource, Class<?> serviceClass);
}

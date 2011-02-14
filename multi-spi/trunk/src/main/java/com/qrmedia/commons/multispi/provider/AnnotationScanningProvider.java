/*
 * @(#)AnnotationScanningProvider.java     4 Dec 2010
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

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.intersection;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.reflections.Reflections;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Scans files in a base package and its subpackages on the classpath for classes with
 * a certain marker annotation. The marker annotation needs to have {@code RUNTIME} 
 * {@link RetentionPolicy retention}.
 * <p>
 * Only classes that actually implement the requested service are returned.
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
@ThreadSafe
public class AnnotationScanningProvider implements ServiceImplementationProvider {
    private final Class<? extends Annotation> markerAnnotation;
    private final String basePackage;
    private final Reflections reflections;
    
    public AnnotationScanningProvider(Class<? extends Annotation> markerAnnotation, 
            String basePackage) {
        this.markerAnnotation = markerAnnotation;
        this.basePackage = basePackage;
        reflections = new Reflections(basePackage);
    }

    public Set<String> findServiceImplementations(Class<?> serviceClass,
            ClassLoader classpathResourceLoader) {
        return ImmutableSet.copyOf(transform(intersection(
                reflections.getTypesAnnotatedWith(markerAnnotation), 
                reflections.getSubTypesOf(serviceClass)), new ClassToName()));
    }
    
    // move to some util
    private static class ClassToName implements Function<Class<?>, String> {
        public String apply(Class<?> from) {
            return from.getName();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((basePackage == null) ? 0 : basePackage.hashCode());
        result = prime
                * result
                + ((markerAnnotation == null) ? 0 : markerAnnotation.hashCode());
        return result;
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
        AnnotationScanningProvider other = (AnnotationScanningProvider) obj;
        if (basePackage == null) {
            if (other.basePackage != null)
                return false;
        } else if (!basePackage.equals(other.basePackage))
            return false;
        if (markerAnnotation == null) {
            if (other.markerAnnotation != null)
                return false;
        } else if (!markerAnnotation.equals(other.markerAnnotation))
            return false;
        return true;
    }

}

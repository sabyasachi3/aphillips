/*
 * @(#)ServiceImplementationProvider.java     4 Dec 2010
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

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Finds implementations of a service interface or abstract class.
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
public interface ServiceImplementationProvider {

    /**
     * Returns a set of fully-qualified class names of implementations of the
     * given service class. Is not required to check if the classes returned
     * <em>actually</em> implement the requested interface or abstract class. 
     * Never returns {@code null}.
     * 
     * @param serviceClass the (non-<code>null</code>) requested service class
     * @return a non-<code>null</code> set of fully-qualified class names of presumed
     *         implementations of the requested service
     */
    @Nonnull Set<String> findServiceImplementations(@Nonnull Class<?> serviceClass); 
}

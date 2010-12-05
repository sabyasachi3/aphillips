/*
 * @(#)MetaInfServicesProvider.java     4 Dec 2010
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

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

/**
 * Reads service implementation classes from a Provider-Configuration file in 
 * META-INF/services as defined in the <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">Java SE specification</a>.
 * <p>
 * Does <strong>not</strong> check if the classes listed in the configuration file
 * <em>actually</em> implement the requested interface!
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
@ThreadSafe
public class MetaInfServicesProvider extends ClasspathResourcesReadingProvider<List<String>> {
    private static final String PROVIDER_FILE_DIR = "META-INF/services";
    private static final Charset PROVIDER_FILE_CHARSET = Charset.forName("UTF-8");
    private static final char COMMENT_CHARACTER = '#';
    
    public MetaInfServicesProvider() {
        super(new Function<Class<?>, String>() {
            public String apply(Class<?> from) {
                return PROVIDER_FILE_DIR + '/' + from.getName();
            }
        }, new IoFunction<URL, List<String>>() {
            public List<String> apply(URL item) throws IOException {
                return Resources.readLines(item, PROVIDER_FILE_CHARSET);
            }
        });
    }
    
    protected Set<String> processResource(List<String> lines, Class<?> serviceClass) {
        return ImmutableSet.copyOf(filter(
                transform(lines, compose(new StripChars(' ', '\t'), new StripComments())), 
                not(equalTo(StringUtils.EMPTY))));
    }
    
    private static class StripComments implements Function<String, String> {
        public String apply(String from) {
            return StringUtils.substringBefore(from, String.valueOf(COMMENT_CHARACTER));
        }
    }
    
    // move to StringFunctions
    private static class StripChars implements Function<String, String> {
        private final char[] charsToRemove;
        
        private StripChars(char... charsToRemove) {
            this.charsToRemove = charsToRemove;
        }

        public String apply(String from) {
            return StringUtils.strip(from, String.valueOf(charsToRemove));
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return MetaInfServicesProvider.class.hashCode();
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
        return true;
    }

}

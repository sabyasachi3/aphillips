/*
 * @(#)CollectionEqualsMatcherDemo.java     10 Nov 2009
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
package com.xebia.aphillips.easymock;

import static com.xebia.aphillips.easymock.CollectionEqualsMatcher.colEq;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.easymock.Capture;
import org.junit.Test;

/**
 * Demo of the {@link CollectionEqualsMatcher}.
 */
public class CollectionEqualsMatcherDemo {

    private static interface StubTarget {
        void method(Collection<String> args);
    }
    
    @Test
    public void directMatch() {
        StubTarget mock = createMock(StubTarget.class);
        String arg = "007";
        mock.method(Arrays.asList(arg));
        expectLastCall();
        replay(mock);
        
        Set<String> args = new HashSet<String>();
        args.add(arg);
        mock.method(args);
        
        verify(mock);
    }
    
    @Test
    public void usingCapture() {
        StubTarget mock = createMock(StubTarget.class);
        String arg = "007";
        Capture<Collection<String>> argsCapture = new Capture<Collection<String>>();
        mock.method(capture(argsCapture));
        expectLastCall();
        replay(mock);
        
        Set<String> args = new HashSet<String>();
        args.add(arg);
        mock.method(args);

        verify(mock);
        assertTrue("Expected equal collections", CollectionUtils.isEqualCollection(
                Arrays.asList(arg), argsCapture.getValue()));
    }
    
    @Test
    public void usingMatcher() {
        StubTarget mock = createMock(StubTarget.class);
        String arg = "007";
        mock.method(colEq(Arrays.asList(arg)));
        expectLastCall();
        replay(mock);
        
        Set<String> args = new HashSet<String>();
        args.add(arg);
        mock.method(args);
        
        verify(mock);
    }
    
    @Test
    public void usingVarargMatcher() {
        StubTarget mock = createMock(StubTarget.class);
        String arg1 = "007";
        String arg2 = "008";
        mock.method(colEq(arg1, arg2));
        expectLastCall();
        replay(mock);
        
        Set<String> args = new HashSet<String>();
        args.add(arg2);
        args.add(arg1);
        mock.method(args);
        
        verify(mock);
    }    
}

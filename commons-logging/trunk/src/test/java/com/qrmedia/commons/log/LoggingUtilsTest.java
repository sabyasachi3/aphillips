/*
 * @(#)LoggingUtilsTest.java     15 May 2009
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
package com.qrmedia.commons.log;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Test;

import com.qrmedia.commons.log.test_päckage1.StubObject;

/**
 * Unit tests for the {@link LoggingUtils}.
 * 
 * @author aphillips
 * @since 15 May 2009
 *
 */
public class LoggingUtilsTest {

    @Test
    public void toPackageNameFreeString_noPackageName() {
        Integer obj = 7;
        assertEquals(obj.toString(), LoggingUtils.toPackageNameFreeString(obj));
    }
    
    /**
     * Ensures domain names are <em>not</em> truncated.
     */
    @Test
    public void toPackageNameFreeString_url() throws URISyntaxException {
        String uri = "http://mi6.gov.uk/jbond";
        assertEquals(uri, LoggingUtils.toPackageNameFreeString(new URI(uri)));
    }
    
    @Test
    public void toPackageNameFreeString() {
        StubObject obj = new StubObject();
        assertEquals("StubObject@" + getDefaultHashCodeString(obj), 
                     LoggingUtils.toPackageNameFreeString(obj));
    }
    
    private static String getDefaultHashCodeString(Object obj) {
        return Integer.toHexString(obj.hashCode());
    }
    
    @Test
    public void arrayToString_noPackageNames() {
        assertEquals("[0, 0, 7]", 
                     LoggingUtils.arrayToString(new Integer[] { 0, 0, 7 }, true));
    }   
    
    @Test
    public void arrayToString_stripPackageNames() {
        StubObject obj1 = new StubObject();
        StubObject obj2 = new StubObject();
        assertEquals("[StubObject@" + getDefaultHashCodeString(obj1) + ", StubObject@" 
                         + getDefaultHashCodeString(obj2) + "]",
                     LoggingUtils.arrayToString(new StubObject[] { obj1, obj2 }, true));
    }
    
    @Test
    public void arrayToString_lengthLimited() {
        assertEquals("[StubOb...",
                     LoggingUtils.arrayToString(new StubObject[] { new StubObject() }, true, 10));        
    }    
    
    @Test
    public void arrayToString() {
        StubObject obj1 = new StubObject();
        StubObject obj2 = new StubObject();
        assertEquals("[com.qrmedia.commons.log.test_päckage1.StubObject@" 
                         + getDefaultHashCodeString(obj1) 
                         + ", com.qrmedia.commons.log.test_päckage1.StubObject@" 
                         + getDefaultHashCodeString(obj2) + "]",
                     LoggingUtils.arrayToString(new StubObject[] { obj1, obj2 }, false));        
    }   

    @Test
    public void collectionToString_noPackageNames() {
        assertEquals("(0, 0, 7)", 
                     LoggingUtils.collectionToString(Arrays.asList(0, 0, 7), true));
    }   
    
    @Test
    public void collectionToString_stripPackageNames() {
        StubObject obj1 = new StubObject();
        StubObject obj2 = new StubObject();
        assertEquals("(StubObject@" + getDefaultHashCodeString(obj1) + ", StubObject@" 
                         + getDefaultHashCodeString(obj2) + ")",
                     LoggingUtils.collectionToString(Arrays.asList(obj1, obj2), true));
    }
    
    @Test
    public void collectionToString_lengthLimited() {
        assertEquals("(StubOb...",
                     LoggingUtils.collectionToString(Arrays.asList(new StubObject()), true, 10));        
    }    
    
    @Test
    public void collectionToString() {
        StubObject obj1 = new StubObject();
        StubObject obj2 = new StubObject();
        assertEquals("(com.qrmedia.commons.log.test_päckage1.StubObject@" 
                         + getDefaultHashCodeString(obj1) 
                         + ", com.qrmedia.commons.log.test_päckage1.StubObject@" 
                         + getDefaultHashCodeString(obj2) + ")",
                     LoggingUtils.collectionToString(Arrays.asList(obj1, obj2), false));        
    }    
    
}

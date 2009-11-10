package com.xebia.aphillips.junit.rules;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

/**
 * ExternalResource is a base class for Rules (like TemporaryFolder) that
 * set up an external resource before a test (a file, socket, server,
 * database connection, etc.), and guarantee to tear it down afterward.
 */
public class UsesExternalResource {
    private InputStream googleHome = null;

    @Rule
    public ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            googleHome = new URL("http://www.google.com").openStream();
        }
        
        @Override
        protected void after() {
            if (googleHome != null) {
                try {
                    googleHome.close();
                } catch (IOException exception) {}
            }
        }
    };
    
    @Test 
    public void doctypeIsHtml() throws IOException {
        byte[] buf = new byte[21];
        googleHome.read(buf);
        assertTrue("Expected doctype html", new String(buf).contains("doctype html"));
    }
    
    @After
    public void checkStreamClosed() {
        try {
            googleHome.read();
            throw new AssertionError("Expected the stream to be closed!");
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
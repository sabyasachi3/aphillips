package com.xebia.aphillips.junit.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.Verifier;

/**
 * Verifier is a base class for Rules like ErrorCollector, which
 * can turn otherwise passing test methods into failing tests if a verification
 * check is failed.
 */
public class ErrorLogVerifier {
    private StringBuilder errorLog = new StringBuilder();

    @Rule
    public MethodRule verifier = new Verifier() {
        @Override
        public void verify() {
            assertEquals("Expected an empty error log", 0, errorLog.length());
        }
    };

    @Test
    public void testThatDoesNotWriteErrorLog() {
        assertTrue(true);
    }

    @Test
    public void testThatMightWriteErrorLog() {
        assertTrue(true);
        errorLog.append("Test succeeds but actually something went wrong!");
    }
}
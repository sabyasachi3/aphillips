package com.xebia.aphillips.junit.rules;

import static org.hamcrest.Matchers.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

/**
 * The ErrorCollector Rule allows execution of a test to continue
 * after the first problem is found (for example, to collect <em>all</em> the
 * incorrect rows in a table, and report them all at once).
 */
public class UsesErrorCollectorTwice {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void example() {
        collector.addError(new Throwable("first thing went wrong"));
        collector.addError(new Throwable("second thing went wrong"));
        collector.checkThat("ERROR", not("ERROR"));
        collector.checkThat("OK", not("ERROR"));
        System.out.println("Got here!");
    }
    
}
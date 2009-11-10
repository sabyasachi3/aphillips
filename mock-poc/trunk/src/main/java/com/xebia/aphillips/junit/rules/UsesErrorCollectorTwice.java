package com.xebia.aphillips.junit.rules;

import static org.hamcrest.Matchers.not;

import org.hamcrest.Matcher;
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
    public void example1() {
        collector.addError(new Throwable("first thing went wrong"));
        collector.addError(new Throwable("second thing went wrong"));
        collector.checkThat("ERROR", not("ERROR"));
        collector.checkThat("OK", not("ERROR"));
        System.out.println("Got here (1)!");
    }
    
    private static void assertError(ErrorCollector collector, Throwable error) {
        collector.addError(error);
    }

    private static <T> void assertThat(ErrorCollector collector, T value,
            Matcher<T> matcher) {
        collector.checkThat(value, matcher);
    }
    
    @Test
    public void example2() {
        assertError(collector, new Throwable("first thing went wrong"));
        assertError(collector, new Throwable("second thing went wrong"));
        assertThat(collector, "ERROR", not("ERROR"));
        assertThat(collector, "OK", not("ERROR"));
        System.out.println("Got here (2)!");
    }    
}
/*
 * @(#)AdviceDemo.java     21 May 2009
 */
package com.qrmedia.commons.aspect.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A demonstration of Commons/Aspect advice.
 * 
 * @author aphillips
 * @since 21 May 2009
 * 
 */
public class AdviceDemo {
    private static final String[] CONTEXT_FILENAMES = 
        new String[] { "example-applicationContext.xml" };

    public static void main(String[] args) {
        Log log = LogFactory.getLog(AdviceDemo.class);
        
        AdvisedObject advisedObject = 
            (AdvisedObject) new ClassPathXmlApplicationContext(CONTEXT_FILENAMES)
            .getBean("advisedBean");
        
        log.info("Calling a traced method...");
        advisedObject.tracedMethod("James Bond", 7, "License to Kill".getBytes());
        log.info("Calling a traced method that returns a result...");
        log.info("Result: " + advisedObject.resultReturningTracedMethod(6));
        log.info("Calling a profiled method...");
        advisedObject.profiledMethod();
    }

}
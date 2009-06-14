/*
 * @(#)AdvisedObject.java     21 May 2009
 */
package com.qrmedia.commons.aspect.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A demonstration object to be advised via AspectJ.
 * 
 * @author aphillips
 * @since 21 May 2009
 *
 */
public class AdvisedObject {
    private final Log log = LogFactory.getLog(AdvisedObject.class);
    
    /**
     * FYI: According to the <a href="http://static.springframework.org/spring/docs/2.5.x/reference/aop.html">
     * relevant Spring documentation</a>, <em>"any given pointcut will be matched against public 
     * methods only!"</em>.
     */
    public void tracedMethod(String arg0, int arg1, byte[] arg2) {
        log.info("In tracedMethod.");
    }

    public long resultReturningTracedMethod(long arg0) {
        long result = arg0 + 1;
        log.info("Returning " + result + " from resultReturningTracedMethod.");
        return result;
    }

    public void profiledMethod() {

        try {
            Thread.sleep(200);
            log.info("Exiting profiledMethod.");
        } catch (InterruptedException exception) {
            // do nothing
        }

    }
    
}

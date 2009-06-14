/*
 * @(#)ConstantReturnValueInvocationHandler.java     27 May 2009
 */
package com.qrmedia.commons.lang.annotation.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * A method invocation handler that returns fixed values based on the method name.
 * 
 * @author aphillips
 * @since 27 May 2009
 *
 */
class ConstantReturnValueInvocationHandler implements InvocationHandler {
    protected final Map<String, Object> methodReturnValues;
    
    protected ConstantReturnValueInvocationHandler(Map<String, Object> methodReturnValues) {
        this.methodReturnValues = methodReturnValues;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String methodName = method.getName();
        
        // this could be a problem for methods like hashCode and equals...
        if (!methodReturnValues.containsKey(methodName)) {
            throw new AssertionError("Method '" + methodName 
                    + "' invoked on a dynamic annotation that only supports methods "
                    + methodReturnValues.keySet());
        }
        
        return methodReturnValues.get(methodName);
    }
    
}
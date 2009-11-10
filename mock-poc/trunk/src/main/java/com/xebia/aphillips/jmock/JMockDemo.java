/*
 * @(#)MockitoTest.java     26 May 2009
 */
package com.xebia.aphillips.jmock;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xebia.aphillips.jmock.JMockDemo.Turtle.PenState;

/**
 * JMock demo. See the <a href="http://jmock.org/">JMock documentation</a> for
 * more information.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
@RunWith(JMock.class)
public class JMockDemo {
    private final Mockery context = new JUnit4Mockery();
    
    static interface Turtle {
        enum PenState { PEN_UP, PEN_DOWN };
        
        void forward(int degrees);
        void turn(int degrees);
        void stop();
        void flashLEDs();

        void penUp();
        void penDown();
        PenState queryPen();
    }
    
    @Test
    public void usingMocks() {
        //mock creation
        final Turtle turtle = context.mock(Turtle.class);
        final Turtle turtle2 = context.mock(Turtle.class, "turtle2");
        
        context.checking(new Expectations() {{
            oneOf (turtle).turn(45);           // The turtle will be told to turn 45 degrees once only
            allowing (turtle).flashLEDs();   // The turtle can be told to flash its LEDs any number of types or not at all
            ignoring (turtle2);              // Turtle 2 can be told to do anything.  This test ignores it.
            
            allowing (turtle).queryPen();    // The turtle can be asked about its pen any number of times and will always
                will(returnValue(PenState.PEN_DOWN)); // return PEN_DOWN

            atLeast(1).of (turtle).stop();   // The turtle will be told to stop at least once.
        }});
        
        turtle.flashLEDs();
        turtle.turn(45);
        turtle.flashLEDs();
        turtle2.stop();
        System.out.println(turtle.queryPen());
        turtle.stop();
    }
 
    @Test
    public void ordering() {
        final Turtle turtle = context.mock(Turtle.class);
        final Sequence drawing = context.sequence("drawing");
        
        context.checking(new Expectations() {{
            oneOf (turtle).forward(10); inSequence(drawing);
            oneOf (turtle).turn(45); inSequence(drawing);
            oneOf (turtle).stop();
            oneOf (turtle).forward(10); inSequence(drawing);
        }});
        
        turtle.forward(10);
        turtle.turn(45);
        turtle.forward(10);
        turtle.stop();
    }
    
    @Test
    public void stateful() {
        final Turtle turtle = context.mock(Turtle.class);
        final States pen = context.states("pen").startsAs("up");
        
        context.checking(new Expectations() {{
            oneOf (turtle).penDown(); then(pen.is("down"));
            oneOf (turtle).forward(10); when(pen.is("down"));
            oneOf (turtle).turn(90); when(pen.is("down"));
            oneOf (turtle).forward(10); when(pen.is("down"));
            oneOf (turtle).penUp(); then(pen.is("up"));
        }});
        
        turtle.penDown();
        turtle.turn(90);
        turtle.forward(10);
        turtle.forward(10);
        turtle.penUp();
    }    
    
}

/*
 * @(#)MockitoTest.java     26 May 2009
 */
package com.xebia.aphillips.mockito;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Mockito demo.
 * <p>
 * See the <a href="http://mockito.org/">Mockito documentation</a> for more
 * information.
 * 
 * @author aphillips
 * @since 26 May 2009
 *
 */
@SuppressWarnings("unchecked")
public class MockitoDemo {
    
    @Test
    public void usingMocks() {
        //mock creation
        List<String> mockedList = mock(List.class);
        
        //using mock object
        mockedList.add("one");
        System.out.println(mockedList.get(0));
        mockedList.clear();
        
        //verification
        verify(mockedList).add("one");
        verify(mockedList).clear();
        
    }
    
    @Test
    public void prepareValues() {
        //You can mock concrete classes, not only interfaces
        LinkedList<Object> mockedList = mock(LinkedList.class);
        
        //stubbing
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());
        
        //following prints "first"
        System.out.println(mockedList.get(0));
        
        //following throws runtime exception
        try {
            System.out.println(mockedList.get(1));
        } catch (RuntimeException exception) {
            System.out.println("Caught runtime exception");
        }
        
        //following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));

        doThrow(new RuntimeException()).when(mockedList).clear();
        
        //following throws RuntimeException:
        try {
            mockedList.clear();
        } catch (RuntimeException exception) {
            System.out.println("Caught runtime exception");
        }
        
        //Although it is possible to verify a stubbed invocation, usually it's just redundant
        //If your code cares what get(0) returns then something else breaks (often before even verify() gets executed).
        //If your code doesn't care what get(0) returns then it should not be stubbed.
        verify(mockedList).get(0);
    }
    
    @Test
    public void argumentMatching() {
        List<String> mockedList = mock(List.class);
        
        //stubbing using built-in anyInt() argument matcher
        when(mockedList.get(anyInt())).thenReturn("element");
        
        //following prints "element"
        System.out.println(mockedList.get(999));
        
        //you can also verify using an argument matcher
        verify(mockedList).get(anyInt());
    }
    
    @Test
    public void invocationCountChecking() {
        List<String> mockedList = mock(List.class);
        
        //using mock 
        mockedList.add("once");
        
        mockedList.add("twice");
        mockedList.add("twice");
        
        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");
        
        //following two verifications work exactly the same - times(1) is used by default
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");
        
        //exact number of invocations verification
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");
        
        //verification using never(). never() is an alias to times(0)
        verify(mockedList, never()).add("never happened");
        
        //verification using atLeast()/atMost()
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("five times");
        verify(mockedList, atMost(5)).add("three times");
    }
    
    @Test
    public void orderedChecking() {
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);
        
        //using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");
        
        //create inOrder object passing any mocks that need to be verified in order
        InOrder inOrder = inOrder(firstMock, secondMock);
        
        //following will make sure that firstMock was called before secondMock
        inOrder.verify(firstMock).add("was called first");
        inOrder.verify(secondMock).add("was called second");
        
        List thirdMock = mock(List.class);
        thirdMock.add("1");
        thirdMock.add("2");
        
        InOrder inOrder2 = inOrder(thirdMock);
        inOrder2.verify(thirdMock).add("1");
        inOrder2.verify(thirdMock).add("2");
    }
    
    @Test
    public void noInteractions() {
        List<String> mockOne = mock(List.class);
        List<String> mockTwo = mock(List.class);
        List<String> mockThree = mock(List.class);
        
        //using mocks - only mockOne is interacted
        mockOne.add("one");
        
        //ordinary verification
        verify(mockOne).add("one");
        
        //verify that method was never called on a mock
        verify(mockOne, never()).add("two");
        
        //verify that other mocks were not interacted
        verifyZeroInteractions(mockTwo, mockThree);
        
        List<String> mockFour = mock(List.class);
        mockFour.add("one");
        mockFour.add("two");
        
        verify(mockFour).add("one");
        
        //following verification will fail 
        verifyNoMoreInteractions(mockFour);
    }
    
    @Test
    public void fluidInterfaceMocking() {
        List<String> mockedList = mock(List.class);
        
        when(mockedList.get(anyInt()))
        .thenThrow(new RuntimeException())
        .thenReturn("foo");
      
        // First call: throws runtime exception:
        try {
            mockedList.get(0);
        } catch (RuntimeException exception) {
            System.out.println("Caught runtime exception");
        }
      
        //Second call: prints "foo"
        System.out.println(mockedList.get(1));
      
        //Any consecutive call: prints "foo" as well (last stubbing wins). 
        System.out.println(mockedList.get(999));
    }
    
    @Test
    public void fluidInterfaceMatching2() {
        List<String> mockedList = mock(List.class);
        
        //Alternative, shorter version of consecutive stubbing:
        when(mockedList.get(anyInt())).thenReturn("one", "two", "three");
        
        System.out.println(mockedList.get(1));
        System.out.println(mockedList.get(2));
        System.out.println(mockedList.get(3));
        System.out.println(mockedList.get(4));        
    }
    
    @Test
    public void answer() {
        List<String> mockedList = mock(List.class);

        when(mockedList.get(99)).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Object mock = invocation.getMock();
                return "Mock " + mock + " called with arguments: " + Arrays.toString(args);
            }
        });

        System.out.println(mockedList.get(1));
        
        // Following prints "called with arguments: 99"
        System.out.println(mockedList.get(99));
        
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Object mock = invocation.getMock();
                System.out.println("Mock " + mock + " called with arguments: " 
                                   + Arrays.toString(args));
                return null;
            }
        }).when(mockedList).clear();  
        
        mockedList.clear();
    }
    
    @Test
    public void spyTest() {
        List list = new LinkedList();
        List spy = spy(list);
      
        //optionally, you can stub out some methods:
        when(spy.size()).thenReturn(100);
      
        //using the spy calls real methods
        spy.add("one");
        spy.add("two");
      
        //prints "one" - the first element of a list
        System.out.println(spy.get(0));
      
        //size() method was stubbed - 100 is printed
        System.out.println(spy.size());
      
        //optionally, you can verify
        verify(spy).add("one");
        verify(spy).add("two");
    }
    
}

package com.example.finalproj;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class ServiceTest {
    @Test
    public void changeServiceName(){
        Service s = new Service("CALL","Employee");
        s.setName("DONT CALL");
        assertEquals("DONT CALL",s.getName());


    }



    @Test
    public void PerformedBy(){
        Service s = new Service("CALL","Employee");
        assertEquals("Employee",s.getPerformedBy());
    }
}

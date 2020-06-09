package com.example.finalproj;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HoursTest {
    @Test
    public void changeTime(){
        Hours h = new Hours("SATURDAY",3,4, 4, 6);
        h.setStart("ONE");
        assertEquals(h.getTimeStart(),"ONE");
    }
}

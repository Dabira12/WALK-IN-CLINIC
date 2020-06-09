package com.example.finalproj;

import org.junit.Test;
import static org.junit.Assert.*;


public class AppointmentTest {
    @Test
    public void StartHour(){
        Appointment a = new Appointment("Tuesday",3,4,5,6,"Laurier","Dabz");
        assertEquals(a.getStartHour(),3);
    }

    @Test
    public void EndHour(){
        Appointment a = new Appointment("Tuesday",3,4,5,6,"Laurier","Dabz");
        assertEquals(a.getEndHour(),5);
    }

    @Test
    public void EndMinute(){
        Appointment a = new Appointment("Tuesday",3,4,5,6,"Laurier","Dabz");
        assertEquals(a.getEndMinute(),6);
    }

    @Test
    public void StartMinute(){
        Appointment a = new Appointment("Tuesday",3,4,5,6,"Laurier","Dabz");
        assertEquals(a.getStartMinute(),4);
    }

    @Test
    public void Date(){
        Appointment a = new Appointment("Tuesday",3,4,5,6,"Laurier","Dabz");
        assertEquals(a.getDate(),"Tuesday");
    }

    @Test
    public void location(){
        Appointment a = new Appointment("Tuesday",3,4,5,6,"Laurier","Dabz");
        assertEquals(a.getLocation(),"Laurier");
    }


}

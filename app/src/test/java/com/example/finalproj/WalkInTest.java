package com.example.finalproj;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WalkInTest {
    @Test
    public void UserTest(){
        Person p = new Person("JAMES","boo","james@yahoo.com","james","Employee");
        WalkInClinic clinic = new WalkInClinic("Clinic","157 Laurier Avenue");
        clinic.addUser(p);
        clinic.removeUser(p);
        assertNull(clinic.getUser(p));

    }

    @Test
    public void AddressTest(){
        WalkInClinic clinic = new WalkInClinic("Clinic","157 Laurier Avenue");
        assertEquals(clinic.getAddress(),"157 Laurier Avenue");

    }

}

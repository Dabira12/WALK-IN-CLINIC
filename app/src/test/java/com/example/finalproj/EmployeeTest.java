package com.example.finalproj;

import android.content.Context;

import org.junit.Test;
import static org.junit.Assert.*;



public class EmployeeTest {
    @Test
    public void numberTest(){
        Employee p = new Employee("JAMES","boo","james@yahoo.com","james"," ","das","ottawa clinic",true,true,true,true);
        assertFalse(p.validPhoneNumber());
    }

}

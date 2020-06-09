package com.example.finalproj;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PersonTest {

    @Test
    public void accountTest(){
        Person p = new Person("JAMES","boo","james@yahoo.com","james","Employee");
        assertEquals(p.getAccountType(),"Employee");
    }

    @Test
    public void getterTest(){
        Person p = new Person("JAMES","boo","james@yahoo.com","james","Employee");
        assertEquals(p.getLastName(),"boo");


    }


}

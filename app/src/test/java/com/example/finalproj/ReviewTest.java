package com.example.finalproj;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ReviewTest {
    @Test
    public void changeComment(){
        Review rev = new Review("HI",3);
        rev.comment= "HEY";
        assertEquals(rev.getComment(),"HEY");
    }

    @Test
    public void changeRating(){
        Review rev = new Review("HI",3);
        rev.rating= 4;
        assertEquals(rev.getRating(),4);
    }

}

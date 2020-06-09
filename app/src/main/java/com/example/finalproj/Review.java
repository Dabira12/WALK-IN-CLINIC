package com.example.finalproj;

public class Review {
    String comment;
    int rating;

    public Review (String comment, int rating) {
        this.comment = comment;
        this.rating = rating;
    }
    public Review () {}

    public String getComment() {
        return comment;
    }
    public int getRating() {
        return rating;
    }
}

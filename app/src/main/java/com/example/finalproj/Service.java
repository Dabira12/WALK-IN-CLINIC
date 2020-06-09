package com.example.finalproj;

public class Service {
    private String name;
    private String performedBy;

    public Service(String name, String performedBy) {
        this.name = name;
        this.performedBy = performedBy;
    }
    public Service() {}

    public String getName() {
        return name;
    }
    public String getPerformedBy() {
        return performedBy;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
}

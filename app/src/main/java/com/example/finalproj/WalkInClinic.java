package com.example.finalproj;
import android.util.AndroidException;

import java.util.ArrayList;

public class WalkInClinic {
    private int workingHours;
    private String clinicName;
    private String address;
    private Service service;

    private ArrayList<Person> users;
    private ArrayList<Service> servicesOffered;

    public WalkInClinic(String clinicName, String address) {
        users = new ArrayList<Person>();
        servicesOffered = new ArrayList<Service>();
        this.clinicName = clinicName;
        this.address = address;
        servicesOffered.add(new Service());
    }
    public WalkInClinic() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addUser(Person user) {
        users.add(user);
    }

    public void removeUser(Person user) {
        users.remove(user);
    }

    public void addService(Service service) {
        servicesOffered.add(service);
    }

    public void removeService(Service service) {
        servicesOffered.remove(service);
    }

    public String getClinicName() {
        return clinicName;
    }

    public Service getService() { return service; }

    public Service getService(Service s){
        if (servicesOffered.isEmpty()){
            return null;
        }
        return(servicesOffered.get(servicesOffered.indexOf(s)));

    }

    public Person getUser(Person p){
        if (users.isEmpty()){
            return null;
        }
        return(users.get(users.indexOf(p)));

    }

    public ArrayList<Service> getServicesOffered(){
        return servicesOffered;
    }

}

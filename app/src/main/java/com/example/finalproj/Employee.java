package com.example.finalproj;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Geocoder;
import android.util.Log;
import android.util.Patterns;
import android.content.Context;

public class Employee extends Person {

    private String phoneNumber;
    private String clinicAddress;
    private String clinic;
    private boolean acceptsCredit;
    private boolean acceptsDebit;
    private boolean acceptsType1Insurance;
    private boolean acceptsType2Insurance;

    private ArrayList<Service> employeeServices = new ArrayList<Service>();;

    private Context context;

    public Employee(String firstName, String lastName, String email, String password,
                    String phoneNumber, String clinicAddress, String clinic, boolean acceptsCredit,
                    boolean acceptsDebit, boolean acceptsType1Insurance, boolean acceptsType2Insurance) {
        super(firstName, lastName, email, password, "Employee");
        this.phoneNumber = phoneNumber;
        this.clinicAddress = clinicAddress;
        this.clinic = clinic;
        this.acceptsCredit = acceptsCredit;
        this.acceptsDebit = acceptsDebit;
        this.acceptsType1Insurance = acceptsType1Insurance;
        this.acceptsType2Insurance = acceptsType2Insurance;
    }

    public Employee() {

    }

    public void addService(Service service) {
        employeeServices.add(service);
    }
    public void removeService(Service service) {
        employeeServices.remove(service);
    }
    public Service getService(Service s){
        if (employeeServices.isEmpty()){
            return null;
        }
        return(employeeServices.get(employeeServices.indexOf(s)));

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getClinicAddress() {
        return clinicAddress;
    }
    public String getClinic() {
        return clinic;
    }
    public boolean getAcceptsCredit() {
        return acceptsCredit;
    }
    public boolean getAcceptsDebit() {
        return acceptsDebit;
    }
    public boolean getAcceptsType1Insurance() {
        return acceptsType1Insurance;
    }
    public boolean getAcceptsType2Insurance() {
        return acceptsType2Insurance;
    }
    public void setPhoneNumber(String s) {
        phoneNumber = s;
    }
    public void setClinicAddress(String s) {
        clinicAddress = s;
    }

    public void setClinic(String c) {
        clinic = c;
    }
    public void setAcceptsCredit(boolean a) {
        acceptsCredit = a;
    }
    public void setAcceptsDebit(boolean a) {
        acceptsDebit = a;
    }
    public void setAcceptsType1Insurance(boolean a) {
        acceptsType1Insurance = a;
    }
    public void setAcceptsType2Insurance(boolean a) {
        acceptsType2Insurance = a;
    }

    public boolean validPhoneNumber() {
        if (phoneNumber.contains(" ") || (phoneNumber.length() > 15)) {
            return false;
        }
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static boolean validAddress(Context c, String address) {
        Geocoder g = new Geocoder(c,Locale.getDefault());
        Log.d("geo", g.toString());

        try {
            List l = g.getFromLocationName(address,5);
            if(l==null || l.isEmpty()) {
                return false;
            }
            else{
                return true;
            }
        }
        catch (Exception e){
            Log.d("EX", "" + e);
            return false;
        }










      


    }

}

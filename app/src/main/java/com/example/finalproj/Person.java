package com.example.finalproj;

public class Person {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String accountType;

    public Person(String firstName, String lastName, String email, String password, String accountType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }
    public Person() {}

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getAccountType() { return accountType; }

}

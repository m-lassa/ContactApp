package com.example.contactapp.datamodel;
import javafx.beans.property.SimpleStringProperty;

public class Contact {

    //The fields are of SimpleStringProperty type instead of String
    //This allows for data binding with the TableView
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty phoneNumber;
    private SimpleStringProperty notes;


    //Constructors
    //The first one accepts SimpleStringProperties directly
    public Contact(SimpleStringProperty firstName, SimpleStringProperty lastName, SimpleStringProperty phoneNumber, SimpleStringProperty notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        if(notes.isEmpty().get()) {
            this.notes = new SimpleStringProperty("NA");
        } else this.notes = notes;
    }
    //The second constructor accepts Strings and creates SimpleStringProperties based on the String values
    public Contact(String firstName, String lastName, String phoneNumber, String notes) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        if(notes != "") {
            this.notes = new SimpleStringProperty(notes);
        } else this.notes = new SimpleStringProperty("NA");
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    @Override
    public String toString() {
        return "Contact: \n" +
                "First name: " + firstName +
                "\nLast name: " + lastName +
                "\nPhone Number: " + phoneNumber +
                "\nNotes: " + notes;
    }
}

package com.example.contactapp;

import com.example.contactapp.datamodel.Contact;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ContactDialogController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneNumField;
    @FXML
    private TextArea notesField;


    public Contact processInput(){
        //Create SimpleStringProperties from each textfield/area
        SimpleStringProperty firstName = new SimpleStringProperty(firstNameField.getText().trim());
        SimpleStringProperty lastName = new SimpleStringProperty(lastNameField.getText().trim());
        SimpleStringProperty phoneNumber = new SimpleStringProperty(phoneNumField.getText().trim());
        SimpleStringProperty notes = new SimpleStringProperty(notesField.getText().trim());

        //Validate the input: first name, last name and phone number cannot be empty
        //The Notes field can be empty, this case is handled by the Contact constructor
        if(!firstName.isEmpty().get() && !lastName.isEmpty().get() && !phoneNumber.isEmpty().get()){
            //Create a new Contact with the information inputted by the user
            Contact newContact = new Contact(firstName, lastName, phoneNumber, notes);
            return newContact;
        } else {
            //prompt an alert in case the input is invalid
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Missing input");
            alert.setContentText("Ensure that First name, Last name and Phone number are filled in correctly.");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        return null;
    }

    //Use the selected contact to populate the fields in the form
    public void editContact(Contact contact){
        firstNameField.setText(contact.getFirstName());
        lastNameField.setText(contact.getLastName());
        phoneNumField.setText(contact.getPhoneNumber());
        notesField.setText(contact.getNotes());

    }

}
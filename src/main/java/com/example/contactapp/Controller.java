package com.example.contactapp;

import com.example.contactapp.datamodel.Contact;
import com.example.contactapp.datamodel.ContactData;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class Controller {

    @FXML
    private BorderPane mainPane;
    @FXML
    private TableView<Contact> contactTableView = new TableView<Contact>();

    @FXML
    private TableColumn<Contact, String> firstNameCol = new TableColumn<Contact, String>("First Name");
    @FXML
    private TableColumn<Contact, String> lastNameCol = new TableColumn<Contact, String>("Last Name");
    @FXML
    private TableColumn<Contact, String> phoneNumCol = new TableColumn<Contact, String>("Phone Number");
    @FXML
    private TableColumn<Contact, String> notesCol = new TableColumn<Contact, String>("Notes");
    @FXML
    private ChoiceBox searchChoice;
    @FXML
    private TextField searchField;

    private ContactData contactData;

    public void initialize(){
        //Initialize the ContactData
        contactData = new ContactData();
        //Load the data from the .xml file
        contactData.loadContacts();

        //Set a placeholder in case the TableView is empty
        if (contactTableView.getItems().size() == 0) {
            Label label = new Label("The contact list is empty. Select Add to add a new contact.");
            label.setFont(new Font("Times New Roman", 27));
            contactTableView.setPlaceholder(label);
        }

        //Create a FilteredList with the contact data.
        //This allows for the list to be filtered/searched
        FilteredList<Contact> filteredList = new FilteredList<>(contactData.getContacts(), p -> true);

        //Make the list a SortedList
        //The compare() method is overwritten so to order items by Last Name
        SortedList<Contact> sortedList = new SortedList<>(filteredList, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getLastName().compareTo(o2.getLastName());
            }
        });

        //Link the list to the TableView
        contactTableView.setItems(sortedList);

        contactTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //Set the CellValueFactory for each of the table columns to correspond to the Contact fields
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNumCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        contactTableView.getColumns().setAll(firstNameCol, lastNameCol, phoneNumCol, notesCol);

        //Set the choices in the ChoiceBox: this will be used to search contacts by either first or last name
        searchChoice.getItems().addAll("First Name", "Last Name");
        //By default it is set on First Name
        searchChoice.setValue("First Name");

        /*based on the value in the ChoiceBox, the search field will check whether the characters
        inputted by the user correspond to the first or last name of any item in the
        TableView. Only the items that correspond to the search will be shown in the UI. */
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if(searchChoice.getValue() == "First Name"){
                filteredList.setPredicate(p -> p.getFirstName().toLowerCase().contains(newValue.toLowerCase().trim()));
            } else if(searchChoice.getValue() == "Last Name"){
                filteredList.setPredicate(p -> p.getLastName().toLowerCase().contains(newValue.toLowerCase().trim()));
            }
        });
    }


    //method to launch the Contact DialogPane when creating a new contact
    @FXML
    public void showContactDialogue(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Add new contact");
        dialog.setHeaderText("Use this form to add first and last name and phone number of the contact. Add any notes as necessary.");
        //create an FXMLLoader and set the location to the contact dialog fxml resource
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("Could not open dialog window.");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        //When the OK button is pressed, call the method from the ContactDialogController
        //to save the user input and create a new Contact
        if(result.isPresent() && result.get() == ButtonType.OK){
            ContactDialogController controller = fxmlLoader.getController();
            Contact newContact = controller.processInput();
            if(newContact != null) {
                contactTableView.getSelectionModel().select(newContact);
                contactData.addContact(newContact);
                contactData.saveContacts();
            }
        }

    }

    //delete table row by clicking on the "Delete" menu item
    @FXML
    public void deleteSelectedItem(){
        //get selected item
        Contact selectedContact = contactTableView.getSelectionModel().getSelectedItem();

        if (selectedContact == null){
            Alert itemSelAlert = new Alert(Alert.AlertType.INFORMATION);
            itemSelAlert.setContentText("No item selected.");
            itemSelAlert.setHeaderText(null);
            itemSelAlert.showAndWait();
        } else {
            Alert alert = deleteItemAlert();
            alert.setContentText("Delete " + selectedContact.getFirstName() + " " + selectedContact.getLastName() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            //delete from observable list once OK button is pressed
            if (selectedContact != null) {
                if (result.isPresent() && (result.get() == ButtonType.OK)) {
                    contactData.deleteContact(selectedContact);
                    contactData.saveContacts();
                }
            }
        }
    }

    //allow to delete a table row by pressing the Delete key
    @FXML
    public void handleDeleteKeyPressed(KeyEvent keyEvent){
        //get selected item
        Contact selectedContact = contactTableView.getSelectionModel().getSelectedItem();

        //delete from observable list once OK button is pressed
        if(selectedContact != null){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                Alert alert = deleteItemAlert();
                alert.setContentText("Delete " + selectedContact.getFirstName() + " " + selectedContact.getLastName() + "?");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && (result.get() == ButtonType.OK)) {

                    contactData.deleteContact(selectedContact);
                    contactData.saveContacts();
                }
            }
        } else if (selectedContact == null){
            Alert itemSelAlert = new Alert(Alert.AlertType.INFORMATION);
            itemSelAlert.setContentText("No item selected.");
            itemSelAlert.setHeaderText(null);
            itemSelAlert.showAndWait();
        }
    }

    //Method to launch the DialogPane and edit an item
    //This works in a manner closely comparable to the showContactDialogue() method
    @FXML
    public void editSelectedItem(){
        Contact selectedContact = contactTableView.getSelectionModel().getSelectedItem();

        if(selectedContact == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No item selected.");
            alert.setHeaderText(null);
            alert.showAndWait();
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Edit contact");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("Could not open dialog window.");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        ContactDialogController controller = fxmlLoader.getController();
        controller.editContact(selectedContact);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Contact editedContact = controller.processInput();
            if(editedContact != null){
                contactData.deleteContact(selectedContact);
                contactData.addContact(editedContact);
                contactData.saveContacts();
            }
        }
    }

    //Exit the program through the respective menu item
    @FXML
    public void handleExit(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit program");
        alert.setHeaderText(null);
        alert.setContentText("Click OK to confirm");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && (result.get() == ButtonType.OK)){
            Platform.exit();
        }
    }

    //call a standard Alert before deleting a table row
    private Alert deleteItemAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove contact");
        alert.setHeaderText(null);
        alert.setContentText("Delete selected contact?");
        return alert;
    }


}
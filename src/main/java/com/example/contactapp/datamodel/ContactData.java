package com.example.contactapp.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;


public class ContactData {

    private static final String CONTACTS_FILE = "contacts.xml";

    private static final String CONTACT = "contact";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String NOTES = "notes";

    private ObservableList<Contact> contacts;

    public ContactData() {
        // Inizialize observableArrayList
        contacts = FXCollections.observableArrayList();
    }
    public void addContact(Contact contact){
        contacts.add(contact);
    }
    public void deleteContact(Contact contact){
        contacts.remove(contact);
    }
    public ObservableList<Contact> getContacts() {
        return contacts;
    }

    //Method to load contact data from the .xml file
    public void loadContacts() {
        try {
            // Create a new XMLInputFactory and set up an EventReader
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream inputStream = new FileInputStream(CONTACTS_FILE);

            XMLEventReader xmlReader = inputFactory.createXMLEventReader(inputStream);
            Contact contact = null;
            // read the XML document
            while (xmlReader.hasNext()) {
                XMLEvent xmlEvent = xmlReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    // If we have a contact item, we create a new contact
                    if (startElement.getName().getLocalPart().equals(CONTACT)) {
                        contact = new Contact("", "", "", "");
                        continue;
                    }

                    if (xmlEvent.isStartElement()) {
                        if (xmlEvent.asStartElement().getName().getLocalPart()
                                .equals(FIRST_NAME)) {
                            xmlEvent = xmlReader.nextEvent();
                            contact.setFirstName((xmlEvent.asCharacters().getData()));
                            continue;
                        }
                    }
                    if (xmlEvent.asStartElement().getName().getLocalPart()
                            .equals(LAST_NAME)) {
                        xmlEvent = xmlReader.nextEvent();
                        contact.setLastName((xmlEvent.asCharacters().getData()));
                        continue;
                    }

                    if (xmlEvent.asStartElement().getName().getLocalPart()
                            .equals(PHONE_NUMBER)) {
                        xmlEvent = xmlReader.nextEvent();
                        contact.setPhoneNumber((xmlEvent.asCharacters().getData()));
                        continue;
                    }

                    if (xmlEvent.asStartElement().getName().getLocalPart()
                            .equals(NOTES)) {
                        xmlEvent = xmlReader.nextEvent();
                        contact.setNotes((xmlEvent.asCharacters().getData()));
                        continue;
                    }
                }

                // If we reach the end of a contact element, we add it to the list
                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals(CONTACT)) {
                        contacts.add(contact);
                    }
                }
            }
        }
        catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void saveContacts() {

        try {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream(CONTACTS_FILE));
            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            eventWriter.add(end);

            StartElement contactsStartElement = eventFactory.createStartElement("",
                    "", "contacts");
            eventWriter.add(contactsStartElement);
            eventWriter.add(end);

            for (Contact contact: contacts) {
                //System.out.println(contact.getFirstName());
                saveContact(eventWriter, eventFactory, contact);
            }

            eventWriter.add(eventFactory.createEndElement("", "", "contacts"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Problem with Contacts file: " + e.getMessage());
            e.printStackTrace();
        }
        catch (XMLStreamException e) {
            System.out.println("Problem writing contact: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveContact(XMLEventWriter eventWriter, XMLEventFactory eventFactory, Contact contact)
            throws FileNotFoundException, XMLStreamException {

        XMLEvent end = eventFactory.createDTD("\n");

        // create contact open tag
        StartElement configStartElement = eventFactory.createStartElement("",
                "", CONTACT);
        eventWriter.add(configStartElement);
        eventWriter.add(end);
        // Write the different nodes
        createNode(eventWriter, FIRST_NAME, contact.getFirstName());
        createNode(eventWriter, LAST_NAME, contact.getLastName());
        createNode(eventWriter, PHONE_NUMBER, contact.getPhoneNumber());
        createNode(eventWriter, NOTES, contact.getNotes());

        eventWriter.add(eventFactory.createEndElement("", "", CONTACT));
        eventWriter.add(end);
    }

    private void createNode(XMLEventWriter eventWriter, String name,
                            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }

}

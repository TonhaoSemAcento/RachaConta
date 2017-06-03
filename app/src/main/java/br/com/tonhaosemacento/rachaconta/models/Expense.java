package br.com.tonhaosemacento.rachaconta.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.io.Serializable;

/**
 * Created by TonhaoSemAcento on 27/05/2017.
 */

public class Expense implements Serializable {
    private String guid;
    private String description;
    private Boolean othersCanEdit;
    private String owner;
    private ArrayList<Contacts> contacts;
    private ArrayList<ExpenseItem> expenseItems;

    public Expense(){

    }

    public Expense(String description, Boolean othersCanEdit, String owner){
        this.guid = UUID.randomUUID().toString();
        this.description = description;
        this.othersCanEdit = othersCanEdit;
        this.owner = owner;
        this.contacts = new ArrayList<Contacts>();
        this.expenseItems = new ArrayList<ExpenseItem>();
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOtherscanEdit() {
        return othersCanEdit;
    }

    public void setOtherscanEdit(Boolean otherscanEdit) {
        this.othersCanEdit = otherscanEdit;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString()
    {
        return "Expense{"+
                "guid='"+guid+ '\'' +
                ",description='"+description+'\'' +
                ",othersCanEdit='"+othersCanEdit+ '\'' +
                ",owner='"+owner+ '\'' +
                '}';
    }

    public ArrayList<ExpenseItem> getExpenseItems() {
        return expenseItems;
    }

    public void setExpenseItems(ArrayList<ExpenseItem> expenseItems) {
        this.expenseItems = expenseItems;
    }

    public double getTotal(){
        double total = 0;
        for (ExpenseItem expenseItem : this.expenseItems){
            total += expenseItem.getValue();
        }

        return total;
    }

    public void addExpenseItem(ExpenseItem expenseItem)  {
        this.expenseItems.add(expenseItem);
    }

    public ArrayList<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contacts> contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contacts contact){
        this.contacts.add(contact);
    }
}


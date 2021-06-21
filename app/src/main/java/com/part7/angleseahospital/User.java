package com.part7.angleseahospital;

public class User {


    private int ID;
    private String FName;
    private String LName;
    private String DOB;
    private String Email;
    private int PhoneNumber;

    private int Pin;

    private String Notes;

    private String Photo;

    public User(int Id, String fname, String lname, String dob, String email, int phonenum, int pin, String notes, String photo) {
        ID = Id;
        FName = fname;
        LName = lname;
        DOB = dob;
        Email = email;
        PhoneNumber = phonenum;
        Pin = pin;
        Notes = notes;
        Photo = photo;
    }

    public User() {

    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getPin() {
        return Pin;
    }

    public void setPin(int pin) {
        Pin = pin;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        this.Photo = photo;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
package com.part7.angleseahospital;

public class Staff {
    private String name;
    private String Shift;
    private String Date;
    private String ID;

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    private String Note;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Staff(String Id, String name, String shift, String date, String note) {
        this.name = name;
        this.Shift = shift;
        this.ID = Id;
        this.Date = date;
        this.Note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShift() {
        return Shift;
    }

    public void setShiftStart(String shiftStart) {
        this.Shift = shiftStart;
    }
}
package com.fahad.mybills;

import java.io.Serializable;

public class Bill implements Serializable {
    private String type;
    private String company;
    private String ref;
    private String bill_name;
    private String bill_month;
    private String reading_date;
    private String units;
    private String current_bill;
    private String after_due_bill;
    private String due_date;
    private int remaining_days;
    private String bill_data;

    public Bill(String type, String company, String ref, String bill_name, String bill_month, String reading_date, String units, String current_bill, String after_due_bill, String due_date, int remaining_days, String bill_data) {

        this.type = type;
        this.company = company;
        this.ref = ref;
        this.bill_name = bill_name;
        this.bill_month = bill_month;
        this.reading_date = reading_date;
        this.units = units;
        this.current_bill = current_bill;
        this.after_due_bill = after_due_bill;
        this.due_date = due_date;
        this.remaining_days = remaining_days;
        this.bill_data = bill_data;
    }

    public String getType() {
        return type;
    }

    public String getCompany() {
        return company;
    }

    public String getRef() {
        return ref;
    }

    public String getPersonName() {
        return bill_name;
    }

    public String getMonth() {
        return bill_month;
    }

    public String getReadingDate() {
        return reading_date;
    }

    public String getUnits() {
        return units;
    }

    public String getCurrentBill() {
        return current_bill;
    }

    public String getAfterBill() {
        return after_due_bill;
    }

    public String getDueDate() {
        return due_date;
    }

    public int getRemainingDays() {
        return remaining_days;
    }

    public String getBillData() {
        return bill_data;
    }
}

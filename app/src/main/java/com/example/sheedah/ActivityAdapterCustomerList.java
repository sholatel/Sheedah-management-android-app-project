package com.example.sheedah;

public class ActivityAdapterCustomerList {
    String customerName;
    String status;
    ActivityAdapterCustomerList (String customerName, String status) {
        this.customerName=customerName;
        this.status=status;
    }
    public String getCustomerName () {
        return  this.customerName;
    }

    public String getStatus () {
        return  this.status;
    }
    public void setCustomerName (String customerName) {
       this.customerName=customerName;
    }

    public void setStatus (String status) {
         this.status=status;
    }

}

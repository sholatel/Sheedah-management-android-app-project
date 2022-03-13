package com.example.sheedah;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Customer implements Serializable {
    public String customerName, customerAddress, phoneNumber, debtStatement;
    public boolean debtStatus;


    public Customer () {

    }

    public Customer (String customerName, String customerAddress, String phoneNumber,
                     String debtStatement, boolean debtStatus )  {
        this.customerName= customerName;
        this.customerAddress=customerAddress;
        this.phoneNumber=phoneNumber;
        this.debtStatement=debtStatement;
        this.debtStatus =debtStatus;

    }
    //getters for quering the database for customer details

}

  class  CustomerUpdater extends  Customer{
    public CustomerUpdater(String customerName, String customerAddress, String phoneNumber, String debtStatement, boolean debtStatus) {
        super(customerName, customerAddress, phoneNumber, debtStatement, debtStatus);
    }


      public String getCustomerName() {
          return  "";
      }


      public String getCustomerAddress () {
          return  "";
      }


      public String getPhoneNumber () {
          return  "";
      }


      public boolean getDebtStatus () {
          return  false  ;
      }

      public Map getTransactionHistory () {
          return new HashMap();
      }


      public String getDebtStatement () {
          return  "";
      }


      //setters for changing customer details

      public void setCustomerName (String Name) {
          //do somthing
      }

      public void setCustomerAddress (String address) {
          //do somthing
      }


      public void setPhoneNumber (String phoneNumber) {
          //do somthing
      }

      public void setDebtStatus (boolean debtStatus) {
          //do somthing
      }

      public void updateDebtStatement (String debtStatement) {
          //do somthing
      }


      public void updateTransactionHistory (String transactionHistory) {
          //do something
      }
}
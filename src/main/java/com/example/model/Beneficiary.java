
package com.example.model;

import java.util.Objects;

public class Beneficiary {

    private int beneficiaryID;
    private int customerID;
    private String name;
    private String accountNumber;
    private String bankDetails;

    // Default constructor
    public Beneficiary() {
    }

    // Parameterized constructor
    public Beneficiary(int beneficiaryID, int customerID,
                       String name, String accountNumber,
                       String bankDetails) {
        setBeneficiaryID(beneficiaryID);
        setCustomerID(customerID);
        setName(name);
        setAccountNumber(accountNumber);
        setBankDetails(bankDetails);
    }

    public int getBeneficiaryID() {
        return beneficiaryID;
    }

    public void setBeneficiaryID(int beneficiaryID) {
        if (beneficiaryID < 0) {
            throw new IllegalArgumentException(
                    "Beneficiary ID cannot be negative");
        }
        this.beneficiaryID = beneficiaryID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        if (customerID <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be positive");
        }
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Beneficiary name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Account number cannot be empty");
        }
        this.accountNumber = accountNumber.trim();
    }

    public String getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(String bankDetails) {
        if (bankDetails == null || bankDetails.isBlank()) {
            throw new IllegalArgumentException(
                    "Bank details cannot be empty");
        }
        this.bankDetails = bankDetails.trim();
    }

    @Override
    public String toString() {
        return "Beneficiary{" +
                "beneficiaryID=" + beneficiaryID +
                ", customerID=" + customerID +
                ", name='" + name + '\'' +
                ", accountNumber='" + maskAccountNumber() + '\'' +
                ", bankDetails='" + bankDetails + '\'' +
                '}';
    }

    private String maskAccountNumber() {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return "Not provided";
        }

        int visibleDigits = Math.min(4, accountNumber.length());
        return "*".repeat(accountNumber.length() - visibleDigits)
                + accountNumber.substring(
                        accountNumber.length() - visibleDigits);
    }
}


package com.example.App;

import com.example.model.*;
import com.example.services.*;
import java.util.Scanner;

public class BankingSystemApp {

    private static final Scanner sc = new Scanner(System.in);
    private static final BankingService service =
            new BankingServiceImpl();

    private static int nextTransactionId = 1;

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> addCustomer();
                    case 2 -> addAccount();
                    case 3 -> addBeneficiary();
                    case 4 -> addTransaction();
                    case 5 -> findCustomer();
                    case 6 -> listAccounts();
                    case 7 -> listTransactions();
                    case 8 -> listBeneficiaries();

                    case 10 -> updateCustomer();
                    case 11 -> deleteCustomer();
                    case 12 -> updateAccount();
                    case 13 -> deleteAccount();
                    case 14 -> updateBeneficiary();
                    case 15 -> deleteBeneficiary();
                    case 16 -> updateTransaction();
                    case 17 -> deleteTransaction();

                    case 9 -> {
                        running = false;
                        System.out.println("Thank you for using Banking System!");
                    }

                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println();
        }

        sc.close();
    }

    private static void printMenu() {
        System.out.println("========== BANKING SYSTEM ==========");
        System.out.println("1. Add Customer");
        System.out.println("2. Add Account");
        System.out.println("3. Add Beneficiary");
        System.out.println("4. Add Transaction");
        System.out.println("5. Find Customer by ID");
        System.out.println("6. List Accounts by Customer ID");
        System.out.println("7. List Transactions by Account ID");
        System.out.println("8. List Beneficiaries by Customer ID");
        System.out.println("9. Exit");
        System.out.println("10. Update Customer");
        System.out.println("11. Delete Customer");
        System.out.println("12. Update Account");
        System.out.println("13. Delete Account");
        System.out.println("14. Update Beneficiary");
        System.out.println("15. Delete Beneficiary");
        System.out.println("16. Update Transaction");
        System.out.println("17. Delete Transaction");
        System.out.println("====================================");
    }

    // ADD CUSTOMER
    private static void addCustomer() {
        int id = readInt("Customer ID: ");

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Address: ");
        String address = sc.nextLine();

        System.out.print("Contact: ");
        String contact = sc.nextLine();

        service.addCustomer(
                new Customer(id, name, address, contact)
        );

        System.out.println("Customer added successfully.");
    }

    // ADD ACCOUNT
    private static void addAccount() {
        int id = readInt("Account ID: ");
        int customerId = readInt("Customer ID: ");

        System.out.print("Account Type (Saving/Current): ");
        String type = sc.nextLine();

        double balance = readDouble("Opening Balance: ");

        service.addAccount(
                new Account(id, customerId, type, balance)
        );

        System.out.println("Account added successfully.");
    }

    // ADD BENEFICIARY
    private static void addBeneficiary() {
        int id = readInt("Beneficiary ID: ");
        int customerId = readInt("Customer ID: ");

        System.out.print("Beneficiary Name: ");
        String name = sc.nextLine();

        System.out.print("Account Number: ");
        String accountNumber = sc.nextLine();

        System.out.print("Bank Details: ");
        String bankDetails = sc.nextLine();

        service.addBeneficiary(
                new Beneficiary(
                        id, customerId, name,
                        accountNumber, bankDetails
                )
        );

        System.out.println("Beneficiary added successfully.");
    }

    // ADD TRANSACTION
    private static void addTransaction() {
        int accountId = readInt("Account ID: ");

        System.out.print("Type (Deposit/Withdrawal): ");
        String type = sc.nextLine();

        double amount = readDouble("Amount: ");

        Transaction transaction = new Transaction(
                nextTransactionId++, accountId, type, amount
        );

        service.addTransaction(transaction);

        System.out.println("Transaction successful.");
        System.out.println(service.findAccountById(accountId));
    }

    // FIND CUSTOMER
    private static void findCustomer() {
        int id = readInt("Customer ID: ");
        Customer customer = service.findCustomerById(id);

        if (customer == null) {
            System.out.println("Customer not found.");
        } else {
            System.out.println(customer);
        }
    }

    // LIST ACCOUNTS
    private static void listAccounts() {
        int customerId = readInt("Customer ID: ");

        var accounts = service.getAccountsByCustomerId(customerId);

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            accounts.forEach(System.out::println);
        }
    }

    // LIST TRANSACTIONS
    private static void listTransactions() {
        int accountId = readInt("Account ID: ");

        var transactions =
                service.getTransactionsByAccountId(accountId);

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    // LIST BENEFICIARIES
    private static void listBeneficiaries() {
        int customerId = readInt("Customer ID: ");

        var beneficiaries =
                service.getBeneficiariesByCustomerId(customerId);

        if (beneficiaries.isEmpty()) {
            System.out.println("No beneficiaries found.");
        } else {
            beneficiaries.forEach(System.out::println);
        }
    }

    // UPDATE CUSTOMER
    private static void updateCustomer() {
        int id = readInt("Customer ID to update: ");
        Customer old = service.findCustomerById(id);

        if (old == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("New Name: ");
        String name = sc.nextLine();

        System.out.print("New Address: ");
        String address = sc.nextLine();

        System.out.print("New Contact: ");
        String contact = sc.nextLine();

        service.updateCustomer(
                new Customer(id, name, address, contact)
        );

        System.out.println("Customer updated successfully.");
    }

    // DELETE CUSTOMER
    private static void deleteCustomer() {
        int id = readInt("Customer ID to delete: ");

        service.deleteCustomer(id);

        System.out.println("Customer deleted successfully.");
    }

    // UPDATE ACCOUNT
    private static void updateAccount() {
        int id = readInt("Account ID to update: ");
        Account old = service.findAccountById(id);

        if (old == null) {
            System.out.println("Account not found.");
            return;
        }

        int customerId = readInt("Customer ID: ");

        System.out.print("New Account Type (Saving/Current): ");
        String type = sc.nextLine();

        double balance = readDouble("New Balance: ");

        service.updateAccount(
                new Account(id, customerId, type, balance)
        );

        System.out.println("Account updated successfully.");
    }

    // DELETE ACCOUNT
    private static void deleteAccount() {
        int id = readInt("Account ID to delete: ");

        service.deleteAccount(id);

        System.out.println(
                "Account and its transaction history deleted."
        );
    }

    // UPDATE BENEFICIARY
    private static void updateBeneficiary() {
        int id = readInt("Beneficiary ID to update: ");
        Beneficiary old = service.findBeneficiaryById(id);

        if (old == null) {
            System.out.println("Beneficiary not found.");
            return;
        }

        int customerId = readInt("Customer ID: ");

        System.out.print("New Beneficiary Name: ");
        String name = sc.nextLine();

        System.out.print("New Account Number: ");
        String accountNumber = sc.nextLine();

        System.out.print("New Bank Details: ");
        String bankDetails = sc.nextLine();

        service.updateBeneficiary(
                new Beneficiary(
                        id, customerId, name,
                        accountNumber, bankDetails
                )
        );

        System.out.println("Beneficiary updated successfully.");
    }

    // DELETE BENEFICIARY
    private static void deleteBeneficiary() {
        int id = readInt("Beneficiary ID to delete: ");

        service.deleteBeneficiary(id);

        System.out.println("Beneficiary deleted successfully.");
    }

    // UPDATE TRANSACTION
    private static void updateTransaction() {
        int id = readInt("Transaction ID to update: ");
        Transaction old = service.findTransactionById(id);

        if (old == null) {
            System.out.println("Transaction not found.");
            return;
        }

        int accountId = readInt("Account ID: ");

        System.out.print("New Type (Deposit/Withdrawal): ");
        String type = sc.nextLine();

        double amount = readDouble("New Amount: ");

        service.updateTransaction(
                new Transaction(id, accountId, type, amount)
        );

        System.out.println("Transaction updated successfully.");
    }

    // DELETE TRANSACTION
    private static void deleteTransaction() {
        int id = readInt("Transaction ID to delete: ");

        service.deleteTransaction(id);

        System.out.println("Transaction deleted successfully.");
    }

    // READ INTEGER
    private static int readInt(String message) {
        System.out.print(message);

        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid integer: ");
            sc.next();
        }

        int value = sc.nextInt();
        sc.nextLine();

        return value;
    }

    // READ DOUBLE
    private static double readDouble(String message) {
        System.out.print(message);

        while (!sc.hasNextDouble()) {
            System.out.print("Enter a valid amount: ");
            sc.next();
        }

        double value = sc.nextDouble();
        sc.nextLine();

        return value;
    }
}

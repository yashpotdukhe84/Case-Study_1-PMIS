
package com.example.services;

import com.example.model.Account;
import com.example.model.Beneficiary;
import com.example.model.Customer;
import com.example.model.Transaction;

import java.util.*;

public class BankingServiceImpl implements BankingService {

    private final Map<Integer, Customer> customers = new HashMap<>();
    private final Map<Integer, Account> accounts = new HashMap<>();
    private final Map<Integer, Transaction> transactions = new HashMap<>();
    private final Map<Integer, Beneficiary> beneficiaries = new HashMap<>();

    @Override
    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerID(), customer);
    }

    @Override
    public void addAccount(Account account) {
        if (!customers.containsKey(account.getCustomerID())) {
            throw new IllegalArgumentException("Customer not found.");
        }

        if (account.getBalance() < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }

        accounts.put(account.getAccountID(), account);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        Account account = accounts.get(transaction.getAccountID());

        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        String type = transaction.getType().trim().toLowerCase();

        if (type.equals("deposit")) {
            account.setBalance(
                account.getBalance() + transaction.getAmount()
            );
        } else if (type.equals("withdrawal")
                || type.equals("withdraw")) {

            if (account.getBalance() < transaction.getAmount()) {
                throw new IllegalArgumentException("Insufficient balance.");
            }

            account.setBalance(
                account.getBalance() - transaction.getAmount()
            );
        } else {
            throw new IllegalArgumentException(
                "Enter Deposit or Withdrawal."
            );
        }

        transactions.put(
            transaction.getTransactionID(), transaction
        );
    }

    @Override
    public void addBeneficiary(Beneficiary beneficiary) {
        if (!customers.containsKey(beneficiary.getCustomerID())) {
            throw new IllegalArgumentException("Customer not found.");
        }

        beneficiaries.put(
            beneficiary.getBeneficiaryID(), beneficiary
        );
    }

    @Override
    public Customer findCustomerById(int id) {
        return customers.get(id);
    }

    @Override
    public Account findAccountById(int id) {
        return accounts.get(id);
    }

    @Override
    public Transaction findTransactionById(int id) {
        return transactions.get(id);
    }

    @Override
    public Beneficiary findBeneficiaryById(int id) {
        return beneficiaries.get(id);
    }

    @Override
    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> result = new ArrayList<>();

        for (Account account : accounts.values()) {
            if (account.getCustomerID() == customerId) {
                result.add(account);
            }
        }

        return result;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : transactions.values()) {
            if (transaction.getAccountID() == accountId) {
                result.add(transaction);
            }
        }

        return result;
    }

    @Override
    public List<Beneficiary> getBeneficiariesByCustomerId(int customerId) {
        List<Beneficiary> result = new ArrayList<>();

        for (Beneficiary beneficiary : beneficiaries.values()) {
            if (beneficiary.getCustomerID() == customerId) {
                result.add(beneficiary);
            }
        }

        return result;
    }

    @Override
    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    @Override
    public Collection<Customer> getAllCustomers() {
        return customers.values();
    }

    @Override
    public Collection<Transaction> getAllTransactions() {
        return transactions.values();
    }

    @Override
    public Collection<Beneficiary> getAllBeneficiaries() {
        return beneficiaries.values();
    }
    
@Override
public void updateCustomer(Customer customer) {
    if (!customers.containsKey(customer.getCustomerID())) {
        throw new IllegalArgumentException("Customer not found.");
    }
    customers.put(customer.getCustomerID(), customer);
}

@Override
public void deleteCustomer(int customerId) {
    if (!customers.containsKey(customerId)) {
        throw new IllegalArgumentException("Customer not found.");
    }

    boolean hasAccounts = accounts.values().stream()
            .anyMatch(a -> a.getCustomerID() == customerId);

    if (hasAccounts) {
        throw new IllegalArgumentException(
                "Delete the customer's accounts first.");
    }

    beneficiaries.values().removeIf(
            b -> b.getCustomerID() == customerId);

    customers.remove(customerId);
}

@Override
public void updateAccount(Account account) {
    if (!accounts.containsKey(account.getAccountID())) {
        throw new IllegalArgumentException("Account not found.");
    }

    if (!customers.containsKey(account.getCustomerID())) {
        throw new IllegalArgumentException("Customer not found.");
    }

    if (account.getBalance() < 0) {
        throw new IllegalArgumentException("Balance cannot be negative.");
    }

    accounts.put(account.getAccountID(), account);
}

@Override
public void deleteAccount(int accountId) {
    if (!accounts.containsKey(accountId)) {
        throw new IllegalArgumentException("Account not found.");
    }

    transactions.values().removeIf(
            t -> t.getAccountID() == accountId);

    accounts.remove(accountId);
}

@Override
public void updateBeneficiary(Beneficiary beneficiary) {
    if (!beneficiaries.containsKey(beneficiary.getBeneficiaryID())) {
        throw new IllegalArgumentException("Beneficiary not found.");
    }

    if (!customers.containsKey(beneficiary.getCustomerID())) {
        throw new IllegalArgumentException("Customer not found.");
    }

    beneficiaries.put(beneficiary.getBeneficiaryID(), beneficiary);
}

@Override
public void deleteBeneficiary(int beneficiaryId) {
    if (beneficiaries.remove(beneficiaryId) == null) {
        throw new IllegalArgumentException("Beneficiary not found.");
    }
}

@Override
public void updateTransaction(Transaction transaction) {
    Transaction old = transactions.get(transaction.getTransactionID());

    if (old == null) {
        throw new IllegalArgumentException("Transaction not found.");
    }

    Account oldAccount = accounts.get(old.getAccountID());
    Account newAccount = accounts.get(transaction.getAccountID());

    if (oldAccount == null || newAccount == null) {
        throw new IllegalArgumentException("Account not found.");
    }

    if (transaction.getAmount() <= 0) {
        throw new IllegalArgumentException("Amount must be positive.");
    }

    // Reverse the old transaction.
    String oldType = old.getType().trim().toLowerCase();

    double oldEffect = oldType.equals("deposit")
            ? old.getAmount() : -old.getAmount();

    double oldBalanceAfterReverse =
            oldAccount.getBalance() - oldEffect;

    // Validate the replacement transaction before changing balances.
    String newType = transaction.getType().trim().toLowerCase();

    if (!newType.equals("deposit")
            && !newType.equals("withdrawal")
            && !newType.equals("withdraw")) {
        throw new IllegalArgumentException(
                "Type must be Deposit or Withdrawal.");
    }

    double newEffect = newType.equals("deposit")
            ? transaction.getAmount() : -transaction.getAmount();

    double resultingBalance = newAccount.getBalance();

    if (oldAccount.getAccountID() == newAccount.getAccountID()) {
        resultingBalance = oldBalanceAfterReverse;
    }

    resultingBalance += newEffect;

    if (resultingBalance < 0) {
        throw new IllegalArgumentException("Insufficient balance.");
    }

    // Apply the reversal and replacement.
    oldAccount.setBalance(oldBalanceAfterReverse);
    newAccount.setBalance(resultingBalance);

    transactions.put(transaction.getTransactionID(), transaction);
}

@Override
public void deleteTransaction(int transactionId) {
    Transaction transaction = transactions.get(transactionId);

    if (transaction == null) {
        throw new IllegalArgumentException("Transaction not found.");
    }

    Account account = accounts.get(transaction.getAccountID());

    if (account == null) {
        throw new IllegalArgumentException("Account not found.");
    }

    String type = transaction.getType().trim().toLowerCase();

    // Reverse the transaction's effect on the balance.
    if (type.equals("deposit")) {
        if (account.getBalance() < transaction.getAmount()) {
            throw new IllegalArgumentException(
                    "Cannot reverse deposit: balance would become negative.");
        }
        account.setBalance(account.getBalance() - transaction.getAmount());
    } else {
        account.setBalance(account.getBalance() + transaction.getAmount());
    }

    transactions.remove(transactionId);
}

}


package com.example.services;

import com.example.MySQLConnection;
import com.example.model.Account;
import com.example.model.Beneficiary;
import com.example.model.Customer;
import com.example.model.Transaction;

import java.sql.*;
import java.util.*;

public class BankingServiceImpl implements BankingService {

    private Connection connect() throws SQLException {
        return MySQLConnection.getConnection();
    }

    private RuntimeException dbError(SQLException e) {
        return new RuntimeException("Database operation failed: "
                + e.getMessage(), e);
    }

    // ==================== CUSTOMER ====================

    @Override
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO customers "
                + "(customer_id, name, address, contact) VALUES (?, ?, ?, ?)";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customer.getCustomerID());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getContact());

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Customer was not inserted.");
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Customer findCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("contact")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Collection<Customer> getAllCustomers() {
        List<Customer> result = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET name=?, address=?, contact=? "
                + "WHERE customer_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getContact());
            ps.setInt(4, customer.getCustomerID());

            requireUpdated(ps.executeUpdate(), "Customer");
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id=?";

        try (Connection con = connect()) {
            // A customer with accounts cannot be deleted.
            try (PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM accounts WHERE customer_id=?")) {
                check.setInt(1, customerId);
                try (ResultSet rs = check.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new IllegalArgumentException(
                                "Delete the customer's accounts first.");
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                requireUpdated(ps.executeUpdate(), "Customer");
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // ==================== ACCOUNT ====================

    @Override
    public void addAccount(Account account) {
        if (account.getBalance() < 0) {
            throw new IllegalArgumentException(
                    "Balance cannot be negative.");
        }

        String sql = "INSERT INTO accounts "
                + "(account_id, customer_id, type, balance) VALUES (?, ?, ?, ?)";

        try (Connection con = connect()) {
            if (findCustomerById(account.getCustomerID()) == null) {
                throw new IllegalArgumentException("Customer not found.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, account.getAccountID());
                ps.setInt(2, account.getCustomerID());
                ps.setString(3, account.getType());
                ps.setDouble(4, account.getBalance());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Account findAccountById(int id) {
        String sql = "SELECT * FROM accounts WHERE account_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("account_id"),
                            rs.getInt("customer_id"),
                            rs.getString("type"),
                            rs.getDouble("balance")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Collection<Account> getAllAccounts() {
        List<Account> result = new ArrayList<>();

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM accounts");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Account(
                        rs.getInt("account_id"),
                        rs.getInt("customer_id"),
                        rs.getString("type"),
                        rs.getDouble("balance")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> result = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Account(
                            rs.getInt("account_id"),
                            rs.getInt("customer_id"),
                            rs.getString("type"),
                            rs.getDouble("balance")
                    ));
                }
                return result;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void updateAccount(Account account) {
        if (account.getBalance() < 0) {
            throw new IllegalArgumentException(
                    "Balance cannot be negative.");
        }

        String sql = "UPDATE accounts SET customer_id=?, type=?, balance=? "
                + "WHERE account_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, account.getCustomerID());
            ps.setString(2, account.getType());
            ps.setDouble(3, account.getBalance());
            ps.setInt(4, account.getAccountID());

            requireUpdated(ps.executeUpdate(), "Account");
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void deleteAccount(int accountId) {
        try (Connection con = connect()) {
            con.setAutoCommit(false);

            try {
                // Remove dependent transactions first.
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM transactions WHERE account_id=?")) {
                    ps.setInt(1, accountId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM accounts WHERE account_id=?")) {
                    ps.setInt(1, accountId);
                    requireUpdated(ps.executeUpdate(), "Account");
                }

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // ==================== BENEFICIARY ====================

    @Override
    public void addBeneficiary(Beneficiary beneficiary) {
        String sql = "INSERT INTO beneficiaries "
                + "(beneficiary_id, customer_id, name, account_number, "
                + "bank_details) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, beneficiary.getBeneficiaryID());
            ps.setInt(2, beneficiary.getCustomerID());
            ps.setString(3, beneficiary.getName());
            ps.setString(4, beneficiary.getAccountNumber());
            ps.setString(5, beneficiary.getBankDetails());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Beneficiary findBeneficiaryById(int id) {
        String sql = "SELECT * FROM beneficiaries WHERE beneficiary_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Beneficiary(
                            rs.getInt("beneficiary_id"),
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("account_number"),
                            rs.getString("bank_details")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public List<Beneficiary> getBeneficiariesByCustomerId(int customerId) {
        List<Beneficiary> result = new ArrayList<>();
        String sql = "SELECT * FROM beneficiaries WHERE customer_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Beneficiary(
                            rs.getInt("beneficiary_id"),
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("account_number"),
                            rs.getString("bank_details")
                    ));
                }
                return result;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Collection<Beneficiary> getAllBeneficiaries() {
        List<Beneficiary> result = new ArrayList<>();

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM beneficiaries");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Beneficiary(
                        rs.getInt("beneficiary_id"),
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("account_number"),
                        rs.getString("bank_details")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void updateBeneficiary(Beneficiary beneficiary) {
        String sql = "UPDATE beneficiaries SET customer_id=?, name=?, "
                + "account_number=?, bank_details=? WHERE beneficiary_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, beneficiary.getCustomerID());
            ps.setString(2, beneficiary.getName());
            ps.setString(3, beneficiary.getAccountNumber());
            ps.setString(4, beneficiary.getBankDetails());
            ps.setInt(5, beneficiary.getBeneficiaryID());

            requireUpdated(ps.executeUpdate(), "Beneficiary");
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void deleteBeneficiary(int beneficiaryId) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM beneficiaries WHERE beneficiary_id=?")) {
            ps.setInt(1, beneficiaryId);
            requireUpdated(ps.executeUpdate(), "Beneficiary");
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // ==================== TRANSACTION ====================

    @Override
    public void addTransaction(Transaction transaction) {
        validateTransaction(transaction);

        String sql = "INSERT INTO transactions "
                + "(transaction_id, account_id, type, amount) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = connect()) {
            con.setAutoCommit(false);

            try {
                Account account = lockAccount(con, transaction.getAccountID());
                if (account == null) {
                    throw new IllegalArgumentException("Account not found.");
                }

                double newBalance = calculateBalance(
                        account.getBalance(),
                        transaction.getType(),
                        transaction.getAmount());

                updateBalance(con, account.getAccountID(), newBalance);

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, transaction.getTransactionID());
                    ps.setInt(2, transaction.getAccountID());
                    ps.setString(3, transaction.getType());
                    ps.setDouble(4, transaction.getAmount());
                    ps.executeUpdate();
                }

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Transaction findTransactionById(int id) {
        String sql = "SELECT * FROM transactions WHERE transaction_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Transaction(
                            rs.getInt("transaction_id"),
                            rs.getInt("account_id"),
                            rs.getString("type"),
                            rs.getDouble("amount")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> result = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Transaction(
                            rs.getInt("transaction_id"),
                            rs.getInt("account_id"),
                            rs.getString("type"),
                            rs.getDouble("amount")
                    ));
                }
                return result;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public Collection<Transaction> getAllTransactions() {
        List<Transaction> result = new ArrayList<>();

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM transactions");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getInt("account_id"),
                        rs.getString("type"),
                        rs.getDouble("amount")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        validateTransaction(transaction);

        String sql = "UPDATE transactions SET account_id=?, type=?, amount=? "
                + "WHERE transaction_id=?";

        try (Connection con = connect()) {
            con.setAutoCommit(false);

            try {
                Transaction old = findTransactionById(
                        transaction.getTransactionID());

                if (old == null) {
                    throw new IllegalArgumentException(
                            "Transaction not found.");
                }

                Account oldAccount = lockAccount(con, old.getAccountID());
                Account newAccount = old.getAccountID()
                        == transaction.getAccountID()
                        ? oldAccount
                        : lockAccount(con, transaction.getAccountID());

                if (oldAccount == null || newAccount == null) {
                    throw new IllegalArgumentException("Account not found.");
                }

                double oldReversed = reverseBalance(
                        oldAccount.getBalance(),
                        old.getType(),
                        old.getAmount());

                double newBalance = oldAccount.getAccountID()
                        == newAccount.getAccountID()
                        ? oldReversed
                        : newAccount.getBalance();

                newBalance = calculateBalance(
                        newBalance,
                        transaction.getType(),
                        transaction.getAmount());

                if (oldAccount.getAccountID() != newAccount.getAccountID()) {
                    updateBalance(con, oldAccount.getAccountID(), oldReversed);
                }
                updateBalance(con, newAccount.getAccountID(), newBalance);

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, transaction.getAccountID());
                    ps.setString(2, transaction.getType());
                    ps.setDouble(3, transaction.getAmount());
                    ps.setInt(4, transaction.getTransactionID());
                    requireUpdated(ps.executeUpdate(), "Transaction");
                }

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    @Override
    public void deleteTransaction(int transactionId) {
        try (Connection con = connect()) {
            con.setAutoCommit(false);

            try {
                Transaction transaction =
                        findTransactionById(transactionId);

                if (transaction == null) {
                    throw new IllegalArgumentException(
                            "Transaction not found.");
                }

                Account account = lockAccount(con, transaction.getAccountID());
                if (account == null) {
                    throw new IllegalArgumentException("Account not found.");
                }

                double balance = reverseBalance(
                        account.getBalance(),
                        transaction.getType(),
                        transaction.getAmount());

                updateBalance(con, account.getAccountID(), balance);

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM transactions WHERE transaction_id=?")) {
                    ps.setInt(1, transactionId);
                    requireUpdated(ps.executeUpdate(), "Transaction");
                }

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // ==================== HELPERS ====================

    private void requireUpdated(int rows, String entity) {
        if (rows == 0) {
            throw new IllegalArgumentException(entity + " not found.");
        }
    }

    private void validateTransaction(Transaction t) {
        if (t.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive.");
        }

        String type = t.getType().trim().toLowerCase(Locale.ROOT);
        if (!type.equals("deposit") && !type.equals("withdrawal")
                && !type.equals("withdraw")) {
            throw new IllegalArgumentException(
                    "Type must be Deposit or Withdrawal.");
        }
    }

    private double calculateBalance(double balance, String type, double amount) {
        String normalized = type.trim().toLowerCase(Locale.ROOT);

        if (normalized.equals("deposit")) {
            return balance + amount;
        }

        if (balance < amount) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        return balance - amount;
    }

    private double reverseBalance(double balance, String type, double amount) {
        String normalized = type.trim().toLowerCase(Locale.ROOT);

        if (normalized.equals("deposit")) {
            if (balance < amount) {
                throw new IllegalArgumentException(
                        "Cannot reverse deposit: balance would become negative.");
            }
            return balance - amount;
        }

        return balance + amount;
    }

    private Account lockAccount(Connection con, int accountId)
            throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id=? FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("account_id"),
                            rs.getInt("customer_id"),
                            rs.getString("type"),
                            rs.getDouble("balance")
                    );
                }
                return null;
            }
        }
    }

    private void updateBalance(Connection con, int accountId, double balance)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE accounts SET balance=? WHERE account_id=?")) {
            ps.setDouble(1, balance);
            ps.setInt(2, accountId);
            requireUpdated(ps.executeUpdate(), "Account");
        }
    }
}

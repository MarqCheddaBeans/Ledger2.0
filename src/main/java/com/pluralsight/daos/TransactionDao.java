package com.pluralsight.daos;

import com.pluralsight.config.DatabaseConfig;
import com.pluralsight.models.Transaction;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao implements ITransactionDao{


    private static DataSource ds;

    @Override
    public ArrayList customSearch() {

        public static void customSearch() {
            System.out.println("--- Database Custom Search ---");
            // ... (Keep your existing Scanner code to get userStartDate, userEndDate, etc.) ...

            String sql = "SELECT t.*, v.vendor_name FROM Transactions t " +
                    "JOIN Vendors v ON t.vendor_id = v.vendor_id WHERE 1=1";

            // Build query dynamically
            if (!userStartDate.isEmpty()) sql += " AND transaction_date >= ?";
            if (!userEndDate.isEmpty()) sql += " AND transaction_date <= ?";
            if (!userDescription.isEmpty()) sql += " AND description LIKE ?";
            if (!userVendor.isEmpty()) sql += " AND v.vendor_name LIKE ?";
            if (userAmount != null) sql += " AND amount = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                int paramIndex = 1;
                if (!userStartDate.isEmpty()) pstmt.setString(paramIndex++, userStartDate);
                if (!userEndDate.isEmpty()) pstmt.setString(paramIndex++, userEndDate);
                if (!userDescription.isEmpty()) pstmt.setString(paramIndex++, "%" + userDescription + "%");
                if (!userVendor.isEmpty()) pstmt.setString(paramIndex++, "%" + userVendor + "%");
                if (userAmount != null) pstmt.setDouble(paramIndex++, userAmount);

                ResultSet rs = pstmt.executeQuery();
                List<Transaction> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(new Transaction(
                            rs.getDate("transaction_date").toLocalDate(),
                            rs.getTime("transaction_time").toLocalTime(),
                            rs.getString("description"),
                            rs.getString("vendor_name"),
                            rs.getDouble("amount")
                    ));
                }
                printTransactions(results);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList sortByVendor() {
        return null;
    }

    @Override
    public ArrayList sortPreviousYear() {
        return null;
    }

    @Override
    public static void sortPreviousMonth() {

        // SQL query that automatically calculates "last month" regardless of the current year
        String sql = "SELECT t.*, v.vendor_name FROM Transactions t " +
                "JOIN Vendors v ON t.vendor_id = v.vendor_id " +
                "WHERE MONTH(transaction_date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) " +
                "AND YEAR(transaction_date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)";

        List<Transaction> prevMonthList = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                prevMonthList.add(new Transaction(
                        rs.getDate("transaction_date").toLocalDate(),
                        rs.getTime("transaction_time").toLocalTime(),
                        rs.getString("description"),
                        rs.getString("vendor_name"),
                        rs.getDouble("amount")
                ));
            }
            printTransactions(prevMonthList);

        } catch (SQLException e) {
            System.out.println("Error fetching previous month: " + e.getMessage());
        }
    }

    @Override
    public ArrayList sortYearToDate() {
        return null;
    }

    @Override
    public ArrayList sortMonthToDate() {
        return null;
    }

    @Override
    public static void sortByPayments() {
        String sql = "SELECT t.*, v.vendor_name FROM Transactions t " +
                "JOIN Vendors v ON t.vendor_id = v.vendor_id " +
                "WHERE amount < 0";

        List<Transaction> payments = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(new Transaction(
                        rs.getDate("transaction_date").toLocalDate(),
                        rs.getTime("transaction_time").toLocalTime(),
                        rs.getString("description"),
                        rs.getString("vendor_name"),
                        rs.getDouble("amount")
                ));
            }
            System.out.println("----------------------------\nDisplaying Payments\n----------------------------");
            printTransactions(payments);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList sortByDeposits() {
        return null;
    }

    @Override
    public ArrayList userPayment() {
        return null;
    }

    @Override
    public ArrayList userDeposit() {
        return null;
    }

    @Override
    public ArrayList displayFullLedger() {
        ArrayList<Ledger> transactions = new ArrayList<>();

        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            transaction_id,
                            transaction_date,
                            description,
                            vendor,
                            amount
                        FROM
                            ledger
                        ORDER BY transaction_date DESC;
                        """)
        ) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Ledger();
                    transaction.setId(resultSet.getInt("transaction_id"));
                    transaction.setTransactionDate(resultSet.getDate("transaction_date").toLocalDate());
                    transaction.setDescription(resultSet.getString("description"));
                    transaction.setVendor(resultSet.getString("vendor"));
                    transaction.setAmount(resultSet.getDouble("amount"));

                    transactions.add(transaction);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving all transactions: " + e);
            System.exit(1);
        }

        return transactions;
    }
    public static void printTransactions(List<Transaction> transactions) {
        // 1. Check if the list is empty to avoid printing just a header
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        // 2. Print the Header Row
        System.out.println("-------------------------------------------------------------------------------------------------------");
        // %-12s means "a string taking up 12 characters, aligned to the left"
        System.out.printf("%-12s | %-10s | %-50s | %-25s | %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("-------------------------------------------------------------------------------------------------------");

        // 3. Loop through the list and print each transaction
        for (Transaction t : transactions) {

            // Optional: Color code the output (Green for deposits, Red for payments)
            // You can remove the ANSI codes if your terminal doesn't support colors.
            String color = t.getAmount() < 0 ? "\u001B[31m" : "\u001B[32m"; // Red : Green
            String reset = "\u001B[0m";

            System.out.printf("%-12s | %-10s | %-50s | %-25s | %s$ %10.2f%s%n",
                    t.getDate(),                 // Date
                    t.getTime(),                 // Time
                    t.getDescription(),          // Description
                    t.getVendor(),               // Vendor
                    color,                       // Start Color
                    t.getAmount(),               // Amount (%.2f formats to 2 decimal places)
                    reset                        // Reset Color
            );
        }
        System.out.println("-------------------------------------------------------------------------------------------------------");
    }
}

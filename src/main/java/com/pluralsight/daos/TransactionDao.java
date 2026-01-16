package com.pluralsight.daos;

import com.pluralsight.models.Transaction;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionDao implements ITransactionDao{


    private DataSource ds;

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
    public ArrayList sortPreviousMonth() {
        return null;
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
    public ArrayList sortByPayments() {
        return null;
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
}

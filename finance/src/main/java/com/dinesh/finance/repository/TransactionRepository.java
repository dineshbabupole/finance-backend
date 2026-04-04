package com.dinesh.finance.repository;

import com.dinesh.finance.model.Transaction;
import com.dinesh.finance.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // fetch all transactions that are not soft deleted
    List<Transaction> findByDeletedFalse();

    // filter transactions by type (INCOME or EXPENSE) excluding deleted ones
    List<Transaction> findByTypeAndDeletedFalse(TransactionType type);

    // filter transactions by category excluding deleted ones
    List<Transaction> findByCategoryAndDeletedFalse(String category);

    // filter transactions within a date range excluding deleted ones
    List<Transaction> findByDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);

    // calculate total income from all active transactions
    // COALESCE returns 0 instead of null when there are no income records
    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND deleted = 0",
            nativeQuery = true)
    BigDecimal calculateTotalIncome();

    // calculate total expenses from all active transactions
    // COALESCE returns 0 instead of null when there are no expense records
    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND deleted = 0",
            nativeQuery = true)
    BigDecimal calculateTotalExpenses();

    // group active transactions by category and sum the amounts
    // used in dashboard to show spending breakdown per category
    @Query(value = "SELECT category, SUM(amount) as total FROM transactions WHERE deleted = 0 GROUP BY category ORDER BY total DESC",
            nativeQuery = true)
    List<Object[]> totalAmountByCategory();

    // get monthly income and expense totals for last 12 months
    // groups dates by year and month for trend analysis
    @Query(value = "SELECT t.date as month, type, SUM(amount) as total " +
            "FROM transactions WHERE deleted = 0 " +
            "GROUP BY month, type ORDER BY month DESC LIMIT 12",
            nativeQuery = true)
    List<Object[]> monthlyTrends();
    // Recent activity — last 5 transactions
    @Query(value = "SELECT * FROM transactions WHERE deleted = 0 ORDER BY date DESC LIMIT 5",
            nativeQuery = true)
    List<Transaction> recentActivity();
}
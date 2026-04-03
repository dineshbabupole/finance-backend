package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDeletedFalse();

    List<Transaction> findByTypeAndDeletedFalse(TransactionType type);

    List<Transaction> findByCategoryAndDeletedFalse(String category);

    List<Transaction> findByDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'INCOME' AND t.deleted = false")
    BigDecimal sumIncome();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'EXPENSE' AND t.deleted = false")
    BigDecimal sumExpenses();

    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.deleted = false GROUP BY t.category")
    List<Object[]> sumByCategory();

    @Query(value = "SELECT strftime('%Y-%m', date) as month, type, SUM(amount) " +
            "FROM transactions WHERE deleted = 0 " +
            "GROUP BY month, type ORDER BY month DESC LIMIT 12",
            nativeQuery = true)
    List<Object[]> monthlyTrends();
}
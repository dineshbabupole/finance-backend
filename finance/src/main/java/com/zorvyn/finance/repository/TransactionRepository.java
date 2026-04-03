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

    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t WHERE t.type = 'INCOME' AND t.deleted = 0",
            nativeQuery = true)
    BigDecimal sumIncome();

    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t WHERE t.type = 'EXPENSE' AND t.deleted = 0",
            nativeQuery = true)
    BigDecimal sumExpenses();
    @Query(value = "SELECT t.category, SUM(t.amount) FROM transactions t WHERE t.deleted = 0 GROUP BY t.category",
            nativeQuery = true)
    List<Object[]> sumByCategory();
    @Query(value = "SELECT t.date as month, t.type, SUM(t.amount) " +
            "FROM transactions t WHERE t.deleted = 0 " +
            "GROUP BY month, t.type ORDER BY month DESC LIMIT 12",
            nativeQuery = true)
    List<Object[]> monthlyTrends();

}
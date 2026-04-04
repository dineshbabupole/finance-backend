package com.dinesh.finance.service;

import com.dinesh.finance.dto.DashboardSummary;
import com.dinesh.finance.model.Transaction;
import com.dinesh.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    // summary data for dashboard
    // all roles can access this - viewer, analyst, admin
    public DashboardSummary getSummary() {

        BigDecimal totalIncome = transactionRepository.calculateTotalIncome();
        BigDecimal totalExpenses = transactionRepository.calculateTotalExpenses();

        // net balance is simply income minus expenses
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // counting only non deleted transactions
        long totalTransactions = transactionRepository.findByDeletedFalse().size();

        return new DashboardSummary(totalIncome, totalExpenses, netBalance, totalTransactions);
    }

    // shows how much was spent or earned in each category
    // only analyst and admin can access this
    public Map<String, BigDecimal> getCategoryWiseTotals() {

        List<Object[]> categoryData = transactionRepository.totalAmountByCategory();

        // using LinkedHashMap to keep the order - highest category comes first
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();

        for (Object[] row : categoryData) {

            // skipping if category name or amount is null to avoid errors
            if (row[0] == null || row[1] == null) {
                continue;
            }

            String categoryName = row[0].toString();
            BigDecimal categoryAmount = new BigDecimal(row[1].toString());

            categoryTotals.put(categoryName, categoryAmount);
        }

        return categoryTotals;
    }

    // shows income and expense totals month by month
    // useful for analyst to see financial trends over time
    // only analyst and admin can access this
    public Map<String, Map<String, BigDecimal>> getMonthWiseTrends() {

        List<Object[]> trendData = transactionRepository.monthlyTrends();

        // outer map is month, inner map is type (INCOME/EXPENSE) with amount
        // example: { "2026-03": { "INCOME": 50000, "EXPENSE": 10000 } }
        Map<String, Map<String, BigDecimal>> monthlyTrends = new LinkedHashMap<>();

        for (Object[] row : trendData) {

            // skipping incomplete rows to avoid null pointer errors
            if (row[0] == null || row[1] == null || row[2] == null) {
                continue;
            }

            String month = row[0].toString();
            String transactionType = row[1].toString();
            BigDecimal monthlyAmount = new BigDecimal(row[2].toString());

            // if month key does not exist create a new map for it
            // then put the transaction type and amount inside it
            monthlyTrends
                    .computeIfAbsent(month, k -> new LinkedHashMap<>())
                    .put(transactionType, monthlyAmount);
        }

        return monthlyTrends;
    }
    public List<Transaction> recent(){
        return transactionRepository.recentActivity();
    }
}
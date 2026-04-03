package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.DashboardSummary;
import com.zorvyn.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    // ─── SUMMARY ──────────────────────────────────────────
    // For VIEWER, ANALYST, ADMIN
    public DashboardSummary getSummary() {
        BigDecimal totalIncome   = transactionRepository.sumIncome();
        BigDecimal totalExpenses = transactionRepository.sumExpenses();
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);
        long total               = transactionRepository.findByDeletedFalse().size();

        return new DashboardSummary(totalIncome, totalExpenses, netBalance, total);
    }

    // ─── CATEGORY WISE TOTALS ─────────────────────────────
    // For ANALYST, ADMIN
    public Map<String, BigDecimal> getCategoryTotals() {
        List<Object[]> rows = transactionRepository.sumByCategory();

        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String category     = (String) row[0];
            BigDecimal total    = (BigDecimal) row[1];
            result.put(category, total);
        }

        return result;
    }

    // ─── MONTHLY TRENDS ───────────────────────────────────
    // For ANALYST, ADMIN
    public Map<String, Map<String, BigDecimal>> getMonthlyTrends() {
        List<Object[]> rows = transactionRepository.monthlyTrends();

        // Structure: { "2026-03": { "INCOME": 50000, "EXPENSE": 10000 } }
        Map<String, Map<String, BigDecimal>> result = new LinkedHashMap<>();

        for (Object[] row : rows) {
            String month        = (String) row[0];
            String type         = (String) row[1];
            BigDecimal amount   = new BigDecimal(row[2].toString());

            result
                    .computeIfAbsent(month, k -> new LinkedHashMap<>())
                    .put(type, amount);
        }

        return result;
    }
}
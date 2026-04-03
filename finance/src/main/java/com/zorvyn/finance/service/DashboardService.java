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
            // Skip null categories
            if (row[0] == null || row[1] == null) {
                continue;
            }

            String category    = row[0].toString();
            BigDecimal total   = new BigDecimal(row[1].toString());
            result.put(category, total);
        }

        return result;
    }

    // ─── MONTHLY TRENDS ───────────────────────────────────
    // For ANALYST, ADMIN
    public Map<String, Map<String, BigDecimal>> getMonthlyTrends() {

        List<Object[]> rows = transactionRepository.monthlyTrends();

        Map<String, Map<String, BigDecimal>> result = new LinkedHashMap<>();

        for (Object[] row : rows) {
            // Skip rows where month or type is null
            if (row[0] == null || row[1] == null || row[2] == null) {
                continue;
            }

            String month      = row[0].toString();
            String type       = row[1].toString();
            BigDecimal amount = new BigDecimal(row[2].toString());

            result
                    .computeIfAbsent(month, k -> new LinkedHashMap<>())
                    .put(type, amount);
        }

        return result;
    }
}
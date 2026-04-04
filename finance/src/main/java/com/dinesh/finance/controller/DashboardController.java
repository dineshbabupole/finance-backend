package com.dinesh.finance.controller;

import com.dinesh.finance.dto.DashboardSummary;
import com.dinesh.finance.model.Transaction;
import com.dinesh.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // ─── SUMMARY ──────────────────────────────────────────
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<DashboardSummary> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getRecent() {
        return ResponseEntity.ok(dashboardService.recent());
    }

    // ─── CATEGORY TOTALS ──────────────────────────────────
    @GetMapping("/by-category")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<?> getByCategory() {
        return ResponseEntity.ok(dashboardService.getCategoryWiseTotals());
    }

    // ─── MONTHLY TRENDS ───────────────────────────────────
    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<?> getTrends() {
        return ResponseEntity.ok(dashboardService.getMonthWiseTrends());
    }
}
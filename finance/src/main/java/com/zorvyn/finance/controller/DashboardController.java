package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.DashboardSummary;
import com.zorvyn.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // ─── CATEGORY TOTALS ──────────────────────────────────
    @GetMapping("/by-category")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<Map<String, BigDecimal>> getByCategory() {
        return ResponseEntity.ok(dashboardService.getCategoryTotals());
    }

    // ─── MONTHLY TRENDS ───────────────────────────────────
    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getTrends() {
        return ResponseEntity.ok(dashboardService.getMonthlyTrends());
    }
}
package org.avi1606.financedataprocessing.controller;

import org.avi1606.financedataprocessing.dto.DashboardSummary;
import org.avi1606.financedataprocessing.dto.CategoryTotal;
import org.avi1606.financedataprocessing.dto.MonthlyTrend;
import org.avi1606.financedataprocessing.dto.TransactionMapper;
import org.avi1606.financedataprocessing.dto.TransactionResponse;
import org.avi1606.financedataprocessing.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        try {
            DashboardSummary summary = dashboardService.getSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/category-totals")
    public ResponseEntity<List<CategoryTotal>> getCategoryTotals() {
        try {
            List<CategoryTotal> totals = dashboardService.getCategoryTotals();
            return ResponseEntity.ok(totals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions() {
        try {
            List<TransactionResponse> transactions = dashboardService.getRecentTransactions()
                    .stream()
                    .map(TransactionMapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<List<MonthlyTrend>> getMonthlyTrends() {
        try {
            List<MonthlyTrend> trends = dashboardService.getMonthlyTrends();
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


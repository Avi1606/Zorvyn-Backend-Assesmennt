package org.avi1606.financedataprocessing.controller;

import org.avi1606.financedataprocessing.dto.TransactionMapper;
import org.avi1606.financedataprocessing.dto.TransactionRequest;
import org.avi1606.financedataprocessing.dto.TransactionResponse;
import org.avi1606.financedataprocessing.service.TransactionService;
import org.avi1606.financedataprocessing.enums.Category;
import org.avi1606.financedataprocessing.enums.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        try {
            // Get current user ID from JWT token
            UUID userId = getCurrentUserId();
            var transaction = transactionService.createTransaction(request, userId);
            TransactionResponse response = TransactionMapper.toResponse(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<TransactionResponse> transactions;
            if (type != null || category != null || startDate != null || endDate != null) {
                transactions = transactionService.getTransactionsByFilters(type, category, startDate, endDate)
                        .stream()
                        .map(TransactionMapper::toResponse)
                        .collect(Collectors.toList());
            } else {
                transactions = transactionService.getAllTransactions()
                        .stream()
                        .map(TransactionMapper::toResponse)
                        .collect(Collectors.toList());
            }
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        try {
            var transaction = transactionService.getTransactionById(id);
            TransactionResponse response = TransactionMapper.toResponse(transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request) {
        try {
            var transaction = transactionService.updateTransaction(id, request);
            TransactionResponse response = TransactionMapper.toResponse(transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTransaction(@PathVariable UUID id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/insights")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getInsights() {
        try {
            List<TransactionResponse> insights = transactionService.getRecentTransactions(10)
                    .stream()
                    .map(TransactionMapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // principal is the userId string we stored in JWT
        return UUID.fromString((String) authentication.getPrincipal());
    }
}



package org.avi1606.financedataprocessing.service;

import lombok.extern.slf4j.Slf4j;
import org.avi1606.financedataprocessing.dto.CategoryTotal;
import org.avi1606.financedataprocessing.dto.DashboardSummary;
import org.avi1606.financedataprocessing.dto.MonthlyTrend;
import org.avi1606.financedataprocessing.enums.TransactionType;
import org.avi1606.financedataprocessing.model.Transaction;
import org.avi1606.financedataprocessing.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardService {
    @Autowired
    private TransactionRepository transactionRepository;

    public DashboardSummary getSummary() {
        BigDecimal totalIncome = transactionRepository.calculateTotalIncome();
        BigDecimal totalExpense = transactionRepository.calculateTotalExpense();
        BigDecimal netBalance = totalIncome.subtract(totalExpense);
        log.info("Dashboard summary retrieved - Income: {}, Expense: {}, Balance: {}",
                totalIncome, totalExpense, netBalance);
        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .build();
    }

    public List<CategoryTotal> getCategoryTotals() {
        List<Transaction> transactions = transactionRepository.findAllByIsDeletedFalseOrderByDateDesc();
        Map<String, CategoryTotal> categoryMap = new LinkedHashMap<>();
        transactions.forEach(transaction -> {
            String category = transaction.getCategory().toString();
            categoryMap.putIfAbsent(category, CategoryTotal.builder()
                    .category(category)
                    .total(BigDecimal.ZERO)
                    .count(0L)
                    .build());
            CategoryTotal ct = categoryMap.get(category);
            ct.setTotal(ct.getTotal().add(transaction.getAmount()));
            ct.setCount(ct.getCount() + 1);
        });
        return categoryMap.values().stream()
                .sorted((c1, c2) -> c2.getTotal().compareTo(c1.getTotal()))
                .collect(Collectors.toList());
    }

    public List<Transaction> getRecentTransactions() {
        return transactionRepository.findRecentTransactions(10);
    }

    public List<MonthlyTrend> getMonthlyTrends() {
        List<Transaction> transactions = transactionRepository.findAllByIsDeletedFalseOrderByDateDesc();
        Map<YearMonth, Map<String, BigDecimal>> monthlyData = new TreeMap<>();
        transactions.forEach(transaction -> {
            YearMonth yearMonth = YearMonth.from(transaction.getDate());
            monthlyData.putIfAbsent(yearMonth, new HashMap<>());
            Map<String, BigDecimal> monthData = monthlyData.get(yearMonth);
            String type = transaction.getType().toString();
            monthData.put(type, monthData.getOrDefault(type, BigDecimal.ZERO).add(transaction.getAmount()));
        });
        List<MonthlyTrend> trends = monthlyData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    YearMonth yearMonth = entry.getKey();
                    Map<String, BigDecimal> data = entry.getValue();
                    BigDecimal income = data.getOrDefault("INCOME", BigDecimal.ZERO);
                    BigDecimal expense = data.getOrDefault("EXPENSE", BigDecimal.ZERO);
                    BigDecimal netChange = income.subtract(expense);
                    return MonthlyTrend.builder()
                            .month(yearMonth.toString())
                            .income(income)
                            .expense(expense)
                            .netChange(netChange)
                            .build();
                })
                .collect(Collectors.toList());
        log.info("Monthly trends retrieved: {} months", trends.size());
        return trends;
    }

    public List<CategoryTotal> getSpendingInsights() {
        List<Transaction> expenses = transactionRepository
                .findByTypeAndIsDeletedFalseOrderByDateDesc(TransactionType.EXPENSE);
        Map<String, CategoryTotal> categoryMap = new LinkedHashMap<>();
        expenses.forEach(transaction -> {
            String category = transaction.getCategory().toString();
            categoryMap.putIfAbsent(category, CategoryTotal.builder()
                    .category(category)
                    .total(BigDecimal.ZERO)
                    .count(0L)
                    .build());
            CategoryTotal ct = categoryMap.get(category);
            ct.setTotal(ct.getTotal().add(transaction.getAmount()));
            ct.setCount(ct.getCount() + 1);
        });
        return categoryMap.values().stream()
                .sorted((c1, c2) -> c2.getTotal().compareTo(c1.getTotal()))
                .collect(Collectors.toList());
    }

    public List<CategoryTotal> getIncomeInsights() {
        List<Transaction> incomes = transactionRepository
                .findByTypeAndIsDeletedFalseOrderByDateDesc(TransactionType.INCOME);
        Map<String, CategoryTotal> categoryMap = new LinkedHashMap<>();
        incomes.forEach(transaction -> {
            String category = transaction.getCategory().toString();
            categoryMap.putIfAbsent(category, CategoryTotal.builder()
                    .category(category)
                    .total(BigDecimal.ZERO)
                    .count(0L)
                    .build());
            CategoryTotal ct = categoryMap.get(category);
            ct.setTotal(ct.getTotal().add(transaction.getAmount()));
            ct.setCount(ct.getCount() + 1);
        });
        return categoryMap.values().stream()
                .sorted((c1, c2) -> c2.getTotal().compareTo(c1.getTotal()))
                .collect(Collectors.toList());
    }
}


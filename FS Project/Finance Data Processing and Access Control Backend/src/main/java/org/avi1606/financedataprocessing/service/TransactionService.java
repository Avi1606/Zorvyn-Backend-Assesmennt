package org.avi1606.financedataprocessing.service;

import lombok.extern.slf4j.Slf4j;
import org.avi1606.financedataprocessing.dto.TransactionRequest;
import org.avi1606.financedataprocessing.enums.Category;
import org.avi1606.financedataprocessing.enums.TransactionType;
import org.avi1606.financedataprocessing.exception.ResourceNotFoundException;
import org.avi1606.financedataprocessing.model.Transaction;
import org.avi1606.financedataprocessing.model.User;
import org.avi1606.financedataprocessing.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Transaction createTransaction(TransactionRequest transactionRequest, UUID userId) {
        User user = userService.getUserById(userId);
        Transaction transaction = Transaction.builder()
                .amount(transactionRequest.getAmount())
                .type(transactionRequest.getType())
                .category(transactionRequest.getCategory())
                .date(transactionRequest.getDate())
                .notes(transactionRequest.getNotes())
                .createdBy(user)
                .isDeleted(false)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created: {} by user: {}", savedTransaction.getId(), userId);
        return savedTransaction;
    }

    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findByIdAndNotDeleted(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByIsDeletedFalseOrderByDateDesc();
    }

    public List<Transaction> getTransactionsByFilters(TransactionType type, Category category,
                                                       LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByMultipleCriteria(type, category, startDate, endDate);
    }

    @Transactional
    public Transaction updateTransaction(UUID transactionId, TransactionRequest transactionRequest) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(transactionRequest.getType());
        transaction.setCategory(transactionRequest.getCategory());
        transaction.setDate(transactionRequest.getDate());
        transaction.setNotes(transactionRequest.getNotes());
        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transaction updated: {}", transactionId);
        return updatedTransaction;
    }

    @Transactional
    public void deleteTransaction(UUID transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);
        log.info("Transaction soft deleted: {}", transactionId);
    }

    public List<Transaction> getRecentTransactions(int limit) {
        return transactionRepository.findRecentTransactions(limit);
    }
}


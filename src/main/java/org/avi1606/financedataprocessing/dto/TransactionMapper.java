package org.avi1606.financedataprocessing.dto;

import org.avi1606.financedataprocessing.model.Transaction;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {
    public TransactionResponse toDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .date(transaction.getDate())
                .notes(transaction.getNotes())
                .createdById(transaction.getCreatedBy().getId())
                .createdByName(transaction.getCreatedBy().getName())
                .createdAt(transaction.getCreatedAt())
                .isDeleted(transaction.getIsDeleted())
                .build();
    }

    public List<TransactionResponse> toDtoList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Static helper methods for convenient use in controllers
    public static TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .date(transaction.getDate())
                .notes(transaction.getNotes())
                .createdById(transaction.getCreatedBy().getId())
                .createdByName(transaction.getCreatedBy().getName())
                .createdAt(transaction.getCreatedAt())
                .isDeleted(transaction.getIsDeleted())
                .build();
    }
}



package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.*;
import com.zorvyn.finance.model.*;
import com.zorvyn.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    // ─── CREATE ───────────────────────────────────────────
    public TransactionResponse create(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .deleted(false)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    // ─── GET ALL (with optional filters) ──────────────────
    public List<TransactionResponse> getAll(TransactionType type,
                                            String category,
                                            LocalDate start,
                                            LocalDate end) {
        List<Transaction> results;

        if (type != null) {
            results = transactionRepository.findByTypeAndDeletedFalse(type);

        } else if (category != null) {
            results = transactionRepository.findByCategoryAndDeletedFalse(category);

        } else if (start != null && end != null) {
            results = transactionRepository.findByDateBetweenAndDeletedFalse(start, end);

        } else {
            results = transactionRepository.findByDeletedFalse();
        }

        return results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── GET BY ID ────────────────────────────────────────
    public TransactionResponse getById(Long id) {
        Transaction transaction = findActiveById(id);
        return toResponse(transaction);
    }

    // ─── UPDATE ───────────────────────────────────────────
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction transaction = findActiveById(id);

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setNotes(request.getNotes());

        return toResponse(transactionRepository.save(transaction));
    }

    // ─── SOFT DELETE ──────────────────────────────────────
    public void delete(Long id) {
        Transaction transaction = findActiveById(id);
        transaction.setDeleted(true);           // not physically removed
        transactionRepository.save(transaction);
    }

    // ─── HELPERS ──────────────────────────────────────────
    private Transaction findActiveById(Long id) {
        return transactionRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getType(),
                t.getCategory(),
                t.getDate(),
                t.getNotes()
        );
    }
}
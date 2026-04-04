package com.dinesh.finance.service;

import com.dinesh.finance.dto.TransactionRequest;
import com.dinesh.finance.dto.TransactionResponse;
import com.dinesh.finance.model.Transaction;
import com.dinesh.finance.model.TransactionType;
import com.dinesh.finance.dto.*;
import com.dinesh.finance.model.*;
import com.dinesh.finance.repository.TransactionRepository;
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
    // get all active transactions
    public List<TransactionResponse> getAll() {
        return transactionRepository.findByDeletedFalse()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // get transactions filtered by type
    public List<TransactionResponse> getByType(TransactionType type) {
        return transactionRepository.findByTypeAndDeletedFalse(type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // get transactions filtered by category
    public List<TransactionResponse> getByCategory(String category) {
        return transactionRepository.findByCategoryAndDeletedFalse(category)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // get transactions within a date range
    public List<TransactionResponse> getByDateRange(LocalDate start, LocalDate end) {
        if(end == null){
            end=start;
        }
        return transactionRepository.findByDateBetweenAndDeletedFalse(start, end)
                .stream()
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
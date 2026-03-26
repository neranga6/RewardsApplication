package com.charter.rewardsapplication.service;

import com.charter.rewardsapplication.exception.ResourceNotFoundException;
import com.charter.rewardsapplication.model.CreateTransactionRequest;
import com.charter.rewardsapplication.model.CustomerRewardsSummary;
import com.charter.rewardsapplication.model.MonthlyPoints;
import com.charter.rewardsapplication.model.PurchaseTransaction;
import com.charter.rewardsapplication.repo.PurchaseTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.charter.rewardsapplication.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class RewardCalculationService {

    private final PurchaseTransactionRepository repository;

    /**
     * Calculates reward points based on business rules.
     */
    public int calculatePoints(double amount) {
        return java.util.stream.Stream.of(
                        (int) Math.max(0, amount - UPPER_THRESHOLD) * UPPER_RATE,
                        (int) Math.max(0, Math.min(amount, UPPER_THRESHOLD) - LOWER_THRESHOLD) * LOWER_RATE
                ).mapToInt(Integer::intValue)
                .sum();
    }
    /**
     * Returns rewards for all customers.
     */
    @Transactional(readOnly = true)
    public List<CustomerRewardsSummary> getAllRewards() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(PurchaseTransaction::getCustomerId))
                .values()
                .stream()
                .map(this::buildSummary)
                .toList();
    }

    /**
     * Returns rewards for a single customer.
     */
    @Transactional(readOnly = true)
    public CustomerRewardsSummary getRewardsByCustomer(Long customerId) {

        List<PurchaseTransaction> txs = repository.findByCustomerId(customerId);

        return Optional.of(txs)
                .filter(list -> !list.isEmpty())
                .map(this::buildSummary)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found: " + customerId));
    }

    /**
     * Creates transaction with rollback support.
     */

    @Transactional(rollbackFor = Exception.class)
    public PurchaseTransaction createTransaction(CreateTransactionRequest req) {

        if (req.customerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (req.customerName() == null || req.customerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (req.transactionDate() == null) {
            throw new IllegalArgumentException("Transaction date is required");
        }
        if (req.amount() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setCustomerId(req.customerId());
        tx.setCustomerName(req.customerName());
        tx.setAmount(req.amount());
        tx.setTransactionDate(req.transactionDate());

        return repository.save(tx);
    }


    /**
     * Builds reward summary per customer.
     */
    private CustomerRewardsSummary buildSummary(List<PurchaseTransaction> txs) {

        PurchaseTransaction first = txs.get(0);

        Map<Month, Integer> monthly = txs.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getMonth(),
                        Collectors.summingInt(t -> calculatePoints(t.getAmount()))
                ));

        List<MonthlyPoints> monthlyPoints = monthly.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new MonthlyPoints(
                        e.getKey().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        e.getValue()))
                .toList();

        int total = monthlyPoints.stream().mapToInt(MonthlyPoints::points).sum();

        return new CustomerRewardsSummary(
                first.getCustomerId(),
                first.getCustomerName(),
                monthlyPoints,
                total
        );
    }
}
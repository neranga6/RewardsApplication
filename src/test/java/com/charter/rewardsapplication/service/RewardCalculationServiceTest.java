package com.charter.rewardsapplication.service;

import com.charter.rewardsapplication.exception.ResourceNotFoundException;
import com.charter.rewardsapplication.model.CreateTransactionRequest;
import com.charter.rewardsapplication.model.CustomerRewardsSummary;
import com.charter.rewardsapplication.model.PurchaseTransaction;
import com.charter.rewardsapplication.repo.PurchaseTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardCalculationServiceTest {

    @Mock
    private PurchaseTransactionRepository repository;

    @InjectMocks
    private RewardCalculationService service;

    private PurchaseTransaction tx1;
    private PurchaseTransaction tx2;
    private PurchaseTransaction tx3;

    @BeforeEach
    void setUp() {
        tx1 = PurchaseTransaction.builder()
                .id(1L)
                .customerId(101L)
                .customerName("John")
                .amount(120.0)
                .transactionDate(LocalDate.of(2026, 1, 10))
                .build();

        tx2 = PurchaseTransaction.builder()
                .id(2L)
                .customerId(101L)
                .customerName("John")
                .amount(75.0)
                .transactionDate(LocalDate.of(2026, 1, 15))
                .build();

        tx3 = PurchaseTransaction.builder()
                .id(3L)
                .customerId(102L)
                .customerName("Mary")
                .amount(130.0)
                .transactionDate(LocalDate.of(2026, 2, 11))
                .build();
    }

    @Test
    void calculatePoints_ShouldReturnZero_WhenAmountIs50OrLess() {
        assertThat(service.calculatePoints(50)).isEqualTo(0);
        assertThat(service.calculatePoints(45)).isEqualTo(0);
    }

    @Test
    void calculatePoints_ShouldReturnCorrectPoints_WhenAmountBetween51And100() {
        assertThat(service.calculatePoints(75)).isEqualTo(25);
        assertThat(service.calculatePoints(100)).isEqualTo(50);
    }

    @Test
    void calculatePoints_ShouldReturnCorrectPoints_WhenAmountGreaterThan100() {
        assertThat(service.calculatePoints(120)).isEqualTo(90);
        assertThat(service.calculatePoints(130)).isEqualTo(110);
    }

    @Test
    void getAllRewards_ShouldReturnSummariesGroupedByCustomer() {
        when(repository.findAll()).thenReturn(List.of(tx1, tx2, tx3));

        List<CustomerRewardsSummary> result = service.getAllRewards();

        assertThat(result).hasSize(2);

        CustomerRewardsSummary john = result.stream()
                .filter(r -> r.customerId().equals(101L))
                .findFirst()
                .orElseThrow();

        CustomerRewardsSummary mary = result.stream()
                .filter(r -> r.customerId().equals(102L))
                .findFirst()
                .orElseThrow();

        assertThat(john.customerName()).isEqualTo("John");
        assertThat(john.totalPoints()).isEqualTo(115);
        assertThat(john.monthlyPoints()).hasSize(1);
        assertThat(john.monthlyPoints().get(0).month()).isEqualTo("January");
        assertThat(john.monthlyPoints().get(0).points()).isEqualTo(115);

        assertThat(mary.customerName()).isEqualTo("Mary");
        assertThat(mary.totalPoints()).isEqualTo(110);
    }

    @Test
    void getRewardsByCustomer_ShouldReturnSummary_WhenCustomerExists() {
        when(repository.findByCustomerId(101L)).thenReturn(List.of(tx1, tx2));

        CustomerRewardsSummary result = service.getRewardsByCustomer(101L);

        assertThat(result.customerId()).isEqualTo(101L);
        assertThat(result.customerName()).isEqualTo("John");
        assertThat(result.totalPoints()).isEqualTo(115);
        assertThat(result.monthlyPoints()).hasSize(1);
    }

    @Test
    void getRewardsByCustomer_ShouldThrowException_WhenCustomerDoesNotExist() {
        when(repository.findByCustomerId(999L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.getRewardsByCustomer(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found: 999");
    }

    @Test
    void createTransaction_ShouldSaveAndReturnTransaction_WhenRequestIsValid() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                10L,
                200L,
                "Alice",
                120.0,
                LocalDate.of(2026, 3, 26)
        );

        PurchaseTransaction saved = PurchaseTransaction.builder()
                .id(10L)
                .customerId(200L)
                .customerName("Alice")
                .amount(120.0)
                .transactionDate(LocalDate.of(2026, 3, 26))
                .build();

        when(repository.save(any(PurchaseTransaction.class))).thenReturn(saved);

        PurchaseTransaction result = service.createTransaction(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getCustomerId()).isEqualTo(200L);
        assertThat(result.getCustomerName()).isEqualTo("Alice");

        verify(repository, times(1)).save(any(PurchaseTransaction.class));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenAmountIsNegative() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                11L,
                201L,
                "Bob",
                -10.0,
                LocalDate.of(2026, 3, 26)
        );

        assertThatThrownBy(() -> service.createTransaction(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount cannot be negative");

        verify(repository, never()).save(any(PurchaseTransaction.class));
    }
}
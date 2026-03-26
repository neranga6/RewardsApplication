package com.charter.rewardsapplication.repo;


import com.charter.rewardsapplication.model.PurchaseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {

    List<PurchaseTransaction> findByCustomerId(Long customerId);
}

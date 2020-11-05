package com.jm.online_store.repository;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String username);

    List<Customer> findByDayOfWeekForStockSend(Customer.DayOfWeekForStockSend dayOfWeekForStockSend);
}

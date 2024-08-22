package com.csi.daos;

import java.util.List;

import com.csi.utils.entities.Customer;
import com.csi.utils.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {
	List<Order> findByCustomer(Customer customer);
}

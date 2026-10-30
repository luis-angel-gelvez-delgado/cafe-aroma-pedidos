package com.cafearoma.pedidos.repository;

import com.cafearoma.pedidos.model.CustomerOrder;
import com.cafearoma.pedidos.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/** Spring Data genera el SQL a partir del nombre de cada método. */
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findAllByOrderByIdDesc();

    List<CustomerOrder> findByStatusOrderByIdAsc(OrderStatus status);

    List<CustomerOrder> findByStatusInOrderByIdAsc(Collection<OrderStatus> statuses);

    List<CustomerOrder> findByStatusInOrderByIdDesc(Collection<OrderStatus> statuses);
}

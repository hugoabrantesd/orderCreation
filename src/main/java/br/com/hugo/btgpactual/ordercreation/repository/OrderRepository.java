package br.com.hugo.btgpactual.ordercreation.repository;

import br.com.hugo.btgpactual.ordercreation.domain.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<OrderEntity, Long> {

    Page<OrderEntity> findByCustomerId(Long customerId, PageRequest pageRequest);

}

package org.flow_manager.dao;

import org.flow_manager.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutBoxEventRepository extends JpaRepository<OutboxEvent, Long> {
}

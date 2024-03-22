package com.dorflander.tutorial.jpa.TransactionDemo.repo;

import com.dorflander.tutorial.jpa.TransactionDemo.entity.EventLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogRepo extends JpaRepository<EventLogEntity, Long> {
    /**
     * You don't see here any sql call, because JPA generates them for you.
     */
}

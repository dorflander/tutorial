package com.dorflander.tutorial.jpa.TransactionDemo.repo;

import com.dorflander.tutorial.jpa.TransactionDemo.entity.Entity1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo1 extends JpaRepository<Entity1, Long> {
    /**
     * You don't see here any sql call, because JPA generates them for you.
     */
}

package com.dorflander.tutorial.jpa.TransactionDemo.service;

import com.dorflander.tutorial.jpa.TransactionDemo.entity.Entity1;
import com.dorflander.tutorial.jpa.TransactionDemo.repo.Repo1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Slf4j
@Service
public class SubService {

    @Autowired
    Repo1 repo1;

    /**
     * read the reference
     * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Propagation.html
     * https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html
     */


    //Default
    //Support a current transaction, create a new one if none exists.
    //@Transactional(propagation = Propagation.REQUIRED)

    //Support a current transaction, execute non-transactionally if none exists.
    //@Transactional(propagation = Propagation.SUPPORTS)

    //Support a current transaction, throw an exception if none exists.
    //@Transactional(propagation = Propagation.MANDATORY)

    //Create a new transaction, and suspend the current transaction if one exists.
    @Transactional(propagation = Propagation.REQUIRES_NEW)

    //Execute non-transactionally, suspend the current transaction if one exists.
    //You can switch off the transaction for this method
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)

    //Execute non-transactionally, throw an exception if a transaction exists.
    //@Transactional(propagation = Propagation.NEVER)

    //Execute within a nested transaction if a current transaction exists, behave like REQUIRED otherwise.
    //@Transactional(propagation = Propagation.NESTED)
    public void insertEntity1() {
        Entity1 entity1 = new Entity1();
        entity1.setName("banana");
        entity1.setPrice(101);
        entity1 = repo1.saveAndFlush(entity1);
    }

    //Default - Propagation.REQUIRED
    //Support a current transaction, create a new one if none exists.
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertEntity1ButThrowsAnException() {
        Entity1 entity1 = new Entity1();
        entity1.setName("melon");
        entity1.setPrice(101);
        entity1 = repo1.saveAndFlush(entity1);

        log.debug("trx: " + TransactionSynchronizationManager.getCurrentTransactionName());

        throw new NullPointerException("Simulate a kind of Exception");
    }

}

package com.dorflander.tutorial.jpa.TransactionDemo.service;

import com.dorflander.tutorial.jpa.TransactionDemo.entity.Entity1;
import com.dorflander.tutorial.jpa.TransactionDemo.entity.EventLogEntity;
import com.dorflander.tutorial.jpa.TransactionDemo.exception.MyBusinessException;
import com.dorflander.tutorial.jpa.TransactionDemo.repo.EventLogRepo;
import com.dorflander.tutorial.jpa.TransactionDemo.repo.Repo1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Slf4j
@Service
public class TestService {

    @Autowired
    Repo1 repo1;

    @Autowired
    EventLogRepo eventLogRepo;

    @Autowired
    TestService2 testService2;

    @Autowired
    SubService subService;


    public void printDBRecords() throws InterruptedException {
        log.debug("\n\n\n\n\n\n");
        log.debug("#");
        log.debug("# Database Content");
        log.debug("#");
        //log.debug("Entity1 (table1)");
        log.debug("select * from table1");
        log.debug("--------------------");


        List<Entity1> list = repo1.findAll();
        log.debug("Number of records: " + list.size());
        for (Entity1 e : list) {
            log.debug(e.toString());
        }

        log.debug("");
        log.debug("");

        //log.debug("EventLogEntity (event_log)");
        log.debug("select * from event_log");
        log.debug("------------------------");

        List<EventLogEntity> eventLogEntityListlist = eventLogRepo.findAll();
        log.debug("Number of records: " + eventLogEntityListlist.size());
        for (EventLogEntity e : eventLogEntityListlist) {
            log.debug(e.toString());
        }
        log.debug("\n\n\n");

    }


    /**
     * Business logic requirement:
     * if there is no error then all the records have to be inserted
     * if error happens then no record should be inserted
     * <p>
     * Most cases the business logic dictates that records can only be added if all the operations were successful.
     * You want here all or nothing. Here you need to use transaction
     */
    @Transactional
    //(behind the scenes) begin transaction
    public void test1_whyYouNeedIt() throws Exception {

        //insert into table1....
        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        //insert into table1....
        entity1 = new Entity1();
        entity1.setName("banana");
        entity1.setPrice(101);
        entity1 = repo1.saveAndFlush(entity1);

        //todo test0
        //Let's say some web service call is here and simpulate an error
        //.....callAWebService();
//        if (true) {
//            //callAWebService(entity1.getId());
//            throw new RuntimeException("Simulate a webService call Exception");
//        }

        EventLogEntity eventLogEntity = new EventLogEntity();
        eventLogEntity.setDesc("we have 3 records saved");
        eventLogEntity = eventLogRepo.saveAndFlush(eventLogEntity);

        //todo test2
        //Let's say some web service call is here and simpulate an error
        //Note: Mind what if it's NOT a RuntimeException, but something else
        //.....callAWebService();
        //throw new Exception("Simulate a webService call Exception");
        throw new NullPointerException("Simulate a NullPointerException");
    }
    //(behind the scenes) commit or rollback

    /**
     * If you do not throw out the exception, the rollback won't work
     */
    @Transactional
    public void test3_error_with_catch() {
        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        entity1 = new Entity1();
        entity1.setName("banana");
        entity1.setPrice(101);
        entity1 = repo1.saveAndFlush(entity1);

        EventLogEntity eventLogEntity = new EventLogEntity();
        eventLogEntity.setDesc("we have 3 records saved");
        eventLogEntity = eventLogRepo.saveAndFlush(eventLogEntity);

        //
        // You catch the exception here, therefore the JPA will NOT know there was an error, and should rollback.
        //
        try {
            //Simulate a ws call, that goes wrong
            throw new RuntimeException("Simulate a webService call Exception");
        } catch (Exception e) {
            log.error("catch the exception, for some reason", e);

            //test
            throw e;
        }
    }

    /**
     * You can rollback the transaction, when you can not throw an exception for some reason.
     * This kind of behaviour is not recommended for new design.
     * Sometimes, like you have to modify an old code, and there is no other choice, you can do this
     */
    @Transactional
    public void test4_catch_and_rollback() {
        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        entity1 = new Entity1();
        entity1.setName("banana");
        entity1.setPrice(101);
        entity1 = repo1.saveAndFlush(entity1);

        EventLogEntity eventLogEntity = new EventLogEntity();
        eventLogEntity.setDesc("we have 3 records saved");
        eventLogEntity = eventLogRepo.saveAndFlush(eventLogEntity);

        //
        // This kind of behaviour is not recommended for new design.
        // Sometimes, like you have to modify an old code, and there is no other choice, you can do this
        //
        try {
            //Simulate a ws call, that goes wrong
            throw new RuntimeException("Simulate a webService call Exception");
        } catch (Exception e) {
            log.error("catch the exception", e);

            // Mark the transaction as rollback-only
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();


            log.info("Try to insert, will not work");

            //
            //Note: after marking the connection to rollback, you cannot use the connection
            //Some databases would throw an Exception here
            entity1 = new Entity1();
            entity1.setName("banana 2");
            entity1.setPrice(101);
            entity1 = repo1.saveAndFlush(entity1);

        }
    }

    /**
     * When you invoke a method within another method that's annotated with @Transactional, the called method inherits
     * the transactional context of the caller. This seamless propagation ensures consistent transaction management
     * across different layers of your application, maintaining data integrity and simplifying error handling by
     * allowing for centralized transaction control.
     * <p>
     * See the next example for proper usage
     */
    @Transactional
    public void test5_subMethods() {
        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        //do here some webServiceCall(...), sometimes does not work...

        //What is happening here, if the transaction propagation is Propagation.NEVER for insertEntity1 ?
        //Note: Very tricky, why ?
        //Hint: It's an internal call !!!!
        //Answer: As it is internal call, the proxy class will not be called, just yours.
        //So the @Transactional at insertEntity1 has no affect when calling from here.
        insertEntity1();

        //Simulate a ws call, that goes wrong
        if (true) {
            //callWebService(entity1.getId(), entity2.getId())
            throw new RuntimeException("Simulate a webService call Exception");
        }
    }

    /**
     * This will NOT work if you call it from this class !!!
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void insertEntity1() {
        Entity1 entity2 = new Entity1();
        entity2.setName("banana");
        entity2.setPrice(101);
        entity2 = repo1.saveAndFlush(entity2);
    }


    /**
     * When you invoke a method within another method that's annotated with @Transactional, the called method inherits
     * the transactional context of the caller. This seamless propagation ensures consistent transaction management
     * across different layers of your application, maintaining data integrity and simplifying error handling by
     * allowing for centralized transaction control.
     * <p>
     * And you can change that propagation
     */
    @Transactional
    public void test6_subMethods() {
        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        //do here some webServiceCall(...), sometimes does not work...

        //let's call another method with different transaction propagation properly
        subService.insertEntity1();

        //Simulate a ws call, that goes wrong
        if (true) {
            //callWebService(entity1.getId(), entity2.getId())
            throw new RuntimeException("Simulate a webService call Exception");
        }
    }

    /**
     * If a RuntimeException passes through an @Transactional annotated method, that will be rollback
     * even if you catch that exception later
     */
    @Transactional
    public void test7_subMethods() {
        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        //do here some webServiceCall(...), sometimes does not work...

        //let's call another method with different transaction propagation properly
        try {
            subService.insertEntity1ButThrowsAnException();
        } catch (Exception e) {
            log.error("", e);
        }
    }


    /**
     * In this scenario you want to
     * - All or nothing and You definitely want to insert an eventLog record, whether an Exception occurred or it was successful.
     * - For this, you can use the 'REQUIRES_NEW' propagation in the insertEventLog method.
     */
    @Transactional
    public void test8_insertInAnyCase() {
        boolean isOk = false;

        try {
            Entity1 entity1 = new Entity1();
            entity1.setName("apple");
            entity1.setPrice(100);
            entity1 = repo1.saveAndFlush(entity1);

            //do here some webServiceCall(...), sometimes does not work...

            Entity1 entity2 = new Entity1();
            entity2.setName("banana");
            entity2.setPrice(101);
            entity2 = repo1.saveAndFlush(entity2);

            //Simulate a ws call, that goes wrong
            if (true) {
                //callWebService(entity1.getId(), entity2.getId())
                throw new RuntimeException("Simulate a webService call Exception");
            }

            //let's say if you reach here, then it was all okay.
            isOk = true;

        } finally {
            testService2.insertEventLog(isOk);
        }
    }


    /**
     * You can change the default behaviour
     * default:
     *  RuntimeException: rollback
     *  other Exception: not rollback
     * @throws Exception
     */
    //@Transactional
    //@Transactional(rollbackFor = Exception.class)
    //@Transactional(noRollbackFor = MyBusinessException.class)
    public void test9_noRollbackFor() throws Exception {

        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        //do here some webServiceCall(...), sometimes does not work...

        Entity1 entity2 = new Entity1();
        entity2.setName("banana");
        entity2.setPrice(101);
        entity2 = repo1.saveAndFlush(entity2);

        //Simulate a ws call, that goes wrong
        if (true) {
            //callWebService(entity1.getId(), entity2.getId())
            throw new MyBusinessException("Simulate a webService call Exception");
            //throw new Exception("Simulate a webService call Exception 2");
        }

        //your code goes on
    }


    /**
     * You can specify several Exception classes but, keep it simple
     * Do NOT overcomplicate it
     */
    @Transactional(rollbackFor = {Exception.class, MyBusinessException.class}, noRollbackFor = RuntimeException.class)
    public void test10_rollBackFor_and_noRollbackFor() throws Exception {

        Entity1 entity1 = new Entity1();
        entity1.setName("apple");
        entity1.setPrice(100);
        entity1 = repo1.saveAndFlush(entity1);

        Entity1 entity2 = new Entity1();
        entity2.setName("banana");
        entity2.setPrice(101);
        entity2 = repo1.saveAndFlush(entity2);

        //Simulate a ws call, that goes wrong
        if (true) {
            //callWebService(entity1.getId(), entity2.getId())
            //throw new RuntimeException("Simulate a webService call Exception");
            //throw new MyBusinessException("Simulate a webService call Exception");
            //throw new MyOtherBusinessException("Simulate a webService call Exception");
            //throw new Exception("Simulate a webService call Exception 2");
        }
    }


}



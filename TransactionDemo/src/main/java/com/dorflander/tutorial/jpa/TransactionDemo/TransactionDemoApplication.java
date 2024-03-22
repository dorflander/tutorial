package com.dorflander.tutorial.jpa.TransactionDemo;

import com.dorflander.tutorial.jpa.TransactionDemo.entity.Entity1;
import com.dorflander.tutorial.jpa.TransactionDemo.entity.EventLogEntity;
import com.dorflander.tutorial.jpa.TransactionDemo.repo.EventLogRepo;
import com.dorflander.tutorial.jpa.TransactionDemo.repo.Repo1;
import com.dorflander.tutorial.jpa.TransactionDemo.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;


//this @Slf4j annotation is just creating this line automatically at compile time:
//          private static Logger log = LoggerFactory.getLogger(TransactionDemoApplication.class);
@Slf4j
@SpringBootApplication
public class TransactionDemoApplication implements CommandLineRunner {

    @Autowired
    TestService testService;

    @Autowired
    Repo1 repo1;

    @Autowired
    EventLogRepo eventLogRepo;

    /**
     * The program comes here first
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(TransactionDemoApplication.class, args);
    }

    /**
     * What is happening here?
     * <p>
     * - Spring Boot initializes all the Components, Services, ...
     * - Finding and Reading the application.properties (currently here: src/main/resources/application.properties)
     * - Uses the H2 memory database
     * - As the 'spring.jpa.defer-datasource-initialization=true' the jpa automatically created the tables
     * ...
     * - And when all done the program comes here.
     *
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.print("run begin");
        //You can start the H2 database as Server
        //Server server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();

        /**
         Have a look what is in the DB By opening the h2 console:
         http://localhost:8080/h2-console
         JDBC URL: jdbc:h2:mem:testdb
         User Name: sa
         password:
         */

        //
        // Begin testing
        //

        try {
            //MAIN TEST 1.
            //Why you need transaction? Many times you want all or nothing!
            //testService.test1_whyYouNeedIt();

            //MAIN TEST 2.
            //Not working if the method is in the same class
            //test2_notWorking_in_sameClass();

            //MAIN TEST 3.
            //Exception in a catch, guess what it's a catch :) - won't rollback
            //testService.test3_error_with_catch();

            //MAIN TEST 4.
            //When you need to catch, but you also need to rollback
            //testService.test4_catch_and_rollback();

            //MAIN TEST 5. - Sub methods again - Tricky - Does not what you expected
            //
            //testService.test5_subMethods();

            //MAIN TEST 6. - Sub methods again - Working
            //
            //testService.test6_subMethods();

            //MAIN TEST 7. - Sub methods again - catch
            //
            //testService.test7_subMethods();

            //MAIN TEST 8.
            //Avoid common mistake, howto do eventLog
            //create a new transaction in a transaction
            //testService.test8_insertInAnyCase();

            //MAIN TEST 9. - rollbackFor, noRollbackFor
            //testService.test9_noRollbackFor();

            //MAIN TEST 10. - rollbackFor and noRollbackFor combined
            //testService.test10_rollBackFor_and_noRollbackFor();

        } catch (Exception e) {
            log.error("", e);
        }

        //
        // End testing
        //

        //
        //Printing the result
        //
        testService.printDBRecords();


        /**
         I started this program as a server program, that you can use the H2 console running in the background.
         As the pom.xml contains this dependency, the program thinks it should listen on a port and waiting for requests.
         So the program will not quit after it leaves this run method.
         It is listening on the default port which is the 8080
         <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
         </dependency>
         */

        System.out.print("run end. Still listening on port 8080");
    }


    /**
     * Documentation about the calls
     * https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/transaction.html#tx-decl-explained
     *
     * In Spring Boot, when you mark a method as @Transactional, it means you're telling Spring to handle transactions automatically
     * whenever that method is called. However, this magic works because Spring creates a proxy around your class, and this proxy
     * is responsible for starting and managing the transactions. When you call a @Transactional method from another method in
     * the same class, you're bypassing this proxy because you're directly calling the method on the 'this' reference, not on
     * the proxy Spring created. As a result, the transaction management doesn't kick in, and your method won't run within a
     * transaction as you might expect. To ensure transaction management works, you should call the @Transactional method from a
     * method in a different class, where Spring can properly intercept the call through the proxy it created.
     */
    @Transactional
    public void test2_notWorking_in_sameClass() throws Exception {
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

        //todo test1
        //Let's say some web service call is here and simpulate an error
        throw new RuntimeException("Simulate a webService call Exception");

        //todo test2
        //Let's say some web service call is here and simpulate an error
        //Note: Mind what if it's NOT a RuntimeException, but something else
        //throw new Exception("Simulate a webService call Exception");
    }

}

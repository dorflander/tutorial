package com.dorflander.tutorial.jpa.TransactionDemo.service;

import com.dorflander.tutorial.jpa.TransactionDemo.entity.EventLogEntity;
import com.dorflander.tutorial.jpa.TransactionDemo.repo.EventLogRepo;
import com.dorflander.tutorial.jpa.TransactionDemo.repo.Repo1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class TestService2 {

    @Autowired
    Repo1 repo1;

    @Autowired
    EventLogRepo eventLogRepo;

    //@Transactional
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertEventLog(boolean isOk) {

        log.info("insertEventLog is calling - BEGIN");

        EventLogEntity eventLogEntity = new EventLogEntity();
        eventLogEntity.setDesc("isSuccess: " + isOk);
        eventLogEntity = eventLogRepo.saveAndFlush(eventLogEntity);

        log.info("insertEventLog is calling - END");
    }


}

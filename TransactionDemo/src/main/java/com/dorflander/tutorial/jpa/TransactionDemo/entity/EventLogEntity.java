package com.dorflander.tutorial.jpa.TransactionDemo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "event_log")
public class EventLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "desc")
    private String desc;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "EventLogEntity{" +
                "id=" + id +
                ", desc='" + desc + '\'' +
                '}';
    }
}

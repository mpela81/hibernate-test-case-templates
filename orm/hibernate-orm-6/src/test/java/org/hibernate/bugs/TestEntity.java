package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class TestEntity {

    @Id
    public String id;

    public String stringA;
    public LocalDate date;
    public Boolean boolA;
    public Boolean boolB;
    public String stringB;

    // ... more fields
}

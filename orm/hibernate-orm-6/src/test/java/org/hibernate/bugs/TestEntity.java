package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestEntity {

    @Id
    public String id;

    public String strField;
}

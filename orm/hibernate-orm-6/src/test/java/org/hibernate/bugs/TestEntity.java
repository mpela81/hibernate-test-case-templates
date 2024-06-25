package org.hibernate.bugs;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Table(name = "TEST_ENTITY")
public class TestEntity {

    @Id
    public String id;

    public String strField;

    @Convert(converter = NumericBooleanConverter.class)
    public Boolean boolField;

    public Integer intField;
}

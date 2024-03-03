package org.hibernate.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
public class TestEntity {

    public static class CustomStringConverter implements AttributeConverter<CustomString, String> {

        @Override
        public String convertToDatabaseColumn(final CustomString attribute) {
            return attribute != null ? attribute.toString() : null;
        }

        @Override
        public CustomString convertToEntityAttribute(final String dbData) {
            return dbData != null ? new CustomString(dbData) : null;
        }
    }

    public enum MyEnum {
        ENUM_A,
        ENUM_B
    }

    @Id
    @GeneratedValue
    public Long id;

    @Convert(converter = CustomStringConverter.class)
    public CustomString stringWithConverter;

    @Type(CustomStringType.class)
    public CustomString stringWithUserType;

    @Enumerated(EnumType.STRING)
    public MyEnum enumValue;

}

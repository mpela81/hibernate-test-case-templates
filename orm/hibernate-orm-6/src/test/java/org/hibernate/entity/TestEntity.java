package org.hibernate.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TestEntity {

    public static class CustomString {
        private final String s;

        public CustomString(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

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
    public CustomString description;

    @Enumerated(EnumType.STRING)
    public MyEnum enumValue;

}

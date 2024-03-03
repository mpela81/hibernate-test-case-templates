package org.hibernate.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static java.sql.Types.VARCHAR;

public class CustomStringType implements UserType<CustomString> {

    @Override
    public int getSqlType() {
        return SqlTypes.VARCHAR;
    }

    @Override
    public Class<CustomString> returnedClass() {
        return CustomString.class;
    }

    @Override
    public boolean equals(CustomString s, CustomString j1) {
        return Objects.equals(s, j1);
    }

    @Override
    public int hashCode(CustomString s) {
        return s.hashCode();
    }

    @Override
    public CustomString nullSafeGet(ResultSet resultSet, int i, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
        String string = resultSet.getString(i);
        return resultSet.wasNull() ? null : new CustomString(string.replaceAll("^@MY@", ""));
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, CustomString s, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {
        if (s == null) {
            preparedStatement.setNull(i, VARCHAR);
        } else {
            preparedStatement.setString(i, "@MY@" + s);
        }
    }

    @Override
    public CustomString deepCopy(CustomString s) {
        return s;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(CustomString s) {
        return null;
    }

    @Override
    public CustomString assemble(Serializable cached, Object owner) {
        return null;
    }
}

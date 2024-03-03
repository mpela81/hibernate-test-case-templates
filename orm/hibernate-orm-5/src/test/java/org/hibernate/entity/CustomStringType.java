package org.hibernate.entity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static java.sql.Types.VARCHAR;

public class CustomStringType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{VARCHAR};
    }

    @Override
    public Class<CustomString> returnedClass() {
        return CustomString.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null || y == null) {
            return false;
        }
        if (x == y) {
            return true;
        }
        return Objects.equals(x.toString(), y.toString());
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String string = StandardBasicTypes.STRING.nullSafeGet(rs, names[0], session);
        return rs.wasNull() ? null : new CustomString(string.replaceAll("^@MY@", ""));
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            StandardBasicTypes.STRING.nullSafeSet(st, null, index, session);
        } else if (value instanceof String) {
            StandardBasicTypes.STRING.nullSafeSet(st, value, index, session);
        } else {
            StandardBasicTypes.STRING.nullSafeSet(st, "@MY@" + value, index, session);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}

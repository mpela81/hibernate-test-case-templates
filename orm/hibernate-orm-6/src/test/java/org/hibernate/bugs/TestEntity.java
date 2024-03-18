package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.StringJoiner;

@Entity(name = "ENTITY")
public class TestEntity
{
	@Id
	@GeneratedValue
	public Long id;

	public String field;

	@Override
	public String toString()
	{
		return new StringJoiner(", ", TestEntity.class.getSimpleName() + "[", "]").add("id=" + id).add("field='" + field + "'").toString();
	}
}

package org.hibernate.bugs;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.List;
import java.util.StringJoiner;

@Entity(name = "ENTITY")
public class TestEntity
{
	@Id
	public String id;

	@OneToMany(mappedBy = "root", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public List<LinkedEntity> linkedEntities;

	@Override
	public String toString()
	{
		return new StringJoiner(", ", TestEntity.class.getSimpleName() + "[", "]").add("id='" + id + "'").add("linkedEntities=" + linkedEntities).toString();
	}
}

package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.StringJoiner;

@Entity(name = "LINKED")
public class LinkedEntity
{
	@Id
	public String id;

	@ManyToOne(targetEntity = TestEntity.class)
	@JoinColumn(name = "rootId", referencedColumnName = "id", nullable = false)
	public TestEntity root;

	@Override
	public String toString()
	{
		return new StringJoiner(", ", LinkedEntity.class.getSimpleName() + "[", "]").add("id='" + id + "'").toString();
	}
}

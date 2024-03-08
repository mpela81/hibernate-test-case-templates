package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "LINKED")
public class LinkedEntity
{
	@Id
	public String id;

	@ManyToOne(targetEntity = TestEntity.class)
	@JoinColumn(name = "rootId", referencedColumnName = "id", nullable = false)
	public TestEntity root;
}

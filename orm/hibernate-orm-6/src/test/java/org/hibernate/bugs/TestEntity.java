package org.hibernate.bugs;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity(name = "ENTITY")
public class TestEntity
{
	@Id
	public String id;

	@OneToMany(mappedBy = "root", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public List<LinkedEntity> linkedEntities;
}

package org.hibernate.bugs;

import jakarta.persistence.*;
import org.hibernate.type.NumericBooleanConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		// Do stuff...

		MyEntity entity = new MyEntity();
		entity.id = "E1";
		entity.bValue = false;
		entityManager.persist(entity);
		entityManager.flush();

		// comparing with 0 fails...
		TypedQuery<MyEntity> query = entityManager.createQuery("select e from MyEntity e where bValue = false", MyEntity.class);
		List<MyEntity> results = query.getResultList();
		assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Entity(name = "MyEntity")
	public static class MyEntity {

		@Id
		public String id;

		//@Column(columnDefinition = "tinyint not null")
		@Convert(converter = NumericBooleanConverter.class)
		boolean bValue;
	}
}

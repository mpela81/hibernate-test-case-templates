package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
	public void hhh17826Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		TestEntity testEntity = new TestEntity();
		testEntity.field = "V1";
		//final TestEntity mergedEntity = entityManager.merge(testEntity);
		entityManager.unwrap(Session.class).saveOrUpdate(testEntity);
		entityManager.getTransaction().commit();
		entityManager.close();

		System.out.println("Entity saved: " + testEntity);
		//System.out.println("Entity merged: " + mergedEntity);

		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		testEntity = new TestEntity();
		//testEntity.id = 1L;
		testEntity.field = "V2";
		//entityManager.unwrap(Session.class).saveOrUpdate(testEntity);
		entityManager.merge(testEntity);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

}

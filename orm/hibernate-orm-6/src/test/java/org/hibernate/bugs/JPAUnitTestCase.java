package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
	public void hhh123Test() {
		prepareDatabase();

		try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
			Root<TestEntity> root = cq.from(TestEntity.class);
			cq.where(cb.equal(root.get("id"), "ID1"));

			List<TestEntity> results = entityManager.createQuery(cq).getResultList();
			Assert.assertEquals(1, results.size());
		}
	}

	private void prepareDatabase() {
		try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
			entityManager.getTransaction().begin();

			TestEntity entity = new TestEntity();
			entity.id = "ID1";
			entity.strField = "S1";
			entityManager.persist(entity);

			entityManager.getTransaction().commit();
		}
	}


}

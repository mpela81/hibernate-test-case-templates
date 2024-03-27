package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		populateDb();

		// clear all cache
		entityManagerFactory.unwrap(SessionFactory.class).getCache().evictAll();;

		// find 1 (hits db)
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		TestEntity entity = entityManager.find(TestEntity.class, "ID1");
		assertNotNull(entity);
		assertEquals("S1", entity.strField);
		entityManager.close();

		// find 2 (hits cache)
		entityManager = entityManagerFactory.createEntityManager();
		entity = entityManager.find(TestEntity.class, "ID1");
		assertNotNull(entity);
		assertEquals("S1", entity.strField);
		entityManager.close();

		Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		long hitCount = stats.getSecondLevelCacheHitCount();
		long missCount = stats.getSecondLevelCacheMissCount();
		long putCount = stats.getSecondLevelCachePutCount();
		System.out.format("Cache Hits: %d, Misses: %d, Puts: %d\n", hitCount, missCount, putCount);
	}

	private void populateDb() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		TestEntity entity = new TestEntity();
		entity.id = "ID1";
		entity.strField = "S1";
		entityManager.persist(entity);

		entityManager.getTransaction().commit();
		entityManager.close();
	}


}

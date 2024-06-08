package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
	public void hhh123Test() throws Exception {
		saveEntities();

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		Root<TestEntity> outerRoot = cq.from(TestEntity.class);

		Subquery<Integer> subquery = cq.subquery(Integer.class);
		Root<TestEntity> subRoot = subquery.from(TestEntity.class);
		subquery.select(cb.max(subRoot.get("internalVersion")))
				.where(cb.equal(subRoot.get("id"), outerRoot.get("id")));

		final Predicate[] predicates = new Predicate[2];
		predicates[0] = cb.equal(outerRoot.get("linkedId"), "link1");
		predicates[1] = cb.equal(outerRoot.get("internalVersion"), subquery);
		cq.where(predicates);

		TypedQuery<TestEntity> query = entityManager.createQuery(cq);
		List<TestEntity> results = query.getResultList();
		Assert.assertEquals(2, results.size());

		entityManager.close();
	}

	private void saveEntities() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		TestEntity entity = new TestEntity();
		entity.id = "ID1";
		entity.linkedId = "link1";
		entity.internalVersion = 1;
		entityManager.persist(entity);

		TestEntity entity2 = new TestEntity();
		entity2.id = "ID2";
		entity2.linkedId = "link2";
		entity2.internalVersion = 10;
		entityManager.persist(entity2);

		TestEntity entity3 = new TestEntity();
		entity3.id = "ID3";
		entity3.linkedId = "link1";
		entity3.internalVersion = 1;
		entityManager.persist(entity3);

		entityManager.getTransaction().commit();
		entityManager.close();
	}


}

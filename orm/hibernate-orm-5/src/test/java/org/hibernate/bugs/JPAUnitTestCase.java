package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		saveEntity(entityManager, "ID1");
		saveEntity(entityManager, "ID2");
		entityManager.getTransaction().commit();
		entityManager.close();

		entityManager = entityManagerFactory.createEntityManager();
		final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		final CriteriaQuery<TestEntity> cr = cb.createQuery(TestEntity.class);
		final Root<TestEntity> root = cr.from(TestEntity.class);
		cr.select(root);
		final TypedQuery<TestEntity> query = entityManager.createQuery(cr);

		//entityManager.getTransaction().begin();
		try (ScrollableResults result = query.unwrap(Query.class).scroll(ScrollMode.FORWARD_ONLY)) {
			while (result.next()) {
				final TestEntity entity = (TestEntity) result.get(0);
				System.out.println("Entity found: " + entity);
			}
		}
		//entityManager.getTransaction().commit();
		entityManager.close();
	}

	private static void saveEntity(final EntityManager entityManager, final String id)
	{
		final TestEntity testEntity = new TestEntity();
		testEntity.id = id;

		final LinkedEntity linked = new LinkedEntity();
		linked.id = id + "_LINK";
		linked.root = testEntity;

		testEntity.linkedEntities = new ArrayList<>();
		testEntity.linkedEntities.add(linked);

		entityManager.persist(testEntity);
	}
}

package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.hibernate.ScrollMode;
import org.hibernate.query.Query;
import org.junit.After;
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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		saveEntity(entityManager, "ID1");
		saveEntity(entityManager, "ID2");
		entityManager.getTransaction().commit();
		entityManager.close();

		entityManager = entityManagerFactory.createEntityManager();
		var cb = entityManager.getCriteriaBuilder();
		var cr = cb.createQuery(TestEntity.class);
		var root = cr.from(TestEntity.class);
		cr.select(root);
		var query = entityManager.createQuery(cr);

		//entityManager.getTransaction().begin();
		try (var result = query.unwrap(Query.class).scroll(ScrollMode.FORWARD_ONLY)) {
			while (result.next()) {
				var entity = result.get();
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

		testEntity.linkedEntities = List.of(linked);

		entityManager.persist(testEntity);
	}

}

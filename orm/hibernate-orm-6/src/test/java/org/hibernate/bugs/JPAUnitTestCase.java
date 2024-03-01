package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.hibernate.entity.TestEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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
	public void hhh17790Test_CustomType_Like_JPQL_Parameter() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.description = new TestEntity.CustomString("Desc1");
		entityManager.persist(entity);

		final List<TestEntity> results = entityManager.createQuery(
						"select e from TestEntity e where e.description like :text",
						TestEntity.class
				)
				.setParameter("text", "%De%")
				.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh17790Test_CustomType_Like_Criteria_Parameter() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.description = new TestEntity.CustomString("Desc1");
		entityManager.persist(entity);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cr = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cr.from(TestEntity.class);
		cr.select(root);
		ParameterExpression<String> param = cb.parameter(String.class);
		cr.where(cb.like(root.get("description"), param));

		TypedQuery<TestEntity> query = entityManager.createQuery(cr);
		query.setParameter(param, "%De%");
		List<TestEntity> results = query.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh17790Test_Enum_Like_JPQL_Parameter() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.enumValue = TestEntity.MyEnum.ENUM_A;
		entityManager.persist(entity);

		final List<TestEntity> results = entityManager.createQuery(
						"select e from TestEntity e where e.enumValue like :text",
						TestEntity.class
				)
				.setParameter("text", "%A%")
				.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh17790Test_Enum_Like_Criteria_Parameter() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.enumValue = TestEntity.MyEnum.ENUM_A;
		entityManager.persist(entity);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cr = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cr.from(TestEntity.class);
		cr.select(root);
		ParameterExpression<String> param = cb.parameter(String.class);
		cr.where(cb.like(root.get("enumValue"), param));

		TypedQuery<TestEntity> query = entityManager.createQuery(cr);
		query.setParameter(param, "%A%");
		List<TestEntity> results = query.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	/// these tests pass
	@Test
	public void hhh17790Test_CustomType_Like_HQL_Literal() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.description = new TestEntity.CustomString("Desc1");
		entityManager.persist(entity);

		final List<TestEntity> results = entityManager.createQuery(
						"select e from TestEntity e where e.description like '%De%'",
						TestEntity.class
				)
				.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh17790Test_CustomType_Like_Criteria_Literal() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.description = new TestEntity.CustomString("Desc1");
		entityManager.persist(entity);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cr = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cr.from(TestEntity.class);
		cr.select(root);
		cr.where(cb.like(root.get("description"), "%De%"));

		TypedQuery<TestEntity> query = entityManager.createQuery(cr);
		List<TestEntity> results = query.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh17790Test_Enum_Like_JPQL_Literal() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.enumValue = TestEntity.MyEnum.ENUM_A;
		entityManager.persist(entity);

		final List<TestEntity> results = entityManager.createQuery(
						"select e from TestEntity e where e.enumValue like '%A%'",
						TestEntity.class
				)
				.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh17790Test_Enum_Like_Criteria_Literal() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final TestEntity entity = new TestEntity();
		entity.enumValue = TestEntity.MyEnum.ENUM_A;
		entityManager.persist(entity);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cr = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cr.from(TestEntity.class);
		cr.select(root);
		cr.where(cb.like(root.get("enumValue"), "%A%"));

		TypedQuery<TestEntity> query = entityManager.createQuery(cr);
		List<TestEntity> results = query.getResultList();

		Assertions.assertEquals(1, results.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}

package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private static final int NUM_QUERY = 30000;
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

		AtomicInteger sessions = new AtomicInteger();
		ThreadLocal<EntityManager> entityManager = ThreadLocal.withInitial( () -> {
			sessions.incrementAndGet();
            return entityManagerFactory.createEntityManager();
        } );

		ExecutorService executorService = Executors.newFixedThreadPool(12);
		List<Future<Void>> futures = new ArrayList<>();
		for (int i = 0; i < NUM_QUERY; i++) {
			final ThreadLocalRandom generator = ThreadLocalRandom.current();
			final String stringParamA = "A" + i;
			final LocalDate dateParam = LocalDate.of(2025, 7, 18);
			final Boolean boolParamB = generator.nextBoolean() ? false : null;
			final boolean useQuery2 = generator.nextBoolean();
			final boolean closeSession = generator.nextInt(100) < 10;
			futures.add(executorService.submit(() -> {
				EntityManager currentEm = entityManager.get();
				if (useQuery2) {
					executeQuery2(currentEm, dateParam, boolParamB, stringParamA);
				} else {
					executeQuery(currentEm, dateParam, boolParamB, stringParamA);
				}
				if (closeSession) {
					currentEm.close();
					entityManager.remove();
				}
				return null;
			}));
		}

		AtomicInteger count = new AtomicInteger(0);
		futures.forEach(f -> {
            try {
                f.get();
				count.incrementAndGet();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

		System.out.format("Executed %d queries by %d sessions\n", count.get(), sessions.get());
		Assert.assertEquals(NUM_QUERY, count.get());
	}

	private void executeQuery(EntityManager entityManager, LocalDate dateParam, Boolean boolParamB, String stringParamA) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cq.from(TestEntity.class);
		ParameterExpression<String> stringA = cb.parameter(String.class);
		ParameterExpression<LocalDate> date = cb.parameter(LocalDate.class);
		ParameterExpression<Boolean> boolA = cb.parameter(Boolean.class);
		ParameterExpression<Boolean> boolB = (boolParamB != null) ? cb.parameter(Boolean.class) : null;

		Predicate predicate = cb.and(
				cb.equal(root.get("stringA"), stringA),
				cb.equal(root.get("date"), date),
				cb.equal(root.get("boolA"), boolA),
				cb.isNotNull(root.get("stringB")));
		if (boolB != null) {
			predicate = cb.and(predicate, cb.equal(root.get("boolB"), boolB));
		}

		cq.where(predicate);

		TypedQuery<TestEntity> query = entityManager.createQuery(cq);
		query.setParameter(stringA, stringParamA)
				.setParameter(date, dateParam)
				.setParameter(boolA, false);
		if (boolB != null) {
			query.setParameter(boolB, boolParamB);
		}

		List<TestEntity> results = query.getResultList();
		Assert.assertNotNull(results);
		//Assert.assertEquals(1, results.size());
	}

	private void executeQuery2(EntityManager entityManager, LocalDate dateParam, Boolean boolParamB, String stringParamA) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cq.from(TestEntity.class);
		ParameterExpression<LocalDate> date = cb.parameter(LocalDate.class);
		ParameterExpression<Boolean> boolA = cb.parameter(Boolean.class);
		ParameterExpression<String> stringA = cb.parameter(String.class);
		ParameterExpression<Boolean> boolB = (boolParamB != null) ? cb.parameter(Boolean.class) : null;

		Predicate predicate = cb.and(
				cb.equal(root.get("date"), date),
				cb.equal(root.get("boolA"), boolA),
				cb.equal(root.get("stringA"), stringA),
				cb.isNotNull(root.get("stringB")));
		if (boolB != null) {
			predicate = cb.and(predicate, cb.equal(root.get("boolB"), boolB));
		}

		cq.where(predicate);

		TypedQuery<TestEntity> query = entityManager.createQuery(cq);
		query.setParameter(date, dateParam);
		query.setParameter(boolA, false);
		query.setParameter(stringA, stringParamA);
		if (boolB != null) {
			query.setParameter(boolB, boolParamB);
		}

		List<TestEntity> results = query.getResultList();
		Assert.assertNotNull(results);
		//Assert.assertEquals(1, results.size());
	}

	private void prepareDatabase() {
		try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
			entityManager.getTransaction().begin();

			for (int i = 0; i < NUM_QUERY; i++) {
				TestEntity entity = new TestEntity();
				entity.id = "ID" + i;
				entity.stringA = "A" + i;
				entity.boolA = false;
				entity.boolB = false;
				entity.stringB = "RED";
				entity.date = LocalDate.of(2025, 7, 18);
				entityManager.persist(entity);
			}

			entityManager.getTransaction().commit();
		}
	}


}

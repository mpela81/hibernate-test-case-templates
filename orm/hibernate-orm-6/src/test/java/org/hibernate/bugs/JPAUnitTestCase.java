package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private static final int NUM_QUERY = 100000;
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

		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
		List<Future<Void>> futures = new ArrayList<>();
		for (int i = 0; i < NUM_QUERY; i++) {
			futures.add(executorService.submit(() -> {
				try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
					CriteriaBuilder cb = entityManager.getCriteriaBuilder();
					CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
					Root<TestEntity> root = cq.from(TestEntity.class);
					ParameterExpression<String> stringA = cb.parameter(String.class);
					ParameterExpression<LocalDate> date = cb.parameter(LocalDate.class);
					ParameterExpression<Boolean> boolA = cb.parameter(Boolean.class);
					ParameterExpression<Boolean> boolB = cb.parameter(Boolean.class);
					cq.where(cb.and(
							cb.equal(root.get("stringA"), stringA),
							cb.equal(root.get("date"), date),
							cb.equal(root.get("boolA"), boolA),
							cb.isNotNull(root.get("stringB")),
							cb.equal(root.get("boolB"), boolB)));

					final int n = ThreadLocalRandom.current().nextInt(0, NUM_QUERY);
					List<TestEntity> results = entityManager.createQuery(cq)
							.setParameter(stringA, "A" + n)
							.setParameter(date, LocalDate.of(2025, 7, 18))
							.setParameter(boolA, false)
							.setParameter(boolB, false)
							.getResultList();
					Assert.assertEquals(1, results.size());
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

		Assert.assertEquals(NUM_QUERY, count.get());
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

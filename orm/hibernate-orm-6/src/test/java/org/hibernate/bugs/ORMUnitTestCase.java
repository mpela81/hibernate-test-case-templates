/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {
	private static final int NUM_QUERY = 30000;

	// Add your entities here.
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] {
			TestEntity.class
		};
	}

	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
	@Override
	protected String[] getMappings() {
		return new String[] {
//				"Foo.hbm.xml",
//				"Bar.hbm.xml"
		};
	}
	// If those mappings reside somewhere other than resources/org/hibernate/test, change this.
	@Override
	protected String getBaseForMappings() {
		return "org/hibernate/test/";
	}

	// Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );
		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.FALSE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.FALSE.toString() );
		configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "false" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() {
		prepareDatabase();

		AtomicInteger sessions = new AtomicInteger();
		ThreadLocal<Session> session = ThreadLocal.withInitial( () -> {
			sessions.incrementAndGet();
			return sessionFactory().openSession();
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
				Session currentSession = session.get();
				if (useQuery2) {
					executeQuery2(currentSession, dateParam, boolParamB, stringParamA);
				} else {
					executeQuery(currentSession, dateParam, boolParamB, stringParamA);
				}
				if (closeSession) {
					currentSession.close();
					session.remove();
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

	private void executeQuery(Session session, LocalDate dateParam, Boolean boolParamB, String stringParamA) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
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

		TypedQuery<TestEntity> query = session.createQuery(cq);
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

	private void executeQuery2(Session session, LocalDate dateParam, Boolean boolParamB, String stringParamA) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
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

		TypedQuery<TestEntity> query = session.createQuery(cq);
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
		try (Session s = sessionFactory().openSession()) {
			s.getTransaction().begin();

			for (int i = 0; i < NUM_QUERY; i++) {
				TestEntity entity = new TestEntity();
				entity.id = "ID" + i;
				entity.stringA = "A" + i;
				entity.boolA = false;
				entity.boolB = false;
				entity.stringB = "RED";
				entity.date = LocalDate.of(2025, 7, 18);
				s.persist(entity);
			}

			s.getTransaction().commit();
		}
	}

}

package org.hibernate.bugs;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.stat.Statistics;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
@State(Scope.Thread)
public class JPABenchmark {

	private EntityManagerFactory entityManagerFactory;
	private EntityManager em;

	@Setup
	public void setup() {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("delete TestEntity").executeUpdate();
		populateData(em);
		em.getTransaction().commit();
	}

	@TearDown
	public void destroy() {
		Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		System.out.println("\nQuery plan cache hit: " + stats.getQueryPlanCacheHitCount() +
				", Query plan cache miss: " + stats.getQueryPlanCacheMissCount());
		em.close();
		entityManagerFactory.close();
	}

	@Benchmark
	public void perf6Hql() {
		TestEntity entity = em.createQuery("from TestEntity where strField = :strField", TestEntity.class)
				.setParameter("strField", "S1")
				.getSingleResult();
		assertEquals("ID1", entity.id);
	}

	@Benchmark
	public void perf6Native() {
		TestEntity entity = (TestEntity) em.createNativeQuery("select * from TestEntity where strField = :strField", TestEntity.class)
				.setParameter("strField", "S1")
				.getSingleResult();
		assertEquals("ID1", entity.id);
	}

	@Benchmark
	public void perf6Criteria() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cq.from(TestEntity.class);
		cq.select(cq.from(TestEntity.class));
		cq.where(cb.equal(root.get("strField"), "S1"));
		TestEntity entity = em.createQuery(cq).getSingleResult();
		assertEquals("ID1", entity.id);
	}

	public static void main(String[] args) throws RunnerException, IOException {
		if (args.length == 0) {
			final Options opt = new OptionsBuilder()
					.include(".*" + JPABenchmark.class.getSimpleName() + ".*")
					.warmupIterations(3)
					.warmupTime(TimeValue.seconds(3))
					.measurementIterations(3)
					.measurementTime(TimeValue.seconds(5))
					.threads(1)
					//.addProfiler("gc")
					.forks(2)
					.build();
			new Runner(opt).run();
		} else {
			Main.main(args);
		}
	}

	public void populateData(EntityManager entityManager) {
		final TestEntity entity = new TestEntity();
		entity.id = "ID1";
		entity.strField = "S1";

		entityManager.persist(entity);
	}

	@Entity(name = "TestEntity")
	public static class TestEntity {

		@Id
		public String id;

		public String strField;
	}
}

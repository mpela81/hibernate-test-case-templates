package org.hibernate.bugs;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
@State(Scope.Thread)
public class JPABenchmark {

	private EntityManagerFactory entityManagerFactory;

	@Setup
	public void setup() {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");

		final EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("delete TestEntity").executeUpdate();
		populateData(em);
		em.getTransaction().commit();
		em.close();
	}

	@TearDown
	public void destroy() {
		entityManagerFactory.close();
	}

	@Benchmark
	public void perf5Hql() {
		final EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		TestEntity entity = em.createQuery("from TestEntity where strField = :strField", TestEntity.class)
				.setParameter("strField", "S1")
				.getSingleResult();
		assertEquals("ID1", entity.id);
		em.getTransaction().commit();
		em.close();
	}

	@Benchmark
	public void perf5Native() {
		final EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		TestEntity entity = (TestEntity) em.createNativeQuery("select * from TestEntity where strField = :strField", TestEntity.class)
				.setParameter("strField", "S1")
				.getSingleResult();
		assertEquals("ID1", entity.id);
		em.getTransaction().commit();
		em.close();
	}

	@Benchmark
	public void perf5Criteria() {
		final EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cq.from(TestEntity.class);
		cq.select(cq.from(TestEntity.class));
		cq.where(cb.equal(root.get("strField"), "S1"));
		TestEntity entity = em.createQuery(cq).getSingleResult();
		assertEquals("ID1", entity.id);
		em.getTransaction().commit();
		em.close();
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

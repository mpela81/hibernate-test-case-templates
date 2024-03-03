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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.entity.CustomString;
import org.hibernate.entity.TestEntity;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

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

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { TestEntity.class };
	}

	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
	@Override
	protected String[] getMappings() {
		return new String[] {};
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

		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	@Override
	protected boolean isCleanupTestDataRequired() {
		return true;
	}

	@Test
	public void hhh17790Test_UserType_Like_Criteria_Literal() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();

		final TestEntity entity = new TestEntity();
		entity.stringWithUserType = new CustomString("TestString");
		session.persist(entity);

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cr = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cr.from(TestEntity.class);
		cr.select(root);
		cr.where(cb.or(cb.like(root.get("stringWithUserType"), "%Test%")));

		TypedQuery<TestEntity> query = session.createQuery(cr);
		List<TestEntity> results = query.getResultList();

		Assertions.assertEquals(1, results.size());

		tx.commit();
		session.close();
	}

	@Test
	public void hhh17790Test_UserType_Like_Criteria_Parameter() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();

		final TestEntity entity = new TestEntity();
		entity.stringWithUserType = new CustomString("TestString");
		session.persist(entity);

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cr = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cr.from(TestEntity.class);
		cr.select(root);
		ParameterExpression<String> param = cb.parameter(String.class);
		cr.where(cb.like(root.get("stringWithUserType"), param));

		TypedQuery<TestEntity> query = session.createQuery(cr);
		query.setParameter(param, "%Test%");
		List<TestEntity> results = query.getResultList();

		Assertions.assertEquals(1, results.size());

		tx.commit();
		session.close();
	}

	@Test
	public void hhh17790Test_UserType_Like_JPQL_Literal() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();

		final TestEntity entity = new TestEntity();
		entity.stringWithUserType = new CustomString("TestString");
		session.persist(entity);

		final List<TestEntity> results = session.createQuery(
						"select e from TestEntity e where e.stringWithUserType like '%Test%'",
						TestEntity.class
				)
				.getResultList();

		Assertions.assertEquals(1, results.size());

		tx.commit();
		session.close();
	}

	@Test
	public void hhh17790Test_UserType_Like_JPQL_Parameter() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();

		final TestEntity entity = new TestEntity();
		entity.stringWithUserType = new CustomString("TestString");
		session.persist(entity);

		final List<TestEntity> results = session.createQuery(
						"select e from TestEntity e where e.stringWithUserType like :text",
						TestEntity.class
				)
				.setParameter("text", "%Test%")
				.getResultList();

		Assertions.assertEquals(1, results.size());

		tx.commit();
		session.close();
	}

}

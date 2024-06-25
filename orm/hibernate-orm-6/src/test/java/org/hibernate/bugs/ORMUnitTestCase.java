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

import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.NumericBooleanConverter;
import org.junit.Assert;
import org.junit.Test;

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
		configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	@Test
	@SuppressWarnings("deprecation")
	public void hhhTestTransformer() {
		prepareDatabase();

		try (Session s = openSession()) {
			TestDTO result = s.createNativeQuery("select id, strField, boolField from TEST_ENTITY where id = ?1", TestDTO.class)
					.setParameter(1, "ID1")
					.addScalar("id")
					.addScalar("strField")
					.addScalar("boolField", Boolean.class)
					.setTupleTransformer(new AliasToBeanResultTransformer<>(TestDTO.class))
					.getSingleResult();

			Assert.assertNotNull(result);
			Assert.assertEquals("ID1", result.id);
			Assert.assertEquals("S1", result.strField);
			Assert.assertTrue(result.boolField);
		}
	}

	@Test
	public void hhhTestConstructor() {
		prepareDatabase();

		try (Session s = openSession()) {
			TestDTO result = s.createNativeQuery("select id, strField, boolField from TEST_ENTITY where id = ?1", TestDTO.class)
					.setParameter(1, "ID1")
					.getSingleResult();

			Assert.assertNotNull(result);
			Assert.assertEquals("ID1", result.id);
			Assert.assertEquals("S1", result.strField);
			Assert.assertTrue(result.boolField);
		}
	}

	public static class TestDTO {
		public String id;

		public String strField;

		@Convert(converter = NumericBooleanConverter.class)
		public Boolean boolField;

		public TestDTO() {
		}

		public TestDTO(String id, String strField, Boolean boolField) {
			this.id = id;
			this.strField = strField;
			this.boolField = boolField;
		}
	}

	private void prepareDatabase() {
		try (Session s = openSession()) {
			Transaction tx = s.beginTransaction();

			s.createNativeMutationQuery("truncate table TEST_ENTITY").executeUpdate();

			TestEntity entity = new TestEntity();
			entity.id = "ID1";
			entity.strField = "S1";
			entity.intField = 1;
			entity.boolField = true;
			s.persist(entity);

			tx.commit();
		}
	}

}

package com.ceres.cldoc;

import com.ceres.cldoc.model.Person;

public class SettingsServiceTest extends TransactionalTest {

	public void testAll() {
		Person person = new Person();
		person.firstName = "Heinz";
		person.lastName = "Achmed";
		Locator.getEntityService().save(getSession(), person);
		ISettingsService ss = Locator.getSettingsService();
		final String NAME = "ABCD123";
		final String VALUE1 = "V1";
		final String VALUE2 = "V2";
		String value = ss.get(getSession(), NAME, person);
		assertNull(value);
		ss.set(getSession(), NAME, VALUE1, person);

		value = ss.get(getSession(), NAME, person);
		assertEquals(VALUE1, value);

		ss.set(getSession(), NAME, VALUE2, null);
		value = ss.get(getSession(), NAME, person);
		assertEquals(VALUE1, value);

		value = ss.get(getSession(), NAME, null);
		assertEquals(VALUE2, value);
	}
}

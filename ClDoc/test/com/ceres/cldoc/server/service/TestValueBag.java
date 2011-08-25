package com.ceres.cldoc.server.service;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.ceres.cldoc.shared.domain.Address;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.ceres.cldoc.shared.domain.ValueBag;

public class TestValueBag extends TestCase {

	@Test
	public void testConvert() {
		ValueBagHelper vbs = new ValueBagHelper();
		HumanBeing person = new HumanBeing();
		person.id = 1234l;
		person.firstName = "Heinz";
		Date birthDate = new Date();
		
		Address a = new Address();
		a.city = "Gusterath";
		person.primaryAddress = a;
		
		ValueBag vb = vbs.convert(person);
		
		assertEquals("Heinz", vb.getString("firstName"));
		assertEquals(birthDate, vb.getDate("dateOfBirth"));
		assertEquals(1234l, vb.getNumber("id"));
		
		ValueBag vba = vb.getValueBag("primaryAddress");
		assertEquals("Gusterath", vba.getString("city"));
		assertEquals("Gusterath", vb.getString("primaryAddress.city"));
		
		HumanBeing reconverted = vbs.reconvert((ValueBag) vb);
		
		vb.set("a.b.c.d", "asdf");
		assertNotNull(vb.getValueBag("a.b.c"));
		assertEquals("asdf", vb.getString("a.b.c.d"));
	
	}

}

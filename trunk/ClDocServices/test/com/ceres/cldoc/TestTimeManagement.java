package com.ceres.cldoc;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.ITimeManagementService;
import com.ceres.cldoc.timemanagement.TimeSheetDay;
import com.ceres.cldoc.timemanagement.TimeSheetElement;
import com.ceres.cldoc.timemanagement.TimeSheetMonth;
import com.ceres.cldoc.timemanagement.TimeSheetYear;

public class TestTimeManagement extends TransactionalTest {

	public void testTimeSheet() throws Exception {
		Date month = new Date();
		TimeSheetMonth tsm = new TimeSheetMonth(month, -10 * 60);
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);
		int curMonth = c.get(Calendar.MONTH);
		while (curMonth == c.get(Calendar.MONTH)) {
			TimeSheetDay tsd;
			
			if (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				tsd = new TimeSheetDay(c.getTime(), false, 8 * 60);
				if (c.get(Calendar.DATE) > 23) {
					tsd.setAbsence(TimeSheetElement.AbsenceType.HOLIDAY);
				} else {
					tsd.setWorkingTime(8 * 60);// - (c.get(Calendar.DATE) % 4));
				}
			} else {
				tsd = new TimeSheetDay(c.getTime(), true, 0);
				tsd.setWorkingTime(0);
			}
			tsm.addChild(tsd);
			c.add(Calendar.DATE, 1);
		}
		
		System.out.println(tsm);
		
	}
	
	public void testGetYearlySheet() throws Exception {
		Person p = new Person();
		p.firstName = "Heinz";
		p.lastName = "Achmed";
		Locator.getEntityService().save(getSession(), p);
		List<Entity> workpatterns = Locator.getEntityService().list(getSession(), 1001);
		Catalog c = Locator.getCatalogService().load(getSession(), "MASTERDATA.ER.arbeitet entsprechend");
		EntityRelation er = new EntityRelation();
		er.subject = p;
		er.object = workpatterns.get(0);
		er.type = c;
		Locator.getEntityService().save(getSession(), er);
		
		TimeSheetYear tsy = Locator.getTimeManagementService().loadTimeSheetYear(getSession(), p, 2014);
		System.out.println(tsy);
		
		tsy.add(createAndSaveTimeRegistration(ITimeManagementService.WORKINGTIME_ACT, p, d(2,Calendar.JANUARY, 8, 30), d(2,Calendar.JANUARY, 12, 30)));
		System.out.println(tsy);
		tsy.add(createAndSaveTimeRegistration(ITimeManagementService.WORKINGTIME_ACT, p, d(2,Calendar.JANUARY, 13, 30), d(2,Calendar.JANUARY, 17, 30)));
		System.out.println(tsy);
		tsy.add(createAndSaveTimeRegistration(ITimeManagementService.ANNUAL_LEAVE_ACT, p, d(27,Calendar.JANUARY, 0, 0), d(7,Calendar.FEBRUARY, 0, 0)));
		System.out.println(tsy);

		tsy = Locator.getTimeManagementService().loadTimeSheetYear(getSession(), p, 2014);
		System.out.println(tsy);

	}

	private Act createAndSaveTimeRegistration(String className, Person person, Date start, Date end) {
		Act act = new Act(new ActClass(className));
		act.setParticipant(person, Participation.ADMINISTRATOR, start, end);
		Locator.getActService().save(getSession(), act);
		return act;
	}

	private Date d(int day, int month, int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, day);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.YEAR, 2014);
		
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		return c.getTime();
	}
	
}

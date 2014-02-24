package com.ceres.cldoc.timemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.util.Jdbc;
import com.ceres.core.ISession;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;

public class TimeManagementServiceImpl implements ITimeManagementService {

	@Override
	public WorkPattern getWorkPattern(ISession session, Entity person) {
		return getWorkPattern(session, person, new Date());
	}

	@Override
	public WorkPattern getWorkPattern(final ISession session, final Entity person, final Date referenceDate) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public WorkPattern execute(Connection con) throws Exception {
				Catalog c = Locator.getCatalogService().load(session, "MASTERDATA.ER.arbeitet entsprechend");
				PreparedStatement s = con.prepareStatement(
						"select * from WorkPattern wp " +
								"inner join Entity e on e.id = wp.id " +
								"inner join EntityRelation er on er.objectId = wp.id AND er.type = ? " +
						"where current_date >= startdate and (current_date < enddate OR enddate is null) AND er.SubjectId = ? ");
				int i = 1;
				s.setLong(i++, c.id);
//				s.setDate(i++, new java.sql.Date(referenceDate.getTime()));
//				s.setDate(i++, new java.sql.Date(referenceDate.getTime()));
				s.setLong(i++, person.getId());
				ResultSet rs = s.executeQuery();
				WorkPattern result = null;
				
				if (rs.next()) {
					result = new WorkPattern();
					result.setId(rs.getLong("id"));
					result.setType(rs.getInt("type"));
					result.setName(rs.getString("name"));
					result.weeklyHours = rs.getFloat("weeklyHours");
					result.leaveEntitlement = rs.getInt("leaveEntitlement");
				}
				s.close();
				return result;
			}
		});
	}
	
	private Date getDate(int day, int month, int year) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		return c.getTime();
	}
	
	@Override
	public TimeSheetYear loadTimeSheetYear(ISession session, Entity person, int year) {
		TimeSheetYear tsy = initTimeSheetYear(session, person, year);
		if (tsy != null) {
			loadTimeSheetYearData(session, tsy, person, year);
		}
		return tsy;
	}

	private TimeSheetYear initTimeSheetYear(ISession session, Entity person, int year) {
		Calendar now = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.set(year, 0, 1);
		WorkPattern wp = getWorkPattern(session, person, c.getTime());
		TimeSheetYear tsy = null;
		if (wp != null) {
			tsy = new TimeSheetYear(c.getTime(), wp.leaveEntitlement);
			int quota = 0;
			for (int m = Calendar.JANUARY; m <= Calendar.DECEMBER; m++) {
				TimeSheetMonth tsm = addMonthSheet(session, person, tsy, c.getTime(), quota);
				quota = tsm.getQuota();
				c.add(Calendar.MONTH, 1);
			}
		}		
		return tsy;
	}

	private void loadTimeSheetYearData(ISession session, TimeSheetYear tsy, Entity person, int year) {
		List<Act> acts = Locator.getActService().load(session, null, person, Participation.ADMINISTRATOR.id, getDate(1,Calendar.JANUARY,year), getDate(31,Calendar.DECEMBER,year));
		for (Act act:acts) {
			tsy.add(act);
		}
	}
	

	private TimeSheetMonth addMonthSheet(ISession session, Entity person, TimeSheetYear tsy, Date month, int quota) {
		WorkPattern wp = getWorkPattern(session, person, month);
		TimeSheetMonth tsm = new TimeSheetMonth(month, quota);
		tsy.addChild(tsm);
		Calendar c = Calendar.getInstance();
		c.setTime(month);
		c.set(Calendar.DATE, 1);
		int dailyQuota = (int) wp.weeklyHours * 60 / 5;
		int cMonth = c.get(Calendar.MONTH);
		while (c.get(Calendar.MONTH) == cMonth) {
			boolean isHoliday = isHoliday(c);
			TimeSheetDay tsd = new TimeSheetDay(c.getTime(), isHoliday, (isHoliday || c.after(Calendar.getInstance()) ? 0 : dailyQuota));
			c.add(Calendar.DATE, 1);
			tsm.addChild(tsd);
		}
		return tsm;
	}

	private boolean isHoliday(Calendar c) {
		HolidayManager hm = HolidayManager.getInstance(HolidayCalendar.GERMANY);
		
		return hm.isHoliday(c) || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}

}

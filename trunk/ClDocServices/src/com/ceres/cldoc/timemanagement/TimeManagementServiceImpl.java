package com.ceres.cldoc.timemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
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
								"inner join EntityRelation er on er.objectId = wp.id AND er.type = ? AND ? >= startdate and (? < enddate OR enddate is null)" +
						"where er.SubjectId = ? order by startdate desc");
				int i = 1;
				s.setLong(i++, c.id);
				s.setDate(i++, new java.sql.Date(referenceDate.getTime()));
				s.setDate(i++, new java.sql.Date(referenceDate.getTime()));
				s.setLong(i++, person.getId());
				ResultSet rs = s.executeQuery();
				WorkPattern result = null;
				
				if (rs.next()) {
					result = new WorkPattern();
					result.setId(rs.getLong("id"));
					result.setType(rs.getInt("type"));
					result.setName(rs.getString("name"));
					result.hours = parsePattern(rs.getString("pattern"));
//					result.leaveEntitlement = rs.getInt("leaveEntitlement");
				}
				s.close();
				return result;
			}
		});
	}
	
	protected float[] parsePattern(String string) {
		float[] result = null;
		StringTokenizer st = new StringTokenizer(string, "-");
		
		if (st.countTokens() == 5) {
			result = new float[5];
			int i = 0;
			while (st.hasMoreTokens()) {
				result[i++] = Float.valueOf(st.nextToken());
			}
		} 
		
		return result;
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
		Calendar c = Calendar.getInstance();
		c.set(year, 0, 1);
		WorkPattern wp = getWorkPattern(session, person, c.getTime());
		TimeSheetYear tsy = null;
		if (wp != null) {
			tsy = new TimeSheetYear(c.getTime(), 30);
			for (int m = Calendar.JANUARY; m <= Calendar.DECEMBER; m++) {
				TimeSheetMonth tsm = addMonthSheet(session, person, tsy, c.getTime());
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
	

	private TimeSheetMonth addMonthSheet(ISession session, Entity person, TimeSheetYear tsy, Date month) {
		WorkPattern wp = getWorkPattern(session, person, month);
		TimeSheetMonth tsm = new TimeSheetMonth(tsy, wp, month);
		tsy.addChild(tsm);
		Calendar c = Calendar.getInstance();
		c.setTime(month);
		c.set(Calendar.DATE, 1);
		int cMonth = c.get(Calendar.MONTH);
		while (c.get(Calendar.MONTH) == cMonth) {
			boolean isHoliday = isHoliday(c);
			TimeSheetDay tsd = new TimeSheetDay(tsm, c.getTime(), isHoliday, isHoliday ? 0 : getDailyMinutes(wp, c));
			c.add(Calendar.DATE, 1);
			tsm.addChild(tsd);
		}
		return tsm;
	}

	private int getDailyMinutes(WorkPattern wp, Calendar c) {
		int day = c.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
		return (int) ((day >= 0 && day < 5 ? wp.hours[day] : 0) * 60);
	}


	private boolean isHoliday(Calendar c) {
		HolidayManager hm = HolidayManager.getInstance(HolidayCalendar.GERMANY);
		
		return hm.isHoliday(c) || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}

	@Override
	public void setWorkPattern(final ISession session, final Person person, final Entity wp, final Date startFromMonth) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws Exception {
				Catalog c = Locator.getCatalogService().load(session, "MASTERDATA.ER.arbeitet entsprechend");
				PreparedStatement s = con.prepareStatement("delete from EntityRelation where type = ? and subjectId = ? and date(startdate) >= ?");
				int i = 1;
				s.setLong(i++, c.id);
				s.setLong(i++, person.getId());
				s.setDate(i++, new java.sql.Date(startFromMonth.getTime()));
				int rows = s.executeUpdate();
				s.close();
				
				EntityRelation er = new EntityRelation();
				er.subject = person;
				er.object = wp;
				er.type = c;
				Calendar cal = Calendar.getInstance();
				cal.setTime(startFromMonth);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				er.startDate = cal.getTime();

				Locator.getEntityService().save(session, er);
				return null;
			}
		});
	}

}
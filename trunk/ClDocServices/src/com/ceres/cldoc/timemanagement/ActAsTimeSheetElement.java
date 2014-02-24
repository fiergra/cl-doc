package com.ceres.cldoc.timemanagement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;

public class ActAsTimeSheetElement implements TimeSheetElement {

	private static final long serialVersionUID = 4598196757943108004L;
	private Act act;

	public ActAsTimeSheetElement() {
	}

	public ActAsTimeSheetElement(Act act) {
		this.act = act;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public int getQuota() {
		return 0;
	}

	@Override
	public int getWorkingTime() {
		return getDuration(act.getParticipation(Participation.ADMINISTRATOR));
	}

	@Override
	public List<TimeSheetElement> getChildren() {
		return null;
	}

	@Override
	public int getBalance() {
		return getWorkingTime();
	}

	@Override
	public void addChild(TimeSheetElement element) {
	}

	@Override
	public void setAbsence(AbsenceType absenceType) {
	}

	@Override
	public int getAbsences() {
		return 0;
	}

	private int getDuration(Participation p) {
		return getDuration(p.start, p.end);
	}

	private int getDuration(Date start, Date end) {
		int duration = (int) (end.getTime() - start.getTime()) / (1000 * 60);
		return duration;
	}

	@Override
	public Date getDate() {
		return act.getParticipation(Participation.ADMINISTRATOR).start;
	}

	@Override
	public boolean isAbsent() {
		return false;
	}

	@Override
	public AbsenceType getAbsenceType() {
		return AbsenceType.NONE;
	}

	public Map<String, Serializable> getAct() {
		return act;
	}

}

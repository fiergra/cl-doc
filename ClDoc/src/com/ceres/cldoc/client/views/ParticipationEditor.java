package com.ceres.cldoc.client.views;

import java.util.Date;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.TimeTextBox;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ParticipationEditor extends VerticalPanel {

	private final Label lbDate = new Label();
	private final Label lbEntity = new Label();
	private ParticipationListBox lbParticipationListBox;	
	private final TimeTextBox txtFrom = new TimeTextBox();
	private final TimeTextBox txtUntil = new TimeTextBox();
	private final Catalog participationType;
	private Interactor interactor;

	public ParticipationEditor(ClDoc clDoc, Catalog participationType) {
		setup(clDoc);
		this.participationType = participationType;
		init(null);
		setHorizontalAlignment(ALIGN_CENTER);
	}

	private void init(Participation participation) {
		if (participation != null) {
			DateTimeFormat df = DateTimeFormat.getFormat("dd.MM.");
			lbDate.setText(participation.start != null ? df.format(participation.start) : "");
			df = DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL);
			lbDate.setTitle(participation.start != null ? df.format(participation.start) : "");
			lbEntity.setText(participation.entity.getName());
			lbParticipationListBox.setSelected((Person) participation.entity);
			txtFrom.setDate(participation.start);
			txtUntil.setDate(participation.end);
		} else {
			lbDate.setText(null);
			lbEntity.setText(null);
			lbParticipationListBox.setSelected(null);
			txtFrom.setDate(null);
			txtUntil.setDate(null);
		}
	}

	public static final long ONE_DAY = 1000l * 60l * 60l * 24l;
	public static final long ONE_WEEK = ONE_DAY * 7L;
	
	private Date incrementDate(Date date, long offset) {
		if (date == null) {
			date = new Date();
		}
		
		Date newDate = new Date(date.getTime() + offset);
		return newDate;
	}

	
	private void setup(ClDoc clDoc) {
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.setVerticalAlignment(ALIGN_MIDDLE);
		hp1.setHorizontalAlignment(ALIGN_RIGHT);
		hp1.setSpacing(5);
		
		Label lbPrev = new Label("<");
		Label lbNext = new Label(">");
		
		lbPrev.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				incrementDate(-ONE_DAY);
			}
		});

		lbNext.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				incrementDate(ONE_DAY);
			}
		});
		
		txtFrom.setStyleName("participationTimeCell");
		txtFrom.setWidth("3em");
		txtUntil.setWidth("3em");
		txtUntil.setStyleName("participationTimeCell");
		
		txtFrom.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Date date = txtFrom.getDate();
				setStartDate(date);
			}
		});
		
		txtUntil.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Date date = txtUntil.getDate();
				setEndDate(date);
			}
		});
		
		hp1.add(lbPrev);
		hp1.add(lbDate);
		hp1.add(lbNext);
		
		lbPrev.setStyleName("participationDateButton");
		lbNext.setStyleName("participationDateButton");
		lbDate.setStyleName("participationDateLabel");
		
		lbEntity.setStyleName("participationEntityLabel");
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.setVerticalAlignment(ALIGN_MIDDLE);
		hp2.add(txtFrom);
		hp2.add(new Label("-"));
		hp2.add(txtUntil);

		lbParticipationListBox = new ParticipationListBox(clDoc, null);
		lbParticipationListBox.setInnerStyleName("participationTimeCell");
		lbParticipationListBox.setInnerWidth("90%");
		lbParticipationListBox.addSelectionChangedHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				setSelectedEntity(lbParticipationListBox.getSelected());
			}
		});
		add(lbParticipationListBox);
		add(hp1);
		add(hp2);
	}

	private IAct getAct() {
		return interactor != null ? interactor.getModel() : null;
	}
	protected void setStartDate(Date date) {
		if (getAct() != null) {
			Participation p = getAct().getParticipation(participationType);
			p.start = date;
			interactor.setModification();
		} 
	}

	protected void setSelectedEntity(Person person) {
		if (getAct() != null && person != null) {
			Participation p = getAct().getParticipation(participationType);
			p.entity = person;
			interactor.setModification();
		} 
	}

	protected void setEndDate(Date date) {
		if (getAct() != null) {
			Participation p = getAct().getParticipation(participationType);
			p.end = date;
			interactor.setModification();
		} 
	}

	protected void incrementDate(long offset) {
		if (getAct() != null) {
			Participation p = getAct().getParticipation(participationType);
			if (p.start != null) {
				p.start = incrementDate(p.start, offset);
			}
			if (p.end != null) {
				p.end = incrementDate(p.end, offset);
			}
			interactor.setModification();
			init(p);
		} 
	}

	public void setAct(Interactor interactor) {
		this.interactor = interactor;
		if (getAct() != null) {
			Participation p = getAct().getParticipation(participationType);
			init(p);
		} else {
			init(null);
		}
	}
}

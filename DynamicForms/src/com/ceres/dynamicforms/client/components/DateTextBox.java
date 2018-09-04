package com.ceres.dynamicforms.client.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.ceres.dynamicforms.client.ClientDateHelper;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateTextBox extends EnabledHorizontalPanel implements Focusable {
	private static final String AM = "am";
	private static final String PM = "pm";
	public static final String AMPM = "ampm";

	private Date dateValue = null;
	private TextBox textBox = new TextBox();
	private PushButton pbDatePicker;
	private int h = -1;
	private int m = -1;

	public DateTextBox() {
		this(false);
	}
	
	public DateTextBox(boolean showAmPm) {
		super();
		textBox.setWidth("5em");
		add(textBox);
		if (showAmPm) {
			add(createAmPmBox());
		}
		Image imgCalendar = new Image("assets/images/calendar.png");
//		imgCalendar.setPixelSize(16, 16);
		pbDatePicker = new PushButton(imgCalendar);
		pbDatePicker.setStyleName("flatButton");
		add(pbDatePicker);
		
		pbDatePicker.addClickHandler(e -> showDatePicker());
		textBox.addKeyPressHandler(e -> {
			if (e.getCharCode() == '+') {
				textBox.cancelKey();
				increment();
			} else if (e.getCharCode() == '-') {
				textBox.cancelKey();
				decrement();
			}
		});
		
		textBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				if (dateValue != null) {
					// keep the time portion 
					h = ClientDateHelper.getHours(dateValue);
					m = ClientDateHelper.getMinutes(dateValue);
				}
				Date value = parseValue();
				
				if (value == null) {
					addStyleName("invalid");
				} else {
					removeStyleName("invalid");
				}

				if (value != null && h > -1) {
					// set the time portion
					value = ClientDateHelper.setTime(value, h, m);
				}
				
				if (value != dateValue || (value != null && !value.equals(dateValue))) {
					setDate(value);
					notifyDateChangeHandlers();
				}
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private void showDatePicker() {
		DatePicker datePicker = new DatePicker();
		
		Date d = getDate();
		if (d != null) {
			datePicker.setValue(getDate());
			datePicker.setCurrentMonth(getDate());
		}
		PopupPanel popUp = new PopupPanel(true, true);
		popUp.setStyleName("plainVanilla");
		popUp.add(datePicker);
		popUp.showRelativeTo(textBox);
		
		datePicker.addValueChangeHandler(e -> { 
			Date value = getDate();
			if (value != null) {
				Date newValue = e.getValue();
				newValue.setHours(value.getHours());
				newValue.setMinutes(value.getMinutes());
				newValue.setSeconds(value.getSeconds());
				setDate(newValue);
				
			} else {
				setDate(e.getValue());
			}
			
			notifyDateChangeHandlers();
			popUp.hide();
		});
	}

	private void increment(int value) {
		Date date = getDate();
		
		if (date == null) {
			date = new Date();
		}
		
		setDate(ClientDateHelper.addDays(date, value));
		notifyDateChangeHandlers();

	}

	private void increment() {
		increment(1);
	}

	private void decrement() {
		increment(-1);
	}

	private ListBox amPmBox;

	private ListBox createAmPmBox() {
		amPmBox = new ListBox();
		amPmBox.setVisibleItemCount(1);
		amPmBox.addItem(AM);
		amPmBox.addItem(PM);
		
		amPmBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Date date = getDate();
				if (date != null) {
					date = syncWithAmPm(date);
					setDate(date);
				}

				notifyDateChangeHandlers();
			}
		});

		return amPmBox;
	}

	private void notifyDateChangeHandlers() {
		for (ValueChangeHandler<Date> h:dateChangeHandlers) {
			h.onValueChange(null);
		}
	}

	private final DateTimeFormat[] dateFormats = getDateTimeFormats();

	public Date parseValue() {
		String sValue = textBox.getValue();
		Date date = null;
		int i = 0;
		
		while (date == null && i < dateFormats.length) {
			try {
				date = dateFormats[i++].parseStrict(sValue);
				if (date != null) {
					int year = ClientDateHelper.getYear(date);
					if (year < 100) {
						ClientDateHelper.setYear(date, year + 2000);
					}
				}
			} catch (IllegalArgumentException x) {
				
			}
		}		
		return date;
	}
	
	protected DateTimeFormat[] getDateTimeFormats() {
		return new DateTimeFormat[] {
				DateTimeFormat.getFormat("dd.MM.yyyy"),
				DateTimeFormat.getFormat("dd/MM/yyyy"),
				DateTimeFormat.getFormat("ddMMyyyy"),
				DateTimeFormat.getFormat("dd.MM.yy"),
				DateTimeFormat.getFormat("ddMMyy"),
				DateTimeFormat.getFormat("dd.MM."),
				DateTimeFormat.getFormat("dd.MM"),
				DateTimeFormat.getFormat("dd/MM"),
				DateTimeFormat.getFormat("ddMM")
			};	
	}

	public Date getDate() {
		return dateValue;
	}

	public void setDate(Date value) {
		dateValue = value;
		if (value != null) {
			textBox.setValue(formatValue(value));
			if (amPmBox != null) {
				if (!amPmBox.isEnabled()) {
					amPmBox.setEnabled(true);
				}
				int h = ClientDateHelper.getHours(value);
				amPmBox.setSelectedIndex(h >= 12 ? 1 : 0);
				dateValue = syncWithAmPm(dateValue);
			}
		} else {
			textBox.setValue(null);
			if (amPmBox != null) {
				amPmBox.setEnabled(false);
			}
		}
	}

	private Date syncWithAmPm(Date date) {
		if (amPmBox.getSelectedIndex() == 0) {
			date = ClientDateHelper.setTime(date, 11,  59);
		} else {
			date = ClientDateHelper.setTime(date, 23,  59);
		}
		return date;
	}

	protected String formatValue(Date value) {
		return DateTimeFormat.getFormat("dd.MM.yyyy").format(value);
	}

	private final Collection<ValueChangeHandler<Date>> dateChangeHandlers = new ArrayList<ValueChangeHandler<Date>>();
	
	public void addDateChangeHandler(ValueChangeHandler<Date> dateChangeHandler) {
		dateChangeHandlers.add(dateChangeHandler);
	}

	@Override
	public int getTabIndex() {
		return textBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		textBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		textBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		textBox.setTabIndex(index);
	}
	
	@SuppressWarnings("deprecation")
	public Boolean isAm() {
		return getDate() != null ? getDate().getHours() < 12 : null;
	}

	public TextBox getTextBox() {
		return textBox;
	}

	
	
}

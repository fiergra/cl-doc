package com.ceres.dynamicforms.client.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ceres.dynamicforms.client.ResultCallback;
import com.google.gwt.aria.client.OrientationValue;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabbedLayoutPanel extends DockLayoutPanel {

	class ContentAndTab {
		FocusPanel tabWidget;
		Widget tabContent;
		public HTML separator;
		
		public ContentAndTab(Widget tabContent, FocusPanel tabWidget) {
//			this.tabContent = new DockLayoutPanel(Unit.PX);
			this.tabContent = tabContent;
			this.tabContent.setStyleName("tabContent");
			this.tabWidget = tabWidget;
		}
		
		
	}
	
	private List<ContentAndTab> tabs = new ArrayList<>();
	private ContentAndTab selected = null;
	
	private final CellPanel innerWrapper;
	private final CellPanel tabsPanel;
	private final HorizontalPanel widgetsPanel;
	private OrientationValue orientation;

	public TabbedLayoutPanel(double height, Unit unit) {
		this(height, unit, OrientationValue.HORIZONTAL);
	}
	
	public TabbedLayoutPanel(double height, Unit unit, OrientationValue orientation) {
		super(unit);
		this.orientation = orientation;
		CellPanel tabsWrapper = new HorizontalPanel();
		tabsWrapper.setStyleName("tabsWrapper");
		tabsWrapper.setSize("100%", "100%");
		widgetsPanel = new HorizontalPanel();
		widgetsPanel.setHeight("100%");
		widgetsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (orientation.equals(OrientationValue.HORIZONTAL)) {
			tabsPanel = new HorizontalPanel();
			innerWrapper = new HorizontalPanel();
			innerWrapper.setHeight("100%");
			addNorth(tabsWrapper, height);
			tabsWrapper.addStyleDependentName("horizontal");
		} else {
			tabsPanel = new VerticalPanel();
			innerWrapper = new VerticalPanel();
			innerWrapper.setWidth("100%");
			addWest(tabsWrapper, height);
			tabsWrapper.addStyleDependentName("vertical");
		}

		setStyleName("tabbedPanel");
		tabsPanel.setStyleName("tabsPanel");
		tabsPanel.setSpacing(5);

		innerWrapper.add(tabsPanel);
		innerWrapper.add(widgetsPanel);
		tabsWrapper.add(innerWrapper);
		
		
	}


	public void add(Widget tabContent, String tabLabel, boolean asHtml) {
		HTML tl = new HTML();
		tl.setHTML(tabLabel);
		tl.setHeight("100%");
		tl.setStyleName("tabTextLabel");
		add(tabContent, tl);
	}

	public void add(Widget tabContent, String tabLabel) {
		Label tl = new Label(tabLabel);
		tl.setHeight("100%");
		tl.setStyleName("tabTextLabel");
		add(tabContent, tl);
	}

	public void add(Widget tabContent, Widget tabLabel) {
		tabLabel.setWidth("100%");
		final FocusPanel fp = new FocusPanel(tabLabel);
		final ContentAndTab cat = new ContentAndTab(tabContent, fp);
		tabs.add(cat);
		fp.setStyleName("tabLabel");
		if (tabs.size() > 1) {
			cat.separator = new HTML();
			cat.separator.setStyleName(orientation.equals(OrientationValue.HORIZONTAL) ? "hTabSeparator" : "vTabSeparator");
			tabsPanel.add(cat.separator);
		}
		tabsPanel.add(fp);
		fp.addClickHandler(e -> { 
			setSelected(cat);
			notifySelectionHandlers(tabs.indexOf(cat));
		});
		
		if (tabs.size() == 1) {
			setSelected(cat);
		}
	}

	private void setSelected(ContentAndTab cat) {
		if (selected != null) {
			selected.tabWidget.removeStyleDependentName("selected");
			selected.tabWidget.getWidget().removeStyleDependentName("selected");
			remove(selected.tabContent);
		}
		
		selected = cat;
		
		if (cat != null) {
			cat.tabWidget.addStyleDependentName("selected");
			cat.tabWidget.getWidget().addStyleDependentName("selected");
			add(cat.tabContent);
		}		
	}

	public int getTabIndex(Widget tabContent) {
		int index = tabs.stream().map(t -> t.tabContent).collect(Collectors.toList()).indexOf(tabContent);
		return index;
	}

	public void selectTab(Widget tabContent) {
		int index = getTabIndex(tabContent);
		if (index > -1) {
			setSelected(tabs.get(index));
		}
	}

	public void selectTab(int index) {
		if (index > -1 && index < tabs.size()) {
			setSelected(tabs.get(index));
		}
	}

	public void clear() {
		setSelected(null);
		tabs.clear();
		tabsPanel.clear();
	}


	public void addWidget(Widget widget) {
		widgetsPanel.add(widget);
	}

	
	public boolean removeTab(Widget tabContent) {
		boolean removed = false;
		int index = getTabIndex(tabContent);
		if (index > -1) {
			ContentAndTab tab = tabs.get(index);
			tabs.remove(tab);
			removed = tabsPanel.remove(tab.tabWidget);
			if (tab.separator != null) {
				tabsPanel.remove(tab.separator);
			}
			
			if (selected == tab) {
				setSelected(!tabs.isEmpty() ? tabs.get(0) : null);
			}
		} 
		return removed;
	}

	private void notifySelectionHandlers(int indexOf) {
		if (selectionHandlers != null) {
			selectionHandlers.forEach(h -> h.callback(indexOf));
		}
	}


	private List<ResultCallback<Integer>> selectionHandlers;
	
	public HandlerRegistration addSelectionHandler(ResultCallback<Integer> selectionHandler) {
		if (selectionHandlers == null) {
			selectionHandlers = new ArrayList<>();
		}
		selectionHandlers.add(selectionHandler);
		return null;
	}
}

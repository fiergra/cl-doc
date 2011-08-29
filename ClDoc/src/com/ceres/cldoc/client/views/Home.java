package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Home extends DockLayoutPanel {
	private ClDoc clDoc;

	public Home(ClDoc clDoc) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setup();
	}

	final TextBox searchBox = new TextBox();
	final VerticalPanel verticalList = new VerticalPanel();
	private Timer timer = new Timer() {

		@Override
		public void run() {
			doSearch();
		}
	};
	
	private void setup() {
		HorizontalPanel hp = new HorizontalPanel();
		
		searchBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				timer.cancel();
				timer.schedule(250);
			}
		});

		hp.setSpacing(2);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(new Label("search"));
		searchBox.setWidth("50em");
		hp.add(searchBox);
	
		Button pbNew = new Button("new...");
		hp.add(pbNew);
		pbNew.setStylePrimaryName("button");
		pbNew.addStyleName("gray");
		pbNew.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ValueBag vb = new ValueBag("com.ceres.cldoc.shared.domain.HumanBeing");
				vb.set("primaryAddress", new ValueBag("com.ceres.cldoc.shared.domain.Address"));
				editPerson(vb);
			}
		});

		addNorth(hp, 3);
		verticalList.setWidth("97%");
		verticalList.setSpacing(2);
		ScrollPanel sp = new ScrollPanel(verticalList);
		sp.addStyleName("searchResults");
		add(sp);
	}

	private String lastSearch = "";

	protected void doSearch() {
		String search = searchBox.getText();
		
		if (!lastSearch.equals(search)) {
			clDoc.status("searching...");
			lastSearch = search;
			searchBox.setEnabled(false);
			SRV.humanBeingService.search(search, new DefaultCallback<List<HumanBeing>>() {

				@Override
				public void onSuccess(List<HumanBeing> result) {
					clDoc.clearStatus();
					verticalList.clear();
					for (final HumanBeing p : result) {
						PersonRenderer pr = new PersonRenderer(p, 
							new OnClick() {
								
								@Override
								public void onClick(PopupPanel pp) {
									loadAndOpenFile(p.id);
								}
							},	
							new OnClick() {
							
							@Override
							public void onClick(PopupPanel pp) {
								loadAndEditPerson(p.id);
							}

						});
						pr.setWidth("100%");
						verticalList.add(pr);
					}
					searchBox.setEnabled(true);
				}
			});
			
//			SRV.humanBeingService.findByString(search,
//					new DefaultCallback<List<ValueBag>>() {
//
//						@Override
//						public void onSuccess(List<ValueBag> result) {
//							results.clear();
//							for (final ValueBag p : result) {
//								results.add(new PersonRenderer(p, 
//									new OnClick() {
//										
//										@Override
//										public void onClick(PopupPanel pp) {
//											loadAndOpenFile(p);
//										}
//									},	
//									new OnClick() {
//									
//									@Override
//									public void onClick(PopupPanel pp) {
//										loadAndEditPerson(p);
//									}
//
//								}));
//							}
//							searchBox.setEnabled(true);
//						}
//
//						@Override
//						public void onFailure(Throwable caught) {
//							searchBox.setEnabled(true);
//							super.onFailure(caught);
//						}
//					});
		}
	}
	
	private void savePerson(ValueBag result) {
		SRV.humanBeingService.save(result, new DefaultCallback<ValueBag>() {

			@Override
			public void onSuccess(ValueBag result) {
				searchBox.setText(result.getString("lastName"));
				doSearch();
			}
		});
		
	}
	
	private void deletePerson(ValueBag result) {
		SRV.personService.delete(result, new DefaultCallback<Void>() {

			@Override
			public void onSuccess(Void v) {
				doSearch();
			}
		});
		
	}

	private void loadAndOpenFile(long pid) {
		SRV.humanBeingService.findById(pid, new DefaultCallback<HumanBeing>() {

			@Override
			public void onSuccess(HumanBeing result) {
				clDoc.openPersonalFile(result);
			}
		});
	}
	
	private void loadAndEditPerson(long pid) {
		SRV.humanBeingService.findById(pid, new DefaultCallback<ValueBag>() {

			@Override
			public void onSuccess(ValueBag result) {
				editPerson(result);
			}
		});
	}
	
	
	private void editPerson(final ValueBag result) {
		final PersonEditor pe = new PersonEditor(result);
		pe.showModal("PersonEditor", 
		new OnClick() {
			
			@Override
			public void onClick(PopupPanel pp) {
				pe.close();
				savePerson(result);
			}
		},
		new OnClick() {
			
			@Override
			public void onClick(PopupPanel pp) {
				pe.close();
				deletePerson(result);
			}
		},
		new Form.DefaultOnClickClose()
		);
	}

}

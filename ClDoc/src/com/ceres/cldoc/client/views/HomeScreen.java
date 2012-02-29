package com.ceres.cldoc.client.views;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PersonWrapper;


public class HomeScreen extends PersonSearchList {

	public HomeScreen(final ClDoc clDoc) {
		super(clDoc, 
				new OnClick<Person>() {

					@Override
					public void onClick(Person pp) {
						editPerson(clDoc.getSession(), pp);
					}
				},
				new OnClick<Person>() {

					@Override
					public void onClick(Person pp) {
						loadAndOpenFile(clDoc, pp.id);
					}
				},
				new OnClick<Person>() {

					@Override
					public void onClick(Person pp) {
						loadAndEditPerson(clDoc.getSession(), pp.id);
					}
				});
	}

//	private void setup() {
//		HorizontalPanel hp = new HorizontalPanel();
//		
//		searchBox.addKeyUpHandler(new KeyUpHandler() {
//
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				timer.cancel();
//				timer.schedule(250);
//			}
//		});
//
//		hp.setSpacing(2);
//		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		hp.add(new Label("search"));
//		searchBox.setWidth("50em");
//		hp.add(searchBox);
//	
//		Button pbNew = new Button("new...");
//		hp.add(pbNew);
//		pbNew.setStylePrimaryName("button");
//		pbNew.addStyleName("gray");
//		pbNew.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				GenericItem vb = new GenericItem("com.ceres.cldoc.shared.domain.HumanBeing", null);
//				vb.set("primaryAddress", new GenericItem("com.ceres.cldoc.shared.domain.Address", null));
//				editPerson(vb);
//			}
//		});
//
//		addNorth(hp, 3);
//		verticalList.setWidth("97%");
//		verticalList.setSpacing(2);
//		ScrollPanel sp = new ScrollPanel(verticalList);
//		sp.addStyleName("searchResults");
//		add(sp);
//	}
//
//	private String lastSearch = "";
//
//	protected void doSearch() {
//		String search = searchBox.getText();
//		
//		if (!lastSearch.equals(search)) {
//			clDoc.status("searching...");
//			lastSearch = search;
//			searchBox.setEnabled(false);
//			SRV.humanBeingService.search(search, new DefaultCallback<List<HumanBeing>>() {
//
//				@Override
//				public void onSuccess(List<HumanBeing> result) {
//					clDoc.clearStatus();
//					verticalList.clear();
//					for (final HumanBeing p : result) {
//						PersonRenderer pr = new PersonRenderer(p, 
//							new OnClick() {
//								
//								@Override
//								public void onClick(PopupPanel pp) {
//									loadAndOpenFile(p.id);
//								}
//							},	
//							new OnClick() {
//							
//							@Override
//							public void onClick(PopupPanel pp) {
//								loadAndEditPerson(p.id);
//							}
//
//						});
//						pr.setWidth("100%");
//						verticalList.add(pr);
//					}
//					searchBox.setEnabled(true);
//				}
//			});
			
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
//		}
//	}
	
	private static void savePerson(Session session, Person result) {
		SRV.humanBeingService.save(session, result, new DefaultCallback<Person>() {

			@Override
			public void onSuccess(Person result) {
//				searchBox.setText(result.lastName);
//				doSearch();
			}
		});
		
	}
	
	private static void deletePerson(Session session, Person person) {
		SRV.humanBeingService.delete(session, person, new DefaultCallback<Void>(){

			@Override
			public void onSuccess(Void result) {
				
			}});
	}

	private static void loadAndOpenFile(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>() {

			@Override
			public void onSuccess(Person result) {
				clDoc.openPersonalFile(clDoc.getSession(), result);
			}
		});
	}
	
	private static void loadAndEditPerson(final Session session, long pid) {
		SRV.humanBeingService.findById(session, pid, new DefaultCallback<Person>() {

			@Override
			public void onSuccess(Person result) {
				editPerson(session, result);
			}
		});
	}
	
	
	private static void editPerson(final Session session, final Person humanBeing) {
		final PersonEditor pe = new PersonEditor(session, new PersonWrapper(humanBeing));
		pe.showModal("PersonEditor", 
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				savePerson(session, humanBeing);
			}
		},
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				deletePerson(session, humanBeing);
			}
		},
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
			}
		}
		);
	}

}

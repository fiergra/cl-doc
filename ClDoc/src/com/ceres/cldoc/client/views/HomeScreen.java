package com.ceres.cldoc.client.views;

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
						editPerson(clDoc, pp);
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
						loadAndEditPerson(clDoc, pp.id);
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
//				GenericAct vb = new GenericAct("com.ceres.cldoc.shared.domain.HumanBeing", null);
//				vb.set("primaryAddress", new GenericAct("com.ceres.cldoc.shared.domain.Address", null));
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
//					new DefaultCallback<List<Act>>() {
//
//						@Override
//						public void onSuccess(List<Act> result) {
//							results.clear();
//							for (final Act p : result) {
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
	
	private static void savePerson(ClDoc clDoc, Person result) {
		SRV.humanBeingService.save(clDoc.getSession(), result, new DefaultCallback<Person>(clDoc, "save") {

			@Override
			public void onSuccess(Person result) {
//				searchBox.setText(result.lastName);
//				doSearch();
			}
		});
		
	}
	
	private static void deletePerson(ClDoc clDoc, Person person) {
		SRV.humanBeingService.delete(clDoc.getSession(), person, new DefaultCallback<Void>(clDoc, "deletePerson"){

			@Override
			public void onSuccess(Void result) {
				
			}});
	}

	private static void loadAndOpenFile(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onSuccess(Person result) {
				clDoc.openPersonalFile(clDoc.getSession(), result);
			}
		});
	}
	
	private static void loadAndEditPerson(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onSuccess(Person result) {
				editPerson(clDoc, result);
			}
		});
	}
	
	
	private static void editPerson(final ClDoc clDoc, final Person humanBeing) {
		final PersonEditor pe = new PersonEditor(clDoc, new PersonWrapper(humanBeing));
		pe.showModal("PersonEditor", 
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				savePerson(clDoc, humanBeing);
			}
		},
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				deletePerson(clDoc, humanBeing);
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

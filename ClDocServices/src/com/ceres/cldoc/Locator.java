package com.ceres.cldoc;

public class Locator {

	private static IEntityService entityService;
	private static IUserService userService;
	private static CatalogServiceImpl catalogService;
	private static IGenericItemService genericItemService;
	private static ParticipationServiceImpl participationService;
	private static LayoutDefinitionServiceImpl layoutDefinitionService;

	public static IEntityService getEntityService() {
		if (entityService == null) {
			entityService = new EntityServiceImpl();
		}
		return entityService;
	}

	public static IUserService getUserService() {
		if (userService == null) {
			userService = new UserServiceImpl();
		}
		return userService;
	}

	public static ICatalogService getCatalogService() {
		if (catalogService == null) {
			catalogService = new CatalogServiceImpl();
		}
		return catalogService;
	}

	public static IGenericItemService getGenericItemService() {
		if (genericItemService == null) {
			genericItemService = new GenericItemServiceImpl();
		}
		return genericItemService;
	}

	public static IParticipationService getParticipationService() {
		if (participationService == null) {
			participationService = new ParticipationServiceImpl();
		}
		return participationService;
	}

	public static ILayoutDefinitionService getLayoutDefinitionService() {
		if (layoutDefinitionService == null) {
			layoutDefinitionService = new LayoutDefinitionServiceImpl();
		}
		return layoutDefinitionService;
	}

}

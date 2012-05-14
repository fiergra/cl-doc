package com.ceres.cldoc;

public class Locator {

	private static IEntityService entityService;
	private static IUserService userService;
	private static CatalogServiceImpl catalogService;
	private static IActService actService;
	private static ParticipationServiceImpl participationService;
	private static LayoutDefinitionServiceImpl layoutDefinitionService;
	private static DocServiceImpl docService;
	private static LogServiceImpl logService;

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

	public static IActService getActService() {
		if (actService == null) {
			actService = new ActServiceImpl();
		}
		return actService;
	}

	public static IDocService getDocService() {
		if (docService == null) {
			docService = new DocServiceImpl();
		}
		return docService;
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

	public static ILogService getLogService() {
		if (logService == null) {
			logService = new LogServiceImpl();
		}
		return logService;
	}

}
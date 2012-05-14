package com.ceres.cldoc.server.service;

import java.util.List;

import com.ceres.cldoc.IActService;
import com.ceres.cldoc.IDocService;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.ActService;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LogEntry;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ActServiceImpl extends RemoteServiceServlet implements
		ActService {

	private IActService getActService() {
		return Locator.getActService();
	}
	
	private IDocService getDocService() {
		return Locator.getDocService();
	}
	
	@Override
	public List<Act> findByEntity(Session session, AbstractEntity entity) {
		return getActService().load(session, entity);
	}

	@Override
	public Act findById(Session session, long id) {
		return getActService().load(session, id);
	}

	@Override
	public Act save(Session session, Act act) {
		getActService().save(session, act);
		return act;
	}

	@Override
	public void delete(Session session, Act act) {
		getActService().delete(session, act);
	}

	@Override
	public String print(Session session, Act act) {
//		try {
//			InputStream in = getDocService().print(session, act);
//			File tempFile = File.createTempFile("tmp", ".pdf");
//			OutputStream out = new FileOutputStream(tempFile);
//			inAndOut(in, out);
//			StringBuffer url = this.getThreadLocalRequest().getRequestURL();
//			String result = url.substring(0, url.lastIndexOf("/")) + "/download?file=" + tempFile.getAbsolutePath();
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
		return null;
	}

	@Override
	public List<LogEntry> listRecent(Session session) {
		return Locator.getLogService().listRecent(session);
	}

}
package com.ceres.cldoc.server.service;

import java.util.Date;
import java.util.List;

import com.ceres.cldoc.IActService;
import com.ceres.cldoc.IDocService;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.client.service.ActService;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Attachment;
import com.ceres.cldoc.model.LogEntry;
import com.ceres.core.IEntity;
import com.ceres.core.ISession;
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
	public List<Act> findByEntity(ISession session, String className, IEntity entity, Long roleId, Date dateFrom, Date dateTo) {
		return getActService().load(session, className, entity, roleId, dateFrom, dateTo);
	}

	@Override
	public Act findById(ISession session, long id) {
		return getActService().load(session, id);
	}

	@Override
	public Act save(ISession session, Act act) {
		getActService().save(session, act);
		return act;
	}

//	@Override
//	public void delete(ISession session, Act act) {
//		getActService().delete(session, act);
//	}

	@Override
	public String print(ISession session, Act act) {
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
	public List<LogEntry> listRecent(ISession session) {
		return Locator.getLogService().listRecent(session);
	}

	@Override
	public List<Act> save(ISession session, List<Act> acts) {
		getActService().save(session, acts);
		return acts;
	}

	@Override
	public List<Attachment> listAttachments(ISession session, Act act) {
		return getActService().listAttachments(session, act);
	}

	@Override
	public Attachment saveAttachment(ISession session, Attachment attachment) {
		getActService().saveAttachment(session, attachment);
		return attachment;
	}

	@Override
	public void deleteAttachment(ISession session, Attachment attachment) {
		getActService().deleteAttachment(session, attachment);
	}

}

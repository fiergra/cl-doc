package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.ceres.dynamicforms.client.DateLink;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorLink;
import com.ceres.dynamicforms.client.components.TimeTextBox;

public class ParticipationTimeFactory implements ILinkFactory {

	@Override
	public DateLink createLink(
			Interactor interactor, String fieldName,
			HashMap<String, String> attributes) {
		final TimeTextBox db = new TimeTextBox();
		final String role = attributes.get("role");
		final String which = attributes.get("which");
		
		DateLink link = new DateLink(interactor, fieldName, db, attributes) {

			@Override
			public void toDialog(Map<String, Serializable> item) {
				db.setDate(getTime((Act)item));
			}

			private Date getTime(Act act) {
				Participation p = act.getParticipation(role);
				Date result = null;
				if (p != null) {
					if ("start".equals(which)) {
						result = p.start;
					} else if ("end".equals(which)) {
						result = p.end;
					} else {
						Logger.getLogger(ParticipationTimeFactory.class.getName()).info("unsupported participation time spec: " + which);
					}
				}
				return result;
			}

			@Override
			public void fromDialog(Map<String, Serializable> item) {
				Participation p = ((Act)item).getParticipation(role);
				if (p != null) {
					if ("start".equals(which)) {
						p.start = db.getDate();
					} else if ("end".equals(which)) {
						p.end = db.getDate();
					} else {
						Logger.getLogger(ParticipationTimeFactory.class.getName()).info("unsupported participation time spec: " + which);
					}
				}
			}
			
		};
		return link;
	}

}

package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("humanbeing")
public interface HumanBeingService extends RemoteService {
	HumanBeing save(HumanBeing humanBeing);
	void delete(HumanBeing person);
	List<HumanBeing> search(String criteria);
	HumanBeing findById(long id);
}

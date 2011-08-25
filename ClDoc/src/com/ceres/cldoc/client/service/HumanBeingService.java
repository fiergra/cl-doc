package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("humanbeing")
public interface HumanBeingService extends RemoteService {
	ValueBag findById(Number id);
	ValueBag save(ValueBag humanBeing);
	void delete(ValueBag person);
	List<HumanBeing> search(String criteria);
	List<ValueBag> findByString(String criteria);
	HumanBeing findById(long id);
}

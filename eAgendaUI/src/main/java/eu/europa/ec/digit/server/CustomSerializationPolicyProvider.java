package eu.europa.ec.digit.server;

import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyProvider;

public class CustomSerializationPolicyProvider implements SerializationPolicyProvider {

	@Override
	public SerializationPolicy getSerializationPolicy(String moduleBaseURL, String serializationPolicyStrongName) {
		return new SimpleSerializationPolicy();
	}

}
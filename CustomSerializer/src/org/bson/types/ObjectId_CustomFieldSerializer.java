package org.bson.types;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public final class ObjectId_CustomFieldSerializer {

	public static void deserialize(SerializationStreamReader streamReader, ObjectId instance) throws SerializationException {

	}

	public static ObjectId instantiate(SerializationStreamReader streamReader) throws SerializationException {
		String id = streamReader.readString();
		return new ObjectId(id);
	}

	public static void serialize(SerializationStreamWriter streamWriter, ObjectId instance) throws SerializationException {
		streamWriter.writeString(instance.toString());
	}

}

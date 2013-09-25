package com.decker.jdclassifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;

/**
 * Not currently used.
 * @author Caleb Shingledecker
 *
 */
public class JDDocumentSerializer implements Serializer<JDDocument>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1611231984667135766L;

	@Override
	public JDDocument deserialize(DataInput in, int available) throws IOException {
		return new JDDocument(in.readUTF(),in.readUTF(),in.readInt());
	}

	@Override
	public void serialize(DataOutput out, JDDocument value) throws IOException {
		out.writeUTF(value.name);
		out.writeUTF(value.description);
		out.write(value.maxWeight);
	}

}

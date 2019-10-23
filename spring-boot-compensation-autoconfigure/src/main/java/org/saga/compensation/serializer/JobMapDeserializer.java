package org.saga.compensation.serializer;

import java.io.IOException;
import java.io.Serializable;

public interface JobMapDeserializer<T extends Serializable> {
    public T deserialize(String str) throws IOException;
}

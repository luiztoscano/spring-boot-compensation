package org.saga.compensation.serializer;

import java.io.IOException;
import java.io.Serializable;

public interface JobMapSerializer<T extends Serializable> {
    public String serialize(T obj) throws IOException;
}

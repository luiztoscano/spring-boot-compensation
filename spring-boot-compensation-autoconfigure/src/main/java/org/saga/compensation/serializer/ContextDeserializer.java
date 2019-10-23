package org.saga.compensation.serializer;

import org.saga.compensation.CompensationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Base64;

public class ContextDeserializer implements JobMapDeserializer<CompensationContext> {
    @Override
    public CompensationContext deserialize(String str) throws IOException {
        final byte[] bytes = Base64.getDecoder().decode(str);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return (CompensationContext) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}

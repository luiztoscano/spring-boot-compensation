package org.saga.compensation.serializer;

import org.saga.compensation.CompensationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class ContextSerializer implements JobMapSerializer<CompensationContext> {

    @Override
    public String serialize(CompensationContext obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            final byte[] byteArray = bos.toByteArray();

            return Base64.getEncoder().encodeToString(byteArray);
        }
    }
}

package com.acubeapps.lockscreen.shortcuts;

import android.util.Base64;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import java.io.IOException;

/**
 * Created by vikram.rathi on 6/27/16.
 */
public abstract class ThriftTypeAdapter<T extends TBase> extends TypeAdapter<T> {

    private static final TProtocolFactory FACTORY = new TJSONProtocol.Factory();
    private static final TSerializer SERIALIZER = new TSerializer(FACTORY);
    private static final TDeserializer DESERIALIZER = new TDeserializer(FACTORY);

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        try {
            byte[] data = SERIALIZER.serialize(value);
            out.value(Base64.encodeToString(data, Base64.NO_WRAP));
        } catch (TException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            return null;
        }
        String data = in.nextString();

        T result = newT();
        try {
            DESERIALIZER.deserialize(result, Base64.decode(data, Base64.NO_WRAP));
        } catch (TException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    protected abstract T newT();
}

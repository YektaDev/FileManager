package dev.yekta.filemanager;

import com.sun.istack.internal.NotNull;

import java.util.List;

public interface FileSerializable<T> {
    @NotNull
    SerializableField[] serialize();

    @NotNull
    T deserialize(List<SerializableField> data);
}

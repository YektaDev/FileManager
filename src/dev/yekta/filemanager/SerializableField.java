/*
 * Copyright © 2021 Ali Khaleqi Yekta, All Rights Reserved.
 *
 * Author: Ali Khaleqi Yekta [YektaDev]
 * Website: https://Yekta.Dev
 * Email: Me@Yekta.Dev
 */

package dev.yekta.filemanager;

import com.sun.istack.internal.NotNull;

public class SerializableField {
    public enum Type {STRING, INT, FLOAT, DOUBLE, BOOLEAN, BYTE}

    @NotNull
    private final Type type;
    private final Object field;

    public SerializableField(String stringField) {
        this.type = Type.STRING;
        this.field = stringField;
    }

    public SerializableField(Integer intField) {
        this.type = Type.INT;
        this.field = intField;
    }

    public SerializableField(Float floatField) {
        this.type = Type.FLOAT;
        this.field = floatField;
    }

    public SerializableField(Double doubleField) {
        this.type = Type.DOUBLE;
        this.field = doubleField;
    }

    public SerializableField(Boolean booleanField) {
        this.type = Type.BOOLEAN;
        this.field = booleanField;
    }

    public SerializableField(Byte byteField) {
        this.type = Type.BYTE;
        this.field = byteField;
    }

    public Object getField() {
        return field;
    }

    public Type getType() {
        return type;
    }
}

/*
 * Copyright © 2021 Ali Khaleqi Yekta, All Rights Reserved.
 *
 * Author: Ali Khaleqi Yekta [YektaDev]
 * Website: https://Yekta.Dev
 * Email: Me@Yekta.Dev
 */

package dev.yekta.filemanager;

import com.sun.istack.internal.NotNull;

import java.util.List;

public interface FileSerializable<T> {
    @NotNull
    SerializableField[] serialize();

    @NotNull
    T deserialize(List<SerializableField> data);
}

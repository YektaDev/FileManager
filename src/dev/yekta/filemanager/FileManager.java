/*
 * Copyright © 2021 Ali Khaleqi Yekta, All Rights Reserved.
 *
 * Author: Ali Khaleqi Yekta [YektaDev]
 * Website: https://Yekta.Dev
 * Email: Me@Yekta.Dev
 */

package dev.yekta.filemanager;

import com.sun.istack.internal.NotNull;
import dev.yekta.filemanager.SerializableField.Type;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dev.yekta.filemanager.FileManager.ErrorMessages.*;

@SuppressWarnings({"unused", "SameParameterValue"})
public class FileManager<T extends FileSerializable<T>> {
    static class ErrorMessages {
        final static String FILE_NOT_FOUND = "File not found";
        final static String CANNOT_CLOSE_FILE = "Cannot close the file";
        final static String CANNOT_WRITE_TO_FILE = "Cannot write to the file";
        final static String CANNOT_GET_POINTER = "Cannot get pointer of the file";
        final static String CANNOT_MOVE_POINTER = "Cannot move pointer of the file";
        final static String CANNOT_READ_FILE = "Cannot read from the file";
        final static String CANNOT_CHANGE_LENGTH = "Cannot change length of the file";
    }

    public final static boolean IS_STRING_UTF8 = true;
    public final static int STRING_LENGTH = 20;
    private final RandomAccessFile file;
    private final Supplier<T> supplier;
    private final String filename;
    private final int recordSize;

    FileManager(@NotNull Supplier<T> supplier, @NotNull String filename) {
        this.supplier = supplier;
        this.filename = filename;
        this.recordSize = getRecordSize();

        RandomAccessFile f;
        try {
            f = new RandomAccessFile(filename, "rw");
        } catch (Exception ignored) {
            this.file = null;
            error(FILE_NOT_FOUND);
            return;
        }

        this.file = f;
    }

    private void error(@NotNull String err) {
        System.err.println(err + ": " + filename);
    }

    private int getRecordSize() {
        int bytes = 0;
        for (SerializableField field : supplier.get().serialize()) {
            switch (field.getType()) {
                case STRING:
                    if (IS_STRING_UTF8)
                        bytes += STRING_LENGTH * 2;
                    else
                        bytes += STRING_LENGTH;
                    break;
                case INT:
                case FLOAT:
                    bytes += 4;
                    break;
                case DOUBLE:
                    bytes += 8;
                    break;
                case BOOLEAN:
                case BYTE:
                default:
                    bytes += 1;
            }
        }
        return bytes;
    }

    void close() {
        try {
            file.close();
        } catch (IOException e) {
            error(CANNOT_CLOSE_FILE);
        }
    }

    void write(@NotNull String str) {
        try {
            if (IS_STRING_UTF8)
                file.writeChars(fixStringLength(str));
            else
                file.writeBytes(fixStringLength(str));
        } catch (IOException e) {
            error(CANNOT_WRITE_TO_FILE);
        }
    }

    void write(int num) {
        try {
            file.writeInt(num);
        } catch (IOException e) {
            error(CANNOT_WRITE_TO_FILE);
        }
    }

    void write(double num) {
        try {
            file.writeDouble(num);
        } catch (IOException e) {
            error(CANNOT_WRITE_TO_FILE);
        }
    }

    void write(float num) {
        try {
            file.writeFloat(num);
        } catch (IOException e) {
            error(CANNOT_WRITE_TO_FILE);
        }
    }

    void write(boolean bool) {
        try {
            file.writeBoolean(bool);
        } catch (IOException e) {
            error(CANNOT_WRITE_TO_FILE);
        }
    }

    void write(byte b) {
        try {
            file.writeByte(b);
        } catch (IOException e) {
            error(CANNOT_WRITE_TO_FILE);
        }
    }

    void write(@NotNull FileSerializable<T> object) {
        for (SerializableField field : object.serialize()) {
            Object obj = field.getField();

            switch (field.getType()) {
                case STRING:
                    write((String) obj);
                    break;
                case INT:
                    write((Integer) obj);
                    break;
                case FLOAT:
                    write((Float) obj);
                    break;
                case DOUBLE:
                    write((Double) obj);
                    break;
                case BOOLEAN:
                    write((Boolean) obj);
                    break;
                default:
                    write((Byte) obj);
            }
        }
    }

    void writeAll(@NotNull List<FileSerializable<T>> objects) {
        for (FileSerializable<T> obj : objects) {
            write(obj);
        }
    }

    void append(@NotNull FileSerializable<T> object) {
        long currentPos = getPointerPosition();
        List<FileSerializable<T>> list = (List<FileSerializable<T>>) readHereToEnd();
        seek(currentPos);
        setFileLength(currentPos);

        list.add(0, object);

        writeAll(list);
    }

    void delete() {
        long currentPos = getPointerPosition();
        List<FileSerializable<T>> list = (List<FileSerializable<T>>) readHereToEnd();
        seek(currentPos);
        setFileLength(currentPos);

        list.remove(0);

        writeAll(list);
    }

    void setFileLength(long length) {
        try {
            file.setLength(length);
        } catch (IOException e) {
            error(CANNOT_CHANGE_LENGTH);
        }
    }

    long getPointerPosition() {
        try {
            return file.getFilePointer();
        } catch (IOException e) {
            error(CANNOT_GET_POINTER);
        }
        return -1;
    }

    void seekStart() {
        seek(0);
    }

    void seekEnd() {
        seek(getFileLength());
    }

    void seek(long pos) {
        try {
            file.seek(pos);
        } catch (IOException e) {
            error(CANNOT_MOVE_POINTER);
        }
    }

    void seekRecord(long recordIndex) {
        seek(recordSize * recordIndex);
    }

    void move(long nRecords) {
        seek(getPointerPosition() + (recordSize * nRecords));
    }

    void moveBack(long nRecords) {
        move(-1 * nRecords);
    }

    long countRecords() {
        return getFileLength() / recordSize;
    }

    void swap(long recordIndex1, long recordIndex2) {
        seekRecord(recordIndex1);
        T t1 = read();
        seekRecord(recordIndex2);
        T t2 = read();

        seekRecord(recordIndex1);
        write(t2);
        seekRecord(recordIndex2);
        write(t1);
    }

    private String fixStringLength(@NotNull String str) {
        int length = str.length();

        if (length > STRING_LENGTH) {
            return str.substring(0, STRING_LENGTH);
        } else if (length == STRING_LENGTH) {
            return str;
        }

        StringBuilder stringBuilder = new StringBuilder(str);
        for (int i = length; i < STRING_LENGTH; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private String readString() {
        StringBuilder stringBuilder = new StringBuilder(STRING_LENGTH);
        try {
            if (IS_STRING_UTF8)
                for (int i = 0; i < STRING_LENGTH; i++) {
                    stringBuilder.append(file.readChar());
                }
            else
                for (int i = 0; i < STRING_LENGTH; i++) {
                    stringBuilder.append((char) file.readByte());
                }
        } catch (IOException e) {
            error(CANNOT_READ_FILE);
            return null;
        }
        return stringBuilder.toString().trim();
    }

    private Integer readInt() {
        try {
            return file.readInt();
        } catch (IOException e) {
            error(CANNOT_READ_FILE);
            return null;
        }
    }

    private Float readFloat() {
        try {
            return file.readFloat();
        } catch (IOException e) {
            error(CANNOT_READ_FILE);
            return null;
        }
    }

    private Double readDouble() {
        try {
            return file.readDouble();
        } catch (IOException e) {
            error(CANNOT_READ_FILE);
            return null;
        }
    }

    private Boolean readBoolean() {
        try {
            return file.readBoolean();
        } catch (IOException e) {
            error(CANNOT_READ_FILE);
            return null;
        }
    }

    private Byte readByte() {
        try {
            return file.readByte();
        } catch (IOException e) {
            error(CANNOT_READ_FILE);
            return null;
        }
    }

    T read() {
        List<SerializableField> fields = new ArrayList<>();

        for (SerializableField fieldCounter : supplier.get().serialize()) {
            Type type = fieldCounter.getType();
            SerializableField fieldToAdd;

            switch (type) {
                case STRING:
                    fieldToAdd = new SerializableField(readString());
                    break;
                case INT:
                    fieldToAdd = new SerializableField(readInt());
                    break;
                case FLOAT:
                    fieldToAdd = new SerializableField(readFloat());
                    break;
                case DOUBLE:
                    fieldToAdd = new SerializableField(readDouble());
                    break;
                case BOOLEAN:
                    fieldToAdd = new SerializableField(readBoolean());
                    break;
                default:
                    fieldToAdd = new SerializableField(readByte());
            }

            fields.add(fieldToAdd);
        }

        return supplier.get().deserialize(fields);
    }

    long getFileLength() {
        try {
            return file.length();
        } catch (IOException e) {
            return -1;
        }
    }

    List<T> readHereToEnd() {
        List<T> records = new ArrayList<>();

        while (getPointerPosition() < getFileLength()) {
            records.add(read());
        }

        return records;
    }

    List<T> readStartToEnd() {
        seekStart();
        return readHereToEnd();
    }
}
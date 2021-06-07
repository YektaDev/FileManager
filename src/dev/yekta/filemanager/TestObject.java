/*
 * Copyright © 2021 Ali Khaleqi Yekta, All Rights Reserved.
 *
 * Author: Ali Khaleqi Yekta [YektaDev]
 * Website: https://Yekta.Dev
 * Email: Me@Yekta.Dev
 */

package dev.yekta.filemanager;

import java.util.List;

public class TestObject implements FileSerializable<TestObject> {
    private String name;
    private int age;
    private float grade;
    private double bigGrade;
    private boolean isHappy;

    public TestObject(String name, int age, float grade, double bigGrade, boolean isHappy) {
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.bigGrade = bigGrade;
        this.isHappy = isHappy;
    }

    public TestObject() {

    }

    @Override
    public SerializableField[] serialize() {
        return new SerializableField[]{
                new SerializableField(name),
                new SerializableField(age),
                new SerializableField(grade),
                new SerializableField(bigGrade),
                new SerializableField(isHappy),
        };
    }

    @Override
    public TestObject deserialize(List<SerializableField> data) {
        return new TestObject(
                (String) data.get(0).getField(),
                (int) data.get(1).getField(),
                (float) data.get(2).getField(),
                (double) data.get(3).getField(),
                (boolean) data.get(4).getField()
        );
    }

    @Override
    public String toString() {
        return "Obj{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", grade=" + grade +
                ", bigGrade=" + bigGrade +
                ", isHappy=" + isHappy +
                '}';
    }
}

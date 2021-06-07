/*
 * Copyright © 2021 Ali Khaleqi Yekta, All Rights Reserved.
 *
 * Author: Ali Khaleqi Yekta [YektaDev]
 * Website: https://Yekta.Dev
 * Email: Me@Yekta.Dev
 */

package dev.yekta.filemanager;

public class Test {
    public static void main(String[] args) {
        FileManager<TestObject> file = new FileManager<>(TestObject::new, "data.dat");
        file.write(new TestObject("A", 18, 20f, 20.00d, true));
        file.write(new TestObject("B", 18, 20f, 20.00d, true));
        file.write(new TestObject("C", 18, 20f, 20.00d, true));
        file.write(new TestObject("D", 18, 20f, 20.00d, true));
        file.write(new TestObject("E", 18, 20f, 20.00d, true));
        file.write(new TestObject("F", 18, 20f, 20.00d, true));
        file.seekRecord(2);
        file.write(new TestObject("Supposed to be C", 18, 20f, 20.00d, true));
        file.close();

        file = new FileManager<>(TestObject::new, "data.dat");

        //file.seekRecord(3);
        //file.delete();
        //file.append(new Obj("YYYY",88,20.8f,288.00d,false));

        file.swap(0, 5); // A <-> F

        for (TestObject o : file.readStartToEnd())
            System.out.println(o);

        file.close();
    }
}

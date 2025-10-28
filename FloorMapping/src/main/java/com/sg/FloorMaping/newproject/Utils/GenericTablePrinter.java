package com.sg.FloorMaping.newproject.Utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class GenericTablePrinter {

    public static <T> void printObject(T obj) {
        if (obj == null) {
            System.out.println("No data to display.");
            return;
        }
        printList(List.of(obj)); // reuse the same method
    }

    public static <T> void printList(List<T> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No data to display.");
            return;
        }

        Class<?> clazz = list.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();

        // 1. Compute max width for each column
        int[] maxWidths = new int[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            maxWidths[i] = fields[i].getName().length();
        }

        for (T item : list) {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Object value = fields[i].get(item);
                    int length = value != null ? value.toString().length() : 4; // "null"
                    if (length > maxWidths[i]) maxWidths[i] = length;
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }
        }

        // 2. Print header
        for (int i = 0; i < fields.length; i++) {
            System.out.printf("%-" + (maxWidths[i] + 2) + "s", fields[i].getName());
        }
        System.out.println();

        // 3. Print separator
        for (int width : maxWidths) {
            System.out.print("-".repeat(width + 2));
        }
        System.out.println();

        // 4. Print rows
        for (T item : list) {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Object value = fields[i].get(item);
                    System.out.printf("%-" + (maxWidths[i] + 2) + "s", value != null ? value : "null");
                } catch (IllegalAccessException e) {
                    System.out.printf("%-" + (maxWidths[i] + 2) + "s", "ERROR");
                }
            }
            System.out.println();
        }
    }
}

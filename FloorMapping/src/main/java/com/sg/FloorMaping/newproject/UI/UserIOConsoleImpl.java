package com.sg.FloorMaping.newproject.UI;

import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class UserIOConsoleImpl implements UserIO{
    private final Scanner console = new Scanner(System.in);

    @Override
    public void print(String message) { System.out.println(message); }

    @Override
    public String readString(String prompt) {
        System.out.print(prompt + ": ");
        return console.nextLine().trim();
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        int result;
        while (true) {
            System.out.print(prompt + " [" + min + "-" + max + "]: ");
            try {
                result = Integer.parseInt(console.nextLine().trim());
                if (result < min || result > max) {
                    System.out.println("Number must be between " + min + " and " + max);
                    continue;
                }
                return result;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
}

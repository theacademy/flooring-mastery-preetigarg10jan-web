package com.sg.FloorMaping.newproject.UI;

public interface UserIO {
    void print(String message);
    String readString(String prompt);
    int readInt(String prompt, int min, int max);
}


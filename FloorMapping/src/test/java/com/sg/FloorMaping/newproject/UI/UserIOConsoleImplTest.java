package com.sg.FloorMaping.newproject.UI;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class UserIOConsoleImplTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ----------------------------------------------------------
    // print()
    // ----------------------------------------------------------
    @Test
    void testPrint_outputsMessage() {
        UserIOConsoleImpl io = new UserIOConsoleImpl();
        io.print("Hello world!");
        String output = outContent.toString().trim();
        assertTrue(output.contains("Hello world!"));
    }

    // ----------------------------------------------------------
    // readString()
    // ----------------------------------------------------------
    @Test
    void testReadString_returnsTrimmedInput() {
        String input = "   Alice   \n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        UserIOConsoleImpl io = new UserIOConsoleImpl();
        String result = io.readString("Enter name");

        assertEquals("Alice", result);
        assertTrue(outContent.toString().contains("Enter name"));
    }

    // ----------------------------------------------------------
    // readInt() - valid input
    // ----------------------------------------------------------
    @Test
    void testReadInt_validInput() {
        String input = "5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        UserIOConsoleImpl io = new UserIOConsoleImpl();
        int result = io.readInt("Enter number", 1, 10);

        assertEquals(5, result);
        String console = outContent.toString();
        assertTrue(console.contains("[1-10]"));
    }

    // ----------------------------------------------------------
    // readInt() - invalid then valid
    // ----------------------------------------------------------
    @Test
    void testReadInt_invalidThenValid() {
        String input = "abc\n12\n8\n"; // invalid string, out of range, valid
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        UserIOConsoleImpl io = new UserIOConsoleImpl();
        int result = io.readInt("Choose", 1, 10);

        assertEquals(8, result);
        String console = outContent.toString();

        assertTrue(console.contains("Please enter a valid integer"));
        assertTrue(console.contains("Number must be between 1 and 10"));
    }

    // ----------------------------------------------------------
    // readInt() - below min and above max
    // ----------------------------------------------------------
    @Test
    void testReadInt_belowAndAboveRange() {
        String input = "0\n11\n5\n"; // too low, too high, valid
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        UserIOConsoleImpl io = new UserIOConsoleImpl();
        int result = io.readInt("Pick", 1, 10);

        assertEquals(5, result);
        String console = outContent.toString();
        assertTrue(console.contains("Number must be between 1 and 10"));
    }
}

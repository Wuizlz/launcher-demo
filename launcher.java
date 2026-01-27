
/*  
    Course: CS 33600
    Name: Daniel Briseno
    Email: dbriseno@pnw.edu
    Assignment: 0
*/
import java.io.IOException;
import java.util.Scanner;

public class Launcher {
    private final static Scanner in = new Scanner(System.in);

    // exit option
    private static final int EXIT_OPTION = 0;

    // range for the options
    private static final int MIN_OPTION = 0;
    private static final int MAX_OPTION = 8;

    // System route
    final static String systemDrive = System.getenv("SystemDrive");
    final static String system32 = systemDrive + "\\Windows\\system32\\";

    // list of commands
    private final static String[] cmds = { "Taskmgr.exe",
            "notepad.exe",
            "charmap.exe",
            "Sndvol.exe",
            "winver.exe",
            "msinfo32.exe",
            "nslookup.exe",
            "cmd.exe"
    };

    private static final String MENU_TEXT = String.join("\n",
            "Please make a choice from the following list.",
            "  0: Quit",
            "  1: Run Task Manager",
            "  2: Run Notepad",
            "  3: Run Character Map",
            "  4: Run Sound Volume",
            "  5: Run \"About Windows\"",
            "  6: Run \"System Information\"",
            " *7: Run NS Lookup",
            " *8: Run Cmd Shell",
            "Enter your choice: ");

    private static void handleInput() {
        boolean done = false;
        while (!done) {

            int userInput = -1;
            boolean valid = false;

            System.out.print(MENU_TEXT);

            while (!valid) {
                if (in.hasNextInt()) {
                    userInput = in.nextInt();
                    if (userInput >= MIN_OPTION && userInput <= MAX_OPTION) {
                        valid = true;
                    } else {
                        System.out.print("Enter your choice: ");
                    }
                } else {
                    in.next();
                    System.out.print("Enter your choice: ");
                }
            }
            if (userInput == EXIT_OPTION) {
                done = true;
            } else {
                initializeProcess(userInput);
                System.out.println();
            }

        }

    }

    private static void initializeProcess(int userInput) {
        try {
            ProcessBuilder pb = new ProcessBuilder(system32 + cmds[userInput - 1]);
            if (userInput >= MAX_OPTION - 1) {
                pb.inheritIO();
            }
            Process p = pb.start();
            System.out.println("Started program " + userInput + " with pid = " + p.pid());

            if (userInput >= MAX_OPTION - 1) {
                System.out.println("Launcher waiting on Program " + userInput + "...");
                System.out.println();
                p.waitFor();

                int exitValue = p.exitValue();

                if (exitValue == EXIT_OPTION) {

                    p.info().totalCpuDuration().ifPresent(
                            d -> System.out
                                    .println("Program " + userInput + " exited with return value 0 and ran for "
                                            + d.toMillis() + " cpu miliseconds"));
                }
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Failed, " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        handleInput();
    }
}

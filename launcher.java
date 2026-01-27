
/*  
    Course: CS 33600
    Name: Daniel Briseno
    Email: dbriseno@pnw.edu
    Assignment: 1
*/
import java.io.IOException;
import java.util.Scanner;

/**
 * A menu-driven launcher that starts common Windows System32 utilities.
 * <p>
 * The user selects an option (0-8). Option 0 is to exit the program. Options
 * 1-6 launch GUI utilities.
 * Options 7 (nslookup) and 8 (cmd) inherit the console IO and the launcher
 * waits for the process to exit.
 * </p>
 *
 */
public class Launcher {

    /** @hidden */
     Launcher(){/*to prevent defualt constructor*/}

    private final static Scanner in = new Scanner(System.in);

    // exit option
    private static final int EXIT_OPTION = 0;

    // range for the options
    private static final int MIN_OPTION = 0;
    private static final int MAX_OPTION = 8;

    // System route
    private final static String systemDrive = System.getenv("SystemDrive");
    private final static String system32 = systemDrive + "\\Windows\\system32\\";

    // array of executables
    private final static String[] cmds = { "Taskmgr.exe",
            "notepad.exe",
            "charmap.exe",
            "Sndvol.exe",
            "winver.exe",
            "msinfo32.exe",
            "nslookup.exe",
            "cmd.exe"
    };

    // defined prompt message for reusability and reduced horizontal scale
    /** @hidden */
    private static final String MENU_TEXT = String.join("\n",
            "Please make a choice from the following list.",
            "  0: Quit",
            "  1: Run TaskManager",
            "  2: Run Notepad",
            "  3: Run Character Map",
            "  4: Run Sound Volume",
            "  5: Run \"About Windows\"",
            "  6: Run \"System Information\"",
            " *7: Run NS Lookup",
            " *8: Run Cmd shell",
            "Enter your choice: ");

    /**
     * Displays the menu and repeatedly prompts the user until they choose to quit.
     * Ensures the input is an integer within the allowed range.
     */
    public static void handleInput() {
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

    /**
     * Launches the selected System32 program based on the user's menu choice.
     * For interactive launcher programs (options 7 and 8), the process inherits IO
     * and the method waits for the process to terminate, then prints CPU time if
     * available.
     *
     * @param userInput the validated menu option chosen by the user (1-8)
     */
    public static void initializeProcess(int userInput) {
        try {

            ProcessBuilder pb = new ProcessBuilder(system32 + cmds[userInput - 1]);
            if (userInput >= MAX_OPTION - 1) { // specifically for launcher programs
                /*
                 * for the sub-process to utilize parents IO field as well(causes a race of what
                 * to print to system
                 */
                pb.inheritIO();
            }

            Process p = pb.start();
            System.out.println("Started program " + userInput + " with pid = " + p.pid());

            if (userInput >= MAX_OPTION - 1) {
                System.out.println("Launcher waiting on Program " + userInput + "...");
                System.out.println();
                p.waitFor();

                int exitValue = p.exitValue();

                /*
                 * exitValue is then used to compared to Exit_Option to then print total cpu
                 * time
                 * then goes back to the top of handleInput
                 */
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

    /**
     * Program entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        handleInput();
    }
}


/*  
    Course: CS 33600
    Name: Daniel Briseno
    Email: dbriseno@pnw.edu
    Assignment: 1
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

    private static void handleInput() { // function to start prompt towards user
        boolean done = false;
        while (!done) { // runs when not done

            int userInput = -1;
            boolean valid = false;

            System.out.print(MENU_TEXT);

            while (!valid) { // runs when not valid number
                if (in.hasNextInt()) { // checks if int in next token
                    userInput = in.nextInt(); // swallows it
                    if (userInput >= MIN_OPTION && userInput <= MAX_OPTION) {
                        valid = true; // set to true to escape loop
                    } else {
                        System.out.print("Enter your choice: "); // bad input -> prompts then hits loop again
                    }
                } else {
                    in.next();
                    System.out.print("Enter your choice: "); // input wasnt an int -> prompts then hits loop again
                }
            }
            if (userInput == EXIT_OPTION) { // once valid check if input is 0 to exit
                done = true;
            } else {
                initializeProcess(userInput); //enter function to handle process
                System.out.println();
            }

        }

    }

    private static void initializeProcess(int userInput) {
        try {
            // instance of ProcessBuilder with command path
            ProcessBuilder pb = new ProcessBuilder(system32 + cmds[userInput - 1]);
            if (userInput >= MAX_OPTION - 1) { //specifically for launcher programs
                /* for the sub-process to utilize parents IO field as well(causes a race of what
                 to print to system */
                pb.inheritIO();
            }
            //starts the actual instance with appropiate command path
            Process p = pb.start(); 
            System.out.println("Started program " + userInput + " with pid = " + p.pid());

            if (userInput >= MAX_OPTION - 1) { // to handle additional launcher steps 
                System.out.println("Launcher waiting on Program " + userInput + "...");
                System.out.println();
                p.waitFor(); //blocks until the launched program exits

                int exitValue = p.exitValue(); // exitValue gets populated when process terminates

                /*exitValue is then used to compared to Exit_Option to then print total cpu time 
                then goes back to the top of handleInput*/
                if (exitValue == EXIT_OPTION) { 

                    p.info().totalCpuDuration().ifPresent(
                            d -> System.out
                                    .println("Program " + userInput + " exited with return value 0 and ran for "
                                            + d.toMillis() + " cpu miliseconds"));
                }
            }

            //catches errors from InheritIo and ProcessBuilder.wait
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed, " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        handleInput();
    }
}

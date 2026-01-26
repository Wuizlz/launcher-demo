
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

    private static void HandleInput() {
        while (true) {

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

            InitilizeProcess(userInput);
            System.out.println();
        }

    }

    private static void InitilizeProcess(int userInput) {
        if (userInput == 0)
            System.exit(0);
        try {
            if (userInput < 7) {

                ProcessBuilder pb = new ProcessBuilder(system32 + cmds[userInput - 1]);
                Process p = pb.start();
                System.out.println("Started program " + userInput + " with pid = " + p.pid());

            } else {
                try {
                    System.out.println("test");
                    ProcessBuilder pb = new ProcessBuilder(system32 + cmds[userInput - 1]);
                    pb.inheritIO();
                    Process p = pb.start();
                    System.out.println("Started program " + userInput + " with pid = " + p.pid());
                    System.out.println("Launcher waiting on Program " + userInput + "...");
                    System.out.println();
                    p.waitFor();

                    int exitValue = p.exitValue();

                    if (exitValue == 0) {

                        p.info().totalCpuDuration().ifPresent(
                                d -> System.out
                                        .println("Program " + userInput + " exited with return value 0 and ran for "
                                                + d.toMillis() + " cpu miliseconds\n"));
                    }

                } catch (InterruptedException e) {
                    System.out.println("Failed, " + e.getMessage());
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed, " + e.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        HandleInput();
    }
}

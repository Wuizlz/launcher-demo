/*


*/

import java.io.IOException;
import java.util.Scanner;

/**
 * This program uses the Java Process API
 * to run several Windows programs.
 * See
 * https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessBuilder.html
 * https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Process.html
 */

public class Launcher {
    private final static Scanner in = new Scanner(System.in);
    private final static String[] cmds = { "Taskmgr.exe",
            "notepad.exe",
            "charmap.exe",
            "Sndvol.exe",
            "winver.exe",
            "msinfo32.exe",
            "nslookup.exe",
            "cmd.exe"
    };

    public static void main(String[] args) {
        try {
            final String systemDrive = System.getenv("SystemDrive");
            final String system32 = systemDrive + "\\Windows\\system32\\";

            int userInput;

            while (true) {
                System.out.print(
                        "Please make a choice from the following list.\n  0: Quit\n  1: Run Task Manager\n  2: Run Notepad\n  3: Run Character Map\n  4: Run Sound Volume\n  5: Run \"About Windows\"\n  6: Run \"System Information\"\n *7: Run NS Lookup\n *8: Run Cmd Shell \nEnter your choice:");

                while (true) { // while true is to keep the prompt if requirements are not met
                    if (in.hasNextInt()) {
                        userInput = in.nextInt();
                        if (userInput <= 8) {
                            break;// breaks out the while loop straight to line 35
                        }
                    } else {
                        in.next(); // this consume non-int tokens
                    }
                    System.out.print("Enter your choice:"); // prompt is asked again before while loop is ran again
                }

                if (userInput == 0) { // exit out of program
                    System.exit(0);
                    System.out.println();
                }

                if (userInput == 1) { // open task manager
                    ProcessBuilder pb = new ProcessBuilder(system32 + cmds[0]);
                    Process p = pb.start();
                    System.out.println("Started program 1 with pid = " + p.pid());
                }

                if (userInput == 2) { // open note pad
                    ProcessBuilder pb = new ProcessBuilder(system32 + cmds[1]);
                    Process p = pb.start();
                    System.out.println("Start program 2 with pid = " + p.pid());
                }

                if (userInput == 3) { // open character map
                    ProcessBuilder pb = new ProcessBuilder(system32 + cmds[2]); // ProcessBuilder allows to start a new
                                                                                // process
                    Process p = pb.start(); // starts the process
                    System.out.println("Start program 3 with pid = " + p.pid());
                }

                if (userInput == 4) { // open snipping tool
                    ProcessBuilder pb = new ProcessBuilder(system32 + cmds[3]);
                    Process p = pb.start();
                    System.out.println("Start program 4 with pid = " + p.pid());
                }

                if (userInput == 5) { // open winver
                    ProcessBuilder pb = new ProcessBuilder(system32 + cmds[4]);
                    Process p = pb.start();
                    System.out.println("Start program 5 with pid = " + p.pid());
                }

                if (userInput == 6) // open msinfo
                {
                    ProcessBuilder pb = new ProcessBuilder(system32 + cmds[5]);
                    Process p = pb.start();
                    System.out.println("Start program 6 with pid = " + p.pid());
                }

                if (userInput == 7) { // start nslookup
                    try {

                        ProcessBuilder pb = new ProcessBuilder(system32 + cmds[6]);
                        pb.inheritIO();
                        Process p = pb.start();
                        System.out.println("Started program 7 with pid = " + p.pid());
                        System.out.println("Launcher waiting on Program 7...");
                        System.out.println();
                        p.waitFor();

                        int exitValue = p.exitValue();

                        if (exitValue == 0) {

                            p.info().totalCpuDuration().ifPresent(
                                    d -> System.out.println("Program 7 exited with return value 0 and ran for "
                                            + d.toMillis() + " cpu miliseconds\n"));
                        }

                    } catch (InterruptedException e) {
                        System.out.println("Failed to start" + e.getMessage());
                    }

                }

                if (userInput == 8) { // start cmd
                    try {
                        ProcessBuilder pb = new ProcessBuilder(system32 + cmds[7]);
                        pb.inheritIO();
                        Process p = pb.start();
                        System.out.println("Started program 8 with pid = " + p.pid());
                        System.out.println("Launcher waiting on Program 8...");
                        System.out.println();
                        p.waitFor();

                        int exitValue = p.exitValue();

                        if (exitValue == 0) {
                            p.info().totalCpuDuration().ifPresent(
                                    d -> System.out.println("Program 8 exited with return value 0 and ran for "
                                            + d.toMillis() + " cpu miliseconds\n"));
                        }

                    } catch (InterruptedException e) {
                        System.out.println("Failed to start" + e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Failed to start" + e.getMessage());

        }
    }
}

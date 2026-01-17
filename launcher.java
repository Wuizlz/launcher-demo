import java.io.IOException;
import java.util.Scanner;

public class launcher {
    public static void main(String[] args) {
        try {
            String[] ListOfOperations = { "taskmgr", "notepad", "charmap", "SnippingTool", "msinfo32", "winver" };
            Scanner in = new Scanner(System.in);
            int userInput;

            while (true) {
                System.out.println("Please make a choice from the following list.");
                System.out.println("    0: Quit");
                System.out.println("    1: Run TaskManager");
                System.out.println("    2: Run Notepad");
                System.out.println("    4: Run Snipping Tool");
                System.out.println("    5: Run \"About Windows\"");
                System.out.println("    6: Run \"System Information\"");
                System.out.println("    *7: Run NS Lookup");
                System.out.println("    *8: Run Cmd shell");
                System.out.print("Enter your choice: ");

                while (true) { // while true is to keep the prompt if requirements are not met
                    if (in.hasNextInt()) {
                        userInput = in.nextInt();
                        if (userInput <= 8) {
                            break;// breaks out the while loop straight to line 35
                        }
                    } else {
                        in.next(); // this consume non-int tokens
                    }
                    System.out.print("Enter your choice: "); // prompt is asked again before while loop is ran again
                }

                if (userInput == 0) { // exit out of program
                    System.exit(0);
                }

                if (userInput == 1) { // open task manager
                    ProcessBuilder pb = new ProcessBuilder(ListOfOperations[0]);
                    Process p = pb.start();
                    System.out.println("Started program 1 with pid = " + p.pid());
                }

                if (userInput == 2) {
                    ProcessBuilder pb = new ProcessBuilder(ListOfOperations[1]);
                    Process p = pb.start();
                    System.out.println("Start program 2 with pid = " + p.pid());
                }

                if (userInput == 3) {
                    ProcessBuilder pb = new ProcessBuilder(ListOfOperations[2]); // ProcessBuilder allows to start a new
                                                                                 // process
                    Process p = pb.start(); // starts the process
                    System.out.println("Start program 3 with pid = " + p.pid());
                }

                if (userInput == 4) {
                    ProcessBuilder pb = new ProcessBuilder(ListOfOperations[3]);
                    Process p = pb.start();
                    System.out.println("Start program 4 with pid = " + p.pid());
                }

                if (userInput == 4) {
                    ProcessBuilder pb = new ProcessBuilder(ListOfOperations[4]);
                    Process p = pb.start();
                    System.out.println("Start program 5 with pid = " + p.pid());
                }

                if(userInput == 5)
                {
                    ProcessBuilder pb = new ProcessBuilder(ListOfOperations[5]);
                    Process p = pb.start();
                    System.out.println("Start program 6 with pid = " + p.pid());
                }

                if(userInput == 6)
                {
                    ProcessBuilder pb = new ProcessBuilder(ListOfOperations[6]);
                    Process p = pb.start();
                    System.err.println("Start program 7 with pid = " + p.pid());
                }

            }
        } catch (IOException e) {
            System.out.println("Failed to start" + e.getMessage());
        }
    }
}

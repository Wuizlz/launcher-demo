import java.util.Scanner;

public class launcher {
    public static void main(String[] args) {
        String[] ListOfOperations = { "" };
        Scanner in = new Scanner(System.in);
        System.out.println("Please make a choice from the following list.");
        System.out.println("    0: Quit");
        System.out.println("    1: Run Notepad");
        System.out.println("    2: Run TaskManager");
        System.out.println("    4: Run Snipping Tool");
        System.out.println("    5: Run \"About Windows\"");
        System.out.println("    6: Run \"System Information\"");
        System.out.println("    *7: Run NS Lookup");
        System.out.println("    *8: Run Cmd shell");
        System.out.print("Enter your choice: ");

        while ((!in.hasNextInt()) || in.nextInt() > 9) {
            in.nextLine();
            System.out.print("Enter your choice: ");
        }
        int userInput = in.nextInt();
        System.out.println(userInput);
    }
}

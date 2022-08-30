import java.util.Scanner;

public class MainMenu {
    public static void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int menuItem;
            System.out.println("Main menu");
            System.out.println("1) Flight menu");
            System.out.println("2) Country menu");
            System.out.println("3) Airport menu");
            System.out.println("4) Customer menu");
            System.out.println("5) Ticket menu");
            System.out.println("0) Exit");
            menuItem = scanner.nextInt();
            scanner.nextLine();
            if(menuItem == 1) {
                FlightMenu.start();
            } else if (menuItem == 2) {
                CountryMenu.start();
            } else if (menuItem == 3) {
                AirportMenu.start();
            } else if (menuItem == 4) {
                CustomerMenu.start();
            } else if (menuItem == 5) {
                TicketMenu.start();
            } else if (menuItem == 0) {
                System.out.println("Exit from program");
                return;
            } else {
                System.out.println("Error! Enter number from menu");
            }
        }
    }
}

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicketMenu implements Connect {
    static final Scanner scanner = new Scanner(System.in);
    static Ticket ticket = new Ticket();

    public static void start() {
        while (true) {
            int menuItem;
            System.out.println("Ticket menu: ");
            System.out.println("1) Add ticket");
            System.out.println("2) Delete ticket");
            System.out.println("3) Edit ticket");
            System.out.println("4) Search ticket by number");
            System.out.println("5) Search ticket by flight number or passport");
            System.out.println("6) Show all tickets");
            System.out.println("0) Exit");

            menuItem = scanner.nextInt();
            scanner.nextLine();
            if (menuItem == 1) {
                //добавление билета
                addTicket();
            } else if (menuItem == 2) {
                //удаление билета
                deleteTicket();
            } else if (menuItem == 3) {
                // изменение билета
                editTicket();
            } else if (menuItem == 4) {
                //поиск билета по номеру
                getTicketByPassport();
            } else if (menuItem == 5) {
                //вывод списка билетов
                getTicketByCustom();
            } else if (menuItem == 6) {
                //вывод списка билетов
                getAllTickets();
            } else if (menuItem == 0) {
                System.out.println("Exit to the main menu");
                return;
            } else {
                System.out.println("Error! Enter number from menu");
            }
        }
    }

    public static class Ticket {
        public Ticket() {
        }

        private Integer id;
        private Integer clientId;
        private Timestamp TicketPurchasingTime;
        private Integer ticketNumber;
        private String flightNumber;

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getClientId() {
            return clientId;
        }

        public void setClientId(Integer clientId) {
            this.clientId = clientId;
        }

        public Timestamp getTicketPurchasingTime() {
            return TicketPurchasingTime;
        }

        public void setTicketPurchasingTime(Timestamp ticketPurchasingTime) {
            TicketPurchasingTime = ticketPurchasingTime;
        }

        public Integer getTicketNumber() {
            return ticketNumber;
        }

        public void setTicketNumber(Integer ticketNumber) {
            this.ticketNumber = ticketNumber;
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        @Override
        public String toString() {
            return "Ticket{" +
                    "id=" + id +
                    ", clientId=" + clientId +
                    ", TicketPurchasingTime=" + TicketPurchasingTime +
                    ", ticketNumber=" + ticketNumber +
                    ", flightNumber='" + flightNumber + '\'' +
                    '}';
        }
    }

    //1. Добавление билета
    public static void addTicket() {
        while (true) {
            try {
                System.out.println("Enter customer ID: ");
                ticket.clientId = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }

        while (true) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                System.out.println("Enter ticket purchasing date and time: ");
                java.util.Date utilStartDate = formatter.parse(scanner.nextLine());
                ticket.TicketPurchasingTime = new Timestamp(utilStartDate.getTime());
                break;
            } catch (ParseException e) {
                System.out.println("Enter date in such format dd.MM.yyyy HH:mm:ss!");
            }
        }

        while (true) {
            try {
                System.out.println("Enter ticket number: ");
                ticket.ticketNumber = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }

        System.out.println("Enter flight number: ");
        ticket.flightNumber = scanner.nextLine();

        String SQL_ADD_TICKET = "INSERT INTO tickets (client_id, ticket_purchasing_time, ticket_number, flight_number)" + " VALUES (?, ?, ?, ?)";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_ADD_TICKET)) {
            statement.setInt(1, ticket.getClientId());
            statement.setTimestamp(2, ticket.getTicketPurchasingTime());
            statement.setInt(3, ticket.getTicketNumber());
            statement.setString(4, ticket.getFlightNumber());
            statement.executeUpdate();
            System.out.println("Ticket is added");
        } catch (SQLException e) {
            if (e.getMessage().contains("tickets_ticket_number_key")) {
                System.out.println("Ticket with such number does not exist");
            } else if (e.getMessage().contains("tickets_client_id_fkey")) {
                System.out.println("Customer does not exist");
            } else {
                e.printStackTrace();
            }
        }
    }

    //2. Удаление билета по номеру
    public static void deleteTicket() {
        int record;

        while (true) {
            try {
                System.out.println("Enter ticker number: ");
                ticket.ticketNumber = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }
        String SQL_DELETE_TICKET = "DELETE FROM tickets where ticket_number = ?";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_DELETE_TICKET)) {
            statement.setInt(1, ticket.getTicketNumber());
            statement.executeUpdate();
            record = statement.getUpdateCount();
            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Ticket is deleted");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Ticket found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //3. Изменение билета по номеру
    public static void editTicket() {
        int record;
        while (true) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            try {
                System.out.println("Enter ticker purchasing date: ");
                java.util.Date utilStartDate = formatter.parse(scanner.nextLine());
                ticket.TicketPurchasingTime = new Timestamp(utilStartDate.getTime());
                break;
            } catch (ParseException e) {
                System.out.println("Enter date in such format DD.MM.YYYY!");
            }
        }

        System.out.println("Enter flight number: ");
        ticket.flightNumber = scanner.nextLine();

        while (true) {
            try {
                System.out.println("Enter ticket's old number: ");
                ticket.ticketNumber = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }
        int newTicketNumber;
        while (true) {
            try {
                System.out.println("Enter ticket's new number: ");
                newTicketNumber = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }

        String SQL_EDIT_TICKET = "UPDATE tickets SET " +
                "ticket_purchasing_time = ?, " +
                "ticket_number = ?, " +
                "flight_number = ? " +
                "WHERE ticket_number = ?";

        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_EDIT_TICKET)) {
            statement.setTimestamp(1, ticket.getTicketPurchasingTime());
            statement.setInt(2, ticket.getTicketNumber());
            statement.setString(3, ticket.getFlightNumber());
            statement.setInt(4, newTicketNumber);
            statement.executeUpdate();
            record = statement.getUpdateCount();

            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Ticket is updated");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else if (e.getMessage().contains("tickets_ticket_number_key")) {
                System.out.println("Ticket with such number does not exist");
            } else if (e.getMessage().contains("tickets_client_id_fkey")) {
                System.out.println("Customer does not exist");
            } else {
                e.printStackTrace();
            }
        }
    }

    //4. вывод информации о билете по номеру паспорта и вывести
    //также полное наименование страны из паспорта
    public static void getTicketByPassport() {
        System.out.println("Enter customer passport: ");
        String passport = scanner.nextLine();

        String SQL_INFO_TICKET = "select * from tickets t JOIN customers c ON t.client_id = c.id  where c.passport_id = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_INFO_TICKET)) {
            statement.setString(1, passport);
            ResultSet resultSet = statement.executeQuery();
            String country;
            while (resultSet.next()) {
                ticket.setId(resultSet.getInt("id"));
                ticket.setClientId(resultSet.getInt("client_id"));
                ticket.setTicketPurchasingTime(resultSet.getTimestamp("ticket_purchasing_time"));
                ticket.setTicketNumber(resultSet.getInt("ticket_number"));
                ticket.setFlightNumber(resultSet.getString("flight_number"));
                country = resultSet.getString("citizenship");

                System.out.println(ticket + ", country_name=" + country);
            }

            if (!statement.getResultSet().next()) {
                throw new SQLException("EMPTY");
            }

        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void getTicketByCustom() {
        System.out.println("Enter flight number or passport: ");
        String custom = scanner.nextLine();
        int record = 0;
        String SQL_INFO_TICKET = "select * from tickets t JOIN customers c ON t.client_id = c.id  where t.flight_number = ? or c.passport_id = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_INFO_TICKET)) {
            statement.setString(1, custom);
            statement.setString(2, custom);
            ResultSet resultSet = statement.executeQuery();
            String country;
            while (resultSet.next()) {
                record += 1;
                ticket.setId(resultSet.getInt("id"));
                ticket.setClientId(resultSet.getInt("client_id"));
                ticket.setTicketPurchasingTime(resultSet.getTimestamp("ticket_purchasing_time"));
                ticket.setTicketNumber(resultSet.getInt("ticket_number"));
                ticket.setFlightNumber(resultSet.getString("flight_number"));
                country = resultSet.getString("citizenship");

                System.out.println(ticket + ", country_name=" + country);
            }

            if (record == 0) {
                throw new SQLException("EMPTY");
            }

        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //5. Список всех билетов
    public static void getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String SQL_GET_ALL_TICKETS = "select * from tickets";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_GET_ALL_TICKETS); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(resultSet.getInt("id"));
                ticket.setClientId(resultSet.getInt("client_id"));
                ticket.setTicketPurchasingTime(resultSet.getTimestamp("ticket_purchasing_time"));
                ticket.setTicketNumber(resultSet.getInt("ticket_number"));
                ticket.setFlightNumber(resultSet.getString("flight_number"));
                tickets.add(ticket);
            }

            for(Ticket t : tickets){
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}




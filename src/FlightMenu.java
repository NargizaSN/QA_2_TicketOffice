import org.postgresql.util.PGInterval;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class FlightMenu implements Connect {
    static final Scanner scanner = new Scanner(System.in);
    static Flight flight = new Flight();

    public static void start() {

        while (true) {
            int menuItem;
            System.out.println("Flight menu");
            System.out.println("1) Add flight");
            System.out.println("2) Delete flight");
            System.out.println("3) Edit flight");
            System.out.println("4) Search flight by number");
            System.out.println("5) Show all flights");
            System.out.println("0) Exit");
            menuItem = scanner.nextInt();
            scanner.nextLine();
            if (menuItem == 1) {
                //добавление рейса
                addFlight();
            } else if (menuItem == 2) {
                //удаление рейса
                deleteFlight();
            } else if (menuItem == 3) {
                //изменение рейса
                editFlight();
            } else if (menuItem == 4) {
                //поиск рейса по номеру
                getFlightByNumber();
            } else if (menuItem == 5) {
                //список рейсов
                getAllFlights();
            } else if (menuItem == 0) {
                System.out.println("Exit to the main menu");
                return;
            } else {
                System.out.println("Error! Enter number from menu");
            }
        }
    }

    public static class Flight {
        public Flight() {
        }

        private String aircraftModel;
        private Timestamp departureTime;
        private String flyingFrom;
        private String flyingTo;
        private Time flightTime;
        private Integer numberOfSeats;
        private String flightNumber;


        public String getAircraftModel() {
            return aircraftModel;
        }

        public void setAircraftModel(String aircraftModel) {
            this.aircraftModel = aircraftModel;
        }

        public Timestamp getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(Timestamp departureTime) {
            this.departureTime = departureTime;
        }

        public String getFlyingFrom() {
            return flyingFrom;
        }

        public void setFlyingFrom(String flyingFrom) {
            this.flyingFrom = flyingFrom;
        }

        public String getFlyingTo() {
            return flyingTo;
        }

        public void setFlyingTo(String flyingTo) {
            this.flyingTo = flyingTo;
        }

        public void setFlightTime(Time flightTime) {
            this.flightTime = flightTime;
        }

        public Integer getNumberOfSeats() {
            return numberOfSeats;
        }

        public void setNumberOfSeats(Integer numberOfSeats) {
            this.numberOfSeats = numberOfSeats;
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        @Override
        public String toString() {
            return "Flight{" +
                    "aircraftModel='" + aircraftModel + '\'' +
                    ", departureTime=" + departureTime +
                    ", flyingFrom='" + flyingFrom + '\'' +
                    ", flyingTo='" + flyingTo + '\'' +
                    ", flightTime=" + flightTime +
                    ", numberOfSeats=" + numberOfSeats +
                    ", flightNumber='" + flightNumber + '\'' +
                    '}';
        }
    }

    //1. Добавление рейса
    public static void addFlight() {
        System.out.println("Enter aircraft model: ");
        flight.aircraftModel = scanner.nextLine();

        while (true) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                System.out.println("Enter departure date: ");
                java.util.Date utilStartDate = formatter.parse(scanner.nextLine());
                flight.departureTime = new java.sql.Timestamp(utilStartDate.getTime());
                break;
            } catch (ParseException e) {
                System.out.println("Enter date in such format dd.MM.yyyy HH:mm:ss!");
            }
        }
        System.out.println("Flying from/enter airport code:");
        flight.flyingFrom = scanner.nextLine();
        System.out.println("Flying to/enter airport code::");
        flight.flyingTo = scanner.nextLine();

        PGInterval pgi;
        while (true) {

            try {
                System.out.println("Enter flight time:");
                pgi = new PGInterval(scanner.nextLine());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Enter time in such format HH:mm:ss!");
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.out.println("Something is wrong with time");
                //throw new RuntimeException(e);
            }
        }
        while (true) {
            try {
                System.out.println("Enter number of seats:");
                flight.numberOfSeats = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }

        System.out.println("Enter flight number:");
        flight.flightNumber = scanner.nextLine();

        String SQL_ADD_FLIGHT = "INSERT INTO flights " +
                "(aircraft_model, departure_time, flying_from, flying_to, flight_time, number_of_seats, flight_number)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection conn = Connect.connect();
                PreparedStatement statement = conn.prepareStatement(SQL_ADD_FLIGHT)) {
            statement.setString(1, flight.getAircraftModel());
            statement.setTimestamp(2, flight.getDepartureTime());
            statement.setString(3, flight.getFlyingFrom());
            statement.setString(4, flight.getFlyingTo());
            statement.setObject(5, pgi);
            statement.setInt(6, flight.getNumberOfSeats());
            statement.setString(7, flight.getFlightNumber());
            statement.executeUpdate();

            System.out.println("Flight is added");
        } catch (
                SQLException e) {
            if (e.getMessage().contains("flights_flying_from_fkey")) {
                System.out.println("Airport not exist");
            } else if (e.getMessage().contains("flights_flying_to_fkey")) {
                System.out.println("Airport not exist");
            } else {
                e.printStackTrace();
            }
        }
    }

    //2. Удаление рейса по номеру
    public static void deleteFlight() {
        int record;
        System.out.println("Enter flight number ");
        flight.flightNumber = scanner.nextLine();

        String SQL_DELETE_FLIGHT = "DELETE FROM flights where flight_number = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_DELETE_FLIGHT)) {
            statement.setString(1, flight.getFlightNumber());
            statement.executeUpdate();
            record = statement.getUpdateCount();

            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Delete done");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //3. Изменение рейса по номеру
    public static void editFlight() {
        System.out.println("Enter aircraft model: ");
        flight.aircraftModel = scanner.nextLine();

        while (true) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                System.out.println("Enter departure date: ");
                java.util.Date utilStartDate = formatter.parse(scanner.nextLine());
                flight.departureTime = new java.sql.Timestamp(utilStartDate.getTime());
                break;
            } catch (ParseException e) {
                System.out.println("Enter date in such format dd.MM.yyyy HH:mm:ss!");
            }
        }
        System.out.println("Flying from/enter airport code:");
        flight.flyingFrom = scanner.nextLine();
        System.out.println("Flying to/enter airport code::");
        flight.flyingTo = scanner.nextLine();

        PGInterval pgi;
        while (true) {

            try {
                System.out.println("Enter flight time:");
                pgi = new PGInterval(scanner.nextLine());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Enter time in such format HH:mm:ss!");
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.out.println("Something is wrong with time");
            }
        }
        while (true) {
            try {
                System.out.println("Enter number of seats:");
                flight.numberOfSeats = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }

        System.out.println("Enter new flight number:");
        flight.flightNumber = scanner.nextLine();

        System.out.println("Enter old flight number:");
        String oldFlightNumber = scanner.nextLine();

        String SQL_EDIT_FLIGHT = "UPDATE flights SET " +
                "aircraft_model = ?, " +
                "departure_time = ?, " +
                "flying_from = ?, " +
                "flying_to = ?, " +
                "flight_time = ?, " +
                "number_of_seats = ?, " +
                "flight_number = ?" +
                "WHERE flight_number = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_EDIT_FLIGHT)) {
            statement.setString(1, flight.getAircraftModel());
            statement.setTimestamp(2, flight.getDepartureTime());
            statement.setString(3, flight.getFlyingFrom());
            statement.setString(4, flight.getFlyingTo());
            statement.setObject(5, pgi);
            statement.setInt(6, flight.getNumberOfSeats());
            statement.setString(7, flight.getFlightNumber());
            statement.setString(8, oldFlightNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Данные не найдены");
            } else if (e.getMessage().contains("flights_flying_from_fkey")) {
                System.out.println("Departure airport is not found");
            } else if (e.getMessage().contains("flights_flying_to_fkey")) {
                System.out.println("Arrival airport is not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //4. Вывод информации о рейсе по номеру и также вывести название стран
    // откуда и куда летит самолет
    public static void getFlightByNumber() {
        int record = 0;
        System.out.println("Enter flight number ");
        flight.flightNumber = scanner.nextLine();

        String SQL_INFO_FLIGHT = "select f.*, (select count(1) from tickets t where t.flight_number = f.flight_number) as tickets_count from flights f where flight_number = ?";

        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_INFO_FLIGHT)) {
            statement.setString(1, flight.getFlightNumber());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                record += 1;
                int count;
                Flight flight = new Flight();
                flight.setAircraftModel(resultSet.getString("aircraft_model"));
                flight.setDepartureTime(resultSet.getTimestamp("departure_time"));
                flight.setFlyingFrom(resultSet.getString("flying_from"));
                flight.setFlyingTo(resultSet.getString("flying_to"));
                flight.setFlightTime(resultSet.getTime("flight_time"));
                flight.setNumberOfSeats(resultSet.getInt("number_of_seats"));
                flight.setFlightNumber(resultSet.getString("flight_number"));
                count = resultSet.getInt("tickets_count");

                System.out.println(flight + ",tickets count=" + count);
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

    //5. Список всех рейсов
    public static void getAllFlights() {
        String SQL_GET_ALL_FLIGHTS = "select f.*, (select count(1) from tickets t where t.flight_number = f.flight_number) as tickets_count from flights f";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_GET_ALL_FLIGHTS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Flight flight = new Flight();
                int count;
                flight.setAircraftModel(resultSet.getString("aircraft_model"));
                flight.setDepartureTime(resultSet.getTimestamp("departure_time"));
                flight.setFlyingFrom(resultSet.getString("flying_from"));
                flight.setFlyingTo(resultSet.getString("flying_to"));
                flight.setFlightTime(resultSet.getTime("flight_time"));
                flight.setNumberOfSeats(resultSet.getInt("number_of_seats"));
                flight.setFlightNumber(resultSet.getString("flight_number"));
                count = resultSet.getInt("tickets_count");

                System.out.println(flight + ", tickets count=" + count);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}






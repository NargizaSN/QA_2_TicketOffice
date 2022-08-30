import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AirportMenu implements Connect {
    static final Scanner scanner = new Scanner(System.in);
    static Airport airport = new Airport();
    public static void start() {
        while (true) {
            int menuItem;
            System.out.println("Airport menu: ");
            System.out.println("1) Add airport");
            System.out.println("2) Delete airport");
            System.out.println("3) Edit airport");
            System.out.println("4) Find airport by code");
            System.out.println("5) Show all");
            System.out.println("0) Exit");

            menuItem = scanner.nextInt();
            scanner.nextLine();
            if (menuItem == 1) {
                //добавление аэропорта
                addAirport();
            } else if (menuItem == 2) {
                //удаление аэропорта
                deleteAirport();
            } else if (menuItem == 3) {
                // изменение аэропорта
                editAirport();
            } else if (menuItem == 4) {
                //поиск аэропорта по коду
                getAirportByCode();
            } else if (menuItem == 5) {
                //вывод списка аэропортов
                getAllAirports();
            } else if (menuItem == 0) {
                System.out.println("Exit to main menu");
                return;
            } else {
                System.out.println("Error! Enter number from menu");
            }
        }
    }

    public static class Airport {
        public Airport() {
        }
        private String airportCode;
        private String city;
        private String countryCode;

        public String getAirportCode() {
            return airportCode;
        }

        public void setAirportCode(String airportCode) {
            this.airportCode = airportCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        @Override
        public String toString() {
            return "Airport{" + "airportCode='" + airportCode + '\'' + ", city='" + city + '\'' + ", countryCode='" + countryCode + '\'' + '}';
        }
    }

    //1. Добавление аэропорта
    public static void addAirport() {
        System.out.println("Enter airport code: ");
        airport.airportCode = scanner.nextLine();
        System.out.println("Enter city name: ");
        airport.city = scanner.nextLine();
        System.out.println("Enter country code: ");
        airport.countryCode = scanner.nextLine();

        String SQL_ADD_AIRPORT = "INSERT INTO airports (airport_code, city, country_code) VALUES (?, ?, ?)";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_ADD_AIRPORT)) {
            statement.setString(1, airport.getAirportCode());
            statement.setString(2, airport.getCity());
            statement.setString(3, airport.getCountryCode());
            statement.executeUpdate();
            System.out.println("Airport is added");
        } catch (SQLException e) {
            if (e.getMessage().contains("airports_code_city_country")) {
                System.out.println("Airport already exist");
            } else if (e.getMessage().contains("airports_code")) {
                System.out.println("Airport with such code already exist");
            } else {
                e.printStackTrace();
            }
        }
    }

    //2. Удаление аэропорта по коду
    public static void deleteAirport() {
        int record;

        String SQL_DELETE_AIRPORT = "DELETE FROM airports where id = ? or airport_code = ?";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_DELETE_AIRPORT)) {
            System.out.println("Enter airport code or Id: ");

            if (scanner.hasNextInt()) {
                statement.setInt(1, scanner.nextInt());
                scanner.nextLine();
                statement.setString(2, "");
            } else {
                statement.setInt(1, -1);
                statement.setString(2, scanner.nextLine());
            }

            statement.executeUpdate();
            record = statement.getUpdateCount();

            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Airport deleted");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Airport not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //3. Изменение аэропорта по коду
    public static void editAirport() {
        int record;
        System.out.println("Enter old airport code: ");
        String oldAirportCode = scanner.nextLine();
        System.out.println("Enter new airport code: ");
        airport.airportCode = scanner.nextLine();
        System.out.println("Enter city name: ");
        airport.city = scanner.nextLine();
        System.out.println("Enter country code: ");
        airport.countryCode = scanner.nextLine();

        String SQL_EDIT_AIRPORT = "UPDATE airports set airport_code = ?, city = ?, country_code = ? " + "WHERE airport_code = ?";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_EDIT_AIRPORT)) {
            statement.setString(1, airport.getAirportCode());
            statement.setString(2, airport.getCity());
            statement.setString(3, airport.getCountryCode());
            statement.setString(4, oldAirportCode);
            statement.executeUpdate();

            record = statement.getUpdateCount();

            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Airport is edited");
        } catch (SQLException e) {
            if (e.getMessage().contains("airports_country_code_fkey")) {
                System.out.println("Country code is not correct");
            } else if (e.getMessage().contains("airports_code")) {
                System.out.println("Airport with such code already exist");
            } else if (e.getMessage().contains("airports_code_city_country")) {
                System.out.println("Airport already exist");
            } else if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //4. Вывод информации об аэропорте по коду и также вывести полное наименование страны,
    // где находится аэропорт
    public static void getAirportByCode() {
        int records = 0;
        String countryName = "";

        String SQL_INFO_AIRPORT = "select airport_code, city, country_name from airports a JOIN countries c " +
                "ON a.country_code = c.country_code " +
                "where a.id = ? or a.airport_code = ?";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_INFO_AIRPORT)) {
            System.out.println("Enter airport code: ");
            if (scanner.hasNextInt()) {
                statement.setInt(1, scanner.nextInt());
                scanner.nextLine();
                statement.setString(2, "");
            } else {
                statement.setInt(1, -1);
                statement.setString(2, scanner.nextLine());
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                records += 1;
                airport.setAirportCode(resultSet.getString("airport_code"));
                airport.setCity(resultSet.getString("city"));
                countryName = resultSet.getString("country_name");
            }
            if (records == 0) {
                throw new SQLException("Airport not found");
            }
            System.out.println(airport + ", country_name=" + countryName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //5. Список всех аэропортов
    public static void getAllAirports() {
        List<Airport> airports = new ArrayList<>();
        String SQL_GET_ALL_AIRPORTS = "select * from airports";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_GET_ALL_AIRPORTS); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Airport airport = new Airport();
                airport.setAirportCode(resultSet.getString("airport_code"));
                airport.setCity(resultSet.getString("city"));
                airport.setCountryCode(resultSet.getString("country_code"));
                airports.add(airport);
            }

            for (Airport a : airports) {
                System.out.println(a);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
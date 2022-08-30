import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CountryMenu implements Connect {

    static final Scanner scanner = new Scanner(System.in);
    static Country country = new Country();

    public static void start() {
        while (true) {
            int menuItem;
            System.out.println("Country menu: ");
            System.out.println("1) Add country");
            System.out.println("2) Delete country");
            System.out.println("3) Edit country");
            System.out.println("4) Search country by code");
            System.out.println("5) Show all");
            System.out.println("0) Exit");

            menuItem = scanner.nextInt();
            scanner.nextLine();
            if (menuItem == 1) {
                addCountry();
            } else if (menuItem == 2) {
                //удаление страны
                deleteCountry();
            } else if (menuItem == 3) {
                // изменение страны
                editCountry();
            } else if (menuItem == 4) {
                //поиск страны по коду
                getCountryByCode();
            } else if (menuItem == 5) {
                //вывод списка стран
                getAllCountries();
            } else if (menuItem == 0) {
                System.out.println("Exit to the main menu");
                return;
            } else {
                System.out.println("Error! Enter number from menu");
            }
        }


    }

    public static class Country {
        public Country() {
        }

        private int id;
        private String countryCode;
        private String countryName;

        public void setId(int id) {
            this.id = id;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        @Override
        public String toString() {
            return "Country{" +
                    "id=" + id +
                    ", countryCode='" + countryCode + '\'' +
                    ", countryName='" + countryName + '\'' +
                    '}';
        }
    }

    //1. Добавление страны
    public static void addCountry() {
        System.out.println("Enter country code: ");
        country.countryCode = scanner.nextLine();
        System.out.println("Enter country name: ");
        country.countryName = scanner.nextLine();

        String SQL_ADD_COUNTRY = "INSERT INTO countries (country_code, country_name) VALUES (?, ?)";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_ADD_COUNTRY)) {
            statement.setString(1, country.getCountryCode());
            statement.setString(2, country.getCountryName());
            statement.executeUpdate();
            System.out.println("Country is added");
        } catch (SQLException e) {
            if (e.getMessage().contains("countries_country_code_key")) {
                System.out.println("Country with such code already exist");
            } else if (e.getMessage().contains("countries_country_name_key")) {
                System.out.println("Country with such name already exist");
            } else if (e.getMessage().contains("value too long for type character")) {
                System.out.println("Name is too long");
            } else {
                e.printStackTrace();
            }
        }
    }

    //2. Удаление страны по коду
    public static void deleteCountry() {
        int record;
        String SQL_DELETE_COUNTRY = "DELETE FROM countries where id = ? or country_code = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_DELETE_COUNTRY)) {
            System.out.println("Enter country code or Id: ");
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
            System.out.println("Country is deleted");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Country not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //3. Изменение страны по коду
    public static void editCountry() {
        int record;
        System.out.println("Enter country code");
        country.countryCode = scanner.nextLine();
        System.out.println("Enter country name");
        country.countryName = scanner.nextLine();

        String SQL_EDIT = "UPDATE countries set country_code = ?, country_name = ? WHERE country_code = ?";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_EDIT)) {
            statement.setString(1, country.getCountryCode());
            statement.setString(2, country.getCountryName());
            statement.setString(3, country.getCountryCode());
            statement.executeUpdate();

            record = statement.getUpdateCount();
            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Country is edited");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //4. Вывод информации о стране по коду
    public static void getCountryByCode() {
        int records = 0;

        String SQL_SHOW_COUNTRY = "select * from countries where id = ? or country_code = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_SHOW_COUNTRY)) {
            System.out.println("Enter country code or id");
            if (scanner.hasNextInt()) {
                statement.setInt(1, scanner.nextInt());
                scanner.nextLine();
                statement.setString(2, "");
            } else {
                statement.setInt(1, -1);
                statement.setString(2, scanner.nextLine());
            }
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                records += 1;
                country.setId(rs.getInt("id"));
                country.setCountryCode(rs.getString("country_code"));
                country.setCountryName(rs.getString("country_name"));
            }
            if (records == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println(country);
        } catch (SQLException e) {
            if(e.getMessage().equals("EMPTY")){
                System.out.println("Country not found");
            }else{
                e.printStackTrace();
            }
        }
    }

    //5. Список всех стран
    public static void getAllCountries() {
        List<Country> countries = new ArrayList<>();

        String SQL_GET_ALL_COUNTRIES = "select * from countries";
        try (Connection conn = Connect.connect();
             PreparedStatement statement = conn.prepareStatement(SQL_GET_ALL_COUNTRIES);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Country country = new Country();
                country.setId(resultSet.getInt("id"));
                country.setCountryCode(resultSet.getString("country_code"));
                country.setCountryName(resultSet.getString("country_name"));
                countries.add(country);
            }
            for (Country c : countries) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerMenu implements Connect {
    static final Scanner scanner = new Scanner(System.in);
    static Customer customer = new Customer();

    public static void start() {
        while (true) {
            int menuItem;
            System.out.println("Customer menu: ");
            System.out.println("1) Add customer");
            System.out.println("2) Delete customer");
            System.out.println("3) Edit customer");
            System.out.println("4) Search customer by passport number");
            System.out.println("5) Show all customers");
            System.out.println("0) Exit");

            menuItem = scanner.nextInt();
            scanner.nextLine();
            if (menuItem == 1) {
                //добавление клиента
                addCustomer();
            } else if (menuItem == 2) {
                //удаление клиента
                deleteCustomer();
            } else if (menuItem == 3) {
                // изменение клиента
                editCustomer();
            } else if (menuItem == 4) {
                //поиск клиента по номеру паспорта
                getCustomerByPassport();
            } else if (menuItem == 5) {
                //вывод списка клиентов
                getAllCustomers();
            } else if (menuItem == 0) {
                System.out.println("Exit to the main menu");
                return;
            } else {
                System.out.println("Error! Enter number from menu");
            }
        }
    }

    public static class Customer {
        public Customer() {
        }

        private Integer id;
        private Integer INN;
        private String passportId;
        private String customerName;
        private String gender;
        private Date birthday;
        private String citizenship;

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getINN() {
            return INN;
        }

        public void setINN(Integer INN) {
            this.INN = INN;
        }

        public String getPassportId() {
            return passportId;
        }

        public void setPassportId(String passportId) {
            this.passportId = passportId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Date getBirthday() {
            return this.birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public String getCitizenship() {
            return this.citizenship;
        }

        public void setCitizenship(String citizenship) {
            this.citizenship = citizenship;
        }

        @Override
        public String toString() {
            return "Customer{" + "id=" + id + ", INN=" + INN + ", passportId='" + passportId + '\'' + ", customerName='" + customerName + '\'' + ", gender='" + gender + '\'' + ", birthday=" + birthday + ", citizenship='" + citizenship + '\'' + '}';
        }
    }

    //1. Добавление клиента
    public static void addCustomer() {
        System.out.println("Enter INN: ");
        customer.INN = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter passport: ");
        customer.passportId = scanner.nextLine();
        System.out.println("Enter customer name: ");
        customer.customerName = scanner.nextLine();
        System.out.println("Enter customer gender: ");
        customer.gender = scanner.nextLine();
        System.out.println("Enter customer birthday: ");
        try {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date utilStartDate = formatter.parse(scanner.nextLine());
            customer.birthday = new java.sql.Date(utilStartDate.getTime());
        } catch (ParseException e) {
            System.out.println("Enter date in such format dd.MM.yyyy HH:mm:ss!");
            //throw new RuntimeException(e);
        }
        System.out.println("Enter customer citizenship: ");
        customer.citizenship = scanner.nextLine();

        String SQL_ADD_CUSTOMER = "INSERT INTO customers (INN, passport_id, customer_name, gender, birthday, citizenship) " + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_ADD_CUSTOMER)) {
            statement.setInt(1, customer.getINN());
            statement.setString(2, customer.getPassportId());
            statement.setString(3, customer.getCustomerName());
            statement.setString(4, customer.getGender());
            statement.setDate(5, (java.sql.Date) customer.getBirthday());
            statement.setString(6, customer.getCitizenship());
            statement.executeUpdate();
            System.out.println("Customer is added");
        } catch (SQLException e) {
            if (e.getMessage().contains("customers_inn_key")) {
                System.out.println("Customer with such INN already exist");
            } else if (e.getMessage().contains("customers_passport_id_key")) {
                System.out.println("Customer with such passport already exist");
            } else if (e.getMessage().contains("customers_citizenship_fkey")) {
                System.out.println("Country code is not correct");
            } else {
                e.printStackTrace();
            }
        }
    }

    //2. Удаление клиента по номеру паспорта
    public static void deleteCustomer() {
        int record;
        System.out.println("Enter customer passport: ");
        customer.passportId = scanner.nextLine();

        String SQL_DELETE_CUSTOMER = "DELETE FROM customers where passport_id = ?";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_DELETE_CUSTOMER)) {
            statement.setString(1, customer.getPassportId());
            statement.executeUpdate();
            record = statement.getUpdateCount();
            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Customer is deleted");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else {
                e.printStackTrace();
            }
        }
    }

    //3. Изменение клиента по номеру паспорта
    public static void editCustomer() {
        String newPassport;
        int record;

        System.out.println("Enter old INN: ");
        customer.passportId = scanner.nextLine();
        System.out.println("Enter new INN: ");
        newPassport = scanner.nextLine();
        while (true) {
            try {
                System.out.println("Enter INN ");
                customer.INN = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Not correct");
                scanner.next();
            }
        }
        System.out.println("Enter name: ");
        customer.customerName = scanner.nextLine();
        System.out.println("Enter gender: ");
        customer.gender = scanner.nextLine();
        System.out.println("Enter birthday: ");
        while (true) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            try {
                java.util.Date utilStartDate = formatter.parse(scanner.nextLine());
                customer.birthday = new java.sql.Date(utilStartDate.getTime());
                break;
            } catch (ParseException e) {
                System.out.println("Enter date in such format DD.MM.YYYY!");
            }
        }
        System.out.println("Enter citizenship: ");
        customer.citizenship = scanner.nextLine();

        String SQL_EDIT_CUSTOMER = "UPDATE customers set " +
                "INN = ?, " +
                "passport_id = ?, " +
                "customer_name = ?, " +
                "gender = ?, " +
                "birthday = ?, " +
                "citizenship = ? " +
                "WHERE passport_id = ?";

        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_EDIT_CUSTOMER)) {
            statement.setInt(1, customer.getINN());
            statement.setString(2, newPassport);
            statement.setString(3, customer.getCustomerName());
            statement.setString(4, customer.getGender());
            statement.setDate(5, (java.sql.Date) customer.getBirthday());
            statement.setString(6, customer.getCitizenship());
            statement.setString(7, customer.getPassportId());
            statement.executeUpdate();

            record = statement.getUpdateCount();

            if (record == 0) {
                throw new SQLException("EMPTY");
            }
            System.out.println("Customer is edited");
        } catch (SQLException e) {
            if (e.getMessage().equals("EMPTY")) {
                System.out.println("Not found");
            } else if (e.getMessage().contains("customers_inn_key")) {
                System.out.println("Customer with such INN already exist");
            } else if (e.getMessage().contains("customers_passport_id_key")) {
                System.out.println("Customer with such passport already exist");
            } else if (e.getMessage().contains("customers_citizenship_fkey")) {
                System.out.println("Country code is not correct");
            } else {
                e.printStackTrace();
            }
        }
    }

    //4. Вывод информации о клиенте по коду и также вывести полное наименование страны
    // из паспорта
    public static void getCustomerByPassport() {
        System.out.println("Enter passport: ");
        String passport = scanner.nextLine();
        int record = 0;
        String SQL_INFO_CUSTOMER = "select * from customers where passport_id = ?";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_INFO_CUSTOMER)) {
            statement.setString(1, passport);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                record += 1;
                customer.setId(resultSet.getInt("id"));
                customer.setINN(resultSet.getInt("INN"));
                customer.setPassportId(resultSet.getString("passport_id"));
                customer.setCustomerName(resultSet.getString("customer_name"));
                customer.setGender(resultSet.getString("gender"));
                customer.setBirthday(resultSet.getDate("birthday"));
                customer.setCitizenship(resultSet.getString("citizenship"));

                System.out.println(customer);
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

    //5. Список всех клиентов
    public static void getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String SQL_GET_ALL_CUSTOMERS = "select * from customers";
        try (Connection conn = Connect.connect(); PreparedStatement statement = conn.prepareStatement(SQL_GET_ALL_CUSTOMERS); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Customer customer = new Customer();
                customer.setId(resultSet.getInt("id"));
                customer.setINN(resultSet.getInt("INN"));
                customer.setPassportId(resultSet.getString("passport_id"));
                customer.setCustomerName(resultSet.getString("customer_name"));
                customer.setGender(resultSet.getString("gender"));
                customer.setBirthday(resultSet.getDate("birthday"));
                customer.setCitizenship(resultSet.getString("citizenship"));
                customers.add(customer);
            }
            for (Customer c : customers) {
                System.out.println(c.toString());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}




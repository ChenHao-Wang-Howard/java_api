import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        // Database connection details
        String jdbcURL = "jdbc:mysql://localhost:3306/HealthServiceCenters";
        String username = "root";
        String password = "35172846";
        String csvFilePath = "CentersData.csv";

        // Register MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("Register MySQL JDBC driver successful");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            return;
        }

        // Test database connection
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            logger.info("Database connection successful");
            CentersDAO centersDAO = new CentersDAOImpl(connection);
            fetchDataAndInsertToDB(centersDAO);
            /*
             * // User input for queries
             * Scanner scanner = new Scanner(System.in);
             * 
             * 
             */
            // User input for queries
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Choose an operation:");
                System.out.println("1. Insert record");
                System.out.println("2. Update record");
                System.out.println("3. Delete record");
                System.out.println("4. View record by ID");
                System.out.println("5. View all records");
                System.out.println("6. Query records by condition");
                System.out.println("7. Export query result to CSV");
                System.out.println("8. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        if (insertRecord(scanner, centersDAO)) {
                            System.out.println("Record inserted successfully.");
                        } else {
                            System.out.println("Failed to insert record.");
                        }
                        break;
                    case 2:
                        if (updateRecord(scanner, centersDAO)) {
                            System.out.println("Record updated successfully.");
                        } else {
                            System.out.println("Failed to update record.");
                        }
                        break;
                    case 3:
                        if (deleteRecord(scanner, centersDAO)) {
                            System.out.println("Record delete successfully.");
                        } else {
                            System.out.println("Failed to delete record.");
                        }
                        break;
                    case 4:
                        viewRecordById(scanner, centersDAO);

                        break;
                    case 5:
                        viewAllRecords(centersDAO);
                        break;
                    case 6:
                        queryRecordsByCondition(scanner, centersDAO);

                        
                        break;
                    case 7:
                        exportToCSV(scanner, centersDAO, csvFilePath);
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException | MalformedURLException e) {
            logger.log(Level.SEVERE, "Failed to connect to the database", e);
        }

    }

    // insert
    private static boolean insertRecord(Scanner scanner, CentersDAO centersDAO) {
        System.out.println("Enter center name: ");
        String centerName = scanner.nextLine();
        System.out.println("Enter URL: ");
        String url = scanner.nextLine();
        System.out.println("Enter district code: ");
        int districtCode = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter address: ");
        String address = scanner.nextLine();
        System.out.println("Enter phone: ");
        String phone = scanner.nextLine();

        Center center = new Center(centerName, url, districtCode, address, phone);
        try {
            return centersDAO.insertCenter(center);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to insert record", e);
            return false;
        }
    }

    // update
    private static boolean updateRecord(Scanner scanner, CentersDAO centersDAO) {
        System.out.println("Enter ID of the record to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter new center name: ");
        String centerName = scanner.nextLine();
        System.out.println("Enter new URL: ");
        String url = scanner.nextLine();
        System.out.println("Enter new district code: ");
        int districtCode = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter new address: ");
        String address = scanner.nextLine();
        System.out.println("Enter new phone: ");
        String phone = scanner.nextLine();

        Center center = new Center(id, centerName, url, districtCode, address, phone);

        try {
            return centersDAO.updateCenter(center);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update record", e);
            return false;
        }
    }

    // delete
    private static boolean deleteRecord(Scanner scanner, CentersDAO centersDAO) {
        System.out.println("Enter ID of the record to delete: ");
        int id = scanner.nextInt();
        try {
            return centersDAO.deleteCenter(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete record", e);
            return false;
        }

    }

    // viewRecordById
    private static void viewRecordById(Scanner scanner, CentersDAO centersDAO) {
        System.out.println("Enter ID of the record to print: ");
        int id = scanner.nextInt();
        try {
            System.out.println(centersDAO.getCenterById(id).getCenterName());
            System.out.println(centersDAO.getCenterById(id).getAddress());
            System.out.println(centersDAO.getCenterById(id).getDistrictCode());
            System.out.println(centersDAO.getCenterById(id).getUrl());
            System.out.println(centersDAO.getCenterById(id).getPhone());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load record", e);
        }

    }

    // viewAllRecords(centersDAO)
    private static void viewAllRecords(CentersDAO centersDAO) throws SQLException {
        List<Center> centers = centersDAO.getAllCenters();
        for (Center center : centers) {
            String row = String.format("%d,%s,%s,%d,%s,%s",
                    center.getId(),
                    center.getCenterName(),
                    center.getUrl(),
                    center.getDistrictCode(),
                    center.getAddress(),
                    center.getPhone());
            System.out.println(row);
        }

    }

    // queryRecordsByCondition
    private static void queryRecordsByCondition(Scanner scanner, CentersDAO centersDAO) throws SQLException {
        System.out.println("Enter condition for querying centers (e.g., DistrictCode = 63000080): ");
        String condition = scanner.nextLine();
        List<Center> centers = centersDAO.getCentersByCondition(condition);
        for (Center center : centers) {
            String row = String.format("%d,%s,%s,%d,%s,%s",
                    center.getId(),
                    center.getCenterName(),
                    center.getUrl(),
                    center.getDistrictCode(),
                    center.getAddress(),
                    center.getPhone());
            System.out.println(row);
        }

    }

    private static void fetchDataAndInsertToDB(CentersDAO centersDAO) throws IOException, SQLException {
        URL url = new URL(
                "https://data.taipei/api/frontstage/tpeod/dataset/resource.download?rid=1e57f3cb-7063-4db7-a263-106ab9bdf6d1");
        try (InputStream input = url.openStream();
                InputStreamReader isr = new InputStreamReader(input, "MS950");
                BufferedReader br = new BufferedReader(isr)) {

            String str = "";
            boolean isFirstLine = true; // Flag to skip the first line
            while ((str = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }

                logger.info("Processing line: " + str);
                String[] split = str.split(",");
                if (split.length == 5) {
                    String centerName = split[0].trim();
                    String urlValue = split[1].trim();
                    String districtCodeStr = split[2].trim();
                    if (isNumeric(districtCodeStr)) {
                        int districtCode = Integer.parseInt(districtCodeStr);
                        String address = split[3].trim();
                        String phone = split[4].trim();
                        Center center = new Center(centerName, urlValue, districtCode, address, phone);
                        centersDAO.insertCenter(center);
                    } else {
                        logger.warning("Invalid number format in line: " + str);
                    }
                } else {
                    logger.warning("Invalid data line: " + str);
                }
            }
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private static void exportToCSV(Scanner scanner, CentersDAO centersDAO, String csvFilePath) throws SQLException {
        System.out.println("Enter condition for querying centers (e.g., DistrictCode = 63000080): ");

        String condition = scanner.nextLine();
        List<Center> centers = centersDAO.getCentersByCondition(condition);
        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvFilePath))) {
            csvWriter.write("id,CenterName,URL,DistrictCode,Address,Phone");
            csvWriter.newLine();

            for (Center center : centers) {
                String row = String.format("%d,%s,%s,%d,%s,%s",
                        center.getId(),
                        center.getCenterName(),
                        center.getUrl(),
                        center.getDistrictCode(),
                        center.getAddress(),
                        center.getPhone());
                csvWriter.write(row);
                csvWriter.newLine();
            }

            logger.info("Data exported to CSV file successfully");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error exporting data to CSV", e);
        }
    }
}

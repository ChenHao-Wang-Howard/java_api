import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        // Database connection details
        String jdbcURL = "jdbc:mysql://localhost:3306/HealthServiceCenters";
        String username = "root";
        String password = "35172846";
        String sqlInsert = "INSERT INTO Centers (CenterName, URL, DistrictCode, Address, Phone) VALUES (?, ?, ?, ?, ?)";
        String sqlSelect = "SELECT * FROM Centers";
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
                        try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
                            statement.setString(1, split[0].trim());
                            statement.setString(2, split[1].trim());

                            String districtCodeStr = split[2].trim();
                            if (isNumeric(districtCodeStr)) {
                                statement.setInt(3, Integer.parseInt(districtCodeStr));
                            } else {
                                logger.warning("Invalid number format in line: " + str);
                                continue;
                            }

                            statement.setString(4, split[3].trim());
                            statement.setString(5, split[4].trim());
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            logger.log(Level.SEVERE, "Database insertion error: " + str, e);
                        }
                    } else {
                        logger.warning("Invalid data line: " + str);
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reading URL", e);
            }

            // Export data to CSV
            try (PreparedStatement statement = connection.prepareStatement(sqlSelect);
                    ResultSet resultSet = statement.executeQuery();
                    BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvFilePath))) {

                // CSV header
                csvWriter.write("id,CenterName,URL,DistrictCode,Address,Phone");
                csvWriter.newLine();

                // Write data
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String centerName = resultSet.getString("CenterName");
                    String urlValue = resultSet.getString("URL");
                    int districtCode = resultSet.getInt("DistrictCode");
                    String address = resultSet.getString("Address");
                    String phone = resultSet.getString("Phone");

                    String row = String.format("%d,%s,%s,%d,%s,%s", id, centerName, urlValue, districtCode, address,
                            phone);
                    csvWriter.write(row);
                    csvWriter.newLine();
                }

                logger.info("Data exported to CSV file successfully");
            } catch (SQLException | IOException e) {
                logger.log(Level.SEVERE, "Error exporting data to CSV", e);
            }

        } catch (SQLException | MalformedURLException e) {
            logger.log(Level.SEVERE, "Failed to connect to the database", e);
            return;
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
}

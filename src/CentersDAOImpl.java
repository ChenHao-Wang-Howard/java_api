import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CentersDAOImpl implements CentersDAO {
    private Connection connection;

    public CentersDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean insertCenter(Center center) throws SQLException {
        String sql = "INSERT INTO Centers (CenterName, URL, DistrictCode, Address, Phone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, center.getCenterName());
            statement.setString(2, center.getUrl());
            statement.setInt(3, center.getDistrictCode());
            statement.setString(4, center.getAddress());
            statement.setString(5, center.getPhone());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateCenter(Center center) throws SQLException {
        String sql = "UPDATE Centers SET CenterName = ?, URL = ?, DistrictCode = ?, Address = ?, Phone = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, center.getCenterName());
            statement.setString(2, center.getUrl());
            statement.setInt(3, center.getDistrictCode());
            statement.setString(4, center.getAddress());
            statement.setString(5, center.getPhone());
            statement.setInt(6, center.getId());
            return statement.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean deleteCenter(int id) throws SQLException {
        String sql = "DELETE FROM Centers WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public Center getCenterById(int id) throws SQLException {
        String sql = "SELECT * FROM Centers WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Center(
                        resultSet.getString("CenterName"),
                        resultSet.getString("URL"),
                        resultSet.getInt("DistrictCode"),
                        resultSet.getString("Address"),
                        resultSet.getString("Phone"));
            }
        }
        return null;
    }

    @Override
    public List<Center> getAllCenters() throws SQLException {
        List<Center> centers = new ArrayList<>();
        String sql = "SELECT * FROM Centers";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Center center = new Center(
                        resultSet.getInt("id"),
                        resultSet.getString("CenterName"),
                        resultSet.getString("URL"),
                        resultSet.getInt("DistrictCode"),
                        resultSet.getString("Address"),
                        resultSet.getString("Phone"));
                centers.add(center);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return centers;
    }

    @Override
    public List<Center> getCentersByCondition(String condition) throws SQLException {
        List<Center> centers = new ArrayList<>();
        String sql = "SELECT * FROM Centers WHERE " + condition;
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Center center = new Center(
                        resultSet.getInt("id"),
                        resultSet.getString("CenterName"),
                        resultSet.getString("URL"),
                        resultSet.getInt("DistrictCode"),
                        resultSet.getString("Address"),
                        resultSet.getString("Phone"));
                centers.add(center);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return centers;
    }
}

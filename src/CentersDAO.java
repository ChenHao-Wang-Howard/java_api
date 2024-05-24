import java.sql.SQLException;
import java.util.List;

public interface CentersDAO {
    boolean insertCenter(Center center) throws SQLException;

    boolean updateCenter(Center center) throws SQLException;

    boolean deleteCenter(int id) throws SQLException;

    Center getCenterById(int id) throws SQLException;

    List<Center> getAllCenters() throws SQLException;

    List<Center> getCentersByCondition(String condition) throws SQLException;
}

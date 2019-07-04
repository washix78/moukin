package jp.moukin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    
    private String jdbcUrl;
    private String userName;
    private String password;
    
    public DBManager(String jdbcUrl, String userName, String password) {
        this.jdbcUrl = jdbcUrl;
        this.userName = userName;
        this.password = password;
    }
    
    public Object getResult(String sql) throws SQLException {
        try (
            Connection con = DriverManager.getConnection(jdbcUrl, userName, password);
            Statement stmt = con.createStatement();
        ) {
            if (stmt.execute(sql)) {
                return toList(stmt);
            } else {
                return new Integer(stmt.getUpdateCount());
            }
        }
    }

    private List<String[]> toList(Statement stmt) throws SQLException {
        try (
            ResultSet rs = stmt.getResultSet();
        ) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            String[] names = new String[columnCount];
            for (int arrayI = 0, columnI = 1; arrayI != columnCount; arrayI++, columnI++) {
                names[arrayI] = rsmd.getColumnName(columnI).toLowerCase();
            }
            
            List<String[]> list = new ArrayList<>();
            list.add(names);
            
            while (rs.next()) {
                String[] values = new String[columnCount];
                for (int arrayI = 0, columnI = 1; arrayI != columnCount; arrayI++, columnI++) {
                    values[arrayI] = rs.getString(columnI);
                }
                list.add(values);
            }
            return list;
        }
    }
}

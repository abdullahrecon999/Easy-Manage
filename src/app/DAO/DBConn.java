package app.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    private static String conn_string="";
    private static String username="";
    private static String password="";
    private static Connection conn=null;

    private DBConn() {
    }

    public static DBConn getInstance() {
        return new DBConn();
    }

    public static Connection getConnection() {
        conn_string = "jdbc:oracle:thin:@localhost:1521:xe";
        username="School_Admin";
        password="root";

        try {
            conn=DriverManager.getConnection(conn_string,username,password);
            System.out.println("Connection Established");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database Connection Failed");
        }
        return conn;
    }

    public static void closeConn(Connection con) throws SQLException{
        if(con!=null){
            con.close();
            System.out.println("connection closed");
        }
    }
}

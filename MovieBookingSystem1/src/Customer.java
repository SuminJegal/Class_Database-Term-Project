import java.util.Scanner;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {
    
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:DBSERVER";
    private static final String USER = "DBUSER";
    private static final String PASS = "realtime";
    
    private static Connection conn = null;
    private static DatabaseMetaData meta = null;
    
    public boolean createConn() {
        try {
            Class.forName(DRIVER);
            System.out.println("[*] JDBC ����̹� �ε� �Ϸ�.");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[*] �����ͺ��̽� ���� �Ϸ�.");
        } catch(Exception e) {
            System.out.println("[*] �����ͺ��̽� ���� ���� �߻�: \n" + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    public Connection getConn() {
        return conn;
    }
    
    public DatabaseMetaData getDBMD(Connection conn) {
        try {
            meta = conn.getMetaData();
        } catch (Exception e) {
            System.out.println("[*] DBMD ���� �߻�: \n" + e.getMessage());
        }
        
        return meta;
    }
    
    public void login(Connection conn){
        
    }
    
    public void signUp(Connection conn){
        
    }
    
    public void bookingMovie(Connection conn){
        
    }
    
    public void SearchMovie(Connection conn){
        
    }
    
    public void modifyMyInformation(Connection conn){
        
    }
    
    public void secession(Connection conn){
        
    }

}

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


public class Manager {
    
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:DBSERVER";
    private static final String USER = "HEEBIN";
    private static final String PASS = "heebin";
    
    private static Connection conn = null;
    private static DatabaseMetaData meta = null;
    
    public boolean createConn() {
        try {
            Class.forName(DRIVER);
            System.out.println("[*] JDBC 드라이버 로드 완료.");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[*] 데이터베이스 접속 완료.");
        } catch(Exception e) {
            System.out.println("[*] 데이터베이스 접속 오류 발생: \n" + e.getMessage());
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
            System.out.println("[*] DBMD 오류 발생: \n" + e.getMessage());
        }
        
        return meta;
    }

    public void manageMovie(Connection conn){
        int inputForManageMovie = 0;
        while(inputForManageMovie != 4){
            System.out.print("What do you want to do? (1: Upload movie, 2: delete movie, 3: modify movie information, 4: exit)");
            switch (inputForManageMovie){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break; 
            default:
                System.out.println("Wrong input!!!");
                break;
            }
        }
       
    }

    public void manageCinema(Connection conn){

    }

    public void manageScreening(Connection conn){

    }

    public void manageVIP(Connection conn){

    }

    public void ticketing(Connection conn){

    }

    public void helpPayment(Connection conn){

    }

}
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

class DAO {	// Database Access Object
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
    
    public boolean insert(Connection conn, String query) {
        try {
            Statement stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(query);
            if(rowCount == 0) {
                System.out.println("������ ���� ����");
                return false;
            } else {
                System.out.println("������ ���� ����");
            }
        } catch (Exception e) {
            System.out.println("[*] INSERT ���� �߻�: \n" + e.getMessage());
        }
        
        return true;
    }
    
    public ResultSet select(Connection conn, String query) {
        Statement stmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsMeta = null;
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            rsMeta = rs.getMetaData();
            
            // ���� ��� ��Ÿ ���� ����
            for(int col = 1; col <= rsMeta.getColumnCount(); col++) {
                int type = rsMeta.getColumnType(col);
                String typeName = rsMeta.getColumnTypeName(col);
                String name = rsMeta.getColumnName(col);
                System.out.println(col + "st column " + name + 
                        " is JDBC type " + type + " which is called " + typeName);
            }
            
            // ���� ��� ��ȯ
            return rs;
        } catch (Exception e) {
            System.out.println("[*] SELECT ���� �߻�: \n" + e.getMessage());
        }
        
        return rs;
    }
    
    public boolean updateBranch(Connection conn, String branch_number, int asset) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE BRANCH SET ASSETS = ? WHERE BRANCH_NUMBER = ?");
            
            pstmt.setInt(1, asset);
            pstmt.setString(2, branch_number);
            
            int rowCount = pstmt.executeUpdate();
            if(rowCount == 0) {
                System.out.println("������ ���� ����");
            } else {
                System.out.println("������ ���� ����");
            }
        } catch (Exception e) {
            System.out.println("[*] UPDATE ���� �߻�: \n" + e.getMessage());
        }
        
        return true;
    }
    
    public boolean delete(Connection conn, String query) {
        try {
            Statement stmt = conn.createStatement();
            int rowCount = stmt.executeUpdate(query);
            if(rowCount == 0) {
                System.out.println("������ ���� ����");
                return false;
            } else {
                System.out.println("������ ���� ����");
            }
        } catch (Exception e) {
            System.out.println("[*] DELETE ���� �߻�: \n" + e.getMessage());
        }
        
        return true;
    }
    
    public DatabaseMetaData getDBMD(Connection conn) {
        try {
            meta = conn.getMetaData();
        } catch (Exception e) {
            System.out.println("[*] DBMD ���� �߻�: \n" + e.getMessage());
        }
        
        return meta;
    }
}

public class Main {
    private static Connection conn = null;          // Ŀ�ؼ� ���� ���� ��ü
    private static DatabaseMetaData meta = null;    // �����ͺ��̽� ��Ÿ���� ���� ��ü
    
    public static void main(String[] args) {
        DAO dao = new DAO();        // Database Access Object ����
        dao.createConn();           // �����ͺ��̽��� ���� ����
        conn = dao.getConn();       // �����ͺ��̽� ���� ���� ���
        
        meta = dao.getDBMD(conn);   // �����ͺ��̽� ��Ÿ���� ���
        try {
            System.out.println(meta.getTimeDateFunctions());
            System.out.println(meta.getUserName());
        } catch (Exception e) {
            System.out.println("[*] ��Ÿ���� ��� ���� �߻�: \n" + e.getMessage());
        }
        
        try {
            String account_number, branch_number;
            int balance;
            
            ResultSet rs = dao.select(conn, "SELECT * FROM ACCOUNT");
            while(rs.next()) {
                account_number = rs.getString(1);
                branch_number = rs.getString(2);
                balance = rs.getInt(3);
                System.out.println(account_number + ", " + branch_number + ", " + balance);
            }
        } catch (Exception e) {
            System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
        }
        
        dao.insert(conn, "INSERT INTO ACCOUNT VALUES ('A-103', 'B4', 700)");
        dao.insert(conn, "INSERT INTO ACCOUNT VALUES ('A-555', 'B2', 300)");
        dao.updateBranch(conn, "B3", 800000);
        dao.delete(conn, "DELETE FROM ACCOUNT WHERE ACCOUNT_NUMBER = 'A-103'");
        
        try {
            conn.close();   // Ŀ�ؼ� �ݱ�
            System.out.println("[*] �����ͺ��̽� ���� ����.");
        } catch (SQLException e) {
            System.out.println("[*] ���� �߻�: \n" + e.getMessage());
        }
    }
}
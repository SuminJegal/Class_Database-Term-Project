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
    
    String inputedUserID;
    
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:DBSERVER";
    private static final String USER = "HEEBIN";
    private static final String PASS = "heebin";
    
    private static Connection conn = null;
    private static DatabaseMetaData meta = null;
    
    Scanner scan;
    
    public Customer(){
        scan = new Scanner(System.in);
    }
    
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
    
    public boolean login(Connection conn){
        String userPW = "";
        String inputPW;
        
        System.out.print("What is your ID? : ");
        inputedUserID = scan.nextLine();
        System.out.print("What is your pw? : ");
        inputPW = scan.nextLine();
        
        try{
            ResultSet rs = this.select(conn, "SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '"+inputedUserID+"'");
            rs.next();
            String tempuserPW = rs.getString(1);
            int i=0;
            while(tempuserPW.charAt(i) != ' '){
                userPW = userPW + tempuserPW.charAt(i);
                i++;
            }
            if(inputPW.equals(userPW)){
                System.out.println("Log-in success!");
                return true;
            }
            else {
                System.out.println("Fail to Log-in");
                return false;
            }
        } catch (Exception e) {
            System.out.println("There is not your ID");
            //System.out.println("[*] ���� ��� ��� ���� �߻�: \n" + e.getMessage());
            return false;
        }
        
    }
    
    public boolean signUp(Connection conn){
        String inputID = "";
        String inputName, inputPW, inputPhoneNumber, inputAddress, inputBirth;
        
        boolean nobodyUseThisID = false;
        
        while(nobodyUseThisID == false){
            System.out.print("ID : ");
            inputID = scan.nextLine();
            
            try{
                ResultSet rs = this.select(conn, "SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '"+inputID+"'");
                rs.next();
                String tempuserPW = rs.getString(1);
                System.out.println("You cannot use this ID.");
                System.out.println("Please input another ID.");
               
            } catch (Exception e) {
                System.out.println("You can use this ID.");
                nobodyUseThisID = true;
            }
        }
        
        System.out.print("Name : ");
        inputName = scan.nextLine();
        System.out.print("PW : ");
        inputPW = scan.nextLine();
        System.out.print("Phone number : ");
        inputPhoneNumber = scan.nextLine();
        System.out.print("Address : ");
        inputAddress = scan.nextLine();
        System.out.print("Birth(e.g. 93/07/12) : ");
        inputBirth = scan.nextLine();
        
        return this.insert(conn, "INSERT INTO CUSTOMER VALUES('"+inputID+"','"+inputName+"','"+inputPW+"','"+inputPhoneNumber
                +"','"+inputAddress+"','"+inputBirth+"',"+0);
        
    }
    
    public void bookingMovie(Connection conn){
        
    }
    
    public void SearchMovie(Connection conn){
        
    }
    
    public void modifyMyInformation(Connection conn){
        try{
            ResultSet rs = this.select(conn, "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = '"+inputedUserID+"'");
            int indexOfRs=0;
            if(rs.getString(indexOfRs) != null){
                
            }
           
        } catch (Exception e) {
            System.out.println("You can use this ID.");
            nobodyUseThisID = true;
        }
    }
    
    public void secession(Connection conn){
        int inputForsecession;
        System.out.print("Are you sure to remove your account? (1: Yes, 2: No!) : ");
        inputForsecession = scan.nextInt();
        switch(inputForsecession){
            case 1:
                this.delete(conn, "DELETE FROM Customer WHERE CUSTOMER_ID = '"+inputedUserID+"'");
                break;
            case 2:
                break;
            default:
                System.out.println("Wrong input!!!");
                break;
        }
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

}


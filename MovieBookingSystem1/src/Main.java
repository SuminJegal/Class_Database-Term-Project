import java.util.Scanner;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    
    private static Connection conn01 = null;          // 커넥션 정보 저장 객체
    private static DatabaseMetaData meta01 = null;  // 데이터베이스 메타정보 저장 객체
    private static Connection conn02 = null;
    private static DatabaseMetaData meta02 = null;
    
    public static void main(String[] args){

        int input = 0;
        Customer customer = new Customer();
        Manager manager = new Manager();
        customer.createConn();           // 데이터베이스와 연결 수립
        conn01 = customer.getConn();       // 데이터베이스 연결 정보 취득
        meta01 = customer.getDBMD(conn01);
        manager.createConn();
        conn02 = manager.getConn();
        meta02 = manager.getDBMD(conn02);
        
        try {
            System.out.println(meta01.getTimeDateFunctions());
            System.out.println(meta01.getUserName());
        } catch (Exception e) {
            System.out.println("[*] customer 메타정보 출력 오류 발생: \n" + e.getMessage());
        }
        
        try {
            System.out.println(meta02.getTimeDateFunctions());
            System.out.println(meta02.getUserName());
        } catch (Exception e) {
            System.out.println("[*] manager 메타정보 출력 오류 발생: \n" + e.getMessage());
        }

        while (input!= 3){
            System.out.print("Are you manager or customer? (1: manager, 2: customer 3: exit) : ");
            Scanner scan = new Scanner(System.in);
            input = scan.nextInt();
            switch (input){
                case 1:
                    managerScenario(manager,conn02);
                    break;
                case 2:
                    customerScenario(customer, conn01);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Wrong input!!!");
                    break;
            }
        }
        
        try {
            conn01.close();   // 커넥션 닫기
            System.out.println("[*] customer 데이터베이스 접속 종료.");
        } catch (SQLException e) {
            System.out.println("[*] customer 오류 발생: \n" + e.getMessage());
        }
        
        try {
            conn02.close();   // 커넥션 닫기
            System.out.println("[*] manager 데이터베이스 접속 종료.");
        } catch (SQLException e) {
            System.out.println("[*] manager 오류 발생: \n" + e.getMessage());
        }

    }

    private static void managerScenario(Manager manager, Connection conn){
        int inputInManager = 0;
        Scanner scanner = new Scanner(System.in);
        while (inputInManager != 7){
            System.out.print("What do you want to do? (1: manage movies, 2: manage cinema, 3: manage Screening," +
                    "4: manage VIP, 5: ticketing, 6: payment, 7: exit) : ");
            inputInManager = scanner.nextInt();
            switch (inputInManager) {
                case 1:
                    manager.manageMovie(conn);
                    break;
                case 2:
                    manager.manageCinema(conn);
                    break;
                case 3:
                    manager.manageScreening(conn);
                    break;
                case 4:
                    manager.manageVIP(conn);
                    break;
                case 5:
                    manager.ticketing(conn);
                    break;
                case 6:
                    manager.helpPayment(conn);
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Wrong input!!!");
                    break;
            }

        }
    }

    private static void customerScenario(Customer customer, Connection conn){
        int inputInCustomer = 0;
        Scanner scanner = new Scanner(System.in);
        while(inputInCustomer !=3){
            System.out.print("What do you want to do? (1: log-in, 2: sign up, 3: exit) : ");
            inputInCustomer = scanner.nextInt();
            switch (inputInCustomer){
                case 1:
                    customer.login(conn);
                    int inputInLogIn = 0;
                    while(inputInLogIn!=4 && inputInLogIn!=5){
                        System.out.print("What do you want to do? (1: booking a movie, 2: search movies, " +
                                "3: modify your information, 4: secession, 5:log-out : ");
                        inputInLogIn = scanner.nextInt();
                        switch (inputInLogIn){
                            case 1:
                                customer.bookingMovie(conn);
                                break;
                            case 2:
                                customer.SearchMovie(conn);
                                break;
                            case 3:
                                customer.modifyMyInformation(conn);
                                break;
                            case 4:
                                customer.secession(conn);
                                break;
                            case 5:
                                System.out.println("Complete log out");
                                break;
                            default:
                                System.out.println("Wrong input!!!");
                                break;
                        }
                    }
                    break;
                case 2:
                    customer.signUp(conn);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Wrong input!!!");
                    break;
            }
        }
    }

}

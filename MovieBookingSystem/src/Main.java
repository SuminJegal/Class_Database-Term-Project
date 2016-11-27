import java.util.Scanner;

public class Main {
    
    public static void main(String[] args){

        int input = 0;

        while (input!= 3){
            System.out.print("Are you manager or customer? (1: manager, 2: customer 3: exit) : ");
            Scanner scan = new Scanner(System.in);
            input = scan.nextInt();
            switch (input){
                case 1:
                    managerScenario();
                    break;
                case 2:
                    customerScenario();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Wrong input!!!");
                    break;
            }
        }



    }

    private static void managerScenario(){
        int inputInManager = 0;
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);
        while (inputInManager != 7){
            System.out.print("What do you want to do? (1: manage movies, 2: manage cinema, 3: manage Screening," +
                    "4: manage VIP, 5: ticketing, 6: payment, 7: exit) : ");
            inputInManager = scanner.nextInt();
            switch (inputInManager) {
                case 1:
                    manager.manageMovie();
                    break;
                case 2:
                    manager.manageCinema();
                    break;
                case 3:
                    manager.manageScreening();
                    break;
                case 4:
                    manager.manageVIP();
                    break;
                case 5:
                    manager.ticketing();
                    break;
                case 6:
                    manager.helpPayment();
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Wrong input!!!");
                    break;
            }

        }
    }

    private static void customerScenario(){
        int inputInCustomer = 0;
        Customer customer = new Customer();
        Scanner scanner = new Scanner(System.in);
        while(inputInCustomer !=3){
            System.out.print("What do you want to do? (1: log-in, 2: sign up, 3: exit) : ");
            inputInCustomer = scanner.nextInt();
            switch (inputInCustomer){
                case 1:
                    customer.login();
                    int inputInLogIn = 0;
                    while(inputInLogIn!=4 && inputInLogIn!=5){
                        System.out.print("What do you want to do? (1: booking a movie, 2: search movies, " +
                                "3: modify your information, 4: secession, 5:log-out : ");
                        inputInLogIn = scanner.nextInt();
                        switch (inputInLogIn){
                            case 1:
                                customer.bookingMovie();
                                break;
                            case 2:
                                customer.SearchMovie();
                                break;
                            case 3:
                                customer.modifyMyInformation();
                                break;
                            case 4:
                                customer.secession();
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
                    customer.signUp();
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

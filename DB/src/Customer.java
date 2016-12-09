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

	String inputtedUserID;
	int reservationNumber = 0, ticketNumber = 0;
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:DBSERVER";
	private static final String USER = "HEEBIN";
	private static final String PASS = "heebin";

	private static Connection conn = null;
	private static DatabaseMetaData meta = null;

	Scanner scan;

	public Customer() {
		scan = new Scanner(System.in);
	}

	public boolean createConn() {
		try {
			Class.forName(DRIVER);
			System.out.println("[*] JDBC 드라이버 로드 완료.");
			conn = DriverManager.getConnection(URL, USER, PASS);
			System.out.println("[*] 데이터베이스 접속 완료.");
		} catch (Exception e) {
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

	public boolean login(Connection conn) {
		String userPW = "";
		String inputPW;

		System.out.print("What is your ID? : ");
		inputtedUserID = scan.nextLine();
		System.out.print("What is your pw? : ");
		inputPW = scan.nextLine();

		try {
			ResultSet rs = this.select(conn,
					"SELECT CUSTOMER_PW FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			rs.next();
			userPW = rs.getString(1);
			if (inputPW.equals(userPW)) {
				System.out.println("Log-in success!");
				return true;
			} else {
				System.out.println("Fail to Log-in");
				return false;
			}
		} catch (Exception e) {
			System.out.println("There is not your ID");
			// System.out.println("[*] 질의 결과 출력 오류 발생: \n" + e.getMessage());
			return false;
		}

	}

	public boolean signUp(Connection conn) {
		String inputID = "";
		String inputName, inputPW, inputPhoneNumber, inputAddress, inputBirth;

		boolean nobodyUseThisID = false;

		while (nobodyUseThisID == false) {
			System.out.print("ID : ");
			inputID = scan.nextLine();

			try {
				ResultSet rs = this.select(conn,
						"SELECT CUSTOMER_ID FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputID + "'");
				rs.next();
				rs.getString(1);
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

		return this.insert(conn, "INSERT INTO CUSTOMER VALUES('" + inputID + "','" + inputName + "','" + inputPW + "','"
				+ inputPhoneNumber + "','" + inputAddress + "','" + inputBirth + "',0,0)");

	}

	public void bookingMovie(Connection conn) {
		String movieId, movieName, screenNum = "";
		String inputCinemaName, inputMovieName, inputSeatNum;
		int reservationSeatCount = 0, remainSeat, screenTotalSeatNumber, reservationNumber = 0;
		String inputScreeningNumber;
		try {
			ResultSet rs = this.select(conn, "SELECT CINEMA_NAME FROM CINEMA");
			System.out.println("=================================================");
			while (rs.next()) {
				System.out.println("영화관 이름 : " + rs.getString(1));
			}

		} catch (Exception e) {
			System.out.println("영화관이 존재하지 않습니다.");
		}
		System.out.println("----------------------------------------------------");
		System.out.print("영화관 선택 : ");
		inputCinemaName = scan.nextLine();

		try {// 영화관을 선택하면 그 영화관에서 상영중인 영화를 보여준다.
			ResultSet rs = this.select(conn, "SELECT MOVIE_ID, MOVIE_NAME FROM MOVIE WHERE MOVIE_ID IN ("
					+ "SELECT MOVIE_ID FROM SCREENING WHERE CINEMA_NAME = '" + inputCinemaName + "')");
			System.out.println("----------------------------------------------------");
			while (rs.next()) {
				movieId = rs.getString(1);
				movieName = rs.getString(2);
				System.out.println("MOVIE_ID : " + movieId + "\t MOVIE_NAME : " + movieName);
			}
		} catch (Exception e) {
			System.out.println("그 영화관에는 영화가 상영하지 않습니다.");
		}
		System.out.println("----------------------------------------------------");
		// 영화관에서 상영중인 영화를 선택하면 영화 상영일정들을 보여준다.
		System.out.print("영화 선택 : ");
		inputMovieName = scan.nextLine();
		try {
			ResultSet rs = this.select(conn,
					"SELECT SCREENING_TIME, MOVIE_ID, SCREENING_NUMBER, SCREEN_NUMBER FROM SCREENING WHERE CINEMA_NAME = '"+inputCinemaName+"' AND MOVIE_ID = ("
							+ "SELECT MOVIE_ID FROM MOVIE WHERE MOVIE_NAME = '" + inputMovieName + "')");
			System.out.println("----------------------------------------------------");
			System.out.println("선택한 영화 : " + inputMovieName);
			rs.next();
			do {
				screenNum = rs.getString(4);
				System.out.println("상영번호 : " + rs.getString(3) + "\t상영일정 : " + rs.getString(1));
			} while (rs.next());
		} catch (Exception e) {
			System.out.println("선택한 영화가 잘 못 됐습니다.");
		}
		System.out.println("----------------------------------------------------");

		System.out.print("상영번호 선택 : ");
		inputScreeningNumber = scan.nextLine();

		// 예매할 매수를 선택한다.
		try {
			ResultSet screen = this.select(conn, "SELECT SCREEN_TOTAL_SEAT_NUMBER FROM SCREEN WHERE SCREEN_NUMBER = "
					+ screenNum + " AND CINEMA_NAME = '" + inputCinemaName + "'");
			screen.next();
			screenTotalSeatNumber = Integer.parseInt(screen.getString(1));
			ResultSet rs = this.select(conn,
					"SELECT REMAIN_SEAT_NUMBER FROM SCREENING WHERE SCREENING_NUMBER = " + inputScreeningNumber);
			rs.next();
			remainSeat = Integer.parseInt(rs.getString(1));
			System.out.print("예매할 매수 선택(남은 좌석 수 : " + remainSeat + ") : ");
			reservationSeatCount = scan.nextInt();
			scan.nextLine();
			int temp = reservationSeatCount;
			// 예매한 매수만큼 좌석을 선택한다.예매와 좌석까지 완료
			System.out.println("----------------------------------------------------");
			if (reservationSeatCount <= remainSeat) {
				int[] checkRemainSeat = new int[100];
				for (int i = 0; i < reservationSeatCount;) {
					System.out.println("좌석선택<1~" + screenTotalSeatNumber + "> ");
					try {
						ResultSet rt = this.select(conn,
								"SELECT SEAT_NUMBER FROM TICKET WHERE SCREENING_NUMBER = " + inputScreeningNumber);
						rt.next();
						do {
							checkRemainSeat[Integer.parseInt(rt.getString(1))] = 1;
						} while (rt.next());
						System.out.print("<예매된 좌석(");
						for (int j = 0; j < checkRemainSeat.length; j++) {
							if (checkRemainSeat[j] == 1)
								System.out.print(j + " ");
						}
						System.out.print("), 남은 예매 수(" + (reservationSeatCount - i) + ")> : ");
					} catch (Exception e) {
						System.out.print("<남은 예매 수(" + (reservationSeatCount - i) + ")> : ");
					}
					inputSeatNum = scan.nextLine();
					try {
						try {
							if (reservationNumber == 0) {
								insert(conn, "INSERT INTO RESERVATION VALUES (RESERVATENUM.NEXTVAL,"
										+ inputScreeningNumber + ",'" + inputtedUserID + "')");
								ResultSet ru = this.select(conn, "SELECT RESERVATENUM.CURRVAL FROM DUAL");
								ru.next();
								reservationNumber = Integer.parseInt(ru.getString(1));
							}
						} catch (Exception e) {
							System.out.println("예약테이블시퀀스 오류");
						}
						insert(conn,
								"INSERT INTO TICKET VALUES (TICKETNUM.NEXTVAL," + inputSeatNum + "," + reservationNumber
										+ ",'" + inputCinemaName + "'," + screenNum + "," + inputScreeningNumber + ")");
						update(conn,
								"UPDATE CUSTOMER SET CUSTOMER_TIKETING_NUMBER = CUSTOMER_TIKETING_NUMBER + 1 WHERE CUSTOMER_ID = '"
										+ inputtedUserID + "'");
						update(conn,
								"UPDATE SCREENING SET REMAIN_SEAT_NUMBER = REMAIN_SEAT_NUMBER - 1 WHERE SCREENING_NUMBER = "
										+ inputScreeningNumber);
						i++;
					} catch (Exception e) {
						System.out.println("티켓테이블 오류");
					}
				}
				this.doPayment(conn, reservationSeatCount, reservationNumber);
			} else {
				System.out.println("남은 좌석이 부족합니다.");
			}
		} catch (Exception e) {
			System.out.println("그 일정에는 영화 상영이 없습니다.");
		}
		System.out.println("=================================================");
	}

	private void doPayment(Connection conn, int numOfTicket, int reservationNum) {
		int inputMethodToPayment = 0;
		int point;
		int usedPoint = 0;
		int totalCost = 10000 * numOfTicket;
		while (inputMethodToPayment != 1 && inputMethodToPayment != 2) {
			System.out.print("결제하는 방법은? (1: 현장 결제, 2: 인터넷 결제) : ");
			inputMethodToPayment = scan.nextInt();
			scan.nextLine();
			switch (inputMethodToPayment) {
			case 1:
				break;
			case 2:
				try {
					ResultSet rs = this.select(conn,
							"SELECT CUSTOMER_POINT FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
					rs.next();
					point = Integer.parseInt(rs.getString(1));
				} catch (Exception e) {
					System.out.println("고객 테이블 에러");
					break;
				}
				System.out.println("인터넷 결제를 시작합니다.");
				System.out.println("당신의 총 결제 금액은 " + totalCost + "입니다.");
				System.out.println("당신의 포인트는 " + point + "p 있습니다.");
				if (point >= 1000) {
					int tempInput = 0;
					while (tempInput != 1 && tempInput != 2) {
						System.out.print("포인트를 사용하시겠습니까? (1:Yse, 2:No) : ");
						tempInput = scan.nextInt();
						scan.nextLine();
						switch (tempInput) {
						case 1:
							boolean tempUseCheck = false;
							while (tempUseCheck == false) {
								System.out.print("얼마의 포인트를 사용하시겠습니까? : ");
								usedPoint = scan.nextInt();
								scan.nextLine();
								if (usedPoint <= point) {
									tempUseCheck = true;
								} else {
									System.out.println("사용할 수 있는 포인트를 초과했습니다. 다시 입력하세요");
								}
							}
							break;
						case 2:
							break;
						default:
							System.out.println("Wrong input. Please input again.");
							break;
						}
					}
				} else {
					System.out.println("포인트 부족으로 결제에는 사용하실 수 없습니다. ");
				}
				int cash = totalCost - usedPoint;
				this.insert(conn, "INSERT INTO PAYMENT VALUES( PAYMENTNUM.NEXTVAL," + usedPoint + "," + cash + ",'"
						+ inputtedUserID + "'," + reservationNum + ",0)");
				int addPoint = numOfTicket * 100;
				this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_POINT = CUSTOMER_POINT+" + addPoint
						+ "WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				System.out.println("결제에 성공했습니다!");
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}
	}

	public void SearchMovie(Connection conn) {
		try {
			ResultSet rs = this.select(conn,
					"SELECT MOVIE_NAME, RESERVATION_RATE FROM MOVIE ORDER BY RESERVATION_RATE DESC");
			System.out.println("=================================================");
			System.out.printf("%30s		|%10s\n", "영화 제목", "예매율");
			System.out.println("-------------------------------------------------");
			rs.next();
			do {
				System.out.printf("%30s		|%10s%s\n", rs.getString(1), rs.getString(2), "%");
			} while (rs.next());
			System.out.println("=================================================");
			System.out.println();
		} catch (Exception e) {
			System.out.println("There is no movie.");
		}
	}

	public void modifyMyInformation(Connection conn) {
		try {
			int inputForModify = 0;

			System.out.println(inputtedUserID);
			ResultSet rs = this.select(conn, "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
			rs.next();
			System.out.println("======================================================");
			System.out.println("This is your information");
			System.out.println("------------------------------------------------------");
			System.out.println("ID : " + rs.getString(1));
			System.out.println("Name : " + rs.getString(2));
			System.out.println("Password : " + rs.getString(3));
			System.out.println("Phone number : " + rs.getString(4));
			System.out.println("Adderss : " + rs.getString(5));
			System.out.println("Birth : " + rs.getString(6));
			System.out.println("Point : " + rs.getString(7));
			System.out.println("======================================================");

			while (inputForModify != 6) {
				System.out.print("What do you want to change? (1: name, 2: password, 3: phone number,"
						+ " 4: address, 5: birth, 6: exit)");
				inputForModify = scan.nextInt();
				scan.nextLine();
				String userInput = "";
				if (inputForModify != 5 && inputForModify != 6) {
					System.out.print("How do you want to change? : ");
					userInput = scan.nextLine();
				}
				switch (inputForModify) {
				case 1:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_NAME = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 2:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_PW = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 3:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_PHONENUMBER = '" + userInput
							+ "' WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
					break;
				case 4:
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_ADDRESS = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 5:
					System.out.print("How do you want to change?(e.g. 93/07/12) : ");
					userInput = scan.nextLine();
					this.update(conn, "UPDATE CUSTOMER SET CUSTOMER_BRITH = '" + userInput + "' WHERE CUSTOMER_ID = '"
							+ inputtedUserID + "'");
					break;
				case 6:
					break;
				default:
					System.out.println("Wrong input!!!");
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("Wrong ID.");
		}
	}

	public void checkMyReservation(Connection conn) {
		int indexForReservation = 0;
		try {
			ResultSet rs = this.select(conn,
					"SELECT MOVIE_ID, RESERVATION_NUMBER, CINEMA_NAME, SCREENING_TIME, SCREEN_NUMBER FROM RESERVATION, SCREENING WHERE RESERVATION.CUSTOMER_ID = '"
							+ inputtedUserID + "' AND SCREENING.SCREENING_NUMBER = RESERVATION.SCREENING_NUMBER");
			System.out.println(
					"==================================================================================================");
			System.out.printf("%10s      %20s       %10s        %15s        %5s\n", "예약번호", "영화 제목", "영화관", "상영 시간",
					"상영관");
			System.out.println(
					"--------------------------------------------------------------------------------------------------");
			rs.next();
			do {
				ResultSet rt = this.select(conn,
						"SELECT MOVIE_NAME FROM MOVIE WHERE MOVIE_ID = '" + rs.getString(1) + "'");
				rt.next();
				System.out.printf("%10s      %20s       %10s        %15s        %5s\n", rs.getString(2),
						rt.getString(1), rs.getString(3), rs.getString(4), rs.getString(5));
			}while (rs.next());
			System.out.println(
					"==================================================================================================");
			System.out.println();
		} catch (Exception e) {
			System.out.println("There is no reservation.");
		}
		while (indexForReservation != 3) {
			System.out.println(
					"What do you want to do? (1: show tickets of reservation, 2: delete reservation, 3: exit) : ");
			indexForReservation = scan.nextInt();
			scan.nextLine();
			String inputReservationNumber;
			switch (indexForReservation) {
			case 1:
				System.out.println("What reservation do you want to see more? (Input the reservation number) : ");
				inputReservationNumber = scan.nextLine();
				try {
					ResultSet rs = this.select(conn,
							"SELECT * FROM TICKET WHERE RESERVATION_NUMBER = " + inputReservationNumber);
					System.out.println(
							"==================================================================================================");
					System.out.printf("%10s      %10s       %10S        %10S        %5S\n", "티켓번호", "좌석번호", "예약번호",
							"영화관", "상영관");
					System.out.println(
							"--------------------------------------------------------------------------------------------------");
					while (rs.next()) {
						System.out.printf("%10s      %10s       %10S        %10S        %5S\n", rs.getString(1),
								rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
					}
					System.out.println(
							"==================================================================================================");
					System.out.println();
				} catch (Exception e) {
					System.out.println("You input wrong number");
				}
				break;
			case 2:
				String screeningNumber = "";
				System.out.println("What reservation do you want to delete? (Input the reservation number) : ");
				inputReservationNumber = scan.nextLine();
				try {
					ResultSet rs = this.select(conn, "SELECT SCREENING_NUMBER FROM TICKET WHERE RESERVATION_NUMBER = "
							+ Integer.parseInt(inputReservationNumber));
					int ticketCount = 0;
					while (rs.next()) {
						ticketCount++;
						screeningNumber = rs.getString(1);
					}
					delete(conn, "DELETE FROM RESERVATION WHERE RESERVATION_NUMBER = "
							+ Integer.parseInt(inputReservationNumber));
					update(conn, "UPDATE SCREENING SET REMAIN_SEAT_NUMBER = REMAIN_SEAT_NUMBER +" + ticketCount
							+ " WHERE SCREENING_NUMBER = '" + screeningNumber + "'");
					update(conn, "UPDATE CUSTOMER SET CUSTOMER_POINT = CUSTOMER_POINT -" + (ticketCount * 100)
							+ " WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
				} catch (Exception e) {
					System.out.println("You input wrong number");
				}
				break;
			case 3:
				break;
			default:
				System.out.println("Wrong input!!!");
				break;
			}
		}
	}

	public void secession(Connection conn) {
		int inputForsecession;
		System.out.print("Are you sure to remove your account? (1: Yes, 2: No!) : ");
		inputForsecession = scan.nextInt();
		scan.nextLine();
		switch (inputForsecession) {
		case 1:
			this.delete(conn, "DELETE FROM Customer WHERE CUSTOMER_ID = '" + inputtedUserID + "'");
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
			if (rowCount == 0) {
				System.out.println("데이터 삽입 실패");
				return false;
			} else {
				System.out.println("데이터 삽입 성공");
			}
		} catch (Exception e) {
			System.out.println("[*] INSERT 오류 발생: \n" + e.getMessage());
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

			// 질의 결과 메타 정보 추출
			for (int col = 1; col <= rsMeta.getColumnCount(); col++) {
				int type = rsMeta.getColumnType(col);
				String typeName = rsMeta.getColumnTypeName(col);
				String name = rsMeta.getColumnName(col);
				// System.out.println(col + "st column " + name + " is JDBC type
				// " + type + " which is called " + typeName);
			}

			// 질의 결과 반환
			return rs;
		} catch (Exception e) {
			System.out.println("[*] SELECT 오류 발생: \n" + e.getMessage());
		}

		return rs;
	}

	public boolean update(Connection conn, String query) {
		try {
			Statement pstmt = conn.createStatement();
			int rowCount = pstmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("데이터 수정 실패");
			} else {
				System.out.println("데이터 수정 성공");
			}
		} catch (Exception e) {
			System.out.println("[*] UPDATE 오류 발생: \n" + e.getMessage());
		}

		return true;
	}

	public boolean delete(Connection conn, String query) {
		try {
			Statement stmt = conn.createStatement();
			int rowCount = stmt.executeUpdate(query);
			if (rowCount == 0) {
				System.out.println("데이터 삭제 실패");
				return false;
			} else {
				System.out.println("데이터 삭제 성공");
			}
		} catch (Exception e) {
			System.out.println("[*] DELETE 오류 발생: \n" + e.getMessage());
		}

		return true;
	}
}
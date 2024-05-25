import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class User {
	// 멤버 변수
	private String userID;	// 사용자 아이디
	private String userPW;	// 사용자 비밀번호
	private String userName;	// 사용자 이름
	private int userAccount;	// 사용자 잔액
	private ArrayList<Bus> busArrayList;	// 사용자 운행 상품
	private ArrayList<Integer> userSeatArrayList;	// 사용자 좌석



	// 생성자
	public User(String userID, String userPW, String userName, int userAccount, ArrayList<Bus> busArrayList, ArrayList<Integer> userSeatArrayList) {
		this.userID = userID.strip();
		this.userPW = userPW.strip();
		this.userName = userName.strip();
		this.userAccount = userAccount;
		this.busArrayList = busArrayList;
		this.userSeatArrayList = userSeatArrayList;
	}

	public User() {

	}

	// Getter, Setter

	public String getUserID() {return userID;}

	public String getUserPW() {return userPW;}

	public String getUserName() {return userName;}

	public int getUserAccount() {return userAccount;}

	public ArrayList<Bus> getBusArrayList() {return busArrayList;}

	public ArrayList<Integer> getUserSeatArrayList() {return userSeatArrayList;}

	public void removeBus(int busNo) {
		busArrayList.remove(busNo-1);
		userSeatArrayList.remove(busNo-1);
	}
	public void setUserAccount(int userAccount) {
		this.userAccount = userAccount;
	}


	// 메소드
	public User loadUserFromFile(String fileName) {
		//try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
			String line = br.readLine();
			String[] userInfo = line.split(" ");
			String userID = userInfo[0]; // 사용자 아이디
			String userPW = userInfo[1]; // 사용자 비밀번호
			String userName = userInfo[2]; // 사용자 이름
			line = br.readLine();
			userInfo = line.split(" ");
			int userAccount = Integer.parseInt(userInfo[0]); // 사용자 잔액

			ArrayList<Bus> busArrayList = new ArrayList<>();
			ArrayList<Integer> userSeatArrayList = new ArrayList<>();

			// 사용자 운행 상품을 읽어와서 ArrayList에 추가
			while ((line = br.readLine()) != null) {
				String[] busInfo = line.split("/");
				// ex) 2024-03-25 / 서울 / 대구 / 출발시간 : 11:00 / 보유좌석 : 13번 / 가격 : 5000원 / 버스 등급 : 일반
				if (busInfo.length == 7) {
					String date = busInfo[0].trim();
					String departure = busInfo[1].trim();
					String arrival = busInfo[2].trim();
					String time = busInfo[3].trim();
					String userSeatStr = busInfo[4].trim();
					String priceStr = busInfo[5].trim();
					String sGrade = busInfo[6].trim();
					time = time.replace("출발시간 : ", "");
					time = time.replace(":","∶");
					userSeatStr = userSeatStr.replace("보유좌석 : ", "");
					userSeatStr = userSeatStr.replace("번", "");
					int userSeat = Integer.parseInt(userSeatStr);
					userSeatArrayList.add(userSeat);
					int price = Integer.parseInt(priceStr.replace("가격 : ", "").replace("원", ""));
					sGrade = sGrade.replace("버스 등급 : ", "");
					//System.out.println("테스트 : " + sGrade + "\n");
					String seats;
					//BufferedReader busReader = new BufferedReader((new FileReader("bus/" + departure + " " + arrival + " " + date + " " + time + ".txt")));
					BufferedReader busReader = new BufferedReader(new InputStreamReader(new FileInputStream("bus/" + departure + " " + arrival + " " + date + " " + time + " " + sGrade + ".txt"), StandardCharsets.UTF_8));
					seats = busReader.readLine().split(",")[1];

					Grade grade = null;
					try {
						grade = Grade.fromString(sGrade);
					} catch (IllegalArgumentException e) {
						System.out.println("타입 변경에 실패하였습니다.");
					}
					busArrayList.add(new Bus(departure, arrival, date, time, price, seats, grade));
					//busArrayList.add(new Bus(departure, arrival, date, time, price, seats));
				} else {
					System.err.println("올바르지 않은 형식의 데이터입니다: " + line);
				}
			}
			return new User(userID, userPW, userName, userAccount, busArrayList,userSeatArrayList);
		} catch (IOException e) {
			System.err.println("파일 읽기 오류: " + e.getMessage());
			return null;
		}
	}

	@Override
	public String toString() {
		return "User{" +
				"userID='" + userID + '\'' +
				", userPW='" + userPW + '\'' +
				", userName='" + userName + '\'' +
				", userAccount=" + userAccount +
				", busArrayList=" + busArrayList +
				", userSeatArrayList=" + userSeatArrayList +
				'}';
	}
}
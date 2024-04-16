import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Admin {
	// 멤버 변수
	private final LocalDate programDate = LocalDate.of(2024,1,1);
	private ArrayList<String> cityDic;	// 지역 사전 리스트

	public static Scanner scan = new Scanner(System.in);

	// 생성자
	public Admin() {
		this.cityDic = new ArrayList<>();
		readCityDic();
	}

	// 메소드
	// 기능 실행 메소드
	public void initAdmin() {
		int selected = 0;	// 입력 메뉴

		while (true) {	// 사용자 메뉴 입력 확인
			try {
				System.out.println("-".repeat(50));
				System.out.println("1. 상품 등록");
				System.out.println("2. 상품 조회");
				System.out.println("3. 로그아웃");
				System.out.print("메뉴를 입력하세요: ");
				selected = scan.nextInt();

				if(selected >= 1 && selected <= 3) {
					break;
				} else {
					System.out.println("1 ~ 3 사이의 숫자 중 하나만 입력해주세요.");
				}
			} catch (InputMismatchException e) {
				Error error = new Error();
				error.printRegularExpressionError();
				scan.next(); // 잘못된 입력을 지우기
			}
		}

		switch (selected) {
			case 1:
				registerBus();
				break;
			case 2:
				findBus();
				break;
			default:
//				logout();
				break;
		}
	}

	// 텍스트 파일을 읽어 와 지역 사전 리스트 초기화 하는 메소드
	private void readCityDic() {
		try {
			Scanner file = new Scanner(new File("cityDictionary.txt"));
			while(file.hasNext()) {
				String word = file.next();
				this.cityDic.add(word);
			}
			file.close();
		} catch (FileNotFoundException e) {
			System.err.println("파일을 찾을 수 없습니다.");
		}
	}

	// 1. 상품 등록 관련 메소드
	private void registerBus() {
		String departure;	// 출발지
		String arrival;	// 도착지
		String departureDayStr;	// 출발일
		String departureTimeStr;	// 출발시각
		String sPrice;	// 가격문자열
		int price;	// 정수 가격


		System.out.println("-".repeat(50));
		System.out.println("출발지와 도착지로 가능한 지역은 " + printCityDic() + "입니다.");
		scan.nextLine();
		// 출발지와 도착지 입력
		boolean cityFlag = false;	// 지역사전 플래그
		do {
			System.out.print("출발지를 입력하세요: ");
			departure = scan.nextLine();
			cityFlag = checkCityDic(departure);
		} while(!cityFlag);
		cityFlag = false;
		do {
			System.out.print("도착지를 입력하세요: ");
			arrival = scan.nextLine();
			cityFlag = checkCityDic(arrival);
		} while(!cityFlag);
		// 출발일 입력 받기
		boolean departureReFlag = false;	// 출발일 정규표현식 플래그
		boolean departureFlag = false;	// 출발일 의미규칙 플래그
		boolean departureWithinFlag = false;	// 1년 이내 검사 플래그
		do {
			System.out.print("출발일을 입력하세요(yyyy-mm-dd): ");
			departureDayStr = scan.nextLine();
			departureReFlag = checkDepartureDayRe(departureDayStr.trim());
			if(departureReFlag) {
				departureFlag = checkCalender(departureDayStr);
				departureWithinFlag = checkDepartureDay(departureDayStr);
			}
		}while(!departureReFlag || !departureFlag || !departureWithinFlag);
		// 출발시각 입력 받기
		boolean departureTimeFlag = false;
		do {
			System.out.print("출발시각을 입력하세요(hh:mm): ");
			departureTimeStr = scan.nextLine();
			departureTimeFlag = checkDepartureTime(departureTimeStr);
		} while (!departureTimeFlag);
		// 가격 입력 받기
		boolean priceFlag = false;
		do {
			System.out.print("가격을 입력하세요: ");
			sPrice = scan.nextLine();
			priceFlag = checkPrice(sPrice);
		} while (!priceFlag);
		price = Integer.parseInt(sPrice);
		// 상품 중복 테스트 코드 작성하기
		LocalDate local = LocalDate.parse(departureDayStr);
		BusManager bm = new BusManager(departure, arrival, local);
		// 버스 중복 어떻게 판단함?
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 날짜 형식 지정
		LocalDate date = LocalDate.parse(departureDayStr, formatter);
		if(bm.hasDuplicates(new Bus(departure, arrival, date, departureTimeStr, price))) {
			System.out.println("중복으로 등록된 상품이 있습니다. 메인화면으로 돌아갑니다.");
			initAdmin();
		} else {
			bm.addBus(departure, arrival, departureDayStr, departureTimeStr, price);
		}
	}

	// 지역 사전 출력 메소드
	private String printCityDic() {
		String str = "";
		for(int i = 0; i<this.cityDic.size(); i++) {
			str += cityDic.get(i);
			str += " ";
		}
		return str;
	}

	// 출발지, 도착지 검사 메소드
	boolean checkCityDic(String city) {
		for(int i = 0; i<this.cityDic.size(); i++) {
			if(city.equals(cityDic.get(i)))
				return true;
		}
		System.out.println("지역 사전에 저장되어있지 않은 지역을 입력하였습니다. 다시 입력해주세요.");
		return false;
	}

	//출발일 검사 메소드
	boolean checkDepartureDayRe(String dayStr) {	// 정규 표현식 검사 메소드
		String re = "^(?:(?:20[0-2]\\d)-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01]))$";
		if(dayStr.matches(re))
			return true;
		System.out.println("올바르지 않은 입력형식입니다. 다시 입력해주세요.");
		return false;
	}

	boolean checkCalender(String dayStr) {	// 출발일 의미 규칙 검사 메소드
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			dateFormat.parse(dayStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateFormat.parse(dayStr));
			return true;
		} catch (Exception e) {
			System.out.println("달력에 존재하지 않는 날짜입니다. 다시 입력해주세요.");
			return false;
		}
	}

	// 1년 이내 검사 메소드
	boolean checkDepartureDay(String dayStr) {
		try {
			// 날짜 형식 지정
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			// 문자열을 LocalDate 객체로 파싱
			LocalDate inputDate = LocalDate.parse(dayStr, formatter);

			// 입력된 날짜가 programDate 이후이고, programDate로부터 1년 이내인지 확인
			if (!inputDate.isBefore(programDate) && !inputDate.isAfter(programDate.plusYears(1))) {
				return true;
			} else {
				System.out.println("입력한 날짜는 프로그램 날짜로부터 1년 이내가 아닙니다.");
				return false;
			}
		} catch (DateTimeParseException e) {
			System.out.println("올바르지 않은 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.");
			return false;
		}
	}

	// 출발시각 검사 메소드
	boolean checkDepartureTime(String timeStr) {
		String re = "^(?:[01]\\d|2[0-3]):[0-5]\\d$";
		if(timeStr.matches(re))
			return true;
		System.out.println("올바르지 않은 입력입니다. 다시 입력해주세요.");
		return false;
	}

	// 가격 검사 메소드
	boolean checkPrice(String price) {
		String re = "^(?:[1-9]\\d{2,4}|100000)$";
		if(price.matches(re))
			return true;
		System.out.println("올바르지 않은 입력형식입니다. 다시 입력해주세요.");
		return false;
	}

	// 2. 상품 조회 관련 메소드
	private void findBus() {
		String departure;	// 출발지
		String arrival;	// 도착지
		String departureDayStr;	// 출발일

		// 출발지와 도착지 입력
		System.out.println("-".repeat(50));
		System.out.println("출발지와 도착지로 가능한 지역은 " + printCityDic() + "입니다.");
		scan.nextLine();
		// 출발지와 도착지 입력
		boolean cityFlag = false;	// 지역사전 플래그
		do {
			System.out.print("출발지를 입력하세요: ");
			departure = scan.nextLine();
			cityFlag = checkCityDic(departure);
		} while(!cityFlag);
		cityFlag = false;
		do {
			System.out.print("도착지를 입력하세요: ");
			arrival = scan.nextLine();
			cityFlag = checkCityDic(arrival);
		} while(!cityFlag);
		// 출발일 입력 받기
		boolean departureReFlag = false;	// 출발일 정규표현식 플래그
		boolean departureFlag = false;	// 출발일 의미규칙 플래그
		boolean departureWithinFlag = false;	// 1년 이내 검사 플래그
		do {
			System.out.print("출발일을 입력하세요(yyyy-mm-dd): ");
			departureDayStr = scan.nextLine();
			departureReFlag = checkDepartureDayRe(departureDayStr.trim());
			if(departureReFlag) {
				departureFlag = checkCalender(departureDayStr);
				departureWithinFlag = checkDepartureDay(departureDayStr);
			}
		}while(!departureReFlag || !departureFlag || !departureWithinFlag);
		// 필터링 된 상품만 출력
		LocalDate local = LocalDate.parse(departureDayStr);
		BusManager bm = new BusManager(departure, arrival, local);
		if(bm.isBusListEmpty()) {
			System.out.println("조회된 운행 상품이 없습니다.");
			initAdmin();
		} else {
			bm.printBusList();
			initAdmin();
		}
	}

//	// 3. 로그아웃 관련 메소드
	private void logout() {
//		MainMenuManager m = new MainMenuManager();
//		m.showmenu();
	}


}
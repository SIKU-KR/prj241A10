import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserManager {
	public boolean logout_user = false;
	// 멤버 변수

	private final String directory = "user";    // 디렉토리 이름
	private ArrayList<User> userArrayList;    // 유저 목록
	final String red = "\u001B[31m" ;
	final String exit = "\u001B[0m" ;

	// 생성자
	public UserManager() {
		this.userArrayList = new ArrayList<>();
//				getUserFromFileSystem();
	}


	// 기능 실행 메소드
	public void initUserManager(User user) {
		Scanner scan = new Scanner(System.in);
		String menuStr = "\n" + user.getUserName() + "님, 안녕하세요!\n";
		menuStr += "보유 잔액: " + user.getUserAccount() + "원\n";
		menuStr += "-".repeat(20) + "\n";
		menuStr += "1. 운행 상품 조회 및 예매\n";
		menuStr += "2. 사용자 운행 상품 조회 및 취소\n";
		menuStr += "3. 잔액 충전\n";
		menuStr += "4. 로그아웃\n";
		System.out.println(menuStr);
		int menuNum = 0;
		boolean menuNumCheck = true;
		while (menuNumCheck) {
			try {
				System.out.print("원하시는 기능의 메뉴 번호를 입력하세요: ");
				String input = scan.nextLine();
				if (input.matches("0+[1-9]+") || input.matches("0+")) {
					throw new NumberFormatException(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
				}

				menuNum = Integer.parseInt(input);
				if (menuNum < 1 || menuNum > 4)
					throw new IllegalArgumentException();
			} catch (NumberFormatException e) {
				System.out.println(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
				continue;
			} catch (IllegalArgumentException e) {
				System.out.println(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
				continue;
			}

			menuNumCheck = false;
		}
		switch (menuNum) {
			case 1:
				searchAndBook(user);
				break;
			case 2:
				userSearchAndCancel(user);
				break;
			case 3:
				chargeMoney(user);
				break;
			case 4:
				logout();
				break;
		}
	}

	// 1.1 운행상품 검색
	private void searchAndBook(User user) {

		//지역사전 가져오기
		List<String> locationList = new ArrayList<>();
		locationFile(locationList);

		//등록된 운행 상품 조회
		System.out.println("운행 상품 조회 모드입니다.");
		System.out.print("출발지와 도착지로 가능한 지역은 [ ");
		for(String str : locationList){
			System.out.print(str+" ");
		}
		System.out.println("] 입니다.");
		System.out.println("--------------------------------------------------------------");

		Scanner sc = new Scanner(System.in);
		String departure;
		String destination;
		String date;

		while (true){
			try {
				System.out.print("원하시는 출발지를 입력해주세요: ");
				departure=sc.nextLine();
				System.out.print("원하시는 도착지를 입력해주세요: ");
				destination=sc.nextLine();
				compareLocations(departure,destination);
				findlocationInfile(departure,destination,locationList);


				System.out.print("원하시는 날짜를 입력해주세요(yyyy-mm-dd): ");
				date=sc.nextLine();
				if (!validateDate(date)){
					System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
					continue;
				}
				if(!checkCalender((date))){
					System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
					continue;
				}
				if(!isWithinOneYear(date).equals("")) {

					System.out.println(red+"날짜 범위를 벗어났습니다. 다시 입력해주세요. "+isWithinOneYear(date)+" 까지 입력가능합니다."+exit);
					//날짜 범위를 벗어났습니다. 다시 입력해주세요. 2024-01-01 까지 입력가능합니다.
					continue;
				}

				boolean fl = checkBusFile(departure,destination,date,locationList);
				if(fl){
					searchList(departure,destination,date,user);
					break;
				}
				else{

					initUserManager(user);
					break;
				}

			}catch (InputMismatchException e){
				System.out.println(e.getMessage());
			}
		}//while


	}//searchAndBook()

	//지역사전 파일 locationList에 넣어주기
	private void locationFile(List<String> locationList){
		String filePath = "cityDictionary.txt";
		File file = new File(filePath);

		//try(Scanner sr = new Scanner(file)) {
		try(Scanner sr = new Scanner(file, StandardCharsets.UTF_8)) {

			while (sr.hasNext()){
				String location = sr.next();
				if (!location.isEmpty()){
					locationList.add(location);

				}
			}
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	//출발지,도착지,날짜 입력 다 올바르게 받았는데 상품이 없는 경우
	private boolean checkBusFile(String departure, String destination, String date, List<String> loctionList){
		String dir = "bus";
		File filedir = new File(dir);
		File[] fileList = filedir.listFiles(((dir1, name) -> name.endsWith(".txt")));

		for(File file: fileList){
			String fileName = file.getName().replaceAll("\\.txt$", "");
			String[] listName = fileName.split(" ");
			String fileDeparture =  listName[0];
			String fileDestination =  listName[1];
			String fileDate = listName[2];

			if(fileDeparture.equals(departure) && fileDestination.equals(destination) && fileDate.equals(date)){
				return true;
			}

		}
		System.out.println(red+"등록된 운행 상품이 없습니다! 사용자 메뉴로 돌아갑니다!"+exit);
		return false;
	}

	//지역명이 사전에 존재하는지 검사
	private void findlocationInfile(String departure,String destination,List<String> locationList) throws InputMismatchException{
		boolean departureExists = locationList.contains(departure);
		boolean destinationExists = locationList.contains(destination);
		if(!destinationExists || !departureExists){
			throw new InputMismatchException(red+"지역 사전에 저장되어있지 않은 지역을 입력하였습니다. 다시 입력해주세요."+exit);
		}

	}

	//출발지,도착지 비교
	private void compareLocations(String departure,String destination) throws InputMismatchException{
		if(departure.equals(destination)){
			throw new InputMismatchException(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
		}
	}

	//날짜 형식 검사
	private boolean validateDate(String date){
		// 정규표현식 패턴
		String regexPattern = "^(?:(?:20[0-2]\\d)-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01]))$";
		Pattern pattern = Pattern.compile(regexPattern);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 입력된 날짜가 정규표현식을 따르는지 확인
		Matcher matcher = pattern.matcher(date);
		if (matcher.matches()){
			return true;
		}else {
			return false;
		}
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
			return false;
		}
	}

	String isWithinOneYear(String date) {

		LocalDate baseDate = LocalDate.of(mainMenuManager.year, mainMenuManager.month, mainMenuManager.day);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate inputDate = LocalDate.parse(date, formatter);

		LocalDate oneYearAfterBaseDate = baseDate.plusYears(1);

		if(!inputDate.isBefore(baseDate) && !inputDate.isAfter(oneYearAfterBaseDate))
			return "";
		else
			return oneYearAfterBaseDate.format(formatter);
	}

	//1.2 검색한 운생상품 조회
	private void searchList(String departure,String destination,String date,User user){
		List<String> matchFiles = new ArrayList<>();
		makesearchFile(departure,destination,date,matchFiles);

		System.out.println("[검색 결과]");
		visualizationList(matchFiles);
		Scanner sc = new Scanner(System.in);
		while (true){
			try {
				System.out.print("상세 조회할 상품 번호를 입력하세요 (사용자 메뉴로 돌아가려면 0을 입력하세요): ");
				String innum = sc.nextLine();
				if (innum.matches("0+[1-9]+")) {
					throw new NumberFormatException(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
				}

				int selection = Integer.parseInt(innum);
				if(selection==0){
					initUserManager(user);
					return;
				}
				if (selection >= 1 && selection <= matchFiles.size()) {
					// 선택된 상품에 대한 상세 정보 출력 또는 관련 처리
					detailTicket(user,matchFiles.get(selection-1),selection);
					break; // 루프 종료
				} else {
					System.out.println(red+"1 ~ " + matchFiles.size() + " 사이의 숫자 중 하나만 입력해주세요."+exit);
				}


			}catch (NumberFormatException e){
				//정수가 아닐때
				System.out.println(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
			}//try-catch
		}
	}

	//조건 검색 부합한 파일들 넣어주기
	private void makesearchFile(String departure,String destination, String date,List<String> matchFiles){
		matchFiles.clear();
		File dir = new File("bus");
		File[] file = dir.listFiles((d, name) -> name.endsWith(".txt") && name.startsWith(departure + " " + destination + " " + date));
		if(file!=null){
			for (File fl : file){
				matchFiles.add(fl.getName());
			}
		}
	}

	//등록된 상품 리스트 시각화
	private void visualizationList(List<String> matchFiles){
		for(int i=0; i<matchFiles.size(); i++){
			String filename = matchFiles.get(i).replace(".txt", "");
			String bus = filename.replace("∶",":");
			System.out.println((i+1)+" "+bus);
		}
	}


	//1.3선택된 티켓 상세조회, 좌석 선택하고, 예매하기

	//티켓 상세조회
	private void detailTicket(User user, String fileName,int selection){

		String[] titleName = fileName.split(" ");
		String departure = titleName[0];
		String destination = titleName[1];
		String date = titleName[2];

		Bus bus = loadBusFile(fileName);


		String detailInfo = bus.getDetailInfo();
		System.out.println(selection+"번 버스 상세 조회 입니다.");

		System.out.print(detailInfo);

		Scanner sc = new Scanner(System.in);
		while (true){
			System.out.print("예매하려면 y, 이전으로 돌아가려면 n을 입력해주세요: ");
			String res = sc.nextLine();
			if(res.equals("y")){
				bookTicket(user,fileName,selection,bus);
				break;
			}
			else if(res.equals("n")){
				searchList(departure,destination,date,user);
				break;
			}
			else {
				System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			}
		}

	}


	//사용자가 선택한 파일로부터 bus객체를 생성하는거 /User의 loadUserFromFile 로직 이용하기
	private Bus loadBusFile(String filename){
		File file = new File("bus/"+ filename);
		try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)){
			if(sc.hasNextLine()){
				//내용 가져오기
				String line = sc.nextLine();
				String[] temp = line.split(",");
				int price = Integer.parseInt(temp[0].trim());
				String seats = temp[1].trim();
				//파일명 가져오기
				String[] titleName = filename.split(" ");
				String departure = titleName[0];
				String destination = titleName[1];
				String date = titleName[2];
				String time = titleName[3];
				String sGrade = titleName[4].replace(".txt", "");


				Grade grade = null;
				try {
					grade = Grade.fromString(sGrade);
				} catch (IllegalArgumentException e) {
					System.out.println("타입 변경에 실패하였습니다.");
				}
				return new Bus(departure,destination,date,time,price,seats,grade);
				//return new Bus(departure,destination,date,time,price,seats);
			}
		}catch (FileNotFoundException e){
			System.err.println(e.getMessage());
		}catch (Exception e){
			System.err.println(e.getMessage());
		}
		return null;
	}


	//예매하기
	private void bookTicket(User user,String fileName,int selection ,Bus bus){
		System.out.println("좌석현황");
		String seatInfo = bus.getFormattedSeats();
		System.out.println(seatInfo);

		Scanner sc = new Scanner(System.in);
		int seatNum;

		String[] seats = seatInfo.replace("\u25A1", "0").replace("\u25A0", "1").replaceAll("\\s+", "").split("");

		while (true){
			try {
				System.out.print("원하는 좌석을 입력해주세요: ");
				String inputSeat = sc.nextLine();
				if (inputSeat.matches("0+[1-9]+") || inputSeat.matches("0+")) {
					throw new NumberFormatException(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
				}
				seatNum=Integer.parseInt(inputSeat);



				if(seatNum<1 || seatNum > seats.length) {
					System.out.println(red+"없는 좌석입니다. 다시 입력해주세요."+exit);
					continue;
				}
				//System.out.println("입력받은 좌석 번호는 : " + seatNum + "이고, 거기는 "+seats[seatNum-1]);

				if(seats[seatNum-1].equals("0")) {
					//결제화면으로 이동
					//System.out.println("결제화면으로 이동합니다?");
					tossPayment(user,fileName,selection,bus,seatNum);
					break;

				}
				if(seats[seatNum-1].equals("1")) {
					System.out.println(red+"해당 좌석은 예약된 좌석입니다. 다시 입력해주세요."+exit);
				}
			}catch (NumberFormatException e){
				System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			}
		}


	}

	//1.4 결제화면으로 넘겨주기 chargeMoney()로, 잔액, 금액 확인하기
	//구매확정이되면 선택한 티켓 안의 좌석문자열 1로 바꿔주고, user 클래스의 bus arraylist에 넣어주기
	private void tossPayment(User user,String fileName,int selection, Bus bus,int seatNum){
		Scanner sc = new Scanner(System.in);
		int choiceNum;

		ArrayList <Bus> userBus = user.getBusArrayList();
		LocalDate programDate = LocalDate.of(mainMenuManager.year, mainMenuManager.month, mainMenuManager.day);
		//System.out.println("사용자가 구매한 총 운행상품 수 : " + userBus.size());

		int userPoint = 0;
		for(Bus tBus : userBus) {
			if(tBus.isBefore())
				userPoint++;
		}
		//System.out.println("사용자가 구매'확정'한 총 운행상품 수 : " + userPoint);
		String discount = "0%";
		if(userPoint >= 15)
			discount = "20%";
		else if(userPoint >= 10)
			discount = "10%";
		else if(userPoint >= 5)
			discount = "5%";

		System.out.println("현재 잔액 ["+user.getUserAccount()+"]원");
		System.out.println("--------------------------------------------------------------");
		//System.out.println("결제하실 가격은 "+bus.printPrice()+"입니다.");
		System.out.println("운행상품의 가격은 "+bus.printPrice()+"원 입니다.");
		System.out.println(user.getUserName() + "님이 이용하신 운행 상품은 "+userPoint+"개이므로 "+discount+"의 할인이 적용됩니다.");
		System.out.println("최종 결제 가격은 "+bus.discountPrice(userPoint)+"원 입니다.");
		System.out.println("1: 결제하기");
		System.out.println("2: 충전하기");
		System.out.println("3: 티켓 상세조회로 돌아가기");

		boolean run = true;
		while (run){
			try {
				System.out.print("원하시는 번호를 입력해주세요: ");
				String inputPay = sc.nextLine();
				choiceNum = Integer.parseInt(inputPay);
				if (inputPay.matches("0+[1-9]+") || inputPay.matches("0+")) {
					throw new NumberFormatException(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
				}

				switch (choiceNum){
					case 1:
						if(bus.comparePrice(user.getUserAccount())){
							System.out.println(red+"잔액이 부족합니다. 충전하기를 입력해주세요."+exit);
							continue;
						}
						else{
							//user.setUserAccount(user.getUserAccount()- bus.getPrice());
//							System.out.println("test-user잔액:"+user.getUserAccount());
//							System.out.println("test-결제잔액:"+bus.printPrice());

							//좌석정보문자열바꿔주기
							addTicketUser(fileName,user,bus,seatNum);
							user.setUserAccount(bus.subPrice(user.getUserAccount()));
							editUserAccountFile(user);
							System.out.println("결제가 완료되었습니다.");
//							System.out.println("test-결제후잔액"+user.getUserAccount());
//							System.out.println("test-결제후잔액2"+bus.subPrice(user.getUserAccount()));
							System.out.println("--------------------------------------------------------------");
							initUserManager(user);
							run = false;
							break;
						}

					case 2:
						chargeMoney(user);
						run = false;
						break;
					case 3:
						detailTicket(user,fileName,selection);
						run = false;
						break;
					default:
						System.out.println(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
				}

			}catch (NumberFormatException e){
				System.out.println(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
			}
		}

	}

	//좌석 문자열 변경(user-userSeatArrayList, user-busArrayList추가
	private void addTicketUser(String fileName,User user, Bus bus, int seatNum){
		user.getBusArrayList().add(bus);
		user.getUserSeatArrayList().add(seatNum);

		File busFile = new File("bus/" + fileName);
		try (Scanner sc = new Scanner(busFile)){
			if(sc.hasNextLine()){
				String[] contents = sc.nextLine().split(",");
				int price = Integer.parseInt(contents[0].trim());
				String seats = contents[1].trim();
				String[] seatArray = seats.split(" ");

				if(seatArray[seatNum-1].equals("0")){
					seatArray[seatNum-1]="1";
				}
				String turnSeats = String.join(" ",seatArray);

				try (PrintWriter writer = new PrintWriter(busFile)){
					writer.println(price+","+turnSeats);
				}catch (FileNotFoundException e){
					System.out.println(e.getMessage());
				}

			}
		}catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}
	}

	// 2. 2. 사용자 운행상품 조회/취소 관련 메소드
	private void userSearchAndCancel(User user) {
		ArrayList<Bus> userBus = user.getBusArrayList();
		System.out.println(user.getUserName()+"님");
		System.out.println("보유 잔액 : "+user.getUserAccount()+"원");
		System.out.println("--------------------------------------------------------------");
		for(int i = 1; i <= userBus.size(); i++) {
			String bus = userBus.get(i - 1).toString().replace("∶",":");
			//System.out.println(i+". "+ userBus.get(i - 1).toString());
			System.out.println(i+". "+ bus);
		}

		String userInput;
		Scanner sc = new Scanner(System.in);
		do {
			System.out.print("상세 조회/취소할 상품 번호를 입력하세요(사용자 메뉴로 돌아가려면 0을 입력하세요):");
			userInput = sc.nextLine();
		}while(userSearchAndCancelCheck(userInput, userBus.size()));

		if(Integer.parseInt(userInput) == 0) {
			initUserManager(user);
			return;
		}
		userDetailSearchAndCancel(Integer.parseInt(userInput), user);
	}

	private void userDetailSearchAndCancel(int userBusNo, User user) {
		Bus userBus = user.getBusArrayList().get(userBusNo-1);
		int userSeat = user.getUserSeatArrayList().get(userBusNo-1);
		System.out.println(user.getUserName()+"님");
		System.out.println("보유하신 "+userBusNo+"번 상품의 상세 내용입니다.");
		System.out.println("--------------------------------------------------------------");
		System.out.println(userBus.getDetailInfo(userSeat));

		String userInput;
		Scanner sc = new Scanner(System.in);
		do {
			System.out.print("예매를 취소하시려면 y, 사용자 메뉴로 돌아가시려면 n을 입력해주세요:");
			userInput = sc.nextLine();
		}while(userDetailSearchAndCancelCheck(userInput));

		if(userInput.equals("y")) {
			if(!userBus.isAfter()) {
				System.out.println(user.getUserName()+"님");
				System.out.println(userBusNo+"번 운행 상품의 취소 가능 날짜를 지났으므로 취소하실 수 없습니다.");
				initUserManager(user);
			}
			else
				userCancel(userBusNo, user);
		} else if(userInput.equals("n")) {
			initUserManager(user);
		}
	}

	private void userCancel(int userBusNo, User user) {
		Bus userBus = user.getBusArrayList().get(userBusNo-1);
		int preAccount = user.getUserAccount();
		user.setUserAccount(userBus.addPrice(preAccount));

		ArrayList<Integer> userSeats = user.getUserSeatArrayList();
		userBus.returnSeat(userSeats.get(userBusNo -1 ));

		user.removeBus(userBusNo);
		editUserAccountFile(user);


		System.out.println(user.getUserName()+"님");
		System.out.println(userBusNo+"번 운행 상품의 취소가 완료되었습니다.");
		System.out.println("사용자 잔액 : "+preAccount+"원 -> "+user.getUserAccount()+"원");

		initUserManager(user);
	}


	private boolean userSearchAndCancelCheck(String userInput, int userBusCnt) {
		int userNo = -1;
		try {
			userNo = Integer.parseInt(userInput);
		} catch(NumberFormatException ex) {
			System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			return true;
		}
		String userNostr = userNo + "";
		if(userNostr.length() != userInput.length()) { //선후행 0 불가 처리
			System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			return true;
		}
		String userInputStrip = userInput.strip();
		if(userInputStrip.length() != userInput.length()) { //선후행 공백 불가 처리
			System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			return true;
		}
		if(userNo < 0) {
			System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			return true;
		}
		if(userNo > userBusCnt) {
			System.out.println(red+"보유하신 운행상품은 "+userBusCnt+"개입니다. 다시 입력해주세요."+exit);
			return true;
		}
		return false;
	}

	private boolean userDetailSearchAndCancelCheck(String userInput) {
		if(userInput.length() != 1) {
			System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			return true;
		}
		try {
			Integer.parseInt(userInput);
			System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			return true;
		} catch(NumberFormatException ex) {

		}
		if(userInput.equals("y") || userInput.equals("n")) {
			return false;
		}
		System.out.println(red+"다른 문자를 입력하셨습니다. 문자 y 또는 n을 입력해주세요: "+exit);
		return true;
	}


	// 3. 잔액 충전 관련 메소드
	private void chargeMoney(User user) {
		Scanner scan = new Scanner(System.in);
		int chargePrice = 0;
		System.out.println("보유 중인 잔액은 " + user.getUserAccount() + "원입니다.");
		boolean chargePriceCheck = true;
		boolean chargeUserCheck = true;
		while (chargePriceCheck) {
			try {
				System.out.print("충전을 원하는 금액을 입력해주세요: ");
				String userInput = scan.nextLine();
				Integer.parseInt(userInput);
				if(!(userInput.equals(userInput.strip()))){
					throw new NumberFormatException();
				}else if(userInput.charAt(0) == '0'){
					if(userInput.length() == 1){
						throw new InputMismatchException();
					}
					throw new NumberFormatException();
				}else{
					chargePrice = Integer.parseInt(userInput);
					if (chargePrice < 100 || chargePrice > 1000000 || chargePrice % 100 != 0) {
						throw new IllegalArgumentException();
					}
				}
			} catch (InputMismatchException e) {
				System.out.println(red+"100 - 1,000,000 사이의 금액만 충전 가능합니다. 다시 입력해주세요."+exit);
				continue;
			}catch (NumberFormatException e){
				System.out.println(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
				continue;
			}catch (IllegalArgumentException e) {
				if (chargePrice > 1000000) {
					System.out.println(red+"이번 달 충전 가능 금액은 1,000,000원 입니다. 다시 입력해주세요."+exit);
				} else if (chargePrice < 100) {
					System.out.println(red+"100 - 1,000,000 사이의 금액만 충전 가능합니다. 다시 입력해주세요."+exit);
				} else {
					System.out.println(red+"최소 입력 단위는 100원 입니다. 다시 입력해주세요."+exit);
				}
				continue;
			}
			chargePriceCheck = false;
		}

		printChargeCheck(user, chargePrice);
		String[] checkUserStr = new String[0];

		while (chargeUserCheck) {
			try {
				System.out.print("입력: ");
				checkUserStr = scan.nextLine().split(" ");
				if (checkUserStr.length != 3 ||
						!(checkUserStr[0].equals(user.getUserName()) &&
								checkUserStr[1].equals(user.getUserPW()) &&
								checkUserStr[2].equals(Integer.toString(chargePrice)))) {
					throw new IllegalArgumentException();
				}
			} catch (IllegalArgumentException e) {
				if (checkUserStr.length == 3) {
					System.out.println(red+"사용자 정보 또는 충전 금액이 올바르지 않습니다. 다시 입력해주세요."+exit);
				} else {
					System.out.println(red+"올바르지 않은 입력 형식입니다. '[사용자 이름] [비밀번호] [충전 금액]' 형식으로 다시 입력해주세요."+exit);
				}
				continue;
			}

			chargeUserCheck = false;
		}
		user.setUserAccount(user.getUserAccount() + chargePrice);
		editUserAccountFile(user);
		printChargeComplete(user);
		initUserManager(user);
	}

	private void editUserAccountFile(User user) {
		//try (BufferedWriter bw = new BufferedWriter(new FileWriter(directory + "/" + user.getUserID() + ".txt"))) {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory + "/" + user.getUserID() + ".txt"), StandardCharsets.UTF_8))) {
			bw.write(user.getUserID() + " " + user.getUserPW() + " " + user.getUserName());
			bw.newLine();
			bw.write(user.getUserAccount() + "");
			bw.newLine();
			int i = 0;
			for (Bus bus : user.getBusArrayList()) {
				bw.write(bus.getBusContentForUserFile(user.getUserSeatArrayList().get(i)));
				bw.newLine();
				i++;
			}
		} catch (IOException e) {
			System.err.println(red+"파일 쓰기 오류: " + e.getMessage()+exit);
		}
	}

	private void printChargeCheck (User user,int chargePrice){
		String checkStr = "\n결제내역 확인 및 결제동의\n";
		checkStr += user.getUserName() + "님 계정으로 " + chargePrice + "원이 충전됩니다.\n";
		checkStr += "현재 잔액: " + user.getUserAccount() + "원\n";
		checkStr += "충전 후 잔액: " + (user.getUserAccount() + chargePrice) + "원\n";
		checkStr += "충전하시려면 '[사용자 이름] [비밀번호] [충전 금액]' 을 공백 단위로 입력해주세요.\n";
		System.out.print(checkStr);
	}

	private void printChargeComplete (User user){
		String completeStr = "\n충전이 완료되었습니다.\n";
		completeStr += "현재 잔액: " + user.getUserAccount() + "원\n";
		completeStr += "충전이 완료되어, 메인 메뉴로 복귀합니다.\n";
		System.out.println(completeStr);
	}



	// 4. 로그아웃 관련 메소드
	private void logout () {
		logout_user = true;
	}

}
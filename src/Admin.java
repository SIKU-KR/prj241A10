import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin {
	public boolean logout_admin = false;
	// 멤버 변수
	private LocalDate programDate;
	private ArrayList<String> cityDic;	// 지역 사전 리스트
	final String red = "\u001B[31m" ;
	final String exit = "\u001B[0m" ;
	public static Scanner scan = new Scanner(System.in);

	// 생성자
	public Admin() {
		this.cityDic = new ArrayList<>();
		readCityDic();
	}

	// 메소드
	// 기능 실행 메소드
	public void initAdmin() {
		String selected;	// 사용자 입력 메뉴
		while(true) {
			System.out.println("-".repeat(50));
			System.out.println("1. 상품 등록");
			System.out.println("2. 상품 조회");
			System.out.println("3. 로그아웃");
			System.out.print("메뉴를 입력하세요: ");
			selected = scan.nextLine();
			if(selected.equals("1") || selected.equals("2") || selected.equals("3")) {
				break;
			} else {
				System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
			}
		}
		switch (selected) {
			case "1":
				registerBus();
				break;
			case "2":
				findBus();
				break;
			default:
				logout();
				break;
		}
	}

	// 텍스트 파일을 읽어 와 지역 사전 리스트 초기화 하는 메소드
	private void readCityDic() {
		try {
			Scanner file = new Scanner(new File("cityDictionary.txt"), StandardCharsets.UTF_8);
			while(file.hasNext()) {
				String word = file.next();
				this.cityDic.add(word);
			}
			file.close();
		} catch (FileNotFoundException e) {
			System.out.println(red+"파일을 찾을 수 없습니다."+exit);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	// 1. 상품 등록 관련 메소드
	private void registerBus() {
		String departure;	// 출발지
		String arrival;	// 도착지
		String departureDayStr;	// 출발일
		String departureTimeStr;	// 출발시각
		String sGrade;	// 문자열 등급
		Grade grade = null;	// 등급
		int price;	// 정수 가격


		System.out.println("-".repeat(50));
		System.out.println("출발지와 도착지로 가능한 지역은 " + printCityDic() + "입니다.");
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
		// 등급 입력 받기
		boolean gradeFlag = false;
		do {
			System.out.print("버스 등급을 입력하세요: ");
			sGrade = scan.nextLine();
			gradeFlag = checkGrade(sGrade);
		} while(!gradeFlag);
		try {
            grade = Grade.fromString(sGrade);
        } catch (IllegalArgumentException e) {
            System.out.println("타입 변경에 실패하였습니다.");
        }
		// 등급에 해당하는 가격 대응
		price = findPrice(departure, arrival, sGrade);
		
		LocalDate local = LocalDate.parse(departureDayStr);
		BusManager bm = new BusManager(departure, arrival, local);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 날짜 형식 지정
		LocalDate date = LocalDate.parse(departureDayStr, formatter);
		String seats = "";
		if(bm.hasDuplicates(new Bus(departure, arrival, departureDayStr, departureTimeStr.replace(":","∶"), price, seats, grade))) {
			System.out.println(red+"중복으로 등록된 상품이 있습니다. 메인화면으로 돌아갑니다."+exit);
			initAdmin();
		} else {
			try {
				int bus_cnt = countFilesInDirectory();
				// System.out.println("bus 디렉토리 내의 파일 개수: " + bus_cnt);
				if(bus_cnt>=50){
					System.out.println("등록할 수 있는 상품의 개수를 초과했습니다. 최대 등록 가능 상품은 50개입니다.");
					System.out.println("관리자 메뉴로 이동합니다.");
					initAdmin();
				}else {
					bm.addBus(departure, arrival, departureDayStr, departureTimeStr.replace(":","∶"), price, grade);
					initAdmin();
				}
			} catch (IOException e) {
				System.err.println("파일을 세는 동안 오류가 발생했습니다: " + e.getMessage());
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
			}
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
		System.out.println(red+"지역 사전에 저장되어있지 않은 지역을 입력하였습니다. 다시 입력해주세요."+exit);
		return false;
	}

	//출발일 검사 메소드
	boolean checkDepartureDayRe(String dayStr) {	// 정규 표현식 검사 메소드
		String re = "^(?:(?:20[0-2]\\d)-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01]))$";
		if(dayStr.matches(re))
			return true;
		System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
		return false;
	}

	boolean checkCalender(String dayStr) {	// 출발일 의미 규칙 검사 메소드
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			dateFormat.parse(dayStr);
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTime(dateFormat.parse(dayStr));
			return true;
		} catch (Exception e) {
			System.out.println(red+"달력에 존재하지 않는 날짜입니다. 다시 입력해주세요."+exit);
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

			this.programDate = LocalDate.of(mainMenuManager.year,mainMenuManager.month,mainMenuManager.day);
			// 입력된 날짜가 programDate 이후이고, programDate로부터 1년 이내인지 확인
			if (!inputDate.isBefore(programDate) && !inputDate.isAfter(programDate.plusYears(1))) {
				return true;
			} else {
				System.out.println(red+"입력한 날짜는 프로그램 날짜로부터 1년 이내가 아닙니다."+exit);
				return false;
			}
		} catch (DateTimeParseException e) {
			System.out.println(red+"올바르지 않은 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요."+exit);
			return false;
		}
	}

	// 출발시각 검사 메소드
	boolean checkDepartureTime(String timeStr) {
		String re = "^(?:[01]\\d|2[0-3]):[0-5]\\d$";
		if(timeStr.matches(re))
			return true;
		System.out.println(red+"올바르지 않은 입력입니다. 다시 입력해주세요."+exit);
		return false;
	}

	// 가격 검사 메소드
	boolean checkPrice(String price) {
		String re = "^(?:[1-9]\\d{2,4}|100000)$";
		if(price.matches(re))
			return true;
		System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
		return false;
	}
	
	// 등급 검사 메소드
	boolean checkGrade(String grade) {
		if(grade.equals("프리미엄") || grade.equals("우등") || grade.equals("일반"))
			return true;
		System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
		return false;
	}
	
	// 등급에 해당하는 가격 반환 메소드
	int findPrice(String departure, String arrival, String grade) {
		String csvFile = "pricesheet.csv"; // CSV 파일의 경로
        String line = "";
        String cvsSplitBy = ","; // CSV 파일의 구분자

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // 한 줄씩 읽으면서 ','로 구분하여 배열에 저장
                String[] busInfo = line.split(cvsSplitBy);

                // 출발지와 도착지가 순서에 상관없이 일치하는지 확인
                if ((busInfo[0].equals(departure) && busInfo[1].equals(arrival)) || 
                    (busInfo[0].equals(arrival) && busInfo[1].equals(departure))) {
                    
                    // 등급에 따라 해당하는 가격 반환
                    switch (grade) {
                        case "일반":
                            return Integer.parseInt(busInfo[2]);
                        case "우등":
                            return Integer.parseInt(busInfo[3]);
                        case "프리미엄":
                            return Integer.parseInt(busInfo[4]);
                        default:
                            throw new IllegalArgumentException("Unknown grade: " + grade);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 해당하는 출발지, 도착지, 등급이 없는 경우
        throw new IllegalArgumentException("No matching route or grade found");
	}

	// 2. 상품 조회 관련 메소드
	private void findBus() {
		String departure;	// 출발지
		String arrival;	// 도착지
		String departureDayStr;	// 출발일

		// 출발지와 도착지 입력
		System.out.println("-".repeat(50));
		System.out.println("출발지와 도착지로 가능한 지역은 " + printCityDic() + "입니다.");
		//scan.nextLine();
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
			
			int input = -1;
			boolean busCountFlag = false;
			do {
                System.out.print("상세 조회할 상품 번호를 입력하세요 (관리자 메뉴로 돌아가려면 0을 입력하세요):");
                try {
                    String inputStr = scan.nextLine();
                    input = Integer.parseInt(inputStr);
                    String inputCheckStr = input+"";
                    if(inputStr.length() != inputCheckStr.length()){
                        throw new Exception("선 후행 입력 예외 발생!");
                    }
                } catch (Exception e) {
                    System.out.println(red + "올바르지 않은 입력형식입니다. 다시 입력해주세요." + exit);
                }
            } while (!busCountFlag(bm, input));
			if(input != 0){
				bm.printBusSpecific(input);
			}
			initAdmin();
		}
	}

	private boolean busCountFlag(BusManager bm, int input){
		if(input > bm.getBusCount() || input < 0) {
			System.out.println(red+"올바르지 않은 입력 범위입니다. 다시 입력해주세요."+exit);
			return false;
		} else {
			return true;
		}
	}

	// 3. 로그아웃 관련 메소드
	private void logout() {
//		mainMenuManager m = new mainMenuManager();
//		m.showmenu();
		logout_admin = true;
	}

	// 파일 개수 카운트 메소드
	public static int countFilesInDirectory() throws IOException {
		Path path = Paths.get("bus");
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("올바른 디렉토리가 아닙니다.");
		}

		int fileCount = 0;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (Files.isRegularFile(entry)) {
					fileCount++;
				}
			}
		} catch (IOException e) {
			System.err.println("디렉토리 스트림을 여는 동안 오류가 발생했습니다: " + e.getMessage());
			throw e;
		}

		return fileCount;
	}

}

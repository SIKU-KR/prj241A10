import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserManager {
	// 멤버 변수
	private int year;    // 가상 날짜(년)
	private int month;    // 가상 날짜(월)
	private int day;    // 가상 날짜(일)
	private int money;    // 사용자 잔액
	private final String directory = "user";    // 디렉토리 이름
	private ArrayList<User> userArrayList;    // 유저 목록

	// 생성자
	public UserManager() {
		this.userArrayList = new ArrayList<>();
//				getUserFromFileSystem();
	}

	// Getter, Setter


	// 메소드


	// 기능 실행 메소드
	public void initUserManager(User user) {
		Scanner scan = new Scanner(System.in);
		String menuStr = "\n" + user.getUserName() + "님, 안녕하세요!\n";
		menuStr += "보유 잔액: " + user.getUserAccount() + "원\n";
		menuStr += "-".repeat(20);
		menuStr += "1. 운행 상품 조회 및 예매\n";
		menuStr += "2. 사용자 운행 상품 조회 및 취소\n";
		menuStr += "3. 잔액 충전\n";
		menuStr += "4. 로그아웃\n";
		int menuNum = 0;
		boolean menuNumCheck = true;
		while (menuNumCheck) {
			try {
				System.out.print("원하시는 기능의 메뉴 번호를 입력하세요: ");
				menuNum = scan.nextInt();
				if (menuNum < 1 || menuNum > 4)
					throw new IllegalArgumentException();
			} catch (InputMismatchException e) {
				System.out.println("올바르지 않은 입력 형식입니다. 다시 입력해주세요.");
				scan.nextLine();
				continue;
			} catch (IllegalArgumentException e) {
				System.out.println("1 ~ 4 사이의 숫자 중 하나만 입력해주세요.");
				scan.nextLine();
				continue;
			}
			menuNumCheck = false;
		}
		switch (menuNum) {
			case 1:
				searchAndBook();
				break;
			case 2:
				userSearchAndCancel(user);
				break;
			case 3:
				chargeMoeny(user);
				break;
			case 4:
				logout();
				break;
		}
	}

	// 1. 운행상품 조회/예매 관련 메소드
	private void searchAndBook() {

	}

	// 2. 2. 사용자 운행상품 조회/취소 관련 메소드
	private void userSearchAndCancel(User user) {
		ArrayList<Bus> userBus = user.getBusArrayList();
		System.out.println(user.getUserName()+"님");
		System.out.println("보유 잔액 : "+user.getUserAccount()+"원");
		System.out.println("--------------------------------------------------------------");
		for(int i = 1; i <= userBus.size(); i++) {
			System.out.println(i+". "+ userBus.get(i - 1).toString());
		}

		String userInput;
		Scanner sc = new Scanner(System.in);
		do {
			System.out.println("상세 조회/취소할 상품 번호를 입력하세요(사용자 메뉴로 돌아가려면 0을 입력하세요):");
			userInput = sc.nextLine();
		}while(userSearchAndCancelCheck(userInput, userBus.size()));

		if(Integer.parseInt(userInput) == 0) {
			initUserManager(user);
			return;
		}
		userDetailSearchAndCancel(Integer.parseInt(userInput), user); //이미 걸러서 예외처리 필요 없을듯
	}

	private void userDetailSearchAndCancel(int userBusNo, User user) {
		ArrayList<Bus> userBus = user.getBusArrayList();
		System.out.println(user.getUserName()+"님");
		System.out.println("보유하신 "+userBusNo+"번 상품의 상세 내용입니다.");
		System.out.println("--------------------------------------------------------------");
		System.out.println(userBus.get(userBusNo-1).getDetailInfo());

		String userInput;
		Scanner sc = new Scanner(System.in);
		do {
			System.out.println("예매를 취소하시려면 y, 사용자 메뉴로 돌아가시려면 n을 입력해주세요:");
			userInput = sc.nextLine();
		}while(userDetailSearchAndCancelCheck(userInput));

		if(userInput.equals("y")) {
			userCancel(userBusNo, user);
		} else if(userInput.equals("n")) {
			initUserManager(user);
		}
	}

	private void userCancel(int userBusNo, User user) {
		//user 클래스에 미리 원하는 행 지우는 메소드를 만들어두고 실행하는게 편할듯
		//어쨌든 메모장에서 해당 행 삭제하고, ArrayList 에서도 삭제해야함.

		System.out.println(user.getUserName()+"님");
		System.out.println(userBusNo+"번 운행 상품의 취소가 완료되었습니다.");
		int preAccount = user.getUserAccount();
		//user 클래스에서 메모장 가격 변경하는 메소드 호출

		System.out.println("사용자 잔액 : "+preAccount+"원 -> "+user.getUserAccount()+"원");

		initUserManager(user);
	}


	private boolean userSearchAndCancelCheck(String userInput, int userBusCnt) {
		int userNo = -1;
		try {
			userNo = Integer.parseInt(userInput);
		} catch(NumberFormatException ex) {
			System.err.println("올바르지 않은 입력형식입니다. 다시 입력해주세요.");
			return true;
		}
		if(userNo < 0) {
			System.err.println("올바르지 않은 입력형식입니다. 다시 입력해주세요.");
			return true;
		}
		if(userNo > userBusCnt) {
			System.out.println("보유하신 운행상품은 "+userBusCnt+"개입니다. 다시 입력해주세요.");
			return true;
		}
		return false;
	}

	private boolean userDetailSearchAndCancelCheck(String userInput) {
		if(userInput.length() != 1) {
			System.err.println("올바르지 않은 입력형식입니다. 다시 입력해주세요.");
			return true;
		}
		try {
			Integer.parseInt(userInput);
			System.err.println("올바르지 않은 입력형식입니다. 다시 입력해주세요.");
			return true;
		} catch(NumberFormatException ex) {

		}
		if(userInput.equals("y") || userInput.equals("n")) {
			return false;
		}
		System.out.println("다른 문자를 입력하셨습니다. 문자 y 또는 n을 입력해주세요: ");
		return true;
	}


	// 3. 잔액 충전 관련 메소드
	private void chargeMoeny(User user) {
		Scanner scan = new Scanner(System.in);
		int chargePrice = 0;
		System.out.println("보유 중인 잔액은 " + user.getUserAccount() + "원입니다.");
		boolean chargePriceCheck = true;
		boolean chargeUserCheck = true;
		while (chargePriceCheck) {
			try {
				System.out.print("충전을 원하는 금액을 입력해주세요: ");
				chargePrice = scan.nextInt();
				if (chargePrice < 100 || chargePrice > 1000000 || chargePrice % 100 != 0) {
					throw new IllegalArgumentException();
				}
			} catch (InputMismatchException e) {
				System.out.println("올바르지 않은 입력 형식입니다. 다시 입력해주세요.");
				scan.nextLine();
				continue;
			} catch (IllegalArgumentException e) {
				if (chargePrice > 1000000) {
					System.out.println("이번 달 충전 가능 금액은 1,000,000원 입니다. 다시 입력해주세요.");
				} else if (chargePrice < 100) {
					System.out.println("100 - 1,000,000 사이의 금액만 충전 가능합니다. 다시 입력해주세요.");
				} else {
					System.out.println("최소 입력 단위는 100원 입니다. 다시 입력해주세요.");
				}
				scan.nextLine();
				continue;
			}
			scan.nextLine();
			chargePriceCheck = false;
		}

		printChargeCheck(user, chargePrice);
		String[] checkUserStr = new String[0];

		while (chargeUserCheck) {
			try {
				System.out.print("입력: ");
				checkUserStr = scan.nextLine().split("\\s+");
				if (checkUserStr.length != 3 ||
						!(checkUserStr[0].equals(user.getUserName()) &&
								checkUserStr[1].equals(user.getUserPW()) &&
								checkUserStr[2].equals(Integer.toString(chargePrice)))) {
					throw new IllegalArgumentException();
				}
			} catch (IllegalArgumentException e) {
				if (checkUserStr.length == 3) {
					System.out.println("사용자 정보 또는 충전 금액이 올바르지 않습니다. 다시 입력해주세요.");
				} else {
					System.out.println("올바르지 않은 입력 형식입니다. '[사용자 이름] [비밀번호] [충전 금액]' 형식으로 다시 입력해주세요.");
				}
				continue;
			}

			chargeUserCheck = false;
		}
		user.setUserAccount(user.getUserAccount() + chargePrice);
		editUserAccountFile(user);
		printChargeComplete(user);
	}

	private void editUserAccountFile(User user) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(directory + "/" + user.getUserName() + ".txt"))) {
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
			System.err.println("파일 쓰기 오류: " + e.getMessage());
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

	}

}

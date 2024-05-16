import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mainMenuManager {
    // user클래스에 전달할 리스트
    public List<String> foruser = new ArrayList<String>();
    public boolean loginFlag; // true가 사용자 로그인, false가 관리자 로그인
    private final String adminID = "ADMIN";
    private final String adminPW = "admin1234";

    final String red = "\u001B[31m" ;
    final String exit = "\u001B[0m" ;

    // 멤버 변수
    public static int year;	// 가상 날짜(년)
    public static int month;	// 가상 날짜(월)
    public static int day;	// 가상 날짜(일)

    // 생성자
    public mainMenuManager() {
        super();
    }

    public void initmainMenu() {
        int n = getvdate();
        if (n==1){
            showmenu();
        }else {
            System.exit(0);
        }
    }

    public void showmenu(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------------------------------------------------");
        System.out.println("[버스 티켓 관리 플랫폼]");
        System.out.println("1.로그인");
        System.out.println("2.회원가입");
        System.out.println("3.프로그램 종료");
        String menuNum = "0";
        while (true) {
            try {
                System.out.print("원하시는 기능의 메뉴 번호를 입력하세요: ");
                menuNum = scanner.nextLine();
                if (menuNum.equals("1")||menuNum.equals("2")||menuNum.equals("3")){
                    break;
                }else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println(red+"올바르지 않은 입력 형식입니다. 다시 입력해주세요."+exit);
                continue;
            }
        }
        switch (menuNum){
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                System.out.println("프로그램을 종료합니다.");
                System.exit(0);
        }
    }

    // 가상 날짜 입력 함수
    private int getvdate(){
        Scanner scanner = new Scanner(System.in);

        // 정규표현식 패턴
        String regexPattern = "^(?:(?:20[0-2]\\d)-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01]))$";
        Pattern pattern = Pattern.compile(regexPattern);

        while (true){
            System.out.println("--------------------------------------------------------------");
            System.out.println("프로그램을 종료하려면 숫자 0을 입력해주세요.");
            System.out.print("프로그램에서 사용할 날짜를 입력해주세요. (yyyy-mm-dd): ");
            String inputDate = scanner.nextLine();

            // 입력된 날짜가 정규표현식을 따르는지 확인
            Matcher matcher = pattern.matcher(inputDate);

            if (inputDate.equals("0")){
                System.out.println("프로그램을 종료합니다.");
                System.exit(0);
            }
            else if (matcher.matches()) {
                // System.out.println("입력한 날짜가 정규표현식을 따릅니다.");
                // 입력된 날짜가 캘린더에 있는 값인지 확인
                if (isValidDate(inputDate)) {
                    // System.out.println("입력한 날짜는 캘린더에 있는 값입니다.");
                    String[] dateArray = inputDate.split("-");
                    year = Integer.parseInt(dateArray[0]);
                    month = Integer.parseInt(dateArray[1]);
                    day = Integer.parseInt(dateArray[2]);
                    break;
                } else {
                    System.out.println(red+"올바르지 않은 날짜입니다. 다시 입력해주세요."+exit);
                }
            } else {
                System.out.println(red+"올바르지 않은 입력형식입니다. 다시 입력해주세요."+exit);
            }
        }
        return 1;
    }

    // 입력된 날짜가 캘린더에 있는 값인지 확인하는 함수
    public static boolean isValidDate(String inputDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false); // 엄격한 날짜 형식 체크

            // 날짜 파싱
            dateFormat.parse(inputDate);

            // 캘린더 값인지 확인
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(inputDate));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 로그인 기능 메소드
    private void login() {

        foruser.clear();

        List<String> txtFileNames = getUserID("user"+"/");

        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------------------------------------------------");
        System.out.println("로그인");

        while (true) {
            // 아이디 입력 받기
            System.out.print("아이디: ");
            String userID = scanner.nextLine();

            // 패스워드 입력 받기
            System.out.print("비밀번호: ");
            String userPW = scanner.nextLine();

            // 로그인 여부 입력받기
            System.out.print("로그인 하시겠습니까? (y / 다른키를 입력하면 초기화면으로 이동합니다): ");
            String check = scanner.nextLine();
            if (check.equals("y")){
                //관리자용 계정인지 체크
                if (userID.equals(adminID) && userPW.equals(adminPW)){
                    System.out.println("관리자 모드로 접속합니다. 관리자 메뉴화면으로 이동합니다.");
                    loginFlag = false;
                    break;
                }else {
//                    if (txtFileNames.isEmpty()){
//                        System.out.println("먼저 회원가입을 진행해주세요.");
//                        showmenu();
//                        return;
//                    }
                    // 파일 경로
                    String filePath = "user/" + userID + ".txt";
                    // 파일 읽기
                    File file = new File(filePath);
                    if (!file.exists()){
                        System.out.println(red+"로그인에 실패했습니다. 다시 입력해주세요."+exit);
                        continue;
                    }
                    //try (Scanner fileScanner = new Scanner(file)) {
                    try (Scanner fileScanner = new Scanner(file, StandardCharsets.UTF_8)) {
                        // 파일 내용에서 아이디, 비밀번호, 이름 추출
                        String storedID = fileScanner.next();
                        String storedPW = fileScanner.next();
                        String userName = fileScanner.next();

                        // 비밀번호가 일치하는지 확인
                        if (storedID.equals(userID) && storedPW.equals(userPW)) {
                            System.out.println("사용자 모드로 접속합니다. 사용자 메뉴화면으로 이동합니다.");
                            foruser.add(storedID);
                            foruser.add(storedPW);
                            foruser.add(userName);
                            loginFlag = true;
                            break;
                        } else {
                            System.out.println(red + "로그인에 실패했습니다. 다시 입력해주세요." + exit);
                        }
                    } catch (IOException e) {
                        System.out.println(red + "로그인 중 오류가 발생했습니다." + exit);
                    }
                }
            }else {
                System.out.println("초기화면으로 이동합니다.");
                showmenu();
                break;
            }
        }
    }


    // 회원 가입 기능 메소드
    private void register() {

        // txt 파일명(아이디) 목록 가져오기
        List<String> txtFileNames = getUserID("user"+"/");

        // 정규표현식 패턴
        String regexPattern_ID = "[A-Za-z0-9]{3,7}";
        String regexPattern_PW = "[A-Za-z0-9]{5,10}";
        String regexPattern_NAME = "[a-zA-Z]{1,20}";

        Pattern pattern_ID = Pattern.compile(regexPattern_ID);
        Pattern pattern_PW = Pattern.compile(regexPattern_PW);
        Pattern pattern_NAME = Pattern.compile(regexPattern_NAME);

        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------------------------------------------------");
        System.out.println("사용할 계정의 정보를 입력해주세요.");

        // 아이디 입력 받기
        String userID;
        while (true) {
            System.out.print("아이디 (3~7자의 영문 대소문자 또는 숫자): ");
            userID = scanner.nextLine();
            // 아이디 중복 검사
            boolean sameIDcheck = false;
            if (!txtFileNames.isEmpty()) {
                for (String fileName : txtFileNames) {
                    if (fileName.equals(userID)){
                        sameIDcheck = true;
                    }
                }
            }

            // 입력된 아이디가 정규표현식을 따르는지 확인
            Matcher matcher_ID = pattern_ID.matcher(userID);

            if (userID.equals(adminID)) {
                System.out.println(red + "사용할 수 없는 아이디입니다. 다시 입력해주세요." + exit);
            } else if (sameIDcheck) {
                System.out.println(red + "이미 있는 아이디입니다. 다시 입력해주세요." + exit);
            } else if (matcher_ID.matches()){
                break; // 정규표현식 검사를 통과하면 반복문 종료
            }else {
                System.out.println(red + "올바르지 않은 형식의 아이디입니다. 다시 입력해주세요." + exit);
            }
        }

        // 비밀번호 입력 받기
        String userPW;
        while (true) {
            System.out.print("비밀번호 (5~10자의 영문 대소문자 또는 숫자): ");
            userPW = scanner.nextLine();

            // 입력된 비밀번호가 정규표현식을 따르는지 확인
            Matcher matcher_PW = pattern_PW.matcher(userPW);

            if (matcher_PW.matches()) {
                break; // 정규표현식 검사를 통과하면 반복문 종료
            } else {
                System.out.println(red + "올바르지 않은 형식의 비밀번호입니다. 다시 입력해주세요." + exit);
            }
        }

        // 이름 입력 받기
        String userName;
        while (true) {
            System.out.print("이름 (1~20자의 영문 대소문자): ");
            userName = scanner.nextLine();

            // 입력된 이름이 정규표현식을 따르는지 확인
            Matcher matcher_NAME = pattern_NAME.matcher(userName);

            if (matcher_NAME.matches()) {
                break; // 정규표현식 검사를 통과하면 반복문 종료
            } else {
                System.out.println(red + "올바르지 않은 형식의 이름입니다. 다시 입력해주세요." + exit);
            }
        }

        // 회원가입 여부 입력받기
        System.out.print("회원가입 하시겠습니까? (y / 다른키를 입력하면 초기화면으로 이동합니다): ");
        String check = scanner.nextLine();
        if (check.equals("y")){
            saveUserInfoToFile(userID, userPW, userName);
            System.out.println("회원가입이 완료되었습니다. 초기화면으로 이동합니다.");
            showmenu();
        }else {
            System.out.println("초기화면으로 이동합니다.");
            showmenu();
        }

//        // 입력된 아이디와 비밀번호,이름 출력
//        System.out.println("입력된 계정 정보:");
//        System.out.println("아이디: " + userID);
//        System.out.println("비밀번호: " + userPW);
//        System.out.println("이름: " + userName);

    }

    // 파일명(유저아이디) 가져오는 함수
    private List<String> getUserID(String directoryPath) {
        // 디렉토리 내의 파일 목록 가져오기
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        // txt 파일명을 저장할 리스트 생성
        List<String> txtFileNames = new ArrayList<>();

        // 파일 목록 순회하며 확장자가 .txt인 파일의 파일명만 리스트에 추가
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    // 확장자를 제외한 파일명만 가져오기
                    String fileNameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");
                    txtFileNames.add(fileNameWithoutExtension);
                }
            }
        }

        return txtFileNames;
    }

    // 파일에 계정 정보 저장하는 함수
    private void saveUserInfoToFile(String userID, String userPW, String userName) {
        try {
            File dir = new File("user");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath = "user/" + userID + ".txt";
            //FileWriter writer = new FileWriter(filePath);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
            writer.write(userID + " " + userPW + " " + userName + "\n" + "0");
            writer.close();
        } catch (IOException e) {
            System.out.println(red+"계정 정보 저장 중 오류 발생"+exit);
        }
    }

}
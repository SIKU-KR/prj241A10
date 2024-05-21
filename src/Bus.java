import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// TODO !! getFormmattedSeats()의 관리자버전, 유저버전을 업데이트 하기.

public class Bus {
    // 멤버 변수
    private final String departure;	// 출발지
    private final String arrival;	// 도착지
    private LocalDate date;	// 출발일
    private final String time;	// 출발시각
    private final int price;	// 가격
    private String seats;	// 좌석
    private final Grade grade;

    // 생성자
    public Bus(String departure, String arrival, String date, String time, int price, String seats, Grade grade) {
        this.departure = departure.strip();
        this.arrival = arrival.strip();
        this.time = time.strip();
        this.price = price;
        this.grade = grade;
        this.seats = seats.strip();
        try{
            String[] dateArray = date.split("-");
            this.date = LocalDate.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
        } catch(Exception e) {
            System.out.println("Wrong date parameter");
        }
    }

    public Bus(String departure, String arrival, LocalDate date, String time, int price, Grade grade) {
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
        this.time = time;
        this.price = price;
        this.grade = grade;
    }

    // isEqual 메소드 오버라이드
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bus bus = (Bus) obj;
        return departure.equals(bus.departure) && arrival.equals(bus.arrival) && date.equals(bus.date) && time.equals(bus.time) && grade.equals(bus.grade);
    }

    // hashCode 메소드 오버라이드
    @Override
    public int hashCode() {
        return Objects.hash(departure, arrival, date, time, grade);
    }

    // toString 메소드 오버라이드
    @Override
    public String toString() {
        return this.departure + " " + this.arrival + " " + this.getFormattedDate() + " " + this.time + " " + this.grade.toString();
    }

    // "yyyy-mm-dd" formatted data 문자열 반환
    private String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.date.format(formatter);
    }

    // 좌석 배열 출력 메소드
    public String getFormattedSeats() {
        StringBuilder ret = new StringBuilder();
        String[] seatInfo = this.seats.split(" ");
        if(seatInfo.length != 21){
            System.err.println("wrong bus seats information");
            return null;
        }
        for(int i = 0; i < seatInfo.length; i++){
            if((i+1) % 3 == 0) {
                if(seatInfo[i].equals("1")){
                    ret.append("\u25A0").append("\n");
                } else {
                    ret.append("\u25A1").append("\n");
                }
            } else if ((i+1) % 3 == 1) {
                if(seatInfo[i].equals("1")){
                    ret.append("\u25A0");
                } else {
                    ret.append("\u25A1");
                }
            } else {
                if(seatInfo[i].equals("1")){
                    ret.append("\u25A0 ");
                } else {
                    ret.append("\u25A1 ");
                }
            }
        }
        return ret.toString();
    }

    // 상세 정보 출력 메소드
    public String getDetailInfo() {
        String ret;
        ret = "출발지: " + this.departure + "\n";
        ret += "도착지: " + this.arrival + "\n";
        ret += "일자: " + this.getFormattedDate() + "\n";
        String time2 = this.time.replace("∶",":");
        ret += "시간: " + time2 + "\n";
        ret += "버스 등급: " +this.grade.toString() + "\n";
        ret += "가격: " + this.price + "원\n";
        ret += "좌석현황:\n";
        ret += getFormattedSeats();
        return ret;
    }

    // BusManager 생성할 때 지역과 날짜로 필터링 할 때 쓰는 메소드
    public boolean checkFilter(String departure, String arrival, LocalDate date){
        return (this.arrival.equals(arrival)) && (this.departure.equals(departure)) && (this.date.isEqual(date));
    }

    public boolean isWithinOneYear(LocalDate programDate) {
        LocalDate oneYearAfterProgramDate = programDate.plusYears(1);
        return (this.date.isEqual(programDate) || this.date.isAfter(programDate)) && this.date.isBefore(oneYearAfterProgramDate);
    }

    public String getBusContentForUserFile(int userSeat){
        String time2 = this.time.replace("∶",":");
        return this.date + " / " + this.departure + " / " + this.arrival + " / 출발시간 : " + time2 + " / 보유좌석 : " + userSeat + "번 / 가격 : " + this.price + "원 / 버스 등급 : " + this.grade.toString();
    }

    public boolean buySeat(int seatnum) {
        String directory = "bus";
        File file = new File(directory + "/" + this + ".txt");
        try {
            //BufferedReader reader = new BufferedReader(new FileReader(directory + "/" + this + ".txt"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(directory + "/" + this + ".txt"), StandardCharsets.UTF_8));
            String[] line = reader.readLine().strip().split(",");
            String[] seats = line[1].split(" ");
            seats[seatnum + 1] = "1";

            StringBuilder seatsString = new StringBuilder();
            for (String seat : seats) {
                seatsString.append(seat).append(" "); // 좌석 정보를 문자열에 추가하고 띄어쓰기를 붙입니다.
            }
            this.seats = seatsString.toString().trim();
            // 파일을 쓰기 모드로 열어서 수정된 내용을 다시 씁니다.
            //BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            writer.write(line[0] + ","); // 버스 번호를 쓰고 쉼표를 붙입니다.
            writer.write(this.seats); // 좌석 정보 문자열을 쓰고 공백을 제거하여 붙입니다.
            writer.close(); // 파일을 닫습니다.

        } catch (IOException e) {
            System.err.println("존재하지 않는 버스입니다.");
            return false;
        }
        return true;
    }

    public boolean returnSeat(int seatnum) {
        String directory = "bus";
        File file = new File(directory + "/" + this + ".txt");
        try {
            //ufferedReader reader = new BufferedReader(new FileReader(directory + "/" + this + ".txt"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(directory + "/" + this + ".txt"), StandardCharsets.UTF_8));
            String[] line = reader.readLine().strip().split(",");
            String[] seats = line[1].split(" ");
            seats[seatnum - 1] = "0";

            StringBuilder seatsString = new StringBuilder();
            for (String seat : seats) {
                seatsString.append(seat).append(" "); // 좌석 정보를 문자열에 추가하고 띄어쓰기를 붙입니다.
            }
            this.seats = seatsString.toString().trim();
            // 파일을 쓰기 모드로 열어서 수정된 내용을 다시 씁니다.
            //BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            writer.write(line[0] + ","); // 버스 번호를 쓰고 쉼표를 붙입니다.
            writer.write(this.seats); // 좌석 정보 문자열을 쓰고 공백을 제거하여 붙입니다.
            writer.close(); // 파일을 닫습니다.

        } catch (IOException e) {
            System.err.println("존재하지 않는 버스입니다.");
            return false;
        }
        return true;
    }

    private String getFormattedSeats(int userBusNo) {
        StringBuilder ret = new StringBuilder();

        for(int i = 0; i < 21; i++){
            if((i+1) % 3 == 0) {
                if(i+1 == userBusNo){
                    ret.append("\u25A0").append("\n");
                } else {
                    ret.append("\u25A1").append("\n");
                }
            } else if ((i+1) % 3 == 1) {
                if(i+1 == userBusNo){
                    ret.append("\u25A0");
                } else {
                    ret.append("\u25A1");
                }
            } else {
                if(i+1 == userBusNo){
                    ret.append("\u25A0 ");
                } else {
                    ret.append("\u25A1 ");
                }
            }
        }
        return ret.toString();
    }

    public String getDetailInfo(int userBusNo) {
        String ret;
        ret = "출발지: " + this.departure + "\n";
        ret += "도착지: " + this.arrival + "\n";
        ret += "일자: " + this.getFormattedDate() + "\n";
        String time2 = this.time.replace("∶",":");
        ret += "시간: " + time2 + "\n";
        ret += "버스 등급: " +this.grade.toString() + "\n";
        ret += "가격: " + this.price + "원\n";
        ret += "좌석현황:\n";
        ret += getFormattedSeats(userBusNo);
        return ret;
    }

    public String printPrice() {
        return price+"";
    }

    public boolean comparePrice(int account) {
        return account < price;
    }

    public int addPrice(int account) {
        return account + price;
    }

    public int subPrice(int account) {
        return account - price;
    }

}
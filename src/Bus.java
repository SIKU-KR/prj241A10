import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Bus {
	// 멤버 변수
	private String departure;	// 출발지
    private String arrival;	// 도착지
    private LocalDate date;	// 출발일
    private String time;	// 출발시각
    private int price;	// 가격
    private String seats;	// 좌석
		
	// 생성자
    public Bus(String departure, String arrival, String date, String time, int price, String seats) {
        this.departure = departure.strip();
        this.arrival = arrival.strip();
        this.time = time.strip();
        this.price = price;
        this.seats = seats.strip();
        try{
            String[] dateArray = date.split("-");
            this.date = LocalDate.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
        } catch(Exception e) {
            System.err.println("Wrong date parameter");
        }
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
        return departure.equals(bus.departure) && arrival.equals(bus.arrival) && date.equals(bus.date) && time.equals(bus.time);
    }

    // hashCode 메소드 오버라이드
    @Override
    public int hashCode() {
        return Objects.hash(departure, arrival, date, time);
    }

    // toString 메소드 오버라이드
    @Override
    public String toString() {
        return this.departure + " " + this.arrival + " " + this.getFormattedDate() + " " + this.time;
    }

    // "yyyy-mm-dd" formatted data 문자열 반환
    private String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.date.format(formatter);
    }

    // 좌석 배열 출력 메소드
    private String getFormattedSeats() {
        StringBuilder ret = new StringBuilder();
        String[] seatInfo = this.seats.split(" ");
        if(seatInfo.length != 21){
            System.err.println("wrong bus seats information");
            return null;
        }
        for(int i = 0; i < seatInfo.length; i++){
            if((i+1) % 3 == 0) {
                if(seatInfo[i].equals("1")){
                    ret.append("◼︎\n");
                } else {
                    ret.append("◻︎\n");
                }
            } else if ((i+1) % 3 == 1) {
                if(seatInfo[i].equals("1")){
                    ret.append("◼︎");
                } else {
                    ret.append("◻︎");
                }
            } else {
                if(seatInfo[i].equals("1")){
                    ret.append("◼︎ ");
                } else {
                    ret.append("◻︎ ");
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
        ret += "시간: " + this.time + "\n";
        ret += "가격: " + this.price + "원\n";
        ret += "좌석현황:\n";
        ret += getFormattedSeats();
        return ret;
    }

    public boolean checkFilter(String arrival, String departure, LocalDate date){
        return (this.arrival.equals(arrival)) && (this.departure.equals(departure)) && (this.date.equals(date));
    }

    public boolean isWithinOneYear(LocalDate programDate) {
        LocalDate oneYearAfterProgramDate = programDate.plusYears(1);
        return !this.date.isBefore(programDate) && this.date.isBefore(oneYearAfterProgramDate);
    }
}

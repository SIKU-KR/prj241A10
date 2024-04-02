public class Bus {
	// 멤버 변수
	private String departure;	// 출발지
    private String arrival;	// 도착지
    private String date;	// 출발일
    private String time;	// 출발시각
    private int price;	// 가격
    private String seats;	// 좌석
		
	// 생성자
    public Bus(String departure, String arrival, String date, String time, int price, String seats) {
        this.departure = departure.strip();
        this.arrival = arrival.strip();
        this.date = date;
        this.time = time.strip();
        this.price = price;
        this.seats = seats.strip();
    }
	
	// Getter, Setter
	
	
	// 메소드
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
        ret += "일자: " + this.date + "\n";
        ret += "시간: " + this.time + "\n";
        ret += "가격: " + this.price + "원\n";
        ret += "좌석현황:\n";
        ret += getFormattedSeats();
        return ret;
    }

    // 상품 정보 문자열 출력 오버라이딩
    @Override
    public String toString() {
        return this.departure + " " + this.arrival + " " + this.date + " " + time;
    }
}

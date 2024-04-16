import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class BusManager {
	// 멤버 변수
	private ArrayList<Bus> busArrayList;	// 버스 목록
	private final String directory = "bus";	// 디렉토리 이름
    private final LocalDate programDate = LocalDate.of(2024,1,1);
	
	// 생성자, 매개변수 없이 전달되면 모든 파일 불러옴
	public BusManager() {
        this.busArrayList = new ArrayList<>();
        getBusFromFileSystem();
    }

    // 생성자, 출발지, 도착지, 출발일에 부합하는 파일만 읽어옴
    public BusManager(String departure, String arrival, LocalDate date) {
        this.busArrayList = new ArrayList<>();
        getBusFromFileSystem();
        filterBy(departure, arrival, date);
    }

	// 메소드
    // 파일에서 버스 읽어 오는 메소드
    private void getBusFromFileSystem(){
        File dir = new File(this.directory + "/");
        String[] filenames = dir.list();
        if(filenames != null) {
            for (int i = 0; i < filenames.length; i++) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(this.directory + "/" + filenames[i]));
                    String line = reader.readLine().strip();
                    String[] busInfo = filenames[i].split("\\.")[0].split(" ");
                    String[] busDetails = line.split(",");
                    this.busArrayList.add(new Bus(busInfo[0], busInfo[1], busInfo[2], busInfo[3], Integer.parseInt(busDetails[0]), busDetails[1]));
                } catch (IOException e) {
                    System.err.println("운행 상품 읽기를 실패했습니다.");
                } catch (NumberFormatException e) {
                    System.err.println("운행 상품의 내용에서 예외가 발생했습니다.");
                } catch (NullPointerException e) {
                    System.err.println("운행 상품을 읽는 중 예외가 발생했습니다.");
                }
            }
        }
        // 날짜 필터링 로직 추가
        this.busArrayList.removeIf(item -> !item.isWithinOneYear(this.programDate));
    }

    private void filterBy(String departure, String arrival, LocalDate date){
        this.busArrayList.removeIf(item -> !item.checkFilter(departure, arrival, date));
    }

    public boolean isBusListEmpty(){
        return this.busArrayList.isEmpty();
    }

    // 상품 목록 출력 메소드
    public void printBusList() {
        int idx = 1;
        for (Bus b : this.busArrayList){
            System.out.println(idx + ". " + b);
            idx++;
        }
    }

    // returns true if busArrayList has Duplicates
    public boolean hasDuplicates(Bus target){
        for(Bus b: busArrayList){
            if(b.equals(target)){
                return true;
            }
        }
        return false;
    }

    // 버스 객체 텍스트 파일 추가 메소드
    public void addBus(String departure, String arrival, String date, String time, int price){
        String filename = departure + " " + arrival + " " + date + " " + time + ".txt";
        String newBusSeats = "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0";
        String content = price + "," + newBusSeats;

        // 디렉토리가 존재하지 않으면 생성
        try {
            File dir = new File(this.directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 파일에 내용 작성
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.directory + "/" + filename))) {
            writer.write(content);
            System.out.println("성공적으로 운행 상품이 등록되었습니다.");
        } catch (IOException e) {
            System.err.println("파일 쓰기 오류: " + e.getMessage());
        }
        this.busArrayList.add(new Bus(departure, arrival, date, time, price, newBusSeats));
    }

}

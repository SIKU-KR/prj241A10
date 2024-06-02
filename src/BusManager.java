import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

// TODO !! grade 추가하여서 모든 로직 수정하기!!!!!!!
public class BusManager {

    // 멤버 변수
    private final ArrayList<Bus> busArrayList;	// 버스 목록
    private final String directory = "bus";	// 디렉토리 이름


//    // 생성자, 매개변수 없이 전달되면 모든 파일 불러옴
//    public BusManager() {
//        this.busArrayList = new ArrayList<>();
//        this.pricelist = new ArrayList<>();
//        getBusFromFileSystem();
//        readPrice();
//    }

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
            for (String filename : filenames) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(this.directory + "/" + filename));
                    String line = reader.readLine().strip();
                    String[] busInfo = filename.split("\\.")[0].split(" ");
                    String[] busDetails = line.split(",");
                    Grade grade = Grade.fromString(busInfo[4]);
                    this.busArrayList.add(new Bus(busInfo[0], busInfo[1], busInfo[2], busInfo[3], Integer.parseInt(busDetails[0]), busDetails[1], grade));
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
        LocalDate programDate = LocalDate.of(mainMenuManager.year, mainMenuManager.month, mainMenuManager.day);
        this.busArrayList.removeIf(item -> !item.isWithinOneYear(programDate));
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
            String b2 = b.toString().replace("∶",":");
            System.out.println(idx + ". " + b2);
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
    public void addBus(String departure, String arrival, String date, String time, int price, Grade grade){
        String windowTime = time.replace(":","∶");
        String filename = departure + " " + arrival + " " + date + " " + windowTime + " " + grade.toString() + ".txt";
        //String newBusSeats = "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0";
        String newBusSeats = switch (grade.toString()) {
            case "일반" -> "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0";
            case "우등" -> "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0";
            default -> "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0";
        };
        String content = price + "," + newBusSeats;
        // 파일에 내용 작성
        //try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.directory + "/" + filename))) {
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.directory + "/" + filename), StandardCharsets.UTF_8))) {
            writer.write(content);
            System.out.println("성공적으로 운행 상품이 등록되었습니다.");
        } catch (IOException e) {
            System.err.println("파일 쓰기 오류: " + e.getMessage());
        }
        this.busArrayList.add(new Bus(departure, arrival, date, windowTime, price, newBusSeats, grade));
    }
    public int getBusCount(){
        return this.busArrayList.size();
    }

    public void printBusSpecific(int idx){
        System.out.println(this.busArrayList.get(idx-1).getDetailInfo());
    }
}
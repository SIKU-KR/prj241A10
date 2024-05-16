import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Main {

	static boolean logoutFlag;
	private static final mainMenuManager m = new mainMenuManager();
	//사용자
	private static User user = new User();
	private static final UserManager userManager = new UserManager();
	//관리자
	private static final Admin admin = new Admin();

	public static void main(String[] args) {
        // 디렉토리가 존재하지 않으면 생성
		try {
			File dir = new File("bus");
			File dir2 = new File("user");
			if (!dir2.exists()){
				dir2.mkdirs();
			}
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		m.initmainMenu();
		if (m.loginFlag) {
			user = user.loadUserFromFile("user/" + m.foruser.get(0) + ".txt");
			userManager.initUserManager(user);
			//System.out.println(user);
			logoutFlag = userManager.logout_user;
		} else {
			admin.initAdmin();
			logoutFlag = admin.logout_admin;
		}
		while (true){
			if (logoutFlag){
				flow();
			}else {
				break;
			}
		}

	}


	private static void flow(){
		m.showmenu();
		if (m.loginFlag) {
			user = user.loadUserFromFile("user/" + m.foruser.get(0) + ".txt");
			userManager.initUserManager(user);
			//System.out.println(user);
			logoutFlag = userManager.logout_user;
		} else {
			admin.initAdmin();
			logoutFlag = admin.logout_admin;
		}
	}
}

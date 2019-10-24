package day17;

@Component
public class UserDao {
	@Value ("${spring.datasource.driver}")
	private String driver;
	@Value ("${spring.datasource.username}")
	private String username;
	@Value ("${spring.datasource.password}")
	private String password;
	@Value ("${spring.datasource.url}")
	private String url;
	
	void test() {
		// TODO Auto-generated method stub
		System.out.println("----------------------");
		System.out.println(driver);
		System.out.println(username);
		System.out.println(password);
		System.out.println(url);
	}
}

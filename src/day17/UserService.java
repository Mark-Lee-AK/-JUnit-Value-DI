package day17;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;
	
	public void test() {
		System.out.println("\n--UserService----------------");
		userDao.test();
	}
}

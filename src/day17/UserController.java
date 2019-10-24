package day17;

@Controller
public class UserController {
	@Autowired
	private UserService userServ;
	
	public void test() {
		System.out.println("\n--UserController----------------");
		userServ.test();
	}
}

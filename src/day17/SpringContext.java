package day17;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpringContext {
	private Map<String, Object> map = new HashMap<String, Object>();

	// 自动扫描，在文件加中自动找到所有的类
	private void autoScan() throws Exception {
		String path = _Test2.class.getResource("/").getPath();
		path = URLDecoder.decode(path, "UTF-8");

		File dir = new File(path);
		StringBuilder sb = new StringBuilder();// 用StringBuilder拼接包名
		scan(dir, sb);
		autoWired();// 完成自动装配
		/*
		 *	假设目录结构如下
		 * [aaa]目录
		 *   |- b 
		 *   |- c |- [ddd]目录 
		 *        |- e 
		 *        |- f 
		 *   |- g 
		 *   |- h
		 */
	}

	private void autoWired() throws Exception {
		// 从map取出所有的实例,再进行遍历
		Collection<Object> values = map.values();
		for (Object obj : values) {
			Class<? extends Object> c = obj.getClass();// 获得这个实例的“类对象”
			Field[] a = c.getDeclaredFields();// 获得类中所有变量
			for (Field f : a) {//遍历这些成员变量
				// 判断变量上是否存在Autowired和Value注解
				if (f.isAnnotationPresent(Autowired.class)) {
					injectObject(c, obj, f);//注入对象
				}else if(f.isAnnotationPresent(Value.class)) {
					injectableValues(c, obj, f);//注入数据
				}
			}
		}
	}
	
	private void injectObject(Class<? extends Object> c, Object obj, Field f) throws Exception {
		/*
		 *  @Autowired
		 *  UserDao userDao
		 *  
		 *  - 获得变量的类型(类对象)
		 *  - 用类对象获得实例
		 *  - 把实例保存到这个变量
		 */
		Class<?> type = f.getType();
		Object v = get(type);
		f.setAccessible(true);
		f.set(obj, v);
	}

	private void injectableValues(Class<? extends Object> c, Object obj, Field f) throws Exception {
		/*
		 * @Value("${spring.datasoure.username}")
		 * String username;
		 * 
		 * - 获得@Value注解
		 * - 获得注解的属性name或value
		 * - 从Configure获取配置数据
		 * - 把配置数据保存到这个变量
		 */
		Value value = f.getAnnotation(Value.class);
		String name = value.name();//有name用name
		if (name.equals("")) {
			name = value.value();//没有name用value
		}
		String v = Configure.get(name);
		f.setAccessible(true);
		f.set(obj, v);
	}

	private void scan(File dir, StringBuilder sb) throws Exception {
		File[] list = dir.listFiles();
		if (list == null) {
			return;
		}
		for (File f : list) {
			if (f.isFile()) {// f是文件
				String n = f.getName();// 得到文件名
				if (n.endsWith(".class")) {// 如果文件名后缀是.class
					n = n.substring(0, n.length() - 6);// 把后缀去掉，留下类名
					n = sb + "." + n;// 包名.类名
					handle(n);
				}
			} else {// f是文件夹
				if (sb.length() != 0) {// 第一层包不加电,后面的包名在前面连一个点
					sb.append(".");
				}
				String n = f.getName();// 得到文件夹的名字
				sb.append(n);// 文件夹名连进去作为包名
				scan(f, sb);// 递归处理子目录

				// "aa.bb" -> "aa"
				// "aa" -> ""
				int index = sb.lastIndexOf(".");
				if (index == -1) {
					index = 0;
				}
				sb.delete(index, sb.length());
			}
		}
	}

	private void handle(String n) throws Exception {

		// 创建实例,并放入map集合
		Class<?> c = Class.forName(n);
		if (c.isAnnotationPresent(Component.class) || c.isAnnotationPresent(Service.class)
				|| c.isAnnotationPresent(Controller.class)) {
			Object obj = c.newInstance();
			map.put(n, obj);// 完整包名---->实例
		}
	}

	private <T> T get(Class<T> c) {// 根据类对象，取得实例
		String n = c.getName();// 获取完整类名
		return (T) map.get(n);// 用完整类名提取实例
	}

	public static void main(String[] args) throws Exception {
		SpringContext ctx = new SpringContext();
		ctx.autoScan();

		System.out.println(ctx.map);
		System.out.println("-------- get the class --------");
		UserController userController = ctx.get(UserController.class);
		UserDao userDao = ctx.get(UserDao.class);
		UserService userService = ctx.get(UserService.class);
		System.out.println(userDao);
		System.out.println(userService);
		System.out.println(userController);
		
		UserController controller = ctx.get(UserController.class);
		controller.test();
	}
}

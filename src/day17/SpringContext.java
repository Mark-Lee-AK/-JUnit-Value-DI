package day17;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpringContext {
	private Map<String, Object> map = new HashMap<String, Object>();

	// �Զ�ɨ�裬���ļ������Զ��ҵ����е���
	private void autoScan() throws Exception {
		String path = _Test2.class.getResource("/").getPath();
		path = URLDecoder.decode(path, "UTF-8");

		File dir = new File(path);
		StringBuilder sb = new StringBuilder();// ��StringBuilderƴ�Ӱ���
		scan(dir, sb);
		autoWired();// ����Զ�װ��
		/*
		 *	����Ŀ¼�ṹ����
		 * [aaa]Ŀ¼
		 *   |- b 
		 *   |- c |- [ddd]Ŀ¼ 
		 *        |- e 
		 *        |- f 
		 *   |- g 
		 *   |- h
		 */
	}

	private void autoWired() throws Exception {
		// ��mapȡ�����е�ʵ��,�ٽ��б���
		Collection<Object> values = map.values();
		for (Object obj : values) {
			Class<? extends Object> c = obj.getClass();// ������ʵ���ġ������
			Field[] a = c.getDeclaredFields();// ����������б���
			for (Field f : a) {//������Щ��Ա����
				// �жϱ������Ƿ����Autowired��Valueע��
				if (f.isAnnotationPresent(Autowired.class)) {
					injectObject(c, obj, f);//ע�����
				}else if(f.isAnnotationPresent(Value.class)) {
					injectableValues(c, obj, f);//ע������
				}
			}
		}
	}
	
	private void injectObject(Class<? extends Object> c, Object obj, Field f) throws Exception {
		/*
		 *  @Autowired
		 *  UserDao userDao
		 *  
		 *  - ��ñ���������(�����)
		 *  - ���������ʵ��
		 *  - ��ʵ�����浽�������
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
		 * - ���@Valueע��
		 * - ���ע�������name��value
		 * - ��Configure��ȡ��������
		 * - ���������ݱ��浽�������
		 */
		Value value = f.getAnnotation(Value.class);
		String name = value.name();//��name��name
		if (name.equals("")) {
			name = value.value();//û��name��value
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
			if (f.isFile()) {// f���ļ�
				String n = f.getName();// �õ��ļ���
				if (n.endsWith(".class")) {// ����ļ�����׺��.class
					n = n.substring(0, n.length() - 6);// �Ѻ�׺ȥ������������
					n = sb + "." + n;// ����.����
					handle(n);
				}
			} else {// f���ļ���
				if (sb.length() != 0) {// ��һ������ӵ�,����İ�����ǰ����һ����
					sb.append(".");
				}
				String n = f.getName();// �õ��ļ��е�����
				sb.append(n);// �ļ���������ȥ��Ϊ����
				scan(f, sb);// �ݹ鴦����Ŀ¼

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

		// ����ʵ��,������map����
		Class<?> c = Class.forName(n);
		if (c.isAnnotationPresent(Component.class) || c.isAnnotationPresent(Service.class)
				|| c.isAnnotationPresent(Controller.class)) {
			Object obj = c.newInstance();
			map.put(n, obj);// ��������---->ʵ��
		}
	}

	private <T> T get(Class<T> c) {// ���������ȡ��ʵ��
		String n = c.getName();// ��ȡ��������
		return (T) map.get(n);// ������������ȡʵ��
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

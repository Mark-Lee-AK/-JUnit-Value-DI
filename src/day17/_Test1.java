package day17;

import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;

public class _Test1 {
	public static void main(String[] args) throws Exception {
		/*
		 * ��usrDaoȡ�����г�Ա����
		 * �����������Valueע�� 
		 * ʹ��Coinfigure������ȡ�������ݣ�Ϊ��Ա������ֵ
		 */

		Class<UserDao> c = UserDao.class;
		UserDao dao = new UserDao();

		Field[] a = c.getDeclaredFields();
		for (Field f : a) {
			if (f.isAnnotationPresent(Value.class)) {
				Value value = f.getAnnotation(Value.class);
				String name = value.name();
				if (name.equals("")) {
					name = value.value();
				}
				String v = Configure.get(name);
				f.setAccessible(true);
				f.set(dao, v);
			}
		}
		dao.test();
	}
}

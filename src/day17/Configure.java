package day17;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javassist.Loader;
import ognl.Ognl;

public class Configure {
	private static Map<String, Map> cfg;

	public static void load() throws Exception {
		// ��ȡapplication.yml�����ļ���·��
		// "/" --- �����ڵ��ļ��У�binĿ¼�£�
		String path = Configure.class.getResource("/application.yml").getPath();
		// path�ַ����Ǿ���URL������ַ�����UTF-8
		// d:/��/xxx -> d:/%e4&e8%ead/XXX -����س���ȷ���ַ�-> d:/��/xxx
		path = URLDecoder.decode(path, "UTF-8");
		System.out.println(path);

		// ��jackson api��OjectMapper�������ݣ�ָ��yaml�������������yaml����
		// ����.yml�ļ�
		ObjectMapper m = new ObjectMapper(new YAMLFactory());
		// ��ȡ�����ļ��������ݷ�װ��Map������
		// ���ؽ������������ݵ�cfg��
		cfg = m.readValue(new File(path), Map.class);
	}

	public static String get(String ognl) {
		// ${spring.datasource.driver}
		// ---> spring.datasource.driver

		// ȥ���ո�
		ognl = ognl.trim();
		// ��ȡ�±� 2~ognl.length()-1 �ڵ�����
		ognl = ognl.substring(2, ognl.length() - 1);
		try {
			// ʹ��ognl���ʽ����cfg������ȡ����
			return (String) Ognl.getValue(ognl, cfg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		load();
		Map m = cfg.get("spring");
		System.out.println(m);
		Map m2 = (Map) m.get("datasource");
		Object p = m2.get("driver");
		System.out.println(p);

		// ${spring.datasource.driver} ognl
		System.out.println(get("${spring.datasource.driver}"));
		System.out.println(get("${spring.datasource.username}"));
		System.out.println(get("${spring.datasource.password}"));
		System.out.println(get("${spring.datasource.url}"));
	}
}

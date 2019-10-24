package day17;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javassist.Loader;

public class Configure {
	private static Map<String, Map> cfg;

	public static void load() throws Exception {
		// 获取application.yml配置文件的路径
		// "/" --- 类所在的文件夹（bin目录下）
		String path = Configure.class.getResource("/application.yml").getPath();
		// path字符串是经过URL编码的字符串，UTF-8
		// d:/中/xxx -> d:/%e4&e8%ead/XXX -解码回成正确的字符->  d:/中/xxx
		path = URLDecoder.decode(path, "UTF-8");
		System.out.println(path);

		// 用jackson api的OjectMapper解析数据，指定yaml解析插件来解析yaml数据
		// 解析.yml文件
		ObjectMapper m = new ObjectMapper(new YAMLFactory());
		// 读取配置文件，把数据封装到Map对象里
		// 加载解析出来的内容到cfg里
		cfg = m.readValue(new File(path), Map.class);
	}

	public static void main(String[] args) throws Exception {
		load();
		Map m = cfg.get("spring");
		System.out.println(m);
	}
}

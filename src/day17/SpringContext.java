package day17;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class SpringContext {
	private Map<String, Object> map = new HashMap<String, Object>();
	
	// 自动扫描，在文件加中自动找到所有的类
	private void autoScan() throws Exception {
		String path = _Test2.class.getResource("/").getPath();
		path = URLDecoder.decode(path, "UTF-8");

		File dir = new File(path);
		StringBuilder sb = new StringBuilder();//用StringBuilder拼接包名
		scan(dir, sb);
		/*
		 * [aaa]目录
		 *   |- b
		 *   |- c
		 *   |- [ddd]目录
		 *         |- e
		 *         |- f
		 *   |- g
		 *   |- h
		 */
		
	}

	private void scan(File dir, StringBuilder sb) throws Exception {
		// TODO Auto-generated method stub
		File[] list = dir.listFiles();
		if (list == null) {
			
			return;
		}
		for (File f : list) {
			if (f.isFile()) {//f是文件
				String n =f.getName();//得到文件名
				if (n.endsWith(".class")) {//如果文件名后缀是.class
					n = n.substring(0,n.length()-6);//把后缀去掉，留下类名
					n = sb+"."+ n;//包名.类名
					handle(n);
				}
			} else {//f是文件夹
				if (sb.length()!=0) {//第一层包不加电,后面的包名在前面连一个点
					sb.append(".");
				}
				String n = f.getName();//得到文件夹的名字
				sb.append(f.getName());//文件夹名连进去作为包名
				scan(f, sb);//递归处理子目录
				
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
		if (c.isAnnotationPresent(Component.class)||
				c.isAnnotationPresent(Service.class)||
				c.isAnnotationPresent(Controller.class)){
			Object obj = c.newInstance();
			map.put(n, obj);// 完整包名---->实例
		}
	}
	
	public static void main(String[] args) throws Exception {
		SpringContext ctx = new SpringContext();
		ctx.autoScan();
		System.out.println(ctx.map);
	}
}

package day17;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class _Test2 {
	public static void main(String[] args) throws Exception {
		String path = _Test2.class.getResource("/").getPath();
		path = URLDecoder.decode(path, "UTF-8");
		System.out.println(path);
		
		f(new File(path));
	}

	private static void f(File dir) {
		// TODO Auto-generated method stub
		File[] a = dir.listFiles();
		for (File f : a) {
			if (f.isFile()) { //f���ļ���?
				System.out.println(f.getName());
			}else {	// f���ļ���
				f(f); // �ݹ鴦���ڲ��ļ�
			}
		}
	}
}

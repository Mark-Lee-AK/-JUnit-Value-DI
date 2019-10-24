package day17;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class SpringContext {
	private Map<String, Object> map = new HashMap<String, Object>();
	
	// �Զ�ɨ�裬���ļ������Զ��ҵ����е���
	private void autoScan() throws Exception {
		String path = _Test2.class.getResource("/").getPath();
		path = URLDecoder.decode(path, "UTF-8");

		File dir = new File(path);
		StringBuilder sb = new StringBuilder();//��StringBuilderƴ�Ӱ���
		scan(dir, sb);
		/*
		 * [aaa]Ŀ¼
		 *   |- b
		 *   |- c
		 *   |- [ddd]Ŀ¼
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
			if (f.isFile()) {//f���ļ�
				String n =f.getName();//�õ��ļ���
				if (n.endsWith(".class")) {//����ļ�����׺��.class
					n = n.substring(0,n.length()-6);//�Ѻ�׺ȥ������������
					n = sb+"."+ n;//����.����
					try {
						// ����ʵ��,������map����
						Object obj = Class.forName(n).newInstance();
						map.put(n, obj);//��������---->ʵ��
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			} else {//f���ļ���
				if (sb.length()!=0) {//��һ������ӵ�,����İ�����ǰ����һ����
					sb.append(".");
				}
				String n = f.getName();//�õ��ļ��е�����
				sb.append(f.getName());//�ļ���������ȥ��Ϊ����
				scan(f, sb);//�ݹ鴦����Ŀ¼
				
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
	
	public static void main(String[] args) throws Exception {
		SpringContext ctx = new SpringContext();
		ctx.autoScan();
		System.out.println(ctx.map);
	}
}

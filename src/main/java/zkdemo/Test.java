package zkdemo;

import java.util.List;

public class Test {

	public static void main(String[] args) {
		
		CuratorZookeeperClient client = CuratorZookeeperClient.getInstance("127.0.0.1");
		String path = "/server";
		
		try {
			String value = client.get(path);
			
			System.out.println(value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

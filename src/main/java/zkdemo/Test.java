package zkdemo;

import com.bucuoa.utils.WestZookeeperClient;

public class Test {

	public static void main(String[] args) {
		
		WestZookeeperClient client = WestZookeeperClient.getInstance("127.0.0.1");
		String path = "/server/service/interface";
		
		try {
			
			client.write(path,"fkkkkkkkkk");
			
			String value = client.get(path);
			
			System.out.println(value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

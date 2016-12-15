package curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

public class Demo {
//	private static CuratorFramework zkClient;
	
	private static final int CONNECT_TIMEOUT = 15000;
	private static final int RETRY_TIME = Integer.MAX_VALUE;
	private static final int RETRY_INTERVAL = 1000;
	
	static CuratorFramework newCurator(String zkServers) {
		return CuratorFrameworkFactory.builder().connectString(zkServers)
				.retryPolicy(new RetryNTimes(RETRY_TIME, RETRY_INTERVAL)).connectionTimeoutMs(CONNECT_TIMEOUT).build();
	}
	
	public static void main(String[] args) {
		
		String  zkServers = "127.0.0.1:2181";
		CuratorFramework zkClient = newCurator(zkServers);
		
		try {
			CrudExample.setData(zkClient, "/server", "fuck zk".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		zkClient.close();
	}
	

}

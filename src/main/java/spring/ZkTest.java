package spring;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class ZkTest {

	public static void main(String[] args) {
		ZkClient zkClient = new ZkClient("127.0.0.1:2181",3000);  
		  
		zkClient.subscribeChildChanges("/server",new IZkChildListener() {  
		    public void handleChildChange(String parentPath, List<String> children) throws Exception {  
		        if(children == null){  
		            System.out.println("<" + parentPath + "> is deleted");  
		            return;  
		        }  
		        for(String child : children){  
		            System.out.println(parentPath+" add <Child>:" + child);  
		        }  
		    }  
		});  
		
		LockSupport.park();
	}

}

package zkdemo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZkDockerService {
	private static final Logger logger = LoggerFactory.getLogger(ZkDockerService.class);

	private CuratorZookeeperClient zkClient;

	private Set<String> zkPathList = new HashSet<String>();
	// ʧ�����Զ�ʱ������ʱ����Ƿ�������ʧ�ܣ����У����޴�����
	private ScheduledFuture<?> retryFuture;
	// ��ʱ����ִ����
	private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1,new NamedThreadFactory("RegistryFailedRetryTimer", true));
	// ��Ҫ����ע�������
	private Set<ClientData> retrySet = new HashSet<ClientData>();

	/**
	 * init-method����ʼ��ִ�� ������docker��IP��ַ �˿ڶ�ע�ᵽzookeeper��
	 */
	public void register2Zookeeper() {
		try {
			zkClient = CuratorZookeeperClient.getInstance("127.0.0.1");
			ClientData client = findClientData();
			registerClientData(client);
			zkClient.addStateListener(new ZkStateListener() {
				public void reconnected() {
					ClientData client = findClientData();
					// ��������ӵ������б�
					retrySet.add(client);
				}
			});
			// �����߳̽������ԣ�1��ִ��һ�Σ���Ϊjobcenter�Ķ�ʱ����ʱ����̵���1��
			this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
				public void run() {
					// ��Ⲣ����ע������
					try {
						retryRegister();
					} catch (Throwable t) { // �������ݴ�
						logger.error("Unexpected error occur at failed retry, cause: " + t.getMessage(), t);
					}
				}
			}, 1, 1, TimeUnit.SECONDS);

		} catch (Exception e) {
			logger.error("zookeeper write exception", e);
		}
	}

	/**
	 * destrory-method,����ʱִ��
	 */
	public void destroy4Zookeeper() {
		logger.info("zkDockerService destrory4Zookeeper path=" + zkPathList);
		try {
			if (retryFuture != null) {
				retryFuture.cancel(true);
			}

		} catch (Throwable t) {
			logger.warn(t.getMessage(), t);
		}

		if (zkPathList != null && zkPathList.size() > 0) {
			for (String path : zkPathList) {
				try {
					zkClient.delete(path);
				} catch (Exception e) {
					logger.error("zkDockerService destrory4Zookeeper exception", e);
				}
			}
		}
		zkClient.close();
	}

	/** ����Ҫ�洢�Ķ��� **/
	private ClientData findClientData() {
		ClientData client = new ClientData();
		client.setIpAddress("192.168.1.109");
		client.setPort(8080);
		client.setSource(1);
		return client;
	}

	/** ��ֵд��zookeeper�� **/
	private void registerClientData(ClientData client) throws Exception {
		String centerPath = "/server";
		String content = "";
		String strServer = zkClient.write(centerPath, content);
		if (!StringUtils.isBlank(strServer)) {
			zkPathList.add(strServer);
		}
	}

	/**
	 * ������zookeeperʱ���Զ�����
	 */
	protected synchronized void retryRegister() {
		if (!retrySet.isEmpty()) {
			logger.info("jobclient  begin retry register client to zookeeper");
			Set<ClientData> retryClients = new HashSet<ClientData>(retrySet);
			for (ClientData data : retryClients) {
				logger.info("retry register=" + data);
				try {
//					registerJobcenterClient(data);
					retrySet.remove(data);
				} catch (Exception e) {
					logger.error("registerJobcenterClient failed", e);
				}
			}
		}
	}
}
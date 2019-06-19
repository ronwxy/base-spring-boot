package cn.jboost.springboot.autoconfig.alimns.executor;


import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import cn.jboost.springboot.autoconfig.alimns.MnsProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class MnsClientFactory implements ApplicationContextAware, InitializingBean, DisposableBean {

	private final static ThreadLocal<MNSClient> _clients = new ThreadLocal<>();

	private final static Queue<CloudAccount> _accounts = new LinkedBlockingDeque<>();

	private static MnsProperties mnsProperties;

	public static MNSClient getClient() {
		MNSClient client = _clients.get();
		if (client == null) {
			CloudAccount account = new CloudAccount(mnsProperties.getAccessId(), mnsProperties.getAccessKey(),
					mnsProperties.getAccountEndpoint());
			client = account.getMNSClient();

			_accounts.add(account);
			_clients.set(client);
		}
		return client;
	}

	public void destroy() {
		for (CloudAccount account : _accounts) {
			account.getMNSClient().close();
		}
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		MnsClientFactory.mnsProperties = context.getBean(MnsProperties.class);
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(mnsProperties, "mnsProperties can not be null");
	}
}

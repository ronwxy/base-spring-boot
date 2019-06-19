package cn.jboost.springboot.autoconfig.alimns;

import cn.jboost.springboot.autoconfig.alimns.executor.MnsClientFactory;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsExecutor;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsTaskFactory;
import cn.jboost.springboot.autoconfig.alimns.executor.task.receive.ReceiveTaskExecutor;
import cn.jboost.springboot.autoconfig.alimns.listener.MnsListener;
import com.aliyun.mns.model.Message;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnClass(Message.class)
@EnableConfigurationProperties(MnsProperties.class)
public class MnsAutoConfiguration {


	@Bean
	public ApplicationRunner receiverTaskExecutorRunner(ApplicationContext context) {
		Map<String, MnsListener> mnsListenerMap = context.getBeansOfType(MnsListener.class);
		if (mnsListenerMap == null || mnsListenerMap.isEmpty()) {
			return args -> {
			};
		}
		return new ReceiverTaskExecutorRunner(mnsListenerMap.values(), context.getBean(MnsExecutor.class));
	}

	@Bean
	public MnsExecutor mnsExecutor(@Qualifier("mnsTaskExecutor") ThreadPoolTaskExecutor mnsTaskExecutor,
								   @Qualifier("mnsFailedScheduler") ThreadPoolTaskScheduler mnsFailedScheduler,
								   @Qualifier("mnsPostExecutor") TaskExecutor mnsPostExecutor,
								   MnsProperties mnsProperties, @Autowired(required = false) JdbcTemplate jdbcTemplate) {
		MnsExecutor executor = new MnsExecutor(mnsProperties, jdbcTemplate);
		executor.setExecutor(mnsTaskExecutor);
		executor.setFailedScheduler(mnsFailedScheduler);
		executor.setPostExecutor(mnsPostExecutor);
		return executor;
	}

	@Bean(name = "mnsTaskExecutor")
	public ThreadPoolTaskExecutor mnsTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(2);
		threadPoolTaskExecutor.setMaxPoolSize(4);
		threadPoolTaskExecutor.setQueueCapacity(200);
		threadPoolTaskExecutor.setKeepAliveSeconds(120);
		return threadPoolTaskExecutor;
	}

	@Bean(name = "mnsFailedScheduler")
	public ThreadPoolTaskScheduler mnsFailedScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(2);
		return threadPoolTaskScheduler;
	}

	@Bean(name = "mnsPostExecutor")
	public TaskExecutor mnsPostExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		return executor;
	}

	@Bean
	public MnsClientFactory mnsClientFactory() {
		return new MnsClientFactory();
	}

	public static class ReceiverTaskExecutorRunner implements ApplicationRunner, DisposableBean {
		private final Collection<MnsListener> mnsListeners;
		private final MnsExecutor mnsExecutor;
		private final List<ReceiveTaskExecutor> _receiveTaskExecutors = new ArrayList<>();

		public ReceiverTaskExecutorRunner(Collection<MnsListener> mnsListeners, MnsExecutor mnsExecutor) {
			this.mnsListeners = mnsListeners;
			this.mnsExecutor = mnsExecutor;
		}

		@Override
		public void run(ApplicationArguments args) {
			for (MnsListener mnsListener : mnsListeners) {
				ReceiveTaskExecutor receiveTaskExecutor = MnsTaskFactory.buildReceiveTask(mnsListener, mnsExecutor);
				_receiveTaskExecutors.add(receiveTaskExecutor);
				receiveTaskExecutor.run();
			}
		}

		@Override
		public void destroy() throws Exception {
			for (ReceiveTaskExecutor receiveTaskExecutor : _receiveTaskExecutors) {
				receiveTaskExecutor.stop();
			}
		}
	}
}

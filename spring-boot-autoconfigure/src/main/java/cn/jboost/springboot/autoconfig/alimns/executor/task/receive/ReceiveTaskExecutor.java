package cn.jboost.springboot.autoconfig.alimns.executor.task.receive;

public interface ReceiveTaskExecutor extends Runnable {

	void run();

	void stop();

}

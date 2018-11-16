package com.springboot.boot.alimns.executor.task.receive;

public interface ReceiveTaskExecutor extends Runnable {

	void run();

	void stop();

}

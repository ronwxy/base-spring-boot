package com.springboot.boot.alimns.executor;


import com.springboot.boot.alimns.ResourceType;
import com.springboot.boot.alimns.executor.task.receive.QueueReceiveTaskExecutor;
import com.springboot.boot.alimns.executor.task.receive.ReceiveTaskExecutor;
import com.springboot.boot.alimns.executor.task.send.QueueSendTask;
import com.springboot.boot.alimns.executor.task.send.TopicSendTask;
import com.springboot.boot.alimns.listener.MnsListener;
import org.apache.commons.lang3.tuple.Pair;

public class MnsTaskFactory {

	public static Runnable buildSendTask(MessageDto messageDto, MnsExecutor executor) {
		Pair<ResourceType, String> mnsRef = parseMnsRef(messageDto.getMnsRef());
		ResourceType resourceType = mnsRef.getLeft();
		String resourceName = mnsRef.getRight();
		return (ResourceType.queue == resourceType) ?
				(new QueueSendTask(messageDto, executor, resourceName))
				: (new TopicSendTask(messageDto, executor, resourceName));
	}

	public static ReceiveTaskExecutor buildReceiveTask(MnsListener mnsListener, MnsExecutor executor) {
		Pair<ResourceType, String> mnsRef = parseMnsRef(mnsListener.getMnsRef());
		ResourceType resourceType = mnsRef.getLeft();
		String resourceName = mnsRef.getRight();
		if (ResourceType.queue == resourceType) {
			return new QueueReceiveTaskExecutor(mnsListener, executor, resourceName);
		} else {
			throw new UnsupportedOperationException("Build recevie task error, because the mnsRef is supported: mnsRef:" + mnsListener.getMnsRef());
		}
	}

	private static Pair<ResourceType, String> parseMnsRef(String mnsRef) {
		String[] typeAndName = mnsRef.split(":");
		if (typeAndName.length != 2 || ResourceType.valueOf(typeAndName[0].toLowerCase()) == null) {
			throw new IllegalStateException("Build task error, because the mnsRef is illegal, mnsRef:" + mnsRef);
		}
		ResourceType resourceType = ResourceType.valueOf(typeAndName[0].toLowerCase());
		String resourceName = typeAndName[1];
		return Pair.of(resourceType, resourceName);
	}

}

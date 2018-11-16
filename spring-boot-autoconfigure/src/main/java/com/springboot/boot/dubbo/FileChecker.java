package com.springboot.boot.dubbo;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

public abstract class FileChecker {
	private static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	public static boolean checkFileExists(String antPath) {
		boolean hasConfigFiles = false;
		try {
			Resource[] configFiles = resolver.getResources(antPath);

			for (Resource r : configFiles) {
				if (r.exists()) {
					hasConfigFiles = true;
					break;
				}
			}
		} catch (IOException e) {

		}
		return hasConfigFiles;
	}
}

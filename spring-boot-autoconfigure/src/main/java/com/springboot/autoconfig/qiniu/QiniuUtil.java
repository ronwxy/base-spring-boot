package com.springboot.autoconfig.qiniu;

import com.google.common.base.Strings;
import com.springboot.common.jackson.JsonUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class QiniuUtil {

	private static final Logger logger = LoggerFactory.getLogger(QiniuUtil.class);
	private static Auth _qiniuAuth;
	private static UploadManager _qiniuUploudManager;
	private static OperationManager _qiniuOperationManager;
	private static String _qiniuPipeline;
	private static Long _tokenExpiredTime;
	private static List<QiniuBucket> _buckets;

	private QiniuUtil() {
	}

	public static void init(String accessKey, String secretKey, String qiniuPipeLine, String tokenExpiredTime, List<QiniuBucket> buckets) {
		_qiniuAuth = Auth.create(accessKey, secretKey);
		_qiniuPipeline = qiniuPipeLine;
		_qiniuUploudManager = new UploadManager();
		_qiniuOperationManager = new OperationManager(_qiniuAuth);

		_tokenExpiredTime = Long.parseLong(tokenExpiredTime.trim());
		_buckets = buckets;
	}


	public static String getUpToken(String bucketName) {
		return _qiniuAuth.uploadToken(getBucketName(bucketName));
	}

	public static String getUpToken(String key, String bucketName) {
		return _qiniuAuth.uploadToken(getBucketName(bucketName), key);
	}

	public static String getBucketName(String bucketName) {
		QiniuBucket bucket = _findByBucketName(bucketName);
		if (bucket != null)
			return bucket.getBucketName();
		else
			throw new IllegalArgumentException("bucket is null for the name:" + bucketName);
	}

	public static String getBucketUrl(String bucketName) {
		QiniuBucket bucket = _findByBucketName(bucketName);
		return bucket != null ? (String.format("%s://%s/", bucket.getScheme(), bucket.getBucketHost())) : null;
	}


	public static String upload(File file, String key, String mimeType, String bucketName) throws QiniuException {
		String token = getUpToken(key, bucketName);
		String newKey;
		Response res = _qiniuUploudManager.put(file, key, token, null, mimeType, true);
		if (Strings.isNullOrEmpty(key) && res.isJson()) {
			Map<String, Object> jsonResult = JsonUtil.getJsonObj(res.bodyString());
			newKey = MapUtils.getString(jsonResult, "key");
		} else {
			newKey = key;
		}
		return getBucketUrl(bucketName) + newKey;

	}

	public static String stripKeyFromUrl(String url) {
		if (Strings.isNullOrEmpty(url))
			return url;

		try {
			URI uri = new URI(url);
			return _findByBucketHost(uri.getHost()) != null ? uri.getPath() : null;
		} catch (URISyntaxException e) {
			return null;
		}
	}


	public static String pretendUrlToken(String url, long expireSeconds) {
		if (isPrivateResource(url))
			return _qiniuAuth.privateDownloadUrl(unpretendUrlToken(url), TimeUnit.SECONDS.toMillis(expireSeconds));
		else
			return url;
	}

	public static String unpretendUrlToken(String url) {
		String clearUrl = url;
		if (url != null && url.length() > 0) {
			try {
				URI uri = new URI(url);
				String clearQuery = uri.getQuery();
				if (uri.getQuery() != null && uri.getQuery().length() > 0) {
					clearQuery = Stream.of(uri.getQuery().split("&")).filter(qname -> !(qname.startsWith("e=") || qname.startsWith("token="))).collect(Collectors.joining("&"));
				}
				URI clearUri = new URI(uri.getScheme(), null, uri.getHost(), -1, uri.getPath(), clearQuery, uri.getFragment());
				clearUrl = clearUri.toASCIIString();
			} catch (URISyntaxException ex) {
			}
		}
		return clearUrl;
	}

	public static String pretendUrlToken(String url) {
		return pretendUrlToken(url, _tokenExpiredTime);
	}

	public static void fop(String originalUrl, String newUrl, String fops, String projectFlag) throws QiniuException {
		String originalKey = stripKeyFromUrl(originalUrl);
		if (originalKey != null) {
			String newKey = stripKeyFromUrl(newUrl);
			String urlbase64 = UrlSafeBase64.encodeToString(getBucketName(projectFlag) + ":" + newKey);
			String pfops = fops + "|saveas/" + urlbase64;
			StringMap params = new StringMap();
			if (!Strings.isNullOrEmpty(_qiniuPipeline)) {
				params.putWhen("force", 1, true).putNotEmpty("persistentPipeline", _qiniuPipeline);
			}
			String persistid = _qiniuOperationManager.pfop(getBucketName(projectFlag), originalKey, pfops, params);
			logger.info("originalUrl:{}, newUrl:{}, fops:{}, persistid:{}", originalUrl, newUrl, fops, persistid);

		}
	}

	private static boolean isPrivateResource(String url) {
		if (Strings.isNullOrEmpty(url))
			return false;

		try {
			URI uri = new URI(url);
			QiniuBucket qiniuBucket = _findByBucketHost(uri.getHost());
			return qiniuBucket != null && qiniuBucket.isBucketPrivate();
		} catch (Exception e) {
			return false;
		}

	}

	private static QiniuBucket _findByBucketName(String bucketName) {
		return _buckets.stream().filter(bucket -> bucket.getBucketName().equalsIgnoreCase(bucketName)).findFirst().orElse(null);
	}

	private static QiniuBucket _findByBucketHost(String bucketHost) {
		return _buckets.stream().filter(bucket -> bucket.getBucketHost().equalsIgnoreCase(bucketHost)).findFirst().orElse(null);
	}

	public static class QiniuBucket {
		private String bucketName;
		private String bucketHost;
		private boolean bucketPrivate;
		private String scheme;

		public String getBucketName() {
			return bucketName;
		}

		public void setBucketName(String bucketName) {
			this.bucketName = bucketName;
		}

		public String getBucketHost() {
			return bucketHost;
		}

		public void setBucketHost(String bucketHost) {
			this.bucketHost = bucketHost;
		}

		public boolean isBucketPrivate() {
			return bucketPrivate;
		}

		public void setBucketPrivate(boolean bucketPrivate) {
			this.bucketPrivate = bucketPrivate;
		}

		public String getScheme() {
			return scheme;
		}

		public void setScheme(String scheme) {
			this.scheme = scheme;
		}
	}


}

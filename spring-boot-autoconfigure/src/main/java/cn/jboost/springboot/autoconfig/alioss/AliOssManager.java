package cn.jboost.springboot.autoconfig.alioss;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.jboost.springboot.common.util.FileUtil;
import cn.jboost.springboot.common.util.ContentTypeUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yuxk
 * @version V1.0
 * @Title: 阿里oss服务
 * @Description:
 * @date 2019/9/18 15:27
 */
@Slf4j
public class AliOssManager {

    private AliOssProperties properties;

    public AliOssManager(AliOssProperties properties) {
        Assert.notNull(properties, "aliyun oss properties is null.");
        Assert.notNull(properties.getAccessKeyId(), "aliyun oss accessKeyId is not set.");
        Assert.notNull(properties.getAccessKeySecret(), "aliyun oss accessKeySecret is not set.");
        Assert.notNull(properties.getEndpoint(), "aliyun oss endpoint is not set.");
        this.properties = properties;
    }

    /**
     * 获取上传签名
     *
     * @param bucketName  bucket名称
     * @param dir         用户上传文件时指定的前缀（或目录）
     * @param callbackUrl 上传回调服务器的URL， 不需回调设置为null
     * @param expire      多久之后过期，单位秒
     * @return
     */
    public Map<String, String> getUplodPolicy(String bucketName, String dir, String callbackUrl, long expire) {
        OSSClient client = createOssClient();
        try {
            long expireEndTime = System.currentTimeMillis() + expire * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("accessid", properties.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", properties.getEndpoint().replace("https://", "https://" + bucketName + "."));
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

            if (StringUtils.isNotEmpty(callbackUrl)) {
                JSONObject jasonCallback = new JSONObject();
                jasonCallback.put("callbackUrl", callbackUrl);
                jasonCallback.put("callbackBody",
                        "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
                jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
                String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
                respMap.put("callback", base64CallbackBody);
            }

            return respMap;
        } catch (Exception e) {
            log.warn("generate policy failed.", e);
            return null;
        } finally {
            client.shutdown();
        }
    }

    /**
     * 对对象列表的某些url属性进行签名
     *
     * @param list       需要进行处理的bean对象列表
     * @param urlFields  需要进行签名的字段名称列表
     * @param bucketName
     * @param expire     签名多久失效，单位秒
     * @param <T>
     */
    public <T> void signUrl(List<T> list, List<String> urlFields, String bucketName, long expire) {
        OSSClient client = createOssClient();
        long expireTime = System.currentTimeMillis() + expire * 1000;
        for (T entity : list) {
            internalSignUrl(entity, urlFields, bucketName, client, expireTime);
        }
        client.shutdown();
    }

    /**
     * 对对象的某些url属性进行签名
     *
     * @param entity
     * @param urlFields  需要签名的属性名列表
     * @param bucketName
     * @param expire     多久后超时，单位秒
     * @param <T>
     */
    public <T> void signUrl(T entity, List<String> urlFields, String bucketName, long expire) {
        OSSClient client = createOssClient();
        long expireTime = System.currentTimeMillis() + expire * 1000;
        internalSignUrl(entity, urlFields, bucketName, client, expireTime);
        client.shutdown();
    }

    private <T> void internalSignUrl(T entity, List<String> urlFields, String bucketName, OSSClient client, long expireTime) {
        for (String urlField : urlFields) {
            try {
                Field field = entity.getClass().getDeclaredField(urlField);
                if (Objects.nonNull(field) && StringUtils.isNotBlank(String.valueOf(field.get(entity)))) {
                    field.setAccessible(true);
                    URL signedUrl = client.generatePresignedUrl(bucketName, String.valueOf(field.get(entity)), new Date(expireTime));
                    if (signedUrl != null) {
                        field.set(entity, signedUrl.toString());
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("fail to sign url.", e);
            }
        }
    }

    /**
     * 查bucket某文件夹下所有文件
     *
     * @param bucketName
     * @param keyPrefix
     * @return 所有文件信息
     */
    public List<OSSObjectSummary> listObjects(String bucketName, String keyPrefix) {
        OSSClient ossClient = createOssClient();
        ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withPrefix(keyPrefix).withMaxKeys(100));
        ossClient.shutdown();
        return objectListing.getObjectSummaries();
    }


    /**
     * 查bucket某文件夹下所有文件名
     *
     * @param keyPrefix
     * @return
     */
    public List<String> listObjectsKey(String bucketName, String keyPrefix) {
        List<OSSObjectSummary> list = listObjects(bucketName, keyPrefix);
        List<String> result = new ArrayList<>();
        list.forEach(o -> result.add(o.getKey()));
        return result;
    }

    /**
     * 查默认bucket某文件夹下所有文件名
     *
     * @param keyPrefix
     * @return
     */
    public List<String> listObjectsKey(String keyPrefix) {
        return listObjectsKey(properties.getBucket(), keyPrefix);
    }

    /**
     * 简单上传至某bucket
     *
     * @param bucketName
     * @param objectName
     * @param inputStream
     */
    public void putObject(String bucketName, String objectName, InputStream inputStream) {
        // 创建OSSClient实例。
        OSSClient ossClient = createOssClient();
        String extendName = FileUtil.getExtensionName(objectName);
        String contentType = ContentTypeUtil.getContentTypeByExpansion(extendName);
        contentType = StringUtils.isNotEmpty(contentType) ? contentType : null;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        ossClient.putObject(bucketName, objectName, inputStream, metadata);
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /**
     * 简单上传至某默认bucket
     *
     * @param objectName
     * @param inputStream
     */
    public void putObject(String objectName, InputStream inputStream) {
        putObject(properties.getBucket(), objectName, inputStream);
    }

    /**
     * 获取Object
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public OSSObject getObject(String bucketName, String objectName) {
        OSSClient ossClient = createOssClient();
        OSSObject obj = ossClient.getObject(bucketName, objectName);
        ossClient.shutdown();
        return obj;
    }

    /**
     * 获取默认Object
     *
     * @param objectName
     * @return
     */
    public OSSObject getObject(String objectName) {
        return getObject(properties.getBucket(), objectName);
    }

    /**
     * 简单上传至某bucket
     *
     * @param bucketName
     * @param objectName
     * @param file
     */
    public void putObject(String bucketName, String objectName, File file) {
        // 创建OSSClient实例。
        OSSClient ossClient = createOssClient();
        ossClient.putObject(bucketName, objectName, file);
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /**
     * 简单上传至默认bucket
     *
     * @param objectName
     * @param file
     */
    public void putObject(String objectName, File file) {
        // 创建OSSClient实例。
        OSSClient ossClient = createOssClient();
        ossClient.putObject(properties.getBucket(), objectName, file);
        // 关闭OSSClient。
        ossClient.shutdown();
    }


    /**
     * 删除某bucket下的指定文件
     *
     * @param bucketName
     * @param objectName
     */
    public void deleteObject(String bucketName, String objectName) {
        OSSClient ossClient = createOssClient();
        ossClient.deleteObject(bucketName, objectName);
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /**
     * 删除默认bucket下的指定文件
     *
     * @param objectName
     */
    public void deleteObject(String objectName) {
        OSSClient ossClient = createOssClient();
        ossClient.deleteObject(properties.getBucket(), objectName);
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /**
     * 获取某bucket分段上传, 上传完成后调用 {@link AppendObjectStream#shutdown} 关闭客户端
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public AppendObjectStream getAppendObjectStream(String bucketName, String objectName) {
        OSSClient ossClient = createOssClient();
        return new AppendObjectStream(ossClient, bucketName, objectName);
    }

    /**
     * 自定义分段上传类
     */
    public static class AppendObjectStream {
        private OSSClient ossClient;
        private AppendObjectRequest appendObjectRequest;
        private AppendObjectResult appendObjectResult;
        private String bukectName;
        private String objectName;

        public AppendObjectStream(OSSClient ossClient, String bukectName, String objectName) {
            this.ossClient = ossClient;
            this.bukectName = bukectName;
            this.objectName = objectName;
        }

        public void append(String str) {
            append(str.getBytes());
        }

        public void append(byte[] b) {
            append(b, 0, b.length);
        }

        public void append(byte[] b, int offset, int len) {
            append(new ByteArrayInputStream(b, offset, len));
        }

        /**
         * 分段上传
         *
         * @param bai
         */
        public void append(ByteArrayInputStream bai) {
            if (appendObjectRequest == null) {
                ObjectMetadata metadata = new ObjectMetadata();
                String extend = FileUtil.getExtensionName(objectName);
                String contentType = ContentTypeUtil.getContentTypeByExpansion(extend);
                contentType = StringUtils.isNotEmpty(contentType) ? contentType : null;
                metadata.setContentType(contentType);
                appendObjectRequest = new AppendObjectRequest(bukectName, objectName, bai, metadata);
                appendObjectRequest.setPosition(0L);
            } else {
                appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
                appendObjectRequest.setInputStream(new ByteArrayInputStream("".getBytes()));
            }
            appendObjectResult = ossClient.appendObject(appendObjectRequest);
        }

        public void shutdown() {
            ossClient.shutdown();
        }

    }

    private OSSClient createOssClient() {
        return new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
    }

    /**
     * 文件保存
     *
     * @param bizType 文件类型
     * @param mf
     * @return
     * @throws IOException
     */
    public String fileSave(String bizType, MultipartFile mf) throws IOException {
        // 获取文件名
        String orgName = mf.getOriginalFilename();
        String fileExt = FileUtil.getExtensionName(orgName);
        String dbpath = bizType + File.separator + IdUtil.simpleUUID() + "." + fileExt;
        if (dbpath.contains("\\")) {
            dbpath = dbpath.replace("\\", "/");
        }
        log.info("--------文件{}正在保存---------", dbpath);
        putObject(dbpath, mf.getInputStream());
        return dbpath;
    }

    /**
     * 文件保存
     *
     * @param bizType 文件类型
     * @param file
     * @return
     * @throws IOException
     */
    public String fileSave(String bizType, File file) {
        String orgName = file.getName();
        String fileExt = FileUtil.getExtensionName(orgName);
        String dbpath = bizType + File.separator + IdUtil.simpleUUID() + "." + fileExt;
        if (dbpath.contains("\\")) {
            dbpath = dbpath.replace("\\", "/");
        }
        log.info("--------文件{}正在保存---------", dbpath);
        putObject(dbpath, file);
        return dbpath;
    }
}

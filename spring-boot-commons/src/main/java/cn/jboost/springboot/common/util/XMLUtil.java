package cn.jboost.springboot.common.util;

import org.dom4j.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ray4work@126.com
 * @date 2018/5/22 9:21
 */
public class XMLUtil {

    private static final String SEPARATOR = " ";

    private XMLUtil() {
    }

    /**
     * 将xml文本转换为map对象, 节点的参数以"@节点名"为key，以"{参数名=参数值}"格式存储
     *
     * @param xmlStr
     * @return
     */
    public static Map<String, Object> xml2Map(String xmlStr) throws DocumentException {
        Map<String, Object> map = new LinkedHashMap<>();
        Document doc = DocumentHelper.parseText(xmlStr);
        Element rootElement = doc.getRootElement();
        xml2Map(rootElement, map);
        return map;
    }

    private static void xml2Map(Element element, Map<String, Object> map) {
        if (element == null || map == null) {
            return;
        }
        Iterator<Element> elementItr = element.elementIterator();
        String key = element.getName();
        Iterator<Attribute> attrItr = element.attributeIterator(); // 参数
        if (!elementItr.hasNext()) {
            String value = element.getTextTrim();
            map.put(key, value);
        } else {
            Map<String, Object> childMap = new LinkedHashMap<>();
            while (elementItr.hasNext()) {
                map.put(key, childMap);
                Element childElement = elementItr.next();
                xml2Map(childElement, childMap);
            }
        }
        if (attrItr.hasNext()) {
            Map<String, Object> attrMap = new LinkedHashMap<>();
            while (attrItr.hasNext()) {
                Attribute attribute = attrItr.next();
                attrMap.put(attribute.getName(), attribute.getValue());
            }
            map.put("@" + key, attrMap); //将参数以 @key的键存储
        }
    }

    /**
     * map转xml文本
     *
     * @param map
     * @param rootNode 根节点名称
     * @return
     */
    public static String map2Xml(Map<String, Object> map, String rootNode) {
        if (map == null) {
            return null;
        }
        StringBuilder stb = new StringBuilder("<").append(rootNode).append(">");
        map2Xml(stb, map);
        stb.append("</").append(rootNode).append(">");
        return stb.toString();
    }

    private static void map2Xml(StringBuilder stb, Map<String, Object> map) {
        Iterator<?> i = map.entrySet().iterator();
        StringBuilder childStb = new StringBuilder();
        while (i.hasNext()) {
            Map.Entry<String, Object> e = (Map.Entry<String, Object>) i.next();
            String key = e.getKey();
            if (key.indexOf('@') >= 0) {
                continue;
            }
            childStb.append("<").append(key);
            String attr = "@" + key;
            Map<String, Object> attrMap = (Map<String, Object>) map.get(attr);
            if (attrMap != null) {
                Iterator<?> attrIterator = attrMap.entrySet().iterator();
                while (attrIterator.hasNext()) {
                    Map.Entry<String, Object> attrEntry = (Map.Entry<String, Object>) attrIterator
                            .next();
                    String attrKey = attrEntry.getKey();
                    String attrValue = (String) attrEntry.getValue();
                    childStb.append(SEPARATOR).append(attrKey).append("=")
                            .append("\"").append(attrValue).append("\"");
                }
            }
            childStb.append(">");
            Object value = e.getValue();
            if (value instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) value;
                for (Map<String, Object> valueMap : list) {
                    map2Xml(childStb, valueMap);
                }
            } else if (value instanceof Map) {
                Map<String, Object> valueMap = (Map<String, Object>) value;
                map2Xml(childStb, valueMap);
            } else {
                if (value != null) {
                    childStb.append(value);
                } else {
                    childStb.append("");
                }
            }
            childStb.append("</").append(key).append(">");
        }
        stb.append(childStb);
    }

}

package com.cjz.webmvc.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-19 11:35
 */
@Slf4j
  class JsonTest {
    @Test
    void t1() throws Exception{
        final ObjectMapper mapper = new ObjectMapper();
        final Par par = new Par();
        par.setAppName("test");
        par.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli( System.currentTimeMillis()), ZoneOffset.of("+8")).toString());
        par.setVersion("v1.0");
        par.setMessage("测试一下");
        final HashMap<String, String> hashMap = new HashMap<>(4);
        hashMap.put("1111", " 1111");
        hashMap.put("2222", " 2222");
        par.setExtendMap(hashMap);

        // 对象转 json 字符串
          String jsonStr = mapper.writeValueAsString(par);
        log.info("JsonStr: {}", jsonStr);

        // 转map
      //  Map<String, String> map = mapper.readValue(jsonStr, new TypeReference<Map<String, String>>() {});
        Map<String, Object> map = mapper.convertValue(jsonStr, new TypeReference<Map<String, Object>>() {});
        map.put("a", "a");
        map.put("b", "b");
        log.info("Map : {}", map);

        // 转JsonObject
        final ObjectNode objectNode = mapper.valueToTree(par);
        log.info("ObjectNode : {}", objectNode);

        final Par par1 = mapper.readValue(objectNode.traverse(), Par.class);
        log.info("New Par1 : {}", par1);

        objectNode.put("a", "a");
        objectNode.put("b", "b");

        jsonStr = objectNode.toString();
        log.info("JsonStr: {}", jsonStr);
        final Par par2 =   mapper.readValue(jsonStr, Par.class);
        log.info("New Par2 : {}", par2);
    }
}

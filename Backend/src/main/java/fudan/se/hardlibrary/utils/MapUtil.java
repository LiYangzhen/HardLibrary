package fudan.se.hardlibrary.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapUtil {

    /**
     * generate <Sting, String> map
     */
    public Map<String, String> getMessageMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
}

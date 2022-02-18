package fudan.se.hardlibrary.security.jwt;

import com.alibaba.fastjson.JSON;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("message", "Authority insufficient");
        response.getWriter().write(JSON.toJSONString(map));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
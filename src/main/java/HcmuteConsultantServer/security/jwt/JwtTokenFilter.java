package HcmuteConsultantServer.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import HcmuteConsultantServer.model.exception.JWT401Exception;
import HcmuteConsultantServer.security.authentication.UserDetailService;
import HcmuteConsultantServer.service.implement.common.StatusOnlineServiceImpl;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailService userDetailService;
 
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private StatusOnlineServiceImpl commonStatusOnlineServiceImpl;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/") || 
               path.startsWith("/login") || 
               path.startsWith("/auth/") ||
               path.startsWith("/public/") ||
               path.equals("/") ||
               path.startsWith("/ws/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = null;
        try {
            String token = getJwt(request);
            if (token != null && jwtProvider.validateToken(token)) {
                email = jwtProvider.getEmailFromToken(token);
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                commonStatusOnlineServiceImpl.updateStatus(email, true);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (JWT401Exception e) {
            if (email != null) {
                commonStatusOnlineServiceImpl.updateStatus(email, false);
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> body = new HashMap<>();
            body.put("status", e.getStatus());
            body.put("message", e.getMessage());
            body.put("type", e.getType());

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);

            return;
        } catch (Exception e) {
            logger.error("Không thể đặt xác thực vào SecurityContext", e);
        }

        filterChain.doFilter(request, response);
    }

    private void updateOnlineStatus(String email) {
        commonStatusOnlineServiceImpl.updateStatus(email, true);
    }


    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

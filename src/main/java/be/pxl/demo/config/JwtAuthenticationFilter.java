package be.pxl.demo.config;

import be.pxl.demo.security.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LogManager.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtilities jwtUtilities;
    private final CustomUserDetailsService customerUserDetailsService;

    public JwtAuthenticationFilter(JwtUtilities jwtUtilities, CustomUserDetailsService customerUserDetailsService) {
        this.jwtUtilities = jwtUtilities;
        this.customerUserDetailsService = customerUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtUtilities.getToken(request);
        try {
            if (token != null && jwtUtilities.validateToken(token)) {
                String email = jwtUtilities.extractUsername(token);
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                    LOGGER.info("authenticated user with email :{}", email);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) { // ✅ Handle Expired Token
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Authorization header is expired.");
            return;
        }  catch (JwtException e) { // ✅ Handle Other JWT Issues
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is invalid.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{\"error\": \"" + message + "\"}");
    }
}

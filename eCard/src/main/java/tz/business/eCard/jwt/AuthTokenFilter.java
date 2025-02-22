package tz.business.eCard.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tz.business.eCard.userDetailService.UserDetailServiceImpl;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Autowired
    private JWTutils jwtutils;

    private UserDetailServiceImpl userDetailServiceImpl;
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtutils.validateJwtToken(jwt)) {
                String username = jwtutils.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception e) {
            log.warn("failed to set auth token", e);
            e.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {

        return request.getServletPath().startsWith("/auth/**")
                || request.getServletPath().startsWith("/gui/**")
                || request.getServletPath().startsWith("/swagger-ui/**")
                || request.getServletPath().startsWith("/v3/**")
                || request.getServletPath().startsWith("/files/**")
                || request.getServletPath().startsWith("/uploads/**");
    }

    private String parseJwt(@NotNull HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.replace("Bearer ", "").trim();
        }
        return null;
    }

}

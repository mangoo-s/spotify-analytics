package com.example.spotifyscrobble.users.components;

import com.example.spotifyscrobble.users.entity.UserEntity;
import com.example.spotifyscrobble.users.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class UsernameFilter extends OncePerRequestFilter {

    private final UserService userService;
    private static final Set<String> EXEMPT_PATHS = Set.of(
            "/user/profile/me", "/user/profile/username"
    );
    private final PathMatcher pathMatcher;

    public UsernameFilter(UserService userService, PathMatcher pathMatcher) {
        this.userService = userService;
        this.pathMatcher = pathMatcher;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        String path = request.getServletPath();
        return EXEMPT_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt){
            UserEntity user = userService.createUserProfile(jwt);
            if(!user.isComplete()){
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("Error. Username required.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}

package org.example.apiusuarios.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Cache de buckets por IP con expiración tras 30 min sin uso.
    // Evita el memory leak del ConcurrentHashMap original.
    private final Cache<String, Bucket> globalBuckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .maximumSize(10_000)
            .build();

    private final Cache<String, Bucket> loginBuckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .maximumSize(10_000)
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = resolveClientIp(request);

        // Bucket más estricto para el endpoint de login (anti brute-force).
        if (isLoginRequest(request)) {
            Bucket loginBucket = loginBuckets.get(ip, k -> createLoginBucket());
            if (!loginBucket.tryConsume(1)) {
                rechazar(response, "Demasiados intentos de login. Espera unos minutos.");
                return;
            }
        }

        Bucket bucket = globalBuckets.get(ip, k -> createGlobalBucket());
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            rechazar(response, "Has superado el límite de peticiones (Rate Limit). Inténtalo más tarde.");
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // Primer hop de la cadena.
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "POST".equalsIgnoreCase(request.getMethod())
                && uri != null
                && uri.endsWith("/usuarios/login");
    }

    private void rechazar(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(mensaje);
    }

    private Bucket createGlobalBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(50)
                .refillGreedy(50, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createLoginBucket() {
        // 5 intentos por minuto por IP en el endpoint de login.
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}

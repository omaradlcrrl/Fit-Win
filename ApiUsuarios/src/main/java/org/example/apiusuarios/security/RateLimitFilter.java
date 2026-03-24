package org.example.apiusuarios.security;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Bucket bucket = cache.computeIfAbsent(ip, this::createNewBucket);

        // Intenta consumir 1 token del bucket
        if (bucket.tryConsume(1)) {
            // Si hay tokens, permite que la petición continúe al siguiente filtro (o
            // controlador)
            filterChain.doFilter(request, response);
        } else {
            // Si no hay tokens, devuelve un error 429 Too Many Requests
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Has superado el límite de peticiones (Rate Limit). Inténtalo más tarde.");
        }
    }

    private Bucket createNewBucket(String key) {
        // Límite de 50 peticiones por minuto por IP
        Bandwidth limit = Bandwidth.builder()
                .capacity(50)
                .refillGreedy(50, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

package com.example.encuestas_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(req, res);
        } finally {
            // Charset seguro para request
            Charset reqCs = request.getCharacterEncoding() != null
                    ? Charset.forName(request.getCharacterEncoding())
                    : StandardCharsets.UTF_8;

            byte[] reqBytes = req.getContentAsByteArray();
            String reqBody = new String(reqBytes, 0, reqBytes.length, reqCs);

            log.info("REQ {} {} qs='{}' body={}",
                    request.getMethod(), request.getRequestURI(), request.getQueryString(), reqBody);

            // Charset seguro para response
            Charset resCs = response.getCharacterEncoding() != null
                    ? Charset.forName(response.getCharacterEncoding())
                    : StandardCharsets.UTF_8;

            byte[] resBytes = res.getContentAsByteArray();
            String resBody = new String(resBytes, 0, resBytes.length, resCs);

            log.info("RES {} {} status={} body={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), resBody);

            // ¡Importante! devolver el cuerpo a la response real
            res.copyBodyToResponse();
        }
    }
}

package a.gleb.user_app.config.filter;

import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import a.gleb.user_app.service.oauth.AccountContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static a.gleb.user_app.constant.UserAppConstant.REQUESTOR;
import static a.gleb.user_app.constant.UserAppConstant.TRACE_ID_RESPONSE_HEADER;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final Tracer tracer;
    private final AccountContext accountContext;
    private final List<String> excludedEndpoints;

    public RequestLoggingFilter(
            Tracer tracer,
            AccountContext accountContext,
            UserAppConfigurationProperties properties
    ) {
        this.tracer = tracer;
        this.accountContext = accountContext;
        this.excludedEndpoints = properties.observability().excludedPatterns();
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var startTime = System.currentTimeMillis();
        var requestor = accountContext.getAccountLogin();

        try {
            MDC.put(REQUESTOR, requestor);

            // Propagate active `trace_id` to caller via response header for error correlation
            var span = tracer.currentSpan();
            if (span != null) {
                response.setHeader(TRACE_ID_RESPONSE_HEADER, span.context().traceId());
            }

            // Process the request
            filterChain.doFilter(request, response);
        } finally {
            // Log after request is processed
            var duration = System.currentTimeMillis() - startTime;
            var queryString = request.getQueryString();
            var uri = queryString != null
                    ? "%s?%s".formatted(request.getRequestURI(), queryString)
                    : request.getRequestURI();

            log.info(
                    "Request completed: method={} uri={} status={} duration={}ms requestor={} remoteAddr={}",
                    request.getMethod(),
                    uri,
                    response.getStatus(),
                    duration,
                    requestor,
                    request.getRemoteAddr()
            );

            // Clear MDC context
            MDC.remove(REQUESTOR);
        }
    }

    @Override
    protected boolean shouldNotFilter(
            @NonNull HttpServletRequest request
    ) {
        var currentRequestUri = request.getRequestURI();

        if (excludedEndpoints != null) {
            for (String pattern : excludedEndpoints) {
                if (pathMatcher.match(pattern, currentRequestUri)) {
                    return true;
                }
            }
        }

        return false;
    }
}

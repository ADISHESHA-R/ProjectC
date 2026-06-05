package com.attendance.system.web;

import com.attendance.system.service.SiteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminSitePathIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final SiteService siteService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AdminSitePathId.class)
            && (Long.class.equals(parameter.getParameterType())
            || long.class.equals(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("Expected HttpServletRequest");
        }
        @SuppressWarnings("unchecked")
        Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String raw = null;
        if (uriVariables != null) {
            raw = uriVariables.get("id");
            if (raw == null) {
                raw = uriVariables.get("siteId");
            }
        }
        return siteService.resolveSiteIdFromClientKey(raw);
    }
}

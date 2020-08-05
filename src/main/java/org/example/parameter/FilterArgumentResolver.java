package org.example.parameter;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FilterArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Filter.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String filterParam = parameter.getParameterAnnotation(Filter.class).value();
        String filterStr = webRequest.getParameter(filterParam);
        String pageStr = webRequest.getParameter("page");
        String sortStr = webRequest.getParameter("sort");
        return new SimpleFilterResolver(filterStr, pageStr, sortStr);
    }
}

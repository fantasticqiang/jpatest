package org.example.parameter;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 参数解析器 需要实现 HandlerMethodArgumentResolver
 */
public class FilterArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //参数上有@Filter时生效
        return parameter.hasParameterAnnotation(Filter.class);
    }

    /**
     * 从url中获取参数，生成SimpleFilterResolver
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String filterParam = parameter.getParameterAnnotation(Filter.class).value();
        String filterStr = webRequest.getParameter(filterParam);
        String pageStr = webRequest.getParameter("page");
        String sortStr = webRequest.getParameter("sort");
        return new SimpleFilterResolver(filterStr, pageStr, sortStr);
    }
}

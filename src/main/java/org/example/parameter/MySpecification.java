package org.example.parameter;

import org.example.common.Common;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MySpecification<T> implements Specification<T> {

    private ParameterCriteria parameterCriteria;

    public MySpecification(String parameterCriteriaStr) {
        this.parameterCriteria = new ParameterCriteria(parameterCriteriaStr);
    }

    /**
     * 获取查询的字段名称
     * @param root
     * @param path
     * @return
     */
    private Expression get(Root<T> root, String path) {
        String[] paths = path.split("\\.");
        Path finalPath = root.get(paths[0]);
        if (paths.length > 1) {
            for (int i = 1; i < paths.length; i++) {
                finalPath = finalPath.get(paths[i]);
            }
        }
        return finalPath;
    }

    /**
     * 封装一个查询条件
     * @param root
     * @param criteriaQuery
     * @param criteriaBuilder
     * @return
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Expression expression = get(root, parameterCriteria.getKey());
        Class javaType = expression.getJavaType();

        switch (parameterCriteria.getOperator()) {
            case greaterEqualThan:
                return criteriaBuilder.greaterThanOrEqualTo(expression, parameterCriteria.getValue(javaType, criteriaBuilder));
            case greaterThan:
                return criteriaBuilder.greaterThan(expression, parameterCriteria.getValue(javaType, criteriaBuilder));
            case lessEqualThan:
                return criteriaBuilder.lessThanOrEqualTo(expression, parameterCriteria.getValue(javaType, criteriaBuilder));
            case lessThan:
                return criteriaBuilder.lessThan(expression, parameterCriteria.getValue(javaType, criteriaBuilder));
            case equal:
                return criteriaBuilder.equal(expression, parameterCriteria.getValue(javaType, criteriaBuilder));
            case notEqual:
                return criteriaBuilder.notEqual(expression, parameterCriteria.getValue(javaType, criteriaBuilder));
            case in:
                return expression.in(Arrays.asList(parameterCriteria.getStringValue().split(Common.FILTER_SPECIFICATION_VALUE_SPLITTER)));
            case notIn:
                return criteriaBuilder.not(expression.in(Arrays.asList(parameterCriteria.getStringValue().split(Common.FILTER_SPECIFICATION_VALUE_SPLITTER))));
            case like:
                return criteriaBuilder.like(expression,"%" + parameterCriteria.getStringValue() + "%");
            case unlike:
                return criteriaBuilder.notLike(expression,"%" + parameterCriteria.getStringValue() + "%");
            case isnull:
                return criteriaBuilder.isNull(expression);
            case notnull:
                return criteriaBuilder.isNotNull(expression);
            case jspe:
                String[] jspeParamValues = parameterCriteria.getStringValue().split(Common.FILTER_SPECIFICATION_VALUE_SPLITTER);
                String jspePath = jspeParamValues[0], jspeValue = jspeParamValues[1];
                return criteriaBuilder.equal(criteriaBuilder.function("JSON_EXTRACT", String.class, root.get(parameterCriteria.getKey()), criteriaBuilder.literal(jspePath)),
                        jspeValue);
            case jos:
                String[] josParamValues = parameterCriteria.getStringValue().split(Common.FILTER_SPECIFICATION_VALUE_SPLITTER);
                String josPath = josParamValues[0], josValue = josParamValues[1];
                return criteriaBuilder.isNotNull(criteriaBuilder.function("JSON_SEARCH", String.class, root.get(parameterCriteria.getKey()),
                        criteriaBuilder.literal("one"),
                        criteriaBuilder.literal(josValue),
                        criteriaBuilder.nullLiteral(String.class),
                        criteriaBuilder.literal(josPath)));
            case jas:
                String[] jasParamValues = parameterCriteria.getStringValue().split(Common.FILTER_SPECIFICATION_VALUE_SPLITTER);
                String jasPath = jasParamValues[0], jasValue = jasParamValues[1];
                return criteriaBuilder.isNotNull(criteriaBuilder.function("JSON_SEARCH", String.class, root.get(parameterCriteria.getKey()),
                        criteriaBuilder.literal("all"),
                        criteriaBuilder.literal(jasValue),
                        criteriaBuilder.nullLiteral(String.class),
                        criteriaBuilder.literal(jasPath)));
        }
        return null;
    }


    /**
     * 一个标准查询查询参数，url中的查询条件解析成对象
     * 比如：username=张三
     */
    public static class ParameterCriteria {
        private String key;
        private EFieldOperator operator;
        private String value;

        public ParameterCriteria(String key, EFieldOperator operator, String value) {
            this.key = key;
            this.operator = operator;
            this.value = value;
        }

        private ParameterCriteria(String parameterSpecification) {
            String[] parts = parameterSpecification.split(Common.FILTER_OPERATION_SPLITTER);
            this.key = parts[0];
            this.operator = EFieldOperator.getByName(parts[1]);
            String[] valueParts = new String[parts.length - 2];
            System.arraycopy(parts, 2, valueParts, 0, valueParts.length);
            this.value = parts.length > 2 ? String.join(Common.COMMON_SPLITTER, valueParts) : "";

        }

        public String getKey() {
            return key;
        }

        public EFieldOperator getOperator() {
            return operator;
        }

        public Expression getValue(Class clazz, CriteriaBuilder cb) {
            if (clazz.isAssignableFrom(Date.class)) {
                Date result = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    result = sdf.parse(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return cb.literal(result);
            }
            return cb.literal(value);
        }

        public String getStringValue() {
            return value;
        }
    }
}

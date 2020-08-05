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

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Expression expression = get(root, parameterCriteria.getKey());
        Class javaType = expression.getJavaType();

        switch (parameterCriteria.getOperator()) {
            case greaterEqualThan:
                return criteriaBuilder.greaterThanOrEqualTo(get(root, parameterCriteria.getKey()), parameterCriteria.getValue(javaType, criteriaBuilder));
            case greaterThan:
                return criteriaBuilder.greaterThan(get(root, parameterCriteria.getKey()), parameterCriteria.getValue(javaType, criteriaBuilder));
            case lessEqualThan:
                return criteriaBuilder.lessThanOrEqualTo(get(root, parameterCriteria.getKey()), parameterCriteria.getValue(javaType, criteriaBuilder));
            case lessThan:
                return criteriaBuilder.lessThan(get(root, parameterCriteria.getKey()), parameterCriteria.getValue(javaType, criteriaBuilder));
            case equal:
                return criteriaBuilder.equal(get(root, parameterCriteria.getKey()), parameterCriteria.getValue(javaType, criteriaBuilder));
            case notEqual:
                return criteriaBuilder.notEqual(get(root, parameterCriteria.getKey()), parameterCriteria.getValue(javaType, criteriaBuilder));
            case in:
                Expression expression1 = get(root, parameterCriteria.getKey());
                expression1.in(parameterCriteria.getStringValue().split(Common.FILTER_SPECIFICATION_VALUE_SPLITTER));
                return criteriaBuilder.in(expression1);
            case notIn:
                Expression expression2 = get(root, parameterCriteria.getKey());
                expression2.in(parameterCriteria.getStringValue().split(Common.FILTER_SPECIFICATION_VALUE_SPLITTER));
                return criteriaBuilder.not(expression2);
            case like:
                return criteriaBuilder.like(get(root, parameterCriteria.getKey()),"%" + parameterCriteria.getStringValue() + "%");
            case unlike:
                return criteriaBuilder.notLike(get(root, parameterCriteria.getKey()),"%" + parameterCriteria.getStringValue() + "%");
            case isnull:
                return criteriaBuilder.isNull(get(root, parameterCriteria.getKey()));
            case notnull:
                return criteriaBuilder.isNotNull(get(root, parameterCriteria.getKey()));
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

package org.example.parameter;

import org.apache.commons.lang.StringUtils;
import org.example.common.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

/**
 * 接收传过来的url参数，封装成specification查询条件
 * @param <T>
 */
public class SimpleFilterResolver<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String filterString;

    private String pageableString;

    private String sortString;

    public SimpleFilterResolver(String filterString, String pageableString, String sortString) {
        this.filterString = filterString;
        this.pageableString = pageableString;
        this.sortString = sortString;
    }

    //生成查询语句
    public <T> Specification<T> genSpecification() {
        Specification<T> orSpec = null;
        for (String orString : filterString.split(Common.FILTER_SPECIFICATION_OR_SPLITTER)) {
            String[] split = orString.split(Common.FILTER_SPECIFICATION_AND_SPLITTER);
            if (split.length >= 1) {
                Specification<T> mySpec = null;
                for (int i = 0; i < split.length; i++) {
                    if (mySpec == null) {
                        if (StringUtils.isNotBlank(split[i])) {
                            mySpec = new MySpecification<T>(split[i]);
                        }
                    } else {
                        if (StringUtils.isNotBlank(split[i])) {
                            mySpec = mySpec.and(new MySpecification<T>(split[i]));
                        }
                    }
                }
                if (mySpec != null) {
                    if (orSpec == null) {
                        orSpec = mySpec;
                    } else {
                        orSpec = orSpec.or(mySpec);
                    }
                }
            }
        }
        return orSpec;
    }

    //生成分页
    public PageRequest genPageRequest() {
        int start = 0;
        int size = 10;
        if (StringUtils.isNotBlank(pageableString)) {
            String[] split = pageableString.split(Common.COMMON_SPLITTER);
            if (split != null && split.length >= 2) {
                try {
                    Integer startTmp = Integer.valueOf(split[0]);
                    Integer sizeTmp = Integer.valueOf(split[1]);
                    if (startTmp >= 0) {
                        start = startTmp;
                    }
                    if (sizeTmp >= 1) size = sizeTmp;
                } catch (Exception e) {
                    logger.error("分页数据转换失败");
                }
            }
        }
        ArrayList<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.isNotBlank(sortString)) {
            String[] split = sortString.split(Common.FILTER_SPECIFICATION_AND_SPLITTER);
            for (String sortStr : split) {
                String finalSortStr = null;
                boolean asc = true;
                if (sortStr.startsWith("-")) {
                    asc = false;
                    finalSortStr = sortStr.substring(1);
                } else if (sortStr.startsWith("+")) {
                    finalSortStr = sortStr.substring(1);
                } else {
                    finalSortStr = sortStr;
                }
                if (StringUtils.isNotBlank(finalSortStr)) {
                    orders.add(new Sort.Order(asc ? Sort.Direction.ASC : Sort.Direction.DESC, finalSortStr));
                }
            }
        }
        return PageRequest.of(start, size, Sort.by(orders));
    }
}

package org.example.parameter;

import java.lang.annotation.*;

//声明自定义标签用在参数上
@Target({ElementType.PARAMETER})
//声明标签保留到vm运行期
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filter {
    /**
     * 说明：@interface 自定义标签
     * @return
     */
    String value() default "filter";
}

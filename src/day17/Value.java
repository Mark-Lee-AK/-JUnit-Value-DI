package day17;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // 注解目标：成员变量
@Retention(RetentionPolicy.RUNTIME) // 保留范围：运行期内存
public @interface Value {
	String name() default ""; // 属性名格式：${xxx.xxx.xxx}
	String value() default "";// 属性名别名
}

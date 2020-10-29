package sqlsession;

import java.lang.annotation.*;

//自己创建一个注解类型
//通过元注解来描述这个自定义注解
//元注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQL {
    String sql();
    SQLEnums type();
    Class resultType() default Object.class;
}

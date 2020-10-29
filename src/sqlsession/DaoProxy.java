package sqlsession;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

//这个类是Dao类对象的代理
public class DaoProxy {

    public static Object getInstance(Class clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new MethodProxy());
    }


    //内部类
    private static class MethodProxy implements InvocationHandler{

        /**
         * 代理怎么干活
         * @param proxy 代理对象
         * @param method 代理方法对象
         * @param args 代理方法里传递的参数
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())){     //证明现在的XXXDao不是一个接口
                return method.invoke(this,args);
            }else{
                //传进来的是一个接口
                //代理需要干活
                //读取到SQL
                SQL sql = method.getAnnotation(SQL.class);
                //告诉SqlSessionFactory去执行
                return execute(sql,args);
            }

        }


        //负责判断到底该告诉sqlSessionFactory去执行哪个具体的操作
        public  Object execute(SQL sql,Object[] args) throws Exception {
            if(sql != null){
                switch(sql.type()){
                    case INSERT:
                        return SqlSessionFactory.insert(sql.sql(),args);
                    case DELETE:
                        return SqlSessionFactory.delete(sql.sql(),args);
                    case UPDATE:
                        return SqlSessionFactory.update(sql.sql(),args);
                    case SELECT:
                        return SqlSessionFactory.selectOne(sql.sql(),args,sql.resultType());
                }
            }
            return null;
        }


    }
}

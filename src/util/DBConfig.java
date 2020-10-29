package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件dbconfig.properties的类
 */
public class DBConfig {
    private static Properties properties;

    static{

        try {
            properties = new Properties();
            //InputStream in = new FileInputStream("dbconfig.properties");
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("dbconfig.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getConfig(String key){
        return properties.getProperty(key,"");
    }

    public static String getConfig(String key,String defaultValue){
        return properties.getProperty(key,defaultValue);
    }
    public static Integer getIntegerValue(String key){
        return Integer.parseInt(properties.getProperty(key,""));
    }
    public static Integer getIntegerValue(String key,String defaultValue){
        return Integer.parseInt(properties.getProperty(key,defaultValue));
    }
}

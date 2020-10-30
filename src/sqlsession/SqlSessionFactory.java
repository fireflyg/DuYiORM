package sqlsession;


import util.ConnectionPool;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSessionFactory {
    //ceshi**************

    //****************************************
    //这个类的目的是为了帮所有的Dao处理冗余的jdbc操作

    //查询一行记录
    //好多值ResultSet----将ResultSet的信息取出来     存在一个新的小容器里
    //数组   集合List Set Map<key,value>
    //对象domain   类名--表名   属性名--列名     属性类型--列类型
    //设计一个方法   可以处理任何一个表格的任何一条数据  修改 删除  新增
    //参数？ sql  ？？？？
    public static int update(String sql,Object[] values) throws Exception {
        Connection connection = ConnectionPool.getConnection();
        //创建状态参数
        PreparedStatement pstat = connection.prepareStatement(sql);
        //需要将sql和？信息拼接完整
        for(int i = 0; i < values.length; i++){
            pstat.setObject(i+1,values[i]);
        }
        //执行修改操作
        int count = pstat.executeUpdate();
        pstat.close();
        connection.close();         //在这里底层相当于释放
        return count;

    }



    public static int delete(String sql,Object[] values) throws Exception {
        Connection connection = ConnectionPool.getConnection();
        //创建状态参数
        PreparedStatement pstat = connection.prepareStatement(sql);
        //需要将sql和？信息拼接完整
        for(int i = 0; i < values.length; i++){
            pstat.setObject(i+1,values[i]);
        }
        //执行修改操作
        int count = pstat.executeUpdate();
        pstat.close();
        connection.close();         //在这里底层相当于释放
        return count;

    }





    public static long insert(String sql,Object[] values) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        //创建状态参数
        PreparedStatement pstat = connection.prepareStatement(sql);
        //需要将sql和？信息拼接完整
        for(int i = 0; i < values.length; i++){
            pstat.setObject(i+1,values[i]);
        }
        //执行修改操作
        pstat.executeUpdate();
        ResultSet rs = pstat.getGeneratedKeys();//这个方法可以获取一个结果集，这个结果集里面存放着我们刚刚新增的数据
        //当我们新增的数据存入到了数据库当中，rs结果集中就会存在数据
        if(rs.next()){
            //通常结果集的第一列为主键，所有我们将新增数据的主键返回出去
             return rs.getLong(1);       //因为主键id一般情况会很长 所有用long类型的
        }
        return -1;                              //如果新增没成功 返回-1
    }


    //负责单条查询的方法
    public static  <T> T selectOne(String sql,Object[] values,Class resultType) throws Exception {
        T obj = null;
        Connection connection = ConnectionPool.getConnection();
        //创建状态参数
        PreparedStatement pstat = connection.prepareStatement(sql);
        //需要将sql和？信息拼接完整
        for(int i = 0; i < values.length; i++){
            pstat.setObject(i+1,values[i]);
        }
        //执行修改操作
        ResultSet rs = pstat.executeQuery();             //rs信息取出来  存入一个容器
        if(rs.next()){
            obj =  construct(rs, resultType);
        }
        return obj;
    }



    //为了给一个对象进行赋值操作
    public static <T> T construct(ResultSet rs, Class resultType) throws Exception {
        T obj = (T)resultType.newInstance();
        for(int i = 1; i < rs.getMetaData().getColumnCount(); i++){
            String metaName = rs.getMetaData().getColumnName(i);
            setAttribute(obj,metaName,rs);
        }
        return obj;
    }


    //为了给对象的某一个属性进行赋值操作
    public static void setAttribute(Object obj,String attr,ResultSet resultSet) throws Exception {
        String humpAttr = lineToHump(attr);
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.getName().equals(humpAttr)){
                field.setAccessible(true);
                Class fieldType = field.getType();
                field.set(obj,resultSet.getObject(attr,fieldType));
            }
        }

    }

    //验证数据库中列名  存在下划线 需要做一个处理                //           my_name      --------->     myName
    public static String lineToHump(String str){         //列名字
        str = str.toLowerCase();        //现将列名全部转化为小写
        Pattern linePattern = Pattern.compile("_(\\w)");             //下划线之后匹配一个字符
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb,matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();

    }

}

package util;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class MyConnection extends AbstractConnection {
    //将一个真实的连接和一个状态放在一起         序号

    private Connection connection;              //真实的连接
    private int index;                          //位置

    private static String driver = DBConfig.getConfig("driver");
    private static String url = DBConfig.getConfig("url");
    private static String user = DBConfig.getConfig("user");
    private static String password = DBConfig.getConfig("password");


    public MyConnection(int index){
        this.index = index;
    }

    static{
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    {
        try {
            connection = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

/*===================================================================================================*/

    @Override
    public Statement createStatement() throws SQLException {
        return this.connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        //创建一个状态参数对象     (这里用到了静态代理模式Proxy,因为知道代理的是谁，所以称为静态代理模式)
        PreparedStatement pstat = this.connection.prepareStatement(sql);
        return pstat;
    }

    @Override
    public void close() throws SQLException {
        //将连接释放
        ConnectionPool.giveBack(this);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }


}

package test;


import util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {

        try {
            //1.获取连接
            Connection conn = ConnectionPool.getConnection();
            //2.使用
            PreparedStatement pstat = conn.prepareStatement("select * from emp");

            ResultSet rs = pstat.executeQuery();
            while (rs.next()){
                System.out.println(rs.getString("ename"));
            }
            rs.close();
            pstat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

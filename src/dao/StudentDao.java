package dao;

import domain.Student;
import sqlsession.Param;
import sqlsession.SQL;
import sqlsession.SQLEnums;
import sqlsession.SqlSessionFactory;
import util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public interface StudentDao {

    //private SqlSessionFactory sessionFactory = new SqlSessionFactory();

    //持久层---数据持久化
    //负责数据的读写           代码都是纯粹的jdbc+sql

    //设计一个方法  一行数据的修改
    //参数？domain              返回值? int
    @SQL(sql="update student set name=?,sex=?,birth=?,ctime=? where id=?",type= SQLEnums.UPDATE)
    public int update(Student student) throws Exception;
    //1.导包
    //2.加载驱动类
    //3.获得链接
    //4.创建状态参数
    //5.执行修改操作
    //6.关闭

    //设计一个方法   负责删除一行记录
    // 参数？ id    返回值？ int  修改的行数
    @SQL(sql="delete from student where id=?",type=SQLEnums.DELETE)
    public int delete(@Param("id") int id) throws Exception;

    //设计一个新增   一个对象参数   返回值long主键id
    @SQL(sql="insert into student(id,name) values(?,?)",type=SQLEnums.INSERT)
    public long insert(Student student) throws Exception;

    @SQL(sql = "select * from student where id = ?",type=SQLEnums.SELECT,resultType = Student.class)
    public Student selectOne(@Param("id") int id);
}

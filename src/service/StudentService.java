package service;

import dao.StudentDao;
import sqlsession.DaoProxy;

public class StudentService {
    //业务层
    //private StudentDao dao = new StudentDao();               之前是new 一个StudentDao的实现类 现在是通过代理对象来实现
    private StudentDao dao = (StudentDao) DaoProxy.getInstance(StudentDao.class);

}

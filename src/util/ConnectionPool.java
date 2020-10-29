package util;

import java.sql.Connection;

public class ConnectionPool {


    private static final int BUSY_VALUE = 1;
    private static final int FREE_VALUE = 0;
    private static final int NULL_VALUE = -1;


    //设置一个容器        预先装一些连接
    private static Connection[] connectionList = new Connection[DBConfig.getIntegerValue("minPoolSize","1")];
    //设置一个容器     存储每一个连接对应的状态    占用1    释放0   空值-1
    private static byte[] connectionBitMap = new byte[DBConfig.getIntegerValue("minPoolSize","1")];                 //位图
    //用来存储连接个数
    private static int total = 0;


    //块的目的是为了在连接池初始化的时候  让每一个连接的状态都是空值
    static {
        for(int i = 0; i < connectionBitMap.length; i++){
            connectionBitMap[i] = -1;
        }
    }

    //找一个小弟   负责在连接池中找一个状态为free的索引
    public static int getFreeIndex(){
        for(int i = 0; i < connectionBitMap.length; i++){
            if(connectionBitMap[i] == FREE_VALUE){
                return i;
            }
        }
        return -1;                        //如果没找到 就返回-1
    }

    //找寻连接池中空值状态的位置
    public static int getNullIndex(){
        for(int i = 0;i < connectionBitMap.length; i++){
            if(connectionBitMap[i] == NULL_VALUE){
                return i;
            }
        }
        return -1;                                                             //如果没找到 就返回-1
    }

    //扩容
    public static int grow(){
        Connection[] newConnectionList = new Connection[connectionList.length*2];
        byte[] newConnectionBitMap = new byte[connectionBitMap.length*2];
        System.arraycopy(connectionList,0,newConnectionList,0,connectionList.length);
        System.arraycopy(connectionBitMap,0,newConnectionBitMap,0,connectionBitMap.length*2);
        int firstNullIndex = connectionList.length;
        connectionList = newConnectionList;
        connectionBitMap = newConnectionBitMap;
        for(int i = firstNullIndex; i < newConnectionBitMap.length; i++){
                newConnectionBitMap[i] = -1;                                     //初始化
        }
        return firstNullIndex;
    }

    //设计一个方法   分配连接
    public static Connection distribute(int index){
        //有可能在你准备使用这个free连接的时候，这个连接在这期间被别的连接给拿走了，所以需要进一步再次判断
        if(connectionBitMap[index] == BUSY_VALUE){            //刚才看还是free的，刚准备用发现已经被别人拿走了
            return  null;
        }
        Connection connection = null;
        if(connectionBitMap[index] == NULL_VALUE){            //当前位置为空   需要创建
            connection = new MyConnection(index);
            connectionList[index] = connection;
            total++;
        }else if(connectionBitMap[index] == FREE_VALUE){
            connection = connectionList[index];
        }
        connectionBitMap[index] = BUSY_VALUE;                  //我占用了这个连接
        return connection;
    }

    //设计一个方法   释放连接
    protected  static synchronized void giveBack(MyConnection myConnection){
        connectionBitMap[myConnection.getIndex()] = 0;
    }

  //    给用户提供一个从连接池获得连接的方法
    public static synchronized Connection getConnection(){
        //1.想要获取一个状态为free的连接
        int freeIndex = getFreeIndex();
        if(freeIndex > -1){       //证明找到了free的连接index
            //给用户分配一个连接使用
            return distribute(freeIndex);
        }else if(total < DBConfig.getIntegerValue("maxPoolSize","10")){
            int nullIndex = getNullIndex();
            if (nullIndex == -1){                                //数组不够长度
                nullIndex = grow();
            }
            return distribute(nullIndex);
        }
        return null;                                              //容量已经最大  而且没有空闲的连接了
    }
}

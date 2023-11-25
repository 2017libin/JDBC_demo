JDBC_demo 学习笔记

# 环境搭建

## 1. 创建数据库/表

1. idea连接数据库
2. 右键打开命令行
3. 粘贴sql语句到命令行中
4. 选中sql语句，点击执行

```mysql
CREATE DATABASE jdbcStudy CHARACTER SET utf8 COLLATE utf8_general_ci;
USE jdbcStudy;
CREATE TABLE users(
id INT PRIMARY KEY,
NAME VARCHAR(40),
PASSWORD VARCHAR(40),
email VARCHAR(60),
birthday DATE
);
INSERT INTO users(id,NAME,PASSWORD,email,birthday)
VALUES(1,'zhansan','123456','zs@sina.com','1980-12-04'),
(2,'lisi','123456','lisi@sina.com','1981-12-04'),
(3,'wangwu','123456','wangwu@sina.com','1979-12-04');

```

![image-20231125143435515](https://pic-go1.oss-cn-guangzhou.aliyuncs.com/image-20231125143435515.png)

## 2. 导入mysql依赖

```xml
<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
</dependency>
```

# Lessson01

1. 步骤

   1. 加载驱动；
   2. 连接数据库 DriverManager；
   3. 获得执行SQL的对象 Statement；
   4. 获得返回的结果集；
   5. 释放连接。

2. 常用方法

   1. connection：用于连接数据库和获取statement
   2. statement：用于执行sql语句
      1. Statement.executeUpdate执行完后，将会返回一个整数表示数据库几行数据发生了变化
      2. Statement.executeQuery方法用于向数据库发送查询语句
   3. ResultSet：保存执行sql语句的输出
      1. 获取某一行数据
      2. 获取某一行数据中的具体字段

   ```java
   String sql = "SELECT * FROM users";
   ResultSet resultSet = statement.executeQuery(sql);  // 返回结果集,结果集中封装了我们全部查询出来的对象
   
   // Statement对象常用方法
   statement.executeQuery(sql); // 查询操作，返回结果集
   statement.execute(sql); // 执行任何SQL
   statement.executeUpdate(sql); // 更新、插入、删除，均为这个。唯一受影响的是行数
   statement.addBatch(sql); // 把多条sql语句放到一个批处理中
   statement.executeBatch(sql); // 向数据库发送一批sql语句执行
   
   resultSet.getString(); // 不知道数据类型时使用
   // 如果知道列的类型就用指定的类型
   resultSet.getString();
   resultSet.getInt();
   resultSet.getFloat();
   resultSet.getDouble();
   resultSet.getDate();
   resultSet.getObject();
   ....
       
   resultSet.next(); // 移动到下一行
   resultSet.previous(); // 移动到前一行
   resultSet.absolute(int row); // 移动到指定行
   resultSet.beforeFirst(); // 移动resultSet的最前面。
   resultSet.afterLast(); // 移动到resultSet的最后面。
   
   
   ```

3. 简单的完整例子

   ```java
   package org.example.lesson01;
   
   import java.sql.Connection;
   import java.sql.DriverManager;
   import java.sql.ResultSet;
   import java.sql.Statement;
   
   /**
    * 第一个JDBC程序
    */
   public class JdbcFirstDemo {
       public static void main(String[] args) throws Exception{
           // 1.加载驱动
           // DriverManager.registerDriver(new Driver()) 不推荐使用这种方法加载驱动
           Class.forName("com.mysql.jdbc.Driver"); // 固定写法，加载驱动
   
           // 2.用户信息和url
           // uesUnicode=true 支持中文编码
           // characterEncoding=utf8 设定字符集
           // useSSL=true 使用安全的连接
           String url = "jdbc:mysql://localhost:3306/jdbcStudy?uesUnicode=true&characterEncoding=utf8&useSSL=true";
           String username = "root";
           String password = "12345678";
   
           // 3.连接成功，数据库对象 connection 代表数据库
           Connection connection = DriverManager.getConnection(url, username, password);
   
           // 4.执行SQL对象
           Statement statement = connection.createStatement();
   
           // 5.执行SQL对象，执行SQL
           String sql = "SELECT * FROM users";
   
           ResultSet resultSet = statement.executeQuery(sql);  // 返回结果集,结果集中封装了我们全部查询出来的对象
   
           while(resultSet.next()){
               System.out.println("-----------------------------");
               System.out.println("id=" + resultSet.getObject("id"));
               System.out.println("name=" + resultSet.getObject("NAME"));
               System.out.println("pwd=" + resultSet.getObject("PASSWORD"));
               System.out.println("email=" + resultSet.getObject("email"));
               System.out.println("both=" + resultSet.getObject("birthday"));
           }
   
           // 6.释放连接
           resultSet.close();
           statement.close();
           connection.close();
       }
   }
   ```

# Lesson02

1. 通过util类来获取connection、关闭connection/statement/resultset

   ```java
   package org.example.lesson02.utils;
   
   import java.io.InputStream;
   import java.sql.*;
   import java.util.Properties;
   
   public class Utils {
   
       private static String driver = null;
       private static String url = null;
       private static String username = null;
       private static String password = null;
   
       static{
           try{
               // 读取db.properties文件中的数据库连接信息
               InputStream in = Utils.class.getClassLoader().getResourceAsStream("db.properties");
               Properties properties = new Properties();
               properties.load(in);
   
               // 获取数据库连接驱动
               driver = properties.getProperty("driver");
               // 获取数据库连接URL地址
               url = properties.getProperty("url");
               // 获取数据库连接用户名
               username = properties.getProperty("username");
               // 获取数据库连接密码
               password = properties.getProperty("password");
   
               // 加载数据库驱动，只需加载一次！
               Class.forName(driver);
   
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   
       // 获取连接对象
       public static Connection getConnection() throws SQLException{
           return DriverManager.getConnection(url,username,password);
       }
   
       // 释放资源
       public static void release(Connection conn, Statement st, ResultSet rs){
           if(conn!=null){
               try {
                   conn.close();
               } catch (SQLException throwables) {
                   throwables.printStackTrace();
               }
           }
           if(st!=null){
               try {
                   st.close();
               } catch (SQLException throwables) {
                   throwables.printStackTrace();
               }
           }
           if(rs!=null){
               try {
                   rs.close();
               } catch (SQLException throwables) {
                   throwables.printStackTrace();
               }
           }
       }
   }
   ```

2. 测试类调用util来完成增删查改

```java
package lesson02;

import org.example.lesson02.utils.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 插入数据
 */
public class TestUpdate {
    public static void main(String[] args) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try{
            // 获取一个数据库连接
            conn = Utils.getConnection();

            // 通过conn对象获取负责执行SQL命令的Statement对象
            st = conn.createStatement();

            // 要执行的SQL
            String sql = "update users set name='王伟',email='wangwei@163.com' where id=3";

            // 执行操作
            int num = st.executeUpdate(sql);
            if(num>0){
                System.out.println("更新数据成功！！！");
            }else {
                System.out.println("更新数据失败！！！");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            Utils.release(conn,st,rs);
        }
    }
}
```

# Lesson03

1. 使用preparedstatement实现预编译，避免sql注入

   ```java
   package lesson03;
   
   import org.example.lesson02.utils.Utils;
   
   import java.sql.Connection;
   import java.sql.PreparedStatement;
   import java.sql.ResultSet;
   
   /**
    * 查询数据
    */
   public class TestSelect {
       public static void main(String[] args) {
           Connection conn = null;
           PreparedStatement st = null;
           ResultSet rs = null;
           try{
               conn = Utils.getConnection();
               String sql = "select * from users where id=?";
               st = conn.prepareStatement(sql);
               st.setInt(1,1);
               rs = st.executeQuery();
               if(rs.next()){
                   System.out.println(rs.getString("name"));
                   System.out.println("查询数据成功！！！");
               }
           } catch (Exception e) {
               e.printStackTrace();
           } finally {
               Utils.release(conn,st,rs);
           }
       }
   }
   ```

# Lesson04

   1. 事务相关的语句

      ```java
      Connection.setAutoCommit(false);//开启事务(start transaction)
      Connection.rollback();//回滚事务(rollback)
      Connection.commit();//提交事务(commit)
      ```

   2. account表的建立和初始化

      ```sql
      -- 案例
      -- 创建账户表
      CREATE TABLE account(
          id INT PRIMARY KEY AUTO_INCREMENT,
          NAME VARCHAR(40),
          money FLOAT
      );
      -- 插入测试数据
      insert into account(name,money) values('A',1000);
      insert into account(name,money) values('B',1000);
      insert into account(name,money) values('C',1000);
      ```

   3. 模拟转账成功的例子

      ```java
      package org.example.lession04;
      
      import org.example.lesson02.utils.Utils;
      
      import java.sql.Connection;
      import java.sql.PreparedStatement;
      import java.sql.ResultSet;
      import java.sql.SQLException;
      
      /**
       * 模拟转账成功时的业务场景
       */
      public class TestTransaction {
          public static void main(String[] args) {
              Connection conn = null;
              PreparedStatement st = null;
              ResultSet rs = null;
      
              try {
                  // 关闭数据库的自动提交，自动开启事务
                  conn = Utils.getConnection();
                  // 开启事务
                  conn.setAutoCommit(false);
      
                  String sql1 = "update account set money=money-100 where name='A'";
                  st = conn.prepareStatement(sql1);
                  st.executeUpdate();
      
                  int x=1/0;  // 报错
      
                  String sql2 = "update account set money=money+100 where name='B'";
                  st = conn.prepareStatement(sql2);
                  st.executeUpdate();
      
                  // 业务完毕，提交事务
                  conn.commit();
                  System.out.println("转账成功！！！");
              } catch (Exception e) {
                  try {
                      conn.rollback(); // 失败，则事务回滚
                      System.out.println("转账失败！！！事务回滚！！！");
                  } catch (SQLException throwables) {
                      throwables.printStackTrace();
                  }
                  e.printStackTrace();
              } finally {
                  Utils.release(conn,st,rs);
              }
      
          }
      }
      ```

# Lesson05

  1. 数据库连接池：数据库连接池负责**分配,管理和释放**数据库**连接**,它允许应用程序重复使用一个现有的数据库连接,而不是重新建立一个。
     
     1. 优点：减少创建连接的时间，传统的方式需要大量的新建和销毁连接的开销。
     
  2. **最小连接数：**是连接池一直保持的数据库连接，所以如果应用程序对数据库连接的使用量不大，将会有大量的数据库连接资源被浪费。

  3. **最大连接数：**是连接池能申请的最大连接数，如果数据库连接请求超过次数，后面的数据库连接请求将被加入到等待队列中，这会影响以后的数据库操作。

  4. 如果最小连接数与最大连接数相差很大：那么最先连接请求将会获利，之后超过最小连接数量的连接请求等价于建立一个新的数据库连接。不过，这些大于最小连接数的数据库连接在使用完不会马上被释放，他将被放到连接池中等待重复使用或是空间超时后被释放。

  5. 工具类，通过连接池获取连接

     ```java
     package org.example.lesson05.utils;
     
     import org.apache.commons.dbcp2.BasicDataSourceFactory;
     
     import javax.sql.DataSource;
     import java.io.InputStream;
     import java.sql.Connection;
     import java.sql.ResultSet;
     import java.sql.SQLException;
     import java.sql.Statement;
     import java.util.Properties;
     
     /**
      * 数据库连接工具类
      */
     public class UtilsDBCP {
         private static DataSource ds = null;
     
         static{
             try {
                 InputStream in = UtilsDBCP.class.getClassLoader().getResourceAsStream("dbcpconfig.properties");
                 Properties prpo = new Properties();
                 prpo.load(in);
                 // 创建数据源
                 ds = BasicDataSourceFactory.createDataSource(prpo);
             } catch (Exception e) {
                 throw new ExceptionInInitializerError(e);
             }
         }
     
         /**
          * 获取数据库连接
          * @return
          * @throws SQLException
          */
         public static Connection getConnection() throws SQLException {
             return ds.getConnection();
         }
     
         /**
          * 释放资源
          * @param conn
          * @param st
          * @param rs
          */
         public static void release(Connection conn, Statement st, ResultSet rs){
             if(rs!=null){
                 try {
                     rs.close();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
             if(st!=null) {
                 try {
                     st.close();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
             if(conn!=null) {
                 try {
                     conn.close();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
     }
     ```

6. 使用工具类的例子

   ```java
   package org.example.lesson05;
   
   import org.example.lesson05.utils.UtilsDBCP;
   
   import java.sql.*;
   
   /**
    * 数据库连接工具类测试
    */
   public class TestDBCP {
       public static void main(String[] args) {
           Connection conn = null;
           PreparedStatement st = null;
           ResultSet rs = null;
           try {
               conn = UtilsDBCP.getConnection();
               String sql = "insert into users(id,name,password,email,birthday) values (?,?,?,?,?)";
               st = conn.prepareStatement(sql);
               st.setInt(1,6);
               st.setString(2,"apple");
               st.setString(3,"232323");
               st.setString(4,"327338203@qq.com");
               st.setDate(5,new java.sql.Date(System.currentTimeMillis()));
               // 执行插入数据操作
               int num = st.executeUpdate();
               if(num>0){
                   System.out.println("插入数据成功！！！");
               }
   
           } catch (Exception e) {
               e.printStackTrace();
           } finally {
               // SQL释放资源
               UtilsDBCP.release(conn,st,rs);
           }
       }
   }
   ```

   
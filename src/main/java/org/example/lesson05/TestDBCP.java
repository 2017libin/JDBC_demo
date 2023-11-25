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

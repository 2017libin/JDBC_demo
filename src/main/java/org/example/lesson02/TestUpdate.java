package org.example.lesson02;

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
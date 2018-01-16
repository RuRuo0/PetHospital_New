package ph.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ph.po.Visit;

public class VisitDAO
{
    public void save(Visit visit) throws Exception
    {
        Connection con = null;
        PreparedStatement ps = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_ph","root", "123456");// 协议://域名(ip):端口/资源（数据库名）

            ps=con.prepareStatement("insert into t_visit value(null,?,?,CURDATE(),?,?)");
            ps.setInt(1, visit.getPetId());
            ps.setInt(2, visit.getVetId());
            ps.setString(3, visit.getDescription());
            ps.setString(4, visit.getTreatment());

            ps.executeUpdate();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new Exception("找不到驱动:" + e.getMessage());// 异常不能在底层丢失了
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new Exception("数据库操作错误:" + e.getMessage());
        }
        finally
        {
            if (ps != null)
            {
                ps.close();
            }
            if (con != null)
            {
                con.close();
            }
        }
    }
}

package ph.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.net.URLEncoder;
import ph.dao.VetDAO;
import ph.dao.VisitDAO;
import ph.po.Visit;

@WebServlet(name = "VisitServlet")
public class VisitServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            request.setAttribute("vets", new VetDAO().getAll());
            request.getRequestDispatcher("/visitadd.jsp").forward(request, response);
        }
        catch (Exception e)
        {
            request.setAttribute("msg", e.getMessage());
            request.getRequestDispatcher("/CustomerServlet?id="+request.getParameter("cid")).forward(request, response);;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            Visit visit=new Visit();
            visit.setPetId(Integer.parseInt(request.getParameter("pid")));
            visit.setVetId(Integer.parseInt(request.getParameter("vid")));
            visit.setDescription(request.getParameter("description"));
            visit.setTreatment(request.getParameter("treatment"));
            new VisitDAO().save(visit);
            response.sendRedirect("CustomerServlet?id="+request.getParameter("cid")+"&msg="+URLEncoder.encode("病例添加成功","UTF-8"));
        }
        catch (Exception e)
        {
            //重定向如果需要发消息  参考退出时的消息发送  是以get方式传递parameter
            response.sendRedirect("CustomerServlet?id="+request.getParameter("cid")+"&msg="+URLEncoder.encode(e.getMessage(),"UTF-8"));
        }
    }
}

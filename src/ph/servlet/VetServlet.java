package ph.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.List;
import ph.dao.SpecialityDAO;
import ph.dao.VetDAO;
import ph.po.Speciality;
import ph.po.Vet;

@WebServlet(name = "VetServlet")
//Vets Management Servlet
public class VetServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //doPost方法会被多个表单调用  查询医生  保存医生  因此这里需要根据不同表单传递的标示参数调用不同的方法
        String m = request.getParameter("m");
        if("search".equals(m))
        {
            search(request, response);
        }
        else if("save".equals(m))
        {
            save(request, response);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            request.setAttribute("specs", new SpecialityDAO().getAll());
            request.getRequestDispatcher("/vetadd.jsp").forward(request, response);
        }
        catch (Exception e)
        {
            request.setAttribute("msg", e.getMessage());
            request.getRequestDispatcher("/vetsearch.jsp").forward(request, response);
        }
    }

    private void save(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        //这里需要根据表单封装一个Vet   表单里有医生名  专业id下拉列表
        Vet vet=new Vet();
        //根据下拉列表封装多个专业
        String[] sids = request.getParameterValues("sid");
        String vname = request.getParameter("vname");
        if("".equals(vname))
        {
            request.setAttribute("msg", "请输入医生姓名");
            //这里虽然要返回的vetadd.jsp提示消息 但是不能直接转发到vetadd.jsp  因为vetadd.jsp需要专业集合显示数据
//			request.getRequestDispatcher("/vetadd.jsp").forward(request, response);
            doGet(request, response);
        }
        else if(null==sids || 0==sids.length)
        {
            request.setAttribute("msg", "请选择专业");
            doGet(request, response);
        }
        else
        {
            vet.setName(vname);
            for(String sid:sids)
            {
                Speciality s=new Speciality();
                s.setId(Integer.parseInt(sid));
                vet.getSpecs().add(s);
            }
            try
            {
                new VetDAO().save(vet);
                request.setAttribute("msg", "添加成功");
                request.getRequestDispatcher("/vetsearch.jsp").forward(request, response);
            }
            catch (Exception e)
            {
                request.setAttribute("msg",e.getMessage());
                doGet(request,response);
            }
        }

    }

    private void search(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        String vname=request.getParameter("vname");
        String sname=request.getParameter("sname");
        try
        {
            List<Vet> vets=new VetDAO().search(vname, sname);
            if(0==vets.size())
            {
                request.setAttribute("msg", "没有相关医生信息");
                request.getRequestDispatcher("/vetsearch.jsp").forward(request, response);
            }
            else
            {
                request.setAttribute("vets", vets);
                request.getRequestDispatcher("/vetsearch_result.jsp").forward(request, response);
            }
        }
        catch (Exception e)
        {
            request.setAttribute("msg", e.getMessage());
            request.getRequestDispatcher("/vetsearch.jsp").forward(request, response);
        }
    }
}

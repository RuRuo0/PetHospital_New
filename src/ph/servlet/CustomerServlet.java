package ph.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ph.dao.PetDAO;
import ph.dao.UserDAO;
import ph.po.Pet;
import ph.po.User;

@WebServlet("/CustomerServlet")
public class CustomerServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            // 1 得到超链接里面的主人id
            int id = Integer.parseInt(request.getParameter("id"));
            // 2 根据主人id查找UserDAO.getById()
            User user = new UserDAO().getById(id);
            // 3根据主人id查找PetDAO.getPetsByOwnerId()
            List<Pet> pets = new PetDAO().getPetsByOwnerId(id);

            // 4将两个查找结果通过request转发到结果页面 customerdetail.jsp
            user.setPets(pets);
            request.setAttribute("user", user);//Pet是User的属性，已包含在User中传递给了customerdetail.jsp，因此无需单独传递pets
            request.getRequestDispatcher("/customerdetail.jsp").forward(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String m=request.getParameter("m");
        if("search".equals(m))
        {
            search(request, response);
        }
        else if("save".equals(m))
        {
            save(request, response);
        }
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        User user=new User();
        user.setName(request.getParameter("name"));
        user.setAddress(request.getParameter("address"));
        user.setTel(request.getParameter("tel"));
        user.setRole("customer");
        user.setPwd("123456");
        try
        {
            new UserDAO().save(user);
            request.setAttribute("msg", "添加成功");
            request.getRequestDispatcher("/customersearch.jsp").forward(request, response);
        }
        catch (Exception e)
        {
            request.setAttribute("msg", e.getMessage());
            request.getRequestDispatcher("/customersearch.jsp").forward(request, response);
        }
    }

    private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            List<User> users = new UserDAO().searchCustomer(request.getParameter("cname"));
            if (0 == users.size())
            {
                request.setAttribute("msg", "没有找到客户信息");
                request.getRequestDispatcher("/customersearch.jsp").forward(request, response);

            }
            else
            {
                request.setAttribute("users", users);
                request.getRequestDispatcher("/customersearch_result.jsp").forward(request, response);
            }
        }
        catch (Exception e)
        {
            request.setAttribute("msg", e.getMessage());
            request.getRequestDispatcher("/customersearch.jsp").forward(request, response);
        }
    }
}
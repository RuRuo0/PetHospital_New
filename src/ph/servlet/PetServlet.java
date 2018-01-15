package ph.servlet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import ph.dao.PetDAO;
import ph.po.Pet;

@MultipartConfig
@WebServlet("/PetServlet")
public class PetServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String m = request.getParameter("m");
        if("toAdd".equals(m))
        {
            request.getRequestDispatcher("/petadd.jsp").forward(request, response);
        }
        else if("delete".equals(m))
        {
            delete(request, response);
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            new PetDAO().delete(Integer.parseInt(request.getParameter("pid")));
            request.getRequestDispatcher("/CustomerServlet?id=" + request.getParameter("cid")).forward(request, response);
        }
        catch (Exception e)
        {
            request.setAttribute("msg", e.getMessage());
            request.getRequestDispatcher("/CustomerServlet?id=" + request.getParameter("cid")).forward(request, response);
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Part p = request.getPart("photo");
        String oldname = getFileName(p);
        //这里需要上传文件，就需要得到上传文件的目标路径
        //这里的文件保存路径不能是任意路径，只能放到当前应用根目录及其子目录下，才能够通过浏览器访问
        //如何得到这个当前应用根目录，一般情况下不能写死路径，应该通过代码动态得到路径
        //System.out.println(getRuntimePath());//路径有了差文件名
        //如果使用原文件名，可能出现重名文件，若非要使用原文件名，则可以分文件夹或将文件名改为时间毫秒数

        String photo="photo/default.jpg";

        if(!oldname.equals(""))//如果旧文件名不为空表示用户上传了照片，则需要保存照片，否则可以省略这个步骤
        {
            String type = oldname.substring(oldname.lastIndexOf("."));//xxxx.xx
            String newname = System.currentTimeMillis() + type;
            String saveFile = getRuntimePath() + newname;
            photo = "photo/" + newname;
            p.write(saveFile);//上传文件
        }

        Pet pet = new Pet();
        pet.setName(request.getParameter("name"));
        pet.setBirthdate(request.getParameter("birthdate"));
        pet.setOwnerId(Integer.parseInt(request.getParameter("cid")));
        pet.setPhoto(photo);
        try
        {
            new PetDAO().save(pet);
            response.sendRedirect("CustomerServlet?id="+pet.getOwnerId());//这里使用重定向是因为客户查看页面的对应地址是一个get方式的请求地址,且需要一个id值
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getRuntimePath()
    {
        String path = "";
        path = this.getServletContext().getRealPath("/photo");
        path += "\\";
        return path;
    }


    private String getFileName(Part part)
    {
        String filename = "";
        String contentDec = part.getHeader("content-disposition");// 获取header信息中的content-disposition，如果为文件，则可以从其中提取出文件名

        Pattern p = Pattern.compile("filename=\".+\"");//  filename="任意个字符"   .任意字符   +表示数量不固定
        Matcher m = p.matcher(contentDec);
        if(m.find())
        {
            String temp=m.group();
            filename=temp.substring(10,temp.length()-1);
        }
        return filename;
    }
}

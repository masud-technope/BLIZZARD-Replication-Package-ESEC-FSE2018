import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTMLFilter;

public class RequestParamExample extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final ResourceBundle RB = ResourceBundle.getBundle("LocalStrings");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        String title = RB.getString("requestparams.title");
        out.println("<title>" + title + "</title>");
        out.println("</head>");
        out.println("<body bgcolor=\"white\">");
        // img stuff not req'd for source code html showing
        // all links relative
        // XXX
        // making these absolute till we work out the
        // addition of a PathInfo issue
        out.println("<a href=\"../reqparams.html\">");
        out.println("<img src=\"../images/code.gif\" height=24 " + "width=24 align=right border=0 alt=\"view code\"></a>");
        out.println("<a href=\"../index.html\">");
        out.println("<img src=\"../images/return.gif\" height=24 " + "width=24 align=right border=0 alt=\"return\"></a>");
        out.println("<h3>" + title + "</h3>");
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        out.println(RB.getString("requestparams.params-in-req") + "<br>");
        if (firstName != null || lastName != null) {
            out.println(RB.getString("requestparams.firstname"));
            out.println(" = " + HTMLFilter.filter(firstName) + "<br>");
            out.println(RB.getString("requestparams.lastname"));
            out.println(" = " + HTMLFilter.filter(lastName));
        } else {
            out.println(RB.getString("requestparams.no-params"));
        }
        out.println("<P>");
        out.print("<form action=\"");
        out.print("RequestParamExample\" ");
        out.println("method=POST>");
        out.println(RB.getString("requestparams.firstname"));
        out.println("<input type=text size=20 name=firstname>");
        out.println("<br>");
        out.println(RB.getString("requestparams.lastname"));
        out.println("<input type=text size=20 name=lastname>");
        out.println("<br>");
        out.println("<input type=submit>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}

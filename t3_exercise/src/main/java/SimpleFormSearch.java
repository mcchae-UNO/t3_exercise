import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SimpleFormSearch")
public class SimpleFormSearch extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public SimpleFormSearch() {
      super();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String keyword1 = request.getParameter("keyword1");
      String keyword2 = request.getParameter("keyword2");
      search(keyword1, keyword2, response);
   }

   void search(String keyword1, String keyword2, HttpServletResponse response) throws IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String title = "Database Result";
      String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + //
            "transitional//en\">\n"; //
      out.println(docType + //
            "<html>\n" + //
            "<head><title>" + title + "</title></head>\n" + //
            "<body bgcolor=\"#f0f0f0\">\n" + //
            "<h1 align=\"center\">" + title + "</h1>\n");

      Connection connection = null;
      PreparedStatement preparedStatement = null;
      try {
         DBConnection.getDBConnection(getServletContext());
         connection = DBConnection.connection;

         // prettier way to do this? 
         String selectSQL; 
         String theTitle;
         String theGenre;
         if (keyword1.isEmpty() && keyword2.isEmpty()) { // both empty 
            selectSQL = "SELECT * FROM t3Table";
            preparedStatement = connection.prepareStatement(selectSQL);
         } else if (!keyword1.isEmpty() && keyword2.isEmpty()) { // title not empty, genre empty
            selectSQL = "SELECT * FROM t3Table WHERE TITLE LIKE ?";
            theTitle = keyword1 + "%";
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, theTitle);
         } else if (keyword1.isEmpty() && !keyword2.isEmpty()) { // title empty, genre not empty 
          selectSQL = "SELECT * FROM t3Table WHERE GENRE LIKE ?";
          theGenre = keyword2 + "%";
          preparedStatement = connection.prepareStatement(selectSQL);
          preparedStatement.setString(1, theGenre);
         } else { // title not empty, genre not empty 
             selectSQL = "SELECT * FROM t3Table WHERE TITLE LIKE ? AND GENRE LIKE ?";
             theTitle = keyword1 + "%";
             theGenre = keyword2 + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theTitle);
             preparedStatement.setString(2, theGenre);
         }
         System.out.println(preparedStatement);
         ResultSet rs = preparedStatement.executeQuery();

         while (rs.next()) {
            int id = rs.getInt("id");
            String bookTitle = rs.getString("title").trim();
            String author = rs.getString("author").trim();
            String genre = rs.getString("genre").trim();
      
            out.println("ID: " + id + ", ");
            out.println("TITLE: " + bookTitle + ", ");
            out.println("AUTHOR: " + author + ", ");
            out.println("GENRE: " + genre + ", "); 
            
         }
         out.println("<br> <a href=/t3_exercise/simpleFormSearch.html>Search Data</a> <br>");
         out.println("</body></html>");
         rs.close();
         preparedStatement.close();
         connection.close();
      } catch (SQLException se) {
         se.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (preparedStatement != null)
               preparedStatement.close();
         } catch (SQLException se2) {
         }
         try {
            if (connection != null)
               connection.close();
         } catch (SQLException se) {
            se.printStackTrace();
         }
      }
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
   }

}

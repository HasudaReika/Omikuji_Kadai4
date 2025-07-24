package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import omikuji4.OmikujiDB;

/**
 * Servlet implementation class OmikujiServlet
 */
@WebServlet("/omikuji")
public class BirthdayInputServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public void init() throws ServletException {
		OmikujiDB omikujiDB = new OmikujiDB();
		//DBにおみくじが格納されているかチェック
		try {
			if (omikujiDB.checkDB()) { //DBにおみくじが格納されていない場合
				//おみくじをDBに格納する
				omikujiDB.importOmikujiFromCsv();
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//エラーメッセージを設定
		String errorMsg = (String) request.getAttribute("errorMsg");

		//HTMLを出力
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<!doctype html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"UTF-8\"/>");
		out.println("<title>おみくじ占い</title>");
		out.println("<link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/css/style.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<div class=\"container\">");
		out.println("<h1>おみくじ占い</h1>");
		out.println("<form action=\"result\" method=\"post\">");

		if (errorMsg != null) { //エラーメッセージがある場合は表示
			out.println("<p class=\"error\">" + errorMsg + "</p>");
		}

		out.println("<p>誕生日を入力してください 例:20011009<br>");
		out.println("<input type=\"text\" name=\"birthday\" required/><br>");
		out.println("<input type=\"submit\" value=\"送信\"/></p>");
		out.println("</form>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");

		//コンソールに表示する用
		System.out.println("誕生日を入力してください 例：20011009");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//doGetを呼び出す
		doGet(request, response);

	}

}

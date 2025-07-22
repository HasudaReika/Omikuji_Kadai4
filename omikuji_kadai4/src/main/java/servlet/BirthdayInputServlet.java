package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import omikuji4.OmikujiDB;
import omikuji4.OmikujiProgram;

/**
 * Servlet implementation class OmikujiServlet
 */
@WebServlet("/result")
public class BirthdayInputServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	OmikujiDB omikujiDB = new OmikujiDB();
	OmikujiProgram omikujiProgram = new OmikujiProgram();


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BirthdayInputServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//HTMLを出力
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<!doctype html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"UTF-8\"/>");
		out.println("<title>おみくじ占い</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<form action=\"result\" method=\"post\">");
		out.println("<p>誕生日を入力してください 例:20011009<br>");
		out.println("<input type=\"text\" name=\"birthday\"/><br>");
		out.println("<input type=\"submit\" value=\"送信\"/></p>");
		out.println("</form>");
		out.println("</body>");
		out.println("</html>");
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("static-access")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//DBにおみくじが格納されているかチェック
		
		//なければcsvから取り込みテーブルに追加
		
		
		//入力された誕生日文字列を取得
		String birthday = request.getParameter("birthday");
		//日付の妥当性チェック
		if (omikujiProgram.isValidDate(birthday)) {
			//もし正しい場合は値をリクエストスコープにセット
			request.setAttribute("birthday", birthday);
			//結果JSPに値を送る
			request.getRequestDispatcher("/result.jsp").forward(request, response);
		} else {
			//正しくない場合はエラーメッセージを表示したい
		}
		
		
	}

}

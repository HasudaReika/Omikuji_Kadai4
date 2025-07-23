package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import omikuji4.Omikuji;
import omikuji4.OmikujiDB;

/**
 * Servlet implementation class OmikujiServlet
 */
@WebServlet("/result")
public class BirthdayInputServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	OmikujiDB omikujiDB = new OmikujiDB();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BirthdayInputServlet() {
		super();
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
			out.println("<p class=\"error-msg\">" + errorMsg + "</p>");
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

		//--------日付の妥当性チェック---------
		//エラーメッセージの変数を準備
		String errorMsg = null;
		String birthday = (String) request.getParameter("birthday");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		//コンソールに表示する用
		System.out.println(birthday);

		//日時解析を厳密に行う
		sdf.setLenient(false);
		//入力された日付が正しいかチェック
		try {
			sdf.parse(birthday);
			//正しければおみくじを取得する処理へ

		} catch (ParseException e) {
			//存在しない日付の場合、エラーメッセージを出力
			errorMsg = "正しい日付を入力してください";
			//リクエストスコープに値をセット
			request.setAttribute("errorMsg", errorMsg);
			//コンソールに出力用
			System.out.println(errorMsg);
			//エラーメッセージとともにフォームを再表示
			doGet(request, response);
			return;
		}

		//--------おみくじを取得する---------

		//今日の日時を取得
		LocalDate today = LocalDate.now();
		//birthdayをLocalDate型に変換
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		//誕生日文字列を半角に変換
		String birthdayString = Normalizer.normalize(birthday, Form.NFKC);
		LocalDate bdDate = LocalDate.parse(birthdayString, formatter);

		//resultテーブルから取得するおみくじ変数
		Omikuji omikuji = null;
		//ランダムに新しいおみくじを取得する変数
		Omikuji newOmikuji = null;

		//resultテーブルに占った日と誕生日が一致する結果が存在するかチェック、あればおみくじを取得
		try {
			omikuji = omikujiDB.getOmikujiFromResult(today, bdDate);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		if (omikuji != null) { //該当するおみくじがある場合
			//DBからおみくじを取得しリクエストスコープにセット
			request.setAttribute("omikuji", omikuji);
			//コンソールに出力用
			System.out.println(omikuji.disp());

		} else {
			//なければDBからおみくじをランダムに1つ取得
			try {
				newOmikuji = omikujiDB.getRandomOmikuji();
				//リクエストスコープに値をセット
				request.setAttribute("omikuji", newOmikuji);
				//コンソールに出力用
				System.out.println(newOmikuji.disp());
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}

			//---------結果をDBに登録----------
			try {
				omikujiDB.saveResult(bdDate, newOmikuji);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}

		}

		//処理結果をJSPに転送
		RequestDispatcher dispatcher = request.getRequestDispatcher("/result.jsp");
		dispatcher.forward(request, response);

	}

}

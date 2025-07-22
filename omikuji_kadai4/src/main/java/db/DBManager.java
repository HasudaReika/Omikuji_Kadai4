package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {
	/** ドライバクラス名 */
	private static final String DRIVER = "org.postgresql.Driver";
	/** 接続するDBのURL */
	private static final String URL = "jdbc:postgresql://localhost/postgres";
	/** DB接続するためのユーザ名 */
	private static final String USER_NAME = "r_hasuda";
	/** DB接続するためのパスワード */
	private static final String PASSWORD = "hasuda2005";

	/**
	* DBと接続する
	*
	* @return DBコネクション
	* @throws ClassNotFoundException
	* ドライバクラスが見つからなかった場合
	* @throws SQLException
	* DB接続に失敗した場合
	*/
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		//JDBCドライバクラスをJVMに登録
		Class.forName(DRIVER);

		//DBに接続
		Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

//		System.out.println("接続しました");

		return connection;
	}

	/**
	* DBとの接続を切断する
	*
	* @param connection
	* DBとの接続情報
	*/
	public static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
//				System.out.println("DBと切断しました");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	*
	* PreparedStatementをクローズする
	* @param preparedStatement
	* ステートメント情報
	*/
	public static void close(PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* ResultSetをクローズする
	*
	* @param resultSet
	* SQL検索結果
	*/
	public static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

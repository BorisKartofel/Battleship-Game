package JointProjects.main.BattleshipGameServer;

import java.sql.*;

public class DBConnection {
    private String url = "jdbc:postgresql://localhost:5432/BattleshipGame";
    private String user = "postgres";
    private String pwd = "admin";
    public static void main(String[] args) {
        //executeRequest;
/*
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM mytable WHERE columnfoo = 500");
        while (rs.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(rs.getString(1));
        }
        rs.close();
        st.close();
        */

    }

    private Connection getConnection() {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Файл драйвера не найден");
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            return conn;
        } catch (SQLException e) {
            System.err.println("Не удалось подключиться к БД");
            e.printStackTrace();
        }
        return null;
    }
/*
    public ArrayList<> executeRequest(String sql) {
        Connection conn = getConnection();
        if (conn == null)
            return "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //rs.get
            //return rs;
        } catch (SQLException e) {

        }
        return "";
    } */
}

package tasktrack;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;

@WebListener
public class Db implements ServletContextListener {
  static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:h2:file:./data/tasktrack;DB_CLOSE_DELAY=-1");
  static final String USER = System.getenv().getOrDefault("DB_USER", "sa"), PASS = System.getenv().getOrDefault("DB_PASS", "");
  static final String PK = "id BIGINT AUTO_INCREMENT PRIMARY KEY, ";
  static final String[] SCHEMA = {
    "CREATE TABLE IF NOT EXISTS users(" + PK + "name VARCHAR(100) NOT NULL, email VARCHAR(150) NOT NULL UNIQUE, password VARCHAR(200) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
    "CREATE TABLE IF NOT EXISTS courses(" + PK + "user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE, name VARCHAR(100) NOT NULL, code VARCHAR(20), semester INT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
    "CREATE TABLE IF NOT EXISTS tags(" + PK + "user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE, name VARCHAR(50) NOT NULL, color VARCHAR(9) DEFAULT '#6c757d', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
    "CREATE TABLE IF NOT EXISTS tasks(" + PK + "user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE, course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE, title VARCHAR(150) NOT NULL, description VARCHAR(2000), deadline TIMESTAMP NOT NULL, priority VARCHAR(10) DEFAULT 'medium', status VARCHAR(10) DEFAULT 'todo', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
    "CREATE TABLE IF NOT EXISTS subtasks(" + PK + "task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE, title VARCHAR(150) NOT NULL, is_done BOOLEAN DEFAULT FALSE)",
    "CREATE TABLE IF NOT EXISTS tag_task(" + PK + "task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE, tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE, UNIQUE(task_id, tag_id))",
    "CREATE TABLE IF NOT EXISTS task_notes(" + PK + "task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE, type VARCHAR(10) DEFAULT 'note', content VARCHAR(500) NOT NULL)"};

  @Override public void contextInitialized(ServletContextEvent e) {
    for (String s : SCHEMA) x(s);
    if (((Number) one("select count(*) n from users").get("n")).longValue() == 0) {
      long u = x("insert into users(name,email,password) values(?,?,?)", "Demo", "demo@tasktrack.test", hash("password"));
      long c = x("insert into courses(user_id,name,code,semester) values(?,?,?,?)", u, "Web Programming", "EF234301", 5);
      long g = x("insert into tags(user_id,name,color) values(?,?,?)", u, "UTS", "#dc3545");
      int[] days = {2, -1, 6}; String[] names = {"Midterm WebPro", "Laporan praktikum", "Baca modul"};
      for (int i = 0; i < 3; i++) {
        long t = x("insert into tasks(user_id,course_id,title,deadline,priority) values(?,?,?,?,'high')", u, c, names[i], Timestamp.valueOf(java.time.LocalDateTime.now().plusDays(days[i])));
        x("insert into tag_task(task_id,tag_id) values(?,?)", t, g);
        x("insert into subtasks(task_id,title) values(?,?)", t, "Kerjakan soal 1");
      }
    }
  }

  static Connection c() throws SQLException { return DriverManager.getConnection(URL, USER, PASS); }
  static void bind(PreparedStatement p, Object[] a) throws SQLException { for (int i = 0; i < a.length; i++) p.setObject(i + 1, a[i]); }
  public static List<Map<String, Object>> q(String sql, Object... a) {
    try (Connection c = c(); PreparedStatement p = c.prepareStatement(sql)) {
      bind(p, a);
      try (ResultSet r = p.executeQuery()) {
        ResultSetMetaData m = r.getMetaData(); List<Map<String, Object>> out = new ArrayList<>();
        while (r.next()) { Map<String, Object> row = new HashMap<>(); for (int i = 1; i <= m.getColumnCount(); i++) row.put(m.getColumnLabel(i).toLowerCase(), r.getObject(i)); out.add(row); }
        return out;
      }
    } catch (SQLException e) { throw new RuntimeException(e); }
  }
  public static Map<String, Object> one(String sql, Object... a) { List<Map<String, Object>> l = q(sql, a); return l.isEmpty() ? null : l.get(0); }
  public static long x(String sql, Object... a) {
    try (Connection c = c(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      bind(p, a); p.executeUpdate();
      try (ResultSet k = p.getGeneratedKeys()) { return k.next() ? k.getLong(1) : 0; }
    } catch (SQLException e) { throw new RuntimeException(e); }
  }
  static byte[] pbk(String pw, byte[] salt) {
    try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(pw.toCharArray(), salt, 65536, 256)).getEncoded(); }
    catch (Exception e) { throw new RuntimeException(e); }
  }
  static String hash(String pw) { byte[] s = new byte[16]; new SecureRandom().nextBytes(s); return Base64.getEncoder().encodeToString(s) + ":" + Base64.getEncoder().encodeToString(pbk(pw, s)); }
  static boolean check(String pw, String st) { String[] p = st.split(":"); return MessageDigest.isEqual(pbk(pw, Base64.getDecoder().decode(p[0])), Base64.getDecoder().decode(p[1])); }
}

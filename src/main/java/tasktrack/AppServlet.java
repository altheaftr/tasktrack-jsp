package tasktrack;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/app/*")
public class AppServlet extends HttpServlet {
  static final ZoneId Z = ZoneId.of("Asia/Jakarta");
  static final DateTimeFormatter SHOW = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"), IN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  static final List<String> PRI = List.of("low", "medium", "high"), STA = List.of("todo", "doing", "done");

  @Override protected void service(HttpServletRequest q, HttpServletResponse s) throws ServletException, IOException {
    q.setCharacterEncoding("UTF-8");
    String path = q.getPathInfo() == null ? "/" : q.getPathInfo();
    boolean post = "POST".equals(q.getMethod());
    HttpSession ss = q.getSession();
    if (ss.getAttribute("csrf") == null) ss.setAttribute("csrf", UUID.randomUUID().toString());
    if (post && !ss.getAttribute("csrf").equals(q.getParameter("_t"))) { s.sendError(403, "Invalid token"); return; }
    Long uid = (Long) ss.getAttribute("uid");
    boolean pub = path.equals("/login") || path.equals("/register");
    if (uid == null && !pub) { go(q, s, "/login"); return; }
    if (uid != null && pub) { go(q, s, "/dashboard"); return; }
    String[] seg = path.split("/");
    q.setAttribute("nav", seg.length > 1 ? seg[1] : "");
    try { route(q, s, ss, uid == null ? 0 : uid, path, post); } catch (RuntimeException e) { throw new ServletException(e); }
  }

  void view(HttpServletRequest q, HttpServletResponse s, String v) throws ServletException, IOException { q.getRequestDispatcher("/WEB-INF/views/" + v + ".jsp").forward(q, s); }
  void go(HttpServletRequest q, HttpServletResponse s, String p) throws IOException { s.sendRedirect(q.getContextPath() + "/app" + p); }
  void back(HttpServletRequest q, HttpServletResponse s, String fb) throws IOException { String r = q.getHeader("Referer"); if (r != null) s.sendRedirect(r); else go(q, s, fb); }
  static String p(HttpServletRequest q, String n) { String v = q.getParameter(n); return v == null ? "" : v.trim(); }
  static long id(HttpServletRequest q, String n) { try { return Long.parseLong(p(q, n)); } catch (NumberFormatException e) { return 0; } }
  static LocalDateTime ldt(Object o) { return o instanceof Timestamp t ? t.toLocalDateTime() : (LocalDateTime) o; }
  static boolean mine(String table, long id, long uid) { return Db.one("select id from " + table + " where id=? and user_id=?", id, uid) != null; }
  static boolean myTask(long id, long uid) { return mine("tasks", id, uid); }

  List<Map<String, Object>> tasks(long uid, String where, Object... a) {
    Object[] args = new Object[a.length + 1]; args[0] = uid; System.arraycopy(a, 0, args, 1, a.length);
    List<Map<String, Object>> l = Db.q("select t.*, c.name course from tasks t join courses c on c.id=t.course_id where t.user_id=? and " + where + " order by t.deadline", args);
    LocalDateTime now = LocalDateTime.now(Z);
    for (Map<String, Object> r : l) {
      LocalDateTime d = ldt(r.get("deadline"));
      r.put("dl", d.format(SHOW)); r.put("dlin", d.format(IN)); r.put("dt", d);
      r.put("late", d.isBefore(now) && !"done".equals(r.get("status")));
      r.put("iso", d.atZone(Z).toOffsetDateTime().toString());
      r.put("tags", Db.q("select g.* from tags g join tag_task x on x.tag_id=g.id where x.task_id=?", r.get("id")));
    }
    return l;
  }

  void list(HttpServletRequest q, HttpServletResponse s, long uid, boolean hist) throws ServletException, IOException {
    StringBuilder w = new StringBuilder(hist ? "t.status='done'" : "t.status<>'done'"); List<Object> a = new ArrayList<>();
    if (!p(q, "q").isEmpty()) { w.append(" and lower(t.title) like ?"); a.add("%" + p(q, "q").toLowerCase() + "%"); }
    if (id(q, "course") > 0) { w.append(" and t.course_id=?"); a.add(id(q, "course")); }
    if (PRI.contains(p(q, "priority"))) { w.append(" and t.priority=?"); a.add(p(q, "priority")); }
    if (id(q, "tag") > 0) { w.append(" and exists(select 1 from tag_task x where x.task_id=t.id and x.tag_id=?)"); a.add(id(q, "tag")); }
    q.setAttribute("rows", tasks(uid, w.toString(), a.toArray())); q.setAttribute("hist", hist);
    q.setAttribute("courses", Db.q("select * from courses where user_id=? order by name", uid));
    q.setAttribute("tags", Db.q("select * from tags where user_id=? order by name", uid));
    view(q, s, "tasks");
  }

  void crud(HttpServletRequest q, HttpServletResponse s, long uid, String base) throws ServletException, IOException {
    boolean course = base.equals("courses");
    q.setAttribute("base", base); q.setAttribute("title", course ? "Mata Kuliah" : "Tag");
    q.setAttribute("fields", course ? List.of(new String[]{"name", "Nama", "text"}, new String[]{"code", "Kode", "text"}, new String[]{"semester", "Semester", "number"})
                                     : List.of(new String[]{"name", "Nama", "text"}, new String[]{"color", "Warna", "color"}));
    q.setAttribute("items", course
      ? Db.q("select c.*, (select count(*) from tasks t where t.course_id=c.id) n from courses c where c.user_id=? order by c.name", uid)
      : Db.q("select g.*, (select count(*) from tag_task x where x.tag_id=g.id) n from tags g where g.user_id=? order by g.name", uid));
    view(q, s, "crud");
  }

  void route(HttpServletRequest q, HttpServletResponse s, HttpSession ss, long uid, String path, boolean post) throws ServletException, IOException {
    switch (path) {
      case "/login": {
        if (!post) { q.setAttribute("reg", false); view(q, s, "auth"); return; }
        Map<String, Object> u = Db.one("select * from users where email=?", p(q, "email"));
        String pw = q.getParameter("password") == null ? "" : q.getParameter("password");
        if (u != null && Db.check(pw, (String) u.get("password"))) {
          q.changeSessionId(); ss.setAttribute("uid", u.get("id")); ss.setAttribute("uname", u.get("name")); go(q, s, "/dashboard"); return;
        }
        q.setAttribute("bad", "Email atau password salah"); q.setAttribute("reg", false); view(q, s, "auth"); return;
      }
      case "/register": {
        if (!post) { q.setAttribute("reg", true); view(q, s, "auth"); return; }
        String n = p(q, "name"), e = p(q, "email"), pw = q.getParameter("password") == null ? "" : q.getParameter("password");
        String err = n.isEmpty() ? "Nama wajib diisi" : !e.contains("@") ? "Email tidak valid" : pw.length() < 6 ? "Password minimal 6 karakter"
          : !pw.equals(q.getParameter("password_confirmation")) ? "Konfirmasi password tidak sama" : Db.one("select id from users where email=?", e) != null ? "Email sudah terdaftar" : null;
        if (err != null) { q.setAttribute("bad", err); q.setAttribute("reg", true); view(q, s, "auth"); return; }
        long u = Db.x("insert into users(name,email,password) values(?,?,?)", n, e, Db.hash(pw));
        q.changeSessionId(); ss.setAttribute("uid", u); ss.setAttribute("uname", n); go(q, s, "/dashboard"); return;
      }
      case "/logout": ss.invalidate(); s.sendRedirect(q.getContextPath() + "/app/login"); return;
      case "/dashboard": {
        List<Map<String, Object>> open = tasks(uid, "t.status<>'done'"), late = new ArrayList<>(), soon = new ArrayList<>();
        LocalDateTime lim = LocalDateTime.now(Z).plusDays(3); Map<String, Object> next = null;
        for (Map<String, Object> t : open) {
          if ((Boolean) t.get("late")) late.add(t);
          else { if (next == null) next = t; if (((LocalDateTime) t.get("dt")).isBefore(lim)) soon.add(t); }
        }
        long total = ((Number) Db.one("select count(*) n from tasks where user_id=?", uid).get("n")).longValue(), left = open.size();
        Map<String, Object> st = new LinkedHashMap<>(); st.put("Total tugas", total); st.put("Selesai", total - left); st.put("Belum selesai", left);
        q.setAttribute("late", late); q.setAttribute("soon", soon); q.setAttribute("next", next); q.setAttribute("stats", st);
        q.setAttribute("pct", total == 0 ? 0 : Math.round((total - left) * 100f / total));
        q.setAttribute("courses", Db.q("select * from courses where user_id=? order by name", uid));
        view(q, s, "dashboard"); return;
      }
      case "/courses": case "/tags": crud(q, s, uid, path.substring(1)); return;
      case "/courses/save": {
        long i = id(q, "id"); String n = p(q, "name"); Integer sem = null;
        try { sem = Integer.valueOf(p(q, "semester")); } catch (NumberFormatException e) { }
        if (n.isEmpty() || n.length() > 100) { ss.setAttribute("bad", "Nama wajib diisi"); back(q, s, "/courses"); return; }
        if (i > 0) Db.x("update courses set name=?, code=?, semester=? where id=? and user_id=?", n, p(q, "code"), sem, i, uid);
        else Db.x("insert into courses(user_id,name,code,semester) values(?,?,?,?)", uid, n, p(q, "code"), sem);
        ss.setAttribute("ok", "Tersimpan"); back(q, s, "/courses"); return;
      }
      case "/courses/delete": Db.x("delete from courses where id=? and user_id=?", id(q, "id"), uid); ss.setAttribute("ok", "Dihapus"); back(q, s, "/courses"); return;
      case "/tags/save": {
        long i = id(q, "id"); String n = p(q, "name"), col = p(q, "color");
        if (n.isEmpty() || n.length() > 50 || !col.matches("#[0-9a-fA-F]{6}")) { ss.setAttribute("bad", "Data tag tidak valid"); back(q, s, "/tags"); return; }
        if (i > 0) Db.x("update tags set name=?, color=? where id=? and user_id=?", n, col, i, uid);
        else Db.x("insert into tags(user_id,name,color) values(?,?,?)", uid, n, col);
        ss.setAttribute("ok", "Tersimpan"); back(q, s, "/tags"); return;
      }
      case "/tags/delete": Db.x("delete from tags where id=? and user_id=?", id(q, "id"), uid); ss.setAttribute("ok", "Dihapus"); back(q, s, "/tags"); return;
      case "/tasks": list(q, s, uid, false); return;
      case "/history": list(q, s, uid, true); return;
      case "/tasks/form": {
        long i = id(q, "id"); Map<String, Object> t = null; List<Long> sel = new ArrayList<>();
        if (i > 0) {
          List<Map<String, Object>> l = tasks(uid, "t.id=?", i);
          if (l.isEmpty()) { go(q, s, "/tasks"); return; }
          t = l.get(0); for (Map<String, Object> g : (List<Map<String, Object>>) t.get("tags")) sel.add((Long) g.get("id"));
        }
        q.setAttribute("task", t); q.setAttribute("sel", sel);
        q.setAttribute("courses", Db.q("select * from courses where user_id=? order by name", uid));
        q.setAttribute("tags", Db.q("select * from tags where user_id=? order by name", uid));
        view(q, s, "taskform"); return;
      }
      case "/tasks/save": {
        long tid = id(q, "id"), cid = id(q, "course_id"); String title = p(q, "title"), pr = p(q, "priority"), st = p(q, "status"); LocalDateTime dl;
        try { dl = LocalDateTime.parse(p(q, "deadline")); } catch (Exception e) { dl = null; }
        boolean ok = !title.isEmpty() && title.length() <= 150 && dl != null && PRI.contains(pr) && STA.contains(st) && mine("courses", cid, uid);
        if (!ok) { ss.setAttribute("bad", "Data tugas tidak valid"); go(q, s, "/tasks/form" + (tid > 0 ? "?id=" + tid : "")); return; }
        Timestamp ts = Timestamp.valueOf(dl);
        if (tid > 0) Db.x("update tasks set course_id=?, title=?, description=?, deadline=?, priority=?, status=? where id=? and user_id=?", cid, title, p(q, "description"), ts, pr, st, tid, uid);
        else tid = Db.x("insert into tasks(user_id,course_id,title,description,deadline,priority,status) values(?,?,?,?,?,?,?)", uid, cid, title, p(q, "description"), ts, pr, st);
        if (myTask(tid, uid)) {
          Db.x("delete from tag_task where task_id=?", tid);
          String[] tg = q.getParameterValues("tags");
          if (tg != null) for (String g : tg) { try { long gid = Long.parseLong(g); if (mine("tags", gid, uid)) Db.x("insert into tag_task(task_id,tag_id) values(?,?)", tid, gid); } catch (NumberFormatException e) { } }
        }
        ss.setAttribute("ok", "Tugas tersimpan"); go(q, s, "/tasks/view?id=" + tid); return;
      }
      case "/tasks/view": {
        long i = id(q, "id"); List<Map<String, Object>> l = tasks(uid, "t.id=?", i);
        if (l.isEmpty()) { go(q, s, "/tasks"); return; }
        q.setAttribute("task", l.get(0));
        q.setAttribute("subs", Db.q("select * from subtasks where task_id=? order by id", i));
        q.setAttribute("notes", Db.q("select * from task_notes where task_id=? order by id", i));
        view(q, s, "taskview"); return;
      }
      case "/tasks/delete": Db.x("delete from tasks where id=? and user_id=?", id(q, "id"), uid); ss.setAttribute("ok", "Tugas dihapus"); go(q, s, "/tasks"); return;
      case "/tasks/toggle": Db.x("update tasks set status=case when status='done' then 'todo' else 'done' end where id=? and user_id=?", id(q, "id"), uid); back(q, s, "/tasks"); return;
      case "/subtasks/add": {
        long t = id(q, "task_id"); String title = p(q, "title");
        if (myTask(t, uid) && !title.isEmpty() && title.length() <= 150) Db.x("insert into subtasks(task_id,title) values(?,?)", t, title);
        back(q, s, "/tasks"); return;
      }
      case "/subtasks/toggle": Db.x("update subtasks set is_done=not is_done where id=? and task_id in (select id from tasks where user_id=?)", id(q, "id"), uid); back(q, s, "/tasks"); return;
      case "/subtasks/delete": Db.x("delete from subtasks where id=? and task_id in (select id from tasks where user_id=?)", id(q, "id"), uid); back(q, s, "/tasks"); return;
      case "/notes/add": {
        long t = id(q, "task_id"); String type = p(q, "type"), c = p(q, "content");
        if (myTask(t, uid) && (type.equals("note") || type.equals("link")) && !c.isEmpty() && c.length() <= 500) Db.x("insert into task_notes(task_id,type,content) values(?,?,?)", t, type, c);
        back(q, s, "/tasks"); return;
      }
      case "/notes/delete": Db.x("delete from task_notes where id=? and task_id in (select id from tasks where user_id=?)", id(q, "id"), uid); back(q, s, "/tasks"); return;
      case "/profile": {
        if (!post) { q.setAttribute("me", Db.one("select * from users where id=?", uid)); view(q, s, "profile"); return; }
        String n = p(q, "name"), e = p(q, "email"), pw = q.getParameter("password") == null ? "" : q.getParameter("password");
        boolean ok = !n.isEmpty() && e.contains("@") && Db.one("select id from users where email=? and id<>?", e, uid) == null && (pw.isEmpty() || (pw.length() >= 6 && pw.equals(q.getParameter("password_confirmation"))));
        if (!ok) { ss.setAttribute("bad", "Data profil tidak valid"); go(q, s, "/profile"); return; }
        Db.x("update users set name=?, email=? where id=?", n, e, uid);
        if (!pw.isEmpty()) Db.x("update users set password=? where id=?", Db.hash(pw), uid);
        ss.setAttribute("uname", n); ss.setAttribute("ok", "Profil diperbarui"); go(q, s, "/profile"); return;
      }
      default: go(q, s, "/dashboard");
    }
  }
}

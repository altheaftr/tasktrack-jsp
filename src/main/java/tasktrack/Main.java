package tasktrack;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import java.io.File;

/** Starts an embedded Tomcat so the app runs with: java -jar tasktrack.jar */
public class Main {
  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    File web = new File(System.getProperty("webapp", "src/main/webapp")).getAbsoluteFile();
    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir(new File("data/tomcat").getAbsolutePath());
    tomcat.setPort(port);
    tomcat.getConnector();
    Context ctx = tomcat.addWebapp("", web.getAbsolutePath());
    Tomcat.addServlet(ctx, "app", new AppServlet());
    ctx.addServletMappingDecoded("/app/*", "app");
    ctx.addApplicationListener(Db.class.getName());
    tomcat.start();
    System.out.println("TaskTrack running on port " + port);
    tomcat.getServer().await();
  }
}

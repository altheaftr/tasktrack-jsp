# TaskTrack (JSP / Jakarta EE 10, embedded Tomcat 10.1)
Java 17, Maven, JSP + JSTL, servlet controller, H2 file database (auto-created, demo data seeded).

## Run locally
1. `mvn package`            -> builds target/tasktrack.jar (all libraries inside)
2. `java -jar target/tasktrack.jar`   (run from the project root folder, because JSP files are read from src/main/webapp)
3. Open http://localhost:8080/   (demo: demo@tasktrack.test / password)
Change port: set env PORT (Windows cmd: `set PORT=8081`).

## Deploy to DOM Cloud
1. Run `mvn package`, then copy `target/tasktrack.jar` to the project root as `tasktrack.jar`.
2. Commit and push (tasktrack.jar + src/main/webapp must be in the repo).
3. Paste `domcloud.yml` (with your repo URL) in DOM Cloud: Setup -> Deploy.

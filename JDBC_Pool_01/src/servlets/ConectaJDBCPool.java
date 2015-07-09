package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

@WebServlet("/ConectaJDBCPool")
public class ConectaJDBCPool extends HttpServlet {
	private static final int MIN_EVICTABLE_IDLE_TIME = 30000;
	private static final int REMOVE_ABANDONED_TIMEOUT = 60;
	private static final int TIEMPO_MAX_ESPERA_POOL = 10000;
	private static final int NUM_CONEXIONES_INICIO = 10;
	private static final int NUM_MAX_CONEXIONES_SIMULTANEAS = 100;
	private static final int TIME_BETWEEN_EVICTION = 30000;
	private static final int VALIDATION_INTERVAL = 30000;
	private static final String CONSULTA_VALIDACION = "SELECT 1";
	private static final String PASSWD = "root";
	private static final String USER = "root";
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL_BD = "jdbc:mysql://localhost:3306/scott";
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		// se definen las propiedades del pool de conexiones
        PoolProperties p = new PoolProperties();
        // p.setUrl("jdbc:mysql://localhost:3306/mysql");
        p.setUrl(URL_BD);  // url de la BD
        p.setDriverClassName(DRIVER);  // clase del driver
        p.setUsername(USER);  // usuario de la BD
        p.setPassword(PASSWD);  // clave del usuario de la BD
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery(CONSULTA_VALIDACION);  // consulta de validación (en Oracle: SELECT 1 FROM Dual) 
        p.setTestOnReturn(false);
        p.setValidationInterval(VALIDATION_INTERVAL);
        p.setTimeBetweenEvictionRunsMillis(TIME_BETWEEN_EVICTION);
        p.setMaxActive(NUM_MAX_CONEXIONES_SIMULTANEAS);  // nº max. de conexiones que se pueden orecer simultáneamente por el pool
        p.setInitialSize(NUM_CONEXIONES_INICIO);  // nº de conexiones que se crearán al inicializar el pool 
        p.setMaxWait(TIEMPO_MAX_ESPERA_POOL);  // nº max. de milisegundos que el pool espera a que se devuelva una conexión
        p.setRemoveAbandonedTimeout(REMOVE_ABANDONED_TIMEOUT);  // seconds a database connection has been idle before it is considered abandoned
        p.setMinEvictableIdleTimeMillis(MIN_EVICTABLE_IDLE_TIME);
        p.setMinIdle(10);  // mín. nº de conexiones que se pueden poner inactivas a la vez
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
          "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);

        test(out, datasource);
		
	}

	private void test(PrintWriter out, DataSource datasource) {
		Connection con = null;
        try {
          con = datasource.getConnection();
          Statement st = con.createStatement();
          // ResultSet rs = st.executeQuery("select * from user");
          ResultSet rs = st.executeQuery("select * from EMP");
          int cnt = 1;
          while (rs.next()) {
              // out.println((cnt++)+". Host:" +rs.getString("Host")+
              //   " User:"+rs.getString("User")+" Password:"+rs.getString("Password") + " <br />");
              out.println((cnt++) + ". Empno:" + rs.getLong("empno") +
                        " Ename: " + rs.getString("ename") + " Job: " + rs.getString("job") + " <br />");  
          }
          rs.close();
          st.close();
        } catch (SQLException sqlE) {
        	sqlE.printStackTrace(out);
        } finally {
          if (con!=null) 
        	  try {
        		  con.close();
        	  } catch (Exception ignore) {}
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
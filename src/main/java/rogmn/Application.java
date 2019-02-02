package rogmn;

import java.util.Map;
import java.util.Optional;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.LocalSettingsEnv;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import rogmn.db.Emfac;

public class Application
{
  public static ApplicationContext applicationContext;

  public static void main(String[] args)
  {
    applicationContext = Micronaut.run(Application.class);
    Optional<Object> ds = applicationContext.get("datasources", Object.class);
    DataSource datasource = applicationContext.getBean(DataSource.class);
    LocalSettingsEnv env = LocalSettingsEnv.get();
    env.getDataSources().put("rogmn", (BasicDataSource) datasource);
    
    try {
      new InitialContext().bind("java:comp/env/datasources/default", datasource);
    } catch (NamingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Map<String, Object> props = applicationContext.getProperties("datasources.default");
    System.out.println("Props: " + props);
    Emfac.getEmfac();
  }
}
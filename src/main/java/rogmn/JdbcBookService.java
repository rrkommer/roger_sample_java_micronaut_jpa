package rogmn;

import javax.inject.Singleton;
import javax.sql.DataSource;

import io.micronaut.context.annotation.Requires;

@Singleton
@Requires(beans = DataSource.class)
@Requires(property = "datasource.url")
public class JdbcBookService
{

  DataSource dataSource;

  public JdbcBookService(DataSource dataSource)
  {
    this.dataSource = dataSource;
  }

}
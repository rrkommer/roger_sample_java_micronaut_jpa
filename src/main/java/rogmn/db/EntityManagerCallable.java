package rogmn.db;

import javax.persistence.EntityManager;

public interface EntityManagerCallable<V>
{

  /**
   * Call for using a jpa.
   *
   * @param emgr the emgr
   * @return the v
   */
  V call(final EntityManager emgr);
}

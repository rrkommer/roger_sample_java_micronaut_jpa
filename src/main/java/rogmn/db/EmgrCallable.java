package rogmn.db;

public interface EmgrCallable<V>
{

  /**
   * Call for using a jpa.
   *
   * @param emgr the emgr
   * @return the v
   */
  V call(final Em entityManager);
}

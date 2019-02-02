package rogmn.db;

import javax.persistence.EntityManager;



public class Em
{

  /**
   * Underlying jpa.
   */
  private final EntityManager entityManager;
  /**
   * The factory created this Emgr.
   */
  private final Emfac emgrFactory;
  /**
   * The transaction.
   */
  private Tx emgrTx;
  /**
   * Instantiates a new emgr.
   *
   * @param entityManager the entity manager
   * @param emgrFactory the emgr factory
   */
  public Em(EntityManager entityManager, Emfac emgrFactory, Tx emgrTx)
  {
    this.entityManager = entityManager;
    this.emgrFactory = emgrFactory;
    this.emgrTx = emgrTx;
  }


  public EntityManager getEntityManager()
  {
    return entityManager;
  }


  public Emfac getEmgrFactory()
  {
    return emgrFactory;
  }


  public Tx getEmgrTx()
  {
    return emgrTx;
  }
  
}

package rogmn.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;

import org.hibernate.Session;
import org.hibernate.Transaction;



public class Tx
{



  /**
   * The emfac.
   */
  private Emfac emfac;

  /**
   * The requires new.
   */
  boolean requiresNew = false;

  boolean rollback = false;

  /**
   * The requires existing.
   */
  boolean requiresExisting = false;

  /**
   * The timeout.
   * 
   * TODO RK if (emHolder != null && emHolder.hasTimeout()) { int timeoutValue = (int) emHolder.getTimeToLiveInMillis();
   * try { query.setHint("javax.persistence.query.timeout", timeoutValue); } catch (IllegalArgumentException ex) { // oh
   * well, at least we tried... }
   */
  long timeout = -1;
  /**
   * This has an own transaction.
   */
  private boolean ownEmgr = false;

  private Em prevEmgr = null;

  private EntityTransaction tx;

  /**
   * Instantiates a new tx info.
   *
   * @param emfac the emfac
   */
  public Tx(Emfac emfac)
  {
    this.emfac = emfac;
  }

  private void setTransactionTimeOut(Em emgr)
  {
    if (timeout == -1) {
      return;
    }
    Session session = emgr.getEntityManager().unwrap(Session.class);
    Transaction trans = session.getTransaction();
    trans.setTimeout((int) (timeout / 1000));

  }

  private void startTransaction(Em emgr)
  {

    setTransactionTimeOut(emgr);
    tx.begin();
    if (rollback == true) {
      tx.setRollbackOnly();
    }
  }

  private void finalTransaction(Em emgr)
  {
    emgr.getEntityManager().flush();
    if (rollback == true) {
      tx.rollback();
    } else {
      tx.commit();
    }
  }

  private RuntimeException handleException(Em emgr, RuntimeException ex)
  {
    if (ex instanceof RollbackException) {
//      if (log.isDebugEnabled() == true) {
//        log.debug("rollback tx:  in thread " + Thread.currentThread().getId() + "; " + ex.getMessage());
//      }
      return ex;
    } else {
      if (tx != null && tx.isActive() == true) {
        tx.rollback();
      }
      // TODO
      return ex;
      // return EmgrFactory.convertException(ex);
    }
  }

  private void checkFlags()
  {
    if (requiresExisting == true && requiresNew == true) {
      // TODO RK throw
    }
  }

  public <R> R goem(EmgrCallable<R> call)
  {
    checkFlags();
    Em emgr = getCreateEmgr();
    try {
      if (ownEmgr == false) {
        return call.call(emgr);
      }
      tx = emgr.getEntityManager().getTransaction();
      if (tx.isActive() == true) {
        return call.call(emgr);
      }
      startTransaction(emgr);
      R ret = call.call(emgr);
      finalTransaction(emgr);
      return ret;
      // TODO RK caching ex
    } catch (RuntimeException ex) {
      throw handleException(emgr, ex);
    } finally {
      releaseEmgr(emgr);
    }
  }
  public <R> R go(EntityManagerCallable<R> call)
  {
    checkFlags();
    Em emgr = getCreateEmgr();
    try {
      if (ownEmgr == false) {
        return call.call(emgr.getEntityManager());
      }
      tx = emgr.getEntityManager().getTransaction();
      if (tx.isActive() == true) {
        return call.call(emgr.getEntityManager());
      }
      startTransaction(emgr);
      R ret = call.call(emgr.getEntityManager());
      finalTransaction(emgr);
      return ret;
      // TODO RK caching ex
    } catch (RuntimeException ex) {
      throw handleException(emgr, ex);
    } finally {
      releaseEmgr(emgr);
    }
  }
  protected Em getCreateEmgr()
  {
    if (requiresNew == true) {
      return createNewEmgr();
    }
    Em pEmgr = emfac.getThreadEmgr().get();
    if (pEmgr == null) {
      if (requiresExisting == true) {
        throw new TransactionRequiredException("no transaction is in progress");
      }
      return createNewEmgr();
    }
    Tx parentTx = pEmgr.getEmgrTx();
    boolean reqNew = false;
    if (parentTx.rollback != true && rollback == true) {
      reqNew = true;
    }

    if (reqNew == true) {
      return createNewEmgr();
    }
    return pEmgr;

  }

  protected Em createNewEmgr()
  {
    ownEmgr = true;
    EntityManager entityManager = emfac.getEntityManagerFactory().createEntityManager();
    Em emgr = emfac.createEmgr(entityManager, this);
    prevEmgr = emfac.getThreadEmgr().get();
    emfac.getThreadEmgr().set(emgr);
    return emgr;
  }

  protected void releaseEmgr(Em emgr)
  {
    if (ownEmgr == false) {
      return;
    }
    ownEmgr = false;
    emgr.getEntityManager().close();
    emfac.getThreadEmgr().set(prevEmgr);
  }

  /**
   * Requires new.
   *
   * @return the tx info
   */
  public Tx requiresNew()
  {
    requiresNew = true;
    return this;
  }

  /**
   * Requires new.
   *
   * @return the tx info
   */
  public Tx requires()
  {
    requiresExisting = true;
    return this;
  }


  /**
   * Rollback at the end of the transaction.
   * 
   * @return
   */
  public Tx rollback()
  {
    rollback = true;
    return this;
  }

  /**
   * Read only.
   *
   * @return the tx info
   */
  public Tx readOnly()
  {
    rollback = true;
    return this;
  }

  /**
   * Read only.
   *
   * @return the tx info
   */
  public Tx timeOut(long timeout)
  {
    this.timeout = timeout;
    return this;
  }

  public Emfac getEmfac()
  {
    return emfac;
  }

  public void setEmfac(Emfac emfac)
  {
    this.emfac = emfac;
  }

  public boolean isRequiresNew()
  {
    return requiresNew;
  }

  public void setRequiresNew(boolean requiresNew)
  {
    this.requiresNew = requiresNew;
  }

  public boolean isRequiresExisting()
  {
    return requiresExisting;
  }

  public void setRequiresExisting(boolean requiresExisting)
  {
    this.requiresExisting = requiresExisting;
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }
}

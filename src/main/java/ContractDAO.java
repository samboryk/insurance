import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {
    public void save(Contract contract) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try { tx = session.beginTransaction(); session.saveOrUpdate(contract); tx.commit();
        } catch (Exception e) { if (tx != null) tx.rollback(); e.printStackTrace();
        } finally { session.close(); }
    }
    public List<Contract> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Contract> list = new ArrayList<>();
        try { list = session.createQuery("from Contract", Contract.class).getResultList();
        } catch (Exception e) { e.printStackTrace(); } finally { session.close(); }
        return list;
    }
    public void delete(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try { tx = session.beginTransaction(); Contract c = session.get(Contract.class, id);
            if (c != null) session.delete(c); tx.commit();
        } catch (Exception e) { if (tx != null) tx.rollback(); e.printStackTrace();
        } finally { session.close(); }
    }
}
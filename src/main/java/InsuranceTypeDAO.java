import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;

public class InsuranceTypeDAO {
    public void save(InsuranceType type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(type);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<InsuranceType> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<InsuranceType> list = new ArrayList<>();
        try {
            list = session.createQuery("from InsuranceType", InsuranceType.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return list;
    }

    public void delete(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            InsuranceType t = session.get(InsuranceType.class, id);
            if (t != null) session.delete(t);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
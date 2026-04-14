import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();

            // ВИДАЛЕНО: configuration.configure();
            // Без .configure() Hibernate автоматично шукає hibernate.properties

            // Додаємо entity класи
            configuration.addAnnotatedClass(Branch.class);
            configuration.addAnnotatedClass(InsuranceType.class);
            configuration.addAnnotatedClass(Contract.class);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("=== ПОМИЛКА ПРИ СТВОРЕННІ SessionFactory ===");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
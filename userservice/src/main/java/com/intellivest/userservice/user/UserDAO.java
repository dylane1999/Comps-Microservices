package com.intellivest.userservice.user;

import com.intellivest.userservice.exceptions.UserNotFoundException;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// class that is used to interact with the database layer
@Component
public class UserDAO {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * method that is used to get all users in DB
     * 
     * @return User - returns the users
     */
    public User getAllUsers() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        /** uses the class entity name here, not table name */
        List<User> result = session.createQuery("FROM User", User.class).list();
        result.forEach(person -> {
            log.info(person.toString());
        });
        session.getTransaction().commit();
        session.close();
        return result.get(0);

    }

    /**
     * function to get a user by ID from the DB
     * 
     * @return User - returns the user that was queried by UID
     */
    public User getUserByID(String Id) {
        try {
            // create sql transaction session
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            // define and execute query with param id,
            // note that class entity name is used, not table name
            Query<User> userQuery = session.createQuery("select u from User u where id = :id ", User.class);
            userQuery.setParameter("id", Id);
            User user = userQuery.getSingleResult();
            // end sql transaction and commit changes
            session.getTransaction().commit();
            session.close();
            return user;
        } catch (NoResultException e) {
            throw new UserNotFoundException("user with that Id was not found in the databse");
        }
    }

    /**
     * function that is used to add the given user to the database
     * 
     * @param user
     * @return
     * @throws Exception
     */
    public User addPerson(User user) throws Exception {
        // create sql transaction session
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        // persist the user into the db
        session.persist(user);
        // end sql transaction and commit changes
        session.getTransaction().commit();
        session.close();
        log.info("user created", user.toString());
        return user;
    }

    /**
     * function that is used to delete a user from the database
     * 
     * @param user - the user to be deleted
     */
    public void deletePerson(User user) {
        // create sql transaction session
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        // delete user from db
        session.delete(user);
        // end sql transaction and commit changes
        session.getTransaction().commit();
        session.close();
    }

}

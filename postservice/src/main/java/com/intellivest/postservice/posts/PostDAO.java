package com.intellivest.postservice.posts;

import com.intellivest.postservice.exceptions.PostNotFoundException;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * post DAO object
 */
@Component
public class PostDAO {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Post.class);

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * data access method to add a new post to database
     * 
     * @param post
     * @return
     * @throws Exception
     */
    public void addPost(Post post) {
        /** commit object to sql db */
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.persist(post);
        session.getTransaction().commit();
        session.close();
        log.info("post created with id: {}", post.getId());
        return;

    }

    /**
     * data access method to get a list of posts created by the given user Id
     * 
     * @param Id
     * @return
     */
    public List<Post> getPostsByUser(String Id) {
        // setup sql session
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        // do query with given user id
        Query<Post> userQuery = session.createQuery("select p from Post p where poster_id = :poster_id ", Post.class);
        userQuery.setParameter("poster_id", Id);
        // get and return the list of posts
        List<Post> posts = userQuery.list();
        session.getTransaction().commit();
        session.close();
        return posts;
    }

    /**
     * data access method that is used to get the post info by the given Id
     * 
     * @param Id
     * @return
     * @throws PostNotFoundException
     * @throws Exception
     */
    public Post getPostById(String postId) {
        try {

            Session session = sessionFactory.openSession();
            session.beginTransaction();

            Query<Post> userQuery = session.createQuery("select p from Post p where id = :id ", Post.class);
            userQuery.setParameter("id", postId);
            Post post = userQuery.getSingleResult();

            session.getTransaction().commit();
            session.close();
            return post;
        } catch (Exception e) {
            if (e instanceof NoResultException) {
                log.error("post with id : {} was not found in the databse", postId);
                throw new PostNotFoundException("post does not exist");
            }
            log.error("query execution failed");
            throw e;
        }
    }

    /**
     * delete the given post from the database
     * @param post
     */
    public void deletePost(Post post) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(post);
        session.getTransaction().commit();
        session.close();
    }

}

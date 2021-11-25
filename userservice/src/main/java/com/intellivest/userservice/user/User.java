package com.intellivest.userservice.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity bean for the user object
 */
@Entity
@Table(name = "users")
public class User {

    // @GeneratedValue(strategy = GenerationType.IDENTITY)

    /** ID */
    @Id
    @Column(name = "id", nullable = false, unique = true)
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** FIRST NAME */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** LAST NAME */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** EMAIL */
    @Column(name = "email", nullable = false)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "id=" + id + ", name=" + firstName + ", email=" + email;
    }

    /**
     * constructor method to create a new User - cannot use the public constructor
     * due to entity status
     * 
     * @param Id
     * @param firstName
     * @param lastName
     * @param email
     * @return
     */
    public static User createUser(String Id, String firstName, String lastName, String email) {
        User user = new User();
        user.setId(Id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return user;
    }

    /**
     * method that is used to determine if a given user has the same fields as
     * caller user
     * 
     * @param otherUser - the user to compare
     * @return
     */
    public boolean isUserTheSame(User otherUser) {
        if (!this.getId().equals(otherUser.getId())) {
            return false;
        }
        if (!this.getEmail().equals(otherUser.getEmail())) {
            return false;
        }
        if (!this.getFirstName().equals(otherUser.getFirstName())) {
            return false;
        }
        if (!this.getLastName().equals(otherUser.getLastName())) {
            return false;
        }
        return true;
    }
}

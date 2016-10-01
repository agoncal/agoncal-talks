package org.expenses.web.view.account;

import org.expenses.web.beans.DigestPassword;
import org.expenses.web.beans.Encrypted;
import org.expenses.web.model.User;
import org.expenses.web.model.UserRole;
import org.expenses.web.service.UserService;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import java.io.Serializable;

@Named
@SessionScoped
public class AccountBean implements Serializable {

    // ======================================
    // = Attributes =
    // ======================================

    @Inject
    private FacesContext facesContext;

    @Inject
    private UserService service;

    @Inject
    @Encrypted
    private DigestPassword digestPassword;

    @Inject
    @Any
    private Instance<DigestPassword> digestPasswords;

    // Logged user
    private User user = new User();

    // Checks if the user is logged in and if he/she is an administrator (UserRole.Admin)
    private boolean loggedIn;
    private boolean admin;

    private String login;
    private String password;
    private String password1;
    private String password2;

    // ======================================
    // = Business methods =
    // ======================================

    public String doSignup() {
        // Does the login already exists ?
        if (service.findByLogin(user.getLogin()).size() > 0) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Login already exists " + user.getLogin(),
                            "You must choose a different login"));
            return null;
        }

        // Everything is ok, we can create the user
        user.setPassword(password1);
        user = service.persist(user);
        resetPasswords();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Hi " + user.getName(), "Welcome to this website"));
        loggedIn = true;
        if (user.getRole().equals(UserRole.ADMIN))
            admin = true;

        return "/index";
    }

    public String doSignin() {

        // Looks for all password digester implementations
        for (DigestPassword digester : digestPasswords) {
            try {
                user = service.findByLoginPassword(login, digester.digest(password));
                break;
            } catch (NoResultException e) {
                user = null;
            }
        }

        if (user == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Wrong user/password",
                    "Check your inputs or ask for a new password"));
            return null;
        }

        // If the user is an administrator
        if (user.getRole().equals(UserRole.ADMIN)) {
            admin = true;
        }

        // The user is now logged in
        loggedIn = true;
        return "/index";
    }

    public String doLogout() {
        return "/index";
    }

    public String doUpdateProfile() {
        if (password1 != null && !password1.isEmpty())
            user.setPassword(digestPassword.digest(password1));
        user = service.merge(user);
        resetPasswords();
        admin = user.getRole().equals(UserRole.ADMIN);

        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile has been updated for " + user.getName(),
                        null));

        return "/index";
    }

    private void resetPasswords() {
        password1 = null;
        password2 = null;
    }

    // ======================================
    // = Getters & setters =
    // ======================================

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public UserRole[] getRoles() {
        return UserRole.values();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

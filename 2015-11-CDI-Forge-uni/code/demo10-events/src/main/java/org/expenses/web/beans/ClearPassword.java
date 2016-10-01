package org.expenses.web.beans;

@Clear
public class ClearPassword implements DigestPassword {
    public String digest(String plainTextPassword) {
        return plainTextPassword;
    }
}

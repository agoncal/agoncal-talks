package org.expenses.web.beans;

import java.io.Serializable;

public interface DigestPassword extends Serializable {
    String digest(String plainTextPassword);
}

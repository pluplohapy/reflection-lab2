package org.example.testmodel;

public class SecretHolder {

    private String secret;

    private SecretHolder() {
    }

    public String revealSecret() {
        return secret;
    }
}

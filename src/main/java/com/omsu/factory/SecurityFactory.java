package com.omsu.factory;

import com.omsu.auth.CookieAuthChecker;
import com.omsu.auth.IAuthChecker;
import com.omsu.configProperties.ConfigProperties;
import com.omsu.crypto.hash.IHash;
import com.omsu.crypto.hash.SHA256;
import com.omsu.crypto.signature.ISignature;
import com.omsu.crypto.signature.SHA256withRSA;

/**
 * Created by dkuzmin on 8/11/2016.
 */
public class SecurityFactory {

    private static String tokenHashAlgorithm = ConfigProperties.getProperties().getProperty("token.signature.algorithm");
    private static String passwordHashAlgorithm = ConfigProperties.getProperties().getProperty("password.hash.algorithm");
    private static String authChecker = ConfigProperties.getProperties().getProperty("auth.checker");

    public static ISignature getTokenSignature() {
        if (tokenHashAlgorithm.equals("SHA256withRSA")) {
            return new SHA256withRSA();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for token.signature.algorithm property " + tokenHashAlgorithm + "!");
        }
    }

    public static IHash getPasswordHash() {
        if (passwordHashAlgorithm.equals("SHA256")) {
            return new SHA256();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for password.hash.algorithm " + passwordHashAlgorithm + "!");
        }
    }

    public static IAuthChecker getAuthChecker() {
        if (authChecker.equals("cookie")) {
            return new CookieAuthChecker();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for auth.checker " + authChecker + "!");
        }
    }
}

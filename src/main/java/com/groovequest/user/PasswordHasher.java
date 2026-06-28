package com.groovequest.user;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import java.security.GeneralSecurityException;

@ApplicationScoped
public class PasswordHasher {

    public String hash(String rawPass) {
        return BcryptUtil.bcryptHash(rawPass);
    }

    public boolean matches(String rawPass, String storedHash) {
        try {
            PasswordFactory passwordFactory = PasswordFactory.getInstance(
                    BCryptPassword.ALGORITHM_BCRYPT,
                    new WildFlyElytronPasswordProvider()
            );

            BCryptPassword decoded = (BCryptPassword) passwordFactory.translate(ModularCrypt.decode(storedHash));

            return passwordFactory.verify(decoded, rawPass.toCharArray());
        } catch (GeneralSecurityException e) {
            return false;
        }
    }
}

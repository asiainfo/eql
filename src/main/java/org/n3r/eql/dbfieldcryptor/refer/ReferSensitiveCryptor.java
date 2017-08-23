package org.n3r.eql.dbfieldcryptor.refer;

import lombok.val;
import org.n3r.eql.dbfieldcryptor.EqlSecretFieldsConnectionProxy;
import org.n3r.eql.dbfieldcryptor.SensitiveCryptor;
import org.n3r.eql.dbfieldcryptor.refer.aes.AesCryptor;
import org.n3r.eql.util.S;

public class ReferSensitiveCryptor implements SensitiveCryptor {
    private AesCryptor aesCryptor;

    public ReferSensitiveCryptor() {
        val eqlConfig = EqlSecretFieldsConnectionProxy.threadLocal.get();
        if (eqlConfig == null) return;

        val password = eqlConfig.getStr("securetDatabaseFields.password");
        if (S.isBlank(password)) return;

        this.aesCryptor = new AesCryptor(password);
    }

    @Override
    public String encrypt(String data) {
        return aesCryptor == null ? data : aesCryptor.encrypt(data);
    }

    @Override
    public String decrypt(String data) {
        return aesCryptor == null ? data : aesCryptor.decrypt(data);
    }
}

package org.n3r.eql.dbfieldcryptor.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.n3r.eql.dbfieldcryptor.SensitiveCryptor;
import org.n3r.eql.dbfieldcryptor.parser.SensitiveFieldsParser;
import org.n3r.eql.util.O;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;

@Slf4j @AllArgsConstructor
public class ResultSetHandler implements InvocationHandler {
    private ResultSet resultSet;
    private SensitiveFieldsParser parser;
    private SensitiveCryptor cryptor;

    @Override
    public Object invoke(
            Object proxy,
            Method method,
            Object[] args) throws Throwable {
        Object result = method.invoke(resultSet, args);
        if (result == null) return null;

        if (O.in(method.getName(), "getString", "getObject")
                && parser.inResultIndicesOrLabel(args[0])) {
            try {
                String data = result.toString();
                if (data.length() > 1) result = cryptor.decrypt(data);
            } catch (Exception e) {
                log.warn("Decrypt result #{}# error", result);
            }
        }

        return result;
    }

    public ResultSet createResultSetProxy() {
        return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{ResultSet.class}, this);
    }
}

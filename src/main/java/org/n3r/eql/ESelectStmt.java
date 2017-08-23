package org.n3r.eql;

import lombok.SneakyThrows;
import org.n3r.eql.impl.EqlRsRetriever;
import org.n3r.eql.map.EqlRun;
import org.n3r.eql.param.EqlParamsBinder;
import org.n3r.eql.util.Closes;
import org.slf4j.Logger;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ESelectStmt implements Closeable, EStmt {
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private boolean resultSetNext;
    private EqlRsRetriever rsRetriever;
    private int rowNum;
    private EqlRun eqlRun;
    private Logger logger;
    private Object[] params;
    private int fetchSize;
    private String sqlClassPath;

    public void executeQuery() {
        executeQuery(params);
    }

    @SneakyThrows
    public void executeQuery(Object... params) {
        resultSetNext = true;
        rowNum = 0;

        eqlRun.setParams(params);
        new EqlParamsBinder().prepareBindParams(false, eqlRun);

        eqlRun.bindParams(preparedStatement, sqlClassPath);
        resultSet = preparedStatement.executeQuery();
        if (fetchSize > 0) resultSet.setFetchSize(fetchSize);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T next() {
        if (!resultSetNext) return null;

        T rowBean = (T) rsRetriever.selectRow(resultSet, ++rowNum);
        if (rowBean == null) {
            resultSetNext = false;
            closeRs();
        }
        return rowBean;
    }

    public void closeRs() {
        Closes.closeQuietly(resultSet);
        resultSet = null;
    }

    @Override
    public void closeStmt() {
        Closes.closeQuietly(preparedStatement);
        preparedStatement = null;
    }

    @Override
    public void setPreparedStatment(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public void setRsRetriever(EqlRsRetriever rsRetriever) {
        this.rsRetriever = rsRetriever;
    }

    @Override
    public void close() {
        closeRs();
        closeStmt();
    }

    @Override
    public void setEqlRun(EqlRun eqlRun) {
        this.eqlRun = eqlRun;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void setEqlTran(EqlTran eqlTran) {
    }

    @Override
    public void params(Object... params) {
        this.params = params;
    }

    @Override
    public Object[] getParams() {
        return this.params;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public void setSqlClassPath(String sqlClassPath) {
        this.sqlClassPath = sqlClassPath;
    }
}

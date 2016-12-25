package com.omsu.mySQL.DAO;

import com.omsu.core.Rate;
import com.omsu.iDAO.IBasicService;
import com.omsu.mySQL.databaseConnection.DBConnection;
import com.omsu.mySQL.databaseConnection.DBConnectionPoolSingleton;
import org.apache.log4j.Logger;

import javax.naming.NamingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dkuzmin on 7/6/2016.
 */
public class RateService implements IBasicService {

    final static Logger logger = Logger.getLogger("resource");

    public Rate getFromResultSet(ResultSet resultSet) throws SQLException {
        Rate rate = new Rate();
        rate.setId(resultSet.getInt("id"));
        rate.setName(resultSet.getString("fldName"));
        rate.setDescription(resultSet.getString("fldDescription"));
        if(logger.isDebugEnabled()){
            logger.debug("Rate from result set: " + rate);
        }
        return rate;
    }

    @Override
    public List<Rate> getAll() throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started.");
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        List<Rate> rates = new ArrayList<>();
        String query = "select * from tbANIMERate";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            rates.add(getFromResultSet(resultSet));
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function rates: " + rates);
        }
        return rates;
    }

    @Override
    public Rate getById(Integer id) throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param id="+id);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        String query = "select * from tbANIMERate where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        Rate rate = null;
        if(resultSet.next()) {
            rate = getFromResultSet(resultSet);
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function rate: " + rate);
        }
        return rate;
    }

    @Override
    public void deleteById(Integer id) throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param id="+id);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);

        String query = "delete from tbANIMERate where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function deleted rate with id=" + id);
        }
    }

    @Override
    public Rate add(Object object) throws SQLException, NamingException {
        Rate rate = (Rate) object;
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param rate="+rate);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);

        String query = "insert into tbANIMERate (fldName, fldDescription) values(?, ?)";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, rate.getName());
        preparedStatement.setString(2, rate.getDescription());
        preparedStatement.executeUpdate();
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                rate.setId(generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating rate failed, no id obtained.");
            }
        }

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function rate: " + rate);
        }
        return rate;
    }

    @Override
    public Rate update(Object object) throws SQLException, NamingException {
        Rate rate = (Rate) object;
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param rate="+rate);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);

        String query = "update tbANIMERate set fldName = ?, fldDescription = ? where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setString(1, rate.getName());
        preparedStatement.setString(2, rate.getDescription());
        preparedStatement.setInt(3, rate.getId());
        Integer result = preparedStatement.executeUpdate();

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function rate: " + rate);
        }
        return rate;
    }


}

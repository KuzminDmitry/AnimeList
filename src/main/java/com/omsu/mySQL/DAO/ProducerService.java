package com.omsu.mySQL.DAO;

import com.omsu.core.Producer;
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
public class ProducerService implements IBasicService {

    final static Logger logger = Logger.getLogger("resource");

    public Producer getFromResultSet(ResultSet resultSet) throws SQLException {
        Producer producer = new Producer();
        producer.setId(resultSet.getInt("id"));
        producer.setName(resultSet.getString("fldName"));
        producer.setDescription(resultSet.getString("fldDescription"));
        if(logger.isDebugEnabled()){
            logger.debug("Producer from result set: " + producer);
        }
        return producer;
    }

    @Override
    public List<Producer> getAll() throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started.");
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        List<Producer> producers = new ArrayList<>();
        String query = "select * from tbANIMEProducer";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            producers.add(getFromResultSet(resultSet));
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function producers: " + producers);
        }
        return producers;
    }

    @Override
    public Producer getById(Integer id) throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param id="+id);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        String query = "select * from tbANIMEProducer where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        Producer producer = null;
        if(resultSet.next()) {
            producer = getFromResultSet(resultSet);
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function producer: " + producer);
        }
        return producer;
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

        String query = "delete from tbANIMEProducer where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function deleted producer with id=" + id);
        }
    }

    @Override
    public Producer add(Object object) throws SQLException, NamingException {
        Producer producer = (Producer) object;
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param producer="+producer);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);

        String query = "insert into tbANIMEProducer (fldName, fldDescription) values(?, ?)";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, producer.getName());
        preparedStatement.setString(2, producer.getDescription());
        preparedStatement.executeUpdate();
        try (ResultSet geneproducerdKeys = preparedStatement.getGeneratedKeys()) {
            if (geneproducerdKeys.next()) {
                producer.setId(geneproducerdKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating producer failed, no id obtained.");
            }
        }

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function producer: " + producer);
        }
        return producer;
    }

    @Override
    public Producer update(Object object) throws SQLException, NamingException {
        Producer producer = (Producer) object;
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param producer="+producer);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);

        String query = "update tbANIMEProducer set fldName = ?, fldDescription = ? where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setString(1, producer.getName());
        preparedStatement.setString(2, producer.getDescription());
        preparedStatement.setInt(3, producer.getId());
        Integer result = preparedStatement.executeUpdate();

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function producer: " + producer);
        }
        return producer;
    }


}

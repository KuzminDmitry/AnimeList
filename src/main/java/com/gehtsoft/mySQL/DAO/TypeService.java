package com.gehtsoft.mySQL.DAO;

import com.gehtsoft.core.Type;
import com.gehtsoft.iDAO.IBasicService;
import com.gehtsoft.mySQL.databaseConnection.DBConnection;
import com.gehtsoft.mySQL.databaseConnection.DBConnectionPoolSingleton;
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
public class TypeService implements IBasicService {

    final static Logger logger = Logger.getLogger("resource");

    public Type getFromResultSet(ResultSet resultSet) throws SQLException {
        Type type = new Type();
        type.setId(resultSet.getInt("id"));
        type.setName(resultSet.getString("fldName"));
        type.setDescription(resultSet.getString("fldDescription"));
        if(logger.isDebugEnabled()){
            logger.debug("Type from result set: " + type);
        }
        return type;
    }

    @Override
    public List<Type> getAll() throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started.");
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        List<Type> types = new ArrayList<>();
        String query = "select * from tbANIMEType";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            types.add(getFromResultSet(resultSet));
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function types: " + types);
        }
        return types;
    }

    @Override
    public Type getById(Integer id) throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param id="+id);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        String query = "select * from tbANIMEType where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        Type type = null;
        if(resultSet.next()) {
            type = getFromResultSet(resultSet);
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function type: " + type);
        }
        return type;
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

        String query = "delete from tbANIMEType where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function deleted type with id=" + id);
        }
    }

    @Override
    public Type add(Object object) throws SQLException, NamingException {
        Type type = (Type) object;
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param type="+type);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);

        String query = "insert into tbANIMEType (fldName, fldDescription) values(?, ?)";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, type.getName());
        preparedStatement.setString(2, type.getDescription());
        preparedStatement.executeUpdate();
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                type.setId(generatedKeys.getInt(1));
            }
            else {
                throw new SQLException("Creating type failed, no id obtained.");
            }
        }

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function type: " + type);
        }
        return type;
    }

    @Override
    public Type update(Object object) throws SQLException, NamingException {
        Type type = (Type) object;
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param type="+type);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);

        String query = "update tbANIMEType set fldName = ?, fldDescription = ? where id = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setString(1, type.getName());
        preparedStatement.setString(2, type.getDescription());
        preparedStatement.setInt(3, type.getId());
        Integer result = preparedStatement.executeUpdate();

        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function type: " + type);
        }
        return type;
    }


}

package com.omsu.mySQL.DAO;

import com.omsu.crypto.hash.IHash;
import com.omsu.factory.SecurityFactory;
import com.omsu.mySQL.databaseConnection.DBConnectionPoolSingleton;
import com.omsu.core.User;
import com.omsu.iDAO.IUserService;
import com.omsu.mySQL.databaseConnection.DBConnection;
import com.mysql.jdbc.Statement;
import org.apache.log4j.Logger;

import javax.naming.NamingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dkuzmin on 7/6/2016.
 */
public class UserService implements IUserService {

    final static Logger logger = Logger.getLogger("authenticate");

    public User getFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("userId"));
        user.setUserName(resultSet.getString("userName"));
        user.setPassword(resultSet.getString("userPasswordHash"));
        //Parse userRoleIds
        String userRoleIdsString = resultSet.getString("userRoleIds");
        List<Integer> userRoleIds = new ArrayList<>();
        if (userRoleIdsString != null) {
            List<String> temp = Arrays.asList(userRoleIdsString.split(","));
            for (String s : temp) userRoleIds.add(Integer.valueOf(s));
        } else {
            userRoleIds = new ArrayList<>();
        }
        //
        user.setRoleIds(userRoleIds);
        //
        //Parse userRoleNames
        String userRoleNamesString = resultSet.getString("userRoleNames");
        List<String> userRoleNames = new ArrayList<>();
        if (userRoleNamesString != null) {
            userRoleNames = Arrays.asList(userRoleNamesString.split(","));
        } else {
            userRoleNames = new ArrayList<>();
        }
        //
        user.setRoleNames(userRoleNames);
        //
        if(logger.isDebugEnabled()){
            logger.debug("User from result set: " + user);
        }
        return user;
    }

    @Override
    public User getByFilter(User user) throws SQLException, NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param user="+user);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        String query = "select * from userInfo where userName = ? and userPasswordHash = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setString(1, user.getUserName());
        preparedStatement.setString(2, user.getPassword());
        ResultSet resultSet = preparedStatement.executeQuery();
        user = null;
        if (resultSet.next()) {
            user = getFromResultSet(resultSet);
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function user: " + user);
        }
        return user;
    }

    @Override
    public List<User> getAll() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Started");
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        List<User> users = new ArrayList<>();
        String query = "select * from userInfo";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            users.add(getFromResultSet(resultSet));
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function users: " + users);
        }
        return users;
    }

    @Override
    public Object getById(Integer id) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param id="+id);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        String query = "select * from userInfo where userId = ? ";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        User user = null;
        if(resultSet.next()) {
            user = getFromResultSet(resultSet);
        }
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function user: " + user);
        }
        return user;
    }

    @Override
    public void deleteById(Integer id) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Started with param id="+id);
        }
        DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        while (dbConnection == null) {
            dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
        }
        dbConnection.setAutoCommit(false);
        String query = "delete from tbANIMEUserNNRole where fldLinkUser = ?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        query = "delete from tbANIMEAuth where fldLinkUser = ?";
        preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        query = "delete from tbANIMEUser where id = ?";
        preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        dbConnection.commit();
        DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
        if (logger.isDebugEnabled()) {
            logger.debug("Result of function deleted user with id=" + id);
        }
    }

    @Override
    public User add(Object object) throws Exception {
        IHash hashService = SecurityFactory.getPasswordHash();
        User user = (User) object;
        user.setPassword(hashService.hash(user.getPassword()));
        if (user.getRoleIds().size() != 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Started with param user="+user);
            }
            DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
            while (dbConnection == null) {
                dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
            }
            dbConnection.setAutoCommit(false);
            String query = "insert into tbANIMEUser (fldUserName, fldPasswordHash) values(?, ?)";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no id obtained.");
                }
            }
            query = "insert into tbANIMEUserNNRole (fldLinkRole, fldLinkUser) values (?, ?)";
            for (Integer i : user.getRoleIds()) {
                preparedStatement = dbConnection.prepareStatement(query);
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, user.getId());
                preparedStatement.executeUpdate();
            }
            dbConnection.commit();
            DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
            if (logger.isDebugEnabled()) {
                logger.debug("Result of function user: " + user);
            }
            return user;
        } else {
            return null;
        }
    }

    @Override
    public Object update(Object object) throws Exception {
        User user = (User) object;
        if(user.getRoleIds().size() != 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Started with param user="+user);
            }
            DBConnection dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
            while (dbConnection == null) {
                dbConnection = DBConnectionPoolSingleton.getInstance().getDBConnection();
            }
            dbConnection.setAutoCommit(false);
            String query = "update tbANIMEUser set fldUserName = ?, fldPasswordHash = ? where id = ?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getId());
            preparedStatement.executeUpdate();
            //
            query = "delete from tbANIMEUserNNRole where fldLinkUser = ?";
            preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.executeUpdate();
            //
            query = "insert into tbANIMEUserNNRole (fldLinkRole, fldLinkUser) values (?, ?)";
            for (Integer i : user.getRoleIds()) {
                preparedStatement = dbConnection.prepareStatement(query);
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, user.getId());
                preparedStatement.executeUpdate();
            }
            dbConnection.commit();
            DBConnectionPoolSingleton.getInstance().retrieveDBConnection(dbConnection);
            if (logger.isDebugEnabled()) {
                logger.debug("Result of function user: " + user);
            }
            return user;
        }else {
            return null;
        }
    }
}

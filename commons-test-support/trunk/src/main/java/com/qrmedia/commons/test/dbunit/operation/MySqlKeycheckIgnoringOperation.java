/*
 * @(#)MySqlKeyIgnoringOperation.java     22 Nov 2007
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package com.qrmedia.commons.test.dbunit.operation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * Ensures MySQL foreign key checks are ignored while the actual operation is executed, and
 * reset to their previous setting once the operation has completed.
 * <p>
 * If the target database is <i>not</i> MySQL, the decorated operation is simply executed
 * as normal.
 *  
 * @author aphillips
 * @since 22 Nov 2007
 *
 */
public class MySqlKeycheckIgnoringOperation extends DatabaseOperation {
    
    // the name MySQL returns as a result of "DatabaseMetaData.getDatabaseProductName"
    private static final String MYSQL_PRODUCT_NAME = "MySQL";
    
    // the name of the parameter used by MySQL to set the state of foreign key checking
    private static final String MYSQL_KEYCHECK_PARAMETER_NAME = "@@foreign_key_checks";
    
    private static final transient Log LOG = LogFactory.getLog(MySqlKeycheckIgnoringOperation.class);
    
    private DatabaseOperation decoratedOperation;

    /**
     * Creates an instance of this class wrapping the given operation.
     * 
     * @param operation the operation to be executed without key checks
     */
    public MySqlKeycheckIgnoringOperation(DatabaseOperation operation) {
        
        if (operation == null) {
            throw new IllegalArgumentException("'operation' may not be null");
        }
        
        this.decoratedOperation = operation;
    }
    
    /* (non-Javadoc)
     * @see org.dbunit.operation.DatabaseOperation#execute(org.dbunit.database.IDatabaseConnection, org.dbunit.dataset.IDataSet)
     */
    @Override
    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException {
        Connection jdbcConnection = connection.getConnection();
        Statement statement = null;
        String dbProductName = jdbcConnection.getMetaData().getDatabaseProductName();
        boolean dbProviderIsMySql = 
            ((dbProductName != null) && dbProductName.contains(MYSQL_PRODUCT_NAME));
        Integer initialParameterValue = null;
        
        // the SQL to get and set the key checking variable's value
        final String getParameterValueSql = "select " + MYSQL_KEYCHECK_PARAMETER_NAME;
        final String setParameterValueSql = "set " + MYSQL_KEYCHECK_PARAMETER_NAME + " = ";
        
        // if running on MySQL, backup the existing value of the parameter and switch off key checking
        if (dbProviderIsMySql) {
            ResultSet resultSet;
            statement = jdbcConnection.createStatement();
            resultSet = statement.executeQuery(getParameterValueSql);
            
            // the select statment returns only one row of one column - the value of the parameter
            resultSet.next();
            initialParameterValue = Integer.valueOf(resultSet.getInt(1));
 
            if (LOG.isInfoEnabled()) {
                LOG.info("Disabling MySQL foreign key checking.");
            }
            
            // XXX: could perhaps use a prepared statement for this, as it will be called twice
            statement.execute(setParameterValueSql + "0");
        }
        
        // execute the actual operation
        decoratedOperation.execute(connection, dataSet);
        
        // restore the value of the key checking property, if running on MySQL, and clean up
        if (dbProviderIsMySql) {
            
            if (LOG.isInfoEnabled()) {
                LOG.info("Enabling MySQL foreign key checking.");
            }
            
            statement.execute(setParameterValueSql + initialParameterValue);
            statement.close();
        }
        
        // leave the connection open - DbUnit is responsible for handling it
    }

}

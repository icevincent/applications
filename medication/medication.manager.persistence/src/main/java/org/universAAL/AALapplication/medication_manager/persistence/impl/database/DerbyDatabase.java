/*******************************************************************************
 * Copyright 2012 , http://www.prosyst.com - ProSyst Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.universAAL.AALapplication.medication_manager.persistence.impl.database;

import org.universAAL.AALapplication.medication_manager.configuration.ConfigurationProperties;
import org.universAAL.AALapplication.medication_manager.configuration.SqlScriptParser;
import org.universAAL.AALapplication.medication_manager.configuration.PropertyInfo;
import org.universAAL.AALapplication.medication_manager.persistence.impl.Log;
import org.universAAL.AALapplication.medication_manager.persistence.impl.MedicationManagerPersistenceException;
import org.universAAL.AALapplication.medication_manager.persistence.layer.SqlUtility;

import java.io.BufferedReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.sql.Types.*;
import static org.universAAL.AALapplication.medication_manager.persistence.impl.Activator.*;

/**
 * @author George Fournadjiev
 */
public final class DerbyDatabase implements Database {

  private final Connection connection;
  private final SqlUtility sqlUtility;

  public static final String MEDICATION_MANAGER = "Medication_Manager";
  private final ConfigurationProperties configurationProperties;

  public DerbyDatabase(Connection connection, ConfigurationProperties configurationProperties) {
    validateParameter(connection, "connection");
    validateParameter(configurationProperties, "configurationProperties");

    this.connection = connection;
    this.sqlUtility = new DerbySqlUtility(connection, configurationProperties);
    this.configurationProperties = configurationProperties;
  }

  public SqlUtility getSqlUtility() {
    return sqlUtility;
  }

  public void initDatabase() throws Exception {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    if (checkIfTablesNotExists()) {
      createTablesAndPopulateThemInOneTransaction();
      createSequence();
    }
  }

  public int getNextIdFromIdGenerator() {
    return sqlUtility.generateId();

  }

  public Map<String, Column> getById(String tableName, int id) {

    String message = "There are more than one record with the following id : " + id;

    String sqlQuery = "select * from " + MEDICATION_MANAGER + '.' + tableName +
        "\n\t where id=" + id;

    return getStringColumnMapSingleRecord(sqlQuery, tableName, message);
  }

  private Map<String, Column> getStringColumnMapSingleRecord(String sqlQuery, String tableName, String message) {

    Statement statement = null;
    try {
      statement = connection.createStatement();

      ResultSet rs = statement.executeQuery(sqlQuery);

      Set<String> columnsNames = sqlUtility.getDBColumns(tableName);
      ResultSetMetaData metaData = rs.getMetaData();
      Map<String, Column> columns = new LinkedHashMap<String, Column>();
      int count = 0;
      while (rs.next()) {
        count++;
        if (count > 1) {
          throw new MedicationManagerPersistenceException(message);
        }
        columns = fillColumnsData(columnsNames, rs, metaData);
      }

      return columns;

    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(statement);
    }

  }

  private Map<String, Column> getStringColumnMapSingleRecord(PreparedStatement ps, String tableName, String message) {

    try {
      ResultSet rs = ps.executeQuery();

      Set<String> columnsNames = sqlUtility.getDBColumns(tableName);
      ResultSetMetaData metaData = rs.getMetaData();
      Map<String, Column> columns = new LinkedHashMap<String, Column>();
      int count = 0;
      while (rs.next()) {
        count++;
        if (count > 1) {
          throw new MedicationManagerPersistenceException(message);
        }
        columns = fillColumnsData(columnsNames, rs, metaData);
      }

      return columns;

    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      closeStatement(ps);
    }

  }


  public Map<String, Column> findDispenserByPerson(String tableName, String personTableName, int personId) {
    String message = "There are more than one record (dispensers)" +
        " with the following PATIENT_FK_ID : " + personId;

    String sqlQuery = "select * from " + MEDICATION_MANAGER + '.' + tableName +
        "\n\t where PATIENT_FK_ID=" + personId;

    return getStringColumnMapSingleRecord(sqlQuery, tableName, message);
  }

  public Map<String, Column> executeQueryExpectedSingleRecord(String tableName, String sql) {
    String message = "There are more than one record for the following query : " + sql;

    return getStringColumnMapSingleRecord(sql, tableName, message);
  }

  public Map<String, Column> executeQueryExpectedSingleRecord(String tableName, PreparedStatement ps) {
    String message = "There are more than one record!";

    return getStringColumnMapSingleRecord(ps, tableName, message);
  }

  public List<Map<String, Column>> executeQueryExpectedMultipleRecord(String tableName, PreparedStatement statement) {

    List<Map<String, Column>> results = new ArrayList<Map<String, Column>>();

    try {
      ResultSet rs = statement.executeQuery();

      Set<String> columnsNames = sqlUtility.getDBColumns(tableName);
      ResultSetMetaData metaData = rs.getMetaData();
      while (rs.next()) {

        Map<String, Column> columns = fillColumnsData(columnsNames, rs, metaData);
        results.add(columns);
      }

    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    }

    return results;
  }

  public Connection getConnection() {
    return connection;
  }

  private Map<String, Column> fillColumnsData(Set<String> columnsNames,
                                              ResultSet rs, ResultSetMetaData metaData) throws SQLException {

    Map<String, Column> columns = new LinkedHashMap<String, Column>();

    for (String name : columnsNames) {
      int columnIndex = rs.findColumn(name);
      int sqlType = metaData.getColumnType(columnIndex);
      Column col = createColumn(name, sqlType, rs);
      columns.put(col.getName(), col);
    }

    return columns;
  }

  private Column createColumn(String name, int sqlType, ResultSet rs) throws SQLException {
    switch (sqlType) {
      case INTEGER:
        int integer = rs.getInt(name);
        return new Column(name, integer);
      case TIMESTAMP:
        Timestamp date = rs.getTimestamp(name);
        return new Column(name, date);
      case VARCHAR:
        String string = rs.getString(name);
        return new Column(name, string);
      case BOOLEAN:
        Boolean bool = rs.getBoolean(name);
        return new Column(name, bool);
      case CLOB:
        Clob clob = rs.getClob(name);
        String text;
        if (clob != null && clob.length() > 1) {
          text = clob.getSubString(1, (int) clob.length());
        } else {
          text = null;
        }
        return new Column(name, text);
      default:
        throw new MedicationManagerPersistenceException("Unsupported sql type : " + sqlType);
    }
  }

  private void createTablesAndPopulateThemInOneTransaction() {
    boolean autoCommitValue = true;
    Statement statement = null;
    PreparedStatement ps = null;
    try {
      autoCommitValue = connection.getAutoCommit();
      connection.setAutoCommit(false);
      statement = connection.createStatement();

      createTables(statement);
      insertDataIntoTables(statement, configurationProperties);
      String sqlInsertIntoProperties =
          "insert into medication_manager.properties (id, name, value, format, type, description) values (?, ?, ?, ?, ?, ?)";
      ps = connection.prepareStatement(sqlInsertIntoProperties);
      populatePropertiesTable(ps);

      connection.commit();

    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    } finally {
      handleFinally(connection, statement, ps, autoCommitValue);
    }

  }

  private void populatePropertiesTable(PreparedStatement ps) throws SQLException {
      Map<String,PropertyInfo> propertyInfoMap = configurationProperties.getPropertyInfoMap();
      Set<String> keys = propertyInfoMap.keySet();
      int id = 0;
      for (String key : keys) {
        id++;
        insertPropertyIntoTable(id, key, propertyInfoMap, ps);
      }
    }

  private void insertPropertyIntoTable(int id, String key, Map<String,PropertyInfo> propertyInfoMap,
                                       PreparedStatement ps) throws SQLException {

    PropertyInfo propertyInfo = propertyInfoMap.get(key);
    String value = propertyInfo.getValue();
    ps.setInt(1, id);
    ps.setString(2, key);
    ps.setString(3, value);
    ps.setString(4, propertyInfo.getFormat().getValue());
    ps.setString(5, propertyInfo.getType().getValue());
    ps.setString(6, propertyInfo.getDescription());

    ps.execute();

    System.setProperty(key, value);
  }

  public static void handleFinally(Connection connection, Statement statement,
                                   PreparedStatement ps, boolean autoCommitValue) {
    try {
      closeStatement(statement);
      closeStatement(ps);
      connection.rollback();
      connection.setAutoCommit(autoCommitValue);
    } catch (SQLException e) {
      throw new MedicationManagerPersistenceException(e);
    }
  }

  private void createSequence() {
    Log.info("The sequence does not exists, so creating the sequence", getClass());
    PreparedStatement ps = null;
    try {
      ps = connection.prepareStatement(
          "CREATE SEQUENCE " + MEDICATION_MANAGER + ".ID_GEN AS BIGINT START WITH 10000");
      ps.execute();
    } catch (SQLException e) {
      //nothing to do here in case the sequence exist and this method is triggered by other SQLException than sequence does not exists
    } finally {
      closeStatement(ps);
    }
  }

  static void createTables(Statement statement) throws SQLException {
    executeSqlStatementsFromSqlFile("CreateTables.sql", statement);
  }

  static void insertDataIntoTables(Statement statement,
                                   ConfigurationProperties configurationProperties) throws SQLException {

    String dataLoadFileName;

    if (configurationProperties.isTestMode()) {
       dataLoadFileName = "DataLoadFull.sql";
    } else {
      dataLoadFileName = "DataLoad.sql";
    }

    executeSqlStatementsFromSqlFile(dataLoadFileName, statement);
  }

  static void executeSqlStatementsFromSqlFile(String sqlFileName, Statement statement) throws SQLException {
    SqlScriptParser sqlScriptParser = new SqlScriptParser();
    sqlScriptParser.parseSqlFile(sqlFileName);
    executeStatements(statement, sqlScriptParser);
  }

  public static void executeSqlStatementsFromSqlFile(Statement statement,
                                                     BufferedReader bufferedReader) throws SQLException {

    SqlScriptParser sqlScriptParser = new SqlScriptParser();
    sqlScriptParser.parseSqlFile(bufferedReader);
    executeStatements(statement, sqlScriptParser);
  }

  static void executeStatements(Statement statement, SqlScriptParser sqlScriptParser) throws SQLException {
    Map<Integer, String> sqlTablesMap = sqlScriptParser.getSqlStatementMap();

    int counter = 0;
    while (sqlTablesMap.size() > counter) {
      String sqlStatement = sqlTablesMap.get(counter + 1);
      statement.executeUpdate(sqlStatement);
      counter++;
    }

  }

  private boolean checkIfTablesNotExists() throws SQLException {
    Set<String> tableNames = getDBTables(connection);
    return tableNames.isEmpty();
  }

  public static Set<String> getDBTables(Connection connection) throws SQLException {
    Set<String> tablesNames = new LinkedHashSet<String>();
    DatabaseMetaData metaData = connection.getMetaData();
    ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"});
    while (rs.next()) {
      tablesNames.add(rs.getString("TABLE_NAME"));
    }
    return tablesNames;
  }

}

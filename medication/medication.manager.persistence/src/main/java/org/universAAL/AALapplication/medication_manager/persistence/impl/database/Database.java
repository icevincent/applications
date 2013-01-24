package org.universAAL.AALapplication.medication_manager.persistence.impl.database;

import org.universAAL.AALapplication.medication_manager.persistence.layer.SqlUtility;

import java.util.List;

/**
 * @author George Fournadjiev
 */
public interface Database {

  //This method is used only for the purposes of console shell commands and must be removed in the production
    SqlUtility getSqlUtility();

  void initDatabase() throws Exception;

  int getNextIdFromIdGenerator();

  List<Column> getById(String tableName, int id);
}

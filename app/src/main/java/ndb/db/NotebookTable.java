package ndb.db;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 03/04/13
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */

public class NotebookTable
{
  public String  table,

                 idCol,
                 titleCol,
                 ordinalCol,

                dateCreatedCol,
                dateModifiedCol,


                listTypeCol,
                noteSortCol;

  private List<String> importCols;


  public NotebookTable()
  {
    //sTRINGS SO EVERYTHING IS INITIALLIZED TO NULL
    //anything to do here?????????

    return;
  }


  public void setColumnsToImport(List <String> importColumns)
  {
    importCols= importColumns;
  }


  List<String>  getImportColumns()
  {
    return importCols;
  }


}

package ndb.db;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 03/04/13
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */


import java.util.List;

/**
 *
 * Used to save the note table and the column names for a particular database
 *
 */
public class NoteTable
{
  //table and column names from sqlite database
  public String   table,

                  idCol,
                  nbidCol,
                  titleCol,
                  contentCol,
                  tagsCol,               //this one is a bugger!!!

                  noteType,


                  dateCreatedCol,
                  dateModifiedCol,
                  dateDueCol,

                   richContentCol;




  private List<String> importCols;



  public void setColumnsToImport(List <String> importColumns)
  {
    importCols= importColumns;
  }


  List<String>  getImportColumns()
  {
    return importCols;
  }



}

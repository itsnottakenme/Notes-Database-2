package ndb.types;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 24/03/13
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotebookTagPair
{
  //////////////////////////////////////////////////////////////////////////////////////////////
  //describes table information for SQL database
  public static final String  TABLE =    "notebooktagpairs",

                              NBID =       "notebook_id",        // F_K
                              TAG =        "tag",   // a string F_K
                              COUNT =       "nb_id";       //notebook id


  long notebookId;
  String tag;


  public NotebookTagPair()
  {
    return;
  }

  public boolean equals(NotebookTagPair nbtp)
  {
    return (notebookId == nbtp.getNotebookId() && tag.equals(nbtp.getTag()));
  }


  public long getNotebookId()
  {
    return notebookId;
  }

  public void setNotebookId(long notebookId)
  {
    this.notebookId = notebookId;
  }

  public String getTag()
  {
    return tag;
  }

  public void setTag(String tag)
  {
    this.tag = tag;
  }
  // UNIQUE tag and nbid!!
}       /////////END CLASS/////////

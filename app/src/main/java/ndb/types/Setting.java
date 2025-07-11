package ndb.types;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 14/07/13
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */

import android.database.Cursor;

/**
 * Settings is used to map (key, value) pairs to a database table
 */
public class Setting
{
  /**
   * Database table and columns
   */





//  public static final String[] ALL_COLUMNS =
//          {
//                  Note.ID, Note.TITLE, Note.CONTENT, Note.NBID, Note.DATE_MODIFIED, Note.DATE_CREATED,
//                  Note.NOTE_TYPE, Note.TAGS, Note.DATE_DUE, Note.ROLLOVER_UNCOMPLETED_TODOS, Note.SPANS,
//                  Note.COMPLETED, Note.DATE_COMPLETED,
//
//                  Note.TODO_ORDINAL,
//                  Note.PRIORITY,
//                  Note.DUE_TIME,
//                  Note.REPEAT_INTERVAL,
//
//                  Note.REPEAT_FIELD, Note.REPEAT_VALUE, Note.INT3, Note.INT4, Note.INT5, Note.INT6, Note.INT7, Note.INT8,
//                  Note.TEXT1, Note.TEXT2, Note.TEXT3, Note.TEXT4, Note.TEXT5, Note.TEXT6, Note.TEXT7, Note.TEXT8,
//          };




  public static String TABLE= "Settings",
                       KEY= "key",
                       VALUE= "value";



  public static final String[] ALL_COLUMNS =
          {
          Setting.KEY,
          Setting.VALUE,
  };




  /**
   * Keys
   */
  public static String  NDB_HOME_DIRECOTRY= "NDB_HOME_DIRECOTRY",
                        TEMPORARY_NOTE_ORIGINAL_ID= "TEMPORARY_NOTE_ORIGINAL_ID",
                        TEMPORARY_NOTE_VIEW_TYPE= "TEMPORARY_NOTE_VIEW_TYPE";



  public static Setting fromCursor(Cursor cursor)
  {
    Setting setting= new Setting();

    setting.setKey(cursor.getString(cursor.getColumnIndex(Setting.KEY)));
    setting.setValue(cursor.getString(cursor.getColumnIndex(Setting.VALUE)));
    return setting;
  }

  /**
   * Begin object instance
   */
  private String  key,
                  value;


  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }
}         ////END CLASS////

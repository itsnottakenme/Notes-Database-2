package ndb.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.*;
import ndb.util.DateUtil;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 16/03/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 *
 *
 * TODO: INSTEAD OF RETURNING MAPS, RETURN A INNER CLASS STRUCT WITH THE SAME DATA as it is self documenting
 *
 */







public class NoteDataSource
{
  ///////////////////////////////STATIC//////////////////////////////////////////////
  private static int  FALSE= 0,
                      TRUE=  1;
  ///////////////////////////////////////////////////////////////////////////////////










  private SQLiteDatabase mDatabase;
  private String mDbFilename;
  private NDBTableMaster dbMaster;
  private Context mContext;








  //
//  public NoteDataSource(Context context)
//  {
//    dbHelper = new NDBTableHelper(context, NDBTableHelper.NOTES_DB);
//    init();
//    return;
//  }


  public NoteDataSource(Context context, String fileName)
  {
    dbMaster = new NDBTableMaster(context, fileName);
    mContext= context;
    mDbFilename= fileName;
    init();



    return;
  }


  /**
   * 1) creates default notebooks
   * 2)
   */

  private void init()
  {

    ContentValues values;
    Notebook noCategory;
    XmlResourceParser parser;

    List<Notebook> defaultNotebooks;

    /**
     * Put default notebooks in database
     */
    /////////////////////////////////////////////////
    parser= mContext.getResources().getXml(R.xml.default_notebooks);
    try
    {
      defaultNotebooks= Notebook.loadNotebooks(parser);
    }
    catch(Exception e)
    {
      throw new RuntimeException(e.toString());
    }
    /////////////////////////////////////////////////


//    noCategory= new Notebook();
//    noCategory.setGuid(0);
//    noCategory.setTitle(Notebook.DEFAULT);
//
//    values= noCategory.toContentValues();
//    values.put(Notebook.ID, 0);                         //not sure if toContentValues() adds this

    open();

    for (Notebook notebook : defaultNotebooks)
    {
      values= notebook.toContentValues();  //todo: MUST CHECK THAT THIS DOESN'T OVERWRITE EXISTING SETTINGS (EG. NOTE VIEW)
      values.put(Notebook.ID, notebook.getGuid());
      //todo: if record does not already exist...
      mDatabase.insertWithOnConflict(Notebook.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }
    close();
    parser.close();


    //todo enable foreign key constraints. Must be done for each connection. Learn about the life cycle
    //todo this only works 4.1 onwards
    //database.setForeignKeyConstraintsEnabled(true);
    // can be used to see if any foreign key constraints are currently violated
    // boolean works= database.isDatabaseIntegrityOk ();




    return;
  }



  public void open() throws SQLException
  {
    mDatabase = dbMaster.getWritableDatabase();
    return;
  }


  public void close()  //TODO: DOUBLECHECK TO MAKE SURE I AM CALLING THIS (onPause???)
  {
    dbMaster.close();
    return;
  }

 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * This method can only write ONE SINGLE temporary note. Calling this method again will rewrite the previous temporary
   * note if it exists
   * Use: to store thre note from a destroyed EditNoteActivity
   * @param note
   */
  public void writeTemporaryNote(Note note)
  {
    ContentValues noteRow,
                  settingsNoteRow,
                  settingsViewTypeRow;
    String viewType;

    noteRow= note.toContentValues();
    noteRow.put(Note.ID, Note.TEMPORARY_NOTE_ID);


    settingsNoteRow= new ContentValues();
    settingsNoteRow.put(Setting.KEY, Setting.TEMPORARY_NOTE_ORIGINAL_ID);
    settingsNoteRow.put(Setting.VALUE, note.getGuid());      //todo: long into string field. Problem?


    settingsViewTypeRow= new ContentValues();
    settingsViewTypeRow.put(Setting.KEY, Setting.TEMPORARY_NOTE_VIEW_TYPE);
    viewType= getNotebook(note.getNbGuid()).getViewType().toString();
    settingsViewTypeRow.put(Setting.VALUE, viewType);      //todo: long into string field. Problem?


    try
    {
      mDatabase.beginTransaction();     //no exceptions so not possible?
      mDatabase.insertWithOnConflict(Note.TABLE, null, noteRow, SQLiteDatabase.CONFLICT_REPLACE);
      mDatabase.insertWithOnConflict(Setting.TABLE, null, settingsNoteRow, SQLiteDatabase.CONFLICT_REPLACE);
      mDatabase.insertWithOnConflict(Setting.TABLE, null, settingsViewTypeRow, SQLiteDatabase.CONFLICT_REPLACE);
      mDatabase.setTransactionSuccessful();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException("NoteDatasource - writeTemporaryNote failed");
    }
    finally
    {
      mDatabase.endTransaction();
    }

    return;
  }

  /**
   * Returns  temporary note. Returns null if doesn't exist
   * @return
   */
  public Note getTemporaryNote()
  {
    Note note;
    Setting setting;
    Cursor cursor;

    cursor= mDatabase.query(Setting.TABLE, Setting.ALL_COLUMNS, Setting.KEY+ "=" + "'" +Setting.TEMPORARY_NOTE_ORIGINAL_ID+ "'", null, null, null, null);

    if ( cursor.getCount() == 0 )
    {
      note= null;
    }
    else
    { //assert: temporary note exists. fetch it then delete it
      cursor.moveToFirst();
      setting= Setting.fromCursor(cursor);
      note= getNote(Note.TEMPORARY_NOTE_ID);
      note.setGuid(Long.parseLong(setting.getValue()));
      //deleteTemporaryNote();
    }

    return note;
  }

  public String getTemporaryNoteViewType()
  {
    String viewType;
    Setting setting;
    Cursor cursor;

    cursor= mDatabase.query(Setting.TABLE, Setting.ALL_COLUMNS, Setting.KEY+ "=" + "'" +Setting.TEMPORARY_NOTE_VIEW_TYPE+ "'", null, null, null, null);
    if ( cursor.getCount() == 0 )
    {
      viewType= null;
    }
    else
    {
      cursor.moveToFirst();
      setting= Setting.fromCursor(cursor);
      viewType= setting.getValue();
    }


    return viewType;
  }



  public void deleteTemporaryNote()
  {
    try
    {
      mDatabase.beginTransaction();
      mDatabase.delete(Note.TABLE, Note.ID+ "=" +Note.TEMPORARY_NOTE_ID, null);
      mDatabase.delete(Setting.TABLE, Setting.KEY+ "=" + "'" +Setting.TEMPORARY_NOTE_ORIGINAL_ID+ "'", null);
      mDatabase.delete(Setting.TABLE, Setting.KEY+ "=" + "'" +Setting.TEMPORARY_NOTE_VIEW_TYPE+ "'", null);
      mDatabase.setTransactionSuccessful();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException("NoteDatasource - deleteTemporaryNote failed");
    }
    finally
    {
      mDatabase.endTransaction();
    }


    return;
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  //Takes in a Note and fills in missing data and reutrns a new Note
  //TODO MUST BE MODIFIED EVERYTIME NOTE GETS A NEW FIELD. DO ALL THE FIELDS AT ONCE!!!!!!!!!
  public Note createNote(Note note)
  {
    Note newNote;
    ContentValues noteRow,
                  ntpRow;       //notetagpair row
    Cursor cursor;
    long insertId=0;            //to satisfy compiler
    List<String> tagList;

    tagList= Tag.stringToList(note.getTags());


    if (note !=null)
    {
        try
        {
          mDatabase.beginTransaction();
          /**
           * add tags to notebooktagpairs if they are not already there
           */

          for (int i= 0; i<tagList.size(); i++)
          {
            createNotebookTagPair(note.getNbGuid(), tagList.get(i));
          }

          /**
           * set up ContentValues
           */
          noteRow= note.toContentValues();           //todo: remove guid to prevent problems?
          if (note.getDateModified() <= 0)
          {
            noteRow.put(Note.DATE_MODIFIED, Calendar.getInstance().getTimeInMillis());
          }

          if (note.getDateCreated() > 0 )
          {      //ie. don't change created time if already exists (eg if imported from other database)
            noteRow.put(Note.DATE_CREATED, note.getDateCreated());
          }
          else
          {
            noteRow.put(Note.DATE_CREATED, Calendar.getInstance().getTimeInMillis());
          }


          insertId= mDatabase.insert(Note.TABLE, null, noteRow);
          ///////  ////////////    //////////////     //////////////     //////////



          mDatabase.setTransactionSuccessful();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        finally
        {
          mDatabase.endTransaction();
        }



        cursor = mDatabase.query(Note.TABLE,
                Note.ALL_COLUMNS, Note.ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        newNote= Note.fromCursor(cursor);
        cursor.close();
    }
    else
    {
      newNote= null;
    }


    return newNote;
  }


  /**
   * updates a note. updateModifiedDate
   * @param note
   * @param updateModifiedDate
   * @return
   */
  public Note updateNote(Note note, Boolean updateModifiedDate)
  {           //todo if notebook is changed make sure changeNotebook() method is run


    Note updatedNote,       //the note to be returned
         originalNote;  //the previous version of the note retrieved to see which tags changed.
    Cursor cursor;
    ContentValues contentValues;  //for column names and the associated input values
    //this method is updating every field of the note... overload the function so ut can take different p[arameters?

    try
    {
      mDatabase.beginTransaction();

      List<String> tagsToAdd;        //tags to be added
      List<String> tagsToDelete;    //tags to be deleted


     originalNote = getNote(note.getGuid());
     contentValues = note.toContentValues();

      if (updateModifiedDate == true)
      {
        contentValues.put(Note.DATE_MODIFIED, Calendar.getInstance().getTimeInMillis());
      }
      /**
       * Update note
       */
      mDatabase.update(Note.TABLE, contentValues, Note.ID + "=" + note.getGuid(), null);          //db update
      /**
       * Get updated note
       */
      cursor = mDatabase.query(Note.TABLE,
              Note.ALL_COLUMNS, Note.ID + " = " + note.getGuid(), null,
              null, null, null);
      cursor.moveToFirst();
      updatedNote = Note.fromCursor(cursor);


      /**
       * find which tags have changed then update notebooktagpairs
       */

      if (originalNote.getNbGuid() == updatedNote.getNbGuid())
      {
        tagsToAdd = originalNote.getNewTagsFrom(note.getTags());
        tagsToDelete = note.getNewTagsFrom(originalNote.getTags());

        for (int i=0; i< tagsToAdd.size(); i++)
        {
          createNotebookTagPair(note.getNbGuid(), tagsToAdd.get(i));             //db update
        }

        for (int i=0; i< tagsToDelete.size(); i++)
        {
          noteTagPairAfterDelete(note.getNbGuid(), tagsToDelete.get(i));     //db update
        }
      }
      else  //assert notebook has changed. Delete all tags from old notebook and add them toi the new one
      {
        tagsToAdd= updatedNote.getTagsAsList();
        tagsToDelete= originalNote.getTagsAsList();

        for (int i=0; i< tagsToAdd.size(); i++)
        {
          createNotebookTagPair(updatedNote.getNbGuid(), tagsToAdd.get(i));             //db update
        }

        for (int i=0; i< tagsToDelete.size(); i++)
        {
          noteTagPairAfterDelete(originalNote.getNbGuid(), tagsToDelete.get(i));     //db update
        }


      }
      //////////////////////////////////////

      mDatabase.setTransactionSuccessful();
    }
    finally
    {
      mDatabase.endTransaction();
    }

//    /**
//     * Get updated note
//     */
//    cursor = mDatabase.query(Note.TABLE,
//            Note.ALL_COLUMNS, Note.ID + " = " + note.getGuid(), null,
//            null, null, null);
//
//    cursor.moveToFirst();
//    updatedNote = Note.fromCursor(cursor);
    cursor.close();

    return updatedNote;
  }


  /**
   * Looks for noteId's that don't belong to valid notes and then deletes them as well as associated files
   *
   * @return number of records deleted
   */
  public int deleteOrphanedMedia()
  {
             //TODO: iMPLEMENT!
    return 0;
  }





  public Note getNote(long guid)    //todo what happens when guid DNE?
  {
    Note note;
    Cursor cursor;

    cursor = mDatabase.query(Note.TABLE,
            Note.ALL_COLUMNS, Note.ID + " = " + guid, null,
            null, null, null);


    if (cursor.getCount() > 0)  //assert a note was returned
    {
      cursor.moveToFirst();
      note= Note.fromCursor(cursor);
    }
    else //assert cursor is empty
    {
      note= null;
    }

    cursor.close();
    return note;
  }



  /*
   *
   * assumed note has valid guid that can be deleted
   *  uses the notes guid to delete from db
   */
   //todo: change so only noteGuid is needed
  public boolean deleteNote(Note note)  //throws SQLException
  {
    int result; // number of rows affected by change
    boolean success;
    List<String> tagList;
    List<Media> mediaList;

    //    //todo: WARNING IF LAST PARAMETER OF delete(...) IS NULL THEN ALL RECORDS WILL BE DELETED ????




     try
     {
        mDatabase.beginTransaction();
        //delete note from table
        result= mDatabase.delete(Note.TABLE, Note.ID + " = " + note.getGuid(), null);    //db change

        if (result == 0) // ie. zero rows affected
          success = false;
        else
        {         //assert note has been deleted
          success= true;

          /**
           * Delete associated NoteTagPairs
           */
          tagList= Tag.stringToList(note.getTags());
          for (int i=0; i<tagList.size(); i++)
          {
            //deletes the notetagpair if it is the last one in the notebook
            noteTagPairAfterDelete(note.getNbGuid(), tagList.get(i));                   //db change
          }

          /**
           * Delete associated records in media table
           */
          mediaList= getMedia(note.getGuid());
          for (Media media: mediaList)
          {
            deleteMedia(media);
          }

        }

        mDatabase.setTransactionSuccessful();
     }
     finally
     {
       mDatabase.endTransaction();
     }

    return success;
  }


  /**
   *  Deletes media.
   *
   * @param media (used instead of mediaId because its simpler)
   * @return
   */
  public boolean deleteMedia(Media media)
  {
    boolean success;
    int result;
    File file;


    try
    {
      /**
       * Delete record from Media table
       */
      mDatabase.beginTransaction();
      result= mDatabase.delete(Media.TABLE, Media.ID + " = " +media.getGuid(), null);    //db change

      if (result == 0)
      {
        success= false;
      }
      else
      {
       success= true;
       /**
        * Delete associated files
        */
       file= new File(media.getPath());
       file.delete();                       //doesn't throw exception on failure

       if (media.getPreviewPath()!=null)
       {
         file= new File(media.getPreviewPath());
         file.delete();
       }
      }
      mDatabase.setTransactionSuccessful();
    }
    finally
    {
      mDatabase.endTransaction();
    }

    return success;
  }





  //todo make this private. WHAT SHOULD IT BE REPLACED WITH????????
  public List<Note> getAllNotes(String sortField, String ascDsc)
  {
    List<Note> notes;
    Cursor cursor;
    Note newNote;
    String orderPart;


    notes= new ArrayList<Note>();

    orderPart= sortField;
    if (sortField == null && ascDsc == null)
    {
      orderPart= null;
    }
    if (ascDsc == null || ascDsc.equals(Sort.DESCENDING) )
    {
      orderPart= orderPart+" desc";
    }
    else
    {
      orderPart= orderPart+" asc";
    }



    cursor= mDatabase.query(Note.TABLE, Note.ALL_COLUMNS, null, null, null, null, orderPart);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {

      newNote = Note.fromCursor(cursor);   //this should return a single noteBook entry
      notes.add(newNote);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return notes;
  }


  //todo make getNotesFromNotebook(long nbGuid) call this to avoid code duplication
  public Cursor getNotesAsCursor(long nbGuid)
  {
    Cursor cursor;
    List<Note> notes;
    Note newNote;

    notes= new ArrayList<Note>();

    cursor= mDatabase.rawQuery("SELECT * FROM " + Note.TABLE
            + " WHERE "+ Note.NBID +"="+nbGuid
            + " order by " +Note.DATE_MODIFIED+ " desc;",null);  //todo this order is never seen since the other getNotes is always executed right after

    return cursor;

  }


  //todo delete this function so only have single getNotes() function
  public List<Note> getNotesFromNotebook(long nbGuid)
  {
    Cursor cursor;
    List<Note> notes;
    Note newNote;

    notes= new ArrayList<Note>();

    cursor= mDatabase.rawQuery("SELECT * FROM " + Note.TABLE
                            + " WHERE "+ Note.NBID +"="+nbGuid
                            + " order by " +Note.DATE_MODIFIED+ " desc;",null);  //todo this order is never seen since the other getNotes is always executed right after

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {

      newNote = Note.fromCursor(cursor);   //this should return a single noteBook entry
      notes.add(newNote);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return notes;
  }


  /**
   * getNotesForTodoListSingleDay  - this is updated when refactoring occurs
   *
   *
   * @param notebookId
   * @param dueDateToFetch
   * @return
   */
  public Map<Key, Object> /*List<Note>*/ getNotesForTodoListSingleDay(long notebookId, Date dueDateToFetch)
  {
    Map<Key, Object> results= new HashMap();
    Cursor cursor;

    long   dueDateStart,
           dueDateEnd,
           currentTime;

    List<Note> notes;
    notes= new ArrayList<Note>();

    currentTime= Calendar.getInstance().getTimeInMillis();
    dueDateStart= DateUtil.getStartOfDay(dueDateToFetch).getTime();
    dueDateEnd= DateUtil.getEndOfDay(dueDateToFetch).getTime();



    /**
     * The Magnificent Queries
     */
    String orderPart= " ORDER BY " +Note.COMPLETED+ " ASC, "  +Note.PRIORITY+ " DESC, " +Note.DATE_DUE+ " ASC ";

    String queryPastDay =   "SELECT DISTINCT * FROM " + Note.TABLE
                          + " WHERE " +Note.NBID+ "=" +notebookId
                          + " AND " +Note.DATE_DUE+ " >= " + dueDateStart
                          + " AND " +Note.DATE_DUE+ " <= " + dueDateEnd

                          + " AND ("
                          +                Note.COMPLETED+ "=" +TRUE
                          + "      OR (" + Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +FALSE+ ") "
                          +     " ) "
                          + orderPart,



            queryToday=     "SELECT DISTINCT * FROM " + Note.TABLE
                          + " WHERE " +Note.NBID+ "=" +notebookId
                          + " AND ((" +Note.DATE_DUE+ ">=" + dueDateStart + " AND " +Note.DATE_DUE+ "<=" + dueDateEnd + ")"
                          + " OR  (" +Note.DATE_DUE+ "<" + dueDateStart + " AND  " +Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +TRUE/*FALSE*/+ "))"
                          + orderPart,



            queryFutureDay =    "SELECT DISTINCT * FROM " + Note.TABLE
                              + " WHERE " +Note.NBID+ "=" +notebookId
                              + " AND " +Note.DATE_DUE+ " >= " + dueDateStart
                              + " AND " +Note.DATE_DUE+ " <= " + dueDateEnd
                              + orderPart;








    if (currentTime >= dueDateStart && currentTime <= dueDateEnd)
    { //assert Key.TODAY case
      /**
       * CursorToday
       */
      cursor= mDatabase.rawQuery(queryToday, null);
      notes.addAll(Note.setTransientShowDueDates(cursor, Key.TODAY));

    }
    else if (dueDateStart < currentTime)
    { //assert Key.PAST case
      /**
       * CursorPast
       */

      cursor= mDatabase.rawQuery(queryPastDay, null);
      notes.addAll(Note.setTransientShowDueDates(cursor, Key.PAST));
      cursor.close();


    }
    else if ( dueDateEnd > currentTime)
    { // Key.FUTURE case
      /**
       * CursorFuture
       */
      cursor= mDatabase.rawQuery(queryFutureDay, null);
      notes.addAll(Note.setTransientShowDueDates(cursor, Key.FUTURE));
      cursor.close();


    }

    results.put(Key.NOTE_LIST, notes);
    results.put(Key.VIEW_TYPE, Key.VIEW_TODOS_SINGLE_DAY);

    return/* notes*/ results;
  }



  /**      TODO: SINCE THIS CODE IS BASICALLY A SUPERSET OF THE OTHER getNotesForTodoList() METHOD
   *            CALL THIS FUNCTION FROM THERE TO AVOID MAINTENENCE ISSUES.
   *
   *
   * Implements search function for TodoListActivity
   *
   * code copied from the ALL_DAYS method
   *
   * @param searchString
   * @param notebookId
   * @param tag
   *
   *
   * @param currentDate
   * @return
   */
  public Map<Key, Object> getNotesForTodoList(long notebookId, Date currentDate, String searchString, String tag)
  {
//    String  sortField,
//            ascDsc;
    Map<Key, Object> results= new HashMap();
    int tempSize;       //used to store size of notes List from previous add

    long  startOfCurrentDay= DateUtil.getStartOfDay(currentDate).getTime(),
          endOfCurrentDay=   DateUtil.getEndOfDay(currentDate).getTime()-1;

    Cursor  cursorPast,
            cursorToday,
            cursorFuture;

    List<Note> notes;



    /**
     * Where clause strings
     */
    String  notebookWherePart= Note.NBID+ "=" +notebookId,

            tagWherePart =          " ( " +  Note.TAGS + " like " + "'% " +tag+ " %'"
                                  + " or "   + Note.TAGS + " like " + "'" +tag+ " %'"
                                  + " or "   + Note.TAGS + " like " + "'% " +tag+ "'"
                                  + " or "   + Note.TAGS + " like " + "'" +tag+ "') ",

            searchStringWherePart=  " (" +Note.TITLE+ " LIKE '%" +searchString+ "%' OR "
                                  + Note.CONTENT + " LIKE '%" +searchString+ "%'" + ") ";




    String wherePast=     Note.DATE_DUE+ " < " +startOfCurrentDay
                        + " AND ("
                        +  Note.COMPLETED+ "=" +TRUE+ " OR (" + Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +FALSE
                        + "))",

           whereToday=  " ((" +Note.DATE_DUE+ ">=" +startOfCurrentDay+ " AND " +Note.DATE_DUE+ "<=" +endOfCurrentDay+ ")"
                      + " OR  (" +Note.DATE_DUE+ "<" +startOfCurrentDay+ " AND  " +Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +TRUE+ "))",

           whereFuture= Note.DATE_DUE+ ">" +endOfCurrentDay;



    /**
     * Sorting
     */

    String  orderToday= Note.COMPLETED+ " ASC, "  +Note.PRIORITY+ " DESC, " +Note.DATE_DUE+ " ASC ",
            /**
             * todo: orderOther doesn't sort by priority because todo's can have differewnt dateDate's in the same date
             *
             * The fix is make change dueDate's to the beginning of each day and then set dueTime to the
             * it to the latest millisecond. Might be good to make it milliseconds in the day only (modulo) that way
             * it makes its each to sort by day and then by time in that day if needed... doesn't herlp? lol
             *
             * After fix is done put this:
             *   orderOther= " ORDER BY "+Note.DATE_DUE+ " ASC, "+Note.COMPLETED+ " ASC, " +Note.PRIORITY+ " DESC, ";    //for order of past and future queries
             */

            orderOther= Note.DATE_DUE+ " ASC, "+Note.COMPLETED+ " ASC ";    //for order of past and future queries


    /**
     * Set up the common fields of all where clauses
     */
    String whereCommonClause; //fields common to all where clauses

    whereCommonClause = notebookWherePart;

    if ( searchString!=null && !searchString.equals("") )
    {
      whereCommonClause = whereCommonClause+ " and " +searchStringWherePart;
    }
    if ( tag!=null && !tag.equals("") )
    {
      whereCommonClause = whereCommonClause+ " and "  +tagWherePart;
    }

    /**
     * Set up the full where clauses
     */
    wherePast=   whereCommonClause+ " and " +wherePast;
    whereToday=  whereCommonClause+ " and " +whereToday;
    whereFuture= whereCommonClause+ " and " +whereFuture;




    notes= new ArrayList<Note>();
    /**
     * CursorPast
     */
    cursorPast= mDatabase.query(Note.TABLE, Note.ALL_COLUMNS, wherePast, null, null, null, orderOther);
    notes.addAll(Note.setTransientShowDueDates(cursorPast, Key.PAST));
    cursorPast.close();
    if (notes.size() == 0)
    {
      results.put(Key.PAST_TODOS_START_INDEX, -1);
      tempSize = 0;
    }
    else
    {
      results.put(Key.PAST_TODOS_START_INDEX, 0);
      tempSize = notes.size();
    }


    /**
     * CursorToday
     */
    cursorToday= mDatabase.query(Note.TABLE, Note.ALL_COLUMNS, whereToday, null, null, null, orderToday );
    notes.addAll(Note.setTransientShowDueDates(cursorToday, Key.TODAY));
    cursorToday.close();
    if (tempSize - notes.size() == 0)
    { //assert no elements were added
      results.put(Key.TODAYS_TODOS_START_INDEX, -1);
      //no need to change tempSize as list's size is still the same
    }
    else
    {
      results.put(Key.TODAYS_TODOS_START_INDEX, tempSize);
      tempSize= notes.size()-1;
    }



    /**
     * CursorFuture
     */
    cursorFuture= mDatabase.query(Note.TABLE, Note.ALL_COLUMNS, whereFuture, null, null, null, orderOther );
    notes.addAll(Note.setTransientShowDueDates(cursorFuture, Key.FUTURE));
    cursorToday.close();
    if (tempSize - notes.size() == 0)
    { //assert no elements were added
      results.put(Key.FUTURE_TODOS_START_INDEX, -1);
      //no need to change tempSize as list's size is still the same
    }
    else
    {
      results.put(Key.FUTURE_TODOS_START_INDEX, tempSize-1);
      //tempSize= notes.size()-1;
    }

    results.put(Key.VIEW_TYPE, Key.VIEW_TODOS_ALL);
    results.put(Key.NOTE_LIST, notes);


    return results;
  }





  /**           ALL_DAYS
   *
   * Returns all notes with notebookId and is sorted with indexes stored in map.
   *
   * Description of Map keys in TodoAdapter
   * todo: maybe return a map so return data is flexible
   * e.g. keys NOTE_LIST and TODAYS_TODO_INDEX
   * or maybe just keep 3 separate lists and let the adapter handle it?
   *
   * @param notebookId
   * @param currentDate
   * @return
   */
  public Map<Key, Object> getNotesForTodoList(long notebookId, Date currentDate)
  {
    Map<Key, Object> results= new HashMap();
    int tempSize;       //used to store size of notes List from previous add

    long  startOfCurrentDay= DateUtil.getStartOfDay(currentDate).getTime(),
            endOfCurrentDay=   DateUtil.getEndOfDay(currentDate).getTime()-1;

    String
            orderToday= " ORDER BY " +Note.COMPLETED+ " ASC, "  +Note.PRIORITY+ " DESC, " +Note.DATE_DUE+ " ASC ",

          /**
           * todo: orderOther doesn't sort by priority because todo's can have differewnt dateDate's in the same date
           *
           * The fix is make change dueDate's to the beginning of each day and then set dueTime to the
           * it to the latest millisecond. Might be good to make it milliseconds in the day only (modulo) that way
           * it makes its each to sort by day and then by time in that day if needed... doesn't herlp? lol
           *
           * After fix is done put this:
           *   orderOther= " ORDER BY "+Note.DATE_DUE+ " ASC, "+Note.COMPLETED+ " ASC, " +Note.PRIORITY+ " DESC, ";    //for order of past and future queries
           */
            orderOther= " ORDER BY "+Note.DATE_DUE+ " ASC, "+Note.COMPLETED+ " ASC ";    //for order of past and future queries




    String  queryPast=  "SELECT DISTINCT * FROM " + Note.TABLE
                    + " WHERE " +Note.NBID+ "=" +notebookId
                    + " AND " +Note.DATE_DUE+ " < " +startOfCurrentDay
                    + " AND ("
                    +          Note.COMPLETED+ "=" +TRUE+ " OR (" + Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +FALSE/*TRUE*/+ "))"
                    + orderOther,




            queryToday=     "SELECT DISTINCT * FROM " + Note.TABLE
                    + " WHERE " +Note.NBID+ "=" +notebookId
                    + " AND ((" +Note.DATE_DUE+ ">=" +startOfCurrentDay+ " AND " +Note.DATE_DUE+ "<=" +endOfCurrentDay+ ")"
                    + " OR  (" +Note.DATE_DUE+ "<" +startOfCurrentDay+ " AND  " +Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +TRUE/*FALSE*/+ "))"
                    + orderToday,



            queryFuture=    "SELECT DISTINCT * FROM " + Note.TABLE
                    + " WHERE " +Note.NBID+ "=" +notebookId
                    + " AND " +Note.DATE_DUE+ ">" +endOfCurrentDay
                    + orderOther;




    Cursor  cursorPast,
            cursorToday,
            cursorFuture;

    List<Note> notes;
    Note newNote;


    notes= new ArrayList<Note>();


    /**
     * CursorPast
     */
    cursorPast= mDatabase.rawQuery(queryPast, null);
    notes.addAll(Note.setTransientShowDueDates(cursorPast, Key.PAST));
    cursorPast.close();
    if (notes.size() == 0)
    {
      results.put(Key.PAST_TODOS_START_INDEX, -1);
      tempSize = 0;
    }
    else
    {
      results.put(Key.PAST_TODOS_START_INDEX, 0);
      tempSize = notes.size();
    }


    /**
     * CursorToday
     */
    cursorToday= mDatabase.rawQuery(queryToday, null);
    notes.addAll(Note.setTransientShowDueDates(cursorToday, Key.TODAY));
    cursorToday.close();
    if (tempSize - notes.size() == 0)
    { //assert no elements were added
      results.put(Key.TODAYS_TODOS_START_INDEX, -1);
      //no need to change tempSize as list's size is still the same
    }
    else
    {
      results.put(Key.TODAYS_TODOS_START_INDEX, tempSize);
      tempSize= notes.size()-1;
    }



    /**
     * CursorFuture
     */
    cursorFuture= mDatabase.rawQuery(queryFuture, null);
    notes.addAll(Note.setTransientShowDueDates(cursorFuture, Key.FUTURE));
    cursorToday.close();
    if (tempSize - notes.size() == 0)
    { //assert no elements were added
      results.put(Key.FUTURE_TODOS_START_INDEX, -1);
      //no need to change tempSize as list's size is still the same
    }
    else
    {
      results.put(Key.FUTURE_TODOS_START_INDEX, tempSize-1);
      //tempSize= notes.size()-1;
    }

    results.put(Key.VIEW_TYPE, Key.VIEW_TODOS_ALL);
    results.put(Key.NOTE_LIST, notes);


    return results;
  }




  /**
   * Returns the notes arranged in grocery list order (sorted by Note.COMPLETED, then Note.TITLE alphabetically
   * The map map contains     Key.GROCERY_LIST_COMPLETED_START_INDEX and Key.GROCERY_LIST_NOT_COMPLETED_START_INDEX.
   * If one of these categories are empty -1 will be returned for the corresponding index.

   * @param notebookId
   * @return
   */
  public Map<Key, Object> getNotesForGroceryList(long notebookId)
  {
    Map<Key, Object> results= new HashMap();
    Cursor groceriesToGetCursor,
            groceriesAlreadyGottenCursor;
    List<Note> notes= new ArrayList<Note>();
    int tempSize;       //number of items in unCompletedNotesCursor



    /**
     * Groceries to get
     */
    groceriesToGetCursor =   mDatabase.query(Note.TABLE, Note.ALL_COLUMNS,
                                              Note.NBID+ "=" +notebookId+ " and " +Note.COMPLETED+ "=" +FALSE,
                                              null, null, null, Note.TITLE+ " COLLATE NOCASE ASC", null);
    notes.addAll(Note.listFromCursor(groceriesToGetCursor));

    tempSize= groceriesToGetCursor.getCount();
    if (groceriesToGetCursor.getCount() == 0)
    {
      results.put(Key.GROCERY_LIST_NOT_COMPLETED_START_INDEX, -1);
    }
    else
    {
      results.put(Key.GROCERY_LIST_NOT_COMPLETED_START_INDEX, 0);
    }
    groceriesToGetCursor.close();




    /**
     * Groceries already got
     */
    groceriesAlreadyGottenCursor = mDatabase.query(Note.TABLE, Note.ALL_COLUMNS,
                                          Note.NBID+ "=" +notebookId+ " and " +Note.COMPLETED+ "=" +TRUE,
                                          null, null, null, Note.TITLE+ " COLLATE NOCASE ASC", null);

    if ( groceriesAlreadyGottenCursor.getCount() == 0 )
    {
         results.put(Key.GROCERY_LIST_COMPLETED_START_INDEX, -1);
    }
    else
    {
      results.put(Key.GROCERY_LIST_COMPLETED_START_INDEX, tempSize);
    }
    notes.addAll(Note.listFromCursor(groceriesAlreadyGottenCursor));
    groceriesAlreadyGottenCursor.close();

    results.put(Key.NOTE_LIST, notes);
    results.put(Key.VIEW_TYPE, Key.VIEW_GROCERY_LIST);

    return results;
  }

   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * todo: maybe return a map so return data is flexible
   * e.g. keys NOTE_LIST and TODAYS_TODO_INDEX
   * or maybe just keep 3 separate lists and let the adapter handle it?
   *
   * @param notebookId
   * @param currentDate
   * @return
   */
  public List<Note> _DEPRECATED_getNotesForTodoList(long notebookId, Date currentDate)
  {
    long  startOfCurrentDay= DateUtil.getStartOfDay(currentDate).getTime(),
          endOfCurrentDay=   DateUtil.getEndOfDay(currentDate).getTime();

    String orderPart= " ORDER BY " +Note.DATE_DUE+ " ASC ";

    String  queryPast=  "SELECT DISTINCT * FROM " + Note.TABLE
                      + " WHERE " +Note.NBID+ "=" +notebookId
                      + " AND " +Note.DATE_DUE+ " < " +startOfCurrentDay
                      + " AND ("
                      +           Note.COMPLETED+ "=" +TRUE+ " OR (" + Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +TRUE+ "))"
                      + orderPart,




            queryToday=     "SELECT DISTINCT * FROM " + Note.TABLE
                          + " WHERE " +Note.NBID+ "=" +notebookId
                          + " AND ((" +Note.DATE_DUE+ ">=" +startOfCurrentDay+ " AND " +Note.DATE_DUE+ "<=" +endOfCurrentDay+ ")"
                          + " OR  (" +Note.DATE_DUE+ "<" +startOfCurrentDay+ " AND  " +Note.COMPLETED+ "=" +FALSE+ " AND " +Note.ROLLOVER_UNCOMPLETED_TODOS + "=" +FALSE+ "))"
                          + orderPart,



            queryFuture=    "SELECT DISTINCT * FROM " + Note.TABLE
                          + " WHERE " +Note.NBID+ "=" +notebookId
                          + " AND " +Note.DATE_DUE+ ">" +endOfCurrentDay
                          + orderPart;




    Cursor  cursorPast,
            cursorToday,
            cursorFuture;

    List<Note> notes;
    Note newNote;


    notes= new ArrayList<Note>();


    /**
     * CursorPast
     */
    cursorPast= mDatabase.rawQuery(queryPast, null);
    cursorPast.moveToFirst();
    while (!cursorPast.isAfterLast())
    {

      newNote = Note.fromCursor(cursorPast);   //this should return a single noteBook entry
      notes.add(newNote);
      cursorPast.moveToNext();
    }

    // Make sure to close the cursor
    cursorPast.close();

    /**
     * CursorToday
     */
    cursorToday= mDatabase.rawQuery(queryToday, null);
    cursorToday.moveToFirst();
    while (!cursorToday.isAfterLast())
    {

      newNote = Note.fromCursor(cursorToday);   //this should return a single noteBook entry
      notes.add(newNote);
      cursorToday.moveToNext();
    }

    // Make sure to close the cursor
    cursorToday.close();

    /**
     * CursorFuture
     */
    cursorFuture= mDatabase.rawQuery(queryFuture, null);
    cursorFuture.moveToFirst();
    while (!cursorFuture.isAfterLast())
    {

      newNote = Note.fromCursor(cursorFuture);   //this should return a single noteBook entry
      notes.add(newNote);
      cursorFuture.moveToNext();
    }

    // Make sure to close the cursor
    cursorFuture.close();






    return notes;
 }

  /**
   * gets the notes for todo list
   * @param notebookId
   * @param dateStart
   * @param dateEnd
   * @return
   */


  //todo: need to add support for STICKY_DATE
  public List<Note> _DEPRECATED_getNotes(long notebookId, long dateStart, long dateEnd)
  {
    Cursor cursor;
    List<Note> notes;
    Note newNote;
    boolean isAndNeeded = false;

    notes= new ArrayList<Note>();

    String andPart= " and ";

    String queryBase=     "SELECT DISTINCT * FROM " + Note.TABLE
                        + " WHERE " +Note.NBID+ "=" +notebookId
                        + " AND " +Note.DATE_DUE+ " BETWEEN " +dateStart+ " AND " + dateEnd
                        + " ORDER BY " +Note.DATE_DUE+ " ASC; ";

      cursor= mDatabase.rawQuery(queryBase, null);



      cursor.moveToFirst();
      while (!cursor.isAfterLast())
      {

        newNote = Note.fromCursor(cursor);   //this should return a single noteBook entry
        notes.add(newNote);
        cursor.moveToNext();
      }

      // Make sure to close the cursor
      cursor.close();


    return notes;
  }




  /**
   *
   * Will throw an exception if all 3 parameters invalid
   *
   *
   *
   * @param searchString - null if none
   * @param notebookId - nbGuid or a negative integer to search all notebooks
   * @param tag
   * @param sortField - if null then results not sorted
   * @return
   */
  public List<Note> getNotes(String searchString, long notebookId, String tag, String sortField, String ascDsc)
  {
    Cursor cursor;
    List<Note> notes;
    Note newNote;
    boolean isAndNeeded = false;

    notes= new ArrayList<Note>();

    String andPart= " and ";

    String queryBase=     "SELECT DISTINCT * FROM " + Note.TABLE
                        + " WHERE ",

           nbidPart=    Note.NBID+ "=" +notebookId,

            //working tags
         //  tagPart=     Note.TAGS + " like "+"'% "+ tag+" %' ",           //todo see if tag works at beginning, middle, end
             tagPart=         " ( " +  Note.TAGS + " like " + "'% " +tag+ " %'"
                         + " or "   + Note.TAGS + " like " + "'" +tag+ " %'"
                         + " or "   + Note.TAGS + " like " + "'% " +tag+ "'"
                         + " or "   + Note.TAGS + " like " + "'" +tag+ "')",

            searchStringPart= " (" +Note.TITLE+ " LIKE '%" +searchString+ "%' OR "
                           + Note.CONTENT + " LIKE '%" +searchString+ "%'" + ") ",

            orderByPart= " order by " + sortField;


    if (searchString==null && notebookId<0 && tag==null)         //ie get all notes
    {
      notes= getAllNotes(sortField, ascDsc);
    }
    else        //assert search has valid parameters so search
    {
        ///////////////////////////////////////////////////////////////////
        //Construct query
        if (notebookId >= 0)    //assert a valid notebook ID was passed
        {                       //works with Notebook.ALL since notebook.ALL_NOTES_ID == -10
          queryBase= queryBase+nbidPart;
          isAndNeeded= true;
        }
        if (tag != null)                             //tag part
        {
          if (isAndNeeded)
          {
            queryBase= queryBase+andPart+tagPart;
          }
          else //assert and is not needed
          {
            queryBase= queryBase+tagPart;
          }
          isAndNeeded= true;
        }
        if (searchString != null)                    //searchString part
        {
          if (isAndNeeded)
          {
            queryBase= queryBase+andPart+searchStringPart;
          }
          else //assert and is not needed
          {
            queryBase= queryBase+searchStringPart;
          }
          isAndNeeded= true;

        }

        if (sortField != null)
        {
         queryBase= queryBase+orderByPart;
          if (ascDsc == null || ascDsc.equals(Sort.DESCENDING) )
          {
            queryBase=queryBase+" desc";
          }
          else
          {
            queryBase=queryBase+" asc";
          }
        }

        ///////////////////////////////////////////////////////////////////////

        cursor= mDatabase.rawQuery(queryBase, null);



        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {

          newNote = Note.fromCursor(cursor);   //this should return a single noteBook entry
          notes.add(newNote);
          cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
    }

    return notes;
  }









      /*

  private Tag cursorToTag(Cursor cursor)              //TODO THUS NO LONGER WORKS UPDATE!!!
  {
    Tag tag = new Tag();

    tag.setGuid(cursor.getLong(0));    //todo: check these to make sure in right order
    tag.setName(cursor.getString(1));

    return tag;
  }

        */


  //todo what happens when tags is "" ?(empty string). Test case!!!
  private List<String> getTagsFromNote(long noteGuid)
  {
    String tags;
    Cursor cursor;
    String query= "select " +Note.TAGS+ " from " +Note.TABLE
                 +" where " +Note.ID+ "=" +noteGuid;

    cursor= mDatabase.rawQuery(query, null);

    if (cursor.getCount()>0)  //ie cursor contains a result
    {
      cursor.moveToFirst();
      tags= cursor.getString(0);
    }
    else      // assert no results
    {
      tags= null;
    }


    return Tag.stringToList(tags);
  }

  /**
   *
   * Searches for aTag in all notes in same notebook. If it finds none it will delete (aTag, notebookId)
   * from NotebookTagPair
   * */
  private void noteTagPairAfterDelete(long notebookId, String aTag)  //todo can null input delete whole table?
  {
    int  tagCount;
    Cursor cursor;

    String countQuery,
           deleteQuery;


    countQuery =      "select count ("+Note.ID + ") from " + Note.TABLE
                    + " where " +Note.NBID + "=" +notebookId
                    + " and (" + Note.TAGS + " like " + "'% " +aTag+ " %'"
                    + " or "   + Note.TAGS + " like " + "'" +aTag+ " %'"
                    + " or "   + Note.TAGS + " like " + "'% " +aTag+ "'"
                    + " or "   + Note.TAGS + " like " + "'" +aTag+ "')";


    deleteQuery=    "delete from "+ NotebookTagPair.TABLE
                   + " where " + NotebookTagPair.NBID +"="+notebookId+ " and "+ NotebookTagPair.TAG + "="+"'"+aTag+"'";


    cursor= mDatabase.rawQuery(countQuery, null);
    cursor.moveToFirst();
    tagCount= cursor.getInt(0);

    if (tagCount == 0)
    {                    //assert this tag no longer exists in npotebook so can be deleted from NotebookTagPairs
       mDatabase.execSQL(deleteQuery);
    }
    // else do nothing since aTag still exists in the notebook


    return;
  }


  /**
   * Adds entry to notebooktagpair table. If entry already exists no change occurs.
   * @param aTag
   * @param notebookId
   */
  private void createNotebookTagPair(long notebookId, String aTag)
  {

    ContentValues values;

    if ( !aTag.equals("") && aTag != null)
    {
      values= new ContentValues();
      values.put(NotebookTagPair.NBID, notebookId);
      values.put(NotebookTagPair.TAG, aTag);
      mDatabase.insertWithOnConflict(NotebookTagPair.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    return;
  }


  /**
   *
   * @param nbtp - both nbid and tag must be specified
   * @return
   */         //   DEBUG METHOD
  public boolean notebookTagPairExists(NotebookTagPair nbtp)
  {
    boolean result;
    Cursor cursor;

    String query= "select * from " +NotebookTagPair.TABLE
                + " where " +NotebookTagPair.NBID+ "=" +nbtp.getNotebookId()
                + " and "   +NotebookTagPair.TAG+ "=" + "'"+nbtp.getTag()+ "'";

    cursor= mDatabase.rawQuery(query, null);

    if (cursor.getCount() == 1)
    {
      result= true;
    }
    else  //IMPORTANT: this case includes entry not existing as well as duplicate results (ie. inconsistant db)
    {
      result= false;
    }


    return result;
  }






  // testing transactions
  //Comment out in final version of code
  //DEBUG METHOD
  public boolean isDbChangedBeforeEndOfTransaction() //YES!
  {
    boolean result;

    ContentValues noteRow;
    Note note;
    long insertId;



    note = new Note();
    note.setTitle("Bob");
    note.setContent("Likes to eat");
    note.setTags("fat old skinny");
    note.setNbGuid(1);



    try
    {
      mDatabase.beginTransaction();


      //todo probably exception if note fields are empty (null)

      //add new note to databae
      noteRow = new ContentValues();
      noteRow.put(Note.TITLE, note.getTitle());
      noteRow.put(Note.CONTENT, note.getContent());
      noteRow.put(Note.NBID, note.getNbGuid());
      noteRow.put(Note.TAGS, note.getTags());
      noteRow.put(Note.DATE_CREATED, Calendar.getInstance().getTimeInMillis());
      noteRow.put(Note.DATE_MODIFIED, Calendar.getInstance().getTimeInMillis());

      insertId= mDatabase.insert(Note.TABLE, null, noteRow);

      note= this.getNote(insertId);
      if (note != null)
      {
        result= true;
      }
      else
      {
        result= false;
      }

      mDatabase.setTransactionSuccessful();
    }
    finally
    {
      mDatabase.endTransaction();
    }

    return result;
  }


  ////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////FROM NOTEBOOKDATASOURCE/////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////


  public Notebook createNotebook(String title)    //todo ascDsc not addressed
  {
    Notebook notebook;

    notebook= new Notebook();
    notebook.setTitle(title);

    notebook= createNotebook(notebook);
    return notebook;
  }

  /**
   * this cannot be used to create "No Category" notebook since a guid of 0 is interpreted
   * as having no guid (ie. defualt initialization)
   * @param notebook
   * @return
   */
  public Notebook createNotebook(Notebook notebook)
  {
    Notebook newNotebook= null;
    ContentValues notebookRow,
                  googleNotebookRow;
    Cursor cursor;
    long insertId=-1;       //TO Avert compiler error


    if (notebook != null)
    {
      notebookRow= notebook.toContentValues();

      try
      {
        mDatabase.beginTransaction();

        insertId= mDatabase.insert(Notebook.TABLE, null, notebookRow);
        //create and insert googleNotebook row
        googleNotebookRow= new ContentValues();
        googleNotebookRow.put(GoogleNotebook.LOCAL_ID, insertId);
        googleNotebookRow.put(GoogleNotebook.TO_DELETE, 0);
        mDatabase.insert(GoogleNotebook.TABLE, null, googleNotebookRow);

        mDatabase.setTransactionSuccessful();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        mDatabase.endTransaction();
      }

      cursor = mDatabase.query(Notebook.TABLE,
              Notebook.ALL_COLUMNS, Notebook.ID + " = " + insertId, null,
              null, null, null);
      cursor.moveToFirst();
      newNotebook= Notebook.fromCursor(cursor);
      cursor.close();
    }



    return newNotebook;
  }







  private void deleteAllNotebookTagPairsFrom(long notebookId)
  {
    String query= "delete from " +NotebookTagPair.TABLE
            + " where " +NotebookTagPair.NBID+ "=" +notebookId;

    mDatabase.execSQL(query);

    return;
  }



  public List<Notebook> getAllNotebooks(String sortField)
  {
    List<Notebook> notebooks;
    Cursor cursor;
    Notebook newBook;

    String query= "select * from " +Notebook.TABLE
            + " order by " +sortField+ " ASC ";


    notebooks= new ArrayList<Notebook>();

    //  cursor= database.query(Notebook.TABLE,
    //          allColumns, null, null, null, null, null);

    cursor= mDatabase.rawQuery(query, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {

      newBook = Notebook.fromCursor(cursor);   //this should return a single noteBook entry
      notebooks.add(newBook);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return notebooks;
  }





  //todo refactor
  public List<Notebook> getAllNotebooks()
  {
    List<Notebook> notebooks;
    Cursor cursor;
    Notebook newBook;

    String query= "select * from " +Notebook.TABLE
            + " order by " +Notebook.ORDINAL+ " ASC ";



    notebooks= new ArrayList<Notebook>();

    //  cursor= database.query(Notebook.TABLE,
    //          allColumns, null, null, null, null, null);

    cursor= mDatabase.rawQuery(query, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {

      newBook = Notebook.fromCursor(cursor);   //this should return a single noteBook entry
      notebooks.add(newBook);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return notebooks;
  }

  public Notebook getNotebook(long notebookId)
  {
    Notebook notebook= new Notebook();
    Cursor cursor;
    String query= "select * from " +Notebook.TABLE
            + " where " +Notebook.ID+ "=" +notebookId;

    cursor= mDatabase.rawQuery(query, null);
    cursor.moveToFirst();
    notebook= Notebook.fromCursor(cursor);

    return notebook;
  }

  /**
   * returns first occurence of notebook with matching title
   * @param title
   * @return
   */
  public Notebook getNotebook(String title)
  {
    Notebook notebook;
    Cursor cursor;
    String query= "select * from " +Notebook.TABLE
            + " where " +Notebook.TITLE+ "='" +title+ "'" ;

    cursor= mDatabase.rawQuery(query, null);
    cursor.moveToFirst();
    notebook= Notebook.fromCursor(cursor);

    return notebook;
  }



  public void updateOrdinals(List<Notebook> notebookList)
  {
    //assumption: notebookList is already in updated order

    String query;


    for (int i= 0; i<notebookList.size(); i++)
    {
      query=        "update " +Notebook.TABLE
              + " set " +Notebook.ORDINAL+ "=" + i
              + " where " +Notebook.ID+ "=" +notebookList.get(i).getGuid();

      mDatabase.execSQL(query);
    }

    return;
  }








  /**
   * Updates all fields of notebook
   * notebook.toContentValues() is used to change a notebook to a notebookRow
   */
  public Notebook updateNotebook(Notebook notebook)
  {
    ContentValues contentValues;
    Cursor cursor;

    contentValues= notebook.toContentValues();
    mDatabase.update(Notebook.TABLE, contentValues, Notebook.ID+ "=" +notebook.getGuid(), null);

    String  query = "select * from " +Notebook.TABLE
            + " where " +Notebook.ID+ "=" +notebook.getGuid();

    //Receive update notebook and assign
    cursor = mDatabase.rawQuery(query, null);
    cursor.moveToFirst();
    notebook= Notebook.fromCursor(cursor);
    cursor.close();

    return notebook;
  }





  //  public Notebook updateNotebook(Notebook notebook)               h
//  {
//    /**
//     * Todo: Refactor this so it uses toContentValues()         with a differnt version  of  mDatabase.insert(GoogleNotebook.TABLE, null, googleNotebookRow);
//     */
//
//    Cursor cursor;
//
//    String query=   "UPDATE " + Notebook.TABLE
//                  + " SET " +Notebook.TITLE+ "='" +notebook.getTitle()+ "', "
//                  +Notebook.ORDINAL+ " =" +notebook.getOrdinal()+ " ,"
//                  +Notebook.DATE_MODIFIED+ " =" +Calendar.getInstance().getTimeInMillis()+ ", "          //todo this is updated everytime a search is done....
//                  +Notebook.ASCDSC+ "='" +notebook.getAscdsc()+ "' ,"
//                  +Notebook.TODO_VIEW_TYPE+ "='" +notebook.getTodoViewType().name()+ "' ,"
//                  +Notebook.HEADER_COLOR+ "=" +notebook.getHeaderColor()+ " ,"
//
//
//                  +Notebook.VIEW_TYPE+ "='" +notebook.getViewType().name()+ "' "
//                  + " WHERE " +Notebook.ID+ "=" +notebook.getGuid();
//
//
//    mDatabase.execSQL(query);    //update notebook
//
//    query = "select * from " +Notebook.TABLE
//            + " where " +Notebook.ID+ "=" +notebook.getGuid();
//
//    //Receive update notebook and assign
//    cursor = mDatabase.rawQuery(query, null);
//    cursor.moveToFirst();
//    notebook= Notebook.fromCursor(cursor);
//    cursor.close();
//
//
//    return notebook;
//  }






  public long getNoteCount(long notebookId)
  {
    Cursor cursor;
    long rowCount;

    String query= "select count (*) from " +Note.TABLE
            + " where " +Note.NBID+ "=" +notebookId;

    cursor= mDatabase.rawQuery(query, null);
    cursor.moveToFirst();
    rowCount= cursor.getLong(0);

    return rowCount;
  }

  public long getNoteCount()
  {
    Cursor cursor;
    long rowCount;

    String query= "select count (*) from " +Note.TABLE;

    cursor= mDatabase.rawQuery(query, null);
    cursor.moveToFirst();
    rowCount= cursor.getLong(0);

    return rowCount;
  }

  public long getNotebookCount()
  {
    Cursor cursor;
    long rowCount;

    String query= "select count (*) from " +Notebook.TABLE;

    cursor= mDatabase.rawQuery(query, null);
    cursor.moveToFirst();
    rowCount= cursor.getLong(0);

    return rowCount;
  }



  public List<String> getTagsFromNotebook(long notebookId)
  {
    List<String> tagList= new ArrayList<String>();
    Cursor cursor;
    String query =  "select " +NotebookTagPair.TAG+ " from " +NotebookTagPair.TABLE
            + " where " +NotebookTagPair.NBID+ "=" +notebookId
            + " order by " +NotebookTagPair.TAG+ " COLLATE NOCASE ASC";

    cursor= mDatabase.rawQuery(query, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {
      tagList.add(cursor.getString(0));   //this should return a single noteBook entry
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();

    return tagList;
  }

  public List<String> getAllTags()
  {
    List<String> tagList= new ArrayList<String>();
    Cursor cursor;
    String query =  "select distinct " +NotebookTagPair.TAG+ " from " +NotebookTagPair.TABLE
                   +" order by " +NotebookTagPair.TAG+ " COLLATE NOCASE ASC";




    cursor= mDatabase.rawQuery(query, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {
      tagList.add(cursor.getString(0));   //this should return a single noteBook entry
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();

    return tagList;

  }




  /**
   * 1)Deletes the notebook with notebookI
   * 2)Resets the nb_id of notes that were of that notebook to zero "No category"
   * 3)Deletes noteTagPairs for deleted notebook and adds them to "No category" notebook
   * @param notebookId
   * @return
   */
  //todo has noteDs in it. Need to fix that when refactor to same class
  public boolean deleteNotebook(long notebookId)  //throws SQLException
  {
    int result; // number of rows affected by change
    boolean success;
    List<String> tagList;
    //todo: WARNING IF LAST PARAMETER OF delete(...) IS NULL THEN ALL RECORDS WILL BE DELETED
    //NoteDataSource noteDs;


    String removeNotesFromNotebook= "update "  +Note.TABLE
            + " set " +Note.NBID+ "=0"
            + " where " +Note.NBID+ "=" +notebookId;

   // noteDs= new NoteDataSource(mContext);
    try
    {
      mDatabase.beginTransaction();

      //set notes that were in thus notebook nbid to zero
      mDatabase.execSQL(removeNotesFromNotebook);                                              //db update

      //get all tags from the to be deleted notebook and then add them to "No category" Notebook
      tagList= getTagsFromNotebook(notebookId);
      //add tags to notebooktagpairs if they are not already there
      //noteDs.open();
      for (int i= 0; i<tagList.size(); i++)
      {
        createNotebookTagPair(0, tagList.get(i));                                     //db update
      }


      //delete all associated notebooktagpairs
      deleteAllNotebookTagPairsFrom(notebookId);                                          //db update


      //delete notebook
      result= mDatabase.delete(Notebook.TABLE, Notebook.ID + " = " + notebookId, null);        //db update

      if (result>0)
      { //assert a notebook has been deleted so delete associated


        String googleNotebookUpdateQuery= "UPDATE " + GoogleNotebook.TABLE
                                        + " SET " +GoogleNotebook.TO_DELETE+ "=1 "
                                        + " WHERE " +GoogleNotebook.LOCAL_ID+ "=" +notebookId;

        mDatabase.execSQL(googleNotebookUpdateQuery);   //db update

      }

      mDatabase.setTransactionSuccessful();
    }
    finally
    {
      mDatabase.endTransaction();
      //noteDs.close();
    }

    if (result == 0) // ie. zero rows affected
      success = false;
    else
      success= true;


    return success;
  }


  /**
   * Creates record in media table, saves a copy of the file and saves it along with thumbnail.
   * If this can't be done, this method returns null and no change is made to Media table or file system
   * @param mediaUri
   * @param noteGuid
   * @return
   */
  public Media createMedia (Uri mediaUri /*, String mimeType*/, long noteGuid)
  {
    /**
     * todo: recheck usages of java Uri and android Uri classes
     */
    Media media;
    File fileOriginal,
         fileCopy= null,
         previewFile= null;
    FileOutputStream fileOutputStream;
    FileUtils fileUtils;

    String  newFilePath,
            fileExtension,
            previewFilePath;

    MimeTypeMap mimeTypeMap;
    ContentValues cvIncomplete,
                  contentValues;

    Bitmap  originalBitmap,
            previewBitmap;

    long newMediaId;
    Cursor cursor;






    try
    {
      mDatabase.beginTransaction();

      /**
       * Create empty record in table to get guid (so can name file)
       */
//      media= new Media();
//      media.setNoteId(noteGuid);

      cvIncomplete= new ContentValues();
      cvIncomplete.put(Media.NOTE_ID, noteGuid);
      newMediaId = mDatabase.insert(Media.TABLE, null, cvIncomplete/*media.toContentValues()*/);
//      media.setGuid(insertId);                                                  //todo: i am assuming insertId is the id of inserted row


      /**
       * Save  image to disk
       *
       * todo: MUST CHANGE IF FILES OTHER THAN IMAGES STORED
       */
      fileOriginal = new File(mediaUri.getPath());           //todo: probably error!!!!
      fileExtension= fileOriginal.getName().substring(fileOriginal.getName().lastIndexOf(".")+1);
      newFilePath= mContext.getApplicationContext().getFilesDir().toString()+File.separator+"media"+File.separator+newMediaId+"."+fileExtension;
      fileCopy= new File(newFilePath);

      fileUtils= FileUtils.getFileUtils();
      fileUtils.copyFile(fileOriginal, fileCopy);                        //todo: file has right size.. but when opened cant view it?    png to jpg???


      /**
       * Create preview bitmap and then save
       */
      originalBitmap=  BitmapFactory.decodeFile(newFilePath);      //WORKS... for small images.. not large?
      previewBitmap= ThumbnailUtils.extractThumbnail(originalBitmap, 200, 200);
      previewFilePath= mContext.getApplicationContext().getFilesDir().toString()+File.separator+"media"+File.separator+newMediaId+"_thumb."+fileExtension;
      previewFile= new File(previewFilePath);
      fileOutputStream= new FileOutputStream(previewFile);
      previewBitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
      fileOutputStream.close();





      /**
       * Populate Media row
       */
      mimeTypeMap= MimeTypeMap.getSingleton();
      media = new Media();
      media.setGuid(newMediaId);
      media.setNoteId(noteGuid);
      media.setPath(fileCopy.getPath());
      media.setMimeType(mimeTypeMap.getMimeTypeFromExtension(fileExtension));
      media.setUri(/*URLEncoder.encode(fileCopy.toURI().toString(), "UTF-8")*/fileCopy.toURI().toString());
      media.setPreviewPath(previewFile.getPath());

      media.setOriginalFilename(fileOriginal.getName());

      /**
       * Update Media table record (created earlier in method
       */
      contentValues= media.toContentValues();
      mDatabase.update(Media.TABLE, contentValues, Media.ID+ "=" +media.getGuid(), null);


      cursor = mDatabase.query(Media.TABLE,
              Media.ALL_COLUMNS, Media.ID + " = " + newMediaId, null,
              null, null, null);
      cursor.moveToFirst();
      media= Media.fromCursor(cursor);
      cursor.close();




      mDatabase.setTransactionSuccessful();

    }
    catch (Exception e)
    {
      /**
       * todo: delete any files created file on fail
       */
      //
      media= null;
      if (fileCopy !=null)
      {
        fileCopy.delete();
      }
      if (previewFile!=null)
      {
        previewFile.delete();
      }
      e.printStackTrace();
    }
    finally
    {
      mDatabase.endTransaction();
    }



    return media;
  }


  /**
   * returns 0 size list if no media. This works well with for each construct
   * @param noteId
   * @return
   */
  public List<Media> getMedia(long noteId)
  {
    List mediaList;
    Cursor cursor;
    String whereClause = Media.NOTE_ID+ "=" +noteId;

    cursor= mDatabase.query(Media.TABLE, Media.ALL_COLUMNS, whereClause, null, null, null, null );

    mediaList= Media.listFromCursor(cursor);

//    if (mediaList.size() == 0)
//    {
//      mediaList= null;
//    }

    return mediaList;

  }




  /**
   * deletes all notebooktagpairs and generates a new set for the whole database
   */
  //todo IMPLEMENT
  public void regenerateNotebookTagPairs()
  {

    return;
  }


  /**
   * Gets list of tables that currently exists
   * @return
   */
  public List<String> getAllTableNames()
  {
    List<String> tableNames= new ArrayList();
    Cursor cursor;
                  //String query= "SELECT * FROM dbname.sqlite_master WHERE type='table';"
    String query= "select name from sqlite_master where type = 'table'"; //"SELECT * FROM " +mDbFilename+ ".sqlite_master WHERE type='table';"

    cursor= mDatabase.rawQuery(query, null);

    cursor.moveToFirst();
    while(!cursor.isAfterLast())
    {
      tableNames.add(cursor.getString(0));
      cursor.moveToNext();
    }


    return tableNames;
  }


  /**
   * returns the largest _id in the notebook table
   * @return
   */

  public long getLargestNotebookGuid()
  {
    long notebookId;
    Cursor cursor;
    String query= "Select max(" +Notebook.ID+ ") from " +Notebook.TABLE;

    cursor= mDatabase.rawQuery(query, null);

    cursor.moveToFirst();
    notebookId= cursor.getLong(0);
    cursor.close();

    return notebookId;
  }

  //todo what happens if there are no notes? exception?
  public long getLargestNoteGuid()
  {
    long noteId;
    Cursor cursor;
    String query= "Select max(" +Note.ID+ ") from " +Note.TABLE;

    cursor= mDatabase.rawQuery(query, null);

    cursor.moveToFirst();
    noteId= cursor.getLong(0);
    cursor.close();

    return noteId;
  }


  /**
   * For DEBUG ONLY. Returns the database so it can be directly manipulated
   * @return
   */
  SQLiteDatabase getDatabase()
  {
    return mDatabase;
  }





  /**
   * Keys for Extras
   *
   * indexes are -1 if they don't exist
   */
  public enum Key
  {

    /**
     * Types held in the map
     */
    NOTE_LIST,                                         // List<Note>
   // GROCERY_LIST,                                      // List<Note>

    /**
     * Indexes
     */
    PAST_TODOS_START_INDEX,                            // int
    TODAYS_TODOS_START_INDEX,                          // int
    FUTURE_TODOS_START_INDEX,                          // int
    GROCERY_LIST_COMPLETED_START_INDEX,
    GROCERY_LIST_NOT_COMPLETED_START_INDEX,


    /**
     * Sentinel Values
     */

    /**
     * Parts of Todo List
     */
    PAST,
    TODAY,
    FUTURE,



    /**
     * Determines how to view
     */
    VIEW_TYPE,                //key

    VIEW_TODOS_SINGLE_DAY,    //the different values the key can contain
    VIEW_TODOS_ALL,           //
    VIEW_GROCERY_LIST

  }                    ////END ENUM KEY////


  // todo public useTransientDueDates(boolean use)

  /******************************************************
   * Private class Range                                *
   ******************************************************/
  private class Range
  {
    Key mKey;
    int mStart;
    int mEnd;

    Range(Key key, int start, int end)
    {
      mKey= key;
      mStart= start;
      mEnd= end;
    }

    public boolean inRange(int number)
    {
      boolean result= false;

      if (number >= mStart && number <= mEnd)
        result= true;

      return result;
    }


    public Key getKey()
    {
      return mKey;
    }
  }     ///////END INNER CLASS//////////



  /******************************************************
   * Private class Range                                *
   ******************************************************/
  public class TodoData
  {
    public List<Note> noteList;
    public int pastTodosStartIndex= -1,
    todaysTodosStartIndex= -1,
    futureTodosStartIndex= -1,
    groceryListStartIndex= -1,
    groceryListNotCompletedStartIndex= -1;


  }









}     ///////////////////////////////END CLASS///////////////////////////



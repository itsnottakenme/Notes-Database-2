package ndb.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ndb.types.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 11/03/13
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 * todo: refactor so that this is used to -ONLY- CREATE and UPGRADE tables
 *
 * Tables: notebooks, notes, notebookTagPairs, settings
 *
 * Not used: goole_notebooks
 */






public class NDBTableMaster extends SQLiteOpenHelper
{
  public static final String TAG= NDBTableMaster.class.getName();

  public static final String    NOTES_DB      = "notestore.db",
                                TEMP_DB      = "tempDb.db",
                                DEBUG_NOTES_DB = "DEBUG_notestore.db",
                                DEBUG_TEMP_DB      = "tempDb.db";


  public static final int DATABASE_VERSION = 3;



  //////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////// Database creation sql statements///////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////


  /**
   * Notebook table
   */
  private static final String CREATE_NOTEBOOKS_TABLE =                                  //notebooks
                      "create table " + Notebook.TABLE + "("
                                      + Notebook.ID + " integer primary key autoincrement, "
                                      + Notebook.TITLE + " text not null " + " , "
                                      + Notebook.ORDINAL + " integer, "
                                      + Notebook.VIEW_TYPE + " text, "
                                      + Notebook.NOTE_SORT + " text, "
                                      + Notebook.DATE_MODIFIED + " integer, "
                                      + Notebook.DATE_CREATED + " integer, "
                                      + Notebook.TODO_VIEW_TYPE + " text, "

                                      + Notebook.DEFAULT_ROLLOVER + " integer, "
                                     // + Notebook.TODO_VIEW_TYPE + " text, "
                                      + Notebook.ICON + " text, "
                                      + Notebook.HEADER_COLOR + " integer, "
                                      + Notebook.THEME + " text, "

                                      + Notebook.INT1 + " integer, "
                                      + Notebook.INT2 + " integer, "
                                      + Notebook.INT3 + " integer, "
                                      + Notebook.INT4 + " integer, "
                                      + Notebook.INT5 + " integer, "
                                      + Notebook.INT6 + " integer, "
                                      + Notebook.INT7 + " integer, "
                                      + Notebook.INT8 + " integer, "

                                      + Notebook.TEXT1 + " text, "
                                      + Notebook.TEXT2 + " text, "
                                      + Notebook.TEXT3 + " text, "
                                      + Notebook.TEXT4 + " text, "
                                      + Notebook.TEXT5 + " text, "
                                      + Notebook.TEXT6 + " text, "
                                      + Notebook.TEXT7 + " text, "
                                      + Notebook.TEXT8 + " text, "

                                      + Notebook.ASCDSC + " text)";


  /**
   * Note table
   */
  private static final String CREATE_NOTES_TABLE =
                      "create table " + Note.TABLE + "("
                                      + Note.ID + " integer primary key autoincrement, "
                                      + Note.TITLE + " text, "
                                      + Note.CONTENT + " text, "
                                      + Note.NBID + " integer, "
                                      + Note.DATE_MODIFIED + " integer, "
                                      + Note.DATE_CREATED + " integer, "
                                      + Note.CONTENT_PREVIEW + " text, "
                                      + Note.NOTE_TYPE + " integer, "
                                      + Note.TAGS + " text, "
                                      + Note.DATE_DUE + " integer, "
                                      + Note.ROLLOVER_UNCOMPLETED_TODOS + " integer, "
                                      + Note.SPANS + " text, "               // stored as Json
                                      + Note.COMPLETED + " integer, "          // boolean
                                      + Note.DATE_COMPLETED + " integer, "
                                      + Note.TODO_ORDINAL + " integer, "
                                      + Note.PRIORITY + " integer, "
                                      + Note.DUE_TIME + " integer, "
                                      + Note.REPEAT_INTERVAL + " text, "

                                      + Note.REPEAT_FIELD + " integer, "
                                      + Note.REPEAT_VALUE + " integer, "
                                      + Note.INT3 + " integer, "
                                      + Note.INT4 + " integer, "
                                      + Note.INT5 + " integer, "
                                      + Note.INT6 + " integer, "
                                      + Note.INT7 + " integer, "
                                      + Note.INT8 + " integer, "

                                      + Note.TEXT1 + " text, "
                                      + Note.TEXT2 + " text, "
                                      + Note.TEXT3 + " text, "
                                      + Note.TEXT4 + " text, "
                                      + Note.TEXT5 + " text, "
                                      + Note.TEXT6 + " text, "
                                      + Note.TEXT7 + " text, "
                                      + Note.TEXT8 + " text, "

                                      + " foreign key (" +Note.NBID+ ") references " +Notebook.TABLE+" (" +Notebook.ID+ ")"
                                      + " )";



  /**
   * Media table
   */
  private static final String CREATE_MEDIA_TABLE=
                      "create table " + Media.TABLE + "("
                                      + Media.ID + " integer primary key autoincrement, "
                                      + Media.NOTE_ID +  " integer, "
                                      + Media.MIME_TYPE + " text, "
                                      + Media.PATH + " text, "
                                      + Media.ORIGINAL_FILENAME + " text, "
                                      + Media.URI + " text, "

                                      + Media.PREVIEW + " text, "

                                      + Media.HASH + " text, "



                                      + Media.INT1 + " integer, "
                                      + Media.INT2 + " integer, "
                                      + Media.INT3 + " integer, "
                                      + Media.INT4 + " integer, "
                                      + Media.INT5 + " integer, "
                                      + Media.INT6 + " integer, "
                                      + Media.INT7 + " integer, "
                                      + Media.INT8 + " integer, "

                                      + Media.TEXT1 + " text, "
                                      + Media.TEXT2 + " text, "
                                      + Media.TEXT3 + " text, "
                                      + Media.TEXT4 + " text, "
                                      + Media.TEXT5 + " text, "
                                      + Media.TEXT6 + " text, "
                                      + Media.TEXT7 + " text, "
                                      + Media.TEXT8 + " text, "



                                      + " foreign key (" +Media.NOTE_ID + ") references " +Note.TABLE+" (" +Note.ID+ ")"
                                      + " )";






  // //NOTES
  //    TODO_ORDINAL= "TODO_ORDINAL",  //used for manual sorting of todos by user???  may be need to make it TODO_ORDINAL
  //            PRIORITY= "PRIORITY",          //used for sorting... especially todos
  //            DUE_TIME= "DUE_TIME", //to do  sub-sorts
  //  /*
  //   */                        COMPLETED_DATE= "COMPLETED_DATE", //<0 IF NOT COMPLETED. Used to differentiate between due_date and the date completed
  //            REPEAT_INTERVAL= "REPEAT_INTERVAL",   //0 for no repeat, other values for other repeat!
  //
  //            INT1= "INT1",
  //            INT2= "INT2",
  //            INT3= "INT3",
  //            INT4= "INT4",
  //            INT5= "INT5",
  //            INT6= "INT6",
  //            INT7= "INT7",
  //            INT8= "INT8",
  //            TEXT1= "TEXT1",
  //            TEXT2= "TEXT2",
  //            TEXT3= "TEXT3",
  //            TEXT4= "TEXT4",
  //            TEXT5= "TEXT5",
  //            TEXT6= "TEXT6",
  //            TEXT7= "TEXT7",
  //            TEXT8= "TEXT8";
  //


  /**
   * Notebook tagpairs table
   */
  private static final String CREATE_NOTEBOOKTAGPAIRS_TABLE =                            //notebooktagpairs
          "create table " + NotebookTagPair.TABLE + "("
                  + NotebookTagPair.NBID + " integer, "
                  + NotebookTagPair.TAG + " text, "
                  + " unique (" + NotebookTagPair.NBID +" , "+ NotebookTagPair.TAG + ") "
                  + " foreign key (" +NotebookTagPair.NBID+ ") references " +Notebook.TABLE+" (" +Notebook.ID+ ")"
                  + " )";


  /**
   * Settings table
   */
  private static final String CREATE_SETTINGS_TABLE =                                                       //Settings table
          "create table "  + Setting.TABLE + "("
                  + Setting.KEY + " text primary key, "
                  + Setting.VALUE + " text "
                  + " )";



  //todo: input FOREIGN KEY CONSTRAINTS
  //////////////////////////////////////////////////////////////////////////////////////////////////////////



  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////google drive tables////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  private static final String CREATE_GOOGLE_NOTEBOOKS_TABLE =                                  //notebooks
          "create table " + GoogleNotebook.TABLE + "("
//                  + GoogleNotebook.ID + " integer primary key autoincrement, "
                  + GoogleNotebook.LOCAL_ID + " integer primary key, "
                  + GoogleNotebook.GOOGLE_ID + " text " + " , "  //TODO can it be null?
                  + GoogleNotebook.TO_DELETE + " integer "
//                  + GoogleNotebook.GOOGLE_DATE_MODIFIED + " integer, "


//                  + GoogleNotebook.LOCAL_DATE_MODIFIED+ " integer"
                  + ")";



  //////////////////////////////////////////////////////////////////////////////////////////////////////////






           /*
  public NDBTableHelper(Context context)
  {
    super(context, NOTES_DB, null, DATABASE_VERSION);

    return;
  }          */

  public NDBTableMaster(Context context, String dbFilename)
  {
    super(context, dbFilename, null, DATABASE_VERSION);


    return;
  }



  @Override
  public void onCreate(SQLiteDatabase database) //Only is called if DB does not exist
  {
    database.execSQL(CREATE_NOTEBOOKS_TABLE);
    database.execSQL(CREATE_NOTES_TABLE);
    database.execSQL(CREATE_NOTEBOOKTAGPAIRS_TABLE);
    database.execSQL(CREATE_GOOGLE_NOTEBOOKS_TABLE);
    database.execSQL(CREATE_SETTINGS_TABLE);
    database.execSQL(CREATE_MEDIA_TABLE);

    return;
  }


  @Override              //todo: just drops the whole table!!!!
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    try
    {
      /**
       * begin Upgrade
       */
      db.beginTransaction();


      if (oldVersion == 1)
      {
        upgradeV1ToV2(db);           //todo if these throws an exception all hell will break loose as next case will execute anyways...
        oldVersion= oldVersion+1;
      }
      if (oldVersion == 2)
      {
        upgradeV2ToV3(db);
        oldVersion= oldVersion+1;
      }
//      if (oldVersion == 3)
//      {
//        upgradeV3ToV4(db);
//        oldVersion= oldVersion+1;
//      }






      db.setTransactionSuccessful();
    }
    finally
    {
      db.endTransaction();
    }


    return;
  }

  /**
   * Begin database upgrade methods
   *
   */
  private void upgradeV1ToV2(SQLiteDatabase db)
  {

      /**
       * Add columns to Notebook table
       */
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column DEFAULT_ROLLOVER integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column ICON text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column HEADER_COLOR integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column THEME text;");

      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT1 integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT2 integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT3 integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT4 integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT5 integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT6 integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT7 integer;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column INT8 integer;");

      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT1 text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT2 text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT3 text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT4 text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT5 text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT6 text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT7 text;");
      db.execSQL("ALTER TABLE " +Notebook.TABLE+ " add column TEXT8 text;");

      /**
       * Add columns to Note table
       */
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TODO_ORDINAL integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column PRIORITY integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column DUE_TIME integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column REPEAT_INTERVAL text;");

      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT1 integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT2 integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT3 integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT4 integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT5 integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT6 integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT7 integer;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column INT8 integer;");

      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT1 text;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT2 text;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT3 text;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT4 text;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT5 text;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT6 text;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT7 text;");
      db.execSQL("ALTER TABLE " +Note.TABLE+ " add column TEXT8 text;");


    return;
  }

  private void upgradeV2ToV3(SQLiteDatabase db)
  {
    /**
     * Add Media table
     */

    db.execSQL(CREATE_MEDIA_TABLE);
    return;
  }


//  private void upgradeV3ToV4(SQLiteDatabase db)
//  {
//    return;
//  }












  public void deleteDb()
  {
    //SQLiteDatabase (new File("lol");)


    return;
  }

  public static void deleteAllTables(Context context, String dbFilename)
  {
    NDBTableMaster dbHelper;
    SQLiteDatabase db;

    dbHelper = new NDBTableMaster(context, dbFilename);
    db = dbHelper.getWritableDatabase();


    db.execSQL("DROP TABLE IF EXISTS " + Notebook.TABLE);
    db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE);
    db.execSQL("DROP TABLE IF EXISTS " + NotebookTagPair.TABLE);
    db.execSQL("DROP TABLE IF EXISTS " + GoogleNotebook.TABLE);
    //todo add all tables
    db.close();

    return;
  }



}       //////////END CLASS/////////




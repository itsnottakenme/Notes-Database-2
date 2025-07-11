package ndb.types;

import android.content.ContentValues;
//import android.content.res.XmlResourceParser;
import android.database.Cursor;
import ndb.util.Util;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 09/03/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 *
 * This class is a struct to contain SQLite outputed data
 */







public class Notebook /*implements Parcelable */
{

  public static final String[] ALL_COLUMNS =
          { Notebook.ID, Notebook.TITLE, Notebook.ORDINAL, Notebook.VIEW_TYPE,
            Notebook.NOTE_SORT, Notebook.DATE_MODIFIED,Notebook.DATE_CREATED, Notebook.ASCDSC, Notebook.TODO_VIEW_TYPE,
            Notebook.DEFAULT_ROLLOVER, Notebook.ICON, Notebook.HEADER_COLOR, Notebook.THEME,
            Notebook.INT1, Notebook.INT2, Notebook.INT3, Notebook.INT4, Notebook.INT5, Notebook.INT6, Notebook.INT7, Notebook.INT8,
            Notebook.TEXT1, Notebook.TEXT2, Notebook.TEXT3, Notebook.TEXT4, Notebook.TEXT5, Notebook.TEXT6, Notebook.TEXT7, Notebook.TEXT8,
          };


  //Table infromation for SQL database
  public static final String
                              TABLE =        "notebooks",
                              ID =            "_id",    //primary key
                              TITLE =         "title", //name of notebook   //todo change to COL_TITLE!!!

                              ORDINAL =        "ordinal", //position in notebook list for gui
                              VIEW_TYPE =       "listtype",            //todo when recreate database CHANGE COLUMN NAME!!!!!!!!!
                              NOTE_SORT =       "notesort",
                              DATE_MODIFIED =  "datemodified",
                              DATE_CREATED =   "datecreated",

                              ASCDSC       =    "ascdsc",     //indicates whether note sort is ascending or descending
                              TODO_VIEW_TYPE= "TODO_VIEW_TYPE",       //the view type when in todo note view
                              HEADER_COLOR= "HEADER_COLOR",
                              DEFAULT_ROLLOVER= "DEFAULT_ROLLOVER",     //BOOLEAN deterimines default setting for notes created in the notebook. aka rollover todo notes due dview






  /**
   * Refactoring instructions:
   * Every time a new field is added
   * 1) NDBTableHelper - update onUpdate() method
   * 2) NDBTableMaster - update CREATE_TABLE string
   * 3) Notebook - update allNotebookColumns
   * 4) Notebook - update fromCursor()
   * 5) Notebook - update toContentValues()
   *
   *
   *
   *    // no longer need to update: NoteDataSource
   *
   */


  /**
   * Implemented -ONLY- in db but not used
   * todo: steps 4-5 still need to be completed
   */


                            ICON= "ICON",                           //URI of icon (or id?) to be shown on folder

                            THEME= "THEME",



                            INT1= "INT1",
                            INT2= "INT2",
                            INT3= "INT3",
                            INT4= "INT4",
                            INT5= "INT5",
                            INT6= "INT6",
                            INT7= "INT7",
                            INT8= "INT8",
                            TEXT1= "TEXT1",
                            TEXT2= "TEXT2",
                            TEXT3= "TEXT3",
                            TEXT4= "TEXT4",
                            TEXT5= "TEXT5",
                            TEXT6= "TEXT6",
                            TEXT7= "TEXT7",
                            TEXT8= "TEXT8";




  /**
   * Not implemented yet
   */





  ////////////////////////////////////////////////////////////////////////////////


  /**
   * Default Notebooks
   * Note: the actual values are from R.xml.default_notebooks
   * this values simply mirror what is in the file
   */
  public static final String
                              DEFAULT=        "No Category",//,
                              ALL_NOTES= "All Notes";
  public static final long
                              DEFAULT_ID=        0,//,
                              ALL_NOTES_ID= -10;



  //packing for intents

  /**
   * Packing for intents
   */
  final public static String GUID_EXTRA =         "notebook guid";    //content is a long
  final public static long ALL=         -1;       // indicates that all notebooks are wanted



  //todo these are folder names are reserved folder names (so make sure names aren't taken for Google Drive)
  final public static String[] FORBIDDEN_FOLDER_NAMES= {".backup", ""};




  /**
   * Turns the current row of cursor into a notebook
   * @param cursor
   * @return
   */
  public static Notebook fromCursor(Cursor cursor)              //TODO THUS NO LONGER WORKA UPDATE!!!
  {
    String view;    //the view type
    Notebook notebook = new Notebook();
    String todoViewType;
    int headerColor;


    if (cursor.getCount() > 0)
    {
//      int index= cursor.getColumnIndex(Notebook.GUID);
//      long guid= cursor.getLong(index);
//      notebook.setGuid(guid);
      notebook.setGuid(cursor.getLong(cursor.getColumnIndex(Notebook.ID)));    //todo: check these to make sure in right order
      notebook.setTitle(cursor.getString(cursor.getColumnIndex(Notebook.TITLE)));
      notebook.setOrdinal(cursor.getLong(cursor.getColumnIndex(Notebook.ORDINAL)));

      view= cursor.getString(cursor.getColumnIndex(Notebook.VIEW_TYPE));
      if (view == null)
      {
        notebook.setViewType(ViewType.NORMAL);
      }
      else
      {
        notebook.setViewType(ViewType.valueOf(view));
      }

      notebook.setNoteSort(cursor.getString(cursor.getColumnIndex(Notebook.NOTE_SORT)));
      notebook.setDateModified(cursor.getLong(cursor.getColumnIndex(Notebook.DATE_MODIFIED)));
      notebook.setDateCreated(cursor.getLong(cursor.getColumnIndex(Notebook.DATE_CREATED)));

      todoViewType= cursor.getString(cursor.getColumnIndex(Notebook.TODO_VIEW_TYPE));
      if (todoViewType == null)
      {
        notebook.setTodoViewType(TodoViewType.ALL_DAYS);
      }
      else
      {
        notebook.setTodoViewType(TodoViewType.valueOf(todoViewType));
      }

      notebook.setHeaderColor(cursor.getInt(cursor.getColumnIndex(Notebook.HEADER_COLOR)));
      notebook.setDefaultRollover( Util.longToBoolean( cursor.getLong(cursor.getColumnIndex(Notebook.DEFAULT_ROLLOVER)) ));


    }
    else
    {
      notebook= null;
    }

    return notebook;
  }


  public static List<Notebook> loadNotebooks(XmlPullParser parser)    throws XmlPullParserException, IOException
  {
    List<Notebook> notebooks;
    Notebook newNotebook= null;
    int eventType;

    String debug_currentTag;

    final String
                  NOTEBOOKS_ELEMENT="notebooks",
                  NOTEBOOK_ELEMENT="notebook",
                  TITLE_ELEMENT="title",
                  ID_ELEMENT="id";

    notebooks= new ArrayList();
    eventType= parser.next();
    while  (eventType != XmlPullParser.END_DOCUMENT)
    {
      debug_currentTag= parser.getName();
      if (eventType == XmlPullParser.START_TAG && parser.getName().equals(NOTEBOOK_ELEMENT))
      {
        newNotebook= new Notebook();
      }
      else if (eventType == XmlPullParser.START_TAG && parser.getName().equals(TITLE_ELEMENT))
      {
        parser.next();
        newNotebook.setTitle(parser.getText());
      }
      else if (eventType == XmlPullParser.START_TAG && parser.getName().equals(ID_ELEMENT))
      {
        parser.next();
        newNotebook.setGuid(new Long(parser.getText()));
      }
      else if (eventType == XmlPullParser.END_TAG && parser.getName().equals(NOTEBOOK_ELEMENT))
      {
        notebooks.add(newNotebook);
      }

      eventType= parser.next();
    }

    if (notebooks.size() == 0)
    {
      notebooks= null;
    }

    return notebooks;
  }

  /////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////BEGIN OBJECT INSTANCE////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////
  private String title;
  private long guid,       //need to check that this is actually a long in sqlite
                     //number of items in notebook. use trigger?


               dateCreated,
               dateModified,
               ordinal;


  private ViewType viewType;
  private TodoViewType todoViewType;
  private String noteSort;               //todo update this in NoteListActivity. probably needs ascDsc as well

  private String ascdsc;                 //should only contain Sort.Ascending or Sort.Descending

  int headerColor;                  //the color of notebook item and actionbar and bottombar

  private boolean defaultRollover;
  /////////fields to add////////////// //don't forget to add to parceable.

  ////////////////////////////////////////////////////////////////////////////////////////////////
  public Notebook()
  {
    //todo should i be setting guid as -1 here???????
    defaultRollover= false;

    return;
  }

  public Notebook(String title, long id)
  {
    setTitle(title);
    setGuid(id);


    return;
  }
/////////////////////////////////////////////////////


  public boolean isDefaultRollover()
  {
    return defaultRollover;
  }

  public void setDefaultRollover(boolean defaultRollover)
  {
    this.defaultRollover = defaultRollover;
  }

  public int getHeaderColor()
  {
    return headerColor;
  }

  public void setHeaderColor(int headerColor)
  {
    this.headerColor = headerColor;
  }

  public String getAscdsc()
  {
    return ascdsc;
  }

  public void setAscdsc(String ascdsc)
  {
    this.ascdsc = ascdsc;
  }






  public String getNoteSort()
  {
    return noteSort;
  }

  public void setNoteSort(String noteSort)
  {
    this.noteSort = noteSort;
  }

  public Notebook.ViewType getViewType()
  {
    return viewType;
  }

  public void setViewType(Notebook.ViewType viewType)
  {
    this.viewType = viewType;
  }



  public long getDateCreated()
  {
    return dateCreated;
  }

  public void setDateCreated(long dateCreated)
  {
    this.dateCreated = dateCreated;
  }

  public long getDateModified()
  {
    return dateModified;
  }

  public void setDateModified(long dateModified)
  {
    this.dateModified = dateModified;
  }

  public long getOrdinal()
  {
    return ordinal;
  }

  public void setOrdinal(long ordinal)
  {
    this.ordinal = ordinal;
  }




  public void setTitle(String title)
  {

    this.title= title;
    return;

  }

  public void setGuid(long id)
  {

    this.guid= id;
    return;
  }



  public String getTitle()
  {

    return title;

  }

  public long getGuid()
  {

    return guid;

  }


  public TodoViewType getTodoViewType()
  {
    return todoViewType;
  }

  public void setTodoViewType(TodoViewType todoViewType)
  {
    this.todoViewType = todoViewType;
  }

  //this is what listViewAdaptor uses to diplay in listView
  public String toString()
  {
    return /*getGuid()+ "   "+*/getTitle();
  }


  /**
   * todo: GUID is currently only added  if (getGuid()>0). What is the optimal way?
   * @return
   */
  public ContentValues toContentValues()
  {
    ContentValues notebookRow;


    notebookRow = new ContentValues();



      if (getTitle()!=null)
      {
        notebookRow.put(Notebook.TITLE, getTitle());
      }
      if (getViewType()!=null)
      {
        notebookRow.put(Notebook.VIEW_TYPE, getViewType().name());   //todo is this right?
      }

      if (getTodoViewType()!=null)
      {
        notebookRow.put(Notebook.TODO_VIEW_TYPE, getTodoViewType().name());   //todo is this right?
      }

      ////////////
      if (getNoteSort() == null)
      {
        notebookRow.put(Notebook.NOTE_SORT, Note.DATE_MODIFIED);  // NOT Sort.DATE_MODIFIED put a column name!
      }
      else
      {
        notebookRow.put(Notebook.NOTE_SORT, getNoteSort());
      }
      ////////////


      if (getGuid()>0)
      {
        notebookRow.put(Notebook.ID, getGuid());
      }
      else
      {
        notebookRow.put(Notebook.ORDINAL, 6969);  //so new notebook goes to end of list
      }
      if (getOrdinal()>0)
      {
        notebookRow.put(Notebook.ORDINAL, getOrdinal());
      }
      if (getDateCreated()>0)
      {
        notebookRow.put(Notebook.DATE_CREATED, getDateCreated());
      }
      if (getDateModified()>0)
      {
        notebookRow.put(Notebook.DATE_MODIFIED, getDateModified());
      }
      if (getAscdsc() == null)
      {
        notebookRow.put(Notebook.ASCDSC, Sort.DESCENDING);
      }
      else
      {
        notebookRow.put(Notebook.ASCDSC, getAscdsc());
      }

    notebookRow.put(Notebook.HEADER_COLOR, headerColor);

    if (defaultRollover == false)
    {
      notebookRow.put(Notebook.DEFAULT_ROLLOVER, 0);
    }
    else
    {
      notebookRow.put(Notebook.DEFAULT_ROLLOVER, 1);
    }




    return notebookRow;
    }




  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////Parceable implementation////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////

  //todo SO MUCH EASIER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  //todo rather than updating parceable for EVERY changed variable only pass the _id field and let the other actvity pull it from the database
  /*
  public Notebook(Parcel in)
  {
    readFromParcel(in);
  }




  @Override
  public void writeToParcel(Parcel out, int flags)
  {             //todo: must be updated everytime new field in class!!!!!!!!!
    out.writeString(title);
    out.writeLong(guid);

    out.writeLong(dateCreated);
    out.writeLong(dateModified);
    out.writeLong(ordinal);

    out.writeString(listType);
    out.writeString(noteSort);


    return;
  }

  private void readFromParcel(Parcel in)
  {             //todo: must be updated everytime new field in class!!!!!!!!!

    title=            in.readString();
    guid=             in.readLong();

    dateCreated=      in.readLong();
    dateModified=     in.readLong();
    ordinal=          in.readLong();

    listType=         in.readString();
    noteSort =         in.readString();


    return;
  }

  public static final Parcelable.Creator<Notebook> CREATOR = new Parcelable.Creator<Notebook>()
  {

    public Notebook createFromParcel(Parcel in)
    {
      return new Notebook(in);
    }

    public Notebook[] newArray(int size)
    {
      return new Notebook[size];
    }

  };

  @Override
  public int describeContents()
  {
    return 0;
  }
*/



  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////End Parcelable Implementation/i//////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////



  /////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////INNER TYPES///////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  public enum ViewType
  {
    NORMAL(0),
    TODO (1),
    GROCERIES(2);         //GROCERY LISTS

    private int mType;

    private ViewType(int type)
    {
      mType= type;
      return;
    }

    @Override
    public String toString()
    {
      String string;
      switch(mType)
      {
        case 0:
          string= "Notes";
          break;
        case 1:
          string= "Todo List";
          break;
        case 2:
          string= "Groceries List";
          break;
        default:
          string= "Invalid type";
          break;
      }
      return string;
    }

    public int toInt()
    {
      return mType;
    }
    static public ViewType fromInt(int i)
    {
      ViewType type;
      switch(i)
      {
        case 0:
          type= NORMAL;
          break;
        case 1:
          type= TODO;
          break;
        case 2:
          type= GROCERIES;
          break;
        default:
          type= null;
          break;
      }

      return type;
    }

  }  ////END ENUM////

  /**
   * TodoViewType
   */
  public enum TodoViewType
  {
    SINGLE_DAY(0),
    ALL_DAYS (1);

    private int mType;

    private TodoViewType(int type)
    {
      mType= type;
      return;
    }

    @Override
    public String toString()
    {
      String string;
      switch(mType)
      {
        case 0:
          string= "SINGLE_DAY";
          break;
        case 1:
          string= "ALL_DAYS";
          break;
        default:
          string= "Invalid type";
          break;
      }
      return string;
    }

    public int toInt()
    {
      return mType;
    }
    static public TodoViewType fromInt(int i)
    {
      TodoViewType type;
      switch(i)
      {
        case 0:
          type= SINGLE_DAY;
          break;
        case 1:
          type= ALL_DAYS;
          break;
        default:
          type= null;
          break;
      }

      return type;
    }

  }  ////END ENUM////




  /////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////END NNER TYPES///////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////




}      ////////////END CLASS///////////////

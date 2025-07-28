package ndb.types;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import ndb.db.NoteDataSource;
import ndb.util.DateUtil;
import ndb.util.Util;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 09/03/13
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class Note
{
  /**
   * SQL information for Notes table
   */


  public static final String[] ALL_COLUMNS =
                {
                     Note.ID, Note.TITLE, Note.CONTENT, Note.NBID, Note.DATE_MODIFIED, Note.DATE_CREATED,
                     Note.NOTE_TYPE, Note.TAGS, Note.DATE_DUE, Note.ROLLOVER_UNCOMPLETED_TODOS, Note.SPANS,
                     Note.COMPLETED, Note.DATE_COMPLETED,

                        Note.TODO_ORDINAL,
                        Note.PRIORITY,
                        Note.DUE_TIME,
                        Note.REPEAT_INTERVAL,

                        Note.REPEAT_FIELD, Note.REPEAT_VALUE, Note.INT3, Note.INT4, Note.INT5, Note.INT6, Note.INT7, Note.INT8,
                        Note.TEXT1, Note.TEXT2, Note.TEXT3, Note.TEXT4, Note.TEXT5, Note.TEXT6, Note.TEXT7, Note.TEXT8,
                };



  public static final String  TABLE =    "notes",

                              ID =        "_id",        //primary key
                              TITLE =     "title",       //title of note
                              CONTENT =    "content",    //content of note
                              NBID =       "nb_id",       //notebook id

                              DATE_MODIFIED =    "datemodified",
                              DATE_CREATED =     "datecreated",
                              CONTENT_PREVIEW =  "contentpreview",
                              NOTE_TYPE =        "notetype_id",


                              TAGS =             "tags",
                              SPANS= "SPANS",                       //stored as a JSON

                              //FOR _TODO LISTS
                              DATE_DUE= "DATEDUE",      // should be set to 0 to keep due date as current day
                                                        //todo merge with DUE_TIME. Use same setter for both
                              ROLLOVER_UNCOMPLETED_TODOS = "ROLLOVER",    //should todo_ always be shown on current day if not completed or stay for day created?



                              COMPLETED= "COMPLETED",
                              DATE_COMPLETED= "DATE_COMPLETED",
                              PRIORITY= "PRIORITY",          //used for sorting... especially todos

                              REPEAT_FIELD = "INT1",          //Matches up WITH field and value from android Calendar class
                              REPEAT_VALUE = "INT2",          //                          "


  /**
   *  DATE_DUE and DATE_STICKY are both needed for future due dates. When that day comes
   *  DATE_STICKY determines if the note stays on that day or continue to be shown on the current iterating date
   *


   */



  /**
   * Refactoring instructions:
   * Every time a new field is added
   * 1) NDBTableMaster - update onUpdate() method
   * 2) NDBTableMaster - update CREATE_TABLE string
   * 3) Note - update allNoteColumns
   * 4) Note - update fromCursor()
   * 5) Note - update toContentValues()
   * 6) Note - Update clone()
   *
   *   //NoteDatasource no longer needs to be updated
   */

  /**
   * Implemented -ONLY- in db but not used
   * todo: steps 4-6 still need to be completed
   */


  //todo make this a DECIMAL filed so can always put an ordinal between 2 others (eg 1 and 2 -> 2.5)
  TODO_ORDINAL= "TODO_ORDINAL",  //used for manual sorting of todos by user???  may be need to make it TODO_ORDINAL

          DUE_TIME= "DUE_TIME", //to do  sub-sorts
  /*
   */                       // COMPLETED_DATE= "COMPLETED_DATE", //<0 IF NOT COMPLETED. Used to differentiate between due_date and the date completed
  REPEAT_INTERVAL= "REPEAT_INTERVAL",   //0 for no repeat, other values for other repeat!

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









  /**
   * Ideas for fields
   *
   * SHOW_IN_TODO_NOTE_VIEW - since maybe only some notes should show up as todos
   *
   */


   static  public final long TEMPORARY_NOTE_ID= -10;      //Used to store a single temporary note
                                                            //the original guid is stored in Settings table TEMPORARY_NOTE_ORIGINAL_ID

  ////////////////////////////////////////////////////////////////////////////////



  //packing for intents
  //final public static String  OBJECT =      "note object";   // to send as parcelable
  final public static String GUID_EXTRA =      "note guid";

  //Sentinel values
  final public static long    NBGUID_EMPTY=-1;  //to see if nbGuid has been assigned yet








  /**
   *  Takes a Cursor object for 1 entry  and transforms it into a Note object
   *
   * @param cursor
   * @return
   */
  public static Note fromCursor(Cursor cursor)
  {
    Note note = new Note();
//    RepeatingDate repeatingDueDate;
    int     field,    //for repeatingDueDate
            value;    //        "

    note.setGuid(cursor.getLong(cursor.getColumnIndex(Note.ID)));
    note.setTitle(cursor.getString(cursor.getColumnIndex(Note.TITLE)));
    note.setContent(cursor.getString(cursor.getColumnIndex(Note.CONTENT)));
    note.setNbGuid(cursor.getLong(cursor.getColumnIndex(Note.NBID)));
    note.setDateModified(cursor.getLong(cursor.getColumnIndex(Note.DATE_MODIFIED)));
    note.setDateCreated(cursor.getLong(cursor.getColumnIndex(Note.DATE_CREATED)));


    note.setNoteType(cursor.getLong(cursor.getColumnIndex(Note.NOTE_TYPE)));
    note.setTags(cursor.getString(cursor.getColumnIndex(Note.TAGS)));


    note.setDateDue(cursor.getLong(cursor.getColumnIndex(Note.DATE_DUE)));
    note.setRolloverIfNotCompleted(Util.longToBoolean(cursor.getLong(cursor.getColumnIndex(Note.ROLLOVER_UNCOMPLETED_TODOS))));

    note.setSpansAsJson(cursor.getString(cursor.getColumnIndex(Note.SPANS)));
    note.setCompleted(Util.longToBoolean(cursor.getLong(cursor.getColumnIndex(Note.COMPLETED))));
    note.setDateCompleted(cursor.getLong(cursor.getColumnIndex(Note.DATE_COMPLETED)));
    note.setPriority(Priority.fromInt(  cursor.getInt(cursor.getColumnIndex(Note.PRIORITY))));

    /**
     * set repeating due date
     */
    field= cursor.getInt(cursor.getColumnIndex(Note.REPEAT_FIELD));
    value= cursor.getInt(cursor.getColumnIndex(Note.REPEAT_VALUE));
    note.setRepeatingDueDate(field, value);




    if (note.getSpansAsJson() != null && note.getSpansAsJson().equals("0"))          //a hack since database returns null as "0"
    {
      note.setSpansAsJson(null);
    }


    return note;
  }

  /**
   * Transforms a cursor into a list of notes
   * @param cursor
   * @return
   */
  public static List<Note> listFromCursor(Cursor cursor)
  {
    List<Note> notes;
    Note newNote;

    notes= new ArrayList<Note>();

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {

      newNote = Note.fromCursor(cursor);   //this should return a single noteBook entry
      notes.add(newNote);
      cursor.moveToNext();
    }
    return notes;
  }


  public static List<Note> setTransientShowDueDates(Cursor notesCursor, NoteDataSource.Key type)
  {
    return Note.setTransientShowDueDates(Note.listFromCursor(notesCursor), type);
  }

  public static List<Note> setTransientShowDueDates(List<Note> notes, NoteDataSource.Key type)
  {
    /**
     * Assumption all notes will have
     */

    if ( notes.size()>0 )
    {
      switch (type)
      {
        /**
         * Past and Future are processed same way
         */
        case PAST:
        case FUTURE:
          notes.get(0).showDueDate= true;
          for (int i=1; i<notes.size(); i++)
          {
            if (   DateUtil.getStartOfDay(notes.get(i - 1).getDateDue()).equals(DateUtil.getStartOfDay(notes.get(i).getDateDue()))    )
            { //assert dateDue of note and previous note are the same
              notes.get(i).showDueDate= false;
            }
            else
            { //assert dateDue is different than previous note's dateDue
              notes.get(i).showDueDate= true;
            }

          }
          break;

        case TODAY:
          notes.get(0).showDueDate= true;
          for (int i=1; i<notes.size(); i++)
          {
            notes.get(i).showDueDate= false;
          }
          break;

        default:
          /**
           * do nothing type not recognized?
           */
          break;


      }
    }
    return notes;
  }



  /////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////BEGIN OBJECT INSTANCE////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////
  private transient boolean showDueDate;

  //private transient Editable textAndSpans; //perhaps loading before needed will increase performance?

  private String title,
                 content,
                 tags,
                 spansAsJson;             //to save spans to db;



  private long  guid,
                nbGuid,
                noteType,
                dateCreated,
                dateModified,
                dateDue,
                dateCompleted;



  private int repeatingField,           //for repeatingDueDate. field and value are from android Calendar class
              repeatingValue;           //                               "

  private Priority priority;

  private boolean rolloverIfNotCompleted,
                  completed;              //for todo_ notes









  public Note()
  {
    nbGuid= Note.NBGUID_EMPTY;       //todo: is this realy needed?
    dateDue= -1;                     //to tell if note has valid due date or not
    dateCompleted= -1;
    repeatingField= -1;
    completed= false;
    priority= Priority.NORMAL; //to prevent null pointer exception
    /**
     * Note:
     *        note == 0 (default intialization)
     *        This important because we use it to determine if a new note needs to be created or an old one overwritten
     *
     */
    return;
  }



  //todo change this equals!!!!!!!!!
  @Override
  public boolean equals(Object aNote)
  {
    boolean result= false;
    Note note= (Note)aNote;

    if (       getTitle().equals(note.getTitle())
            && getContent().equals(note.getContent())
            && getTags().equals(note.getTags())//(  (tags== null && note.getTags() == null)  || (tags!=null && tags.equals(note.getTags()))   )  //since tags can be null
            && getSpansAsJson().equals(note.getSpansAsJson())


            && guid == note.getGuid()
            && nbGuid == note.getNbGuid()
            && noteType == note.getNoteType()
            && dateCreated == note.getDateCreated()
            && dateModified == note.getDateModified()
            && dateDue == note.getDateDue()
            && dateCompleted == note.getDateCompleted()

            && repeatingField == note.getRepeatingField()
            && repeatingValue == note.getRepeatingValue()


            && priority == note.getPriority()

            && rolloverIfNotCompleted == note.isRolloverIfNotCompleted()
            && completed == note.isCompleted()


            )   //todo tags????????

    {
      result= true;
    }

    return result;
  }


  /**
   * Compares tags in note object to tags.
   * It returns the values in tags not in the note object
   * @param tags
   * @return  list of added tags (can be an empty list)
   *
   *
   */
  public List<String> getNewTagsFrom(String tags)                //getTagsNotIn
  {
    List<String> innerTagList= Tag.stringToList(getTags());
    List<String> outerTagList= Tag.stringToList(tags);

    List<String> newTags= new ArrayList<String>();

    for (int i=0; i<outerTagList.size(); i++)
    {
      if (innerTagList.contains(outerTagList.get(i)) == false)  //assert tag is not in this note object
      {
        newTags.add(outerTagList.get(i));
      }
    }
               /*  ////////only makes it more complicated!!!
    if (newTags.size() == 0)
    {
      newTags= null;
    }           */

    return newTags;
  }







  /////////////////////////////////////////////////


  public Priority getPriority()
  {
    return priority;
  }

  public void setPriority(Priority priority)
  {
    this.priority = priority;
  }

  public long getNoteType()
  {
    return noteType;
  }

  public void setNoteType(long noteType)
  {
    this.noteType = noteType;
  }

  /////////////////////////////////////////////////
  public String getTags()
  {
    String theTags;


    if (tags == null)
    {
      return "";
    }
    else
    {
      return tags.trim();
    }
  }

  public List<String> getTagsAsList()
  {

    return Tag.stringToList(tags);
  }

  public void setTags(String tags)
  {
    //todo need to find better way to do this!!!!!!!!
    //actually seems to be working pretty bitching!! although... might be adding more whitespace every time saved...
    //so can just trim every time added, removed
    //this.tags = " "+tags+" "; //so pattern matching in sql works
    this.tags = tags;
    return;
  }
  /////////////////////////////////////////////////
  public long getDateCreated()
  {
    return dateCreated;
  }

  public void setDateCreated(long dateCreated)
  {
    this.dateCreated = dateCreated;
  }
  /////////////////////////////////////////////////
  public long getDateModified()
  {
    return dateModified;
  }

  public void setDateModified(long dateModified)
  {
    this.dateModified = dateModified;
  }

  /////////////////////////////////////////////////

  public long getDateCompleted()
  {
    return dateCompleted;
  }

  public void setDateCompleted(long dateCompleted)
  {
    this.dateCompleted = dateCompleted;
  }

  /////////////////////////////////////////////////
  public void setContent(String content)
  {
       this.content= content;
       return;
  }

  public String getContent()
  {
    if (content == null)
      return "";
    else
      return content;
  }
  /////////////////////////////////////////////////
  public void setTitle(String title)
  {
    this.title= title;
    return;
  }

  public String getTitle()
  {
    if (title==null)
    {
      return "";
    }
    else
      return title;
  }
  /////////////////////////////////////////////////


  /////////////////////////////////////////////////
  public boolean isCompleted()
  {
    return completed;
  }

  /**
   * If note has a repeatingDueDate then if the note is set to complete, the dueDate will be moved to next one
   * (and won't be set to completed)
   * @param completed
   */
  public void setCompleted(boolean completed)
  {


    if ( repeatingField == -1 || completed == false               || repeatingField == 0 ) //todo: repeatingField == 0 can removed once all get set to -1
    {
      this.completed = completed;
    }
    else  //assert: dueDate needs to be updated
    {
      dateDue= DateUtil.getNext(dateDue, repeatingField, repeatingValue);   //update dateDue to next interval
    }




    return;
  }



  public int getRepeatingField()
  {
    return repeatingField;
  }

  public int getRepeatingValue()
  {
    return repeatingValue;
  }

  /**
   * field and value are taken from android Calendar class
   * @param field
   * @param value
   */
  public void setRepeatingDueDate(int field, int value)
  {
    repeatingField= field;
    repeatingValue= value;
    return;
  }


  ///////////////////////////////////////////////////

  public long getGuid()
  {
    return guid;
  }

  //careful!!!!!!!! changing this will screw up db!!!
  public void setGuid(long guid)
  {
    this.guid= guid;
  }
  ////////////////////////////////////////////////////

  public long getNbGuid()
  {
    return nbGuid;
  }



  public void setNbGuid(long nbGuid)
  {
    this.nbGuid= nbGuid;
  }
  /////////////////////////////////////////////////////

  public long getDateDue()
  {
    return dateDue;
  }

  public void setDateDue(long dateDue)
  {
    this.dateDue = dateDue;
  }

  /////////////////////////////////////////////////////

  public boolean isRolloverIfNotCompleted()
  {
    return rolloverIfNotCompleted;
  }


  public void setRolloverIfNotCompleted(boolean rolloverIfNotCompleted)
  {
    this.rolloverIfNotCompleted = rolloverIfNotCompleted;
  }




  /////////////////////////////////////////////////////
  public String getSpansAsJson()
  {

    /**
     * A hack since null was giving problems with saveNote() in EditNoteActivity
     */
    if (spansAsJson == null)
      return "";
    else
      return spansAsJson;
  }

  public void setSpansAsJson(String spansAsJson)
  {
    this.spansAsJson = spansAsJson;
  }


  public boolean isShowDueDate()
  {
    return showDueDate;
  }

  /////////////////////////////////////////////////////
  public String toString()
  {
    return guid + "  " + title;

  }


  public static String getClassName()
  {
    return "notesdatabase.types.Note";
  }


  /**
   * This combines content and jsonSpans to return an Editable object. Changes to this Editable
   * WILL NOT affect the originating note.
   * @return
   */
  public Editable getEditableContent()
  {
      String text;
      String spansAsJson;
      List<SpanWrapper> spanWrappers = null;
      Editable textAndSpans;

      text= getContent();
      spansAsJson=getSpansAsJson();

      if (text == null)
      {
        text= "";
      }

      textAndSpans = new SpannableStringBuilder(text);

      if (spansAsJson != null)
      {
        spanWrappers= JsonAdapter.fromJson(spansAsJson);

        //convert SpanWrappers to spans for String
        for (int i = 0; i < spanWrappers.size(); i++)
        {
          textAndSpans.setSpan(spanWrappers.get(i).span, spanWrappers.get(i).start, spanWrappers.get(i).end, spanWrappers.get(i).flags);      //todo MUST DO FLAGS PROPERLY!!!!!!!
        }

      }

      return textAndSpans;
  }



  public ContentValues toContentValues()
  {
    ContentValues contentValues;
    //todo: this doesn't include GUID. I guess its a good thing?
    contentValues = new ContentValues();
    contentValues.put(Note.TITLE, getTitle());
    contentValues.put(Note.CONTENT, getContent());
    contentValues.put(Note.NBID, getNbGuid());
    contentValues.put(Note.TAGS, getTags());
    contentValues.put(Note.DATE_CREATED, getDateCreated());
    contentValues.put(Note.DATE_MODIFIED, getDateModified());

    contentValues.put(Note.DATE_DUE, getDateDue());
    contentValues.put(Note.ROLLOVER_UNCOMPLETED_TODOS, Util.booleanToLong(isRolloverIfNotCompleted()));
    contentValues.put(Note.SPANS, getSpansAsJson());
    contentValues.put(Note.COMPLETED, Util.booleanToLong(isCompleted()));
    contentValues.put(Note.DATE_COMPLETED, getDateCompleted());
    contentValues.put(Note.PRIORITY, priority.toInt());

    /**
     * repeating due date
     */
    contentValues.put(Note.REPEAT_FIELD, repeatingField);
    contentValues.put(Note.REPEAT_VALUE, repeatingValue);



    return contentValues;
  }


  /**
   * Clones and returns a copy of the note
   * @return
   */
  @Override
  public Note clone()
  {
    Note note;

    note= new Note();

    note.setTitle(title);         //should be ok since strings are immutible
    note.setContent(content);
    note.setTags(tags);
    note.setSpansAsJson(spansAsJson);


    note.setGuid(guid);
    note.setNbGuid(nbGuid);
    note.setNoteType(noteType);
    note.setDateCreated(dateCreated);
    note.setDateModified(dateModified);
    note.setDateDue(dateDue);
    note.setDateCompleted(dateCompleted);

    note.setRepeatingDueDate(repeatingField, repeatingValue);

    note.setPriority(priority);

    note.setRolloverIfNotCompleted(rolloverIfNotCompleted);
    note.setCompleted(completed);



    return note;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////ENUMS AND INNER CLASSES//////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//  /**
//   * Used for repeating due dates
//   * field and value come from android Calendar class
//   */
//  public static class RepeatingDate
//  {
//
//    public int field;
//    public int value;
//
//
//    public RepeatingDate()
//    {
//      return;
//    }
//
//  }

  /**
   * Priority - currently used for todo_ sorts
   *
   * note - Spinner uses integer value to know Priority value so negative values won't work
   */
  public enum Priority
  {
    /**
     * 0 - low priority
     * 4 - high priority
     *
     * preface priorites _1, _2, etc.
     */
    LOWEST(0),
    LOW(1),
    NORMAL (2),
    IMPORTANT(3),
    CRITICAL(4);



    private int mType;

    private Priority(int type)
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
          string= "Lowest";
          break;
        case 1:
          string= "Low";
          break;

        case 2:
          string= "Normal";
          break;
        case 3:
          string= "Important";
          break;
        case 4:
          string= "Critical";
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

    static public Priority fromInt(int i)
    {
      Priority type;
      switch(i)
      {
        case 0:
          type= LOWEST;
          break;
        case 1:
          type= LOW;
          break;

        case 2:
          type= NORMAL;
          break;
        case 3:
          type= IMPORTANT;
          break;
        case 4:
          type= CRITICAL;
          break;

        default:
           type= NORMAL;  //if int is unrecognized just make priority NORMAL
          break;
      }
      return type;
    }


    static public Priority fromLong(long i)
    {
      return fromInt((int)i);
    }

  }  ////END ENUM////

  /**
   * The purpose of this enum is to make the field and view from android Calendar
   * have String correspondance
   */
  public enum RepeatingDates
  {
    NONE(-1),
    SUNDAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7);

    private int mType;

    private RepeatingDates(int type)
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
        case -1:
          string= "None";
          break;
        case 1:
          string= "Sunday";
          break;

        case 2:
          string= "Monday";
          break;
        case 3:
          string= "Tuesday";
          break;
        case 4:
          string= "Wednesday";
          break;
        case 5:
          string= "Thursday";
          break;
        case 6:
          string= "Friday";
          break;
        case 7:
          string= "Saturday";
          break;

        default:
          string= "None";   //set all invalid values to NONE
          break;
      }
      return string;
    }


    static public RepeatingDates fromInt(int i)
    {
      RepeatingDates type;
      switch(i)
      {
        case -1:
          type= NONE;
          break;
        case 1:
          type= SUNDAY;
          break;

        case 2:
          type= MONDAY;
          break;
        case 3:
          type= THURSDAY;
          break;
        case 4:
          type= WEDNESDAY;
          break;
        case 5:
          type= THURSDAY;
          break;
        case 6:
          type= FRIDAY;
          break;
        case 7:
          type= SATURDAY;
          break;

        default:
          type= NONE;  //if int is unrecognized just make it NONE
          break;
      }
      return type;
    }




  }             ////END ENUM////




} //////////END CLASS NOTE/////////////









































































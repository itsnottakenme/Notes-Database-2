package ndb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ndb.types.Note;
import ndb.types.Notebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: ian
* Date: 03/04/13
* Time: 9:24 PM
*
 *
 * How to use
 * 1) call constructor
 * 2) setImportClumns()
 *
*/
public class ImportDbHelper
{
  // Databases fields
  private SQLiteDatabase importDb;

  private NoteDataSource  tempDb,                 //temp db for user to see if import ok
                          nDb;                    //user's DB to import tempDb record if successful import
                                                  //todo: rename exportDatabse so can make a GENERAL PROGRAM!!!!!!!!

  private NDBTableMaster dbHelper;      //todo: wtf is this for?????????????

  private NotebookTable mImportNb,
                        exportNotebookTable;    //these apply to tempDb and nDb since both are the same

  private NoteTable importNoteTable,
                    exportNoteTable;    //these apply to tempDb and nDb since both are the same



  //TODO *****************WRAP THE ENTIRE CODE INTO A TRANSACTION*****************



  public ImportDbHelper(SQLiteDatabase importDb, NoteDataSource tempDb, NoteDataSource exportDb)
  {
    this.importDb= importDb;        //todo are db's initialized before call? probably...
    this.tempDb= tempDb;
    this.nDb= exportDb;

    return;
  }


  public void setImportTables(NotebookTable notebookTable, NoteTable noteTable)
  {
    mImportNb = notebookTable;
    importNoteTable = noteTable;

    return;
  }


  //////////////////////////////////////////////////////////////////////////////////////////
  /////////todo THIS NEEDS GOOD WAY TO GET inputFields for NoteTable and NotebookTable//////
  //////////////////////////////////////////////////////////////////////////////////////////


  /**
   * <pre>
   *   {@code
   * Steps
   * 1) Get all nbids from importDB Notebooks table
   * 2) Foreach of these notebooks
   *    a)Retrieve the notebook row nbid came from
   *    b)add this notebook row to tempDB but leave out nbid so can generates and returns a new_nbid
   *    c) Wtih  new_nbid
   *          i)get list of of notes from tempDb notes table with nbid
   *          i)add each of these to tempDb using
   *    }
   * </pre>
   *
   */
  public void importDbToTempDb()
  {
    //initialization - setup noteTable, noteBookTable. create tempDB, etc. might have to make NDS not a singleton...
    List<Long> nbIdList;     //list of nbid's of notebooks from importDb
    //long nbRowId;            //the nb row id from the notebooks to be imported

    Cursor nbRowCursor= null;


    List<String> nbColumns;
    Notebook createdNotebook;

    List<Note> noteList;

    nbColumns = mImportNb.getImportColumns();

    nbIdList= getAllNotebookIdsFromImportDb();

    for (Long aNbIdList : nbIdList)
    {
      //retrieve notebook row from importDb
      nbRowCursor = getNotebookFromImportDb(aNbIdList);


      nbRowCursor.moveToFirst();
      createdNotebook = cursorToNotebook(nbRowCursor, nbColumns);
      nbRowCursor.moveToNext();

      createdNotebook = tempDb.createNotebook(createdNotebook);

      //get all notes that correspond to this notebook and then import them into tempDb
      noteList = getNotesFromImportingNotebook(aNbIdList);

      for (int j = 0; j < noteList.size(); j++)
      {
        noteList.get(j).setNbGuid(createdNotebook.getGuid());
        fixTags(noteList.get(j));
        tempDb.createNote(noteList.get(j));
      }


    }

    if (nbRowCursor != null)
    {
      nbRowCursor.close();
    }
    return;
  }


  private List<Note> getNotesFromImportingNotebook(long notebookId)
  {
    Cursor cursor;
    List<Note> notes;
    Note newNote;

    notes= new ArrayList<Note>();


    String[] columns=  importNoteTable.getImportColumns().toArray(new String[importNoteTable.getImportColumns().size()]);


    cursor= importDb.query(importNoteTable.table, columns,
                           importNoteTable.nbidCol+ "=" +notebookId, null, null, null, null);





    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {

      newNote = cursorToNote(cursor, importNoteTable.getImportColumns());   //this should return a single noteBook entry
      notes.add(newNote);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return notes;
  }
  //todo if notebook name is already found in database just add notes to already created db
  public void addTempDbToNdb()
  {
    List<Notebook> notebooks;
    List<Note> notes;

    long maxNbGuid,       //the biggest guid in ndb. used so don't have primary key clashes
         maxNoteGuid;

    Notebook tempNotebook;
    String tempTitle;



    notebooks= tempDb.getAllNotebooks();
    notes= tempDb.getAllNotes(null, null);

    maxNbGuid= nDb.getLargestNotebookGuid();
    maxNoteGuid= nDb.getLargestNoteGuid();                //what happens if there are no notes? it returns 0


    //todo notedatasource needs to implement transaction functions to merge this into a transaction

    //check to see notebooks already exist. If so then update nbid's in the Notes and Notebooks

      for(int i=0; i<notebooks.size(); i++)
      {
        if( nDb.getNotebook(notebooks.get(i).getTitle()) == null ) //assert notebook title doesn't exist so create new notebook
        {
          notebooks.get(i).setGuid( notebooks.get(i).getGuid()+maxNbGuid );
          nDb.createNotebook(notebooks.get(i));
        }
        else       //assert notebook title already exists in nDb
        {
                 // do nothing... already exists so....
        }

                // todo: make so that if Notebook title already exists then related notes just added to that notebook
      }                                               // todo make function changeNoteGuid(long original, long new) that will change nbid in both notes and notebooks
      for(int i=0; i<notes.size(); i++)
      {

        tempTitle= tempDb.getNotebook(notes.get(i).getNbGuid()).getTitle();
        tempNotebook= nDb.getNotebook(tempTitle);

        if (tempNotebook == null) //assert the note will go into a newly created notebook
        {
           notes.get(i).setNbGuid( notes.get(i).getNbGuid()+maxNbGuid );   //update nbguid
        }
        else  //assert notebook will be added to a previously created notebook in nDb
        {
          notes.get(i).setNbGuid( tempNotebook.getGuid() );   //update nbguid
        }


        notes.get(i).setGuid(notes.get(i).getGuid()+maxNoteGuid);       //update _id
        nDb.createNote(notes.get(i));
      }




    return;
  }


  //todo this is for AWESOME NOTE only
  private void fixTags(Note note)
  {
    String tags;

    tags= note.getTags();

    List<String> tagList= new ArrayList<String>();

    if (tags!=null)
    {
      tagList.addAll(Arrays.asList(tags.split("\\|")));
    }
    //else tagList is left empty

    while( tagList.remove("") );  //removes  all the empty strings from tagList

    tags="";
    for (int i=0; i<tagList.size(); i++ )
    {
      if ( i%2 == 1 )
      {

        tags= tags+tagList.get(i)+" ";
      }
    }

    note.setTags(tags);

    return;
  }

  private Note cursorToNote(Cursor cursor, List<String> importColumns)
  {
    Note note = new Note();
    List<String> columns;
    int index;                //index of an item in a list


    columns= importNoteTable.getImportColumns();



    index= columns.indexOf(importNoteTable.titleCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setTitle(cursor.getString(index));
    }

    index= columns.indexOf(importNoteTable.contentCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setContent(cursor.getString(index));         //TODO ONLY NEED EDIT THIS TO SAVE RICH NOTE CONTENT FIELD!!!
     // note.setRichContent(cursor.getString(index));
    }
    index= columns.indexOf(importNoteTable.tagsCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setTags(cursor.getString(index));
    }



    index= columns.indexOf(importNoteTable.idCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setGuid(cursor.getLong(index));
    }

    index= columns.indexOf(importNoteTable.nbidCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setNbGuid(cursor.getLong(index));
    }
    index= columns.indexOf(importNoteTable.noteType);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setNoteType(cursor.getLong(index));
    }
    index= columns.indexOf(importNoteTable.dateCreatedCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setDateCreated(cursor.getLong(index));
    }
    index= columns.indexOf(importNoteTable.dateModifiedCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setDateModified(cursor.getLong(index));
    }
    index= columns.indexOf(importNoteTable.dateDueCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      note.setDateDue(cursor.getLong(index));
    }



    return note;
  }





  private Notebook cursorToNotebook(Cursor cursor, List<String> importColumns)
  {
    Notebook notebook = new Notebook();
    List<String> columns;
    int index;                //index of an item in a list


    columns= mImportNb.getImportColumns();


    index= columns.indexOf(mImportNb.titleCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      notebook.setTitle(cursor.getString(index));
    }

    index= columns.indexOf(mImportNb.idCol);
    if ( index > -1 )
    {
      notebook.setGuid(cursor.getLong(index));
    }

    index= columns.indexOf(mImportNb.ordinalCol);
    if ( index > -1 )
    {
      notebook.setOrdinal(cursor.getLong(index));
    }

    index= columns.indexOf(mImportNb.dateCreatedCol);
    if ( index > -1 )
    {
      notebook.setDateCreated(cursor.getLong(index));
    }

    index= columns.indexOf(mImportNb.dateModifiedCol);
    if ( index > -1 )
    {
      notebook.setDateModified(cursor.getLong(index));
    }

    index= columns.indexOf(mImportNb.listTypeCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      notebook.setViewType( Notebook.ViewType.valueOf(cursor.getString(index)) );
    }

    index= columns.indexOf(mImportNb.noteSortCol);
    if ( index > -1 )    //assert title column is to be imported
    {
      notebook.setNoteSort(cursor.getString(index));
    }

    return notebook;
  }






  private List<Long> getAllNotebookIdsFromImportDb()
  {
    List<Long> nbidList= new ArrayList();
    Cursor cursor;

    String query= "Select " + mImportNb.idCol + " from " + mImportNb.table;

    cursor= importDb.rawQuery(query, null);


    cursor.moveToFirst();
    while(!cursor.isAfterLast())
    {
      nbidList.add(cursor.getLong(0));
      cursor.moveToNext();
    }

    return nbidList;
  }



  private Cursor getNotebookFromImportDb(long notebookId)
  {
    Cursor cursor;
    List<String> columns;

    //get all selected inputFields for the given notebook
    String  queryBase= "select " ,
            queryEnd= " from " +mImportNb.table
                    + " where " +mImportNb.idCol + "=" +notebookId;



    columns= mImportNb.getImportColumns();    //get list of columns we want imported


    if (columns != null && columns.size() > 0)          //todo could have skipped this by using SQLiteDatabase convenience method lol
    {
      for(int i=0; i<columns.size(); i++) //assert inputFields exist
      {
        queryBase= queryBase+columns.get(i);
        if (columns.size()-1 == i)  //assert this is last time loop will execute
        {
          queryBase= queryBase+" ";
        }
        else        //assert more inputFields still need to be added to query
        {
          queryBase= queryBase+", ";
        }
      }
      queryBase= queryBase+queryEnd;
    }

    cursor= importDb.rawQuery(queryBase, null);
    return cursor;

  }






  //////////////VERY IMPORTASNT/////////////////////////////////////////
  /**
   * turns the current row of cursor into ContentValues object
   * makes tuples ( inputFields(i), cursor(i) )
   *
   * */
  private ContentValues cursorRowToContentValues(List<String> inputFields, Cursor cursor)     //todo must UPDATE on field change
  {
    ContentValues nbRow;
    nbRow= new ContentValues();

    for (int i=0; i < inputFields.size(); i++) //pair up inputFields with corresponding cursor field entry
    {
      nbRow.put(inputFields.get(i), cursor.getString(i));  //todo make sure ordinal stays numeric and sorts properly
    }


    return nbRow;
  } /////////////////////////////////////////////////////////////////////


  /**
   * //todo temporary hardcoded method
   */
  public static void importAwesomeNoteDb(Context context, String importDBPath, String tempDbFilename, String ndbFilename)
  {
    ImportDbHelper importDbHelper;

    SQLiteDatabase importDb;
    NoteDataSource tempDb,
            nDb;



    NotebookTable importNotebookTable;
    NoteTable importNoteTable;

    /**
     * set up import notebook table cols
     */

    importNotebookTable= new NotebookTable();
    importNotebookTable.table= "notefolder";
    importNotebookTable.titleCol= "title";
    importNotebookTable.idCol= "idx";
    importNotebookTable.ordinalCol= "listorder";
    importNotebookTable.dateModifiedCol= "regdate";
    importNotebookTable.setColumnsToImport( Arrays.asList("title", "listorder", "regdate") );


    /**
     * set up import note table cols
     */
    importNoteTable= new NoteTable();
    importNoteTable.table="note";

    importNoteTable.idCol="idx";
    importNoteTable.titleCol="title";
    importNoteTable.contentCol="text";
    importNoteTable.tagsCol="tagids";         //todo this col needs special treatment!!!!!!!!
    importNoteTable.dateCreatedCol ="createdate";
    importNoteTable.dateModifiedCol ="regdate";
    importNoteTable.dateDueCol="duedate";
    importNoteTable.nbidCol= "folderidx";

    importNoteTable.richContentCol="text";      //todo has same input as normal content

    importNoteTable.setColumnsToImport( Arrays.asList("title", "text", "tagids", "createdate", "regdate", "duedate" ));





    //create importDb, tempDb and nDb objects
    context.deleteDatabase(tempDbFilename);
    tempDb= new NoteDataSource(context, tempDbFilename);  //todo: careful if eithe rof these lines is wrong
    nDb= new NoteDataSource(context, ndbFilename);                  //     then db becomes fucked!!!

    importDb= SQLiteDatabase.openDatabase(importDBPath, null,SQLiteDatabase.OPEN_READONLY);

    importDbHelper= new ImportDbHelper(importDb, tempDb, nDb);


    importDbHelper.setImportTables(importNotebookTable, importNoteTable);

    tempDb.open();
    nDb.open();
    importDbHelper.importDbToTempDb();


    importDbHelper.addTempDbToNdb();



    nDb.close();
    tempDb.close();






    return;
  }






}     /////////////////////////end class/////////////////////////////////
//////////////////////////////////////////////////////////////////////////////















/////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////junk code/////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////end junk code/////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////



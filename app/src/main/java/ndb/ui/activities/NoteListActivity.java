package ndb.ui.activities;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.view.*;
import android.widget.*;

import android.os.Bundle;
import ndb.db.NDBTableMaster;
import ndb.db.NoteDataSource;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.NdbIntent;
import ndb.types.Note;
import ndb.types.Sort;
import ndb.types.Notebook;
import ndb.ui.adapters.NoteAdapter;
import ndb.ui.bottombar.BottomBar;
import ndb.ui.bottombar.NoteListBottomBar;
import ndb.ui.views.SearchBox;


import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 06/03/13
 * Time: 8:55 PM
 * To change this template use File | Settings | File Templates.
 */





public class NoteListActivity extends AbstractNoteListActivity
{

  /**
   * Parcelables for reloading Activity state
   */
  private static final String LIST_STATE = "listState";
  private Parcelable mListState = null;


  /**
   * Context menu
   */
  final int CONTEXT_MENU_DELETE= 0;

  /**
   * Dialogs
   */
  final int DIALOG_CONFIRM_DELETE= 10;


  /**
   * Views
   */
  private ActionBar mActionBar;
  private ListView mListView;
  private SearchBox sbSearchBox;
  private BottomBar bbNoteList;   //todo: not appearing or hidden...why?????

  /**
   * View accessories
   */
  private SearchListener mSearchBoxListener;
  private NoteAdapter mAdapter;

  /**
   * Others
   */
  private List<Note> mNoteList;
  private Notebook mNotebook;
  private NoteDataSource mDatasource;
  private String mDbFilename;


  /**
   * Bundle keys
   */
  private final static String ROW_ID= "ROW_ID";     ///used to pass rowId to Dialog



  public void onCreate(Bundle savedInstanceState)
  {
    Intent intent;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.note_list_activity);


    mListView = (ListView)findViewById(R.id.notelist_listview);
    sbSearchBox= (SearchBox)findViewById(R.id.notelist_searchbox);
    bbNoteList = (NoteListBottomBar)findViewById(R.id.notelist_bottombar);

    mListView.setItemsCanFocus(true);
    mListView.setFocusableInTouchMode(true);


    intent= getIntent();

    /**
     * Set up db
     */
    mDbFilename = getIntent().getStringExtra(NdbIntent.DATABASE_NAME);
    if (mDbFilename == null)
    {
      mDbFilename = NDBTableMaster.NOTES_DB;
    }

    mDatasource = new NoteDataSource(this, mDbFilename);
    mDatasource.open(); //opens DB
    mNotebook= mDatasource.getNotebook(intent.getLongExtra(Notebook.GUID_EXTRA, 45874837));


    /**
     * Set up ActionBar title
     * Deal with Notebook.ALL case
     */
    mActionBar = getActionBar();
    if (mNotebook == null)
    {
      mNotebook= new Notebook();
      mNotebook.setGuid(Notebook.ALL);

    }
    else
    {
      mActionBar.setTitle(mNotebook.getTitle());
    }
    mActionBar.show();


    updateUi();
    registerForContextMenu(mListView);
    setupListeners();



    return;
  }


  @Override
  protected void onResume()
  {
    super.onResume();
    mDatasource.open();

    updateUi();   //todo this happens after  onActivityResult so overwrites it changes...

    //replaces updateUi. note: activity on result never called...
    search(mSearchBoxListener.mSearchString, mNotebook.getGuid(), mSearchBoxListener.mTag, mNotebook.getNoteSort()/*null*/, mNotebook.getAscdsc()/*null*/ );
    //todo update to include mNotebook.getNoteSort(), mNotebook.getAscDsc()


    if (mListState != null)
    {
      mListView.onRestoreInstanceState(mListState);
    }
    mListState = null;

    return;
  }



  @Override
  public void onPause()
  {
    super.onPause();
    mDatasource.close();


    return;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);    //To change body of overridden methods use File | Settings | File Templates.
    mListState = mListView.onSaveInstanceState();
    outState.putParcelable(LIST_STATE, mListState);
    return;
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    mListState = savedInstanceState.getParcelable(LIST_STATE);
    return;
  }

  private void setupListeners()
  {
    /**
     * ListView item click listener - Launch EditNoteActivity
     */
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
          {



            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
              Intent intent;
              Note note;


              //Toast.makeText(getApplicationContext(), "View note", Toast.LENGTH_SHORT).show();
              intent = new Intent(getApplicationContext(), EditNoteActivity.class);

              //get selected item from listView and pack into intent
              note = (Note) mListView.getItemAtPosition(position);



              //assert: nbGuid exists in note
              intent.putExtra(Note.GUID_EXTRA, note.getGuid());
              intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.EDIT);
              intent.putExtra(NdbIntent.VIEW_TYPE, NdbIntent.NOTE_VIEW);
              intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);
              startActivity(intent);
              return;

            }
          });


    /**
     * SearchListener is an inner class
     */
    mSearchBoxListener= new SearchListener();
    sbSearchBox.setListener(mSearchBoxListener);


    /**
     * BottomBar Listener
     */
    bbNoteList.setOnClickListener(new BottomBar.OnClickListener()
              {

                @Override
                public void onClick(BottomBar bottomBar)
                {

                  Intent intent;
                  //handle all sort cases
                  if (bottomBar.getActionType() == BottomBar.ACTION_SORT)
                  {
                    switch (bottomBar.getActionId())
                    {

                      case BottomBar.SORT_TITLE_ASCENDING:
                        mNotebook.setNoteSort(Note.TITLE);
                        mNotebook.setAscdsc(Sort.ASCENDING);
                        break;

                      case BottomBar.SORT_TITLE_DESCENDING:
                        mNotebook.setNoteSort(Note.TITLE);
                        mNotebook.setAscdsc(Sort.DESCENDING);
                        break;


                      case BottomBar.SORT_DATE_MODIFIED:
                        mNotebook.setNoteSort(Note.DATE_MODIFIED);
                        mNotebook.setAscdsc(Sort.DESCENDING);
                        mAdapter.setShowCreatedDate(false);

                        break;

                      case BottomBar.SORT_DATE_CREATED:
                        mNotebook.setNoteSort(Note.DATE_CREATED);
                        mNotebook.setAscdsc(Sort.DESCENDING);
                        mAdapter.setShowCreatedDate(true);

                        break;

                      //todo field not implemented yet
                      //                  case BottomBar.SORT_DATE_DUE:
                      //                    mNotebook.setNoteSort(Note.DATE_DUE);
                      //                    mNotebook.setAscdsc(Sort.DESCENDING);
                      //                    break;

                      //TODO FILL IN REST OF CASES HERE!!!!!!!!!!!!!

                      default:

                        break;


                    }

                    if (mNotebook.getGuid() != Notebook.ALL)          //todo this line is a bit hacky...
                    {
                      mDatasource.updateNotebook(mNotebook);
                    }
                    search(mSearchBoxListener.getSearchString(), mNotebook.getGuid(),
                            mSearchBoxListener.getTag(), mNotebook.getNoteSort(), mNotebook.getAscdsc());
                  }   // //END SORT CASES///
                  else if (bottomBar.getActionId() == BottomBar.LAUNCH_EDIT_NOTEBOOK_ACTIVITY)
                  {
                    intent = new Intent(getApplicationContext(), EditNotebookActivity.class);
                    intent.putExtra(Notebook.GUID_EXTRA, mNotebook.getGuid());
                    intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.EDIT_NOTEBOOK);
                    intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);
                    startActivityForResult(intent, 1);

                  }
                }


              });



    return;
  }

  /**
   * Loads notes in ListView
   * Gets all the notes for  given notebook.
   * It also sets up the tag bubbles for SearchBox
   *
   * todo: This method CLASHES with search(). Find a way to unify the 2
   */
  protected void updateUi()
  {

    /**
     * Update notes in ListView
     */
    if ( mNotebook.getGuid() == Notebook.ALL )
    {
      mActionBar.setTitle("All Notes");
      mNoteList= mDatasource.getAllNotes(Note.DATE_MODIFIED, Sort.DESCENDING);           //todo: CAN I GET THE DEFAULT SORT FROM SOMEWHERE ELSE??????


      sbSearchBox.show();
    }
    else  //just load a single notebook
    {
      mNoteList= mDatasource.getNotesFromNotebook(mNotebook.getGuid());
    }
    // Use the SimpleCursorAdapter to show the
    // elements in a ListView

    mAdapter = new NoteAdapter((Context)this, R.layout.note_item, (ArrayList)mNoteList);
    mListView.setAdapter(mAdapter);


    /**
     * Update SearchBox
     */
    if ( mNotebook.getGuid() == Notebook.ALL )
    {
      sbSearchBox.setSearchParameters(mDatasource.getAllTags());
    }
    else  //just load a single notebook
    {
      sbSearchBox.setSearchParameters(mDatasource.getTagsFromNotebook(mNotebook.getGuid()));
    }

    if (mNotebook.getHeaderColor() != 0)
    {
      mActionBar.setBackgroundDrawable(new ColorDrawable(mNotebook.getHeaderColor()));
      bbNoteList.setBackgroundColor(mNotebook.getHeaderColor());
    }


    return;
  }


  /**
   * TODO: This CLASHES with onResume(). fIND A WAY TO UNIFY THEM
   *
   * @param requestCode
   * @param resultCode
   * @param data
   */

  @Override   //todo this function is never called!!!! sincce viewnote activity doesn't return result
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    //probably need to make case statements since this will be called in diufferent contexts
//    Note newNote= new Note();
//
   super.onActivityResult(requestCode, resultCode, data);



    if (resultCode == RESULT_OK)
    {        //reload notelist. assert note has been added/changed

       //  updateUi();    //todo doesn't redo search
       mDatasource.open();
       search(mSearchBoxListener.mSearchString, mNotebook.getGuid(), mSearchBoxListener.mTag, mNotebook.getNoteSort()/*null*/, mNotebook.getAscdsc()/*null*/ );
                                                          //todo update to mNotebook.getNoteSort(), mNotebook.getAscDsc()

    }
    else if (requestCode == RESULT_CANCELED)
    {
      //do nothing
    }

    return;
  }


  @Override     //this should be in all activities for ActionBar i believe
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // use an inflater to populate the ActionBar with items

    MenuInflater inflater = getMenuInflater();



    inflater.inflate(R.menu.note_list_menu, menu);

    return true;

  }



  @Override                //listener for ActionBar...
  public boolean onOptionsItemSelected(MenuItem item)
  {
    Intent intent;

    switch (item.getItemId())
    {


      case R.id.add_option:
        Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT).show();
        intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.CREATE);
        intent.putExtra(NdbIntent.VIEW_TYPE, NdbIntent.NOTE_VIEW);
        intent.putExtra(Notebook.GUID_EXTRA, mNotebook.getGuid());
        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);
        startActivityForResult(intent,1);
        break;

      case R.id.refresh_option:
        Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT).show();
        break;

      case R.id.search_option:
        Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
        sbSearchBox.toggleVisibility();
        break;





      default:
        Toast.makeText(this, "Something pressed! in menu handler", Toast.LENGTH_SHORT).show();

        break;
    }

    return true;
  }




  @Override         //Creates context menu
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
  {

    if (v.getId()==this.mListView.getId())
    {
      AdapterView.AdapterContextMenuInfo info;

      info = (AdapterView.AdapterContextMenuInfo)menuInfo;


      menu.setHeaderTitle("Note");

      menu.add(Menu.NONE, CONTEXT_MENU_DELETE, 0, "delete");
      //menu.add(Menu.NONE, CONTEXT_MENU_EDIT, 1, "edit");
    }

    return;
  }


  @Override
  public boolean onContextItemSelected(MenuItem item)
  {

    AdapterView.AdapterContextMenuInfo info;
    int rowId;
    Note note;
    boolean result;
    Bundle argsBundle;

    info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    rowId = (int) mListView.getAdapter().getItemId(info.position);/*what item was selected is ListView*/


    switch (item.getItemId())      //ind out how getItemId() found. it has NOTHING to do with
    {



      case CONTEXT_MENU_DELETE:
        argsBundle= new Bundle();
        argsBundle.putInt(ROW_ID, rowId);
        showDialog(DIALOG_CONFIRM_DELETE, argsBundle);
        return(true);
      //break;




      //      case CONTEXT_MENU_EDIT:
      //        //do smth else)
      //        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  /**
   * set tag=null if not searching with tag
   *
   * 3 casesa
   * 1) searchTerm only
   * 2) tag only
   * 3) searchTerm and tag
   * @param searchTerm
   * @param tag
   */
  /**
   *
   * @param searchTerm
   * @param notebookId - negative number for no notebookId
   * @param tag
   */

  //todo this is always called on initial load (before use) why?

  /**
   * Updates mNoteList and notifies mAdapter that the data set has changed
   *
   * @param searchTerm
   * @param notebookId
   * @param tag
   * @param sortColumn       - is AN ACTUAL column name from database
   * @param ascDsc
   */
  private void search(String searchTerm, long notebookId, String tag, String sortColumn, String ascDsc)
  {

    if (sortColumn == null)
    {
      sortColumn = Note.DATE_MODIFIED;
    }
    if (ascDsc == null)
    {
      ascDsc= Sort.DESCENDING;
    }
    mAdapter.clear();


    if ( mNotebook.getGuid() == Notebook.ALL )   //todo: this is not needed since Notebook.ALL is defined as -1
    {
      mNoteList.addAll(mDatasource.getNotes(searchTerm, -1, tag, sortColumn, ascDsc));
    }
    else  //just load a single notebook
    {
      mNoteList.addAll(mDatasource.getNotes(searchTerm, notebookId, tag, sortColumn, ascDsc));
    }

      mAdapter.notifyDataSetChanged();

    return;
  }




  @Override
  protected Dialog onCreateDialog(int id,  final Bundle args)
  {

    switch (id)
    {
      case DIALOG_CONFIRM_DELETE:
        // Create out AlterDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete selected note?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            int rowId;
            Note note;
            Boolean result;

            rowId= args.getInt(ROW_ID);
            note= (Note) mListView.getAdapter().getItem(rowId);
            result= mDatasource.deleteNote(note);

            if (result == true)            //record was deleted
            {
              mAdapter.remove(note);
            }

            return;
          }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        break;

    }
    return super.onCreateDialog(id);
  }






  /**
   * Class SearchListener
   */
  class SearchListener  implements SearchBox.SearchBoxListener
  {
    private String  mTag= null,
                    mSearchString= null;
    @Override
    public void onTagChange(String tag)
    {
      if (mSearchString == null || mSearchString.equals(""))    //assert only search by tag
      {
        search(null, mNotebook.getGuid(), tag, mNotebook.getNoteSort(), mNotebook.getAscdsc());     //todo update to mNotebook.getNoteSort(), mNotebook.getAscDsc()
      }
      else  //assert must search by both tag and searchString
      {
        search(mSearchString, mNotebook.getGuid(), tag, mNotebook.getNoteSort(), mNotebook.getAscdsc());
                                                    //todo update to mNotebook.getNoteSort(), mNotebook.getAscDsc() already did!! :)
      }
      mTag= tag;
      return;
    }

    @Override
    public void onSearchStringChange(String searchString)
    {
      if (mTag == null || mTag.equals(""))      ///assert only search by searchString
      {
        search(searchString, mNotebook.getGuid(), null, mNotebook.getNoteSort(), mNotebook.getAscdsc());   //todo update to mNotebook.getNoteSort(), mNotebook.getAscDsc()
      }
      else    //assert search by searchString and tag
      {
        search(searchString, mNotebook.getGuid(), mTag, mNotebook.getNoteSort(), mNotebook.getAscdsc());   //todo update to mNotebook.getNoteSort(), mNotebook.getAscDsc()
      }
      mSearchString= searchString;
    }

    public String getTag()
    {
      return mTag;
    }
    public String getSearchString()
    {
      return mSearchString;
    }
  }  ////END INNER CLASS////




}     //////////////////////END CLASS/////////////////////////


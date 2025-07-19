package ndb.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.mobeta.android.dslv.DragSortListView;
import ndb.db.NDBTableMaster;
import ndb.db.NoteDataSource;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.NdbIntent;
import ndb.types.Note;
import ndb.types.Notebook;
import ndb.ui.bottombar.BottomBar;
import ndb.ui.adapters.NotebookAdapter;
import ndb.ui.bottombar.NotebookListBottomBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 11/03/13
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotebookListActivity extends Activity
{

  /**
   * Context menu items
   */
  final int CONTEXT_MENU_DELETE=  0,
            CONTEXT_MENU_MOVE=    1,
            CONTEXT_MENU_EDIT=    2;


  /**
   * Dialogs
   */
  final int DIALOG_CONFIRM_DELETE= 10;



  /**
   * Views
   */
  private TextView tvDebug;
  private DragSortListView mListView;
  private BottomBar bbNotebookList;
  private ActionBar mActionBar;

  /**
   * Other
   */
  private NoteDataSource mDatasource;
  private NotebookAdapter mAdapter;
  private List<Notebook> mNotebookList;
  private String mDbFilename;


  /**
   * Bundle keys
   */
  private final static String ROW_ID= "ROW_ID";     ///used to pass rowId to Dialog


  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    Note unsavedNoteFromPreviousInstance;
    Intent editNoteActivityIntent;
    String editNoteActivityViewType;


    super.onCreate(savedInstanceState);

    setContentView(R.layout.notebook_list_activity);

    bbNotebookList= (NotebookListBottomBar)findViewById(R.id.todo_list_bottombar);
    tvDebug= (TextView)findViewById(R.id.debug_textView);
    mListView = (DragSortListView)findViewById(R.id.notebooks_dslv);
    mListView.setDragEnabled(false);


    //set the filename of the database
    mDbFilename = getIntent().getStringExtra(NdbIntent.DATABASE_NAME);
    if (mDbFilename == null)
    {
      mDbFilename = NDBTableMaster.NOTES_DB;
    }


    mDatasource = new NoteDataSource(this, mDbFilename);
    mDatasource.open(); //opens DB

    mNotebookList = mDatasource.getAllNotebooks();

    mAdapter = new NotebookAdapter(this, R.layout.notebook_item, (ArrayList) mNotebookList, mDatasource);
    mListView.setAdapter(mAdapter);


    //todo find a better way!!!!!!!!!!!!! un-commenting will return context menu for delete
    registerForContextMenu(mListView);


    // gets the activity's default ActionBar
    mActionBar = getActionBar();
    mActionBar.setTitle("Notebooks");
     mActionBar.show();
    // set the app icon as an action to go home
    // we are home so we don't need it
    // actionBar.setDisplayHomeAsUpEnabled(true);


/////////////////////////////////////////////////////////
    tvDebug= (TextView)findViewById(R.id.debug_textView);
    tvDebug.setText(" Notebooks: " +mDatasource.getNotebookCount()+ "                                                                                Notes: " +mDatasource.getNoteCount());


    setupListeners();

    /**
     * Check if unsaved note exists - if so load it in EditNoteActivity
     */
    unsavedNoteFromPreviousInstance= mDatasource.getTemporaryNote();
    if (unsavedNoteFromPreviousInstance != null)
    {
      editNoteActivityIntent=  new Intent(getApplicationContext(), EditNoteActivity.class);
      editNoteActivityIntent.putExtra(Note.GUID_EXTRA, Note.TEMPORARY_NOTE_ID);
      //editNoteActivityIntent.putExtra(NdbIntent.TYPE, NdbIntent.EDIT_NOTE);   //TODO: this is wrong!!!!!!!!!!!!!!!!!!
                                                                                //todo if id == o then is new note, otherwise is an edit
      if (unsavedNoteFromPreviousInstance.getGuid() == 0)
      {
        editNoteActivityIntent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.CREATE);

      }
      else
      {
        editNoteActivityIntent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.EDIT);
      }
      editNoteActivityIntent.putExtra(NdbIntent.VIEW_TYPE, mDatasource.getTemporaryNoteViewType());
      editNoteActivityIntent.putExtra(Notebook.GUID_EXTRA, unsavedNoteFromPreviousInstance.getNbGuid());
      editNoteActivityIntent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);
      startActivity(editNoteActivityIntent);
    }



    return;
  }

  @Override
  public void onResume()
  {
    super.onResume();
    mDatasource.open();
    mAdapter.clear();
    mAdapter.addAll(mDatasource.getAllNotebooks());
    //mListView.invalidate(); doesn't work to reload
    //mAdapter.notifyDataSetInvalidated();
    return;
  }


  @Override
  public void onPause()
  {
    super.onPause();
    //mDatasource.close();       //todo is this an issue when a background process is working on db?
    return;
  }


  private void setupListeners()
  {

    /**
     * ListView Item click listener - launch NoteListActivity or TodoListActivity
     */
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                  {
                    Intent intent;
                    Notebook notebook;



                    //get selected item from listView and pack into intent
                    notebook = (Notebook) mListView.getItemAtPosition(position);

                    if (notebook.getViewType() == null)
                    {
                      notebook.setViewType(Notebook.ViewType.NORMAL);
                    }

                    switch (notebook.getViewType())
                    {
                      case NORMAL:
                        Toast.makeText(getApplicationContext(), "View as notes", Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext(), NoteListActivity.class);
                        break;
                      case TODO:
                        Toast.makeText(getApplicationContext(), "View as todos", Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext(), TodoListActivity.class);
                        break;
                      case GROCERIES:
                        Toast.makeText(getApplicationContext(), "View as GROCERIES", Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext(), GroceryListActivity.class);
                        break;

                      default:
                        Toast.makeText(getApplicationContext(), "Unknown notebook viewtype", Toast.LENGTH_SHORT).show();
                        return;
                        //break;
                    }
                    //Toast.makeText(getApplicationContext(), "Open notebook", Toast.LENGTH_SHORT).show();



                    //intent.putExtra(Notebook.OBJECT, notebook);
                    intent.putExtra(Notebook.GUID_EXTRA, notebook.getGuid());
                    intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.VIEW_NOTEBOOK);
                    intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);

                    startActivity(intent);
                    return;
                  }
                });


    /**
     * DSLV drop listener
     */
    mListView.setDropListener(new DragSortListView.DropListener()
              {
                @Override
                public void drop(int from, int to)
                {
                  Notebook notebook = mAdapter.getItem(from);

                  mAdapter.remove(notebook);
                  mAdapter.insert(notebook, to);

                  //mListView.setDragEnabled(false);

                  //now reorder notebook order

                  mDatasource.updateOrdinals(mNotebookList);

                  //todo re-add changed data and notify. In future can only load ordinals as opposed to entire notebooks
                  mAdapter.notifyDataSetChanged(mDatasource);


                  return;
                }
              });

    /**
     * BottomBar listener
     */
    bbNotebookList.setOnClickListener(new BottomBar.OnClickListener()
              {
                @Override
                public void onClick(BottomBar bottomBar)
                {
                  int actionId=       bottomBar.getActionId();

                  switch (actionId)
                  {
                    case BottomBar.MOVE_FOLDERS:     //todo change this case so it allows folders to be deleted too

                      if (mListView.isDragEnabled() == true)
                      { //end dragsort

                        mListView.setDragEnabled(false);

                      }
                      else
                      { //begin dragsort
                        mListView.setDragEnabled(true);
                      }

                      break;


                    case BottomBar.SYNC:
                      break;

                    default:
                      break;
                  }

                  return;
                }
              });



    return;
  }




  @Override     //this should be in all activities for ActionBar i believe
  public boolean onCreateOptionsMenu(Menu menu)
  {


    // use an inflater to populate the ActionBar with items
    MenuInflater inflater;

    inflater = getMenuInflater();
    inflater.inflate(R.menu.notebook_list_menu, menu);


    return true;
  }



  @Override                //listener for ActionBar... maybe other menus too?
  public boolean onOptionsItemSelected(MenuItem item)
  {
    Intent intent;
    Notebook notebook;

    switch (item.getItemId())
    {


      case R.id.add_option:


        ////////////////////////////////////////////////////////////////////////////
        Toast.makeText(this, "Add new notebook", Toast.LENGTH_SHORT).show();
        intent = new Intent(this, EditNotebookActivity.class);
        intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.CREATE_NOTEBOOK);
        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);
        startActivityForResult(intent,1);
        /////////////////////////////////////////////////////////////////////////////////

        break;

      case R.id.settings_option:
        Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        //intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);  //todo maybe needed??????
        //startActivity(new Intent(this,PreferencesActivity.class));
        startActivity(new Intent(this,AdvancedFeaturesActivity.class));
        //todo must update the gui to reflect changes in preferences
        break;

      case R.id.search_option:
        Toast.makeText(getApplicationContext(), "Search all notebooks", Toast.LENGTH_SHORT).show();
        intent = new Intent(getApplicationContext(), NoteListActivity.class);

        //get selected item from listView and pack into intent
        //notebook = new Notebook();
        //notebook.setGuid(Notebook.ALL);
        intent.putExtra(Notebook.GUID_EXTRA, /*notebook.getGuid()*/Notebook.ALL);
        intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.VIEW_NOTEBOOK);
        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);

        startActivity(intent);

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
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
      menu.setHeaderTitle("Notebook");

      menu.add(Menu.NONE, CONTEXT_MENU_EDIT, 0, "Edit");
      menu.add(Menu.NONE, CONTEXT_MENU_DELETE, 0, "Delete");
    }

    return;
  }


  @Override
  public boolean onContextItemSelected(MenuItem item)
  {

    AdapterView.AdapterContextMenuInfo info;
    int rowId;
    Notebook notebook;
    Intent intent;
    Bundle argsBundle;


    boolean result;

    info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    rowId = (int) mListView.getAdapter().getItemId(info.position);/*what item was selected is ListView*/


    switch (item.getItemId())
    {
      case CONTEXT_MENU_DELETE:
        argsBundle= new Bundle();
        argsBundle.putInt(ROW_ID, rowId);
        showDialog(DIALOG_CONFIRM_DELETE, argsBundle);
       //return(true);
        break;

      case CONTEXT_MENU_EDIT:
        Toast.makeText(this, "Edit selected", Toast.LENGTH_SHORT).show();
        intent = new Intent(this, EditNotebookActivity.class);
        notebook= (Notebook) mListView.getAdapter().getItem(rowId);
        intent.putExtra(Notebook.GUID_EXTRA, notebook.getGuid());
        intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.EDIT_NOTEBOOK);
        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);
        startActivityForResult(intent, 1);  //todo: find out more about result code
        break;
    }

    return(super.onOptionsItemSelected(item));
  }





  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    //probably need to make case statements since this will be called in different contexts
    //needs a case for the cancel button

    super.onActivityResult(requestCode, resultCode, data);
    //add new notebook if user made one
    if(resultCode == RESULT_OK)
    {
      mAdapter.notifyDataSetChanged(mDatasource);

    }
    else if (resultCode == RESULT_CANCELED)
    {
      //do nothing
    }

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
        builder.setMessage("Delete selected notebook?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            int rowId;
            Notebook notebook;
            Boolean result;

            rowId= args.getInt(ROW_ID);
            notebook= (Notebook) mListView.getAdapter().getItem(rowId);
            result= mDatasource.deleteNotebook(notebook.getGuid()); //todo this returns a boolean. Check!!!

            if (result == true)            //record was deleted
            {
              mAdapter.remove(notebook);
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







} //////////////END CLASS//////////////


























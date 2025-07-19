package ndb.ui.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.mobeta.android.dslv.DragSortListView;
import ndb.R; //kanana.notesdatabase.R;
import ndb.db.NDBTableMaster;
import ndb.db.NoteDataSource;
import ndb.types.NdbIntent;
import ndb.types.Note;
import ndb.types.Notebook;
import ndb.ui.adapters.GroceryAdapter;
import ndb.ui.bottombar.BottomBar;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 16/04/13
 * Time: 8:38 PM
 *
 *
 *
 *
 */
public class GroceryListActivity extends AbstractNoteListActivity /*implements View.OnClickListener*/
{

  /**
   * Context menu items
   */
  final int CONTEXT_MENU_DELETE=  0,
          CONTEXT_MENU_MOVE=    1,
          CONTEXT_MENU_EDIT=    2;


  /**
   * Views
   */
  private ActionBar mActionBar;
//  private TextView tvDate;
  private DragSortListView mListView;
  private GroceryAdapter mAdapter;
  private BottomBar bbTodo;
//  private RelativeLayout rlDateBox;


  /**
   * NORMAL mode Listeners
   */
  AdapterView.OnItemLongClickListener mNormalModeOnItemLongClickListener;
  AdapterView.OnItemClickListener mNormalModeOnItemClickListener;

  /**
   * ADVANCED mode Listeners
   */
  AdapterView.OnItemLongClickListener mAdvancedModeOnItemLongClickListener;
  AdapterView.OnItemClickListener mAdvancedModeOnItemClickListener;

  /**
   * Other
   */

  private NoteDataSource mDatasource;
  private List<Note> mNoteList;
  private Notebook mNotebook;
//  private Date mListDate;        //the date listed in tvDate
  private String mDbFilename;
  private int mInteractionMode= BottomBar.INTERACTION_MODE_NORMAL;




  @Override
  public void onCreate(Bundle savedInstanceState)
  {


    super.onCreate(savedInstanceState);
    setContentView(R.layout.grocery_list_activity);

    //tvDate= (TextView)findViewById(R.id.tv_date);
    mListView= (DragSortListView)findViewById(R.id.grocery_list_dslv);
    bbTodo= (BottomBar)findViewById(R.id.grocery_list_bottombar);



    setupListeners();
    initialize();
    setInteractionMode(mInteractionMode);
    updateListView();


    return;
  }




  private void initialize()
  {
    //ActionBar actionBar;
    Intent intent;

    intent= getIntent();
    mActionBar= getActionBar();

    mListView.setDragEnabled(false);
//    mListDate = new Date(Calendar.getInstance().getTimeInMillis());




    /**
     * set the filename of the database
     */

    mDbFilename = getIntent().getStringExtra(NdbIntent.DATABASE_NAME);
    if (mDbFilename == null)
    {
      mDbFilename = NDBTableMaster.NOTES_DB;
    }

    mDatasource = new NoteDataSource(this, mDbFilename);
    mDatasource.open(); //opens DB
    mNotebook= mDatasource.getNotebook(intent.getLongExtra(Notebook.GUID_EXTRA, 45874837));
    mNoteList= new ArrayList<Note>();
    mAdapter = new GroceryAdapter(this, R.layout.grocery_item, (ArrayList) mNoteList);
    mAdapter.setDividerColor(mNotebook.getHeaderColor());
    mListView.setAdapter(mAdapter);


    mActionBar.setTitle(mNotebook.getTitle());



    mListView.setItemsCanFocus(false);



    updateUi();


    return;
  }





  private void setupListeners()
  {

    /**
     * NORMAL_INTERACTION_MODE_LISTENERS
     *
     * Maybe not necessary as this case is already handled....
     */
    mNormalModeOnItemClickListener= new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
      {
        Intent intent;
        Note note;
        intent = new Intent(getApplicationContext(), EditNoteActivity.class);

        //get selected item from listView and pack into intent
        note= (Note) mListView.getAdapter().getItem(position);

        intent.putExtra(Note.GUID_EXTRA, note.getGuid());
        intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.EDIT);
        intent.putExtra(NdbIntent.VIEW_TYPE, NdbIntent.TODO_VIEW);

        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename); //todo does called activity use this?

        startActivityForResult(intent, 0);  //todo: find out more about result code

        return;
      }
    };

    mNormalModeOnItemLongClickListener= new AdapterView.OnItemLongClickListener()
    {
      /**
       * Sets note COMPLETED
       */
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
      {

        Note note;
        note= (Note) mListView.getAdapter().getItem(position);

        if ( note.isCompleted() == true)
        {
          note.setCompleted(false);
        }
        else
        {
          note.setCompleted(true);
          /**
           * If todo_ due date rolls over, chnage the due date to the time completed
           */
          if (note.isRolloverIfNotCompleted() == true)
          {
            note.setDateDue(Calendar.getInstance().getTimeInMillis());
          }
        }
        mDatasource.updateNote(note, false);
        updateListView();



        return true;      //click is handled so will not propagate to other listeners
      }
    };


    /**
     * ADVANCED_INTERACTION_MODE_LISTENERS
     *
     * none... for now
     */








    /**
     * BottomBar listener
     */
    bbTodo.setOnClickListener(new BottomBar.OnClickListener()
    {
      @Override
      public void onClick(BottomBar bottomBar)
      {
        //                    if (bottomBar.getActionType() == BottomBar.ACTION_SORT)
        //                    {
        switch (bottomBar.getActionId())
        {

          case BottomBar.TODO_SHOW_ALL:
            mNotebook.setTodoViewType(Notebook.TodoViewType.ALL_DAYS);
            mDatasource.updateNotebook(mNotebook);
            updateListView();
            break;

          case BottomBar.TODO_SHOW_SINGLE_DAY:
            mNotebook.setTodoViewType(Notebook.TodoViewType.SINGLE_DAY);
            mDatasource.updateNotebook(mNotebook);
            updateListView();
            break;


          case BottomBar.INTERACTION_MODE_NORMAL:
            setInteractionMode(BottomBar.INTERACTION_MODE_NORMAL);
            break;

          case BottomBar.INTERACTION_MODE_ADVANCED:
            setInteractionMode(BottomBar.INTERACTION_MODE_ADVANCED);
            break;


          default:

            break;


        }



        //                    }
        return;
      }
    });


    return;
  }




//  /**
//   * So far only deals with functionality of changing days in the date box
//   * @param v
//   */
//  @Override
//  public void onClick(View v)
//  {
//    //To change body of implemented methods use File | Settings | File Templates.
//    Date currentTime= Calendar.getInstance().getTime();
//
//
//    switch (v.getId())
//    {
//      case R.id.backButton:
//        mListDate= DateUtil.addNumberOfDays(DateUtil.getStartOfDay(mListDate), -1);
//        break;
//      case R.id.forwardButton:
//        mListDate= DateUtil.addNumberOfDays(DateUtil.getStartOfDay(mListDate), +1);
//        break;
//      default:
//        Toast.makeText(this, "Unhandled onCLick() call", Toast.LENGTH_SHORT).show();
//        break;
//
//    }
//
//
//    if (       mListDate.getTime() >= DateUtil.getStartOfDay(currentTime).getTime()
//            && mListDate.getTime() <= DateUtil.getEndOfDay(currentTime).getTime()  )
//    {
//      tvDate.setText("Today");
//    }
//    else
//    {
//      tvDate.setText(DateUtil.formatDate(mListDate));
//    }
//    updateListView();
//
//    return;
//  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.

    /**
     * Reload todo list (so EditNoteActivity note is not overwrittem
     */
    updateListView();
    return;
  }

  @Override     //this should be in all activities for ActionBar i believe
  public boolean onCreateOptionsMenu(Menu menu)
  {


    // use an inflater to populate the ActionBar with items
    MenuInflater inflater;

    inflater = getMenuInflater();
    inflater.inflate(R.menu.todo_list_menu, menu);


    return true;
  }


  @Override                //listener for ActionBar... maybe other menus too?
  public boolean onOptionsItemSelected(MenuItem item)
  {
    Intent intent;
    Note note;

    switch (item.getItemId())
    {


      case R.id.add_option:
        Toast.makeText(this, "Add new note", Toast.LENGTH_SHORT).show();
        note= new Note();
        //note.setDateDue(mListDate.getTime());
        note.setNbGuid(mNotebook.getGuid());
        note.setRolloverIfNotCompleted(mNotebook.isDefaultRollover());       //todo refactor the note creation into EditNoteActivity
        note= mDatasource.createNote(note);

        launchEditNoteActivity(note.getGuid());
        //updateListView();

        break;



      default:
        Toast.makeText(this, "This case is not handled", Toast.LENGTH_SHORT).show();

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

      menu.add(Menu.NONE, CONTEXT_MENU_MOVE, 0, "Move");
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
    Note note;
    Intent intent;

    boolean result;

    info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    rowId = (int) mListView.getAdapter().getItemId(info.position);/*what item was selected is ListView*/


    switch (item.getItemId())      //ind out how getItemId() found. it has NOTHING to do with
    {
      case CONTEXT_MENU_DELETE:
        // Toast.makeText(this, "delete not implemented", Toast.LENGTH_SHORT).show();
        note= (Note) mListView.getAdapter().getItem(rowId);
        mDatasource.deleteNote(note);

        updateListView();
        break;

      case CONTEXT_MENU_MOVE:
        //        mListView.setDragEnabled(true);
        break;

      case CONTEXT_MENU_EDIT:

       // intent = new Intent(getApplicationContext(), EditNoteActivity.class);

        //get selected item from listView and pack into intent
        note= (Note) mListView.getAdapter().getItem(rowId);
        launchEditNoteActivity(note.getGuid());

        //assert: nbGuid exists in note
//        intent.putExtra(Note.GUID, note.getGuid());
//        intent.putExtra(NdbIntent.TYPE, NdbIntent.EDIT_TODO_NOTE);           //todo switch to edit_grocery_note
//        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename); //todo does called activity use this?
//        //ignoreNextSave= true;       //so OnFocusListener doesn't overwrite change
//        startActivityForResult(intent, 0);  //todo: find out more about result code
        break;
    }

    return(super.onOptionsItemSelected(item));
  }


  /**
   * Launches EditNoteActivity with EDIT_TODO_NOTE.
   * @param noteGuid
   */
  private void launchEditNoteActivity(long noteGuid)
  {
    Intent intent;
    intent = new Intent(getApplicationContext(), EditNoteActivity.class);


    //assert: nbGuid exists in note
    intent.putExtra(Note.GUID_EXTRA, noteGuid);
    intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.EDIT);
    intent.putExtra(NdbIntent.VIEW_TYPE, NdbIntent.TODO_VIEW);
    intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename); //todo does called activity use this?
    startActivityForResult(intent, 0);  //todo: find out more about result code



    return;
  }


  /**
   * Call this after updating mNotes list
   *
   * Preconditions:
   * 1) todoListType is set
   */

  private void updateListView()
  {
    Map notesAndExtras= null;
    mAdapter.clear();        //clears notes and extras





    notesAndExtras= mDatasource.getNotesForGroceryList(mNotebook.getGuid());


    //mNoteList= (List<Note>)notesAndExtras.get(NoteDataSource.Key.NOTE_LIST);
    //mAdapter.putExtras(notesAndExtras);       //todo refactor to setNotesAndExtras to eliminate need for previous line
    //mAdapter.addAll(mNoteList);               //
    mAdapter.setNotesAndExtras(notesAndExtras);
    mAdapter.notifyDataSetChanged();

//    if (mNotebook.getTodoViewType() == Notebook.TodoViewType.ALL_DAYS)
//    {
//      mListView.setSelection((Integer)notesAndExtras.get(NoteDataSource.Key.TODAYS_TODOS_START_INDEX));
//    }

    return;
  }


  //  /**
  //   * Updates the notes in list view. Sets top of screen to first item of given date
  //   * @param date
  //   */
  //  private void updateListView(Date date)
  //  {
  //
  //  }



  protected void updateUi()
  {

    if (mNotebook.getHeaderColor() != 0)
    {
      mActionBar.setBackgroundDrawable(new ColorDrawable(mNotebook.getHeaderColor()));
      bbTodo.setBackgroundColor(mNotebook.getHeaderColor());
    }


    return;
  }



  /**
   * Changes Listeners, interactions etc
   *
   * Interaction modes:
   * 1) BottomBar.INTERACTION_MODE_NORMAL
   * 2) BottomBar.INTERACTION_MODE_ADVANCED
   * @param interactionMode
   */
  private void setInteractionMode(int interactionMode)
  {
    if (interactionMode == BottomBar.INTERACTION_MODE_NORMAL)
    {
      unregisterForContextMenu(mListView);
      mListView.setOnItemClickListener(mNormalModeOnItemClickListener);
      mListView.setOnItemLongClickListener(mNormalModeOnItemLongClickListener);

    }
    else if (interactionMode == BottomBar.INTERACTION_MODE_ADVANCED)
    {
      registerForContextMenu(mListView);
      //mListView.setOnItemClickListener(mAdvancedModeOnItemClickListener);
      mListView.setOnItemLongClickListener(/*mAdvancedModeOnItemLongClickListener*/ null);

    }
    else
    {
      Toast.makeText(getApplicationContext(), "Unknown interaction mode", Toast.LENGTH_SHORT).show();
    }

    return;
  }


}    ////END CLASS////









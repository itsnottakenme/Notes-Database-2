package ndb.ui.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobeta.android.dslv.DragSortListView;
import ndb.R; //kanana.notesdatabase.R;
import ndb.db.NDBTableMaster;
import ndb.db.NoteDataSource;
import ndb.types.NdbIntent;
import ndb.types.Note;
import ndb.types.Notebook;
import ndb.ui.adapters.TodoAdapter;
import ndb.ui.bottombar.BottomBar;
import ndb.ui.views.SearchBox;
import ndb.util.DateUtil;

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
public class TodoListActivity extends AbstractNoteListActivity implements View.OnClickListener
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
  private SearchBox sbSearchBox;
  private TextView tvDate;
  private DragSortListView mListView;
  private TodoAdapter mAdapter;
  private BottomBar bbTodo;
  private RelativeLayout rlDateBox;
  private LinearLayout ll_td_wrapper;


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
   * Other Listeners
   */
  private SearchListener mSearchBoxListener;

  /**
   * Other
   */

  private NoteDataSource mDatasource;
  private List<Note> mNoteList;
  private Notebook mNotebook;
  private Date mListDate;        //the date listed in tvDate
  private String mDbFilename;
  private int mInteractionMode= BottomBar.INTERACTION_MODE_NORMAL;



 // private int mViewMode;        //SHOW_ALL or SINGLE_DAY


  /**
   * Control
   */

  /**
   * When returning from EditNoteActivity OnFocusListener() is called before OnActivityResult()
   * this variable is used to prevent OnFocusListener from overwriting result from EditNoteActivity
   */
  private boolean   mIgnoreNextSave = false,

                    mScrollListToToday = false;     //should list scroll to Today item?


  @Override
  public void onCreate(Bundle savedInstanceState)
  {


    super.onCreate(savedInstanceState);
    setContentView(R.layout.todo_list_activity);

    sbSearchBox= (SearchBox)findViewById(R.id.notelist_searchbox);
    tvDate= (TextView)findViewById(R.id.tv_date);
    mListView= (DragSortListView)findViewById(R.id.todo_list_dslv);
    bbTodo= (BottomBar)findViewById(R.id.todo_list_bottombar);
    rlDateBox= (RelativeLayout)findViewById(R.id.date_select_layout);



    /// ///////////////////////////////////////
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

    ll_td_wrapper= (LinearLayout)findViewById(R.id.ll_td_wrapper);

    /////////////// Other WindowInsets code after IDs assigned/////////////////
    ViewCompat.setOnApplyWindowInsetsListener(ll_td_wrapper, (v, insets) -> {
      Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

      // Apply padding to the content layout to avoid system bars
      // changed to hopefully work :P
      v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);

      /// /////////////////////////////////////////////////////////////////////
      return insets;
    });


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
   final LinearLayout vFooter;




    mListView.setDragEnabled(false);
    mListDate = new Date(Calendar.getInstance().getTimeInMillis());
    tvDate.setText("Today");


    /**
     * set ListView footer
     */
    vFooter= (LinearLayout)getLayoutInflater().inflate(R.layout.listview_dummy_footer, null);
    mListView.addFooterView(vFooter, null, false);


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
    mAdapter = new TodoAdapter(this, R.layout.todo_item, (ArrayList) mNoteList);
    mAdapter.setDividerColor(mNotebook.getHeaderColor());
    mListView.setAdapter(mAdapter);


    mActionBar.setTitle(mNotebook.getTitle());
    mListView.setItemsCanFocus(false);

    mScrollListToToday= true;
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

    /**
     * Toggle complete for note
     */
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
                      if (note.isRolloverIfNotCompleted() == true && note.getRepeatingField() == -1)    //todo: half relevant code in note.setCompleted(). Poor archetecure!
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
     * SearchListener is an inner class
     */
    mSearchBoxListener= new SearchListener();
    sbSearchBox.setListener(mSearchBoxListener);



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
                          mNotebook= mDatasource.updateNotebook(mNotebook);
                          updateListView();
                          break;

                        case BottomBar.TODO_SHOW_SINGLE_DAY:
                          mNotebook.setTodoViewType(Notebook.TodoViewType.SINGLE_DAY);
                          mNotebook= mDatasource.updateNotebook(mNotebook);
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




  /**
   * So far only deals with functionality of changing days in the date box
   * @param v
   */
  @Override
  public void onClick(View v)
  {
    //To change body of implemented methods use File | Settings | File Templates.
    Date currentTime= Calendar.getInstance().getTime();


    switch (v.getId())
    {
      case R.id.backButton:
        mListDate= DateUtil.addNumberOfDays(DateUtil.getStartOfDay(mListDate), -1);
        break;
      case R.id.forwardButton:
        mListDate= DateUtil.addNumberOfDays(DateUtil.getStartOfDay(mListDate), +1);
        break;
      default:
        Toast.makeText(this, "Unhandled onCLick() call", Toast.LENGTH_SHORT).show();
        break;

    }


    if (       mListDate.getTime() >= DateUtil.getStartOfDay(currentTime).getTime()
            && mListDate.getTime() <= DateUtil.getEndOfDay(currentTime).getTime()  )
    {
      tvDate.setText("Today");
    }
    else
    {
      tvDate.setText(DateUtil.formatDate(mListDate));
    }
    updateListView();

    return;
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.

    /**
     * Reload todo list (so EditNoteActivity note is not overwrittem
     */
    mScrollListToToday= false;
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
        //Intent intent;
        intent = new Intent(getApplicationContext(), EditNoteActivity.class);
        intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.CREATE);
        intent.putExtra(NdbIntent.VIEW_TYPE, NdbIntent.TODO_VIEW);
        intent.putExtra(Notebook.GUID_EXTRA, mNotebook.getGuid());

        if (mNotebook.getTodoViewType() == Notebook.TodoViewType.SINGLE_DAY)
        {
          intent.putExtra(Note.DATE_DUE, mListDate.getTime());
        }
        else // ASSERT: mNotebook.getTodoViewType() == Notebook.TodoViewType.ALL_DAYS
        {
          intent.putExtra(Note.DATE_DUE, Calendar.getInstance().getTimeInMillis());
        }

        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename); //todo does called activity use this?
        startActivityForResult(intent, 0);  //todo: find out more about result code
        break;

      case R.id.search_option:
        Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
        sbSearchBox.toggleVisibility();
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

        intent = new Intent(getApplicationContext(), EditNoteActivity.class);

        //get selected item from listView and pack into intent
        note= (Note) mListView.getAdapter().getItem(rowId);


        //assert: nbGuid exists in note
        intent.putExtra(Note.GUID_EXTRA, note.getGuid());
        intent.putExtra(NdbIntent.ACTION_TYPE, NdbIntent.EDIT);
        intent.putExtra(NdbIntent.VIEW_TYPE, NdbIntent.TODO_VIEW);
        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename); //todo does called activity use this?
        //ignoreNextSave= true;       //so OnFocusListener doesn't overwrite change
        startActivityForResult(intent, 0);  //todo: find out more about result code
        break;
    }

    return(super.onOptionsItemSelected(item));
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


    /**
     * All Days case
     */
    if (mNotebook.getTodoViewType() == Notebook.TodoViewType.ALL_DAYS)
    {
      rlDateBox.setVisibility(View.GONE);
      notesAndExtras = mDatasource.getNotesForTodoList(mNotebook.getGuid(), Calendar.getInstance().getTime());
    }

    /**
     * Single Day case
     */
    else if (mNotebook.getTodoViewType() == Notebook.TodoViewType.SINGLE_DAY)
    {
      rlDateBox.setVisibility(View.VISIBLE);
      notesAndExtras= mDatasource.getNotesForTodoListSingleDay(mNotebook.getGuid(), mListDate);
    }

    mNoteList= (List<Note>)notesAndExtras.get(NoteDataSource.Key.NOTE_LIST);
    mAdapter.putExtras(notesAndExtras);       //todo refactor to setNotesAndExtras to eliminate need for previous line
    mAdapter.addAll(mNoteList);               //
    mAdapter.notifyDataSetChanged();

    /**
     * Puts Today item at top of screen iof required
     */

    if (mNotebook.getTodoViewType()==Notebook.TodoViewType.ALL_DAYS && mScrollListToToday == true)
    {
      /*
       * Seems to be the only way mListView.setSelection() works properly
       */
      final int listIndex= (Integer)notesAndExtras.get(NoteDataSource.Key.TODAYS_TODOS_START_INDEX);
      mListView.post(new Runnable()
                {
                  @Override
                  public void run()
                  {
                    mListView.setSelection(listIndex);
                  }
                });
    }

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


  /**
   *
   *
   * @param searchTerm
   * @param notebookId
   * @param tag
   * @param sortColumn       - is AN ACTUAL column name from database
   * @param ascDsc
   *
   *
   * todo: BROKEN this was taken from NoteListActivity. This all needs to be changed!!!!!!!!!!!!!!
   */
  private void search(String searchTerm, long notebookId, String tag, String sortColumn, String ascDsc)
  {
    /**
     * todo: this automatically turns view to ALL_DAYS. i SHOULD CHANGE change this as well.
     */
    Map notesAndExtras;
//    if (sortColumn == null)
//    {
//      sortColumn = Note.DATE_MODIFIED;
//    }
//    if (ascDsc == null)
//    {
//      ascDsc= Sort.DESCENDING;
//    }
    mAdapter.clear();


    /**
     * Reload adapter
     */
    notesAndExtras= mDatasource.getNotesForTodoList(notebookId, Calendar.getInstance().getTime(), searchTerm, tag);
    mNoteList= (List<Note>)notesAndExtras.get(NoteDataSource.Key.NOTE_LIST);
    mAdapter.putExtras(notesAndExtras);       //todo refactor to setNotesAndExtras to eliminate need for previous line
    mAdapter.addAll(mNoteList);               //
    mAdapter.notifyDataSetChanged();


    /**
     * Probably not needed anymore... except for maybe Notebook.ALL case
     */
//    if ( mNotebook.getGuid() == Notebook.ALL )   //todo: this is not needed since Notebook.ALL is defined as -1
//    { //todo: getNotesForTodoList() needs to implement Notebook.ALL case
//      mNoteList.addAll(mDatasource.getNotes(searchTerm, -1, tag, sortColumn, ascDsc));
//    }
//    else  //just load a single notebook
//    {
//      //mNoteList.addAll(mDatasource.getNotes(searchTerm, notebookId, tag, sortColumn, ascDsc));
//      mNoteList.addAll(mDatasource.getNotesForTodoList(mNotebook.getGuid(), Calendar.getInstance().getTime(), searchTerm, tag) );
//
//    }



    mAdapter.notifyDataSetChanged();

    return;
  }



  /**
   * Class SearchListener
   * todo: BROKEN this was taken from NoteListActivity. This all needs to be changed!!!!!!!!!!!!!!
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





}    ////END CLASS////











/**
 *
 * Extra code:
 */


/**
 * Listeners
 */



//  /**
//   * Doesn't work due to no way find position (and therefore guid) of changed ListView item
//   */
//  //todo maybe use OnItemSelectedListener instead. Use a variable to keep a reference to previously selected item
//  // mAdapter.setOnFocusChangeListener(
//
///**
// *  this is disabled!!!!!!!!!!!!!!!!!!! Move code!!!!!!!
// *
// */
//View.OnFocusChangeListener ofcl=  new View.OnFocusChangeListener()
//{
//  @Override
//  public void onFocusChange(View v, boolean hasFocus)
//  {
//    Note note;
//    RichEditText retContent;
//
//    if (hasFocus == false /*&& ignoreNextSave == false*/)
//    {
//      if (v.getClass() != LinearLayout.class)
//      {
//        v = (View) v.getParent();   //get todo list item
//      }
//
//      retContent = (RichEditText) v.findViewById(R.id.richNoteContent);
//      note = mDatasource.getNote((Long) v.getTag(R.string.NOTE_GUID));                     //was v.getId()
//      note.setContent(retContent.getText().toString());
//
//
//      /**
//       * Note: do not save dateDue from textView here. Value in textview maybe not be correct dateDue
//       * because it is a processed field (eg. "Today")
//       */
//
//
//      /**
//       * todo: fix json saving code!
//       * The following line is what causes the keyboard to freeze/malfunction upon second(?) edit
//       * My guess is that the editing spans are removed upon save. When ret reloads the item is still in focus and
//       * the editing spans are not reinstated (I am guessing this because selection and cursor are sometimes in
//       * different places)
//       *
//       * Even when
//       * mDatasource.updateNote(note);                     -and-
//       * mNoteList= mDatasource.getNotesFromNotebook(mNotebook.getGuid());
//       * are commented out note.setSpansAsJson(retContent.getSpansAsJson()) is still a problem!!!
//       *
//       * retContent.getSpansAsJson() must internally break itself...
//       * */
//      //note.setSpansAsJson(retContent.getSpansAsJson());
//
//
//      //Todo INSERT more note saving code here: dueDate, etc
//      mDatasource.updateNote(note);
//      mNoteList = mDatasource.getNotesFromNotebook(mNotebook.getGuid());     //todo: these notes are in WRONG ORDER! Make another update notes method
//      Toast.makeText(getApplicationContext(), "Saved"/*+note.getContent()*/, Toast.LENGTH_SHORT).show();
//
//    }
//    //ignoreNextSave= false;
//  }
//};



/**
 *        When Views inside item are selected, this listener is not called.      Maybe make RichEditTexts non-focusable
 *        and then when item is clicked it can pass the focus to the RichEditText. Then this shouldn't be an issue anymore.
 *
 * todoFails due to the fact that lastSelectedView may have already been recycled. Ask on Stack Overflow
 * Make a hack in Adapter that if recycled view == lastSelectedView, then dobn't recycle
 */
//    mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
//                {
//                  private View lastSelectedView= null;          //this should only be LinearLayouts
//                  private long lastSelectedNoteGuid;            //used to ensure view hasn't been recycled. maybe make it view creation date?
//
//                  @Override
//                  public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
//                  {
//                    Note note;
//                    RichEditText retContent;
//
//                    mNoteList.get(position).getGuid();
//
//                    if (lastSelectedView != null)
//                    {
//                      retContent = (RichEditText) lastSelectedView.findViewById(R.id.richNoteContent);    //todo careful view may be recycled already
//                      note= mDatasource.getNote(mNoteList.get(position).getGuid());
//                      note.setContent(retContent.getText().toString());
//                      note.setSpansAsJson(retContent.getSpansAsJson());
//                      //Todo INSERT more note saving code here: dueDate, etc
//                      //
//
//                      mDatasource.updateNote(note);
//                      mNoteList= mDatasource.getNotesFromNotebook(mNotebook.getGuid());
//                      Toast.makeText(getApplicationContext(), "Saved:"+note.getContent(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    lastSelectedView= view;
//                    //todo mAdapter.setNotToBeRecycledView(lastSelectedView);
//                    return;
//                  }
//
//                  @Override
//                  public void onNothingSelected(AdapterView<?> adapterView)
//                  {
//                    Toast.makeText(getApplicationContext(), "onNothingSelected() called", Toast.LENGTH_SHORT).show();
//                  }
//                });



//    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
//                  {
//                    @Override
//                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
//                    {
//                      Toast.makeText(getApplicationContext(), "Long click listener", Toast.LENGTH_SHORT).show();
//                      return false;
//                    }
//                  });


/**
 * Other:
 */

//  private void saveCurrentItemasANote()
//  {
////    case R.id.save_option:
////
////    //intent= getIntent();
////    //intentReason= intent.getStringExtra(NdbIntent.TYPE);
////
////
////    if (intentReason.equals(NdbIntent.CREATE_NOTE))
////    {
////
////      note.setTitle(etTitle.getText().toString());
////      note.setContent(retContent.getText().toString());
////      /////////////////////////////////////////
////      //etContent.removeTemporarySpans();
////      note.setContent( /*new HtmlSpanner().toHtml(*/retContent.getCleanedText().toString() );
////      /////////////////////////////////////////
////
////      note.setTags(etTags.getText().toString());
////
////      //todo cant be assumed a notebook will be in the intent. if not then NULLPOINTEREXCEPTION
////      note.setNbGuid(mNotebook.getGuid());
////      note.setSpansAsJson(retContent.getSpansAsJson());
////      //getByteArrayOfSpans());
////
////
////      //CREATE NOTE MUST BE EDITED SO CAN SHOW NOTEBOOK ID!!!
////      note= mDatasource.createNote(note);
////
////      //newNote = datasource.createNote(data.getStringExtra(Note.TITLE), data.getStringExtra(Note.CONTENT));
////    }
////
////    else if (intentReason.equals(NdbIntent.EDIT_NOTE))
////    {         //assert note already exists so will be updated... actually i think SQL can handle fboth these cases the same way :)
////      //note= (Note)intent.getParcelableExtra(Note.OBJECT); //so other fields won't change
////      note= mDatasource.getNote(intent.getLongExtra(Note.GUID, 374647));
////      note.setTitle(etTitle.getText().toString());
////      note.setContent(retContent.getText().toString());
////
////      ////////////////////
////      //etContent.removeTemporarySpans();
////      //note.setContent(retContent.getCleanedText().toString() );
////      ///////////////////
////
////      note.setSpansAsJson(retContent.getSpansAsJson());
////      note.setTags(etTags.getText().toString());
////
////
////
////      note= mDatasource.updateNote(note);
////
////
////    }
////    else
////    {
////      Toast.makeText(this, "save - unhandled case. note database not updated", Toast.LENGTH_SHORT).show();
////    }
////
////    returnIntent= new Intent();
////    //returnIntent.putExtra(Note.OBJECT, note);           //this contains the wrong note!!!!!!!!!
////    returnIntent.putExtra(Note.GUID, note.getGuid());
////    setResult(RESULT_OK, returnIntent);
////
////
////    finish();  //probably can be moved to end
////    break;
////
////
////
//
//  }







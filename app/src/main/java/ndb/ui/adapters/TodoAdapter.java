package ndb.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import ndb.R; //kanana.notesdatabase.R;
import ndb.db.NoteDataSource;
import ndb.types.Note;
import ndb.util.DateUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 05/07/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 *
 *
 *
 * todo: refactoring
 * - overide clear() to clear extra as well
 *
 *
 *
 *
 *
 *
 * - add a method DontRecycleSingleView(View view) that will prevent the view from being recycled (lastSelectedView)
 *
 *
 *     mAdapter.clearAllExtras();

       notesAndExtras = mDatasource.getNotesForTodoList(mNotebook.getGuid(), mListDate);
       mNoteList= (List<Note>)notesAndExtras.get(TodoAdapter.Key.TODO_LIST);
       mAdapter.putExtras(notesAndExtras);

 *
 * Call extrasExist() before using them
 *
 */
public class TodoAdapter extends ArrayAdapter<Note>
{
//  /**
//   * Keys for Extras
//   *
//   * indexes are -1 if they don't exist
//   */
//  public enum Key
//  {
//
//    /**
//     * Types held in the map
//     */
//    TODO_LIST,                                         // List<Note>
//
//    PAST_TODOS_START_INDEX,                            // int
//    TODAYS_TODOS_START_INDEX,                          // int
//    FUTURE_TODOS_START_INDEX,                          // int
//
//
//    /**
//     * Sentinel Values
//     */
//
//    /**
//     * Parts of Todo List
//     */
//    PAST,
//    TODAY,
//    FUTURE,
//
//    /**
//     * Determines how to view
//     */
//    VIEW_TYPE,                //key
//
//    VIEW_TODOS_SINGLE_DAY,    //the different values the key can contain
//    VIEW_TODOS_ALL,           //
//
//
//
//  }

  /**
   * Member variables
   */
  private int mDividerColor,
              mOtherDateColor= Color.parseColor("#ffff0000"),//16711680,
              mTodaysDateColor = Color.parseColor("#00C300");//65280;    //green


  private Map<NoteDataSource.Key, Object> mExtras;
  private View.OnFocusChangeListener mOnFocusChangeListener;
  private ArrayList<Note> mNoteList;





  public TodoAdapter(Context context, int textViewResourceId, ArrayList<Note> notes)
  {
    super(context, textViewResourceId, notes);
    mNoteList = notes;
    mExtras= new HashMap();
    return;
  }

  /**
   * Contains organizational data about the note list
   * eg. indexes of
   * indexes are -1
   * @param key
   * @param data
   */
  public void putExtra(NoteDataSource.Key key, Object data)
  {
    mExtras.put(key, data);
    return;
  }

  /**
   * Puts extras into a Map. Its ok if TODO_LIST is included in Map as it will be automatically removed for convenience
   * @param extras
   */
  public void putExtras(Map<NoteDataSource.Key, Object> extras)
  {
    extras.remove(NoteDataSource.Key.NOTE_LIST);         //a hack to remove list of notes to prevent memory leak
    mExtras.putAll(extras);
    return;
  }

  public void clearAllExtras()
  {
    mExtras.clear();
  }


  /**
   * todo refactor all the shit out of getView()!!!!!!!!!!!!!!!!!!!! put the due date logic into NoteDataSource or even
   * into a static method in Note.
   * ie. Note.setTransientDueDates(List<Note>)
   * @param position
   * @param convertView
   * @param parent
   * @return
   */

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    Note note;                         //the note whose view is being created
    long previousNoteDateDue=0;          //used to determine if note should set due date as visible
    ViewHolder holder;

    NoteDataSource.Key partOfTodoList;


    if (convertView == null)
    {
      LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = vi.inflate(R.layout.todo_item, null);
      convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));   //fix for dslv bug where width= match_parent is ignored

      holder= new ViewHolder();
      holder.retTitle = /*(RichEditText)*/(TextView) convertView.findViewById(R.id.note_title); //todo slow to use findbyId. Optimize!
      holder.tvDueDate= (TextView) convertView.findViewById(R.id.date_due);
      holder.tvDebugDateDue= (TextView) convertView.findViewById(R.id.debug_date_due);
      holder.tvDivider= (TextView) convertView.findViewById(R.id.todo_divider);

      convertView.setTag(holder);
    }
    else
    {
      holder= (ViewHolder)convertView.getTag();
      holder.tvDueDate.setVisibility(View.GONE);
    }


    if (mOnFocusChangeListener!=null)
    {
      convertView.setOnFocusChangeListener(mOnFocusChangeListener);                //do I really need listener for both?
      holder.retTitle.setOnFocusChangeListener(mOnFocusChangeListener);          //
    }

    note= mNoteList.get(position);

    /**
     * Set the views
     */
    if (note != null)
    {
      /**
       * set the content of note
       */
      if (note.getContent() == null)
      {
        holder.retTitle.setText("");
      }
      else
      {
        //todo uncomment when retContent become RichEditText again
        //holder.retContent.setText(note.getContent(), note.getSpansAsJson());
        holder.retTitle.setText(note.getTitle());

      }


      /**
       * set the date_due if necessary
       */

      if ( mExtras.get(NoteDataSource.Key.VIEW_TYPE) == NoteDataSource.Key.VIEW_TODOS_ALL )
      {
        /**
         * All days case
         */
        holder.tvDueDate.setText(DateUtil.formatDate(new Date(note.getDateDue())));
        if ( note.isShowDueDate() == true)
        {
          holder.tvDueDate.setVisibility(View.VISIBLE);
          if (position == (Integer)mExtras.get(NoteDataSource.Key.TODAYS_TODOS_START_INDEX))
          {
            holder.tvDueDate.setText("Today");
            holder.tvDueDate.setBackgroundColor(mTodaysDateColor);
          }
          else
          {
            holder.tvDueDate.setBackgroundColor(mOtherDateColor);
          }
        }
        else
        {
          holder.tvDueDate.setVisibility(View.GONE);
        }
      }
      else if ( mExtras.get(NoteDataSource.Key.VIEW_TYPE) == NoteDataSource.Key.VIEW_TODOS_SINGLE_DAY )
      {
        /**
         * View single day case
         *
         * Don't need to show dates for single day case
         *
         */
      }
//               //todo: change date COLORS here!!!!!!!
//      if (holder.tvDueDate.getText().toString().equals("Today"))
//      {                                                                        ;
//        holder.tvDueDate.setBackgroundColor(/*mTodaysDateColor*/ 345634);
//      }
//      else
//      {
//        holder.tvDueDate.setBackgroundColor(/*mOtherDateColor*/998);
//      }
      holder.tvDivider.setBackgroundColor(mDividerColor);
      holder.tvDebugDateDue.setText("------Position: " +position+ " -----noteId: "+note.getGuid()+" ---------------{"+note.getDateDue()+"}"+ " "+DateUtil.formatDate(note.getDateDue()));
      //convertView.setId((int)note.getGuid());           ////////////// //todo replace with setTag(). This is an improper use!!!
      convertView.setTag(R.string.NOTE_GUID, note.getGuid());


     /**
      * set Strikethrough FONT
      */
     if (note.isCompleted())
     {
       holder.retTitle.setPaintFlags(holder.retTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
     }
      else
     {
       holder.retTitle.setPaintFlags(holder.retTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
     }

    }

    return convertView;
  }


  public void setOnFocusChangeListenerc(View.OnFocusChangeListener onFocusChangeListener)
  {
    mOnFocusChangeListener= onFocusChangeListener;
  }


  //  private boolean extrasExist()
  //  {
  //    boolean exist= false;
  //
  //    if ( mExtras.get(Key.PAST_TODOS_START_INDEX)!=null   &&
  //         mExtras.get(Key.TODAYS_TODOS_START_INDEX)!=null &&
  //         mExtras.get(Key.FUTURE_TODOS_START_INDEX)!=null    )
  //      exist= true;
  //
  //    return exist;
  //  }

  @Override
  public void clear()
  {
    super.clear();
    mExtras.clear();
  }

  public int getDividerColor()
  {
    return mDividerColor;
  }

  public void setDividerColor(int dividerColor)
  {
    this.mDividerColor = dividerColor;
  }


  /******************************************************
   * Private class ViewHolder                           *
   ******************************************************/
  public static class ViewHolder
  {
    public /*RichEditText*/ TextView retTitle = null;
    TextView tvDueDate= null;
    TextView tvDebugDateDue,
             tvDivider;
    long arrayIndex;


    long noteGuid;
  }






}    ///////////END CLASS//////////////











/**
 * A hack to make sure items in ArrayList are in the correct order since
 * order is not maintained automatically
 * @param datasource
 */
//  public void notifyDataSetChanged(NoteDataSource datasource)
//  {
//    this.clear();
//
//    datasource.open();
//    this.addAll(datasource.getAllNotes());
//
//
//    super.notifyDataSetChanged();
//    return;
//  }






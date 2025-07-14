package ndb.ui.adapters;

import android.content.Context;
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
public class GroceryAdapter extends ArrayAdapter<Note>
{


  int mDividerColor;


  Map<NoteDataSource.Key, Object> mExtras;

  View.OnFocusChangeListener mOnFocusChangeListener;
  private ArrayList<Note> mNoteList;

  public GroceryAdapter(Context context, int textViewResourceId, ArrayList<Note> notes)
  {
    super(context, textViewResourceId, notes);
    mNoteList = notes;
    mExtras= new HashMap();
    return;
  }


  /**
   * Puts extras into a Map. Its ok if TODO_LIST is included in Map as it will be automatically removed for convenience
   * @param extras
   */
  private void putExtras(Map<NoteDataSource.Key, Object> extras)
  {
    extras.remove(NoteDataSource.Key.NOTE_LIST);         //a hack to remove list of notes to prevent memory leak
    mExtras.putAll(extras);
    return;
  }


  /**
   * 1) clears previous notes and extras
   * 2) adds back new notes and extras from notesAndExtras
   * @param notesAndExtras
   */
  public void setNotesAndExtras(Map<NoteDataSource.Key, Object> notesAndExtras)
  {
    mExtras.clear();
    mNoteList.clear();
    mNoteList.addAll((List<Note>)notesAndExtras.get(NoteDataSource.Key.NOTE_LIST));
    notesAndExtras.remove(NoteDataSource.Key.NOTE_LIST);
    mExtras.putAll(notesAndExtras);

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
    ViewHolder holder;


    if (convertView == null)
    {
      LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = vi.inflate(R.layout.grocery_item, null);
      convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));   //fix for dslv bug where width= match_parent is ignored

      holder= new ViewHolder();
      holder.tvTitle = (TextView) convertView.findViewById(R.id.note_title); //todo slow to use findbyId. Optimize!
      holder.tvCompletedHeading = (TextView) convertView.findViewById(R.id.completed_heading);
      holder.tvDivider= (TextView) convertView.findViewById(R.id.note_divider);

      convertView.setTag(holder);
    }
    else
    {
      holder= (ViewHolder)convertView.getTag();
      holder.tvCompletedHeading.setVisibility(View.GONE);
    }


    if (mOnFocusChangeListener!=null)
    {
      convertView.setOnFocusChangeListener(mOnFocusChangeListener);                //do I really need listener for both?
      holder.tvTitle.setOnFocusChangeListener(mOnFocusChangeListener);          //                     //todo WHAT DO WITH THESE?????????????????????????????????
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
        holder.tvTitle.setText("");
      }
      else
      {
        holder.tvTitle.setText(note.getTitle());
      }


      /**
       * set the heading for grocceries ("TO GET:" AND "GOTTEN")
       */

      if ( mExtras.get(NoteDataSource.Key.VIEW_TYPE) == NoteDataSource.Key.VIEW_GROCERY_LIST )
      {

        if (position == (Integer)mExtras.get(NoteDataSource.Key.GROCERY_LIST_NOT_COMPLETED_START_INDEX))
        {
          holder.tvCompletedHeading.setText("To get");
          holder.tvCompletedHeading.setVisibility(View.VISIBLE);
        }
        else if (position == (Integer)mExtras.get(NoteDataSource.Key.GROCERY_LIST_COMPLETED_START_INDEX))
        {
          holder.tvCompletedHeading.setText("Completed");
          holder.tvCompletedHeading.setVisibility(View.VISIBLE);
        }
        else
        {
          holder.tvCompletedHeading.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(note.getTitle());

      }
      holder.tvDivider.setBackgroundColor(mDividerColor);              //todo maybe do only once at creation
      convertView.setTag(R.string.NOTE_GUID, note.getGuid());


      /**
       * set Strikethrough FONT
       */
      if (note.isCompleted())
      {
        holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      }
      else
      {
        holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
      }

    }

    return convertView;
  }


  public void setOnFocusChangeListenerc(View.OnFocusChangeListener onFocusChangeListener)
  {
    mOnFocusChangeListener= onFocusChangeListener;
  }


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
    public TextView tvTitle = null;
    TextView tvCompletedHeading = null;
    TextView //tvDebugDateDue,
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






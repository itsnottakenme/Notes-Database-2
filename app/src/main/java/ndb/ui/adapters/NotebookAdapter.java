package ndb.ui.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
//import notesdatabase.db.NotebookDataSource;
import ndb.R; //kanana.notesdatabase.R;
import ndb.db.NoteDataSource;
import ndb.types.Notebook;


import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 20/03/13
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotebookAdapter extends ArrayAdapter<Notebook>
{



  private ArrayList<Notebook> mNotebookList; //not needed as parent class keeps this list
  private NoteDataSource mNoteDs;


  public NotebookAdapter(Context context, int textViewResourceId, ArrayList<Notebook> notebooks, NoteDataSource dataSource)
  {
    super(context, textViewResourceId, notebooks);
    mNotebookList = notebooks;
    mNoteDs = dataSource;
    return;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View v;
    Notebook notebook;
    TextView tvTitle;
    TextView tvGuid;
    TextView tvNoteCount;

    v= convertView;

    if (v == null)
    {
      LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(R.layout.notebook_item, null);
    }

    notebook= mNotebookList.get(position);
    if (notebook != null)
    {
      tvTitle= (TextView) v.findViewById(R.id.notebook_title);
      tvGuid = (TextView) v.findViewById(R.id.notebook_guid);
      tvNoteCount= (TextView) v.findViewById(R.id.notebook_note_count);

      ///////////SET GRADIENT////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////////////////////
      //v.setBackgroundColor(notebook.getHeaderColor());



      GradientDrawable gd = new GradientDrawable(
              GradientDrawable.Orientation.TOP_BOTTOM,
              new int[] {notebook.getHeaderColor(),notebook.getHeaderColor()+1000});
      gd.setCornerRadius(0f);

      v.setBackgroundDrawable(gd);

      /////////////////////////////////////////////////////////////////////////////////////////
      if (tvTitle != null)
      {
        tvTitle.setText(notebook.getTitle());
      }

      if(tvGuid!= null)
      {
        tvGuid.setText("GUID: " + notebook.getGuid() + " Ordinal: "+notebook.getOrdinal());
      }
      if (tvNoteCount != null)
      {
       // mNoteDs = new NoteDataSource(getContext(), NDBTableHelper.NOTES_DB);

        tvNoteCount.setText(/*"Notes: " +*/ mNoteDs.getNoteCount(notebook.getGuid())+"");

      }
    }
    return v;
  }

  /**
   * A hack to make sure items in ArrayList are in the correct order since
   * order is not maintained automatically
   * @param datasource
   */
  public void notifyDataSetChanged(NoteDataSource datasource)
  {
    this.clear();

    datasource.open();
    this.addAll(datasource.getAllNotebooks());


    super.notifyDataSetChanged();
    return;
  }



} ////////////END CLASS//////////////

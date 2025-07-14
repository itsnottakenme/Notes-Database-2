package ndb.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.Note;
import ndb.types.Tag;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 21/03/13
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class NoteAdapter extends ArrayAdapter<Note>
{
  private ArrayList<Note> mNoteList;
  /**
   * showCreatedDate
   * true - shows created date
   * false - shows modified date
   */
  private boolean mShowCreatedDate = false;
  DateFormat mDateFormatter;



  public NoteAdapter(Context context, int textViewResourceId, ArrayList<Note> notes)
  {
    super(context, textViewResourceId, notes);
    mNoteList = notes;
    mDateFormatter = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
    return;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View v;
    Note note;
    Date date;
    ViewHolder holder;




    v= convertView;

    if (v == null)
    {
      LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(R.layout.note_item, null);
      holder= new ViewHolder();
      holder.tvTitle= (TextView) v.findViewById(R.id.note_row_title);
      holder.tvContent = (TextView) v.findViewById(R.id.note_row_content);
      holder.tvDate= (TextView) v.findViewById(R.id.note_row_date);
      holder.tvTags= (TextView) v.findViewById(R.id.note_row_tags);
      v.setTag(holder);
    }
    else
    {
      holder= (ViewHolder)v.getTag();
    }

    note= mNoteList.get(position);





    if (note != null)
    {

      if (holder.tvTitle != null)
      {
        holder.tvTitle.setText(note.getTitle());
      }

      if(holder.tvContent != null)
      {
        if (note.getContent()==null)
        {
          holder.tvContent.setText("");
        }
        else
        {
          holder.tvContent.setText(note.getContent());
        }
      }

      if (holder.tvDate != null)
      {
        if (mShowCreatedDate==true)
        {
          date= new Date(note.getDateCreated());
          holder.tvDate.setText("Created: " + mDateFormatter.format(date));
        }
        else  //show modified date
        {
          date= new Date(note.getDateModified());
          holder.tvDate.setText("Mod: " + mDateFormatter.format(date));
        }
      }
      if (holder.tvTags != null)
      {
        List<String> tagList= Tag.stringToList(note.getTags());

        String tags="";// "Tags: ";

        for (int i=0; i<tagList.size(); i++)
        {
          tags= tags + "'" +tagList.get(i)+ "' ";
        }

        //todo: DEBUG
        holder.tvTags.setText(tags+ "noteId: "+note.getGuid());

      }

    }
    return v;
  }



  public void setShowCreatedDate(boolean showIt)
  {
    mShowCreatedDate = showIt;
    return;
  }



  private class ViewHolder
  {
    TextView tvTitle;
    TextView tvContent;
    TextView tvDate;           //displays modified date or created date
    TextView tvTags;

  }





}    ///////////END CLASS//////////////

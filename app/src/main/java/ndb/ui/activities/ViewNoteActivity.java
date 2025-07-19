package ndb.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import ndb.db.NDBTableMaster;
import ndb.db.NoteDataSource;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.NdbIntent;
import ndb.types.Note;
import ndb.types.Notebook;


/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 17/03/13
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class ViewNoteActivity extends Activity
{

  /**
   * Views
   */
  TextView  tvTitle,
            tvContent,
            tvTags;

  ActionBar actionBar;


  /**
   * Other
   */
  private String mDbFilename;
  Note mNote;
  Notebook mNotebook;

  NoteDataSource mDatasource;


  public void onCreate(Bundle savedInstanceState)
  {
    Intent intent;


    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_note_activity);

    intent= getIntent();


    mDbFilename = getIntent().getStringExtra(NdbIntent.DATABASE_NAME);
    if (mDbFilename == null)
    {
      mDbFilename = NDBTableMaster.NOTES_DB;
    }


    mDatasource = new NoteDataSource(this, mDbFilename);
    mDatasource.open(); //opens DB

    mNote= mDatasource.getNote(intent.getLongExtra(Note.GUID_EXTRA, 2834846));


    tvTitle= (TextView)findViewById(R.id.title_textView);
    tvContent= (TextView)findViewById(R.id.content_textView);
    tvTags= (TextView)findViewById(R.id.tags_textView);

    tvTitle.setText(mNote.getTitle());
    tvContent.setText(mNote.getEditableContent());
    tvTags.setText(mNote.getTags());

    // gets the activity's default ActionBar
    actionBar = getActionBar();
    actionBar.setTitle("View note");
    actionBar.show();
    // set the app icon as an action to go home
    // we are home so we don't need it
    // actionBar.setDisplayHomeAsUpEnabled(true);



    return;
  }




  @Override
  public void onResume()
  {
    super.onResume();
    mDatasource.open();
    return;
  }


  @Override
  public void onPause()
  {
    super.onPause();
    mDatasource.close();       //todo is this an issue when a background process is working on db?
    return;
  }



  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK)
    {

      mDatasource.open();
      mNote= mDatasource.getNote(mNote.getGuid()); //reload note from DB to see changes

      //update activity based on result
      tvTitle.setText(mNote.getTitle());
      tvContent.setText( mNote.getEditableContent());
      tvTags.setText(mNote.getTags());

    }


    return;
  }


  @Override     //this should be in all activities for ActionBar i believe
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // use an inflater to populate the ActionBar with items
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.view_note_menu, menu);

    return true;

  }

  @Override                //listener for ActionBar... maybe other menus too?
  public boolean onOptionsItemSelected(MenuItem item)
  {
    Intent intent;

    switch (item.getItemId())
    {


      case R.id.edit_option:
        intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(Note.GUID_EXTRA,mNote.getGuid());
        intent.putExtra(NdbIntent.ACTION_TYPE,NdbIntent.EDIT);
        intent.putExtra(NdbIntent.VIEW_TYPE,NdbIntent.NOTE_VIEW);

        if (mNotebook != null)
        {
          intent.putExtra(Notebook.GUID_EXTRA, mNotebook.getGuid());
        }

        //todo: probably need to send name of current notebook so can add note to it
        intent.putExtra(NdbIntent.DATABASE_NAME, mDbFilename);
        startActivityForResult(intent, 1);  //todo: find out more about result code
        break;


      default:
        Toast.makeText(this, "Unhandled menu press", Toast.LENGTH_SHORT).show();

        break;
    }

    return true;
  }




}        /////////////////END CLASS//////////////////
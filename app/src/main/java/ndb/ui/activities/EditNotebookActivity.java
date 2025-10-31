package ndb.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import ndb.db.NDBTableMaster;
import ndb.db.NoteDataSource;
//import kanana.notesdatabase.db.NotebookDataSource;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.NdbIntent;
import ndb.types.Notebook;
//import net.margaritov.preference.colorpicker.ColorPickerDialog;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 12/03/13
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditNotebookActivity extends Activity
{
  /**
   * Dialog IDs
   */
  private static final int  DIALOG_COLOR_PICKER= 1;


  /**
   * Views
   */
  private ActionBar mActionBar;
  EditText etTitle;
  Spinner spNotebookViewType;
  Button bNotebookColor;
  CheckBox cbDefaultRollover;
  LinearLayout ll_en_wrapper;


  /**
   * Listeners
   */
  AmbilWarnaDialog.OnAmbilWarnaListener mAmbilWarnaListener;          //Color picker dialog


  /**
   * Other
   */
  private NoteDataSource mDatasource;
  private Notebook mNotebook;
  private Intent mIntent;
  private String mDbFilename;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    //Intent intent;
    String action;
    //Notebook notebook;


    super.onCreate(savedInstanceState);

    setContentView(R.layout.edit_notebook_activity);
    etTitle = (EditText)findViewById(R.id.newNotebookEditText);
    spNotebookViewType = (Spinner)findViewById(R.id.notebook_view_type_spinner);
    bNotebookColor= (Button)findViewById(R.id.notebook_color_button);
    cbDefaultRollover= (CheckBox) findViewById(R.id.checkbox_default_rollover);

    ll_en_wrapper= (LinearLayout) findViewById(R.id.ll_en_wrapper);

    /// ///////////////////////////////////////
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

    ll_en_wrapper= (LinearLayout)findViewById(R.id.ll_en_wrapper);

    /////////////// Other WindowInsets code after IDs assigned/////////////////
    ViewCompat.setOnApplyWindowInsetsListener(ll_en_wrapper, (v, insets) -> {
      Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

      // Apply padding to the content layout to avoid system bars
      // changed to hopefully work :P
      v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);

      /// /////////////////////////////////////////////////////////////////////
      return insets;
    });



    spNotebookViewType.setAdapter(new ArrayAdapter<Notebook.ViewType>(this, android.R.layout.simple_spinner_item, Notebook.ViewType.values()));


    mDbFilename = getIntent().getStringExtra(NdbIntent.DATABASE_NAME);
    if (mDbFilename == null)
    {
      mDbFilename = NDBTableMaster.NOTES_DB;
    }

    mDatasource = new NoteDataSource(this, mDbFilename);
    mDatasource.open(); //opens DB

    mActionBar = getActionBar();
    mIntent= getIntent();
    action= mIntent.getStringExtra(NdbIntent.ACTION_TYPE);


    /**
     * EDIT NOTEBOOK
     */
    if (action.equals(NdbIntent.EDIT_NOTEBOOK))
    {
      //notebook= mIntent.getParcelableExtra(Notebook.OBJECT);
      mNotebook= mDatasource.getNotebook(mIntent.getLongExtra(Notebook.GUID_EXTRA, 1256899696)); //todo if this number is ever used then there's an error!!!!!!
      etTitle.setText(mNotebook.getTitle());
      spNotebookViewType.setSelection(mNotebook.getViewType().toInt());
      cbDefaultRollover.setChecked(mNotebook.isDefaultRollover());
      mActionBar.setTitle("Edit Notebook");
    }

    /**
     * CREATE NOTEBOOK
     */
    else if (action.equals(NdbIntent.CREATE_NOTEBOOK))
    {
      spNotebookViewType.setSelection(Notebook.ViewType.NORMAL.toInt());
      mNotebook= new Notebook();
      mActionBar.setTitle("Create Notebook");
         //do nothing
    }




    bNotebookColor.setBackgroundColor(mNotebook.getHeaderColor());

    setupListeners();

    return;
  }



  private void setupListeners()
  {
    /**
     * Launch ColorPickerDialog
     */
    bNotebookColor.setOnClickListener(new View.OnClickListener()
              {
                @Override
                public void onClick(View v)
                {
                  // initialColor is the initially-selected color to be shown
                  // in the rectangle on the left of the arrow.
                  // for example, 0xff000000 is black, 0xff0000ff is blue.
                  // Please be aware of the initial 0xff which is the alpha.
                  AmbilWarnaDialog dialog = new AmbilWarnaDialog(EditNotebookActivity.this, mNotebook.getHeaderColor(), mAmbilWarnaListener);
                  dialog.show();
                  return;

                  //showDialog(DIALOG_COLOR_PICKER);
                  //colorPickerDialog= new AmbilWarnaDialog(getApplicationContext(), mNotebook.getHeaderColor(), mAmbilWarnaListener);
                  //colorPickerDialog.
                  //colorPickerDialog.show();
                }
              });




    /**
     * OnColorChangeListener
     */
    mAmbilWarnaListener= new AmbilWarnaDialog.OnAmbilWarnaListener()
              {
                @Override
                public void onCancel(AmbilWarnaDialog dialog)
                {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color)
                {
                  mNotebook.setHeaderColor(color);
                  mDatasource.updateNotebook(mNotebook);
                  updateUi();
                  return;
                }
              };
    //colorPickerDialog = new AmbilWarnaDialog(this, mNotebook.getHeaderColor(), mAmbilWarnaListener);



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



  public void onClick(View view)
  {
    //Notebook notebook= null;
    Intent intent;
    String action;

    intent= new Intent();


    action= mIntent.getStringExtra(NdbIntent.ACTION_TYPE);


    switch (view.getId())
    {
      case R.id.okButton:
        //mDatasource= new NoteDataSource(this);



        if (action.equals(NdbIntent.CREATE_NOTEBOOK))
        {
          Toast.makeText(this, "save new notebook", Toast.LENGTH_SHORT).show();
          mNotebook= new Notebook();
          mNotebook.setTitle( etTitle.getText().toString() );
          mNotebook.setViewType(Notebook.ViewType.fromInt(spNotebookViewType.getSelectedItemPosition()));
          mNotebook.setDefaultRollover(cbDefaultRollover.isChecked());
          mNotebook = mDatasource.createNotebook(mNotebook);              //todo update with all notebook fields

        }
        else if (action.equals(NdbIntent.EDIT_NOTEBOOK))
        {
          //notebook= mDatasource.getNotebook(mNotebook);//mIntent.getParcelableExtra(Notebook.OBJECT);
          mNotebook.setTitle( etTitle.getText().toString() );
          mNotebook.setViewType( Notebook.ViewType.fromInt(spNotebookViewType.getSelectedItemPosition()) );
          mNotebook.setDefaultRollover(cbDefaultRollover.isChecked());
          mNotebook= mDatasource.updateNotebook(mNotebook);
        }

        intent.putExtra(NdbIntent.ACTION_TYPE, action);
        intent.putExtra(Notebook.GUID_EXTRA, mNotebook.getGuid());//intent.putExtra(Notebook.OBJECT, notebook);
        setResult(RESULT_OK, intent);

        finish();  //probably can be moved to end

        break;

      case R.id.cancelButton:
        setResult(RESULT_CANCELED, intent); //intent is emnpty

        finish();


      break;


    }


    return;
  }


  public void updateUi()
  {
    bNotebookColor.setBackgroundColor(mNotebook.getHeaderColor());
    return;
  }

  /**
   * todo: move all Notebook saving code here
   */
  private void saveNotebook()
  {
    return;
  }



}                 ////END CLASS////
package ndb.ui.activities;

import android.app.*;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;

import ndb.db.NDBTableMaster;
import ndb.db.NoteDataSource;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.*;
import ndb.ui.adapters.NotebookSpinnerAdapter;
import ndb.ui.bottombar.BottomBar;
import ndb.ui.dialogs.DueDateDialog;
import ndb.ui.views.MockMenuItem;
import ndb.ui.views.RichEditText;
import ndb.ui.views.TagsControl;
import ndb.util.DateUtil;
import ndb.util.Util;

import java.util.*;



// For Window insets COPIED

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 09/03/13
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 *
 * For intents deals with cases:
 *            NdbIntent.CREATE_NOTE
 *            NdbIntent.EDIT_NOTE
 *            NdbIntent.EDIT_TODO_NOTE       - not fully implemented
 *
 * KEYS in intent:
 * NdbIntent.ACTION_TYPE
 * NdbIntent.VIEW_TYPE
 *
 * Note.GUID_EXTRA
 * Notebook.GUID_EXTRA
 * Note.DATE_DUE
 * NdbIntent.DATABASE_NAME
 *
 *
 *
 * TODO: Break down intents into mutiple parts
 * 1) EDIT or CREATE                                   -mActionType
 * 2) NOTE or TODO                                     -mViewType
 * Save these as member variables so can be changed as needed (eg. a new note needs to be saved before
 * media can be added. For this case changing CREATE to EDIT makes things so much simpler)
 *
 *
 *
 *
 * EditNoteActivity isloaded by
 * 1)NoteListActivity
 * 2)TodoListActivity
 * 3)GroceryListActivity
 *
 *
 *
 *
 *
 * TODO: REFACTORING
 *Private methods to add
 * populateActivity fields() - takes mNote and uses it to populate all views (whether on screen or not) in activity
 *
 *
 *
 *
 */
public class EditNoteActivity extends  Activity  //todo: AppCompatActivity just causes CRASH on load :P
{
  static private String TAG= "EditNoteActivity";


  /**
   * Context menu
   */
  final int CONTEXT_MENU_DELETE= 0;



  /**
   * Dialogs
   */
  static final int  DIALOG_CANCEL_CHANGES= 0,
                    DIALOG_DUE_DATE = 1;;

  private DueDateDialog mDueDateDialog;


  /**
   * Intent requestCodes
   */
  static final int SELECT_PICTURE= 10;


  /**
   * GUI Views
   */
  private ActionBar mActionBar;
  private EditText etTitle;
  private RichEditText retContent;
  private LinearLayout llTodoItems;
  private TextView tvDueDate;
  private CheckBox  cbCompleted,
                    cbDateDueSticky;

  private Spinner spNotebook,
                  spPriority;

  private TagsControl tcTags;
  private LinearLayout llImages;
  private Button bAddImage;
  private BottomBar bbEditNote;
  private LinearLayout ll_note_wrapper; //outermost layout of note. todo: use for WindowInsets


  //HACK
  private ImageView mLastImageViewLongClicked= null;      //for context menu



  /**
   * View Accessories
   */
  private ArrayAdapter mNotebookTitleSpinnerAdapter;
  private List<Notebook> mNotebookList;

  private View.OnClickListener imageOnClickListsner;
  private View.OnLongClickListener imageOnLongClickListener;
  ;



  /**
   * Listeners
   */
  private DueDateDialog.DueDateDialogListener mDueDateDialogListener;


  /**
   * Other
   */
  private Notebook mNotebook;
  private Note mNote;
  private Note mOriginalNote;           //used to check if note contents changed and thus whether to update modifiedDate
  private boolean mMediaChanged= false; //used to check if note contents changed and thus whether to update modifiedDate
  private NoteDataSource mDatasource;
  private String mDbFilename;
  private Intent mIntent;

  /**
   * Taken fron intent
   */
  private String  mActionType,
                  mViewType;


  /**
   * Misc control variables
   */
  private boolean mIsActivityClosing= false;



  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    //Intent intent;
    //String actionType;
    boolean found;
    List<Media> mediaList;
    String actionBarTitle;
    Note temporaryNote;               // unsaved note from last instance
    long noteId;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_note_activity);


  /////////////////
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false); //todo:supposed to enable edge to edge display??? yes!
    ///////////////
      /// ////////////////////////////////////////////


    retContent = (RichEditText)findViewById(R.id.note_content);
    etTitle= (EditText)findViewById(R.id.note_title);
    tcTags= (TagsControl)findViewById(R.id.tags_control);
    bbEditNote= (BottomBar)findViewById(R.id.editnote_bottombar);
    tvDueDate= (TextView)findViewById(R.id.todo_due_date);
    llTodoItems= (LinearLayout)findViewById(R.id.todo_items_layout);
    cbCompleted= (CheckBox)findViewById(R.id.checkbox_completed);
    cbDateDueSticky = (CheckBox)findViewById(R.id.checkbox_stickyDateDue);
    spNotebook= (Spinner)findViewById(R.id.notebook_spinner);
    spPriority= (Spinner)findViewById(R.id.priority_spinner);
    llImages= (LinearLayout)findViewById(R.id.images_layout);
    bAddImage= (Button)findViewById(R.id.add_image_button);

    bbEditNote.setVisibility(View.GONE);


    mActionBar = getActionBar();
    mActionBar.setDisplayHomeAsUpEnabled(true);

    ll_note_wrapper= (LinearLayout)findViewById(R.id.ll_note_wrapper);

      /////////////// Other WindowInsets code after IDs assigned/////////////////
      ViewCompat.setOnApplyWindowInsetsListener(ll_note_wrapper, (v, insets) -> {
                Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                // Apply padding to the content layout to avoid system bars
                // changed to hopefully work :P
                v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);

                /// /////////////////////////////////////////////////////////////////////
      return insets;
              });


    /**
     * HANDLE INTENTS - REFACTORED!!!!!!!!!!!!!!!!!!
     */
    mIntent = getIntent();
    mViewType=   mIntent.getStringExtra(NdbIntent.VIEW_TYPE);
    mDbFilename = mIntent.getStringExtra(NdbIntent.DATABASE_NAME);
    mActionType= mIntent.getStringExtra(NdbIntent.ACTION_TYPE);




    /**
     * todo: this is STUPID!!!!!!!!         ***HACK***
     * mIntent.getStringExtra(NdbIntent.VIEW_TYPE); WORKS IN PRODUCTION CODE BUT RETURNS NULL in Robotium testing
     * even though this extra is in the intent in the debugger
     */
    if (mViewType == null)
    {
      mViewType= NdbIntent.NOTE_VIEW;
    }


    /**
     * Open DB
     */

    if (mDbFilename == null)
    {
      mDbFilename = NDBTableMaster.NOTES_DB;
    }

    mDatasource = new NoteDataSource(this, mDbFilename);
    mDatasource.open(); //opens DB
    temporaryNote= mDatasource.getTemporaryNote();




    if (mActionType.equals(NdbIntent.EDIT))    //assert note already made and has valid nbGuid
    {
      /**
       * NdbIntent.EDIT
       */
      actionBarTitle= "Edit ";
      //mActionBar.setTitle("Edit note");
      if (temporaryNote == null)
      {
        mNote= mDatasource.getNote(mIntent.getLongExtra(Note.GUID_EXTRA, 374647));
      }
      else
      {
        mNote= temporaryNote;
      }

      mNotebook= mDatasource.getNotebook(mNote.getNbGuid());
      etTitle.setText(mNote.getTitle().trim());
      retContent.setText(mNote.getContent(), mNote.getSpansAsJson());
      tcTags.setTags(mNote.getTags());
    }
    else if (mActionType.equals(NdbIntent.CREATE)) //assert note doesn't exist. need get nbGuid from notebook
    {
      /**
       * NdbIntent.CREATE
       */
      actionBarTitle= "New ";

      if (temporaryNote == null)
      {
        mNote= new Note();
        mNote.setDateDue(mIntent.getLongExtra(Note.DATE_DUE, 0));               //todo note: date due is 0 if nothing is passed. Maybe make it -1?

      }
      else
      {
        mNote= temporaryNote;
        etTitle.setText(mNote.getTitle().trim());                                 //todo: probably can be refactored to a common case
        retContent.setText(mNote.getContent(), mNote.getSpansAsJson());
        tcTags.setTags(mNote.getTags());
      }

//      if (mIntent.getLongExtra(Notebook.GUID_EXTRA, -1) != -1)
//      {  //assert Notebook.GUID_EXTRA exists. If not mNote should already have a valid nbGuid
//
//        mNote= mDatasource.getTemporaryNote();                                //TODO: TEMP FIX BUT LOGIC STILL MESSED. DO ALL CASES!!!!

        mNote.setNbGuid(mIntent.getLongExtra(Notebook.GUID_EXTRA, mNote.getNbGuid()));       //default case should never fire. Is deprecated temporary note case
                                                                                                 //todo should be long be Robotium test won't recognize it!!! Only recognizes int!!!!!!!
//      }
      mNotebook= mDatasource.getNotebook(mNote.getNbGuid());


      mNote.setRolloverIfNotCompleted(mNotebook.isDefaultRollover());


      etTitle.requestFocus();
    }
    else
    {
      throw new RuntimeException("EditNoteActivity - unknown intent. case is not handled");
    }

    /**
     * Temporary note case - change GUID to whats its supposed to be
     * todo: I believe this case should never fire as NoteDatasource handles this
     */
    if (mNote.getGuid() == Note.TEMPORARY_NOTE_ID)        //TODO: THIS CODE IS WRONG. FIX!!!!!!!
    {
      mNote.setGuid(mDatasource.getTemporaryNote().getGuid());
      mOriginalNote= mDatasource.getNote(mDatasource.getTemporaryNote().getGuid());
    }
    else  //a note other than temporary note is being loaded
    {
      mOriginalNote= mNote.clone();           //TODO: WRONG!!!!!! THIS CASE STILL RUNS IF TEMPORARY NOTE IS LOADED
    }




    if (mViewType.equals(NdbIntent.NOTE_VIEW))
    {
      actionBarTitle= actionBarTitle+"note";
    }
    else if ((mViewType.equals(NdbIntent.TODO_VIEW)))
    {
      actionBarTitle= actionBarTitle+"todo";
      llTodoItems.setVisibility(View.VISIBLE);
    }
    else if (mViewType.equals(NdbIntent.GROCERY_VIEW))
    {
      actionBarTitle= actionBarTitle+"grocery item";
      llTodoItems.setVisibility(View.VISIBLE); //todo: should this be visible?
    }
    else
    {
      //todo make a more graceful exit... app starts working when a temporary saved note is not handled
      throw new RuntimeException("EditNoteActivity - unknown intent. No view specified");
    }




    mActionBar.setTitle(actionBarTitle);


    /**
     * Set ActionBar color
     */
    mNotebook= mDatasource.getNotebook(mNote.getNbGuid());
    mActionBar= getActionBar();
    if (mNotebook.getHeaderColor() != 0)
    {
      mActionBar.setBackgroundDrawable(new ColorDrawable(mNotebook.getHeaderColor()));
      bbEditNote.setBackgroundColor(mNotebook.getHeaderColor());
    }

    /**
     * Populate these values in all cases
     * (even if views are not visible. It makes saving less error prone)
     */
    cbCompleted.setChecked(mNote.isCompleted());
    cbDateDueSticky.setChecked(mNote.isRolloverIfNotCompleted());
    if (mNote.getDateDue()>1)
    { //assert valid due date

      String dueDateString= DateUtil.formatDate(new Date(mNote.getDateDue()));
      if (mNote.getRepeatingField()>0)
      {
        dueDateString= dueDateString+"*";
      }
      tvDueDate.setText(/*DateUtil.formatDate(new Date(mNote.getDateDue()))*/dueDateString);
    }
    else
    {
      tvDueDate.setText("None");
    }


    /**
     * Set Notebook spinner
     */
    mNotebookList= mDatasource.getAllNotebooks(Notebook.TITLE);
    mNotebookTitleSpinnerAdapter = new NotebookSpinnerAdapter(this, R.layout.notebook_spinner_item, (ArrayList)mNotebookList, mDatasource);
    spNotebook.setAdapter(mNotebookTitleSpinnerAdapter);

    /**
     * Set Priority spinner
     */
    spPriority.setAdapter(new ArrayAdapter<Note.Priority>(this, /*android.R.layout.simple_spinner_item*/R.layout.spinner_item, Note.Priority.values()));
    spPriority.setSelection(mNote.getPriority().toInt());

    /**
     * set notebook in spinner
     * (is there a better way?)
     */
    found= false;
    for (int i=0; i<mNotebookList.size() && !found; i++)
    {
      if ( mNotebookList.get(i).getGuid() == mNote.getNbGuid() )
      {
        spNotebook.setSelection(i);
        found= true;
      }
    }

    tcTags.setTagsMasterList(mDatasource.getAllTags());
    mActionBar.show();          //need to change menus as they are wrong here!






    setupListeners();







    /**
     * Load images (listener must be set up for this to work)
     */
    mediaList=mDatasource.getMedia(mNote.getGuid());
    for (Media image : mediaList)
    {
      //TODO: must check mimeType to see if it is an image before loading!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      loadImage(image);
    }






    return;
  }




  private void setupListeners()
  {

    /**
     * Image listener - loads image in gallery
     * todo: If open image with Photos app -> "media not found" but
     *          with View Photos or Image Viewer-> works!!!
     *          I'm gonna guess this has something to do with the URI format used...
     *          Is this content URI valid??
     *          content://kanana.notesdatabase.mediaprovider/35.jpg
     *
     *          Tried adding permission <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
     *          but no change!
     */
    imageOnClickListsner= new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent;
        Uri uri;
        Media media;

        intent= new Intent();
        media= (Media)v.getTag();


        uri= Uri.parse(MediaProvider.CONTENT_URI+media.getFilename());

        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
        return;
      }
    };


    /**
     * Add images button
     */
    bAddImage.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Intent intent;

        intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

        return;
      }
    });
//    /**
//     * Image listener - LONG CLICK
//     */
//    imageOnLongClickListener= new View.OnLongClickListener()
//    {
//      @Override
//      public boolean onLongClick(View v)
//      {
//        /**
//         * pop up context menu!
//         */
//
//       return false;
//      }
//    };


    /**
     * Handles BottomBar events
     */
    bbEditNote.setOnClickListener(new BottomBar.OnClickListener()
              {
                @Override
                public void onClick(BottomBar bottomBar)
                {
                  Intent intent;

                  if (bbEditNote.getActionId() == BottomBar.INSERT_BULLET)
                  {
                    retContent.toggleBullet();
                  }
                  else if (bbEditNote.getActionId() == BottomBar.INSERT_IMAGE)
                  {
                    /**
                     * code moved to button in llImages
                     */
//                    intent= new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                  }

                  //////////////////////////////////////////////////////////////////////////////////////////////////////////
                 else if  (bbEditNote.getActionId() == BottomBar.TEST_BUTTON)
                  {
                        ////////THIS WORKS!!!///////////////TESTING///////////////////////////////////
    //                    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
    //                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  drawable.getIntrinsicHeight());
    //                    //SpannableString spannable = new SpannableString(getText().toString() + "[smile]");
    //                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
    //                    retContent.getText().setSpan(span, retContent.getText().length()-5, retContent.getText().length(),
    //                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);                        //FOR SOME REASON LENGTH-5 FIXED IT!!!!!
    //                    //setText(spannable);
                        ///////////////////////////////////////////////////////////////////////////////


                    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  drawable.getIntrinsicHeight());
                    //SpannableString spannable = new SpannableString(getText().toString() + "[smile]");
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    retContent.getText().setSpan(span, retContent.getText().length()-5, retContent.getText().length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);                        //FOR SOME REASON LENGTH-5 FIXED IT!!!!!

                  }


                  return;
                }

              });


    /**
     * Turns on BottomBar when retContent is focused
     */
    retContent.setOnFocusChangeListener(new View.OnFocusChangeListener()
              {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                  if (hasFocus == true)
                  {
                    bbEditNote.setVisibility(View.VISIBLE);
                  }
                  else
                  {
                    bbEditNote.setVisibility(View.GONE);
                  }
                  //To change body of implemented methods use File | Settings | File Templates.
                }
              });


    /**
     * Pops up dialog to edit due date
     */
    tvDueDate.setOnClickListener(new View.OnClickListener()
              {
                @Override
                public void onClick(View v)
                {
                  showDialog(DIALOG_DUE_DATE);
                }
              });


    /**
     * Due date changed listener
     */
    mDueDateDialogListener= new DueDateDialog.DueDateDialogListener()
              {

                /**
                 * Resets DueDateDialog fields to previous values (since cancel pressed)
                 */
                @Override
                public void onCancel()          // todo: this also needs to called after dialog dismiised (ie. when clicked outside of dialog box)
                {
                  Calendar cDueDate;                    //due date to be shown in DueDateDialog
                  cDueDate = Calendar.getInstance();    //calender initialized to current time



                  if (mNote.getDateDue() > 0)
                  {     //assert: note due date is valid so change calendar to that
                    cDueDate.setTimeInMillis(mNote.getDateDue());
                  }
                  mDueDateDialog.setDueDate(cDueDate.get(Calendar.YEAR), cDueDate.get(Calendar.MONTH), cDueDate.get(Calendar.DAY_OF_MONTH),
                          mNote.getRepeatingField(), mNote.getRepeatingValue());

                  return;
                }


                @Override
                public void onDueDateSet(int year, int monthOfYear, int dayOfMonth, int repeatingField, int repeatingValue)
                {
                  Calendar calendar= Calendar.getInstance();

                  calendar.set(Calendar.YEAR, year);
                  calendar.set(Calendar.MONTH, monthOfYear);
                  calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                  mNote.setDateDue(calendar.getTimeInMillis());
                  tvDueDate.setText(DateUtil.formatDate(calendar.getTime()));
                  mNote.setRepeatingDueDate(repeatingField, repeatingValue);
                  //new due date is not saved to db. That only happens when save or back button is pressed

                  return;
                }
              };


    return;
  }                       ////END SETUP LISTENERS////




  @Override
  public void onResume()
  {
    super.onResume();
    //mDatasource.open();
    return;
  }


  @Override
  public void onPause()
  {
    super.onPause();

    if (mIsActivityClosing == false)
    {
      /**
       * Save note values from activity in case of unexpected shutdown
       */
      mNote= activityToNote();      //todo: THIS LINE CAUSES THE THE PROBLEM!
      mDatasource.writeTemporaryNote(mNote);
    }



    return;
  }

  @Override
  public void onBackPressed()
  {
    //automatically saves note when back button pressed
    //todo still need to save note on activity onPause()
    MockMenuItem saveItem= new MockMenuItem(R.id.save_option);

    onOptionsItemSelected(saveItem);
    super.onBackPressed();

    return;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if (resultCode == RESULT_OK)
    {
      if (requestCode == SELECT_PICTURE)
      {
        Uri selectedImageUri = data.getData();

        //retContent.addImageAtCursor(selectedImageUri);
        addImage(selectedImageUri);

      }
    }
    return;
  }













  /**
   * Adds image to note.
   * - saves file to app directory
   * - save Media table record
   * @param imageUri
   */
  private void addImage(Uri imageUri)
  {
    Media media;
    ImageView imageView;
    Bitmap bitmap;
    String intentReason;
    ContentResolver contentResolver;
    MimeTypeMap mimeTypeMap;
    String fileExtension;


//    intentReason= mIntent.getStringExtra(NdbIntent.TYPE);
//    if (intentReason.equals(NdbIntent.CREATE_NOTE) || intentReason.equals(NdbIntent.CREATE_TODO_NOTE))
    if (mActionType.equals( NdbIntent.CREATE))
    {
      /**
       * This is hacked to create a new note if media is saved. It is needed because for a new note the GUID is 0
       */
      mNote.setGuid(mDatasource.getLargestNoteGuid()+1);
    }


    /**
     * todo: the proper check here is to check if it is a file or content URI
     * if content -> convert to path
     * if file -> don't need do anything
     */

    if (Util.doesFileExtensionExist(imageUri.toString()) == false) //then fix URI by adding file extension
    {
        imageUri= Uri.parse(Util.getRealPathFromURI(this, imageUri));
    }


    //todo add mimeType;
    media= mDatasource.createMedia(imageUri, mNote.getGuid());
    if (media != null)
    {
      loadImage(media);
      if (mActionType.equals(NdbIntent.CREATE))
      {
        //mNote= activityToNote();    //todo this breaks the note cancel functionality... but only for newly created notes so obscure bug
        mNote = mDatasource.createNote(mNote); //saveNote();
      }
      mMediaChanged= true;
    }
    else
    {
      Toast.makeText(this, "Error - image not added", Toast.LENGTH_SHORT).show();
    }



    return;
  }



  private void loadImage(Media mediaImage)
  {
    ImageView imageView;
    Bitmap bitmap;

    bitmap = BitmapFactory.decodeFile(/*mediaImage.getPath()*/mediaImage.getPreviewPath());      //WORKS... for small images.. not large?
    imageView = new ImageView(this);
    imageView.setImageBitmap(bitmap);
    imageView.setOnClickListener(imageOnClickListsner);
    imageView.setTag(mediaImage);
    registerForContextMenu(imageView);
    llImages.addView(imageView);

    //.todo: ADD LISTENER CODE HERE!!!!!!!!!
    return;
  }







  @Override         //Creates context menu
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
  {

    /**
     * for long click on image
     */
    if (v.getClass() == ImageView.class)  //todo: && v.getParent() ==  llImages && NOT THE ADD BUTTON lol
    {

      menu.add(Menu.NONE, CONTEXT_MENU_DELETE, 0, "delete");
      //menu.add(Menu.NONE, CONTEXT_MENU_EDIT, 1, "edit");
      mLastImageViewLongClicked= (ImageView)v;
    }

    return;
  }




  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    Media mediaImage;
//
//    AdapterView.AdapterContextMenuInfo info;
//    int rowId;
//    Note note;
//    boolean result;
//    Bundle argsBundle;
//
//    info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//    rowId = (int) mListView.getAdapter().getItemId(info.position);/*what item was selected is ListView*/


    switch (item.getItemId())      //ind out how getItemId() found. it has NOTHING to do with
    {



      case CONTEXT_MENU_DELETE:
        /**
         * delete image from db
         */
        mediaImage= (Media)mLastImageViewLongClicked.getTag();
        mDatasource.deleteMedia(mediaImage);
        /**
         * Delete from Activity
         *
         * C
         */
        llImages.removeView(mLastImageViewLongClicked);
        //mLastImageViewLongClicked.setVisibility(View.GONE);



      break;




      //      case CONTEXT_MENU_EDIT:
      //        //do smth else)
      //        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }





  @Override     //this should be in all activities for ActionBar i believe
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // use an inflater to populate the ActionBar with items


    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.note_menu, menu);
    //the following line should work here:
    //miSave= menu.findItem(R.id.save_option);
    return true;

  }


  /**
   * Handler for ActionBar items
   * @param item
   * @return
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    Intent returnIntent;

    switch (item.getItemId())
    {
      case R.id.save_option:
        saveNote();
        returnIntent= new Intent();
        setResult(RESULT_OK, returnIntent);
        mIsActivityClosing= true;
        setResult(RESULT_OK);
        finish();
        break;

      case R.id.cancel_option:
        showDialog(DIALOG_CANCEL_CHANGES);
        break;

      default:
        break;
    }


    return true;
  }


  /**
   * Creates dialogs
   * @param id
   * @return
   */
  @Override
  protected Dialog onCreateDialog(int id)
  {
    Calendar cDueDate;      //due date to be shown in DueDateDialog

    switch (id)
    {
      case DIALOG_CANCEL_CHANGES:
        // Create out AlterDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cancel changes?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                  {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                      mIsActivityClosing= true;
                      mDatasource.deleteTemporaryNote();          //will delete it if it exists. IF it doesn't exist all is good
                      setResult(RESULT_CANCELED);
                      finish();

                    }
                  });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        break;

      case DIALOG_DUE_DATE:
        cDueDate = Calendar.getInstance();    //calender initialized to current time

        if (mNote.getDateDue() > 1)
        {     //assert: note due date is valid so change calendar to that
          cDueDate.setTimeInMillis(mNote.getDateDue());
          Toast.makeText(this, "Calendar set to"+DateUtil.formatDate(cDueDate.getTime()), Toast.LENGTH_SHORT).show();

        }
        mDueDateDialog= new DueDateDialog(this);
        mDueDateDialog.setDueDateDialogListener(mDueDateDialogListener);
        mDueDateDialog.setDueDate(cDueDate.get(Calendar.YEAR), cDueDate.get(Calendar.MONTH), cDueDate.get(Calendar.DAY_OF_MONTH),
                mNote.getRepeatingField(), mNote.getRepeatingValue());

        return mDueDateDialog; //datePickerDialog;



    }
    return super.onCreateDialog(id);
  }



  /**
   * ASSUMPTION: Activity will close after this
   * Uses the data from on screen views to update mNote and then saves mNote back to the db
   */
  private void saveNote()
  {
    //Intent intent;
    //String intentReason;


    //intent= getIntent();
    //intentReason= intent.getStringExtra(NdbIntent.ACTION_TYPE);


     mNote= activityToNote();


    /**
     * Save the mNote to db
     *
     * note: if media is added to a new note, then that note has already been saved so EDIT_NOTE case must executed
     */

    /**
     * todo - CHANGE TO:                                                                             ******************************
     *  if note.id==0 (from Settings or is it -1????????) then we know to create a new note
     *  That way
     */

//    if ( (intentReason.equals(NdbIntent.CREATE_NOTE) && mMediaChanged == false) ||
//         (intentReason.equals(NdbIntent.CREATE_TODO_NOTE) && mMediaChanged == false)  )
    if (mActionType.equals(NdbIntent.CREATE) && mMediaChanged == false)
    {
      if ( !mNote.getTitle().equals("") || !mNote.getContent().equals("") || mMediaChanged == true)         //only save note if title or content are non-empty
      {
        mNote = mDatasource.createNote(mNote); //CREATE NOTE MUST BE EDITED SO CAN SHOW NOTEBOOK ID!!!
      }
    }

    //else if ( intentReason.equals(NdbIntent.EDIT_NOTE) || intentReason.equals(NdbIntent.EDIT_TODO_NOTE) || mMediaChanged == true)
    else if (mActionType.equals(NdbIntent.EDIT) || mMediaChanged == true)
    {         //assert note already exists so will be updated... actually i think SQL can handle fboth these cases the same way :)

      /**
       *   Determines which fields make modifiedDate change
       */
      if ( mOriginalNote.getTitle().equals(mNote.getTitle()) &&
           mOriginalNote.getSpansAsJson().equals(mNote.getSpansAsJson()) &&
           mOriginalNote.getContent().equals(mNote.getContent()) &&
           mOriginalNote.getNbGuid() == mNote.getNbGuid() &&
           mOriginalNote.getTags().equals(mNote.getTags()) &&
           mMediaChanged == false )
      { //assert: modifiedDate will not be modified
        mNote = mDatasource.updateNote(mNote, false);
      }
      else
      { //assert: update modifiedDate
        mNote = mDatasource.updateNote(mNote, true);
      }

    }
    else
    {
      Toast.makeText(this, "save - unhandled case. note database not updated", Toast.LENGTH_SHORT).show();
    }

    mDatasource.deleteTemporaryNote();

    return;
  }

  /**
   * Combines mNote with fields from activty to create a new note
   * @return
   */
  public Note activityToNote()
  {
    Note tempNote;
    tempNote= mNote.clone();

    /**
     * fill mNote with data to be saved
     */
    tempNote.setTitle(etTitle.getText().toString());
    tempNote.setContent(retContent.getText().toString());
    tempNote.setSpansAsJson(retContent.getSpansAsJson());
    tempNote.setTags(tcTags.getTags());


    tempNote.setCompleted(cbCompleted.isChecked());
    tempNote.setRolloverIfNotCompleted(cbDateDueSticky.isChecked());
    tempNote.setNbGuid(((Notebook) spNotebook.getSelectedItem()).getGuid());
    tempNote.setPriority(Note.Priority.fromLong(spPriority.getSelectedItemId()));
    //note: mNote.dueDate updated by the DateSpinner so that case is already handled


    return tempNote;
  }

  /** todo: this method is to load Activity state back from
   * Loads the activity with the given note
   * @param note
   */
  private void noteToActivity(Note note)
  {

  }




}      /////////////////end class////////////////
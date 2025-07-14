package ndb.ui.bottombar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import ndb.R; //kanana.notesdatabase.R;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 21/04/13
 * Time: 8:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditNoteBottomBar  extends BottomBar
{
    protected ImageButton ibBullet,
                          ibCamera,
                          ibTest;     //todo: delete when testing finished!



  public EditNoteBottomBar(Context context)
  {

    super(context);
    return;
  }

  public EditNoteBottomBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    return;
  }

  public EditNoteBottomBar(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    return;
  }




  @Override
  protected void initialize()
  {

    LayoutInflater layoutInflater;

    layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (layoutInflater != null)
    {
      mView = layoutInflater.inflate(R.layout.bottombar_edit_note, this);
    }

    mActionType= ACTION_NONE;   //default initialization
    ibBullet = (ImageButton)findViewById(R.id.bullet_button);
    ibCamera= (ImageButton)findViewById(R.id.camera_button);
    ibTest= (ImageButton)findViewById(R.id.test_button);

    setupListeners();
    return;
  }




  protected void setupListeners()
  {
    //////////////////////////////////////////////////////////////////
    ibBullet.setOnClickListener(new View.OnClickListener()
              {
                @Override
                public void onClick(View v)
                {
                  Toast.makeText(mContext.getApplicationContext(), "add bulletspan", Toast.LENGTH_SHORT).show();
                  mActionId= INSERT_BULLET;
                  mbbOnClickListener.onClick(EditNoteBottomBar.this);
                  return;
                }
              });


    ibCamera.setOnClickListener(new View.OnClickListener()
              {
                @Override
                public void onClick(View v)
                {
                  Toast.makeText(mContext.getApplicationContext(), "camera button", Toast.LENGTH_SHORT).show();
                  mActionId= INSERT_IMAGE;
                  mbbOnClickListener.onClick(EditNoteBottomBar.this);
                  return;
                }
              });

    ibTest.setOnClickListener(new View.OnClickListener()
              {
                @Override
                public void onClick(View v)
                {
                  Toast.makeText(mContext.getApplicationContext(), "test button", Toast.LENGTH_SHORT).show();
                  mActionId= TEST_BUTTON;
                  mbbOnClickListener.onClick(EditNoteBottomBar.this);
                  return;
                }
              });

    //////////////////////////////////////////////////////////////////
    //Set listener for action item clicked
//    qaSort.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
//    {
//      @Override
//      public void onItemClick(QuickAction source, int pos, int actionId)
//      {
//
//        mActionId= actionId;
//
//
//        if (actionId == SORT_TITLE_ASCENDING  || actionId == SORT_TITLE_DESCENDING ||
//                actionId == SORT_DATE_MODIFIED    || actionId == SORT_DATE_CREATED     ||
//                actionId == SORT_DATE_DUE)
//        {   //assert its a sort action
//          mActionType= SORT_ACTION;
//        }
//        //else if //todo other cases here:
//        else
//        {
//          mActionType= NO_ACTION;
//        }
//
//
//        if ( mbbOnClickListener != null)
//        {
//          mbbOnClickListener.onClick(NoteListBottomBar.this);
//        }
//      }
//    });
//    //////////////////////////////////////////////////////////////////
//
//    return;
//
    }




}     /////////////END CLASS///////////

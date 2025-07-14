package ndb.ui.bottombar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import ndb.R; //kanana.notesdatabase.R;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 16/04/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotebookListBottomBar extends BottomBar
{
  protected ImageButton ibMoveFolders,
                        ibSync;

  private boolean ibMoveFoldersNormalImage= true; // a hack to change ibMoveFolders icon

  public NotebookListBottomBar(Context context)
  {

    super(context);
    return;
  }

  public NotebookListBottomBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    return;
  }

  public NotebookListBottomBar(Context context, AttributeSet attrs, int defStyle)
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
      mView = layoutInflater.inflate(R.layout.bottombar_notebook_list, this);
    }

    mActionType= ACTION_NONE;   //default initialization
    ibMoveFolders= (ImageButton)findViewById(R.id.move_folders_button);
    ibSync= (ImageButton)findViewById(R.id.sync_button);
//
//    /////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    //todo these must be moved to subcl
//    // ass
//    ActionItem nextItem 	= new ActionItem(SORT_TITLE_ASCENDING, "Ascending", getResources().getDrawable(net.londatiga.android.R.drawable.menu_down_arrow));
//    ActionItem prevItem 	= new ActionItem(SORT_TITLE_DESCENDING, "Descending", getResources().getDrawable(net.londatiga.android.R.drawable.menu_up_arrow));
//    ActionItem searchItem 	= new ActionItem(SORT_DATE_MODIFIED, "Modified Date", getResources().getDrawable(net.londatiga.android.R.drawable.menu_search));
//    ActionItem infoItem 	= new ActionItem(SORT_DATE_CREATED, "Date Created", getResources().getDrawable(net.londatiga.android.R.drawable.menu_info));
//    //    ActionItem eraseItem 	= new ActionItem(ID_ERASE, "Clear", getResources().getDrawable(net.londatiga.android.R.drawable.menu_eraser));
//    //    ActionItem okItem 		= new ActionItem(ID_OK, "OK", getResources().getDrawable(net.londatiga.android.R.drawable.menu_ok));
//
//    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
//    prevItem.setSticky(true);
//    nextItem.setSticky(true);
//
//    //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
//    //orientation
//    qaSort = new QuickAction(mContext, QuickAction.VERTICAL);
//
//
//    //add action items into QuickAction
//    qaSort.addActionItem(nextItem);
//    qaSort.addActionItem(prevItem);
//    qaSort.addActionItem(searchItem);
//    qaSort.addActionItem(infoItem);
//    //    qaSort.addActionItem(eraseItem);
//    //    qaSort.addActionItem(okItem);
//
//
//
//
//
//    //set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
//    //by clicking the area outside the dialog.
//
//
//    qaSort.setOnDismissListener(new QuickAction.OnDismissListener()
//    {
//      @Override
//      public void onDismiss()
//      {
//        //       Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
//      }
//    });
//
//
//
//
//
//
//    /////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /////////////////////////////////////////////////////////////////////////////////////////////////////////
//


    setupListeners();
    return;
  }




  protected void setupListeners()
  {
    //////////////////////////////////////////////////////////////////
    ibMoveFolders.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Drawable normalImage,
                 alternateImage;

        mActionId= MOVE_FOLDERS;


        //Change icon when image button pressed
        ibMoveFolders.getDrawable();
        normalImage= getResources().getDrawable(R.drawable.ic_move_folder);

        if ( ibMoveFoldersNormalImage == true )
        {
          alternateImage= getResources().getDrawable(R.drawable.ic_move_folder_red);
          ibMoveFolders.setImageDrawable(alternateImage);
          ibMoveFoldersNormalImage= false;
        }
        else
        {
          normalImage= getResources().getDrawable(R.drawable.ic_move_folder);
          ibMoveFolders.setImageDrawable(normalImage);
          ibMoveFoldersNormalImage= true;
        }

        ibMoveFolders.invalidate();

        mbbOnClickListener.onClick(NotebookListBottomBar.this);
      }
    });

    //////////////////////////////////////////////////////////////////

    ibSync.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        mActionId= SYNC;
        mbbOnClickListener.onClick(NotebookListBottomBar.this);
      }
    });







    //    //Set listener for action item clicked
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
//        mbbOnClickListener.onClick(NoteListBottomBar.this);
//      }
//    });
//    //////////////////////////////////////////////////////////////////

    return;

  }




}     /////////////END CLASS///////////


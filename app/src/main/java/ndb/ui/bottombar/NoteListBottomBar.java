package ndb.ui.bottombar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import ndb.R; //kanana.notesdatabase.R;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 16/04/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoteListBottomBar extends BottomBar
{


  protected ImageButton ibEditNotebook,
                        ibSort;

  protected QuickAction qaSort;





  public NoteListBottomBar(Context context)
  {

    super(context);
    return;
  }

  public NoteListBottomBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    return;
  }

  public NoteListBottomBar(Context context, AttributeSet attrs, int defStyle)
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
      mView = layoutInflater.inflate(R.layout.bottombar_note_list, this);
    }

    mActionType= ACTION_NONE;   //default initialization
    ibSort= (ImageButton)findViewById(R.id.todo_sort_button);
    ibEditNotebook= (ImageButton)findViewById(R.id.edit_notebook_button);

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    ActionItem ascendingSortItem = new ActionItem(SORT_TITLE_ASCENDING, "Ascending", getResources().getDrawable(/*net.londatiga.android*/ndb.R.drawable.menu_down_arrow));
    ActionItem descendingSortItem = new ActionItem(SORT_TITLE_DESCENDING, "Descending", getResources().getDrawable(/*net.londatiga.android*/ndb.R.drawable.menu_up_arrow));
    ActionItem modifiedDateItem = new ActionItem(SORT_DATE_MODIFIED, "Modified Date", getResources().getDrawable(/*net.londatiga.android*/ndb.R.drawable.menu_search));
    ActionItem dateCreatedItem = new ActionItem(SORT_DATE_CREATED, "Date Created", getResources().getDrawable(/*net.londatiga.android.*/ndb.R.drawable.menu_info));
//    ActionItem eraseItem 	= new ActionItem(ID_ERASE, "Clear", getResources().getDrawable(net.londatiga.android.R.drawable.menu_eraser));
//    ActionItem okItem 		= new ActionItem(ID_OK, "OK", getResources().getDrawable(net.londatiga.android.R.drawable.menu_ok));

    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
    descendingSortItem.setSticky(true);
    ascendingSortItem.setSticky(true);

    //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
    //orientation
    qaSort = new QuickAction(mContext, QuickAction.VERTICAL);


    //add action items into QuickAction
    qaSort.addActionItem(ascendingSortItem);
    qaSort.addActionItem(descendingSortItem);
    qaSort.addActionItem(modifiedDateItem);
    qaSort.addActionItem(dateCreatedItem);
//    qaSort.addActionItem(eraseItem);
//    qaSort.addActionItem(okItem);





    //set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
    //by clicking the area outside the dialog.


    qaSort.setOnDismissListener(new QuickAction.OnDismissListener()
    {
      @Override
      public void onDismiss()
      {
        //       Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
      }
    });






    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////



    setupListeners();
    return;
  }




  protected void setupListeners()
  {

    /**
     * Sort Button Listener - pops up sort menu
     */
    ibSort.setOnClickListener(new View.OnClickListener()
            {
              @Override
              public void onClick(View v)
              {
                qaSort.show(v);
              }
            });

    /**
     * Sort Menu Listener - propagate result to outside listener
     */
    qaSort.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
            {
              @Override
              public void onItemClick(QuickAction source, int pos, int actionId)
              {

                mActionId= actionId;


                if (actionId == SORT_TITLE_ASCENDING  || actionId == SORT_TITLE_DESCENDING ||
                        actionId == SORT_DATE_MODIFIED    || actionId == SORT_DATE_CREATED     ||
                        actionId == SORT_DATE_DUE)
                {   //assert its a sort action
                  mActionType= ACTION_SORT;
                }

                else
                {
                  mActionType= ACTION_NONE;
                }


                if ( mbbOnClickListener != null)
                {
                  mbbOnClickListener.onClick(NoteListBottomBar.this);
                }
              }
            });


    ibEditNotebook.setOnClickListener(new View.OnClickListener()
            {
              @Override
              public void onClick(View v)
              {
                mActionId= LAUNCH_EDIT_NOTEBOOK_ACTIVITY;
                mActionType= ACTION_MISC;

                if ( mbbOnClickListener != null)
                {
                  mbbOnClickListener.onClick(NoteListBottomBar.this);
                }
                return;
              }
            });





    return;
  }




}     /////////////END CLASS///////////

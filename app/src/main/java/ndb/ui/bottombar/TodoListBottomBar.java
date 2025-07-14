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
public class TodoListBottomBar extends BottomBar
{


  protected ImageButton ibSort,
                        ibInteractionMode;
  protected QuickAction qaSort,
                        qaInteractionMode;


  QuickAction.OnActionItemClickListener mOnActionItemClickListener;





  public TodoListBottomBar(Context context)
  {

    super(context);
    return;
  }

  public TodoListBottomBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    return;
  }

  public TodoListBottomBar(Context context, AttributeSet attrs, int defStyle)
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
      mView = layoutInflater.inflate(R.layout.bottombar_todo_list, this);
    }

    mActionType= ACTION_NONE;   //default initialization
    ibSort= (ImageButton)findViewById(R.id.todo_sort_button);
    ibInteractionMode = (ImageButton)findViewById(R.id.interaction_mode_button);

    /**
     * Sort menu
     */
    ActionItem showAllTodosItem = new ActionItem(TODO_SHOW_ALL, "All", getResources().getDrawable(net.londatiga.android.R.drawable.menu_down_arrow));
    ActionItem showSingleDayTodosItem = new ActionItem(TODO_SHOW_SINGLE_DAY, "By day", getResources().getDrawable(net.londatiga.android.R.drawable.menu_up_arrow));

    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
    showSingleDayTodosItem.setSticky(false);
    showAllTodosItem.setSticky(false);

    //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
    //orientation
    qaSort = new QuickAction(mContext, QuickAction.VERTICAL);


    //add action items into QuickAction
    qaSort.addActionItem(showAllTodosItem);
    qaSort.addActionItem(showSingleDayTodosItem);

    /**
     * Interaction mode menu
     */
    ActionItem normalInteraction = new ActionItem(INTERACTION_MODE_NORMAL, "Normal", getResources().getDrawable(net.londatiga.android.R.drawable.menu_down_arrow));
    ActionItem advancedInteraction = new ActionItem(INTERACTION_MODE_ADVANCED, "Advanced", getResources().getDrawable(net.londatiga.android.R.drawable.menu_up_arrow));

    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
    normalInteraction.setSticky(false);
    advancedInteraction.setSticky(false);

    //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
    //orientation
    qaInteractionMode = new QuickAction(mContext, QuickAction.VERTICAL);


    //add action items into QuickAction
    qaInteractionMode.addActionItem(normalInteraction);
    qaInteractionMode.addActionItem(advancedInteraction);




    //set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
    //by clicking the area outside the dialog.







    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////



    setupListeners();
    return;
  }




  protected void setupListeners()
  {
    /**
     * Sort Button Listeners
     */
    ibSort.setOnClickListener(new View.OnClickListener()
                  {
                    @Override
                    public void onClick(View v)
                    {
                      qaSort.show(v);
                    }
                  });

    qaSort.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
                  {
                    @Override
                    public void onItemClick(QuickAction source, int pos, int actionId)
                    {

                      mActionId= actionId;


                      if (actionId == TODO_SHOW_ALL  || actionId == TODO_SHOW_SINGLE_DAY)
                      {   //assert its a sort action
                        mActionType= ACTION_SORT;
                      }
                      //else if //todo other cases here:
                      else
                      {
                        mActionType= ACTION_NONE;
                      }


                      if ( mbbOnClickListener != null)
                      {
                        mbbOnClickListener.onClick(TodoListBottomBar.this);
                      }
                    }
                  });


    qaSort.setOnDismissListener(new QuickAction.OnDismissListener()
                  {
                    @Override
                    public void onDismiss()
                    {
                      //       Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
                    }
                  });

    /**
     * Interaction Mode Listeners
     */
    ibInteractionMode.setOnClickListener(new View.OnClickListener()
                {
                  @Override
                  public void onClick(View v)
                  {
                    qaInteractionMode.show(v);
                    return;
                  }
                });

    qaInteractionMode.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
                {
                  @Override
                  public void onItemClick(QuickAction source, int pos, int actionId)
                  {

                    mActionId= actionId;


                    if (actionId == INTERACTION_MODE_ADVANCED  || actionId == INTERACTION_MODE_NORMAL)
                    {   //assert its a sort action
                      mActionType= ACTION_INTERACTION_MODE_CHANGE;
                    }
                    else
                    {
                      mActionType= ACTION_NONE;
                    }


                    if ( mbbOnClickListener != null)
                    {
                      mbbOnClickListener.onClick(TodoListBottomBar.this);
                    }
                  }
                });





    return;

  }




}     /////////////END CLASS///////////

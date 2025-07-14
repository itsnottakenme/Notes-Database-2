
package ndb.ui.bottombar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 09/04/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BottomBar extends LinearLayout
{


  /**
   * ActionID's:    When on OnClickListenere is called these values will indicate which action was taken
    */

  public static final int SORT_TITLE_ASCENDING= 1,        //SORT_TITLE_ALPHABETICAL= "ALPHABETICAL",
                          SORT_TITLE_DESCENDING= 2,
                          SORT_DATE_MODIFIED = 3,
                          SORT_DATE_CREATED = 4,
                          SORT_DATE_DUE=       5,

                          MOVE_FOLDERS= 6,
                          SYNC= 7,

                          TODO_SHOW_ALL= 8,
                          TODO_SHOW_SINGLE_DAY= 9,

                          INTERACTION_MODE_NORMAL= 20,
                          INTERACTION_MODE_ADVANCED= 21,


                          INSERT_BULLET= 100,
                          INSERT_IMAGE= 101,

                          LAUNCH_EDIT_NOTEBOOK_ACTIVITY = 102,

                          TEST_BUTTON= 1000;
  ;

  /**
   * Action Types
   */

  public static final int ACTION_NONE = 0,
                          ACTION_SORT =1000,
                          ACTION_INTERACTION_MODE_CHANGE= 1001,
                          ACTION_MISC= 1002;



  /**********************************************************************
   * Begin object instance
   **********************************************************************/

  protected BottomBar.OnClickListener mbbOnClickListener;
  protected View mView;
  protected Context mContext;


//  protected ImageButton ibSort;      //todo move this to the subclass???
//  protected QuickAction qaSort;      //todo make this a list of quickAction so subclasses can use
  protected int mActionId;
  protected int mActionType;



  //todo make listeners for ImageButtons in BottomBar!!!!!!!!!!


  public BottomBar(Context context)
  {
    super(context);
    mContext= context;
    initialize();
    return;
  }

  public BottomBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    mContext= context;
    initialize();
    return;
  }

  public BottomBar(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    mContext= context;
    initialize();
    return;
  }

  /**
   * Mehtod must be overrided to initialize bottom bar. This is where the view is inflated
   * and buttons are set up.
   */
  protected abstract void initialize();


  /**
   * This method has the setup for all the listeners. It must be called in initialize().
   */
  protected abstract void setupListeners();


  /**
   * this is for BottomBAr.OnClickListsner
   * note superclass has ViewGroup.OnclickListener
   * @param bbOnClickListener
   */
  public void setOnClickListener(BottomBar.OnClickListener bbOnClickListener)
  {
    mbbOnClickListener= bbOnClickListener;
    return;
  }


  public int getActionId()
  {
   return mActionId;
  }

  public int getActionType()
  {
    return mActionType;
  }


  public interface OnClickListener
  {
    /**
     *
     * @param bottomBar - indicates which action was taken (see BottomBar for list of actions)
     */
    public abstract void onClick(BottomBar bottomBar);

  }


}   ///////////END CLASS////////////

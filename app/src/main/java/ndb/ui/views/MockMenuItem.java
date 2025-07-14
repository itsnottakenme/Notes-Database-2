package ndb.ui.views;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 30/06/13
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 *
 *
 *
 *
 *
 * *********************************
 * NOTE: THE ONLY METHODS WORKING IN THIS FILE ARE THE CONSTRUCTOR AND getItemId()
 *
 */

//todo deprecate!!!!!!!!!!
public class MockMenuItem implements MenuItem
{
  private int mItemId;

  public MockMenuItem(int itemId)
  {
    mItemId= itemId;
    return;
  }

  @Override
  public int getItemId()
  {
    return mItemId;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public int getGroupId()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public int getOrder()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setTitle(CharSequence title)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setTitle(int title)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public CharSequence getTitle()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setTitleCondensed(CharSequence title)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public CharSequence getTitleCondensed()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setIcon(Drawable icon)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setIcon(int iconRes)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Drawable getIcon()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setIntent(Intent intent)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Intent getIntent()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setShortcut(char numericChar, char alphaChar)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setNumericShortcut(char numericChar)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public char getNumericShortcut()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setAlphabeticShortcut(char alphaChar)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public char getAlphabeticShortcut()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setCheckable(boolean checkable)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isCheckable()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setChecked(boolean checked)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isChecked()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setVisible(boolean visible)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isVisible()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setEnabled(boolean enabled)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isEnabled()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean hasSubMenu()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public SubMenu getSubMenu()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public ContextMenu.ContextMenuInfo getMenuInfo()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void setShowAsAction(int actionEnum)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setShowAsActionFlags(int actionEnum)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setActionView(View view)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setActionView(int resId)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public View getActionView()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setActionProvider(ActionProvider actionProvider)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public ActionProvider getActionProvider()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean expandActionView()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean collapseActionView()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isActionViewExpanded()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public MenuItem setOnActionExpandListener(OnActionExpandListener listener)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}

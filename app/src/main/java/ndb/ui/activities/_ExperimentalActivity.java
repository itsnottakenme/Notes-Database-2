package ndb.ui.activities;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import ndb.R; //kanana.notesdatabase.R;
import ndb.db.NoteDataSource;
import ndb.ui.dialogs.DueDateDialog;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 29/07/13
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 *
 *
 *
 *
 *
 *
 *
 * can just use SimpleCursorAdapter.swapCursor(newCursor) to update data
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class _ExperimentalActivity extends ListActivity // implements LoaderManager.LoaderCallbacks<Cursor>
{
  static final int DUEDATEDIALOG= 6;

  private SimpleCursorAdapter adapter;
  private NoteDataSource mDatasource;
  ListView mListView;


  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout._experiment);
    //mListView= (ListView)findViewById(R.id.list);
    //fillData();
    showDialog(DUEDATEDIALOG, null);
  }


  @Override
  protected Dialog onCreateDialog(int id, Bundle bundle)
  {

    return new DueDateDialog(this);
  }



} ////END CLASS////
package ndb.temp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ndb.db.NDBTableMaster;
import ndb.types.Note;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 24/04/13
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class StartEditNoteActivity extends Activity
{
  //todo doesn't work since classnotfoundDef for most files!!!!!!!!!!!!!
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    Intent intent = new Intent("EditNoteActivity.intent.action.Launch"); //"EditNoteActivity.intent.action.Launch"
    //Intent intent = new Intent(this, kanana.notesdatabase.activities.EditNoteActivity.class);

    intent.putExtra(Note.GUID_EXTRA, new Long(7));
    intent.putExtra("NdbIntent.TYPE", "EDIT_NOTE");

    intent.putExtra("DATABASE_NAME", NDBTableMaster.NOTES_DB);
    startActivity(intent);

  }
}
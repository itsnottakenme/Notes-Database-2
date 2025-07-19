package ndb.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import ndb.R; //kanana.notesdatabase.R;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 22/03/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesActivity extends PreferenceActivity
{
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
  }

}

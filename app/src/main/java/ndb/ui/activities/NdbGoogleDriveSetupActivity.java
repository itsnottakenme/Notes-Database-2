package ndb.ui.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.DriveScopes;
import ndb.types.PrefKey;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 19/05/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 *
 *
 * This Activity gets login information for google drive. Once this information is collected it will be storeds
 *
 */
public class NdbGoogleDriveSetupActivity extends Activity
{
  static final int REQUEST_ACCOUNT_PICKER = 1;


  private GoogleAccountCredential credential;
//  private static Drive service;



  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

    return;
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
  {
    switch (requestCode)
    {
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
        {
          String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null)
          {
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor= preferences.edit();
            editor.putString(PrefKey.KEY_ACCOUNT_NAME, accountName);
            editor.commit();

          }
        }
        break;
    }

    return;
  }






}           ///////////END CLASS////////////
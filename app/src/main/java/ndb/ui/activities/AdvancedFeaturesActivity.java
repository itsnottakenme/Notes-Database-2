package ndb.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.api.services.drive.model.File;
import ndb.R; //kanana.notesdatabase.R;
import ndb.db.NDBTableMaster;
import ndb.remote.GoogleDriveAdapter;
import ndb.types.PrefKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 10/04/13
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 *
 * To do import from db or sync with google. Call it when settings icon is pressed in notebookActivity
 *
 */
public class AdvancedFeaturesActivity extends Activity
{
  private Button bImport,
                 bGoogleDriveSync,
                 bCreateNdbHome,
                 bDeleteAllDriveStuff,
                 bBackupDb,
                 bReplaceDatabase;

  private GoogleDriveAdapter mGDrive;

  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.misc_activity);

    bImport= (Button)findViewById(R.id.import_button);
    bGoogleDriveSync= (Button)findViewById(R.id.googledrive_button);
    bCreateNdbHome = (Button)findViewById(R.id.googledrivecreatendbhome_button);
    bDeleteAllDriveStuff= (Button)findViewById(R.id.googledrivedeleteall_button);
    bBackupDb= (Button) findViewById(R.id.googledrivebackupdb_button);
    bReplaceDatabase= (Button) findViewById(R.id.googledriverestoredb_button);

    mGDrive = new GoogleDriveAdapter(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(AdvancedFeaturesActivity.this).getString(PrefKey.KEY_ACCOUNT_NAME, PrefKey.FAIL));


    setupListeners();
    return;
  }


  private void setupListeners()
  {
    /////////////////////////////////////////////////////////////////////////////////////////////
    bImport.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        /**
         * todo create check where this only works if there is no db file
         */
//            ImportDbHelper.importAwesomeNoteDb(getApplicationContext(), "/storage/emulated/0/_import/temp/notebase.db", NDBTableMaster.TEMP_DB, NDBTableMaster.NOTES_DB);
//            Toast.makeText(getApplicationContext(), "Import started...", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Feature disabled to prevent multiple import of same notes ", Toast.LENGTH_SHORT).show();


           }
        });

    /////////////////////////////////////////////////////////////////////////////////////////////
    bGoogleDriveSync.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
              startActivity( new Intent(getApplicationContext(), NdbGoogleDriveSetupActivity.class) );
              Toast.makeText(getApplicationContext(), "Sync started...", Toast.LENGTH_SHORT).show();
            }
          });

    /////////////////////////////////////////////////////////////////////////////////////////////
    bCreateNdbHome.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {


        //gDrive = new GoogleDriveAdapter(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(MiscActivity.this).getString(PrefKey.KEY_ACCOUNT_NAME, PrefKey.FAIL));
        mGDrive.createNdbHome();
        //todo fix!!!!!!!!!

        Toast.makeText(getApplicationContext(), "Test create note...", Toast.LENGTH_SHORT).show();

      }
    });
    /////////////////////////////////////////////////////////////////////////////////////////////
    bDeleteAllDriveStuff.setOnClickListener(new View.OnClickListener()
              {
                @Override
                public void onClick(View v)
                {

                  //gDrive= new GoogleDriveAdapter(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(MiscActivity.this).getString(PrefKey.KEY_ACCOUNT_NAME, PrefKey.FAIL));
                  mGDrive.trashAllNdbFilesAndFolders();
                  Toast.makeText(getApplicationContext(), "Test Deleting NDB home", Toast.LENGTH_SHORT).show();

                  //To change body of implemented methods use File | Settings | File Templates.
                }
              });

    /////////////////////////////////////////////////////////////////////////////////////////////
    bBackupDb.setOnClickListener(new View.OnClickListener()
              {
                @Override
                public void onClick(View v)
                {

                  //gDrive= new GoogleDriveAdapter(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(MiscActivity.this).getString(PrefKey.KEY_ACCOUNT_NAME, PrefKey.FAIL));
                  mGDrive.createDatabaseBackup(getApplicationContext().getDatabasePath(NDBTableMaster.NOTES_DB).getPath() /*+ dateFormat.format(date)*/);
                  Toast.makeText(getApplicationContext(), "Backing up DB", Toast.LENGTH_SHORT).show();

                  //To change body of implemented methods use File | Settings | File Templates.
                }
              });

    bReplaceDatabase.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {

        mGDrive.downloadBackupDbList();
        Toast.makeText(getApplicationContext(), "loading backed up db list", Toast.LENGTH_SHORT).show();

      }
    });

    /////////////////////////////////////////////////////////////////////////////////////////////
    mGDrive.setGoogleDriveAdapterListener(new GoogleDriveAdapter.GoogleDriveAdapterListener()
              {
                @Override
                public void onResult(GoogleDriveAdapter.SendRequestTask source/*, String completedTask*/ )
                {
                  String completedTask;
                  final List<File> backupDbList;
                  List<String> dialogItems= new ArrayList<String>();

                  java.io.File downloadedDb;
                  Context context;
                  java.io.File currentDatabaseFile;



                  completedTask= source.getTask();
                  context= getApplicationContext();


                  Toast.makeText(context, "Finished " + completedTask, Toast.LENGTH_SHORT).show();


                  if (completedTask.equals(GoogleDriveAdapter.TASK_DOWNLOAD_BACKUP_DB_LIST)   )
                  { ///////////////////////////////////////////////////////////////////////////////////////
                    backupDbList= source.getBackupDbFileList();


                    for (File file : backupDbList)
                    {
                      dialogItems.add(file.getTitle());
                    }



                    final CharSequence[] items = dialogItems.toArray(new String[dialogItems.size()]);


                    AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedFeaturesActivity.this);

                    builder.setTitle("Pick a color");
                    builder.setItems(items, new DialogInterface.OnClickListener()
                          {

                            public void onClick(DialogInterface dialog, int item)
                            {
                              File file;
                              Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();

                              file= backupDbList.get(item);
                              mGDrive.downloadBackupDb(file);
                            }

                          });

                    AlertDialog alert = builder.create();

                    alert.show();

                    //TODO: RUN NEW SEND REQUEST TASK FOR CHOSEN BACKUP DB!!!!!!!!!!!!!


                  }
                  else if (completedTask.equals(GoogleDriveAdapter.TASK_DOWNLOAD_BACKUP_DB))
                  { /////////////////////////////////////////////////////////////////////////////////////////
                    downloadedDb= source.getDownloadedDb();


                    currentDatabaseFile= context.getDatabasePath(NDBTableMaster.NOTES_DB);
                    //context.deleteDatabase(NDBTableMaster.NOTES_DB);
                    downloadedDb.renameTo(currentDatabaseFile);




                    //todo START HERE: switch DBs!!!!!!!!!


                  }
                  return;
                }
              });



    return;
  }

} ///////////////END CLASS/////////////
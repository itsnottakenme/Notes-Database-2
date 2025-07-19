package ndb.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.model.File;
import ndb.db.NDBTableMaster;
import ndb.types.PrefKey;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
* Created with IntelliJ IDEA.
* User: ian
* Date: 19/04/13
* Time: 6:23 PM
* To change this template use File | Settings | File Templates.
*/


//todo make into a singleton?

/**
 *
 *
 *
 * - All folders will be in NOTE_FOLDER
 * - All folders (Notebooks) will have NOTE_FOLDER (or its debug equivalent) as a parent
 * - All files will be in a single notebook (folder)
 *
 *
 *
 * TODO WEIRD STUFF
 *                mNoteFolder, PrefKey.KEY_NDB_HOME, ... all refer to the same thing and can become inconsistant
 */
public class GoogleDriveAdapter
{
  public static final String TAG= "GoogleDriveAdapter";
  public static final String NDB_HOME_FOLDER = "NotesDatabase",     //mNoteFolder is what the app should using excdept for the constructor
                             NDB_BACKUP_FOLDER= ".backup",
                             DEBUG_NDB_HOME_FOLDER = "NotesDatabase_TEST",
                            // DEBUG_NDB_BACKUP_FOLDER= ".backup_TEST",  //not needed

                             FOLDER_MIME_TYPE= "application/vnd.google-apps.folder",
                             FILE_MIME_TYPE=   "application/vnd.google-apps.file";


  /**
   * These are all the tasks that GoogleDriveAdapter can handle
   */
  public static final String    TASK_CREATE_NDB_HOME=   "TASK_CREATE_NDB_HOME",
                                TASK_CREATE_NOTEBOOK=   "TASK_CREATE_NOTEBOOK",
                                TASK_CREATE_NOTE=       "TASK_CREATE_NOTE",
                                TASK_DOWNLOAD_BACKUP_DB_LIST= "TASK_DOWNLOAD_BACKUP_DB_LIST",
                                TASK_DOWNLOAD_BACKUP_DB= "TASK_DOWNLOAD_BACKUP_DB",
                                TASK_TRASH_ALL_NDB= "TASK_TRASH_ALL_NDB",          //sends to trash ndb on google drive
                                TASK_BACKUP_DATABASE=   "TASK_BACKUP_DATABASE"      ;







  private GoogleAccountCredential mCredential;
  private static Drive mDrive;

  private String mNdbHomeFolder;
  private Context mContext;
  //private String currentTask;
  private GoogleDriveAdapterListener mListener;


  public GoogleDriveAdapter(Context context, String accountName)
  {
    mCredential = GoogleAccountCredential.usingOAuth2(context, DriveScopes.DRIVE);
    mCredential.setSelectedAccountName(accountName);
    mDrive = getDriveService(mCredential);
    mNdbHomeFolder = NDB_HOME_FOLDER;
    mContext= context;


    return;
  }

  private void initialize()
  {
    /**
     * -create Ndb root directory if not already exists.
     * -save fileId of the dir so can delete it later (in database?)
     * todo: add the above!!!
     */


    return;
  }

  public void setGoogleDriveAdapterListener(GoogleDriveAdapterListener listener)
  {
    mListener= listener;
    return;
  }


  public String getNoteFolder()
  {
    return mNdbHomeFolder;
  }

  public void setNoteFolder(String noteFolder)
  {
    this.mNdbHomeFolder = noteFolder;
  }


  /**
   * Uses mNoteFolder as the home dir
   * Creates a new NdbHome folder on google drive ONLY IF one doesn't already exists
   *
   * setNoteFolder() *MUST* be called bfroe this is invoked
   */
  public void createNdbHome()
  {
    new SendRequestTask(mContext, TASK_CREATE_NDB_HOME).execute(mNdbHomeFolder);
    return;
  }







  public String getNdbHomeFolderId()
  {
    SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
    String ndbHomeFolderId;

    ndbHomeFolderId = preferences.getString(PrefKey.KEY_NDB_HOME, PrefKey.FAIL);

    if (ndbHomeFolderId == PrefKey.FAIL)
    {
      ndbHomeFolderId = null;
    }

    return ndbHomeFolderId;
  }

  public String getNdbBackupFolderId()
  {
    SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
    String ndbBackupFolderId;

    ndbBackupFolderId = preferences.getString(PrefKey.KEY_NDB_BACKUP, PrefKey.FAIL);

    if (ndbBackupFolderId == PrefKey.FAIL)
    {
      ndbBackupFolderId = null;
    }

    return ndbBackupFolderId;
  }


  /**
   * Results get reported via listener
   *
   * 1) downloads list of backup db
   * 2) pops up dialog to allow user to chose which db to download
   * 3) downloads db
   * 4) calls ImportDbHelper to replace current DB with downloaded one
   */
  public void downloadBackupDbList()
  {
    //add a handler  for GoogleDriveAdapter that gets called from onPostExecute
    new SendRequestTask(mContext, TASK_DOWNLOAD_BACKUP_DB_LIST).execute();
    return;
  }

  public void downloadBackupDb(File file)
  {
    new SendRequestTask(mContext, TASK_DOWNLOAD_BACKUP_DB).execute(file);   //todo doInBackground can only accept a String... params!!!!!!!
    return;
  }


  public void getFile(String fileId)
  {

  }





  public void trashAllNdbFilesAndFolders()
  {
    new SendRequestTask(mContext, TASK_TRASH_ALL_NDB).execute();
    return;
  }


  private synchronized Drive getDriveService(GoogleAccountCredential credential)
  {
    return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
            .build();
  }


  /**
   *
   * @param filename - the name of database to bwe backed up eg. "notes.db"
   */
  public void createDatabaseBackup(String filename)
  {
    String fullPath;

    fullPath= mContext.getDatabasePath(filename).getPath();

    new SendRequestTask(mContext, TASK_BACKUP_DATABASE).execute(fullPath);
    return;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////INNER CLASSES/////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////

  public class SendRequestTask extends AsyncTask<Object, Void, File>
  {
    private final String mTask;        //the task that needs to be performed
    private final Context mContext;
    private String mFilename;


    //relevant variables from the following are populated after task finishes
    private List<File> mBackupDbFileList;
    private java.io.File mDownloadedDb;




    public SendRequestTask(Context context, String taskType)
    {
      mContext= context;
      mTask= taskType;

      return;
    }






    @Override
    protected File doInBackground(Object... params)
    { ///////////////////////////////////////////////////////////////////////////////////////////////////////
      String ndbHome;
      File result= null;

      try
      {

        if ( mTask.equals(TASK_CREATE_NDB_HOME) && params != null )
        {
          ndbHome= (String)params[0];
          mFilename= ndbHome;
          result= createNdbRootDirectory(/*ndbHome*/);  //this function will only create ndbRoot folder if it doesn't alreadt exist
        }
        else if ( mTask.equals(TASK_TRASH_ALL_NDB) )
        {
          trashAllNdbFilesAndFolders();
        }
        else if (mTask.equals(TASK_BACKUP_DATABASE) && params != null)
        {
          uploadDatabase((String) params[0]);
        }
        else if (mTask.equals(TASK_DOWNLOAD_BACKUP_DB_LIST))
        {
          downloadBackUpDbList();
        }
        else if (mTask.equals(TASK_DOWNLOAD_BACKUP_DB))
        {
          mDownloadedDb= downloadFile((File)params[0]);    //params[0] is the fileId of file to be downloaded
        }


      }
      catch (Exception e)
      {
        Log.e(TAG, "Failed to do something: " + e.getMessage());
      }

      return result;
    }



    /**
     *
     * @param file - has been created or update by doInBackground
     */
    @Override
    protected void onPostExecute(File file)
    {
      super.onPostExecute(file);    //To change body of overridden methods use File | Settings | File Templates.

      //if (mListener != null)
      mListener.onResult(this/*, mTask*/); //todo GoogleDriveAdapter.this seems weird... works?


      return;
    }


    /**
     * tasks are listed in GoogleDriveAdapter and all begin with TASK_
     * @return
     */
    public String getTask()
    {
      return mTask;
    }

    private java.io.File downloadFile(File file)
    {

      InputStream inputStream;
      java.io.File outputFile= null;

      byte data[] = new byte[1024];
      long total = 0;
      int count;



      //TODO START HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!! MAY NEED TO TURN THIS INTO GET DOWNLOAD URI and then use DownloadManager
      //  in another class to start the download

      /////////////rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
      if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0)
      {
        try
        {
          HttpResponse resp= mDrive.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
          inputStream= resp.getContent();

          // download the file
          outputFile= new java.io.File( mContext.getFilesDir()/*+"//databases"*/, file.getTitle() );
          OutputStream output = new FileOutputStream(outputFile);

          while ((count = inputStream.read(data)) != -1)
          {
            total += count;
            // publishing the progress....
            //publishProgress((int) (total * 100 / fileLength));
            output.write(data, 0, count);
          }

          output.flush();
          output.close();
          inputStream.close();

        }                 //////////////////////'''''''''''''''''''''''''
        catch (IOException e)
        {
          // An error occurred.
          e.printStackTrace();
          return null;
        }
      }

      ////////////rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr


      return outputFile;
    }


    private void downloadBackUpDbList() throws IOException
    {

      String backUpFolderId;
      backUpFolderId= getNdbBackupFolderId();

      Drive.Files.List request;
      FileList files;

      // "mimeType='application/vnd.google-apps.folder' and trashed=false and title='MyApp' and 'root' in parents "

      request = mDrive.files().list().setQ("'" +backUpFolderId+ "' in parents and trashed=false" );


      files = request.execute();

      mBackupDbFileList= files.getItems();    //puts the files in mBackupDbFileList so can be retrieved through listener


      return;
    }

    private void trashAllNdbFilesAndFolders()          throws IOException
    { ///////////////////////////////////////////////////////////////////////////////////////////////////////

      //Remove key for PrefKey.KEY_NDB_HOME
      SharedPreferences preferences;
      SharedPreferences.Editor editor;
      String ndbHomeId;

      Drive.Files driveFiles;
      Drive.Files.List driveFilesList;




      //Get id of ndbHome folder
      //preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
      //preferences.getString(PrefKey.KEY_NDB_HOME, PrefKey.FAIL);
      ndbHomeId= getNdbHomeFolderId();


      if ( ndbHomeId != null)
      { //assert dir on google drive exists and must be deleted


        //delete dir and subdirs on googledrive
        driveFiles= mDrive.files();
        driveFiles.trash(ndbHomeId).execute();    //todo this line appears to not work!!!!!!
        //todo ENSURE CHILDREN GET MOVED TO TRASH AS WELL!!!!!!!!


        //delete associated PrefKey
        editor= PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.remove(PrefKey.KEY_NDB_HOME);
        editor.remove(PrefKey.KEY_NDB_BACKUP);
        editor.commit();


        ///////////////////////////////////////////////////
      }





      return;
    }

    /**
     * Checks if ndbRoot exists.If it does it turns null. If it doesn't exist it creates it and returns the created File
     * @param
     * @return
     * @throws Exception
     */
    private File createNdbRootDirectory(/*String ndbRoot*/)   throws Exception
    {  ///////////////////////////////////////////////////////////////////////////////////////////////////////
      File ndbRootDir= null,
           backupDir;

      ParentReference parent;
      List<ParentReference> parentReferences;

      SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
      SharedPreferences.Editor editor= preferences.edit();




      if (getNdbHomeFolderId() == null)
        {   //assert ndb home folder needs to be created

          // create ndb home
          ndbRootDir = new File();
          ndbRootDir.setTitle(mNdbHomeFolder);
          ndbRootDir.setMimeType("application/vnd.google-apps.folder");
          ndbRootDir = mDrive.files().insert(ndbRootDir).execute();

          editor.putString(PrefKey.KEY_NDB_HOME, ndbRootDir.getId());
          editor.commit();


          /////////////////////////////////////////

          //set up parent reference
          parent= new ParentReference();
          parent.setId(ndbRootDir.getId());

          parentReferences= new ArrayList();
          parentReferences.add(parent);


          //create backup folder
          backupDir = new File();
          backupDir.setTitle(NDB_BACKUP_FOLDER);
          backupDir.setMimeType("application/vnd.google-apps.folder");
          backupDir.setParents(parentReferences);
          backupDir= mDrive.files().insert(backupDir).execute();

          editor.putString(PrefKey.KEY_NDB_BACKUP, backupDir.getId());
          editor.commit();

          //////////////////////////////////////////


        }



      return ndbRootDir;
    }



    private File uploadDatabase(String path)   throws IOException
    { ///////////////////////////////////////////////////////////////////////////////////////////////////////
      java.io.File dbFile= null;
      FileContent fileContent;

      File fileToUpload= null;
      List<ParentReference> parentReferences;
      ParentReference parent;

      String remoteFilename;      //what the file will be named on the server
      String[] temp;                //used for constructing remoteFilename

      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
      Date date= new Date();



      if (getNdbHomeFolderId() != null)
      {
        // File's binary content
        dbFile = new java.io.File(path);
        fileContent = new FileContent(null, dbFile);     //first parameter is the mime-type

        //Construct remote filename
        temp= dbFile.getName().split("\\."); // regular expression for period in a String
        remoteFilename= temp[0] + " (" + dateFormat.format(date) + ") ";

        /**
         * Filename should only be broken into 2 parts. That is why for loop commented out
         */

//        for (int i=1; i<temp.length; i++)
//        {
          remoteFilename= remoteFilename+ " DBv" + NDBTableMaster.DATABASE_VERSION+ " ." + temp[1]; //remoteFilename= remoteFilename + "." + temp[i];
//        }



        ///////////////////////////////////////////////////////////////////
        // File's metadata.
        fileToUpload = new File();
        fileToUpload.setTitle(remoteFilename);   //append date to file name
        fileToUpload.setMimeType(null);

        //set up parent reference
        parent= new ParentReference();
        parent.setId(getNdbBackupFolderId());

        parentReferences= new ArrayList();
        parentReferences.add(parent);
        fileToUpload.setParents(parentReferences);

        //Upload
        fileToUpload= mDrive.files().insert(fileToUpload, fileContent).execute();
        ////////////////////////////////////////////////////////////////////



      }

      return fileToUpload;
    }



//    private File createFile(String filename, Drive.Parents parent)
//    { ///////////////////////////////////////////////////////////////////////////////////////////////////////
//      File file= null;
//
//
//      file= new File();
//      file.setTitle(filename);
//
//
//      return file;
//    }



    private synchronized boolean _TEMP_DISABLE_folderExists(String folder) throws Exception //todo change from string so user can enter mimetype?
    { ///////////////////////////////////////////////////////////////////////////////////////////////////////
      boolean exists= false;
      Drive.Files.List request;
      FileList files;

      // "mimeType='application/vnd.google-apps.folder' and trashed=false and title='MyApp' and 'root' in parents "

      request = mDrive.files().list().setQ("mimeType='application/vnd.google-apps.folder' "
              +" and trashed=false and title='" +folder+"' and 'root' in parents" );


      files = request.execute(); //todo THIS LINE THROWS AN EXCEPTION!!!!!!!!!!!!!!!

      if ( files.getItems().isEmpty() == true )
      {
        exists= false;
      }
      else
      {
        exists= true; //assert file already exists
      }



      return exists;
    }


    public List<File> getBackupDbFileList()
    {
      return mBackupDbFileList;
    }

    public java.io.File getDownloadedDb()
    {
      return mDownloadedDb;
    }

  }        /////END CLASS/////

  public interface GoogleDriveAdapterListener
  {
    public void onResult(SendRequestTask source/*, String completedTask*/);
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////END INNER CLASSES/////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////






}          ////////END CLASS///////






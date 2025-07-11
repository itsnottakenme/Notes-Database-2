package ndb.types;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.*;
import java.util.HashMap;

/***
 Copyright (c) 2008-2012 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Advanced Android Development_
 http://commonsware.com/AdvAndroid
 */

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 05/10/13
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaProvider extends ContentProvider
{
  //String s= "content://kanana.notesdatabase.provider/media/*";  //all media i think

  /**
   * Class instance
   */
  public static final Uri CONTENT_URI = Uri.parse("content://kanana.notesdatabase.mediaprovider/");/*"content://com.commonsware.android.cp.files/"*/
  private static final HashMap<String, String> MIME_TYPES = new HashMap<String, String>();

  String crap;

  static
  {

    //MIME_TYPES.put(".pdf", "application/pdf");
    MIME_TYPES.put(".bmp", "image/bmp");
    MIME_TYPES.put(".gif", "image/gif");


    MIME_TYPES.put(".jpg", "image/jpeg");
    MIME_TYPES.put(".jpeg", "image/jpeg");
    MIME_TYPES.put(".png", "image/png");

    //todo: add jpg and other image formats
  }


  /**
   * Begin object instance
   */


  /**
   * The constructor I think. Android launches this class (not the user, thats why it is called onCreate()
   * @return
   */
  @Override
  public boolean onCreate()
  {
    /**
     * files already exist so i believe no set up is necessary
     */

    crap= "dog";                     //this line runsQ!

//    File f = new File(getContext().getFilesDir(), "test.pdf");
//
//    if (!f.exists())
//    {
//      AssetManager assets = getContext().getResources().getAssets();
//
//      try
//      {
//        copy(assets.open("test.pdf"), f);
//      }
//      catch (IOException e)
//      {
//        Log.e("FileProvider", "Exception copying from assets", e);
//
//        return false;
//      }
//    }

    return true;
  }


  /**
   * returns the mimeType of an uri I believe. Isn't this just copying MimeTypeMap?
   * @param uri
   * @return
   */
  @Override
  public String getType(Uri uri)
  {
    String path = uri.toString();

    for (String extension : MIME_TYPES.keySet())
    {
      if (path.endsWith(extension))
      {
        return (MIME_TYPES.get(extension));
      }
    }

    return (null);
  }

  @Override
  public ParcelFileDescriptor openFile(Uri uri, String mode)  throws FileNotFoundException
  {
    File f;

    f = new File(getContext().getFilesDir()+File.separator+"media", uri.getPath());      //new File(getContext().getFilesDir(), uri.getPath());
    if (f.exists())
    {
      return (ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY));
    }

    throw new FileNotFoundException(uri.getPath());
  }



  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public Cursor query(Uri url, String[] projection, String selection,
                      String[] selectionArgs, String sort)
  {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Uri insert(Uri uri, ContentValues initialValues)
  {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public int update(Uri uri, ContentValues values, String where, String[] whereArgs)
  {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public int delete(Uri uri, String where, String[] whereArgs)
  {
    throw new RuntimeException("Operation not supported");
  }

  /**
   * Note needed
   */
//  static private void copy(InputStream in, File dst) throws IOException
//  {
//    FileOutputStream out = new FileOutputStream(dst);
//    byte[] buf = new byte[1024];
//    int len;
//
//    while ((len = in.read(buf)) > 0)
//    {
//      out.write(buf, 0, len);
//    }
//
//    in.close();
//    out.close();
//    return;
//  }


}     ////END CLASS////

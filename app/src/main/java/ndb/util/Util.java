package ndb.util;

//import android.text.format.DateFormat;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 27/04/13
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class Util
{
  /**
   * Subdirectroies
   */
  private static String MEDIA_SUBDIRECTORY="media";






  public static String getMediaPath(Context context)
  {
    return context.getApplicationContext().getFilesDir()+ File.separator+MEDIA_SUBDIRECTORY;
  }


  /**
   * checks to see that there is a . in the path and that it is within 5(?) chars from end of string
   * @param path
   * @return
   */
  static public boolean doesFileExtensionExist(String path)
  {
    boolean fileExtensionExists = false;
    int dotPosition;

    dotPosition= path.lastIndexOf(".");

    if (dotPosition>0 &&  dotPosition > path.length()-6)
    {
      fileExtensionExists = true;
    }

   return fileExtensionExists;
  }






  static public String getRealPathFromURI(Context context, Uri contentUri)
  {
    String[] proj = { MediaStore.Images.Media.DATA };
    Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    cursor.moveToFirst();
    return cursor.getString(column_index);
  }


//  public static String getFileNameByUri(Context context, Uri uri)
//  {
//    String fileName="unknown";//default fileName
//    Uri filePathUri = uri;
//    if (uri.getScheme().toString().compareTo("content")==0)
//    {
//      Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//      if (cursor.moveToFirst())
//      {
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
//        filePathUri = Uri.parse(cursor.getString(column_index));
//        fileName = filePathUri.getLastPathSegment().toString();
//      }
//    }
//    else if (uri.getScheme().compareTo("file")==0)
//    {
//      fileName = filePathUri.getLastPathSegment().toString();
//    }
//    else
//    {
//      fileName = fileName+"_"+filePathUri.getLastPathSegment();
//    }
//    return fileName;
//  }





//  /**
//   *
//   * @param path
//   * @param mimeType
//   * @return
//   */
//  static public String addFileExtension(String path, String mimeType)
//  {
//    MimeTypeMap mimeTypeMap;
//    mimeTypeMap= MimeTypeMap.getSingleton();
//
//    return path+"."+mimeTypeMap.getExtensionFromMimeType(mimeType);
//  }
//






  public static boolean longToBoolean(long number)
  {
    boolean result;

    if (number == 0)
    {
      result= false;
    }
    else
    {
      result= true;
    }

    return result;
  }


  public static long booleanToLong(boolean bool)
  {
    long result;


    if (bool == false)
    {

      result= 0;
    }
    else
    {
      result= 1;
    }

    return result;
  }


  /**
   * Puts in <br> tags before any \n
   * @param text
   * @return
   */
//  public static String plainTextToHtml(CharSequence text)
//  {
//    StringBuffer buffer;
//
//    buffer= new StringBuffer(text.length()*2);
//
//    for (int i=0; i<text.length(); i++)
//    {
//      if (text.charAt(i) == '\n')
//      {
//        buffer.append("<br>\n");
//      }
//      else
//      {
//        buffer.append(text.charAt(i));
//      }
//
//    }
//
//
//   return buffer.toString();
//  }




  //added 2025
  @RequiresApi(api = Build.VERSION_CODES.O)
  public static void copy(File origin, File dest) throws IOException {
    //FileUtils.copy(origin.toPath(), dest.toPath());
  }


  /**
   * Copied verbatim :P Stil gives malformed URI
   *
   * @param context
   * @param uri
   * @return
   */
  @Nullable
  public static String createCopyAndReturnRealPath(
          @NonNull Context context, @NonNull Uri uri) {
    final ContentResolver contentResolver = context.getContentResolver();
    if (contentResolver == null)
      return null;

    // Create file path inside app's data dir
    String filePath = context.getApplicationInfo().dataDir + File.separator + "temp_file";
    File file = new File(filePath);
    try {
      InputStream inputStream = contentResolver.openInputStream(uri);
      if (inputStream == null)
        return null;
      OutputStream outputStream = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      int len;
      while ((len = inputStream.read(buf)) > 0)
        outputStream.write(buf, 0, len);
      outputStream.close();
      inputStream.close();
    } catch (IOException ignore) {
      return null;
    }
    return file.getAbsolutePath();
  }

}     ////////////END CLASS////////////



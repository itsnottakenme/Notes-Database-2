package ndb.types;

import android.content.ContentValues;
import android.database.Cursor;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Used as a wrapper for the Media table in db
 *
 *
 * User: ian
 * Date: 30/09/13
 * Time: 8:22 PM
 *
 *
 *
 */
public class Media
{

  /**
   * SQL fields
   */
  public static final String[] ALL_COLUMNS =
          {       Media.ID, Media.NOTE_ID,
                  Media.MIME_TYPE,
                  Media.URI,
                  Media.PATH,
                  Media.PREVIEW,
                  Media.ORIGINAL_FILENAME,

          };



  public static final String  TABLE =    "media",

  ID =        "_id",        //primary key
  NOTE_ID = "NOTE_GUID",

  MIME_TYPE= "MIME_TYPE",
  PATH= "PATH",
  ORIGINAL_FILENAME= "ORIGINAL_FILENAME",


  URI= "URI",
  PREVIEW= "PREVIEW",

  /**
   * Not implemented
   */

  /**
   * Field ideas
   * addedDate - so can sort by when added (can just use guid for that)
    */


  HASH= "HASH",                         //If I decide to use an MD5 checksum
  INT1= "INT1",
  INT2= "INT2",
  INT3= "INT3",
  INT4= "INT4",
  INT5= "INT5",
  INT6= "INT6",
  INT7= "INT7",
  INT8= "INT8",
  TEXT1= "TEXT1",
  TEXT2= "TEXT2",
  TEXT3= "TEXT3",
  TEXT4= "TEXT4",
  TEXT5= "TEXT5",
  TEXT6= "TEXT6",
  TEXT7= "TEXT7",
  TEXT8= "TEXT8";



  /**
   * Begin object instance
   */
  private long    guid,
                  noteId;


  private String  mimeType,
                  uri,
                  path,
                  previewPath,      //holds the preview of item. eg a an icon for an image
                  originalFilename;

//  Uri uri;                        //todo: probably best to turn back to a string. No point in having extra reconstruction costs when valye doesn't change



  MimeTypeMap m;





  public static Media fromCursor(Cursor cursor)
  {
    Media media;
    media= new Media();

    media.setGuid(cursor.getLong(cursor.getColumnIndex(Media.ID)));
    media.setNoteId(cursor.getLong(cursor.getColumnIndex(Media.NOTE_ID)));

    media.setMimeType(cursor.getString(cursor.getColumnIndex(Media.MIME_TYPE)));
    media.setUri(cursor.getString(cursor.getColumnIndex(Media.URI)));
    media.setPath(cursor.getString(cursor.getColumnIndex(Media.PATH)));
    media.setPreviewPath(cursor.getString(cursor.getColumnIndex(Media.PREVIEW)));
    media.setOriginalFilename(cursor.getString(cursor.getColumnIndex(Media.ORIGINAL_FILENAME)));

    /**
     * todo: add Uri
     */


    return media;
  }

  public static List<Media> listFromCursor(Cursor cursor)
  {
    List<Media> mediaList;
    Media newMedia;

    mediaList = new ArrayList<Media>();

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {

      newMedia = Media.fromCursor(cursor);   //this should return a single noteBook entry
      mediaList.add(newMedia);
      cursor.moveToNext();
    }
    return mediaList;
  }




  public ContentValues toContentValues()
  {
    ContentValues contentValues;

    contentValues = new ContentValues();
    contentValues.put(Media.ID, guid);
    contentValues.put(Media.NOTE_ID, noteId);

    contentValues.put(Media.MIME_TYPE, mimeType);
    contentValues.put(Media.URI, uri);                     //todo: uri..... is this right?
    contentValues.put(Media.PATH, path);
    contentValues.put(Media.PREVIEW, previewPath);
    contentValues.put(Media.ORIGINAL_FILENAME, originalFilename);






    return contentValues;
  }




  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public long getGuid()
  {
    return guid;
  }

  public void setGuid(long guid)
  {
    this.guid = guid;
  }

  public String getMimeType()
  {
    return mimeType;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  public long getNoteId()
  {
    return noteId;
  }

  public void setNoteId(long noteId)
  {
    this.noteId = noteId;
  }

  public String getPreviewPath()
  {
    return previewPath;
  }

  public void setPreviewPath(String previewPath)
  {
    this.previewPath = previewPath;
  }

  public String getUri()
  {
    return uri;
  }

  public void setUri(String uri)
  {
    this.uri = uri;
  }

  public String getOriginalFilename()
  {
    return originalFilename;
  }

  public void setOriginalFilename(String originalFilename)
  {
    this.originalFilename = originalFilename;
  }

  /**
   * Construct filename using id and mimeType

   */
  public String getFilename()
  {
    return path.substring(path.lastIndexOf(File.separator)+1);
  }


}

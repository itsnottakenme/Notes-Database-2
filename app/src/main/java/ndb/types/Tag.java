package ndb.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 09/03/13
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class Tag
{

  final public static String OBJECT =      "tag object";   // to send as parcelable
  /**
   * This can take in a String of tags and break them up into an array with one tag per element.
   * It is responsible for removing illegal charcters
   * @param tags
   * @return
   */
  public static List<String> stringToList(String tags)     //todo FIIIIIIIIIIIIIIX
  {
    List<String> tagList= new ArrayList<String>();

    if (tags!=null)
    {
     tagList.addAll(Arrays.asList(tags.split(" ")));
    }
    //else tagList is left empty

    while( tagList.remove("") );  //removes  all the empty strings from tagList


    return tagList;

  }

  /*
  public static String listToString(List<Tag> tagList)
  {
    String tags;
    tags= new String();

    for(int i=0; i<tagList.size(); i++)
    {
      tags= tags+tagList.get(i).getName()+" ";
    }
    return tags;
  }   */


}    //////////////////END CLASS/////////////

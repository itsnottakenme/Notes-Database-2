package ndb.types;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 26/07/13
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */

import com.google.gson.Gson;
import ndb.ui.spans.ListItemSpan;

import java.util.*;

/**
 * A list containing a map for each object added
 *
 *
 * Properties
 * -----------------------
 *
 * All:
 * Type
 * Start
 * End
 *
 *
 *
 *
 *
 *
 *
 * todo: Refactoring - rename to JsonAdapter
 * rename methods to:
 * fromSpans()
 * toSpans()
 *
 * 1) make toJson(List<SpanWrapper> spanWrappers) static. All other methods should be private and called from here
 * 2) rewrite so span creation so that SpanBuilder subclasses handle any span specific data
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class JsonAdapter
{
  public final static String TYPE= "type",
                             START= "start",
                             END= "end",
                             FLAGS= "flags"





                                     ;





  List<Map> mList;
  Set<String> spansThatWerentHandled;

//  public JsonAdapter()
//  {
//    mList = new ArrayList();
//    spansThatWerentHandled= new HashSet();
//  }
//
//
//
//
//  public int size()
//  {
//    return mList.size();
//  }
//
//
//  public void addAll(List<SpanWrapper> spanWrappers)
//  {
//    SpanWrapper currentSpanWrapper;
//    Class spanClass;
//    Map newMap;
//
//
//
//    for (int i=0; i<spanWrappers.size(); i++)
//     {
//       currentSpanWrapper= spanWrappers.get(i);
//       spanClass= currentSpanWrapper.span.getClass();
//
//       //perform
//       newMap= new HashMap();
//       newMap.put(TYPE, currentSpanWrapper.span.getClass().getName());
//       newMap.put(START, currentSpanWrapper.start);
//       newMap.put(END, currentSpanWrapper.end);
//       newMap.put(FLAGS, currentSpanWrapper.flags);
//
//
//       /**
//        * todo: New span classes must be added here in order to be saved
//        */
//       if (spanClass == ListItemSpan.class)
//       {
//         add( (ListItemSpan)currentSpanWrapper.span, newMap);
//       }
//       else
//       {
//         spansThatWerentHandled.add(spanClass.toString());
//       }
//
//
//
//
//
//
//     }
//
//
//
//    return;
//  }
//
//
//
//
//
//
//  public void add(Object span)
//  {
//    Class spanClass;
//
//    spanClass= span.getClass();
//
//    if (spanClass == ListItemSpan.class)
//    {
//      add( (ListItemSpan)span );
//    }
//    else
//    {
//      spansThatWerentHandled.add(spanClass.toString());
//    }
//
//
//    return;
//  }
//
//
//  /**
//   * These private add methods are for adding span specific itenms (eg. images, extra data.
//   * General Span info (class name, start, end, and flags are already added so don't need to be added again
//   * @param listItemSpan
//   * @param map
//   */
//
//  private void add(ListItemSpan listItemSpan /*, int start, int end, int flags*/, Map map)
//  {
//
//    //we are saving no extra data so we just save the map to the list
//    mList.add(map);
//
//    return;
//  }
//
//
////  //todo: implement!!!!!
////  private void add(ImageSpan imageSpan)
////  {
////    Map map= new HashMap();
////
////    map.put(TYPE, ImageSpan.class.toString());
////
////
////    return;
////  }
//
//
//
//

  static public String toJson(List<SpanWrapper> spanWrappers)
  {
    List<Map> mapList;

    mapList= new ArrayList();



    //////////////////////////////////////addAll()////////////////////////////////////
    SpanWrapper currentSpanWrapper;
    Class spanClass;
    Map newMap;



    for (int i=0; i<spanWrappers.size(); i++)
    {
      currentSpanWrapper= spanWrappers.get(i);
      spanClass= currentSpanWrapper.span.getClass();

      //perform
      newMap= new HashMap();
      newMap.put(TYPE, currentSpanWrapper.span.getClass().getName());
      newMap.put(START, currentSpanWrapper.start);
      newMap.put(END, currentSpanWrapper.end);
      newMap.put(FLAGS, currentSpanWrapper.flags);


      /**
       * todo: New span classes must be added here in order to be saved
       */
      if (spanClass == ListItemSpan.class)
      {
        mapList.add(newMap);
      }
      else
      {
        //spansThatWerentHandled.add(spanClass.toString());
      }
      //////////////////////////////////end addAll()////////////////////////////////////
    }

    return new Gson().toJson(mapList);
  }

//  /**
//   *
//   *
//   * @return
//   */
//  public String toJson()
//  {
//    return new Gson().toJson(mList);
//  }


  /**
   *
   * @param json
   * @return
   */
  static public List<SpanWrapper> fromJson(String json)
  {
    List<Map> mapList;
    List<SpanWrapper> spanWrappers;
    SpanWrapper tempSpanWrapper;

    Class spanClass;

    mapList= new Gson().fromJson(json, List.class);
    spanWrappers= new ArrayList();

    if (mapList!=null)
    {
      for (Map map: mapList)
      {

        try
        {
          //Do common field processing
          tempSpanWrapper= new SpanWrapper();
          tempSpanWrapper.span= Class.forName( (String)map.get(TYPE)).newInstance();                    //todo: this is throwing exceptions....
          tempSpanWrapper.start= ((Double)map.get(START)).intValue();
          tempSpanWrapper.end= ((Double)map.get(END)).intValue();
          tempSpanWrapper.flags= ((Double)map.get(FLAGS)).intValue();         //TODO: THIS IS BROKEN AS FLAG IS SAVED AS FLOAT!!!!!!!!!

          //do span type specific building
          spanClass= tempSpanWrapper.span.getClass();


          /**
           * todo: refactor
           * Make this like HtmlSpanner to make this easily extensible
           */
          if (spanClass == ListItemSpan.class)
          {
            fromJson( (ListItemSpan)tempSpanWrapper.span);
          }
          else
          {
            //span will not be loaded because not handled.
          }


          spanWrappers.add(tempSpanWrapper);
        }
        catch (Exception e)
        {
         e.printStackTrace();
        }

      }
    }

    return spanWrappers;
  }


  /**
   * edits input argument (same as output)
   * @param listItemSpan
   * @return
   */
  private static void fromJson(ListItemSpan listItemSpan)
  {
    //no specific processing needed for ListItemSpans... yet!
    return;
  }








 // private listtoJsonArray


  public interface JsonMapListInterface
  {
    //public JsonMapListInterface(String json);
    //
    public /*static*/ String toJson(List<SpanWrapper> spanWrappers);
    public /*static*/ List<SpanWrapper> fromJson(String Json);

  }


//  public void clear()
//  {
//    mList.clear();
//    return;
//  }

}     ////END CLASS////

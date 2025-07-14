package ndb.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.*;
import android.text.style.BulletSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import ndb.types.JsonAdapter;
import ndb.types.SpanWrapper;
import ndb.ui.spans.ListItemSpan;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 21/04/13
 * Time: 8:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class RichEditText extends EditText
{
  private InputFilter mInputFilter;

  private OnKeyListener mOnKeyListener;
  private TextWatcher mTextChangeWatcher;         //.TODO deprecated so DELETE

  private List<Object> mSpansToBeSaved;
  private Context mContext;


  public RichEditText(Context context)
  {
    super(context);
    mContext = context;
    initialize();
    return;
  }

  public RichEditText(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    mContext = context;
    initialize();
    return;
  }

  public RichEditText(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    mContext = context;
    initialize();
    return;
  }


  private void initialize()
  {


    setupSpansToBeSaved();
    setupListeners();
    return;
  }


  private void setupListeners()
  {
    //    mInputFilter= new InputFilter()
    //              {
    //                @Override
    //                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    //                {
    //                   CharSequence chunk;
    //
    //                  chunk= source.subSequence(start, end);
    //
    //                  if (chunk != null && chunk.equals('\n'))            //only hadles case where \n is typed
    //                  {                                                   //todo still need to handle paste/delete cases
    //
    //
    //                  }
    //
    //
    //
    //                  return null;
    //                }
    //              };


    //    mTextChangeWatcher= new TextWatcher()
    //            {
    //              boolean ignoreNextChange= false;   //to make sure
    //
    //
    //              @Override
    //              public void beforeTextChanged(CharSequence s, int start, int count, int after)
    //              {
    //                //don't use not needed!!!!!!!
    //              }
    //
    //              @Override
    //              public void onTextChanged(CharSequence s, int start, int before, int count)
    //              {
    //                //handles when ENTER is pressed on keyboard
    //                //todo: handle when \n gets added/ddeleted in cut or paste
    //                ///Toast.makeText(mContext, "onTextChanged called", Toast.LENGTH_SHORT).show();
    ////                if (s.equals('\n'))
    ////                {
    ////
    ////                }
    //                getEditableText().setSpan(new NewTextSpan(), start, start+count-1, Spanned.SPAN_COMPOSING);
    //
    //                return;
    //              }
    //
//                  @Override
//                  public void afterTextChanged(Editable s)
//                  {
//                    Object[] textChangedSpans;        //there should be only 1
//                    int start,
//                        end;
//
//
//
//                    textChangedSpans= s.getSpans(0, s.length()-1, NewTextSpan.class);
//
//                    if (textChangedSpans != null)
//                    {
//                      start= s.getSpanStart(textChangedSpans[0]);
//                      end=   s.getSpanEnd(textChangedSpans[0]);
//
//
//
//                      //use to temporarily turn of TextWatcher and prevent recursive calls
//                      ////////////////////////////////////////////////////////////////////////
//                      RichEditText.this.removeTextChangedListener(this);
//
//
//
//
//                      s.removeSpan(textChangedSpans[0]);
//                      RichEditText.this.addTextChangedListener(this);
//                      ////////////////////////////////////////////////////////////////////////
//                    }
//
//                    return;
//                  }
    //            };
    //    addTextChangedListener(mTextChangeWatcher);


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //TODO CHANGE TO TextWatcher  addTextChangeListener() is the only one guarenteed to work properly
    //    mOnKeyListener= new OnKeyListener()         //todo limited value... maybe replace witth
    //            {
    //
    //              @Override
    //              public boolean onKey(View v, int keyCode, KeyEvent event)
    //              {
    //                boolean isSuccess= false;
    //                Editable content;
    //                int cursorPosition,
    //                        start,
    //                        end;
    //                ListItemSpan[] currentListItemSpans;
    //
    //
    //                if ( (event.getAction() == KeyEvent.ACTION_DOWN) &&
    //                        (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) )
    //                {    //assert ENTER KEY was pressed
    //
    //                  //determine if cursor is in BulletSpan
    //                  content= getText();
    //                  cursorPosition= getSelectionStart();   //TODO THIS IS ONE AFTER CLOSE OF LISTITEMSPAN. PROBABLY RELATED TO LENGTH() VS LENGTH() -1!!!!!!!!!!!!!!!!!!
    //
    //                  currentListItemSpans = content.getSpans(cursorPosition, cursorPosition, ListItemSpan.class);
    //
    //
    //                  if ( currentListItemSpans.length > 0)   //assert cursor is in a BulletSpan
    //                  {
    //                    start= content.getSpanStart(currentListItemSpans[0]);
    //                    end= content.getSpanEnd(currentListItemSpans[0]);
    //                    //content.removeSpan(currentListItemSpans[0]);        // temporarily remove the span
    //                    toggleBullet(start);
    //                    content.insert(cursorPosition, "\n");
    //                    toggleBullet(start);
    //                  }
    //                  else
    //                  {
    //                    content.insert(cursorPosition, "\n");
    //                  }
    //
    //                  isSuccess= true;
    //                }
    //
    //                return isSuccess; //since EditText still needs to output keypress
    //              }
    //
    //
    //
    //            };
    //    setOnKeyListener(mOnKeyListener);


    //todo: close span????????????????
    //              //todo how
    //              /*
    //              Avenues:
    //
    //              1) write getBullet(int position) to return current bullet so can modify inclusive to exclusive to back again
    //              2) write addBulletBelow(int position). closes current bullet and adds a new one below while updating cursor position
    //               */

    ////////////////////////////////////////////////////////////////////////////////////////////


    mOnKeyListener = new OnKeyListener()         //todo limited value... maybe replace witth
    {

      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event)
      {
        boolean isSuccess = false;
        Editable content;
        int cursorPosition,
                start,
                end;
        ListItemSpan[] currentListItemSpans;


        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {    //assert ENTER KEY was pressed

          //determine if cursor is in BulletSpan
          content = getText();
          cursorPosition = getSelectionStart();   //TODO THIS IS ONE AFTER CLOSE OF LISTITEMSPAN. PROBABLY RELATED TO LENGTH() VS LENGTH() -1!!!!!!!!!!!!!!!!!!

          currentListItemSpans = content.getSpans(cursorPosition, cursorPosition, ListItemSpan.class);


          if (currentListItemSpans.length > 0)   //assert cursor is in a ListItemSpan
          {
            start = content.getSpanStart(currentListItemSpans[0]);
            end = content.getSpanEnd(currentListItemSpans[0]);
            //content.removeSpan(currentListItemSpans[0]);        // temporarily remove the span

            content.insert(cursorPosition, "\n");
            toggleBullet(start);      //close bullet. if this is before content.insert(cursorPosition, "\n"); then EditText doesn't show change
            toggleBullet(start);      //create new bullet with \n as endpoint
          } else
          {
            content.insert(cursorPosition, "\n");
          }


          isSuccess = true;
        }

        return isSuccess; //since EditText still needs to output keypress
      }


    };
    setOnKeyListener(mOnKeyListener);


    return;
  }


  /**
   * Removes any and all ListItemSpans at position
   *
   * @param position
   * @return
   */
  public boolean removeBullet(int position)
  {
    boolean isBulletRemoved = false;
    ListItemSpan[] currentListItemSpans;
    Editable content;


    content = getText();

    currentListItemSpans = content.getSpans(position, position, ListItemSpan.class);
    for (ListItemSpan listItemSpan : currentListItemSpans)
    {
      isBulletRemoved = true;
      content.removeSpan(listItemSpan);
    }

    return isBulletRemoved;
  }

  /**
   * inserts bullet in current block bounded by \n and start/end of text
   *
   * @param position
   */
  public void insertBullet(int position)
  {
    ListItemSpan[] currentListItemSpans;
    Editable content;
    int start,
            end;
    String s;

    content = getText();

    s = content.toString();

    start = s.lastIndexOf("\n", position - 1);
    start = start + 1; //for when returns -1 and so doesn't contain \n as first character in span

    end = s.indexOf("\n", position);
    if (end == -1)
    {
      end = content.length(); //or should it be content.length()?  YES!!!!!!!!

      //content.append(" ");      // added so that characters added to very end of buffer
    }                           //won't be in the span. Important for OnKeyListener implementation
    //    else if (end > start)
    //    {
    //      end= end-1;
    //    }

    content.setSpan(new ListItemSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

    return;
  }


  public void toggleBullet(int position)
  {

    Editable content;
    int start,
            end;
    String s;


    ListItemSpan[] currentListItemSpans;


    content = getText();

    s = content.toString();


    currentListItemSpans = content.getSpans(position, position, ListItemSpan.class);


    if (currentListItemSpans.length == 0)
    {
      insertBullet(position);
    } else //assert cursor is within a ListItemSpan so delete it
    {
      removeBullet(position);
    }

    setTextKeepState(content);


    return;
  }


  /**
   * inserted bullets are actually ListItemSpan's
   * Toggles a ListItemSpan (bullet) on/off for the text containing the cursor either
   * 1) bounded by '\n' (or start of text if no '\n' and the following '\n' or the end of text
   */
  public void toggleBullet()
  {
    toggleBullet(getSelectionStart());
    return;
  }


  //  public void insertBullet(int cursorPosition)
  //  {
  //
  //    Editable content;
  //    int     start,
  //            end;
  //    String s;
  //
  //
  //    ListItemSpan[] currentListItemSpans;
  //
  //
  //    content= getText();
  //
  //    s= content.toString();
  //
  //
  //    //get spans that contain the current cursor position
  ////    int cursorEndFix;
  ////    if (cursorPosition >= content.length())   //(cursorPosition == content.length()-1)
  ////    {
  ////      cursorEndFix= cursorPosition;
  ////    }
  ////    else
  ////    {
  ////      cursorEndFix= cursorPosition + 1;
  ////    }
  //    currentListItemSpans = content.getSpans(cursorPosition, cursorPosition, ListItemSpan.class);
  ////    Object[] acurrentListItemSpans = content.getSpans(cursorPosition,cursorPosition, Object.class);
  ////    currentListItemSpans= new ListItemSpan[0];
  //    //////////
  //
  //
  //    if( currentListItemSpans.length == 0 )
  //    { //assert cursor not inside a ListItemSpan so will add a new one
  //      start= s.lastIndexOf("\n", cursorPosition-1);
  //
  //      end= s.indexOf( "\n", cursorPosition) /*- 1*/; // -1 so we don't include the \n
  //
  //      start= start+1; //works fo all cases (eg. if \n is not found and returns -1
  //
  //
  //      if (end < 0)
  //      { //assert there are no \n in string after start
  //
  //        end= s.length()-1;
  //        if (end < 0)    // for 0 length edittexts.. probqably not needed
  //        {
  //          end= 0;
  //        }
  //
  //      }           //todo should enter be in bullet span or after? AFTER!!!!
  //
  //
  //     if (end < start)   //todo this is a hack... must be a better way...
  //     {
  //       end= start;
  //
  //     }
  //
  //      //this code is note necessary. i thought spans would become characters in text.
  ////      if (start >= end)
  ////      {
  ////        if (content.length() == end+1) //since the final index is 1 less than the length
  ////        {
  ////          content.append(" ");
  ////        }
  ////        end= start+1;
  ////      }
  //
  //
  //      content.setSpan(new ListItemSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
  //    }
  //    else //assert cursor is within a ListItemSpan so delete it
  //    {
  //      content.removeSpan(currentListItemSpans[0]);
  //    }
  //
  //    setTextKeepState(content);
  //
  //
  //    return;
  //  }


  /**     ******************the method causes problems freezing EditText input due to deleting spans!!!!!!!!!!!!******************************
   * This method is unnecessary as JsonAdapter choses which spans (inclusive) that will be saved
   *
   * This method no longer does anything and can safely be replaced with getText()
   * @return
   */

  public Editable _DEPRECATED_getCleanedText()
  {
    //TODO: just use clearComposingText() instead!!!!!!!!!!!!!!!!!!!!!!!

    Object[] spans;
    Editable content;

    clearComposingText();
    content = getText();

    /*  //TODO: This was dangerous as it was DELETING spans used by the EditText
    spans = content.getSpans(0, content.length() - 1, Object.class);


    for (int i = 0; i < spans.length; i++)
    {
      if (mSpansToBeSaved.contains(spans[i].getClass()) == false)
      {
        content.removeSpan(spans[i]);                ///////////TODO: THIS IS THE PROBLEM. I AM DELETING SPANS!!!!!!!!!!!
      }
    }
      */
    return content;
  }

  /**
   * returns ONLY the spans with the specified start and end points that are of the spanType
   *
   * @param start
   * @param end
   * @param spanType
   * @return
   */
  public List<Object> getSpansInclusive(int start, int end, Class spanType)
  {
    List<Object> resultList;
    Object[] spans;
    Editable content;

    resultList = new ArrayList<Object>();
    content = getText();
    spans = content.getSpans(0, content.length() - 1, spanType);

    for (int i = 0; i < spans.length; i++)
    {
      if (start == content.getSpanStart(spans[i]) && end == content.getSpanEnd(spans[i])
              && spanType.isInstance(spans[i]))      //runtime version of instanceof
      {
        resultList.add(spans[i]);
      }

    }


    return resultList;
  }

  /**
   * @return the index of the end of bullet or -1 if the cursor is not in a bullet
   */
  int getEndOfCurrentBullet()
  {
    int end;
    Object[] spans;

    spans = getText().getSpans(getSelectionStart(), getSelectionStart(), ListItemSpan.class);

    if (spans.length > 0)
    {
      end = getText().getSpanEnd(spans[0]);
    } else
    {
      end = -1;
    }

    return end;
  }


  public void closeBullet()
  {

    return;
  }

  public OnKeyListener getOnKeyListener()
  {
    return mOnKeyListener;
  }

  @Override
  public void addTextChangedListener(TextWatcher watcher)
  {
    super.addTextChangedListener(watcher);
    mTextChangeWatcher = watcher;
    return;
  }

  /**
   * note super class has a list (of possibly multiple TextWatchers so this function is at odds with EditText internal behaviour
   *
   * @return
   */
  public TextWatcher getTextChangedListener()
  {
    return mTextChangeWatcher;
  }


  /**
   * Transforms spans into Json format for a safe database storage format
   * @return
   */
  public String getSpansAsJson()
  {
    Editable text= null;
    Object[] spans= null;
    List<SpanWrapper> spanWrappers;
    SpanWrapper temp;

    String jsonString;


    spanWrappers = new ArrayList<SpanWrapper>();
    text = _DEPRECATED_getCleanedText();
    spans = text.getSpans(0, text.length() - 1, Object.class);

    if (spans !=null && spans.length > 0)
    {
            //create SpanWrappers (span along with start and end indexes)
      for (int i = 0; i < spans.length; i++)
      {
        temp = new SpanWrapper();
        temp.spanClass = spans[i].getClass();
        temp.start = text.getSpanStart(spans[i]);
        temp.end = text.getSpanEnd(spans[i]);
        temp.flags = text.getSpanFlags(spans[i]);
        temp.span = spans[i]; //currently not used. just for safety     //todo: disabled to fix problem
        spanWrappers.add(temp);
      }




      jsonString= JsonAdapter.toJson(spanWrappers);// jsonAdapter.toJson();
    }
    else
    {
      jsonString= null;
    }

    return jsonString;
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * ---Add Methods to RichEditText---
   * ---add new setText(Spannable text, Byte[] spans. Outside the blackbox implementation details are not important!!!---
   * Transfers the Spans into SPanWrappers and then saves them to a byte array;
   * <p/>
   * <p/>
   * todo: must make ALL spans serializable for this to work!!!!
   *
   * @return
   */
  public byte[] _DEPRECATED_getByteArrayOfSpans()  //toByteArray( /*Spanned spanned*/)          //getByteArrayOfSpans()
  {
    Editable text;
    //byte[] span;
    List<SpanWrapper> spanWrappers;
    SpanWrapper temp;
    Object[] spans;

    byte[] byteArray = null;     //this is the

    spanWrappers = new ArrayList<SpanWrapper>();


    text = _DEPRECATED_getCleanedText();
    spans = text.getSpans(0, text.length() - 1, Object.class);


    //create SpanWrappers (span along with start and end indexes)
    for (int i = 0; i < spans.length; i++)
    {
      temp = new SpanWrapper();

      temp.spanClass = spans[i].getClass();
      temp.start = text.getSpanStart(spans[i]);
      temp.end = text.getSpanEnd(spans[i]);
      temp.flags = text.getSpanFlags(spans[i]);
      temp.span = spans[i]; //currently not used. just for safety

      spanWrappers.add(temp);
    }

    //Write out Object
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    try
    {
      out = new ObjectOutputStream(bos);
      out.writeObject(spanWrappers);
      byteArray = bos.toByteArray();
      out.close();
      bos.close();

    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return byteArray;
  }


  //  @Override
  //  public void setText()


  /**
   * Sets the text. Spans come from spansAsJson
   *
   * @param text
   * @param spansAsJson
   */

  public void setText(String text, String spansAsJson)
  {
    List<SpanWrapper> spanWrappers = null;
    Object span;
    Editable textAndSpans;

    if (text == null)
    {
      text= "";
    }

    textAndSpans = new SpannableStringBuilder(text);

    if (spansAsJson != null)
    {
      spanWrappers= JsonAdapter.fromJson(spansAsJson);

      //convert SpanWrappers to spans for String
      for (int i = 0; i < spanWrappers.size(); i++)
      {
          textAndSpans.setSpan(spanWrappers.get(i).span, spanWrappers.get(i).start, spanWrappers.get(i).end, spanWrappers.get(i).flags);      //todo MUST DO FLAGS PROPERLY!!!!!!!
      }

    }

    textAndSpans.setFilters(new InputFilter[]{mInputFilter});
    setText(textAndSpans);

    return;

  }


  /**
   * CAUTION: THIS IS THE ONLY setText() method that will automatically apply the input filter. If using another
   * settText(0 method the filter must be added MANUALLY.
   *
   * @param text
   * @param spans
   */
  public void _DEPRECATED_setText(String text, byte[] spans)               //text should just be CharSequnce probably. Use it to create something Spannable to return.
  {                                                                             //or String as that is how it is stored in a note
    List<SpanWrapper> spanWrappers = null;
    Object span;
    Editable textAndSpans;

    textAndSpans = new SpannableStringBuilder(text);

    if (spans != null)
    {
      //Convert byte[] to SpanWrappers List
      ByteArrayInputStream bis = new ByteArrayInputStream(spans);
      ObjectInput in = null;
      try
      {
        in = new ObjectInputStream(bis);
        spanWrappers = (List<SpanWrapper>) in.readObject();
        bis.close();
        in.close();

      } catch (Exception e)
      {
        e.printStackTrace();
      }

      //convert SpanWrappers to spans for String
      for (int i = 0; i < spanWrappers.size(); i++)
      {
        //if (spanWrappers.get(i).spanClass == ListItemSpan.class)

        try
        {
          span = spanWrappers.get(i).spanClass.newInstance();         //todo this won't work with Images....back to rapid prototyping... JSON
          textAndSpans.setSpan(span, spanWrappers.get(i).start, spanWrappers.get(i).end, spanWrappers.get(i).flags);      //todo MUST DO FLAGS PROPERLY!!!!!!!
        } catch (Exception e)
        {
          Log.e("RichEditText", "Paramaterless constructor missing in Span:\n", e);
        }

      }

    }

    textAndSpans.setFilters(new InputFilter[]{mInputFilter});
    setText(textAndSpans);

    return;
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  /**
   * todo: not working
   *
   * @param uri
   */
  public void addImageAtCursor(Uri uri)
  {
    ImageSpan newImageSpan;
    Editable content;
    Bitmap bitmap;
    Drawable drawable;


    bitmap = BitmapFactory.decodeFile(uri.getPath());
    drawable = new BitmapDrawable(bitmap);
    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

    newImageSpan = new ImageSpan(/*mContext, bitmap, DynamicDrawableSpan.ALIGN_BOTTOM*/ drawable, ImageSpan.ALIGN_BASELINE);


    //newImageSpan= new ImageSpan(mContext, uri, DynamicDrawableSpan.ALIGN_BOTTOM);

    content = getEditableText();
    content.setSpan(newImageSpan, getSelectionStart(), getSelectionStart() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


    //    /////////////////////////////////////TESTING///////////////////////////////////
    //    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
    //    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  drawable.getIntrinsicHeight());
    //    SpannableString spannable = new SpannableString(getText().toString() + "[smile]");
    //    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
    //    spannable.setSpan(span, getText().length(),
    //            getText().length() + "[smile]".length(),
    //            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    //    setText(spannable);
    //    ///////////////////////////////////////////////////////////////////////////////


    return;
  }


  private void setupSpansToBeSaved()      //todo add new span here for every new one created!!!!!!
  {
    mSpansToBeSaved = new ArrayList<Object>();

    mSpansToBeSaved.add(ListItemSpan.class);
    mSpansToBeSaved.add(BulletSpan.class);
    mSpansToBeSaved.add(ImageSpan.class);
    return;
  }





//  /**
//   * Allows ordinary TextViews to process and show spansAsJson
//   */
//  public static TextView makeTextView(Context context, String text, String spansAsJson)
//  {
//    List<SpanWrapper> spanWrappers = null;
//    Editable textAndSpans;
//    TextView textView;
//
//    if (text == null)
//    {
//      text= "";
//    }
//
//    textAndSpans = new SpannableStringBuilder(text);
//
//    if (spansAsJson != null)
//    {
//      spanWrappers= JsonAdapter.fromJson(spansAsJson);
//
//      //convert SpanWrappers to spans for String
//      for (int i = 0; i < spanWrappers.size(); i++)
//      {
//        textAndSpans.setSpan(spanWrappers.get(i).span, spanWrappers.get(i).start, spanWrappers.get(i).end, spanWrappers.get(i).flags);      //todo MUST DO FLAGS PROPERLY!!!!!!!
//      }
//
//    }
//
//    textView= new TextView(context);//textAndSpans.setFilters(new InputFilter[]{mInputFilter});
//    textView.setText(textAndSpans);
//
//    return textView;
//
//  }

//  /**
//   * Helper method to turn spansAsJson and content into Editable so can be displayed by an ordinary TextView
//   */
//  public static Editable makeEditable(Context context, String text, String spansAsJson)
//  {
//    List<SpanWrapper> spanWrappers = null;
//    Editable textAndSpans;
//    TextView textView;
//
//    if (text == null)
//    {
//      text= "";
//    }
//
//    textAndSpans = new SpannableStringBuilder(text);
//
//    if (spansAsJson != null)
//    {
//      spanWrappers= JsonAdapter.fromJson(spansAsJson);
//
//      //convert SpanWrappers to spans for String
//      for (int i = 0; i < spanWrappers.size(); i++)
//      {
//        textAndSpans.setSpan(spanWrappers.get(i).span, spanWrappers.get(i).start, spanWrappers.get(i).end, spanWrappers.get(i).flags);      //todo MUST DO FLAGS PROPERLY!!!!!!!
//      }
//
//    }
//
//    return textAndSpans;
//
//  }




  ///////////////////////////////////
  ////////////INNER CLASSES//////////

  //  /**
  //   * Used for saving/loading spans to a byte[]
  //   */
  //  private static class SpanWrapper implements Serializable
  //  {
  //    public Object span;
  //    public int start;
  //    public int end;
  //    public int flags;
  //  }


} //////END CLASS//////

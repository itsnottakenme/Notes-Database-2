package ndb.ui.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ndb.R; //kanana.notesdatabase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 01/04/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchBox extends LinearLayout
{

  /**
   * Views
   */
  private View mView;

  private LinearLayout llSearchLine;
  private EditText etSearchTerm;
  private ImageButton ibResetSearchTerm,
                      ibResetTag;
  private TextView tvSelectedTag;
 // private ScrollView svTags;            //set height = 2*tvTag.getHeight();
  private FlowLayout flTags;
  private List<TextView> tvTagList;


  /**
   * Listeners
   */
  View.OnClickListener mTagsListener; //so can listen for dynacally created tags in FlowLayout
  SearchBoxListener mSearchBoxListener; //notify this object if change



  public SearchBox(Context context)
  {
    super(context);
    initialize();
    return;
  }

  public SearchBox(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initialize();
    return;
  }

  public SearchBox(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    initialize();
    return;
  }

  private void initialize()
  {
    LayoutInflater layoutInflater;

    layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (layoutInflater != null)
    {
      mView = layoutInflater.inflate(R.layout.search_frame, this);
    }

    llSearchLine= (LinearLayout) mView.findViewById(R.id.search_line);
    tvSelectedTag = (TextView)findViewById(R.id.selected_tag_textview);
    etSearchTerm= (EditText)findViewById(R.id.search_edit_text);
    ibResetSearchTerm = (ImageButton)findViewById(R.id.search_cancel_button);
    ibResetTag= (ImageButton)findViewById(R.id.tag_cancel_button);


    flTags= (FlowLayout)findViewById(R.id.search_list_tag_frame);

    tvTagList= new ArrayList<TextView>();


    //svTags.setVisibility(View.GONE);          //hide SearchBox by default
    //llSearchLine.setVisibility(View.GONE);    //
    hide(); //makes SearchBox GONE





    setupListeners();
    return;
  }

  //////////////88888888888888888888888888888888888888888888888888888888888

  private void loadTagsScrollList(List<String> tagList)
  {
    ////////////////set up tag textviews//////////////////////

    TextView tempTv;
    LinearLayout.LayoutParams params;


    tvTagList= new ArrayList<TextView>();
    //0000000000000000000

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    layoutParams.setMargins(30, 20, 30, 0);
    //000000000000000

    flTags.removeAllViews();    //todo some inefficiency here as without this all tags print twice.. so this method is always called twice!
    for (int i=0; i<tagList.size(); i++)
    {
      tempTv= new TextView(getContext());  //todo probably workds but if DOESN"T SHOW THISL INE BAD SO BAD!!!!!!!!!
      tempTv.setText(tagList.get(i));
      tempTv.setBackgroundResource(R.drawable.tag_background);//tempTv.setBackgroundResource(R.drawable.tag);
      /////////////////////////////
      tvTagList.add(tempTv);      //need to keep track so we can add listeners after
      flTags.addView(tempTv, layoutParams);
    }


    ///sets the above listener for each tvTag in FlowLayout
    for (int i=0; i<tvTagList.size(); i++)
    {
      tvTagList.get(i).setOnClickListener(mTagsListener);
    }


    return;
  }


  /**
   *
   */
  private void setupListeners()
  {


    /**
     * Cancel Button
     */
    ibResetSearchTerm.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        etSearchTerm.setText("");
      }
    });


    /**
     * textview tag clicked - pops up tags scrollview
     */
    tvSelectedTag.setOnClickListener(new OnClickListener()
                {
                  @Override
                  public void onClick(View v)
                  {
                    if (flTags.getVisibility() == View.GONE)     //svTags.getVisibility() == View.GONE
                    {
                      flTags.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                      flTags.setVisibility(View.GONE);
                    }
                  }
                });


    /**
     * to find out which one of the tags in FlowLayout was clicked
     */
    mTagsListener= new View.OnClickListener()
                {
                  @Override
                  public void onClick(View v)
                  {
                    TextView tag= (TextView)v;
                    tvSelectedTag.setText(tag.getText().toString());       //todo maybe set layout weight so this field only takes up 1/3 of line (or the margin after the textview)

                    mSearchBoxListener.onTagChange(tag.getText().toString());
                    flTags.setVisibility(View.GONE);
                    return;
                  }
                };

    /**
     * Resets the search tag
     */
    ibResetTag.setOnClickListener(new OnClickListener()
                {
                  @Override
                  public void onClick(View v)
                  {
                    tvSelectedTag.setText("");
                    mSearchBoxListener.onTagChange(tvSelectedTag.getText().toString());
                    etSearchTerm.setText(etSearchTerm.getText());    //purpose: calls the listener for etSearchTerm
                    return;
                  }
                });

    /**
     * //notifies subscriber everytime etSearchTerm changes
     */
    etSearchTerm.addTextChangedListener(new TextWatcher()
                {
                  @Override
                  public void beforeTextChanged(CharSequence s, int start, int count, int after)
                  {
                    //To change body of implemented methods use File | Settings | File Templates.
                  }

                  @Override
                  public void onTextChanged(CharSequence s, int start, int before, int count)
                  {
                    //To change body of implemented methods use File | Settings | File Templates.
                  }

                  @Override
                  public void afterTextChanged(Editable s)
                  {
                    //Toast.makeText(getContext(), s.toString(), Toast.LENGTH_SHORT).show();


                    if (mSearchBoxListener!=null)
                    {
                      mSearchBoxListener.onSearchStringChange(s.toString());
                    }


                    return;
                  }
                });




    return;
  }

         /*

  //todo probably not needed. Use setSearchParameters instead
  public List<String> getTagList()
  {
    return mTagList;
  }

  public void setTagList(List<String> tagList)
  {
    this.mTagList = tagList;     //todo should I clone this?

    //todo invalidate and redraw?
    return;
  }
           */

  /**
   * Makes widget visible
   */
  public void show()
  {
    llSearchLine.setVisibility(View.VISIBLE);
    tvSelectedTag.setVisibility(View.VISIBLE);
    return;
  }
  public void hide()
  {
    llSearchLine.setVisibility(View.GONE);
    flTags.setVisibility(View.GONE);
    tvSelectedTag.setVisibility(View.GONE);
    return;
  }


  public void toggleVisibility()
  {
    if (llSearchLine.getVisibility() == View.GONE)         //todo this is buggy because not handling svTags
    {
      //llSearchLine.setVisibility(View.VISIBLE);
      show();
      requestEditTextFocus();   //doesn't pop up keyboard
    }
    else
    {
      releaseEditTextFocus();
      hide();

    }

    return;
  }




  public void setSearchParameters(/*long notebookId,*/ List<String> tagList)
  {        //todo implement

    //Double LOTS OnFocusChangeListener CRAP!!!!!!!!! // or maybe don't need do anything/???

          loadTagsScrollList(/*notebookId,*/ tagList);

    return;
  }

  public void setListener(SearchBoxListener s)
  {
    mSearchBoxListener= s;
    return;
  }


  /**
   * Sets focus to contained EditText
   * note: requestFocus() is final and can't be overwritten
   * @return
   */
  public void requestEditTextFocus()
  {

    etSearchTerm.requestFocus();
    etSearchTerm.post(new Runnable()
                {
                  @Override
                  public void run() {
                    InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(etSearchTerm, 0);
                  }
                });

    return;// true; //etSearchTerm.requestFocus();
  }

  public void releaseEditTextFocus()
  {
    //etSearchTerm.clearFocus();
    etSearchTerm.requestFocus();
    etSearchTerm.post(new Runnable()
                {
                  @Override
                  public void run()
                  {
                    InputMethodManager keyboard = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(etSearchTerm.getWindowToken(), 0);
                  }
                });
    return;
  }




  /**
   * Interfaces
   */
  public interface SearchBoxListener
  {
    //todo: combine into a single method so it is easier to handle
    public abstract void onTagChange(String tag);
    public abstract void onSearchStringChange(String searchString);

  }



}     /////////////////////////////END CLASS///////////////////////////////


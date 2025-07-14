package ndb.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
public class TagsControl extends LinearLayout
{
//
//  /**
//   * Views
//   */
  private View mView;                   //the root view
  private EditText etTags;
  private Button bTagsDropDown;         //button that when pressed toggles visibility of flTags
  private FlowLayout flTags;
  private List<TextView> tvTagList;     //list of tags to be displayed in flTags



  /**
   * Listeners
   */
  View.OnClickListener mTagsListener; //so can listen for dynacally created tags in FlowLayout


  public TagsControl(Context context)
  {
    super(context);
    initialize();
    return;
  }

  public TagsControl(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initialize();
    return;
  }

  public TagsControl(Context context, AttributeSet attrs, int defStyle)
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
      mView = layoutInflater.inflate(R.layout.tag_control_frame, this);
      //mView.setBackgroundColor(66666);
    }
    etTags = (EditText)findViewById(R.id.tags_edit_text);
    flTags= (FlowLayout)findViewById(R.id.full_tag_list);
    bTagsDropDown= (Button)findViewById(R.id.tags_pop_up_button);

    flTags.setVisibility(View.GONE);

    setupListeners();
    return;
  }


    private void setupListeners()
    {
      /**
       * Toggles visibility of flTags componennt
       */
      bTagsDropDown.setOnClickListener(new OnClickListener()
                {
                  @Override
                  public void onClick(View v)
                  {
                    if (flTags.getVisibility() == View.GONE)
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
       * Adds the text of the selected tag to etTags
       */

      mTagsListener= new View.OnClickListener()
                {
                  @Override
                  public void onClick(View v)
                  {
                    TextView tag= (TextView)v;
                    etTags.setText( etTags.getText().toString()+ " " +tag.getText().toString() );
                    return;
                  }
                };
      return;
    }


  /**
   * Sets the tags in the EditText
   */
  public void setTags(String tags)
  {
    etTags.setText(tags);
  }



  /**
   * gets the tags from etTags
   */
  public String getTags()
  {
    return etTags.getText().toString();
  }


  public void setTagsMasterList(List<String> tagList)
  {
    loadTagsScrollList(tagList);
    return;
  }



  private void loadTagsScrollList(List<String> tagList)
  {
    ////////////////set up tag textviews//////////////////////

    TextView tempTv;
    LinearLayout.LayoutParams layoutParams;

    flTags.removeAllViews();
    tvTagList= new ArrayList<TextView>();

    layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(30, 20, 30, 0);


    for (int i=0; i<tagList.size(); i++)
    {
      tempTv= new TextView(getContext());
      tempTv.setText(tagList.get(i));
      tempTv.setBackgroundResource(R.drawable.tag_background);
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


//  public void setSearchParameters(/*long notebookId,*/ List<String> tagList)
//  {        //todo implement
//
//    //Double LOTS OnFocusChangeListener CRAP!!!!!!!!! // or maybe don't need do anything/???
//
//    loadTagsScrollList(/*notebookId,*/ tagList);
//
//    return;
//  }
//
//  public void setListener(SearchBoxListener s)
//  {
//    mSearchBoxListener= s;
//    return;
//  }
//
//
//  /**
//   * Sets focus to contained EditText
//   * note: requestFocus() is final and can't be overwritten
//   * @return
//   */
//  public boolean requestEditTextFocus()
//  {
//
//    return etSearchTerm.requestFocus();
//  }
//
//
//  /**
//   * Interfaces
//   */
//  public interface SearchBoxListener
//  {
//    public abstract void onTagChange(String tag);
//    public abstract void onSearchStringChange(String searchString);
//
//  }



}     /////////////////////////////END CLASS///////////////////////////////



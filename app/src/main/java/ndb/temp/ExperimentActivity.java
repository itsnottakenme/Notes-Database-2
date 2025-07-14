package ndb.temp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ndb.R; //kanana.notesdatabase.R;
import ndb.ui.activities.NotebookListActivity;
import ndb.db.*;
import ndb.types.NdbIntent;
import ndb.ui.views.SearchBox;

//import net.nightwhistler.htmlspanner.HtmlSpanner;


import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 30/03/13
 * Time: 9:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExperimentActivity extends Activity
{
  ActionBar mActionBar;
  SearchBox mSearchBox;


  final private String bulletList = "Begin list: <ul>  <li>Coffee</li>  <li>Milk</li>  </ul> ";





  TextView tvHtmlSpanner;

  //////////////////////////////////////////////////////////////////////////////////////////
  public void onCreate(Bundle savedInstanceState) //////////////////////////////////////////
  {
//    HtmlSpanner htmlSpanner;
//    htmlSpanner= new HtmlSpanner();
//
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout._experiment);
//
//
//    tvHtmlSpanner= (TextView)findViewById(R.id.debug_textView);
//
//
//
//    ////////////////////////////////////
//    Editable text;
////    HtmlSpannerJunior parser;
////    parser= new HtmlSpannerJunior();
//
//
//    text= new SpannableStringBuilder("Bob likes to eat.\n Bob is gay.\n I want it!!!!!!");
//
//    text.setSpan(new BulletSpan(), 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//
//
//    //////////////////////////////////////////////
//
//
//
//    //tvHtmlSpanner.setText(new HtmlSpannerJunior().toHtml(text) + " END ");
//
//    Spannable spannedText= htmlSpanner.fromHtml(bulletList) ;
//    tvHtmlSpanner.setText(spannedText ) ;
//
//
//
//
//
//
//
//
//
//
//
//
//






    // gets the activity's default ActionBar
    mActionBar = getActionBar();
    mActionBar.setTitle("Notebooks");
    ///////









    //importAwesomeNoteDbTestMethod();

    return;
  } /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////

  private void importAwesomeNoteDbTestMethod()
  {
    ImportDbHelper importDbHelper;

    SQLiteDatabase importDb;
    NoteDataSource tempDb,
                   nDb;



    NotebookTable importNotebookTable;
    NoteTable importNoteTable;

    //set up import notebook table cols
    importNotebookTable= new NotebookTable();
    importNotebookTable.table= "notefolder";
    importNotebookTable.titleCol= "title";
    importNotebookTable.idCol= "idx";
    importNotebookTable.ordinalCol= "listorder";
    importNotebookTable.dateModifiedCol= "regdate";
    importNotebookTable.setColumnsToImport( Arrays.asList("title", "listorder", "regdate") );


    //set up import note table cols
    importNoteTable= new NoteTable();
    importNoteTable.table="note";

    importNoteTable.idCol="idx";
    importNoteTable.titleCol="title";
    importNoteTable.contentCol="text";
    importNoteTable.tagsCol="tagids";         //todo this col needs special treatment!!!!!!!!
    importNoteTable.dateCreatedCol ="createdate";
    importNoteTable.dateModifiedCol ="regdate";
    importNoteTable.nbidCol= "folderidx";
    importNoteTable.setColumnsToImport( Arrays.asList("title", "text", "tagids", "createdate", "regdate" ));





    //create importDb, tempDb and nDb objects
    getApplicationContext().deleteDatabase(NDBTableMaster.TEMP_DB);
    tempDb= new NoteDataSource(this, NDBTableMaster.TEMP_DB);  //todo: careful if either of these lines is wrong
    nDb= new NoteDataSource(this, NDBTableMaster.NOTES_DB);                  //     then db becomes fucked!!!

    importDb= SQLiteDatabase.openDatabase("/storage/emulated/0/_import/temp/notebase.db", null,SQLiteDatabase.OPEN_READONLY);

    importDbHelper= new ImportDbHelper(importDb, tempDb, nDb);


    importDbHelper.setImportTables(importNotebookTable, importNoteTable);

    tempDb.open();
    nDb.open();
    importDbHelper.importDbToTempDb();


    importDbHelper.addTempDbToNdb();



    nDb.close();
    tempDb.close();



    Intent intent= new Intent();
    intent = new Intent(getApplicationContext(), NotebookListActivity.class);
    intent.putExtra(NdbIntent.DATABASE_NAME, NDBTableMaster.NOTES_DB );

    startActivity(intent);




    return;
  }




  public void setupSearchBoxStuff()
  {
    SearchBox searchBox= (SearchBox) findViewById(R.id.search_box);



    //searchBox.setOntextChangesListener();   //todo make a listsner that is called when searchbox text changes or tag changes
    //searchBox.setSearchParameters(mNotebook.getGuid(), tagList);
    searchBox.show();



    return;
  }



  @Override     //this should be in all activities for ActionBar i believe
  public boolean onCreateOptionsMenu(Menu menu)
  {
    LinearLayout llBottomBar;
    View inflatedView;



    getMenuInflater().inflate(R.menu.notebook_list_menu, menu);
//    llBottomBar = (LinearLayout) menu.findItem(R.id.save_option).getActionView();
//    inflatedView = getLayoutInflater().inflate(R.layout.notebook_list_activity_bottom_bar, null);
//    llBottomBar.addView(inflatedView);

    ///////////////////////////////////
    //getMenuInflater().inflate(R.menu.browser_main, menu);   ------------- OK
    //RelativeLayout relativeLayout = (RelativeLayout) menu.findItem(R.id.layout_item).getActionView();
    //View inflatedView = getLayoutInflater().inflate(R.layout.media_bottombar, null);
    //relativeLayout.addView(inflatedView);
    /////////////////////////////////



   return true;
  }






}   /////////END CLASS//////////
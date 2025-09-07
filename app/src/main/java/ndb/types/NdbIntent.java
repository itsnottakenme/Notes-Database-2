package ndb.types;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 19/03/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class NdbIntent
{
  /**
   * KEYS
   */
  final public static String  ACTION_TYPE=          "NdbIntent.ACTION_TYPE",
                              VIEW_TYPE=            "NdbIntent.VIEW_TYPE";


  /**
   * DEPRECATED... BUT STILL IN USE LOL
   * The different intent types
   * TODO: BREAK THESE DOWN
   */
  final public static String
                                CREATE_NOTEBOOK =   "CREATE_NOTEBOOK",
                                EDIT_NOTEBOOK=      "EDIT_NOTEBOOK",
                                VIEW_NOTEBOOK=      "VIEW_NOTEBOOK",           //views the notes in given notebook
                                SEARCH=        "SEARCH_NOTE";      //Make class used to specify information for searches?


  /**
   * Broken down intent cases
   * REPLACEMENT FOR ABOVE INTENT TYPES
   */
  final public static String
                                CREATE= "CREATE",
                                EDIT= "EDIT"
                                /*VIEW= "VIEW"*/;


  final public static String
                                NOTE_VIEW= Notebook.ViewType.NORMAL.toString(), //"NOTE_VIEW",
                                TODO_VIEW= Notebook.ViewType.TODO.toString(), //"TODO_VIEW";
                                GROCERY_VIEW= Notebook.ViewType.GROCERIES.toString();



  //to pass around name of database
  public static String DATABASE_NAME=  "DATABASE_NAME";


  //Intents for launching activities. Strings are taken from android manifest file.
  public static String LAUNCH_EDIT_NOTE_ACTIVITY= "EditNoteActivity.intent.action.Launch";




  //Result codes that intents return ?????
  // MAY NOT NEED THIS as RESULT_OK and RESULT_CANCEL may give enough information
  // as i believe updateUi() is all that would need to be called
  public static String  NEW_NOTE_SUCCESSFUL="ADD_NOTE_SUCCESSFUL";



  //todo: These are for saving and



//  public static class Extra
//  {
//
//  }







}

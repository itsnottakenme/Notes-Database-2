package ndb.types;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 23/03/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sort
{
  public final static String  DATE_MODIFIED = Note.DATE_MODIFIED/*"SORT_DATE_MODIFIED"*/,       //todo ugly hack.. notice only works for notes...
                              DATE_CREATED = "SORT_DATE_CREATED",
                              DATE_DUE=       "SORT_DATE_DUE",
                              TITLE=        "SORT_TITLE",
                              ALPHABETICAL= "SORT_ALPHABETICAL",

                              ASCENDING=  "SORT_ASCENDING",
                              DESCENDING= "SORT_DESCENDING";




}           ////////////END CLASS////////////

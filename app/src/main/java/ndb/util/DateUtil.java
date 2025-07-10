package ndb.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 29/07/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil
{
  final public static long MILLISECONDS_IN_A_DAY= 86400000;

  public static String getCurrentFormattedDateForFile()
  {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
    Date date= new Date();

    return dateFormat.format(date);
  }



  public static String formatDate(Date date)
  {
    DateFormat dateFormat= new SimpleDateFormat("EEEEEEEEE MMMMMMMMMMMMM d, yyyy");

    return dateFormat.format(date);


  }
  public static String formatDate(long timeInMilliseconds)
  {
    return formatDate(new Date(timeInMilliseconds));
  }




  /**
   *
   * @param date
   * @return Returns the start of day in milliseconds
   */
  public static Date getStartOfDay(Date date)
  {

    Calendar cal = Calendar.getInstance();
    cal.setTime(date); // compute start of the day for the timestamp
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  public static Date getStartOfDay(long timeInMilliseconds)
  {
    return getStartOfDay(new Date(timeInMilliseconds));
  }

  /**
   * returns a date object set to the last millisecond of the day
   * @param date
   * @return
   */
  public static Date getEndOfDay(Date date)
  {
    long time;
    /**
     * add a day; get the start of that day; -1 to make it last millisecond of proper day
     */
    time= addNumberOfDays(getStartOfDay(date), +1).getTime() -1;


    return new Date(time); //new Date(date.getTime()+ MILLISECONDS_IN_A_DAY-1);
  }

  public static Date getEndOfDay(long timeInMilliseconds)
  {
    return  getEndOfDay(new Date(timeInMilliseconds+MILLISECONDS_IN_A_DAY-1));
  }


  public static Date addNumberOfDays(Date date, int numberOfDays)
  {
    return new Date(date.getTime()+ MILLISECONDS_IN_A_DAY*numberOfDays);
  }



  /**
   *
   * @param timeInMilliseconds   the date returned will be the first matching instance after this date
   * @param repeatingField
   * @param repeatingValue
   * @return
   */
  public static long getNext(long timeInMilliseconds, int repeatingField, int repeatingValue)
  {
    Calendar cDate = Calendar.getInstance();

    cDate.setTimeInMillis(timeInMilliseconds);

    if (repeatingField == Calendar.DAY_OF_WEEK)
    {
      do
      {
        cDate.add(Calendar.DATE, 1);
      } while(cDate.get(Calendar.DAY_OF_WEEK) != repeatingValue);

    }


    return cDate.getTimeInMillis();
  }


  /**
   * Calculates the number of calendar days in between start and end.
   *
   * Gets the start of the day of both times input and calculates the days between.
   * @param startTimeInMilliseconds
   * @param endTimeInMilliseconds
   * @return
   */
  public static int getDaysBetween(long startTimeInMilliseconds, long endTimeInMilliseconds)
  {
    long    start,
            end,
            between;

    start= getStartOfDay(startTimeInMilliseconds).getTime();
    end= getStartOfDay(endTimeInMilliseconds).getTime();

    between= end- start; //endTimeInMilliseconds - startTimeInMilliseconds;


    return (int)(between/MILLISECONDS_IN_A_DAY);
  }





}          ////END CLASS////

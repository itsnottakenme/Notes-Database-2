package ndb.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import ndb.R; //kanana.notesdatabase.R;
import ndb.types.Note;
import ndb.util.DateUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 14/09/13
 * Time: 6:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class DueDateDialog extends AlertDialog //implements android.view.View.OnClickListener
{
  /**
   * Views
   */
  TextView tvFriendlyDate,
           tvRelativeDays;
  DatePicker dpDatePicker;
  Spinner spRepeatingDueDate;
  Button bOk,
         bCancel;



  /**
   * Listeners
   **/
  DueDateDialogListener mDueDateDialogListener;
  DatePicker.OnDateChangedListener mOnDateChangedListener;


  /**
   * These are for recovering previous values in dialog
   * when cancelButton is pressed
   */
//  private int   savedYear,
//                savedMonthOfYear,
//                savedDayOfMonth,
//                savedRepeatingField,
//                savedRepeatingValue;

  public DueDateDialog(Context context)
  {
    super(context);

    LayoutInflater inflater;
    View view;


    inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    view= inflater.inflate(R.layout.due_date_dialog, null);
    setView(view);

    dpDatePicker= (DatePicker)view.findViewById(R.id.date_picker);
    bOk= (Button)view.findViewById(R.id.ok_button);
    bCancel= (Button)view.findViewById(R.id.cancel_button);
    spRepeatingDueDate = (Spinner)view.findViewById(R.id.repeating_due_date_spinner);
    tvFriendlyDate= (TextView)view.findViewById(R.id.friendly_date_textview);
    tvRelativeDays= (TextView)view.findViewById(R.id.relative_days_textview);


    spRepeatingDueDate.setAdapter(new ArrayAdapter<Note.RepeatingDates>(getContext(), android.R.layout.simple_spinner_item, Note.RepeatingDates.values()));

    setupListeners();


    return;
  }



  private void setupListeners()
  {
    /**
     * DatePicker - updates friendly date and relative days
     */
    mOnDateChangedListener= new DatePicker.OnDateChangedListener()
            {
              @Override
              public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
              {
               // updateFriendlyDate(year, monthOfYear, dayOfMonth);                  //leads to infinite recursion :(

                Calendar cDueDate= new GregorianCalendar();
                int relativeDays;

                cDueDate.set(year, monthOfYear, dayOfMonth);
                tvFriendlyDate.setText(DateUtil.formatDate(cDueDate.getTimeInMillis()));

                /**
                 * Set up friendly date and relative days
                 */
                cDueDate.set(year, monthOfYear, dayOfMonth);
                tvFriendlyDate.setText(DateUtil.formatDate(cDueDate.getTimeInMillis()));


                relativeDays= DateUtil.getDaysBetween(Calendar.getInstance().getTimeInMillis(), cDueDate.getTimeInMillis());
                if (relativeDays > 0)
                {
                  tvRelativeDays.setText("+"+relativeDays);
                }
                else
                {
                  tvRelativeDays.setText(""+relativeDays);
                }

                return;

              }
            };
    dpDatePicker.init(0,0,0, mOnDateChangedListener);


    /**
     * Ok button
     */
    bOk.setOnClickListener(new View.OnClickListener()
            {
              @Override
              public void onClick(View v)
              {
                int repeatingField,
                    repeatingValue;

//                /**
//                 * Saved settings so if dialog is canceled settings can be reset
//                 */
//                savedYear= year;
//                savedMonthOfYear= monthOfYear;
//                savedDayOfMonth= dayOfMonth;
//                savedRepeatingField= repeatingField;
//                savedRepeatingValue= repeatingValue;
//                /***/



                if (spRepeatingDueDate.getSelectedItemPosition() == 0)
                {
                  repeatingField= -1;
                  repeatingValue= -1;
                }
                else
                {
                  repeatingField= Calendar.DAY_OF_WEEK;
                  repeatingValue= spRepeatingDueDate.getSelectedItemPosition();
                };


                mDueDateDialogListener.onDueDateSet(dpDatePicker.getYear(), dpDatePicker.getMonth(), dpDatePicker.getDayOfMonth(),
                        repeatingField, repeatingValue);
                dismiss();
              }
            });

    /**
     * Cancel button
     */
    bCancel.setOnClickListener(new View.OnClickListener()
            {
              @Override
              public void onClick(View v)
              {
                //setDueDate(savedYear, savedMonthOfYear, savedDayOfMonth, savedRepeatingField, savedRepeatingValue);
                mDueDateDialogListener.onCancel();

                dismiss();
              }
            });


    return;
  }




  public DueDateDialogListener getDueDateDialogListener()
  {
    return mDueDateDialogListener;
  }

  public void setDueDateDialogListener(DueDateDialogListener dueDateDialogListener)
  {
    this.mDueDateDialogListener = dueDateDialogListener;
  }


  /** Sets the fields for the DueDateDialog
   * the only repeatingField currently suopported in Calendar.DAY_OF_WEEK
   *
   * This should be called after dialog is canceled after the dialog is canceled as the fields
   * in dialog are not automatically reset
   */
  public void setDueDate(int year, int monthOfYear, int dayOfMonth, int repeatingField, int repeatingValue)
  {
//    updateFriendlyDate(year, monthOfYear, dayOfMonth);    //leads to infinite recursion :(
    Calendar cDueDate = new GregorianCalendar();
    int relativeDays;

//    /**
//     * Saved settings so if dialog is canceled settings can be reset
//     */
//    savedYear= year;
//    savedMonthOfYear= monthOfYear;
//    savedDayOfMonth= dayOfMonth;
//    savedRepeatingField= repeatingField;
//    savedRepeatingValue= repeatingValue;
//    /***/

    dpDatePicker.updateDate(year, monthOfYear, dayOfMonth);

    /**
     * Set up friendly date and relative days
     */
    cDueDate.set(year, monthOfYear, dayOfMonth);
    tvFriendlyDate.setText(DateUtil.formatDate(cDueDate.getTimeInMillis()));


    relativeDays= DateUtil.getDaysBetween(Calendar.getInstance().getTimeInMillis(), cDueDate.getTimeInMillis());
    if (relativeDays > 0)
    {
      tvRelativeDays.setText("+"+relativeDays);
    }
    else
    {
      tvRelativeDays.setText(""+relativeDays);
    }



    /**
     * Repeating due date spinner
     */
    if (repeatingField == Calendar.DAY_OF_WEEK)
    {
      if (repeatingValue == -1)
      {
        spRepeatingDueDate.setSelection(0);
      }
      else
      {
        spRepeatingDueDate.setSelection(repeatingValue);
      }
    }
    else
    {
      spRepeatingDueDate.setSelection(0);     //should be NONE
    }


    return;
  }


//  /**
//   * Updates tvFriendlyDate and tvRelativeDays
//   *
//   * @param year
//   * @param monthOfYear
//   * @param dayOfMonth
//   */
//  private void updateFriendlyDate(int year, int monthOfYear, int dayOfMonth)
//  {
//    Calendar cDueDate = new GregorianCalendar();
//    int relativeDays;
//
//    dpDatePicker.updateDate(year, monthOfYear, dayOfMonth);
//
//    /**
//     * Set up friendly date and relative days
//     */
//    cDueDate.set(year, monthOfYear, dayOfMonth);
//    tvFriendlyDate.setText(DateUtil.formatDate(cDueDate.getTimeInMillis()));
//
//
//    relativeDays= DateUtil.getDaysBetween(Calendar.getInstance().getTimeInMillis(), cDueDate.getTimeInMillis());
//    if (relativeDays > 0)
//    {
//      tvRelativeDays.setText("+"+relativeDays);
//    }
//    else
//    {
//      tvRelativeDays.setText(""+relativeDays);
//    }
//
//
////
////    /**
////     * RepeatingDueDate spinner
////     */
////    if (repeatingField == Calendar.DAY_OF_WEEK)
////    {
////      if (repeatingValue == -1)
////      {
////        spRepeatingDueDate.setSelection(0);
////      }
////      else
////      {
////        spRepeatingDueDate.setSelection(repeatingValue);
////      }
////    }
////    else
////    {
////      spRepeatingDueDate.setSelection(0);     //should be NONE
////    }
//
//    return;
//  }






  /**
   * Interface
   */
  public interface DueDateDialogListener
  {
    public void onCancel();
    public void onDueDateSet(int year, int monthOfYear, int dayOfMonth, int repeatingField, int repeatingValue);
  }












}      ////END CLASS////


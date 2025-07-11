/*
 * Copyright (C) 2012 Dominik Schürmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ndb.ui.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

import java.io.Serializable;


//TODO add HtmlSpanner to LICENSES
//todo code niow from BulletSpan


/**
 * This class is similar to Android's BulletSpan with the following differences:
 * <p/>
 * - Less options
 * <p/>
 * - Instead of drawing a circle we are using a utf8 sign as a bullet (for more
 * compatibility)
 * <p/>
 * - Also supports ordered lists with numbers in front of the item
 */
public class ListItemSpan implements LeadingMarginSpan, Serializable
{


  private final int mGapWidth;
  private final boolean mWantColor;
  private final int mColor;

  private static final int BULLET_RADIUS = 10;                    //default was 3
  public static final int STANDARD_GAP_WIDTH = 20;               //default was 2

  public ListItemSpan()
  {
    mGapWidth = STANDARD_GAP_WIDTH;
    mWantColor = false;
    mColor = 0;
  }

  public ListItemSpan(int gapWidth)
  {
    mGapWidth = gapWidth;
    mWantColor = false;
    mColor = 0;
  }

  public ListItemSpan(int gapWidth, int color)
  {
    mGapWidth = gapWidth;
    mWantColor = true;
    mColor = color;
  }

  public ListItemSpan(Parcel src)
  {
    mGapWidth = src.readInt();
    mWantColor = src.readInt() != 0;
    mColor = src.readInt();
  }

  //  public int getSpanTypeId() {
  //    return TextUtils.BULLET_SPAN;
  //  }

  public int describeContents()
  {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(mGapWidth);
    dest.writeInt(mWantColor ? 1 : 0);
    dest.writeInt(mColor);
  }

  public int getLeadingMargin(boolean first)
  {
    return 2 * BULLET_RADIUS + mGapWidth;
  }

  public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom,
                                CharSequence text, int start, int end, boolean first, Layout l)
  {
    if (((Spanned) text).getSpanStart(this) == start)
    {
      Paint.Style style = p.getStyle();
      int oldcolor = 0;

      if (mWantColor)
      {
        oldcolor = p.getColor();
        p.setColor(mColor);
      }

      p.setStyle(Paint.Style.FILL);

      c.drawCircle(x + dir * BULLET_RADIUS, (top + bottom) / 2.0f, BULLET_RADIUS, p);

      if (mWantColor)
      {
        p.setColor(oldcolor);
      }

      p.setStyle(style);

    }
    return;
  }


  //  private final int mNumber;
  //
  //  private static final int BULLET_RADIUS = 3;
  //  private static final int NUMBER_RADIUS = 5;
  //
  //  public static final int STANDARD_GAP_WIDTH = 20;       //2
  //
  //  public ListItemSpan()
  //  {
  //    mNumber = -1;
  //  }
  //
  //  public ListItemSpan(int number)
  //  {
  //    mNumber = number;
  //  }
  //
  //  public int getLeadingMargin(boolean first)
  //  {
  //    if (mNumber != -1)
  //    {
  //      return 2 * NUMBER_RADIUS + STANDARD_GAP_WIDTH;
  //    } else
  //    {
  //      return 2 * BULLET_RADIUS + STANDARD_GAP_WIDTH;
  //    }
  //  }
  //
  //
  //
  //  @Override
  //  public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
  //                                int top, int baseline, int bottom,
  //                                CharSequence text, int start, int end,
  //                                boolean first, Layout l) {
  //    if (((Spanned) text).getSpanStart(this) == start) {
  //      Paint.Style style = p.getStyle();
  //      int oldcolor = 0;
  //
  //      if (mWantColor) {
  //        oldcolor = p.getColor();
  //        p.setColor(mColor);
  //      }
  //
  //      p.setStyle(Paint.Style.FILL);
  //
  //      c.drawCircle(x + dir * BULLET_RADIUS, (top + bottom) / 2.0f,
  //              BULLET_RADIUS, p);
  //
  //      if (mWantColor) {
  //        p.setColor(oldcolor);
  //      }
  //
  //      p.setStyle(style);
  //    }
  //  }
  //
  //
  //
  //
  //
  //
  //
  ////  //Orginal HtmlSpanner version
  ////  public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top,
  ////                                int baseline, int bottom, CharSequence text, int start, int end,
  ////                                boolean first, Layout l)
  ////  {
  ////    if (((Spanned) text).getSpanStart(this) == start)
  ////    {
  ////      Paint.Style style = p.getStyle();
  ////
  ////      p.setStyle(Paint.Style.FILL);
  ////
  ////      if (mNumber != -1)
  ////      {
  ////        c.drawText(mNumber + ".", x + dir, baseline, p);
  ////      } else
  ////      {
  ////        c.drawText("\u2022", x + dir, baseline, p);
  ////      }
  ////
  ////      p.setStyle(style);
  ////    }
  ////    return;
  ////  }
  //
  //
  //
  //


} ////END CLASS////
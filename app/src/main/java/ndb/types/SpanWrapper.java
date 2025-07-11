package ndb.types;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 26/07/13
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */

import java.io.Serializable;

/**
 * Used for saving/loading spans to a byte[]
 */
public class SpanWrapper implements Serializable
{
  public Class spanClass;
  public int start;
  public int end;
  public int flags;

  public Object span;  //todo: this is left in for maximum compatibility. Currently not used
  //perhaps load this by default, but if exception occurs (eg. newer version of class)
  //then default to using spanClass
  //Still will lose the image if ImageSpan class changes though

  //public List<SpanWrappers> constructSpanList(byte[] spans) //use json for this?

}





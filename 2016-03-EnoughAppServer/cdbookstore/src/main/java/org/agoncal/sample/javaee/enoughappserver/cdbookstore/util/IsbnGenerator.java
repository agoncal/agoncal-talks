package org.agoncal.sample.javaee.enoughappserver.cdbookstore.util;

import javax.inject.Inject;
import java.util.logging.Logger;

@ThirteenDigits
public class IsbnGenerator
{

   @Inject
   private Logger logger;
   @Inject
   @ThirteenDigits
   private String prefix;
   @Inject
   @ThirteenDigits
   private int postfix;

   public String generateNumber()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.restatom.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.xcmis.core.CmisChoiceId;
import org.xcmis.restatom.AtomCMIS;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ChoiceIdElement extends ChoiceElement<CmisChoiceId>
{

   /**
    * Instantiates a new choice id element.
    * 
    * @param internal the internal
    */
   public ChoiceIdElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new choice id element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public ChoiceIdElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   public void build(CmisChoiceId choice)
   {
      if (choice != null)
      {
         super.build(choice);
         if (choice.getValue() != null && choice.getValue().size() > 0)
         {
            for (String v : choice.getValue())
            {
               if (v != null)
                  addSimpleExtension(AtomCMIS.VALUE, v);
            }
         }
         if (choice.getChoice() != null && choice.getChoice().size() > 0)
         {
            for (CmisChoiceId ch : choice.getChoice())
            {
               ChoiceIdElement el = addExtension(AtomCMIS.CHOICE);
               el.build(ch);
            }
         }
      }
   }

}
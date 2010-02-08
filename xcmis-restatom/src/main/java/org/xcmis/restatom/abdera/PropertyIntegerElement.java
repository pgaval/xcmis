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
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyInteger;
import org.xcmis.restatom.AtomCMIS;

import java.math.BigInteger;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PropertyIntegerElement extends PropertyElement<CmisPropertyInteger>
{

   /**
    * Instantiates a new property integer element.
    * 
    * @param internal the internal
    */
   public PropertyIntegerElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new property integer element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public PropertyIntegerElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void build(CmisProperty value)
   {
      if (value != null)
      {
         super.build(value);

         List<BigInteger> listBigInteger = ((CmisPropertyInteger)value).getValue();
         if (listBigInteger != null && listBigInteger.size() > 0)
         {
            for (BigInteger v : listBigInteger)
               addSimpleExtension(AtomCMIS.VALUE, v.toString());
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisPropertyInteger getProperty()
   {
      CmisPropertyInteger i = new CmisPropertyInteger();
      processPropertyElement(i);
      if (getElements() != null && getElements().size() > 0)
      {
         for (Element el : getElements())
            i.getValue().add(new BigInteger(el.getText()));
      }
      return i;
   }

}
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

package org.xcmis.sp.jcr.exo.NEW;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class FolderDataImpl extends AbstractObjectData implements FolderData
{

   public FolderDataImpl(Node node, TypeDefinition type)
   {
      super(node, type);
   }

   public void addObject(ObjectData object) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public ObjectData createChild(TypeDefinition type) throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<ObjectData> getChildren(String orderBy)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean hasChildren()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isAllowedChildType(String typeId)
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isRoot()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void removeObject(ObjectData object)
   {
      // TODO Auto-generated method stub

   }

}

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

package org.xcmis.sp.jcr.exo.query.qom;

import org.xcmis.search.lucene.search.visitor.DocumentMatcherFactory;
import org.xcmis.search.qom.source.join.ChildNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.DescendantNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.EquiJoinConditionImpl;
import org.xcmis.search.qom.source.join.SameNodeJoinConditionImpl;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class DocumentMatcherFactoryImpl extends DocumentMatcherFactory
{

   /**
    * Instantiates a new document matcher factory impl.
    */
   public DocumentMatcherFactoryImpl()
   {
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(final ChildNodeJoinConditionImpl node, final Object context) throws Exception
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(final DescendantNodeJoinConditionImpl node, final Object context) throws Exception
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(final EquiJoinConditionImpl node, final Object context) throws Exception
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(final SameNodeJoinConditionImpl node, final Object context) throws Exception
   {
      throw new UnsupportedOperationException();
   }
}

/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.qom.constraint;

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.query.qom.SameNode;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class SameNodeImpl extends ConstraintImpl implements SameNode
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * An absolute path.
    */
   private final QPath path;

   /**
    * The name of a selector.
    */
   private final InternalQName selectorName;

   /**
    * @param selectorName - The name of a selector.
    * @param path - An absolute path.
    */
   public SameNodeImpl(LocationFactory locationFactory, InternalQName selectorName, QPath path)
   {
      super(locationFactory);
      this.selectorName = selectorName;
      this.path = path;
   }

   /**
    * {@inheritDoc}
    */
   public Object accept(QueryObjectModelVisitor visitor, Object context) throws Exception
   {
      return visitor.visit(this, context);
   }

   /**
    * {@inheritDoc}
    */
   public String getPath()
   {
      return getJCRPath(path);
   }

   /**
    * {@inheritDoc}
    */
   public QPath getQPath()
   {
      return path;
   }

   /**
    * {@inheritDoc}
    */
   public String getSelectorName()
   {
      return getJCRName(selectorName);
   }

   @Override
   public Set<String> getSelectorsNames()
   {
      Set<String> selectors = new HashSet<String>();
      selectors.add(getSelectorName());
      return selectors;
   }
}
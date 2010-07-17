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
package org.xcmis.search.qom;

import org.xcmis.search.qom.constraint.InFolderNode;
import org.xcmis.search.qom.constraint.InTreeNode;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id$
 */
public interface ExtendedQueryObjectModelFactory
{

   /**
    * This is a predicate function that tests whether or not a candidate object
    * is a child-object of the folder object identified by the given <folder id>.
    * 
    * @param selectorName - The value of this optional parameter shall be the
    *          name of one of the Virtual Tables listed in the FROM clause for
    *          the query. non-null
    * @param id - The value of this parameter shall be the ID of a folder object
    *          in the repository. non-null
    * @return the constraint; non-null
    * @throws InvalidQueryException if a particular validity test is possible on
    *           this method, the implementation chooses to perform that test (and
    *           not leave it until later, on {@link #createQuery}), and the
    *           parameters given fail that test
    * @throws RepositoryException if the operation otherwise fails
    */
   public InFolderNode inFolder(String selectorName, String id) throws InvalidQueryException, RepositoryException;

   /**
    * This is a predicate function that tests whether or not a candidate object
    * is a descendant-object of the folder object identified by the given <folder
    * id>.
    * 
    * @param selectorName - The value of this optional parameter shall be the
    *          name of one of the Virtual Tables listed in the FROM clause for
    *          the query. non-null
    * @param id - The value of this parameter shall be the ID of a folder object
    *          in the repository. non-null
    * @return the constraint; non-null
    * @throws InvalidQueryException if a particular validity test is possible on
    *           this method, the implementation chooses to perform that test (and
    *           not leave it until later, on {@link #createQuery}), and the
    *           parameters given fail that test
    * @throws RepositoryException if the operation otherwise fails
    */
   public InTreeNode inTree(String selectorName, String id) throws InvalidQueryException, RepositoryException;

}
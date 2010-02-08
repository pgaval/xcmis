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
package org.xcmis.search.index;

import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS. <br/>Date:
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id$
 */
public interface Recoverer
{

   /**
    * Recover all changes stored in TransactionLog files.
    * 
    * @throws RepositoryException exception on receiving data from repository
    *           occurs
    * @throws IndexException exception on save documents to index occurs
    * @throws IndexTransactionException exception on save documents to index
    *           occurs
    */
   void recover() throws RepositoryException, IndexException, IndexTransactionException;

   /**
    * Check is storage has uncommited Transactions.
    * 
    * @return <code>true</code> if storage has Transaction log files.
    *         <code>false</code> in other case.
    */
   boolean hasUncommitedTransactions();

}
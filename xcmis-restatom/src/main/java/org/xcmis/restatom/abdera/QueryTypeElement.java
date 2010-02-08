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
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.xcmis.core.CmisQueryType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.CMIS;

import java.math.BigInteger;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class QueryTypeElement extends ExtensibleElementWrapper
{

   /**
    * Instantiates a new query type element.
    * 
    * @param internal the internal
    */
   public QueryTypeElement(Element internal)
   {
      super(internal);
   }

   /**
    * Instantiates a new query type element.
    * 
    * @param factory the factory
    * @param qname the qname
    */
   public QueryTypeElement(Factory factory, QName qname)
   {
      super(factory, qname);
   }

   // Never used in real life. Useful for tests.
   /**
    * Builds the element.
    * 
    * @param query the query
    */
   public void build(CmisQueryType query)
   {
      if (query != null)
      {
         setAttributeValue(AtomCMIS.STATEMENT, query.getStatement());
         if (query.isSearchAllVersions() != null)
            setAttributeValue(AtomCMIS.SEARCH_ALL_VERSIONS, query.isSearchAllVersions().toString());
         if (query.isIncludeAllowableActions() != null)
            setAttributeValue(AtomCMIS.INCLUDE_ALLOWABLE_ACTIONS, query.isIncludeAllowableActions().toString());
         if (query.getIncludeRelationships() != null)
            setAttributeValue(AtomCMIS.INCLUDE_RELATIONSHIPS, query.getIncludeRelationships().value());
         setAttributeValue(AtomCMIS.RENDITION_FILTER, query.getRenditionFilter());
         if (query.getMaxItems() != null)
            setAttributeValue(AtomCMIS.MAX_ITEMS, query.getMaxItems().toString());
         if (query.getSkipCount() != null)
            setAttributeValue(AtomCMIS.SKIP_COUNT, query.getSkipCount().toString());
      }
   }

   /**
    * Gets the include relationships.
    * 
    * @return the include relationships
    */
   public EnumIncludeRelationships getIncludeRelationships()
   {
      String tmp = getText(AtomCMIS.INCLUDE_RELATIONSHIPS);
      return tmp == null ? EnumIncludeRelationships.NONE : EnumIncludeRelationships.fromValue(tmp);
   }

   /**
    * Gets the page size.
    * 
    * @return the page size
    */
   public int getPageSize()
   {
      String tmp = getText(AtomCMIS.MAX_ITEMS);
      return tmp == null ? CMIS.MAX_ITEMS : Integer.parseInt(tmp);
   }

   /**
    * Gets the query.
    * 
    * @return the query
    */
   public CmisQueryType getQuery()
   {
      CmisQueryType query = new CmisQueryType();
      query.setStatement(getText(AtomCMIS.STATEMENT));
      query.setSearchAllVersions(isSearchAllVersions());
      query.setIncludeAllowableActions(isIncludeAllowableActions());
      query.setIncludeRelationships(getIncludeRelationships());
      query.setRenditionFilter(getRenditionFilter());
      query.setMaxItems(BigInteger.valueOf(getPageSize()));
      query.setSkipCount(BigInteger.valueOf(getSkipCount()));

      return query;
   }

   /**
    * Gets the rendition filter.
    * 
    * @return the rendition filter
    */
   public String getRenditionFilter()
   {
      return getText(AtomCMIS.RENDITION_FILTER);
   }

   /**
    * Gets the skip count.
    * 
    * @return the skip count
    */
   public int getSkipCount()
   {
      String tmp = getText(AtomCMIS.SKIP_COUNT);
      return tmp == null ? 0 : Integer.parseInt(tmp);
   }

   /**
    * Gets the statement.
    * 
    * @return the statement
    */
   public String getStatement()
   {
      return getText(AtomCMIS.STATEMENT);
   }

   /**
    * Checks if is include allowable actions.
    * 
    * @return true, if is include allowable actions
    */
   public boolean isIncludeAllowableActions()
   {
      return Boolean.parseBoolean(getText(AtomCMIS.INCLUDE_ALLOWABLE_ACTIONS));
   }

   /**
    * Checks if is search all versions.
    * 
    * @return true, if is search all versions
    */
   public boolean isSearchAllVersions()
   {
      return Boolean.parseBoolean(getText(AtomCMIS.SEARCH_ALL_VERSIONS));
   }

   /**
    * Gets the text.
    * 
    * @param elName the el name
    * 
    * @return the text
    */
   protected String getText(QName elName)
   {
      Element el = getExtension(elName);
      if (el != null)
         return el.getText();
      return null;
   }

}
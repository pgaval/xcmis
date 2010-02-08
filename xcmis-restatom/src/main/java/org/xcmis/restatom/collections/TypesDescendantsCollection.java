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

package org.xcmis.restatom.collections;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.RepositoryService;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;

import java.util.Calendar;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class TypesDescendantsCollection extends CmisTypeCollection
{

   /** The repository service. */
   protected final RepositoryService repositoryService;

   /**
    * Instantiates a new types descendants collection.
    * 
    * @param repositoryService the repository service
    */
   public TypesDescendantsCollection(RepositoryService repositoryService)
   {
      super(repositoryService);
      this.repositoryService = repositoryService;
      setHref("/typedescendants");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<CmisTypeDefinitionType> getEntries(RequestContext request) throws ResponseContextException
   {
      // To process hierarchically structure override addFeedDetails(Feed, RequestContext) method.
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      String typeId = request.getTarget().getParameter(AtomCMIS.PARAM_TYPE_ID);
      boolean includePropertyDefinitions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_PROPERTY_DEFINITIONS));
      int depth;
      try
      {
         depth =
            request.getParameter(AtomCMIS.PARAM_DEPTH) == null ? 1 : Integer.parseInt(request
               .getParameter(AtomCMIS.PARAM_DEPTH));
      }
      catch (NumberFormatException e)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_DEPTH);
         throw new ResponseContextException(msg, 400);
      }
      try
      {
         String repositoryId = getRepositoryId(request);
         List<CmisTypeContainer> descendants =
            repositoryService.getTypeDescendants(repositoryId, typeId, depth, includePropertyDefinitions);

         String down = getTypeChildrenLink(typeId, request);
         feed.addLink(down, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

         if (typeId != null)
         {
            String typeLink = getObjectTypeLink(typeId, request);
            feed.addLink(typeLink, AtomCMIS.LINK_VIA, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);

            CmisTypeDefinitionType type = repositoryService.getTypeDefinition(repositoryId, typeId);
            String parentType = type.getParentId();
            if (parentType != null)
            {
               String parent = getObjectTypeLink(parentType, request);
               feed.addLink(parent, AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);
            }
         }
         for (CmisTypeContainer typeContainer : descendants)
         {
            Entry e = feed.addEntry();
            IRI feedIri = new IRI(getFeedIriForEntry(typeContainer.getType(), request));
            addEntryDetails(request, e, feedIri, typeContainer.getType());
            if (typeContainer.getChildren().size() > 0)
               addChildren(e, typeContainer.getChildren(), feedIri, request);
         }
      }
      catch (RepositoryException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new ResponseContextException(createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 400));
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         throw new ResponseContextException(createErrorResponse(t, 500));
      }

   }

   /**
    * Adds the children.
    * 
    * @param entry the entry
    * @param children the children
    * @param feedIri the feed iri
    * @param request the request
    * 
    * @throws ResponseContextException the response context exception
    */
   protected void addChildren(Entry entry, List<CmisTypeContainer> children, IRI feedIri, RequestContext request)
      throws ResponseContextException
   {
      Element childrenElement = entry.addExtension(AtomCMIS.CHILDREN);
      // In this case entry is parent for feed, so use info from entry for new feed.
      String entryId = entry.getId().toString();
      Feed childFeed = request.getAbdera().getFactory().newFeed(childrenElement);
      childFeed.setId("ch:" + entryId); // TODO : entry use typeId and may not have two items with the same id.
      childFeed.setTitle("Type Children");
      childFeed.addAuthor(entry.getAuthor());
      childFeed.setUpdated(entry.getUpdated());

      // Copy some links from entry.
      List<Link> links =
         entry.getLinks(AtomCMIS.LINK_SERVICE, AtomCMIS.LINK_SELF, AtomCMIS.LINK_DOWN,
            AtomCMIS.LINK_CMIS_TYPEDESCENDANTS, AtomCMIS.LINK_UP);
      for (Link l : links)
         childFeed.addLink((Link)l.clone());

      childFeed.addLink(getObjectTypeLink(entryId, request), AtomCMIS.LINK_VIA, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null,
         null, -1);

      // add cmisra:numItems
      Element numItems = request.getAbdera().getFactory().newElement(AtomCMIS.NUM_ITEMS, childrenElement);
      numItems.setText(Integer.toString(children.size()));

      for (CmisTypeContainer typeContainer : children)
      {
         Entry ch = entry.getFactory().newEntry(childrenElement);
         addEntryDetails(request, ch, feedIri, typeContainer.getType());
         if (typeContainer.getChildren().size() > 0)
            addChildren(ch, typeContainer.getChildren(), feedIri, request);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Type Descendants";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Feed createFeedBase(RequestContext request) throws ResponseContextException
   {
      Factory factory = request.getAbdera().getFactory();
      Feed feed = factory.newFeed();
      feed.setId(getId(request));
      feed.setTitle(getTitle(request));
      feed.addAuthor(getAuthor(request));
      feed.setUpdated(AtomUtils.getAtomDate(Calendar.getInstance()));
      feed.addLink(getServiceLink(request), AtomCMIS.LINK_SERVICE, AtomCMIS.MEDIATYPE_ATOM_SERVICE, null, null, -1);
      return feed;
   }
}
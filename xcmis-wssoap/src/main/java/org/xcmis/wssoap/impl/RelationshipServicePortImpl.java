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

package org.xcmis.wssoap.impl;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.RelationshipService;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.RelationshipServicePort;
import org.xcmis.spi.CMIS;

import java.math.BigInteger;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id$
 */
@javax.jws.WebService(// name = "RelationshipServicePort",
serviceName = "RelationshipService", // 
portName = "RelationshipServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
// endpointInterface = "org.xcmis.soap.RelationshipServicePort"
)
public class RelationshipServicePortImpl implements RelationshipServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RelationshipServicePortImpl.class);

   /** Relationship service. */
   private RelationshipService relationshipService;

   /**
    * Constructs instance of <code>RelationshipServicePortImpl</code> .
    * 
    * @param relationshipService RelationshipService
    */
   public RelationshipServicePortImpl(RelationshipService relationshipService)
   {
      this.relationshipService = relationshipService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectListType getObjectRelationships(String repositoryId, //
      String objectId, //
      Boolean includeSubRelationshipTypes, //
      EnumRelationshipDirection relationshipDirection, //
      String typeId, //
      String propertyFilter, //
      Boolean includeAllowableActions, //
      BigInteger maxItems, //
      BigInteger skipCount, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getRelationships");

      CmisObjectListType ret = new CmisObjectListType();
      try
      {
         ret.getObjects().addAll(relationshipService.getObjectRelationships(repositoryId, //
            objectId, //
            relationshipDirection == null ? EnumRelationshipDirection.SOURCE : relationshipDirection, //
            typeId, //
            includeSubRelationshipTypes == null ? false : includeSubRelationshipTypes, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            propertyFilter, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue() //
         ).getObjects());
      }
      catch (Exception e)
      {
         LOG.error("Get relationships error : " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      return ret;
   }
}
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

package org.xcmis.restatom;

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumBasicPermissions;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.w3c.dom.NodeList;
import org.xcmis.spi.object.Entry;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey Zavizionov</a>
 * @version $Id: RepositoryServiceTest.java 2734 2009-08-19 15:42:18Z andrew00x
 *          $ Jul 20, 2009
 */

public class ACLTest extends BaseTest
{
   public void testGetACL() throws Exception
   {
      Entry doc = createDocument(testFolderId, "doc1", null, null);
      String docId = doc.getObjectId();
      CmisAccessControlListType addACL = new CmisAccessControlListType();
      CmisAccessControlEntryType entry = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
      principal.setPrincipalId("Makis");
      entry.setPrincipal(principal);
      entry.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());
      addACL.getPermission().add(entry);
      CmisAccessControlEntryType entry1 = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("root");
      entry1.setPrincipal(principal1);
      entry1.getPermission().add(EnumBasicPermissions.CMIS_READ.value());
      entry1.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());
      addACL.getPermission().add(entry1);

      // Apply via common service
      aclService.applyACL(cmisRepositoryId, docId, addACL, null, EnumACLPropagation.REPOSITORYDETERMINED);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/objacl/" + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //       printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      assertEquals(1, countElements("/cmis:acl", xmlDoc));
      assertEquals(2, countElements("/cmis:acl/cmis:permission", xmlDoc));

      NodeList acl = getNodeSet("/cmis:acl/cmis:permission", xmlDoc);
      int aclLength = acl.getLength();
      List<String> l = new ArrayList<String>();
      for (int i = 0; i < aclLength; i++)
      {
         org.w3c.dom.Node ace = acl.item(i);
         org.w3c.dom.Node p = getNode("cmis:principal", ace);
         String pId = getStringElement("cmis:principalId", p);
         l.add(pId);
         assertTrue("not found any permissions for " + pId, hasElementValue("cmis:permission", ace));
      }
      assertTrue(l.contains("root"));
      assertTrue(l.contains("Makis"));
   }

   public void testSetACL() throws Exception
   {
      Entry doc = createDocument(testFolderId, "doc1", null, null);
      String docId = doc.getObjectId();
      String s = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" //
         + "<cmis:acl xmlns:cmis='http://docs.oasis-open.org/ns/cmis/core/200908/'" //
         + " xmlns:cmism='http://docs.oasis-open.org/ns/cmis/messaging/200908/'" //
         + " xmlns:atom='http://www.w3.org/2005/Atom'" //
         + " xmlns:app='http://www.w3.org/2007/app'" //
         + " xmlns:cmisra='http://docs.oasis-open.org/ns/cmis/restatom/200908/'>" //
         + " <cmis:permission>" //
         + "<cmis:principal>" //
         + "<cmis:principalId>root</cmis:principalId>" //
         + "</cmis:principal>" //
         + "<cmis:permission>cmis:all</cmis:permission>" //
         + "<cmis:direct>true</cmis:direct>" //
         + "</cmis:permission>" //
         + "<cmis:permission>" //
         + "<cmis:principal>" //
         + "<cmis:principalId>Makis</cmis:principalId>" //
         + "</cmis:principal>" //
         + "<cmis:permission>cmis:read</cmis:permission>" // Makis only reads;
         + "<cmis:direct>true</cmis:direct>" //
         + "</cmis:permission>" //
         + "</cmis:acl>";

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/addacl/" + testFolderId;

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("PUT", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);
      //            printBody(writer.getBody());
      assertEquals(201, resp.getStatus());
      
      CmisAccessControlListType acl = aclService.getACL(cmisRepositoryId, docId, true);
      for(CmisAccessControlEntryType ace : acl.getPermission())
      {
         if ("Makis".equals(ace.getPrincipal().getPrincipalId()))
         {
            assertEquals(1, ace.getPermission().size());
            assertEquals("cmis:read", ace.getPermission().get(0));
         }
         else if ("root".equals(ace.getPrincipal().getPrincipalId()))
         {
            if (1 == ace.getPermission().size())
            {
               assertEquals("cmis:all", ace.getPermission().get(0));
            }
            else if (2 == ace.getPermission().size())
            {
               List<String> expected = new ArrayList<String>();
               expected.add("cmis:read");
               expected.add("cmis:write");
               assertEquals(true, ace.getPermission().containsAll(expected));
            }
            else
            {
               fail("Unexpected permissions set: " + ace.getPermission());
            }
         }
      }
   }

}
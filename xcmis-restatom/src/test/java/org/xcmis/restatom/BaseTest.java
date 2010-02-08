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

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.exoplatform.container.StandaloneContainer;
import org.xcmis.core.AccessControlService;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.PolicyService;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ContainerResponseWriter;
import org.exoplatform.services.rest.impl.ContainerRequest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.InputHeadersMap;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.tools.DummyContainerResponseWriter;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.abdera.CMISExtensionFactory;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.Repository;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class BaseTest extends TestCase
{

   protected final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected final String cmisRepositoryId = "cmis1";

   protected final String testFolderName = "testRoot";

   protected String rootFolderId;

   protected StandaloneContainer container;

   protected RequestHandlerImpl requestHandler;

   protected Factory factory;

   public RepositoryService cmisRepositoryService;

   public ObjectService objectService;

   public NavigationService navigationService;

   public RelationshipService relationshipService;

   public VersioningService versioningService;

   public AccessControlService aclService;

   public PolicyService policyService;

   protected String testFolderId;

   public Repository repository;

   protected XPath xp;

   public ContainerResponse service(String method, String requestURI, String baseURI,
      MultivaluedMap<String, String> headers, byte[] data) throws Exception
   {
      return service(method, requestURI, baseURI, headers, data, new DummyContainerResponseWriter());

   }

   public void setUp() throws Exception
   {
      String containerConf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      StandaloneContainer.addConfigurationURL(containerConf);
      container = StandaloneContainer.getInstance();
      requestHandler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);

      Abdera abdera = new Abdera();
      factory = abdera.getFactory();
      factory.registerExtension(new CMISExtensionFactory());

      cmisRepositoryService =
         (org.xcmis.core.RepositoryService)container
            .getComponentInstanceOfType(org.xcmis.core.RepositoryService.class);
      objectService =
         (org.xcmis.core.ObjectService)container
            .getComponentInstanceOfType(org.xcmis.core.ObjectService.class);
      navigationService =
         (org.xcmis.core.NavigationService)container
            .getComponentInstanceOfType(org.xcmis.core.NavigationService.class);
      relationshipService =
         (org.xcmis.core.RelationshipService)container
            .getComponentInstanceOfType(org.xcmis.core.RelationshipService.class);
      versioningService =
         (org.xcmis.core.VersioningService)container
            .getComponentInstanceOfType(org.xcmis.core.VersioningService.class);
      aclService =
         (org.xcmis.core.AccessControlService)container
            .getComponentInstanceOfType(org.xcmis.core.AccessControlService.class);
      policyService =
         (org.xcmis.core.PolicyService)container
            .getComponentInstanceOfType(org.xcmis.core.PolicyService.class);

      ConversationState state = new ConversationState(new Identity("root"));
      ConversationState.setCurrent(state);

      repository = cmisRepositoryService.getRepository(cmisRepositoryId);
      rootFolderId = repository.getRepositoryInfo().getRootFolderId();

      CmisPropertiesType props = new CmisPropertiesType();
      CmisPropertyId propId = new CmisPropertyId();
      propId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propId.getValue().add(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add(testFolderName);
      props.getProperty().add(propId);
      props.getProperty().add(propName);

      testFolderId = getObjectId(objectService.createFolder(cmisRepositoryId, rootFolderId, props, null, null, null));

      xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());
   }

   public void tearDown() throws Exception
   {
      super.tearDown();
      container = null;
      requestHandler = null;
      factory = null;
      for (Iterator<Entry> iter = repository.getCheckedOutDocuments(null); iter.hasNext();)
         iter.next().delete();

      for (Iterator<Entry> iter = repository.getRootFolder().getChildren(); iter.hasNext();)
         iter.next().delete();
   }

   protected void validateAllowableActions(org.w3c.dom.Node actions) throws XPathExpressionException
   {
      assertEquals(1, countElements("cmis:canDeleteObject", actions));
      assertEquals(1, countElements("cmis:canUpdateProperties", actions));
      assertEquals(1, countElements("cmis:canGetFolderTree", actions));
      assertEquals(1, countElements("cmis:canGetProperties", actions));
      assertEquals(1, countElements("cmis:canGetObjectRelationships", actions));
      assertEquals(1, countElements("cmis:canGetObjectParents", actions));
      assertEquals(1, countElements("cmis:canGetFolderParent", actions));
      assertEquals(1, countElements("cmis:canGetDescendants", actions));
      assertEquals(1, countElements("cmis:canMoveObject", actions));
      assertEquals(1, countElements("cmis:canDeleteContentStream", actions));
      assertEquals(1, countElements("cmis:canCheckOut", actions));
      assertEquals(1, countElements("cmis:canCancelCheckOut", actions));
      assertEquals(1, countElements("cmis:canCheckIn", actions));
      assertEquals(1, countElements("cmis:canSetContentStream", actions));
      assertEquals(1, countElements("cmis:canGetAllVersions", actions));
      assertEquals(1, countElements("cmis:canAddObjectToFolder", actions));
      assertEquals(1, countElements("cmis:canRemoveObjectFromFolder", actions));
      assertEquals(1, countElements("cmis:canGetContentStream", actions));
      assertEquals(1, countElements("cmis:canApplyPolicy", actions));
      assertEquals(1, countElements("cmis:canGetAppliedPolicies", actions));
      assertEquals(1, countElements("cmis:canRemovePolicy", actions));
      assertEquals(1, countElements("cmis:canGetChildren", actions));
      assertEquals(1, countElements("cmis:canCreateDocument", actions));
      assertEquals(1, countElements("cmis:canCreateFolder", actions));
      assertEquals(1, countElements("cmis:canCreateRelationship", actions));
      //      assertEquals(1, countElements("cmis:canCreatePolicy", xmlDoc));
      assertEquals(1, countElements("cmis:canDeleteTree", actions));
      assertEquals(1, countElements("cmis:canGetRenditions", actions));
      assertEquals(1, countElements("cmis:canGetACL", actions));
      assertEquals(1, countElements("cmis:canApplyACL", actions));
   }

   protected void checkTree(org.w3c.dom.Node node, Map<String, List<String>> expected) throws Exception
   {
      org.w3c.dom.Node childrenNode = getNode("cmisra:children", node);
      String id =
         getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId", "cmis:objectId", node);

      if (childrenNode == null)
      {
         if (expected.get(id) == null || expected.get(id).size() == 0)
            return;
         // If tag 'cmisra:children' not found but Map contains List<String> for current id.
         fail("Expected children " + expected.get(id) + " not found for object " + id);
      }
      List<String> expectedChildren = expected.get(id);
      org.w3c.dom.NodeList entries = getNodeSet("atom:entry", childrenNode);
      int length = entries.getLength();
      if (length < expectedChildren.size())
         fail("Expected children " + expectedChildren + " not found for object " + id);
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node child = entries.item(i);
         String childId =
            getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId", "cmis:objectId", child);
         if (expectedChildren == null || expectedChildren.size() == 0 || !expectedChildren.contains(childId))
            fail("Unexpected child " + childId + " found for object " + id);
         checkTree(child, expected);
      }
   }

   protected int countElements(String expression, org.w3c.dom.Node xmlDoc) throws XPathExpressionException
   {
      String count = (String)xp.evaluate("count(" + expression + ")", xmlDoc, XPathConstants.STRING);
      return Integer.parseInt(count);
   }

   protected Entry createDocument(String parent, String name, EnumVersioningState versioningState, ContentStream content)
      throws Exception
   {
      Entry doc =
         repository.getObjectById(parent).createChild(repository.getTypeDefinition("cmis:document"), name,
            versioningState);
      if (content != null)
         doc.setContent(content);
      doc.save();
      return doc;
   }

   protected Entry createFolder(String parent, String name) throws Exception
   {
      Entry folder =
         repository.getObjectById(parent).createChild(repository.getTypeDefinition("cmis:folder"), name, null);
      folder.save();
      return folder;
   }

   protected Entry createPolicy(String parent, String name, String policyText) throws Exception
   {
      Entry policy =
         repository.getObjectById(parent).createChild(repository.getTypeDefinition("cmis:policy"), name, null);
      policy.setString(CMIS.POLICY_TEXT, policyText);
      policy.save();
      return policy;
   }

   protected String getAttributeValue(String statement, String attributeName, org.w3c.dom.Document xmlDoc)
      throws XPathExpressionException
   {
      org.w3c.dom.Node node = (org.w3c.dom.Node)xp.evaluate(statement, xmlDoc, XPathConstants.NODE);
      String attr = node.getAttributes().getNamedItem(attributeName).getNodeValue();
      return attr;
   }

   protected org.w3c.dom.Node getNode(String expression, Node node) throws XPathExpressionException
   {
      return (org.w3c.dom.Node)xp.evaluate(expression, node, XPathConstants.NODE);
   }

   protected NodeList getNodeSet(String expression, org.w3c.dom.Node xmlDoc) throws XPathExpressionException
   {
      return (NodeList)xp.evaluate(expression, xmlDoc, XPathConstants.NODESET);
   }

   protected String getNodeValueWithNodeProperty(String statement, String propertyType, String property,
      org.w3c.dom.Node xmlDoc) throws XPathExpressionException
   {
      return getStringElement(statement + "/" + propertyType + "[@" + "propertyDefinitionId" + "='" + property
         + "']/cmis:value", xmlDoc);
   }

   protected String getObjectId(CmisObjectType object)
   {
      return ((CmisPropertyId)getProperty(object, CMIS.OBJECT_ID)).getValue().get(0);
   }

   protected CmisProperty getProperty(CmisObjectType object, String propertyName)
   {
      CmisPropertiesType properties = object.getProperties();
      if (properties != null)
      {
         for (CmisProperty prop : properties.getProperty())
         {
            if (prop.getDisplayName().equals(propertyName))
            {
               return prop;
            }
         }
      }
      return null;
   }

   protected String getStringElement(String expression, org.w3c.dom.Node xmlNode) throws XPathExpressionException
   {
      return (String)xp.evaluate(expression, xmlNode, XPathConstants.STRING);
   }

   protected boolean hasElementValue(String expression, org.w3c.dom.Node xmlElement) throws XPathExpressionException
   {
      String s = (String)xp.evaluate(expression, xmlElement, XPathConstants.STRING);
      return s != null && s.length() > 0;

   }

   protected boolean hasLink(String relValue, org.w3c.dom.Node xmlElement) throws XPathExpressionException
   {
      return hasNodeWithProperty("atom:link", "rel", relValue, xmlElement);
   }

   protected boolean hasNodeWithProperty(String statement, String propertyName, String propertyValue,
      org.w3c.dom.Node xmlElement) throws XPathExpressionException
   {
      org.w3c.dom.Node nodeProperty =
         (org.w3c.dom.Node)xp.evaluate(statement + "[@" + propertyName + "='" + propertyValue + "']", xmlElement,
            XPathConstants.NODE);
      return (nodeProperty != null && nodeProperty.getNodeName() != null);
   }

   protected void printBody(byte[] bytes)
   {
      System.out.println("+++\n" + new String(bytes) + "\n+++\n");
   }

   protected ContainerResponse service(String method, String requestURI, String baseURI,
      MultivaluedMap<String, String> headers, byte[] data, ContainerResponseWriter writer) throws Exception
   {

      if (headers == null)
         headers = new MultivaluedMapImpl();

      ByteArrayInputStream in = null;
      if (data != null)
         in = new ByteArrayInputStream(data);

      EnvironmentContext envctx = new EnvironmentContext();
      MockHttpServletRequest httpRequest =
         new MockHttpServletRequest(requestURI, in, in != null ? in.available() : 0, method, new InputHeadersMap(
            headers));
      envctx.put(HttpServletRequest.class, httpRequest);
      EnvironmentContext.setCurrent(envctx);
      ContainerRequest request =
         new ContainerRequest(method, new URI(requestURI), new URI(baseURI), in, new InputHeadersMap(headers));
      ContainerResponse response = new ContainerResponse(writer);

      try
      {
         requestHandler.handleRequest(request, response);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return response;
   }

   protected void validateEntryCommons(org.w3c.dom.Node xmlEntry) throws XPathExpressionException
   {
      String[] expected = new String[]{ //
         "atom:id", //
            "atom:published", //
            "atom:updated", //
            /*"atom:summary",*///
            "atom:author", //
            "atom:author/atom:name", //
            "atom:title" //
         };

      for (String el : expected)
         assertTrue("Not found xml element " + el, hasElementValue(el, xmlEntry));
   }

   protected void validateFeedCommons(org.w3c.dom.Node xmlFeed) throws XPathExpressionException
   {
      String[] expected = new String[]{ //
         "atom:id", //
            "atom:updated", //
            "atom:author", //
            "atom:title" //
         };
      for (String el : expected)
         assertTrue("Not found xml element " + el, hasElementValue(el, xmlFeed));
   }

   protected void validateObjectEntry(org.w3c.dom.Node xmlEntry, String objectType) throws XPathExpressionException
   {
      validateEntryCommons(xmlEntry);
      assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlEntry));
      assertTrue(hasLink(AtomCMIS.LINK_EDIT, xmlEntry));
      assertTrue(hasLink(AtomCMIS.LINK_SELF, xmlEntry));
      assertTrue(hasLink(AtomCMIS.LINK_DESCRIBEDBY, xmlEntry));
      assertTrue(hasLink(AtomCMIS.LINK_CMIS_ALLOWABLEACTIONS, xmlEntry));
      if (objectType.equalsIgnoreCase("cmis:folder"))
      {
         assertTrue(hasLink(AtomCMIS.LINK_DOWN, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_FOLDERTREE, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_RELATIONSHIPS, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_POLICIES, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_ACL, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_UP, xmlEntry));
      }
      else if (objectType.equalsIgnoreCase("cmis:document"))
      {
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_RELATIONSHIPS, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_POLICIES, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_ACL, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CURRENT_VERSION, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_VERSION_HISTORY, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_UP, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_EDIT_MEDIA, xmlEntry));
      }
      else if (objectType.equalsIgnoreCase("cmis:policy"))
      {
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_RELATIONSHIPS, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_POLICIES, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_ACL, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_UP, xmlEntry));
      }
      else if (objectType.equalsIgnoreCase("cmis:relationship"))
      {
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_SOURCE, xmlEntry));
         assertTrue(hasLink(AtomCMIS.LINK_CMIS_TARGET, xmlEntry));
      }

      // TODO : properties
   }

   protected void validateTypeEntry(org.w3c.dom.Node xmlEntry) throws XPathExpressionException
   {
      validateEntryCommons(xmlEntry);

      assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlEntry));
      assertTrue(hasLink(AtomCMIS.LINK_SELF, xmlEntry));
      assertTrue(hasLink(AtomCMIS.LINK_DOWN, xmlEntry));
      // TODO : check links for not root types
      
      org.w3c.dom.Node xmlType = getNode("cmisra:type", xmlEntry);
      assertTrue("Not found 'cmis:id' element", hasElementValue("cmis:id", xmlType));
      //    assertTrue("Not found 'cmis:displayName' element", hasElementValue("cmis:displayName", xmlDoc));
      assertTrue("Not found 'cmis:queryName' element", hasElementValue("cmis:queryName", xmlType));
      assertTrue("Not found 'cmis:baseId' element", hasElementValue("cmis:baseId", xmlType));
      assertTrue("Not found 'cmis:creatable' element", hasElementValue("cmis:creatable", xmlType));
      assertTrue("Not found 'cmis:fileable' element", hasElementValue("cmis:fileable", xmlType));
      assertTrue("Not found 'cmis:queryable' element", hasElementValue("cmis:queryable", xmlType));
      assertTrue("Not found 'cmis:fulltextIndexed' element", hasElementValue("cmis:fulltextIndexed", xmlType));
      assertTrue("Not found 'cmis:includedInSupertypeQuery' element", hasElementValue("cmis:includedInSupertypeQuery",
         xmlType));
      assertTrue("Not found 'cmis:controllable' element", hasElementValue("cmis:controllable", xmlType));
      assertTrue("Not found 'cmis:controllablePolicy' element", hasElementValue("cmis:controllablePolicy", xmlType));
      
      String baseId = getStringElement("cmis:baseId", xmlType);
      if (baseId.equals("cmis:document"))
      {
         assertTrue("Not found 'cmis:versionable' element", hasElementValue("cmis:versionable", xmlType));
         assertTrue("Not found 'cmis:contentStreamAllowed' element", hasElementValue("cmis:contentStreamAllowed", xmlType));
      }
      // TODO : property-definitions
   }

}
/*
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
package org.xcmis.search.query.plan;

import org.apache.commons.lang.Validate;
import org.xcmis.search.model.source.SelectorName;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * One step from query execution plan.
 */
public class QueryExecutionStep
{

   /**
    * An enumeration dictating the type of plan tree nodes.
    */
   public static enum Type {

      JOIN("Join"), // A node that defines the join type, join criteria, and join strategy

      SOURCE("Source"), //A node that defines the 'table' from which the tuples are being obtained

      PROJECT("Project"), //A node that defines the columns returned from the node.

      WHERE("Where"), //A node that selects a filters the tuples by applying a criteria evaluation filter node (WHERE )

      SORT("Sort"), //A node that defines the columns to sort on, the sort direction for each column, and whether to remove duplicates.

      LIMIT("Limit"); //A node that limits the number of tuples returned
      private static final Map<String, Type> TYPE_BY_SYMBOL;
      static
      {
         Map<String, Type> typesBySymbol = new HashMap<String, Type>();
         for (Type type : Type.values())
         {
            typesBySymbol.put(type.getSymbol().toUpperCase(), type);
         }
         TYPE_BY_SYMBOL = Collections.unmodifiableMap(typesBySymbol);
      }

      private final String symbol;

      private Type(String symbol)
      {
         this.symbol = symbol;
      }

      /**
       * Get the symbol representation of this node type.
       * 
       * @return the symbol; never null and never empty
       */
      public String getSymbol()
      {
         return symbol;
      }

      /**
       * {@inheritDoc}
       * 
       * @see java.lang.Enum#toString()
       */
      @Override
      public String toString()
      {
         return symbol;
      }

      /**
       * Attempt to find the Type given a symbol. The matching is done independent of case.
       * 
       * @param symbol the symbol
       * @return the Type having the supplied symbol, or null if there is no Type with the supplied symbol
       * @throws IllegalArgumentException if the symbol is null
       */
      public static Type forSymbol(String symbol)
      {
         Validate.notNull(symbol, "The symbol argument may not be null");
         return TYPE_BY_SYMBOL.get(symbol.toUpperCase().trim());
      }
   }

   /**
    * Step properties.
    */
   private final Map<String, Object> properties;

   /** The set of named selectors (e.g., tables) that this node deals with. */
   private Set<SelectorName> selectors;

   private final Type type;

   /**
    */
   public QueryExecutionStep(Type type)
   {
      this.type = type;
      this.properties = new HashMap<String, Object>();
      this.selectors = new HashSet<SelectorName>();
   }

   /**
    * @return the type of the step
    */
   public Type getType()
   {
      return type;
   }

   /**
    * Set value of the property with name <b>propertyName</b> 
    * to <b>value</b> 
    * 
    * @param propertyName - property name.
    * @param value - property value.
    */
   public void setProperty(String propertyName, Object value)
   {
      this.properties.put(propertyName, value);
   }

   /**
    * Return property value
    * 
    * @param propertyName
    * @return
    */
   public Object getPropertyValue(String propertyName)
   {
      return this.properties.get(propertyName);
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return type + "[" + properties + "]";
   }

   /**
    * Add a selector to this plan node. This method does nothing if the supplied selector is null.
    * 
    * @param symbol the symbol of the selector
    */
   public void addSelector(SelectorName symbol)
   {
      if (symbol != null)
      {
         selectors.add(symbol);
      }
   }

   /**
    * Add the selectors to this execution step. This method does nothing for any supplied selector that is null.
    * 
    * @param first the first symbol to be added
    * @param second the second symbol to be added
    */
   public void addSelector(SelectorName first, SelectorName second)
   {
      if (first != null)
      {
         selectors.add(first);
      }
      if (second != null)
      {
         selectors.add(second);
      }
   }

   /**
    * Add the selectors to this execution step. This method does nothing for any supplied selector that is null.
    * 
    * @param names the symbols to be added
    */
   public void addSelectors(Iterable<SelectorName> names)
   {
      for (SelectorName name : names)
      {
         if (name != null)
         {
            selectors.add(name);
         }
      }
   }

   /**
    * Get the selectors that are referenced by this execution step.
    * 
    * @return the names of the selectors; never null but possibly empty
    */
   public Set<SelectorName> getSelectors()
   {
      return selectors;
   }
}

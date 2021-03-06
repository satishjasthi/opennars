/* 
 * NOTICE OF LICENSE
 * 
 * This source file is subject to the Open Software License (OSL 3.0) that is 
 * bundled with this package in the file LICENSE.txt. It is also available 
 * through the world-wide-web at http://opensource.org/licenses/osl-3.0.php
 * If you did not receive a copy of the license and are unable to obtain it 
 * through the world-wide-web, please send an email to magnos.software@gmail.com 
 * so we can send you a copy immediately. If you use any of this software please
 * notify me via our website or email, your feedback is much appreciated. 
 * 
 * @copyright   Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * @license     http://opensource.org/licenses/osl-3.0.php
 *              Open Software License (OSL 3.0)
 */

package org.magnos.trie;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 * A {@link Set} where the underlying data structure is a Trie&lt;E,
 * Object&gt;. a TrieSet can be encoded into a minimized byte array and can be
 * decoded from an encoded byte array.
 * 
 * @author Philip Diffenderfer
 * 
 * @param <E>
 *        The element type.
 */
public final class TrieSet<E> implements Set<E> {

   /**
    * The flag used in the underlying Trie of a TrieSet to indicate the given
    * value exists in the TrieSet.
    */
   public static final Object FLAG = new Object();

   /**
    * The flag used in the underlying Trie of a TrieSet to indicate the given
    * value does not exist in the TrieSet.
    */
   public static final Object FLAG_NONE = null;

   protected final Trie<E, Object> trie;

   /**
    * Instantiates a TrieSet given a trie. 
    * <h3>Example Usage</h3>
    * <pre>
    * TrieSet&lt;String&gt; set = new TrieSet&lt;String&gt;( Tries.forStrings() );
    * </pre>
    * 
    * @param trie
    *        The trie to use as the base.
    */
   public TrieSet( Trie<E, Object> trie )
   {
      this.trie = trie;
      this.trie.setDefaultValue( FLAG_NONE );
      this.trie.setDefaultMatch( TrieMatch.EXACT );
   }

   /**
    * Returns the reference to the underlying Trie. The reference to the
    * underlying Trie may change if the {@link #retainAll(Collection)} method is
    * called after the reference is gotten.
    * 
    * @return The reference to the underlying Trie.
    */
   public Trie<E, Object> trie()
   {
      return trie;
   }

   @Override
   public boolean add( E value )
   {
      return trie.put( value, FLAG ) == FLAG_NONE;
   }

   @Override
   public boolean addAll( Collection<? extends E> collection )
   {
      boolean changed = false;

      for (E element : collection)
      {
         changed |= add( element );
      }

      return changed;
   }

   @Override
   public boolean retainAll(Collection<?> collection) {
      throw new RuntimeException("use retainsAll");
      //return false;
   }

   @Override
   public void clear()
   {
      trie.clear();
   }

   @Override
   public boolean contains( Object value )
   {
      return trie.containsKey( value );
   }

   @Override
   public boolean containsAll( Collection<?> collection )
   {
      for (Object element : collection)
      {
         if (!trie.containsKey( element ))
         {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isEmpty()
   {
      return trie.isEmpty();
   }

   @Override
   public Iterator<E> iterator()
   {
      return trie.keySet().iterator();
   }

   @Override
   public boolean remove( Object value )
   {
      return trie.remove( value ) == FLAG;
   }

   @Override
   public boolean removeAll( Collection<?> collection )
   {
      boolean changed = false;

      for (Object element : collection)
      {
         changed |= remove( element );
      }

      return changed;
   }

   public TrieSet<E> retainsAll( Collection<?> collection ) {
      final int previousSize = trie.size();
      final Trie<E, Object> newTrie = trie.newEmptyClone();

      for (Object element : collection)
      {
         if (trie.containsKey( element ))
         {
            newTrie.put( (E)element, FLAG );
         }
      }
      if (previousSize!=newTrie.size())
         return new TrieSet(newTrie); //trie = newTrie;

      return this;
      //return previousSize != trie.size();
   }

//   @SuppressWarnings ("unchecked" )
//   @Override
//   public boolean retainAll( Collection<?> collection )
//   {
//      final int previousSize = trie.size();
//      final Trie<E, Object> newTrie = trie.newEmptyClone();
//
//      for (Object element : collection)
//      {
//         if (trie.containsKey( element ))
//         {
//            newTrie.put( (E)element, FLAG );
//         }
//      }
//
//      trie = newTrie;
//
//      return previousSize != trie.size();
//   }

   @Override
   public int size()
   {
      return trie.size();
   }

   @Override
   public Object[] toArray()
   {
      return trie.keySet().toArray();
   }

   @Override
   public <T> T[] toArray( T[] arr )
   {
      return trie.keySet().toArray( arr );
   }


}

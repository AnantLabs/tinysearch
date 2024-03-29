// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2006 Derrick Oswald
//
// Revision Control Information
//
// $Source: /cvsroot/htmlparser/htmlparser/src/org/htmlparser/tags/TableHeader.java,v $
// $Author: derrickoswald $
// $Date: 2006/04/23 11:59:44 $
// $Revision: 1.4 $
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//

package org.htmlparser.tags;

/**
 * 
 * A table header tag.
 * 
 */
public class TableHeader extends CompositeTag {
	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[] { "TH" };

	/**
	 * The set of tag names that indicate the end of this tag.
	 */
	private static final String[] mEnders = new String[] { "TH", "TR", "TBODY",
			"TFOOT", "THEAD" };

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[] { "TR", "TBODY",
			"TFOOT", "THEAD", "TABLE" };

	/**
	 * Create a new table header tag.
	 */
	public TableHeader() {
	}

	/**
	 * Return the set of names handled by this tag.
	 * 
	 * @return The names to be matched that create tags of this type.
	 */
	public String[] getIds() {
		return (mIds);
	}

	/**
	 * Return the set of tag names that cause this tag to finish.
	 * 
	 * @return The names of following tags that stop further scanning.
	 */
	public String[] getEnders() {
		return (mEnders);
	}

	/**
	 * Return the set of end tag names that cause this tag to finish.
	 * 
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders() {
		return (mEndTagEnders);
	}
}

// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2005 Derrick Oswald
//
// Revision Control Information
//
// $Source: /cvsroot/htmlparser/htmlparser/src/org/htmlparser/parserapplications/filterbuilder/wrappers/HasSiblingFilterWrapper.java,v $
// $Author: derrickoswald $
// $Date: 2005/04/12 11:27:42 $
// $Revision: 1.2 $
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

package org.htmlparser.parserapplications.filterbuilder.wrappers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasSiblingFilter;
import org.htmlparser.parserapplications.filterbuilder.Filter;
import org.htmlparser.parserapplications.filterbuilder.SubFilterList;

/**
 * Wrapper for HasSiblingFilters.
 */
public class HasSiblingFilterWrapper extends Filter implements ActionListener {
	/**
	 * The underlying filter.
	 */
	protected HasSiblingFilter mFilter;

	/**
	 * The drop target container.
	 */
	protected SubFilterList mContainer;

	/**
	 * Create a wrapper over a new HasParentFilter.
	 */
	public HasSiblingFilterWrapper() {
		mFilter = new HasSiblingFilter();

		// add the subfilter container
		mContainer = new SubFilterList(this, "Sibling Filter", 1);
		add(mContainer);
	}

	//
	// Filter overrides and concrete implementations
	//

	/**
	 * Get the name of the filter.
	 * 
	 * @return A descriptive name for the filter.
	 */
	public String getDescription() {
		return ("Has Sibling");
	}

	/**
	 * Get the resource name for the icon.
	 * 
	 * @return The icon resource specification.
	 */
	public String getIconSpec() {
		return ("images/HasSiblingFilter.gif");
	}

	/**
	 * Get the underlying node filter object.
	 * 
	 * @return The node filter object suitable for serialization.
	 */
	public NodeFilter getNodeFilter() {
		NodeFilter filter;
		HasSiblingFilter ret;

		ret = new HasSiblingFilter();

		filter = mFilter.getSiblingFilter();
		if (null != filter)
			ret.setSiblingFilter(((Filter) filter).getNodeFilter());

		return (ret);
	}

	/**
	 * Assign the underlying node filter for this wrapper.
	 * 
	 * @param filter
	 *            The filter to wrap.
	 * @param context
	 *            The parser to use for conditioning this filter. Some filters
	 *            need contextual information to provide to the user, i.e. for
	 *            tag names or attribute names or values, so the Parser context
	 *            is provided.
	 */
	public void setNodeFilter(NodeFilter filter, Parser context) {
		mFilter = (HasSiblingFilter) filter;
	}

	/**
	 * Get the underlying node filter's subordinate filters.
	 * 
	 * @return The node filter object's contained filters.
	 */
	public NodeFilter[] getSubNodeFilters() {
		NodeFilter filter;
		NodeFilter[] ret;

		filter = mFilter.getSiblingFilter();
		if (null != filter)
			ret = new NodeFilter[] { filter };
		else
			ret = new NodeFilter[0];

		return (ret);
	}

	/**
	 * Assign the underlying node filter's subordinate filters.
	 * 
	 * @param filters
	 *            The filters to insert into the underlying node filter.
	 */
	public void setSubNodeFilters(NodeFilter[] filters) {
		if (0 != filters.length)
			mFilter.setSiblingFilter(filters[0]);
		else
			mFilter.setSiblingFilter(null);
	}

	/**
	 * Convert this filter into Java code. Output whatever text necessary and
	 * return the variable name.
	 * 
	 * @param out
	 *            The output buffer.
	 * @param context
	 *            Three integers as follows:
	 *            <li>indent level - the number of spaces to insert at the
	 *            beginning of each line</li>
	 *            <li>filter number - the next available filter number</li>
	 *            <li>filter array number - the next available array of filters
	 *            number</li>
	 * @return The variable name to use when referencing this filter (usually
	 *         "filter" + context[1]++)
	 */
	public String toJavaCode(StringBuffer out, int[] context) {
		String name;
		String ret;

		if (null != mFilter.getSiblingFilter())
			name = ((Filter) mFilter.getSiblingFilter()).toJavaCode(out,
					context);
		else
			name = null;
		ret = "filter" + context[1]++;
		spaces(out, context[0]);
		out.append("HasSiblingFilter ");
		out.append(ret);
		out.append(" = new HasSiblingFilter ();");
		newline(out);
		if (null != name) {
			spaces(out, context[0]);
			out.append(ret);
			out.append(".setSiblingFilter (");
			out.append(name);
			out.append(");");
			newline(out);
		}

		return (ret);
	}

	//
	// NodeFilter interface
	//

	/**
	 * Predicate to determine whether or not to keep the given node. The
	 * behaviour based on this outcome is determined by the context in which it
	 * is called. It may lead to the node being added to a list or printed out.
	 * See the calling routine for details.
	 * 
	 * @return <code>true</code> if the node is to be kept, <code>false</code>
	 *         if it is to be discarded.
	 * @param node
	 *            The node to test.
	 */
	public boolean accept(Node node) {
		return (mFilter.accept(node));
	}

	//
	// ActionListener interface
	//

	/**
	 * Invoked when an action occurs.
	 * 
	 * @param event
	 *            Details about the action event.
	 */
	public void actionPerformed(ActionEvent event) {
	}
}

package org.apache.lucene.search;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;

/**
 * The Scorer for DisjunctionMaxQuery's. The union of all documents generated by
 * the the subquery scorers is generated in document number order. The score for
 * each document is the maximum of the scores computed by the subquery scorers
 * that generate that document, plus tieBreakerMultiplier times the sum of the
 * scores for the other subqueries that generate the document.
 * 
 * @author Chuck Williams
 */
class DisjunctionMaxScorer extends Scorer {

	/*
	 * The scorers for subqueries that have remaining docs, kept as a min heap
	 * by number of next doc.
	 */
	private ArrayList subScorers = new ArrayList();

	/*
	 * Multiplier applied to non-maximum-scoring subqueries for a document as
	 * they are summed into the result.
	 */
	private float tieBreakerMultiplier;

	private boolean more = false; // True iff there is a next document
	private boolean firstTime = true; // True iff next() has not yet been
										// called

	/**
	 * Creates a new instance of DisjunctionMaxScorer
	 * 
	 * @param tieBreakerMultiplier
	 *            Multiplier applied to non-maximum-scoring subqueries for a
	 *            document as they are summed into the result.
	 * @param similarity --
	 *            not used since our definition involves neither coord nor terms
	 *            directly
	 */
	public DisjunctionMaxScorer(float tieBreakerMultiplier,
			Similarity similarity) {
		super(similarity);
		this.tieBreakerMultiplier = tieBreakerMultiplier;
	}

	/**
	 * Add the scorer for a subquery
	 * 
	 * @param scorer
	 *            the scorer of a subquery of our associated DisjunctionMaxQuery
	 */
	public void add(Scorer scorer) throws IOException {
		if (scorer.next()) { // Initialize and retain only if it produces
								// docs
			subScorers.add(scorer);
			more = true;
		}
	}

	/**
	 * Generate the next document matching our associated DisjunctionMaxQuery.
	 * 
	 * @return true iff there is a next document
	 */
	public boolean next() throws IOException {
		if (!more)
			return false;
		if (firstTime) {
			heapify();
			firstTime = false;
			return true; // more would have been false if no subScorers had
							// any docs
		}
		// Increment all generators that generated the last doc and adjust the
		// heap.
		int lastdoc = ((Scorer) subScorers.get(0)).doc();
		do {
			if (((Scorer) subScorers.get(0)).next())
				heapAdjust(0);
			else {
				heapRemoveRoot();
				if (subScorers.isEmpty())
					return (more = false);
			}
		} while (((Scorer) subScorers.get(0)).doc() == lastdoc);
		return true;
	}

	/**
	 * Determine the current document number. Initially invalid, until
	 * {@link #next()} is called the first time.
	 * 
	 * @return the document number of the currently generated document
	 */
	public int doc() {
		return ((Scorer) subScorers.get(0)).doc();
	}

	/**
	 * Determine the current document score. Initially invalid, until
	 * {@link #next()} is called the first time.
	 * 
	 * @return the score of the current generated document
	 */
	public float score() throws IOException {
		int doc = ((Scorer) subScorers.get(0)).doc();
		float[] sum = { ((Scorer) subScorers.get(0)).score() }, max = { sum[0] };
		int size = subScorers.size();
		scoreAll(1, size, doc, sum, max);
		scoreAll(2, size, doc, sum, max);
		return max[0] + (sum[0] - max[0]) * tieBreakerMultiplier;
	}

	// Recursively iterate all subScorers that generated last doc computing sum
	// and max
	private void scoreAll(int root, int size, int doc, float[] sum, float[] max)
			throws IOException {
		if (root < size && ((Scorer) subScorers.get(root)).doc() == doc) {
			float sub = ((Scorer) subScorers.get(root)).score();
			sum[0] += sub;
			max[0] = Math.max(max[0], sub);
			scoreAll((root << 1) + 1, size, doc, sum, max);
			scoreAll((root << 1) + 2, size, doc, sum, max);
		}
	}

	/**
	 * Advance to the first document beyond the current whose number is greater
	 * than or equal to target.
	 * 
	 * @param target
	 *            the minimum number of the next desired document
	 * @return true iff there is a document to be generated whose number is at
	 *         least target
	 */
	public boolean skipTo(int target) throws IOException {
		if (firstTime) {
			if (!more)
				return false;
			heapify();
			firstTime = false;
		}

		while (subScorers.size() > 0
				&& ((Scorer) subScorers.get(0)).doc() < target) {
			if (((Scorer) subScorers.get(0)).skipTo(target))
				heapAdjust(0);
			else
				heapRemoveRoot();
		}
		if ((subScorers.size() == 0))
			return (more = false);
		return true;
	}

	/**
	 * Explain a score that we computed. UNSUPPORTED -- see explanation
	 * capability in DisjunctionMaxQuery.
	 * 
	 * @param doc
	 *            the number of a document we scored
	 * @return the Explanation for our score
	 */
	public Explanation explain(int doc) throws IOException {
		throw new UnsupportedOperationException();
	}

	// Organize subScorers into a min heap with scorers generating the earlest
	// document on top.
	private void heapify() {
		int size = subScorers.size();
		for (int i = (size >> 1) - 1; i >= 0; i--)
			heapAdjust(i);
	}

	/*
	 * The subtree of subScorers at root is a min heap except possibly for its
	 * root element. Bubble the root down as required to make the subtree a
	 * heap.
	 */
	private void heapAdjust(int root) {
		Scorer scorer = (Scorer) subScorers.get(root);
		int doc = scorer.doc();
		int i = root, size = subScorers.size();
		while (i <= (size >> 1) - 1) {
			int lchild = (i << 1) + 1;
			Scorer lscorer = (Scorer) subScorers.get(lchild);
			int ldoc = lscorer.doc();
			int rdoc = Integer.MAX_VALUE, rchild = (i << 1) + 2;
			Scorer rscorer = null;
			if (rchild < size) {
				rscorer = (Scorer) subScorers.get(rchild);
				rdoc = rscorer.doc();
			}
			if (ldoc < doc) {
				if (rdoc < ldoc) {
					subScorers.set(i, rscorer);
					subScorers.set(rchild, scorer);
					i = rchild;
				} else {
					subScorers.set(i, lscorer);
					subScorers.set(lchild, scorer);
					i = lchild;
				}
			} else if (rdoc < doc) {
				subScorers.set(i, rscorer);
				subScorers.set(rchild, scorer);
				i = rchild;
			} else
				return;
		}
	}

	// Remove the root Scorer from subScorers and re-establish it as a heap
	private void heapRemoveRoot() {
		int size = subScorers.size();
		if (size == 1)
			subScorers.remove(0);
		else {
			subScorers.set(0, subScorers.get(size - 1));
			subScorers.remove(size - 1);
			heapAdjust(0);
		}
	}

}

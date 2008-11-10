// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:42:42
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DictSegment.java

package org.mira.lucene.analysis.dict;

import java.util.HashMap;

// Referenced classes of package org.mira.lucene.analysis.dict:
//            WordType, Hit

public class DictSegment {

	public DictSegment() {
		nodeState = 0;
		wordType = new WordType();
	}

	public void addWord(char seg[], WordType wordType) {
		addWord(seg, 0, seg.length - 1, wordType);
	}

	public void addWord(char seg[], int begin, int end, WordType wordType) {
		if (dictTreeNode == null)
			dictTreeNode = new HashMap(2, 0.8F);
		Character keyChar = new Character(seg[begin]);
		DictSegment ds = (DictSegment) dictTreeNode.get(keyChar);
		if (ds == null) {
			ds = new DictSegment();
			dictTreeNode.put(keyChar, ds);
		}
		if (begin < end)
			ds.addWord(seg, begin + 1, end, wordType);
		else if (begin == end) {
			ds.setNodeState(1);
			ds.wordType.addWordType(wordType);
		}
	}

	public Hit search(char seg[], int begin, int end) {
		Hit searchHit = new Hit();
		return search(seg, begin, end, searchHit);
	}

	private Hit search(char seg[], int begin, int end, Hit searchHit) {
		if (dictTreeNode == null) {
			searchHit.setUnmatch();
			return searchHit;
		}
		Character keyChar = new Character(seg[begin]);
		DictSegment ds = (DictSegment) dictTreeNode.get(keyChar);
		if (ds != null) {
			if (begin < end)
				return ds.search(seg, begin + 1, end, searchHit);
			if (begin == end) {
				if (ds.getNodeState() == 1) {
					searchHit.setMatch();
					searchHit.setWordType(ds.getWordType());
				}
				if (ds.hasNextNode())
					searchHit.setPrefixMatch();
			}
		} else {
			searchHit.setUnmatch();
		}
		return searchHit;
	}

	public boolean hasNextNode() {
		return dictTreeNode != null && !dictTreeNode.isEmpty();
	}

	public int getNodeState() {
		return nodeState;
	}

	public void setNodeState(int nodeState) {
		this.nodeState = nodeState;
	}

	public WordType getWordType() {
		return wordType;
	}

	private HashMap dictTreeNode;
	private int nodeState;
	private WordType wordType;
}
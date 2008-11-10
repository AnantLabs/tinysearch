// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:41:37
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IKTokenizer.java

package org.mira.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.TreeSet;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.mira.lucene.analysis.dict.Dictionary;
import org.mira.lucene.analysis.dict.Hit;

// Referenced classes of package org.mira.lucene.analysis:
//            TokenDelegate, MTokenDelegate

public final class IKTokenizer extends Tokenizer {

	IKTokenizer(Reader in, boolean mircoSupported) {
		super(in);
		dict = Dictionary.load();
		// this.mircoSupported = true;
		lastHitState = 0;
		segmentBuff = new char[2048];
		this.mircoSupported = mircoSupported;
		tokens = new TreeSet();
		numberSet = new TreeSet();
		initContext();
	}

	private void initContext() {
		inputStatus = 0;
		contextStatus = 0;
		beginIndex = 0;
		lastMatchEnd = -1;
		numberBeginIndex = -1;
		numberStatus = 0;
		unmatchFlag = false;
		unmatchBegin = -1;
		breakAndRead = false;
		bAr_offset = 0;
	}

	public final Token next() throws IOException {
		if (!tokens.isEmpty()) {
			TokenDelegate td = (TokenDelegate) tokens.first();
			tokens.remove(td);
			return td.getToken();
		}
		int segLength = 0;
		if (breakAndRead) {
			segLength = input.read(segmentBuff, 2048 - bAr_offset, bAr_offset);
			segLength += 2048 - bAr_offset;
		} else {
			segLength = input.read(segmentBuff);
		}
		if (segLength <= 0)
			return null;
		getTokens(segLength);
		offset += bAr_offset;
		if (!tokens.isEmpty()) {
			TokenDelegate td = (TokenDelegate) tokens.first();
			tokens.remove(td);
			return td.getToken();
		} else {
			return null;
		}
	}

	private void getTokens(int segLength) {
		initContext();
		char current = '\0';
		for (int ci = 0; ci < segLength; ci++) {
			nextContextStatus = 0;
			nextNumberStatus = 0;
			segmentBuff[ci] = toDBC(segmentBuff[ci]);
			current = segmentBuff[ci];
			inputStatus = dict.identify(current);
			if (contextStatus == 0) {
				procInitState(ci);
			} else {
				if ((contextStatus & 1) > 0)
					ci = procLetterState(ci);
				if ((contextStatus & 0x10) > 0)
					ci = procNumberState(ci);
				if ((contextStatus & 0x100) > 0)
					ci = procCJKState(ci);
			}
			contextStatus = nextContextStatus;
			numberStatus = nextNumberStatus;
			if (nextContextStatus != 0 || segLength - ci <= 1
					|| segLength - ci >= 50 || segLength != 2048)
				continue;
			bAr_offset = ci + 1;
			breakAndRead = true;
			break;
		}

		if (!breakAndRead) {
			if (numberBeginIndex >= 0)
				pushNumber(numberBeginIndex, segLength - 1);
			if (lastMatchEnd != segLength - 1)
				if (unmatchFlag)
					procSplitSeg(unmatchBegin, segLength - 1);
				else if (beginIndex < segLength)
					procSplitSeg(beginIndex, segLength - 1);
		}
		for (Iterator it = numberSet.iterator(); it.hasNext(); pushTerm((TokenDelegate) it
				.next()))
			;
		numberSet.clear();
		if (!mircoSupported) {
			TreeSet tmpTokens = new TreeSet();
			for (Iterator it = tokens.iterator(); it.hasNext();) {
				TokenDelegate td = (TokenDelegate) it.next();
				MTokenDelegate mtd = new MTokenDelegate(td.getOffset(), td
						.getBegin(), td.getEnd());
				String w = (new String(segmentBuff, td.getBegin(),
						(td.getEnd() - td.getBegin()) + 1)).toLowerCase();
				if (!dict.isUselessWord(w)) {
					mtd.setTerm(w);
					tmpTokens.add(mtd);
				}
			}

			tokens.clear();
			tokens = tmpTokens;
		}
		if (breakAndRead)
			System.arraycopy(segmentBuff, bAr_offset, segmentBuff, 0, segLength
					- bAr_offset);
	}

	private void procInitState(int ci) {
		if ((inputStatus & 0x10000) > 0) {
			nextContextStatus |= 1;
			beginIndex = ci;
		}
		if ((inputStatus & 0x100000) > 0) {
			nextContextStatus |= 0x10;
			if ((inputStatus & 1) > 0)
				nextNumberStatus |= 1;
			if ((inputStatus & 2) > 0)
				nextNumberStatus |= 2;
			numberBeginIndex = ci;
		}
		if ((inputStatus & 0x1000000) > 0) {
			nextContextStatus |= 0x100;
			beginIndex = ci;
			procCJK(beginIndex, ci);
		}
		if ((inputStatus & 0x10000000) > 0)
			beginIndex = ci + 1;
	}

	private int procLetterState(int ci) {
		if ((inputStatus & 0x10000) > 0) {
			nextContextStatus |= 1;
			return ci;
		}
		if ((inputStatus & 0x100000) > 0) {
			pushTerm(beginIndex, ci - 1, true);
			setLastMatchEnd(ci - 1);
			nextContextStatus |= 0x10;
			if ((inputStatus & 1) > 0)
				nextNumberStatus = 1;
			if ((inputStatus & 2) > 0)
				nextNumberStatus = 2;
			numberBeginIndex = ci;
		}
		if ((inputStatus & 0x1000000) > 0) {
			pushTerm(beginIndex, ci - 1, true);
			setLastMatchEnd(ci - 1);
			nextContextStatus |= 0x100;
			beginIndex = ci;
			procCJK(beginIndex, ci);
		}
		if ((inputStatus & 0x10000000) > 0) {
			pushTerm(beginIndex, ci - 1, true);
			setLastMatchEnd(ci - 1);
			nextContextStatus = 0;
			beginIndex = ci + 1;
		}
		return ci;
	}

	private int procNumberState(int ci) {
		if (numberBeginIndex > 0 && ci <= numberBeginIndex)
			return ci;
		if ((inputStatus & 0x10000) > 0) {
			nextContextStatus |= 1;
			if ((inputStatus & 0x100000) > 0 && (contextStatus & 1) > 0) {
				nextContextStatus |= 0x10;
				nextNumberStatus = 2;
			} else if ((contextStatus & 1) > 0) {
				nextContextStatus = 1;
				nextNumberStatus = 0;
				numberBeginIndex = -1;
			} else {
				pushTerm(numberBeginIndex, ci - 1);
				if (unmatchFlag) {
					pushTerm(unmatchBegin, numberBeginIndex - 1);
					setLastMatchEnd(ci - 1);
					unmatchFlag = false;
				} else if (beginIndex < numberBeginIndex) {
					pushTerm(beginIndex, numberBeginIndex - 1);
					setLastMatchEnd(ci - 1);
				}
				beginIndex = ci;
				numberBeginIndex = -1;
			}
			return ci;
		}
		if ((inputStatus & 0x100000) > 0) {
			nextContextStatus |= 0x10;
			if ((inputStatus & 1) > 0) {
				nextNumberStatus = 1;
				if (numberStatus == 0) {
					pushTerm(numberBeginIndex, ci - 1, true);
					setLastMatchEnd(ci - 1);
					beginIndex = ci;
				}
			} else if ((inputStatus & 2) > 0) {
				nextNumberStatus = 2;
				if (numberStatus == 0) {
					pushTerm(numberBeginIndex, ci - 1, true);
					setLastMatchEnd(ci - 1);
					beginIndex = ci;
				}
			} else if (inputStatus == 0x100000)
				nextNumberStatus = 0;
			if ((inputStatus & 0x1000000) > 0)
				nextContextStatus |= 0x100;
			return ci;
		}
		if ((inputStatus & 0x1000000) > 0) {
			nextContextStatus |= 0x100;
			setLastMatchEnd(ci - 1);
			pushNumber(numberBeginIndex, ci - 1);
			if ((contextStatus & 0x100) == 0)
				beginIndex = ci;
			procCJK(ci, ci);
			numberBeginIndex = -1;
			return ci;
		}
		if ((inputStatus & 0x10000000) > 0)
			if ((inputStatus & 8) > 0 && numberStatus == 0) {
				nextContextStatus |= 0x10;
				numberStatus = 8;
			} else {
				nextContextStatus = 0;
				pushTerm(numberBeginIndex, ci - 1, true);
				if ((contextStatus & 0x100) > 0)
					if (unmatchFlag) {
						pushTerm(unmatchBegin, numberBeginIndex - 1);
						setLastMatchEnd(ci - 1);
						unmatchFlag = false;
					} else {
						pushTerm(beginIndex, numberBeginIndex - 1);
						setLastMatchEnd(ci - 1);
					}
				setLastMatchEnd(ci - 1);
				numberBeginIndex = -1;
				beginIndex = ci + 1;
			}
		return ci;
	}

	private int procCJKState(int ci) {
		if ((inputStatus & 0x10000) > 0) {
			nextContextStatus |= 1;
			if ((inputStatus & 0x100000) > 0) {
				nextContextStatus |= 0x10;
				nextNumberStatus = 2;
				if (numberBeginIndex < 0)
					numberBeginIndex = ci;
			}
			if (lastMatchEnd != ci - 1)
				if (unmatchFlag) {
					procSplitSeg(unmatchBegin, ci - 1);
					setLastMatchEnd(ci - 1);
					unmatchFlag = false;
				} else {
					procSplitSeg(beginIndex, ci - 1);
					setLastMatchEnd(ci - 1);
				}
			beginIndex = ci;
			return ci;
		}
		if ((inputStatus & 0x100000) > 0) {
			nextContextStatus |= 0x10;
			if (numberBeginIndex < 0)
				numberBeginIndex = ci;
			if ((inputStatus & 0x1000000) > 0) {
				if ((inputStatus & 1) > 0)
					nextNumberStatus = 1;
				else if ((inputStatus & 2) > 0)
					nextNumberStatus = 2;
			} else {
				if ((inputStatus & 2) > 0)
					nextNumberStatus = 2;
				else
					nextNumberStatus = 0;
				if (lastMatchEnd != ci - 1)
					if (unmatchFlag) {
						procSplitSeg(unmatchBegin, ci - 1);
						setLastMatchEnd(ci - 1);
						unmatchFlag = false;
					} else {
						procSplitSeg(beginIndex, ci - 1);
						setLastMatchEnd(ci - 1);
					}
				beginIndex = ci;
				return ci;
			}
		}
		if ((inputStatus & 0x1000000) > 0) {
			nextContextStatus |= 0x100;
			int move = procCJK(beginIndex, ci);
			ci += move;
			return ci;
		}
		if ((inputStatus & 0x10000000) > 0) {
			nextContextStatus = 0;
			outputNunmber();
			if (unmatchFlag) {
				procSplitSeg(unmatchBegin, ci - 1);
				setLastMatchEnd(ci - 1);
				unmatchFlag = false;
			} else if (beginIndex < ci) {
				procSplitSeg(beginIndex, ci - 1);
				setLastMatchEnd(ci - 1);
			}
			beginIndex = ci + 1;
			return ci;
		} else {
			return ci;
		}
	}

	private int procCJK(int begin, int end) {
		Hit hit = dict.search(segmentBuff, begin, end);
		if (hit.isMatchAndContinue()) {
			if (unmatchFlag) {
				if (hit.getWordType().isSuffix())
					pushTerm(unmatchBegin, end);
				else if (unmatchBegin == begin - 1
						&& !dict.isNoiseWord(String
								.valueOf(segmentBuff[unmatchBegin]))) {
					pushTerm(unmatchBegin, begin - 1, true);
					pushTerm(unmatchBegin, begin, true);
				} else {
					cjkCut(unmatchBegin, begin - 1);
					pushTerm(unmatchBegin, begin - 1);
				}
				setLastMatchEnd(end);
				pushTerm(begin, end);
				unmatchFlag = false;
			} else if (hit.getWordType().isNormWord()) {
				setLastMatchEnd(end);
				pushTerm(begin, end);
			}
			if (!numberSet.isEmpty()) {
				for (Iterator it = numberSet.iterator(); it.hasNext(); pushTerm((TokenDelegate) it
						.next()))
					;
				if (hit.getWordType().isCount()) {
					TokenDelegate number = (TokenDelegate) numberSet.last();
					pushTerm(number.getBegin(), end, true);
					setLastMatchEnd(end);
				} else {
					numberSet.clear();
				}
			}
			lastHitState = 1;
			return 0;
		}
		if (hit.isMatch()) {
			if (unmatchFlag) {
				if (hit.getWordType().isSuffix())
					pushTerm(unmatchBegin, end);
				else if (unmatchBegin == begin - 1
						&& !dict.isNoiseWord(String
								.valueOf(segmentBuff[unmatchBegin]))) {
					pushTerm(unmatchBegin, begin - 1, true);
					pushTerm(unmatchBegin, begin, true);
				} else {
					cjkCut(unmatchBegin, begin - 1);
					pushTerm(unmatchBegin, begin - 1);
				}
				setLastMatchEnd(end);
				pushTerm(begin, end);
				unmatchFlag = false;
			} else if (hit.getWordType().isNormWord()) {
				setLastMatchEnd(end);
				pushTerm(begin, end);
			} else {
				unmatchFlag = true;
				unmatchBegin = begin;
			}
			if (!numberSet.isEmpty()) {
				for (Iterator it = numberSet.iterator(); it.hasNext(); pushTerm((TokenDelegate) it
						.next()))
					;
				if (hit.getWordType().isCount()) {
					TokenDelegate number = (TokenDelegate) numberSet.last();
					pushTerm(number.getBegin(), end, true);
					setLastMatchEnd(end);
					unmatchFlag = false;
				}
				numberSet.clear();
			}
			beginIndex++;
			lastHitState = 2;
			return beginIndex - end;
		}
		if (hit.isPrefixMatch()) {
			lastHitState = 3;
			return 0;
		}
		if (hit.isUnmatch()) {
			if (lastHitState == 3 && unmatchFlag)
				if (unmatchBegin == begin - 1
						&& !dict.isNoiseWord(String
								.valueOf(segmentBuff[unmatchBegin]))) {
					pushTerm(unmatchBegin, begin - 1, true);
					pushTerm(unmatchBegin, begin, true);
				} else {
					cjkCut(unmatchBegin, begin - 1);
					pushTerm(unmatchBegin, begin - 1);
				}
			if (begin > lastMatchEnd) {
				if (!unmatchFlag) {
					unmatchFlag = true;
					unmatchBegin = begin;
				}
				Hit h = dict.search(segmentBuff, end, end);
				if (dict.isNoiseWord(String.valueOf(segmentBuff[end]))
						&& end - begin == 1) {
					cjkCut(unmatchBegin, begin);
					pushTerm(unmatchBegin, begin);
					unmatchFlag = false;
					setLastMatchEnd(begin);
				} else if (h.isMatch() && h.getWordType().isSuffix()) {
					if (unmatchBegin < end) {
						pushTerm(unmatchBegin, end - 1);
						pushTerm(unmatchBegin, end);
						cjkCut(unmatchBegin, end);
						unmatchFlag = false;
						setLastMatchEnd(end);
						beginIndex = end;
						return 0;
					}
				} else {
					h = dict.search(segmentBuff, begin, begin);
					if (h.isMatch() && h.getWordType().isSuffix()
							&& unmatchBegin != begin) {
						pushTerm(unmatchBegin, begin);
						unmatchFlag = false;
						setLastMatchEnd(begin);
					}
				}
			}
			beginIndex++;
			lastHitState = 4;
			return beginIndex - end;
		} else {
			return 0;
		}
	}

	private void procSplitSeg(int begin, int end) {
		unmatchFlag = false;
		pushTerm(begin, end);
		int localMatch = begin - 1;
		boolean localUnmatch = false;
		int localUnmatchBegin = begin - 1;
		for (int i = begin; i <= end; i++) {
			for (int j = i; j <= end; j++) {
				Hit hit = dict.search(segmentBuff, i, j);
				if (hit.isMatchAndContinue()) {
					if (localUnmatch) {
						pushTerm(localUnmatchBegin, i - 1);
						localUnmatch = false;
					}
					pushTerm(i, j);
					localMatch = j;
					continue;
				}
				if (hit.isMatch()) {
					if (localUnmatch) {
						pushTerm(localUnmatchBegin, i - 1);
						localUnmatch = false;
					}
					pushTerm(i, j);
					localMatch = j;
					break;
				}
				if (hit.isPrefixMatch() || !hit.isUnmatch())
					continue;
				if (!localUnmatch) {
					localUnmatch = true;
					localUnmatchBegin = i;
				}
				break;
			}

		}

		if (localMatch != end) {
			pushTerm(localMatch + 1, end);
			cjkCut(localMatch + 1, end);
		}
	}

	private void pushTerm(int begin, int end, boolean directOutput) {
		if (begin > end)
			return;
		if (!directOutput) {
			Hit h = dict.search(segmentBuff, begin, end);
			if (!h.isMatch() && begin != end
					&& dict.isNoiseWord(String.valueOf(segmentBuff[begin])))
				begin++;
			if (dict.isNbSign(segmentBuff[end])
					|| dict.isConnector(segmentBuff[end]))
				end--;
		}
		TokenDelegate td = new TokenDelegate(offset, begin, end);
		if (mircoSupported) {
			String w = (new String(segmentBuff, begin, (end - begin) + 1))
					.toLowerCase();
			if (!dict.isUselessWord(w)) {
				td.setTerm(w);
				tokens.add(td);
			}
		} else {
			tokens.add(td);
		}
	}

	private void pushTerm(int begin, int end) {
		pushTerm(begin, end, false);
	}

	private void pushTerm(TokenDelegate td) {
		if (mircoSupported) {
			String w = new String(segmentBuff, td.getBegin(), (td.getEnd() - td
					.getBegin()) + 1);
			if (!dict.isUselessWord(w)) {
				td.setTerm(w);
				tokens.add(td);
			}
		} else {
			tokens.add(td);
		}
	}

	private void pushNumber(int begin, int end) {
		if (dict.isNbSign(segmentBuff[end])
				|| dict.isConnector(segmentBuff[end])) {
			pushTerm(begin, end - 1);
			return;
		} else {
			TokenDelegate td = new TokenDelegate(offset, begin, end);
			numberSet.add(td);
			return;
		}
	}

	private void outputNunmber() {
		for (Iterator it = numberSet.iterator(); it.hasNext(); pushTerm((TokenDelegate) it
				.next()))
			;
		numberSet.clear();
	}

	private void cjkCut(int begin, int end) {
		if (end - begin <= 1)
			return;
		for (int i = begin; i < end; i++)
			pushTerm(i, i + 1, true);

	}

	private void setLastMatchEnd(int end) {
		if (lastMatchEnd < end)
			lastMatchEnd = end;
	}

	public char toDBC(char input) {
		if (input == '\u3000')
			input = ' ';
		if (input > '\uFF00' && input < '\uFF5F')
			input -= '\uFEE0';
		return input;
	}

	private Dictionary dict;
	private char segmentBuff[];
	private boolean mircoSupported;
	private int offset;
	private TreeSet tokens;
	private TreeSet numberSet;
	private static final int CTX_STA_INIT = 0;
	private static final int CTX_STA_LETTER = 1;
	private static final int CTX_STA_NUMBER = 16;
	private static final int CTX_STA_CJK = 256;
	private int contextStatus;
	private int nextContextStatus;
	private int inputStatus;
	private int beginIndex;
	private int lastMatchEnd;
	private int numberBeginIndex;
	private int numberStatus;
	private int nextNumberStatus;
	private boolean unmatchFlag;
	private int unmatchBegin;
	private boolean breakAndRead;
	private int bAr_offset;
	int lastHitState;
}
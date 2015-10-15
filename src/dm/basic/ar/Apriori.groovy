package dm.basic.ar

import flib.util.CountMap
import flib.util.SetOP
import flib.util.Tuple as JTuple

class Apriori {
	def confidence = 0.8 	// accuracy
	def minSupport = 1  	// coverage
	SetOP sop = new SetOP()
	
	class SuppCmp implements Comparator<JTuple>
	{
		@Override
		public int compare(JTuple o1, JTuple o2) {
			return o2.getInt(0).compareTo(o1.getInt(0))
		}		
	}
	
	class ConfCmp implements Comparator<JTuple>
	{

		@Override
		public int compare(JTuple o1, JTuple o2) {
			return o2.get(0).compareTo(o1.get(0))
		}
		
	}
	
	class ListCmp implements Comparator<List>
	{

		@Override
		public int compare(List s1, List s2) {
			int m = Math.min(s1.size(), s2.size())			
			if(m>0)
			{
				Iterator i1 = s1.iterator()
				Iterator i2 = s2.iterator()
				for(int i=0; i<m; i++)
				{
					int c = i1.next().compareTo(i2.next())
					if(c!=0) return c
				}				
				return s1.size()>s2.size()?1:-1
			}
			else
			{				
				if(s1.size()==s2.size()) return 0
				else return s1.size()==0?-1:1
			}
		}
		
	}
	class SetCmp implements Comparator<Set>{
		@Override
		public int compare(Set s1, Set s2) {			
			int m = Math.min(s1.size(), s2.size())
			if(m>0)
			{
				Iterator i1 = s1.iterator()
				Iterator i2 = s2.iterator()
				for(int i=0; i<m; i++)
				{
					int c = i1.next().compareTo(i2.next())
					if(c!=0) return c
				}				
				return s1.size()>s2.size()?1:-1
			}
			else
			{
				return s1.size()==0?-1:1
			}
		}		
	}
	
	def loadDataSet()
	{
		def data = []
		data.add(["ugly", "a-m", "h-m", "hasCar", "rich"])
		data.add(["normal", "a-y", "h-m", "noCar", "notRich"])
		data.add(["handsome", "a-m", "h-h", "noCar", "notRich"])
		data.add(["ugly", "a-o", "h-l", "noCar", "notRich"])
		data.add(["normal", "a-m", "h-m", "hasCar", "rich"])
		data.add(["normal", "a-y", "h-m", "noCar", "notRich"])
		data.add(["normal", "a-m", "h-m", "hasCar", "rich"])
		data.add(["ugly", "a-o", "h-h", "noCar", "rich"])
		data.add(["handsome", "a-y", "h-h", "hasCar", "rich"])
		data.add(["handsome", "a-y", "h-l", "noCar", "rich"])
		data.add(["ugly", "a-y", "h-l", "hasCar", "rich"])
		data.add(["ugly", "a-m", "h-l", "noCar", "notRich"])
		data.add(["ugly", "a-y", "h-l", "noCar", "rich"])
		data.add(["normal", "a-m", "h-m", "hasCar", "rich"])
		data.add(["ugly", "a-o", "h-m", "hasCar", "rich"])
		data.add(["handsome", "a-m", "h-m", "noCar", "notRich"])
		data.add(["handsome", "a-y", "h-m", "noCar", "notRich"])
		data.add(["handsome", "a-y", "h-m", "noCar", "rich"])
		data.add(["handsome", "a-y", "h-l", "noCar", "notRich"])
		data.add(["handsome", "a-m", "h-m", "hasCar", "rich"])
		data.add(["ugly", "a-o", "a-l", "hasCar", "rich"])
		data.add(["normal", "a-m", "a-h", "hasCar", "notRich"])
		data.add(["normal", "a-m", "h-h", "noCar", "notRich"])
		data.add(["normal", "a-m", "h-h", "hasCar", "rich"])
		data.add(["normal", "a-m", "h-m", "hasCar", "rich"])
		//return [[1, 3, 4], [2, 3, 5], [1, 2, 3, 5], [2, 5]]
		return data
	}
	def createC1(dataSet)
	{
		def T1 = []; def C1 = []
		CountMap cm = new CountMap()
		for(transaction in dataSet)
		{
			for(item in transaction)
			{
				def itemSet = [item]
				cm.count(item)
				if(!T1.contains(itemSet))
				{
					T1.add(itemSet)
					Collections.sort(T1, new ListCmp());
				}				
			}			
		}		
		for(iSet in T1) { 
			if(cm.getCount(iSet[0])>=minSupport) 
			{
				def itemSet = [iSet[0]] as TreeSet
				C1.add(itemSet)
			} 
		}
		return C1
	}
	
	def unSet(s1, s2)
	{
		def ms = [] as Set
		ms.addAll(s1)
		ms.addAll(s2)
		return ms
	}
	
	def minusSet(s1, s2)
	{
		def ms = [] as Set
		ms.addAll(s1)
		ms.removeAll(s2)
		return ms
	}
	
	/**
	 * Confirm if two input Set are equal.
	 * 
	 * @param s1:Set1
	 * @param s2:Set2
	 * @return true if s1==s2; otherwise false
	 */
	def eqSet(s1, s2)
	{
		if(s1.size() == s2.size())
		{
			Iterator i1 = s1.iterator()
			Iterator i2 = s2.iterator()
			while(i1.hasNext())
			{
				if(!i1.next().equals(i2.next())) return false
			}
		}
		return true
	}
	
	def contains(c, s)
	{
		for(e in s) if(!(e in c)) return false
		return true
	}
	
	def scanD(D, Ck)
	{
		def ssCnt = [:].withDefault { key -> return 0}
		for(tid in D)
		{
			for(Set can in Ck)
			{
				//printf("\t[Test] Scan Ck=%s\n", can.toString())
				if(contains(tid, can))
				{
					//printf("\t[Test] %s contains %s\n", tid.toString(), can.toString())
					ssCnt[can] += 1
				}
			}
		}
		
		double numItems = D.size()
		def retList = []
		def supportData = [:]
		//for(key in ssCnt)
		ssCnt.each { key, val->
			double support = val/numItems // Calculate support for every itemset
			//if(support >= confidence)
			//{
			//	retList.add(key)
			//}
			if(val>minSupport) retList.add(key)			
			supportData[key] = val
		}
		return new JTuple(retList, supportData)
	}
	
	def extList(list, range)
	{
		def olist = [] as Set
		range.each{
			olist.add(list[it])
		}
		return olist
	}
	
	/**
	 * Generate C(k) - Based on given C(k-1) to generate C(k).  
	 * 
	 * @param Lk:
	 * 	A List contains C(1) to C(k-1)  
	 * @param k:
	 * 	int 
	 * 	
	 * @return:
	 * 	A list contains C(k)  
	 */
	def aprioriGen(Lk, k)
	{		
		//printf("\t[Test] Lk=%s (k=%d)\n", Lk, k)
		def sc = new SetCmp() 
		def retList = []  
		int lenLk = Lk.size()
		lenLk.times{ i->
			def L1 = extList(Lk[i], (0..<k-2))			
			//if(!(L1 instanceof List)) L1 = [L1]						
			(i+1..<lenLk).each{ j->
				// Join sets if first k-2 items are equal 				
				def L2 = extList(Lk[j], (0..<k-2))
				//if(!(L2 instanceof List)) L2 = [L2]
				//printf("\t[Test] L1=%s; L2=%s\n", L1.toString(), L2.toString())
				//Collections.sort(L1, sc)
				//Collections.sort(L2, sc)
				if(eqSet(L1, L2))
				{		
					//printf("\t\tBingo!\n")			
					retList.add(sop.UNION(Lk[i], Lk[j]))
				}
			}
		}
		return retList
	}
	
	def _usd(supportData, supK)
	{
		supK.each{ k, v->
			supportData[k]=v
		}
	}
	
	/**
	 * Generate L(2)~L(k) - Based on given dataset to generate L(2)~L(k).  
	 * 
	 * @param dataSet:
	 * 	Dataset 
	 * @param minSupport:
	 * 	Minumum support for candidate item set.  
	 * 
	 * @return
	 * 	A tuple (L, supportData)  
	 * 	L: A list contains candidate item set.  
	 * 	supportData: The dict with mapping key as L(2)~L(m). The value is the support value.  
	 */
	def genLk(dataSet)
	{
		def C1 = createC1(dataSet)  
		JTuple tup = scanD(dataSet, C1)  
		def L1 = tup.get(0)
		//printf("\t[Test] L1=%s\n", L1.toString())
		def supportData = tup.get(1)
		def L = [L1]		
		def k = 2
		while (L[k-2].size() > 0)
		{
			def Ck = aprioriGen(L[k-2], k)
			JTuple tt = scanD(dataSet, Ck)
			def Lk = tt.get(0);def supK = tt.get(1)
			L.add(Lk)
			_usd(supportData, supK)
			k += 1
		}
		L.pop()
		return new JTuple(L, supportData)
	}
	
	/**
	 * Generate rules based on given candidate item set.  
	 * 
	 * @param L:
	 * 	Candidate item set list 
	 * @param supportData:
	 * 	Support dict with key as frequent item set and value as support.  
	 * 
	 * @return:
	 * 	Rule list  
	 */
	def generateRules(L, supportData)
	{
		def bigRuleList = []  
		// Get only sets with two or more items   
		(1..<L.size()).each{
			L[it].each{ freqSet->
				def H1 = []
				// consequence contains only one item  
				for(item in freqSet)
				{
					def s = [] as TreeSet; s.add(item)
					H1.add(s)
				}
				if (it > 1)
				{
					rulesFromConseq(freqSet, H1, supportData, bigRuleList)
				}
				else
				{
					calcConf(freqSet, H1, supportData, bigRuleList)
				}
			}
		}
		return bigRuleList
	}
	
	/**
	 * Prune the consequent set based on given minimum confidence. 
	 * 
	 * @param freqSet:
	 * 	Frequent item set  
	 * @param H:
	 * 	consequent set  
	 * @param supportData:
	 * 	Support dict with key as frequent item set and value as support. 
	 * @param brl:
	 * 	List to hold tuple(antecedent set, consequent set, confidence)  
	 * 
	 * @return:
	 * 	Pruned item list
	 */
	def calcConf(freqSet, H, supportData, brl)
	{
		def prunedH = []
		H.each{ conseq->
			def ms = minusSet(freqSet, conseq)
			double conf = ((double)supportData[freqSet])/supportData[ms]
			if(conf>=confidence)
			{
				//printf("\t[Test] %s --> %s (%.02f)\n", ms, conseq, conf)
				brl.add(new JTuple(ms, conseq, conf))
				prunedH.add(conseq)
			}
		}
		return prunedH
	}
	
	/**
	 * Shift one item from condition to consequence to generate new rule set.  
	 * 
	 * @param freqSet:
	 * 	Frequent item set of specific length of composition.  
	 * @param H:
	 * 	Consequence item set 
	 * @param supportData:
	 * 	Support dict with key as frequent item set and value as support.  
	 * @param brl:
	 * 	List to hold tuple(antecedent set, consequent set, confidence)  
	 * 
	 * @return:
	 * 	None
	 */
	def rulesFromConseq(freqSet, H, supportData, brl)
	{
		int m = H[0].size()
		if(freqSet.size() > (m+1)) // Try further merging 
		{
			def Hmp1 = aprioriGen(H, m + 1) // Create Hm+1new candidates
			Hmp1 = calcConf(freqSet, Hmp1, supportData, brl)
			if (Hmp1.size() > 1)
			{
				rulesFromConseq(freqSet, Hmp1, supportData, brl)
			}
		}
	}
	
	def sortBySupp(rules, S)
	{
		PriorityQueue<JTuple> pq = new PriorityQueue<JTuple>(rules.size(), new SuppCmp())
		rules.each{ rt->
			def ms = rt.get(0); def conseq = rt.get(1); def conf = rt.get(2)
			pq.add(new JTuple(S.get(unSet(ms, conseq)), rt))
		}
		def nRules = []
		JTuple t=null
		while((t=pq.poll())!=null) nRules.add(t.get(1))
		return nRules
	}
	
	def sortByConf(rules, S)
	{
		PriorityQueue<JTuple> pq = new PriorityQueue<JTuple>(rules.size(), new ConfCmp())
		rules.each{ rt->
			def ms = rt.get(0); def conseq = rt.get(1); def conf = rt.get(2)
			pq.add(new JTuple(conf, rt))
		}
		def nRules = []
		JTuple t=null
		while((t=pq.poll())!=null) nRules.add(t.get(1))
		return nRules
	}
	
	def run(dtSet)
	{
		def t = genLk(dtSet)
		def L = t.get(0)
		def S = t.get(1)
		return generateRules(L, S)
	}
	
	static void main(args)
	{
		
	}
}

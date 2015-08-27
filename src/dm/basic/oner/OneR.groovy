package dm.basic.oner

import dm.input.SimpleIn
import flib.util.CountMap
import flib.util.Tuple

class OneR {
	public static void main(args)
	{
		// Reading training data
		SimpleIn si = new SimpleIn(new File("data/weathers.dat"))
		
		int ei=-1;
		def headers = si.getHeaders()
		ei=headers.findIndexOf { h-> h.startsWith('*')}
		printf("\t[Info] Target Header: %d (%s)\n", ei, headers[ei])
		printf("\t[Info] Start 1R Algorithm...\n")
		def trainMap = [:]
		printf("\t\t0) Initialize...\n")
		headers.size().times{i->
			if(i==ei) return
			def cmm = [:].withDefault { k -> return new CountMap()}
			trainMap[i] = new Tuple(headers[i], [], cmm, new TreeSet())
		}
		
		printf("\t\t1) Start Analyze data...\n")
		Iterator datIter = si.iterator()
		while(datIter.hasNext())
		{
			def r = datIter.next()
			def p = r[ei]
			for(int i=0; i<r.size(); i++)
			{								
				if(i==ei) continue
				Tuple t = trainMap[i]
				t.get(1).add(r[i])
				t.get(2)[r[i]].count(p)
				t.get(3).add(r[i])
			}
		}
		
		printf("\t\t2) Pick attribute with less error...\n")
		int gErr=Integer.MAX_VALUE
		String attr=null
		for(Tuple t:trainMap.values())
		{
			int err=0
			printf("\t\t\tHeader='%s':\n", t.get(0))
			for(String v:t.get(3))
			{
				CountMap cm = t.get(2)[v]
				def maj = cm.major()			/*Major category/predict list*/
				def mc = maj[0]					/*Pickup first major category/predict*/
				def mcc = cm.getCount(mc)		/*Major category/predict count*/
				def tc = cm.size()				/*Total size of this attribute*/
				printf("\t\t\t\tValue='%s'->%s (%d/%d)\n", v, mc, mcc, tc)
				err+=(tc-mcc)
			}
			if(err<gErr)
			{
				gErr=err
				attr=t.get(0)
			}
		}
		
		printf("\t\t3) Generate 1R: %s\n", attr)
	}
}

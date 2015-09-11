package dm.basic.statistic

import dm.input.SimpleIn
import flib.util.CountMap

class BayesianModel {
	static def missMu = 0.0001
	
	class Classify{
		def headers
		def bayesProbs = [:]
		def yProbs
		def pySet
		
		public Tuple predict(String fs)
		{
			def mxProb=Double.MIN_VALUE
			def csY = null			
			def feats = fs.split(',')
			def psum=0.0
			for(def py:pySet)
			{
				def tp=1.0
				feats.eachWithIndex { v, i ->
					tp*=bayesProbs[i][v][py]					
				}
				tp*=yProbs[py]
				if(tp>mxProb)
				{
					mxProb = tp
					csY = py
				}				
				psum+=tp
			}
			return new Tuple(csY, mxProb/psum)
		}
	}
	
	public Classify train(SimpleIn si)
	{
		int ei=-1;
		def headers = si.getHeaders()
		ei=headers.findIndexOf { h-> h.startsWith('*')}
		printf("\t[Info] Target Header: %d (%s)\n", ei, headers[ei])
		printf("\t[Info] Start Buiding Bayesian Model...\n")
		
		// Key as feature index; Value=['feature name',
		//                              [feature value list],
		//                              Map(k=feature value, v=Count of y),
		//                              Count map(feature value),
		//                              Possible value set]
		def trainMap = [:]
		def yProbs = [:]				/* Pr[H] map*/
		Set pySet = new TreeSet()		/*Possible Output Y Set*/
		CountMap yCM = new CountMap()
		
		printf("\t\t0) Initialize...\n")
		headers.size().times{i->
			if(i==ei) return
			def cmm = [:].withDefault { k -> return new CountMap()}
			def cmf = new CountMap()
			def cmy = [:].withDefault { k -> return new CountMap()}
			trainMap[i] = new Tuple(headers[i], [], cmm, cmf, cmy, new TreeSet())
		}
		
		printf("\t\t1) Start Analyze data...\n")
		Iterator datIter = si.iterator()
		while(datIter.hasNext())
		{
			def r = datIter.next()
			def p = r[ei]
			pySet.add(p)
			for(int i=0; i<r.size(); i++)
			{
				if(i==ei) continue
				Tuple t = trainMap[i]
				t.get(1).add(r[i])
				t.get(2)[r[i]].count(p)
				t.get(3).count(r[i])
				t.get(4)[p].count(r[i])
				t.get(5).add(r[i])
			}
			yCM.count(p)
		}
		pySet.each{ yv->
			yProbs[yv] = yCM.getCount(yv)/yCM.size()
		}
		
		printf("\t\t2) Build up Bayeisan probability table...\n")
		def bayesProbs = [:]
		trainMap.each{ entry->
			def tup = entry.value
			def fi = entry.key
			def fn = tup[0]
			def allRd = tup[1]
			def cmm = tup[2]
			def cmf = tup[3]
			def cmy = tup[4]
			def allPV = tup[5]
			def pt = [:].withDefault { k->
				return [:].withDefault { yk->
					return 0
				}
			}
			allPV.each{ fv->
				pySet.each{ yv->
					def pf=((missMu+cmf.getCount(fv)))/(allRd.size()+missMu*allPV.size())
					def py=((missMu+cmy[yv].getCount(fv)))/(cmy[yv].size()+(missMu*(allPV.size()+1)))
					pt[fv][yv]=py
					printf("\t\t\tP(%s|'%s')=%.05f (%d/%d)\n", fv, yv, pt[fv][yv], cmy[yv].getCount(fv), cmy[yv].size())
				}
			}
			bayesProbs[fi]=pt
		}
		return new Classify(pySet:pySet, headers:headers, bayesProbs:bayesProbs, yProbs:yProbs)
	}
	
	static void Test()
	{
		// Reading training data
		SimpleIn si = new SimpleIn(new File("data/weathers.dat"))
		
		int ei=-1;
		def headers = si.getHeaders()
		ei=headers.findIndexOf { h-> h.startsWith('*')}
		printf("\t[Info] Target Header: %d (%s)\n", ei, headers[ei])
		printf("\t[Info] Start Buiding Bayesian Model...\n")
		
		// Key as feature index; Value=['feature name',
		//                              [feature value list],
		//                              Map(k=feature value, v=Count of y),
		//                              Count map(feature value),
		//                              Possible value set]
		def trainMap = [:]
		Set pySet = new TreeSet()		/*Possible Output Y Set*/
		
		printf("\t\t0) Initialize...\n")
		headers.size().times{i->
			if(i==ei) return
			def cmm = [:].withDefault { k -> return new CountMap()}
			def cmf = new CountMap()
			def cmy = [:].withDefault { k -> return new CountMap()}
			trainMap[i] = new Tuple(headers[i], [], cmm, cmf, cmy, new TreeSet())
		}
		
		printf("\t\t1) Start Analyze data...\n")
		Iterator datIter = si.iterator()
		while(datIter.hasNext())
		{
			def r = datIter.next()
			def p = r[ei]
			pySet.add(p)
			for(int i=0; i<r.size(); i++)
			{
				if(i==ei) continue
				Tuple t = trainMap[i]
				t.get(1).add(r[i])
				t.get(2)[r[i]].count(p)
				t.get(3).count(r[i])
				t.get(4)[p].count(r[i])
				t.get(5).add(r[i])
			}
		}
		
		printf("\t\t2) Build up Bayeisan probability table...\n")
		def bayesProbs = [:]
		trainMap.each{ entry->
			def tup = entry.value
			def fi = entry.key
			def fn = tup[0]
			def allRd = tup[1]
			def cmm = tup[2]
			def cmf = tup[3]
			def cmy = tup[4]
			def allPV = tup[5]
			def pt = [:].withDefault { k->
				return [:].withDefault { yk->
					return 0
				}
			}
			allPV.each{ fv->
				pySet.each{ yv->
					def pf=((missMu+cmf.getCount(fv)))/(allRd.size()+missMu*allPV.size())
					def py=((missMu+cmy[yv].getCount(fv)))/(cmy[yv].size()+(missMu*(allPV.size()+1)))
					pt[fv][yv]=py
					printf("\t\t\tP(%s|'%s')=%.05f (%d/%d)\n", fv, yv, pt[fv][yv], cmy[yv].getCount(fv), cmy[yv].size())
				}
			}
			bayesProbs[fi]=pt
		}
		
		printf("\t\t3) Testing...\n")
		def mxProb=Double.MIN_VALUE
		def csY = null
		def featStr = 'sunny,cool,high,true'
		def feats = featStr.split(',')
		for(def py:pySet)
		{
			printf("\t\t\tlikelihood of '%s'=", py)
			def tp=1.0
			feats.eachWithIndex { v, i ->
				tp*=bayesProbs[i][v][py]
				if(tp>mxProb)
				{
					mxProb = tp
					csY = py
				}
			}
			printf("=%.08f\n", tp)
		}
		println()
		printf("\t\t\t'%s' -> %s\n", featStr, csY)
	}
	
	static void main(args)
	{
		SimpleIn si = new SimpleIn(new File("data/weathers.dat"))
		BayesianModel bm = new BayesianModel()
		Classify cfy = bm.train(si)
		def test_data = ['sunny,cool,high,true',
			             'sunny,cool,normal,true',
						 'rainy,mild,normal,false']
		printf("\t[Info] Start evaluation classify...\n")
		for(def td:test_data)
		{
			def rst = cfy.predict(td)
			printf("\t%s -> %s (%.02f%%)\n", td, rst.get(0), 100*rst.get(1))
		}		
	}
}

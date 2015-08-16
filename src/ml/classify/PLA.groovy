package ml.classify

class PLAClassify{
	def w			/*Weighting Array*/
	def loop		/*Loop in PLA*/
	def sp=-1		/*Spending Time In msec*/
	
	int classify(def x)
	{
		def d = [1] // x0
		d.addAll(x); 
		if(w.size()!=d.size()) 
			throw new Exception(String.format("Unbalanced(len(w)=%d, len(x)=%d)", w.size(), d.size()))
		def sum=0.0
		w.eachWithIndex{ v, i->
			sum+=v*d[i]
		}
		return sum>0?1:-1
	}
}

class PLA {
	def _inp_with_md(def x, def w)
	{
		def sum=0.0
		w.eachWithIndex { v, i->
			sum+=v*x[i]
		}
		return [sum>0?1:-1, Math.abs(sum)]
	}
	
	def _inp(def x, def w)
	{
		def sum=0.0
		w.eachWithIndex { v, i->			
			sum+=v*x[i]
		}
		return sum>0?1:-1
	}
	
	def _genRdmD(def s)
	{
		def r = []
		printf("\t[Test] %s\n", s)
		s.times{ i->
			r.add(i)
		}		
		Collections.shuffle(r)				
		return r
	}
	
	/**
	 * Perception Learning Algorithm in Pocket 
	 * 
	 * @param x: Training data array
	 * @param y: Expectation array
	 * @param limit: Loop limit
	 * @param hlimit: Limit of unchanged missing count to break loop
	 * 
	 * @return PLAClassify object
	 */
	def pocket(def x, def y, def limit=-1, def hlimit=Integer.MAX_VALUE)
	{
		def d = []
		x.each{ xi->
			def di=[1]
			di.addAll(xi)
			d.add(di)
		}	
		def w = []
		def r = _genRdmD(x.size())
		(x[0].size()+1).times{w.add(0)}
		int loop=0
		int pmiss=0
		int h=0
		int st = System.currentTimeMillis()
		double pmdAcm=Double.MAX_VALUE
		while(true)
		{
			int miss=0
			double mdAcm=0.0
			r.each { i->
				def v = y[i]
				def exp = _inp_with_md(d[i], w)
				if(exp[0]!=v)
				{
					w.size().times{ wi->
						w[wi]=w[wi]+v*d[i][wi]
					}
					miss++
					mdAcm+=exp[1]
				}
			}
			
			if(miss==pmiss) h++
			//if(mdAcm>pmdAcm) break
			if(miss==0) break
			if(h==hlimit) break
			if(limit>0 && loop>limit) break
			loop++
			pmiss=miss
			pmdAcm=mdAcm
			r = _genRdmD(x.size())
		}
		st = System.currentTimeMillis()-st
		return new PLAClassify(w:w, loop:loop, sp:st)
	}
	
	/**
	 * Perception Learning Algorithm in Cyclic
	 * 
	 * @see
	 * 	http://localhost/jforum/posts/list/3239.page
	 * 	
	 * @param x: Training data array
	 * @param y: Expectation array
	 * @param limit: Loop limit
	 * @param hlimit: Limit of unchanged missing count to break loop
	 * 
	 * @return PLAClassify object
	 */
	def cyclic(def x, def y, def limit=-1, def hlimit=10)
	{
		def d = []
		x.each{ xi->
			def di=[1]
			di.addAll(xi)
			d.add(di)
		}
		def w = []
		(x[0].size()+1).times{w.add(0)}
		int loop=0
		int pmiss=0
		int h=0
		int st = System.currentTimeMillis()
		while(true)
		{
			int miss=0
			y.eachWithIndex { v, i->
				//printf("\t[Info] y[%d]=%d...%s\n", i, v, d[i])
				if(_inp(d[i], w)!=v)
				{
					w.size().times{ wi->
						w[wi]=w[wi]+v*d[i][wi]
					}
					miss++
				}
			}
			
			if(miss==pmiss) h++
			if(miss==0) break
			if(h==hlimit) break
			if(limit>0 && loop>limit) break			
			loop++
			pmiss=miss
		}		
		st = System.currentTimeMillis()-st
		return new PLAClassify(w:w, loop:loop, sp:st)
	}
	
	static void main(args)
	{
		// http://www.tutorialspoint.com/jfreechart/jfreechart_xy_chart.htm
		// http://www.java2s.com/Code/Java/Chart/JFreeChartXYSeriesDemo.htm
		def x = [[1,7], [1,2], [1,4], [-1,3], [-4,-2], [-3,2], [3,-2], [-2, -11], [2.5, -15]]
		def y = [1,1,1,-1,-1,-1,1, -1, 1]
		PLA pla = new PLA()
		PLAClassify cfy = pla.pocket(x, y)
		def t = [[1,3], [-4,1], [2,2], [3,6], [-1,9]]
		t.each{ v->
			printf("\t[Info] %s is classified as %d\n", v, cfy.classify(v))
		}
	}
}

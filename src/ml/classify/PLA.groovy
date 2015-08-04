package ml.classify

class PLAClassify{
	def w
	public PLAClassify(def w)
	{
		this.w = w
	}
	
	int classify(def x)
	{
		def d = []; d.add(1) // x0
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
	def _inp(def x, def w)
	{
		def sum=0.0
		w.eachWithIndex { v, i->
			//printf("\t\tw[%d]=%d, x[%d]=%d...\n", i, v, i, x[i])
			sum+=v*x[i]
		}
		return sum>0?1:-1
	}
	
	/**
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
		(x[0].size()+1).times{w.add(1)}
		int loop=0
		int pmiss=0
		int h=0
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
		printf("\t[Info] Weighting Matrix(%d):\n", loop)
		w.eachWithIndex{v, i->printf("\t\tw[%d]=%s\n", i, v)}
		return new PLAClassify(w)
	}
	
	static void main(args)
	{
		def x = [[1,1], [1,2], [1,4], [-1,3], [-4,-2], [-3,2], [3,-2]]
		def y = [1,1,1,-1,-1,-1,1]
		PLA pla = new PLA()
		PLAClassify cfy = pla.cyclic(x, y)
		def t = [[1,3], [-4,1], [2,2], [3,6], [-1,9]]
		t.each{ v->
			printf("\t[Info] %s is classified as %d\n", v, cfy.classify(v))
		}
	}
}

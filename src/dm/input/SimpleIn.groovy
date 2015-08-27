package dm.input


class SimpleIn implements Iterable{
	File input
	
	class DataIter implements Iterator{
		BufferedReader br = null
		def next = null
		
		public DataIter(File df)
		{
			br =  new BufferedReader(new FileReader(df))
			br.readLine() // Skip headers
		}

		@Override
		public boolean hasNext() {
			if(br!=null)
			{
				def line = br.readLine()
				if(line!=null) 
				{
					next = line.split(",")
					return true
				}
				else
				{
					br.close()
				}
			}
			return false
		}

		@Override
		public def next() {
			return next
		}

		@Override
		public void remove() {
			throw new java.lang.Exception("Not support!")			
		}		
	}
	
	public SimpleIn(File input)
	{
		this.input=input
	}
	
	public def getHeaders()
	{
		def headers = []
		input.withReader {r->
			r.readLine().split(',').each{ h->
				headers.add(h.trim())
			}
		}		
		return headers
	}
	
	@Override
	public Iterator<String> iterator() {
		return new DataIter(input)
	}
	
	public static void main(args)
	{
		SimpleIn si = new SimpleIn(new File("data/weathers.dat"))
		printf("\t[Info] Read input data header (with '*' is predicting item):\n")
		for(h in si.getHeaders())
		{
			printf("\t\t%s\n", h)
		}
		println()
		printf("\t[Info] Read input data record:\n")
		Iterator iter = si.iterator()
		while(iter.hasNext())
		{
			def r = iter.next()
			for(i in r) printf("%s ", i)
			println()
		}
	}
}

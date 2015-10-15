package ml.rg

import la.Matrix
import la.alg.Alg
import ml.data.ui.DataInXYChart

import org.jfree.ui.RefineryUtilities

/**
 * Linear Regression Methods Aggregation Class
 * 
 * @see
 * 		http://localhost/jforum/posts/list/2232.page
 * 
 * @author John
 *
 */
class LinearRG {
	/**
	 * Loading training data and output Tuple(Features list, Label list)
	 * 
	 * @param file: Training input file with format: 
	 *       <feat1><tab><feat2><tab><feat3> ... <featN><tab><label of record1>
	 * 
	 * @return Tuple(Features list, Label list)
	 * 		   Feature list with each row as record of feature list: 
	 * 				[[<feat1>, <feat2>, ... <featN>], ...]
	 *         Label list with each row as label of each record: 
	 *         		[[label of record1],[label of record2] ...]
	 */
	def loadDataSet(File file)
	{
		/*General function to parse tab -delimited floats.*/
		def dataMat = []
		def labelMat = []
		
		file.eachLine{ line->
			def lineArr =[] 
			def curLine = line.trim().split('\t') as String[]
			(0..(curLine.size()-2)).each{
				lineArr.add(Double.valueOf(curLine[it]))
			}
			dataMat.add(lineArr)
			labelMat.add(Double.valueOf(curLine[-1]))
		}
		
		return new Tuple(dataMat, labelMat)
	}
	
	/**
	 * Calculate 'ws' of Linear Regression
	 * 
	 * @param xArr: Features list
	 * @param yArr: Label list
	 * @return Matrix of weighting 
	 */
	Matrix standRegres(xArr,yArr)
	{		
		def yData = []; yData.add(yArr)		
		Matrix yM = new Matrix(yData);		
		yM.t()
		//printf("%s\n", yM)
		
		Matrix xM = new Matrix(xArr)
		//printf("xM:\n%s\n", xM)
		Matrix xT = xM.t(true)
		//printf("xT:\n%s\n", xT)		
		Matrix xTx = xT*xM
		
		if(Alg.Det(xTx)==0.0) throw new Exception("The input matrix is singular!")
		
		//printf("xTx:\n%s\n", xTx)
		Matrix ws = (Alg.Invert(xTx)) * (xT*yM)
		
		return ws
	}
	
	static void main(args)
	{
		// 1) Loading training data
		LinearRG lrg = new LinearRG()
		Tuple tup = lrg.loadDataSet(new File('data/ch8/ex0.txt'))
		
		def xArr = tup.get(0)
		def yArr = tup.get(1)
		printf("\t[Info] Load %d records...\n", tup.get(0).size())		
		
		// 2) Running Linear Regression		
		Matrix ws = lrg.standRegres(xArr, yArr)				
		printf("%s\n", ws)
		def w = [ws.v(0,0), ws.v(1,0), -1]
		
		// 3) Visualization of result
		List<Tuple> datas = new ArrayList<Tuple>()
		for(int i=0; i<xArr.size(); i++)
		{
			def data = [xArr[i][-1], yArr[i]]
			datas.add(new Tuple(1, data))
		}
		datas.add(new Tuple(2, w))				
		DataInXYChart.YLowerRng=2.0; DataInXYChart.YUpperRng=5.0;
		DataInXYChart.minX = 0; DataInXYChart.maxX = 1; DataInXYChart.AEX=false 
		DataInXYChart demo = new DataInXYChart("Scatter Plot Demo 1", datas);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
}

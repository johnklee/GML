package ml.data.ui

import java.awt.Color
import javax.swing.JPanel

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.ApplicationFrame
import org.jfree.ui.RefineryUtilities

import flib.util.Tuple as JT

class DataInXYChart extends ApplicationFrame{
	List<JT> datas = null
	def minX=-5
	def maxX=5
	def minY=-5
	def maxY=5
	
	/**
	 * A demonstration application showing a scatter plot.
	 *
	 * @param title  the frame title.
	 */
	public DataInXYChart(String title, List<JT> datas) {
		super(title);
		this.datas = datas
		JPanel chartPanel = createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));
		setContentPane(chartPanel);
	}
	
	public DataInXYChart(String title, List<JT> x, List<JT> y) {
		super(title);
		this.datas = []
		x.eachWithIndex { v, i->
			datas.add(new JT(y[i], v))
		}		
		JPanel chartPanel = createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));
		setContentPane(chartPanel);
	}
	
	public DataInXYChart(String title, List<JT> x, List<JT> y, List w) {
		super(title);
		this.datas = []
		x.eachWithIndex { v, i->
			datas.add(new JT(y[i], v))
		}
		datas.add(new JT(2, w))
		JPanel chartPanel = createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));
		setContentPane(chartPanel);
	}
	
	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 *
	 * @return A panel.
	 */
	public JPanel createDemoPanel() {
		JFreeChart chart = createChart(createData());
		ChartPanel chartPanel = new ChartPanel(chart);
		//chartPanel.setVerticalAxisTrace(true);
		//chartPanel.setHorizontalAxisTrace(true);
		// popup menu conflicts with axis trace
		chartPanel.setPopupMenu(null);
		
		chartPanel.setDomainZoomable(true);
		chartPanel.setRangeZoomable(true);
		return chartPanel;
	}
	
	private XYDataset createData()
	{
		def w=[0,0,0]
		
		XYSeriesCollection my_data_series= new XYSeriesCollection()
		XYSeries s1 = new XYSeries("Cluster1"); my_data_series.addSeries(s1)
		XYSeries s2 = new XYSeries("Cluster2"); my_data_series.addSeries(s2)
		XYSeries s3 = new XYSeries("Line"); my_data_series.addSeries(s3)
		XYSeries s4 = new XYSeries("Miss"); my_data_series.addSeries(s4)
		for(JT r:datas)
		{
			if(r.get(0)==1) 
			{
				s1.add(r.get(1)[0], r.get(1)[1])
				minX = Math.min((double)minX, (double)r.get(1)[0])
				maxX = Math.max((double)maxX, (double)r.get(1)[0])
				minY = Math.min((double)minY, (double)r.get(1)[1])
				maxY = Math.max((double)maxY, (double)r.get(1)[1])
			}
			else if(r.get(0)==-1)
			{ 
				s2.add(r.get(1)[0], r.get(1)[1])
				minX = Math.min((double)minX, (double)r.get(1)[0])
				maxX = Math.max((double)maxX, (double)r.get(1)[0])
				minY = Math.min((double)minY, (double)r.get(1)[1])
				maxY = Math.max((double)maxY, (double)r.get(1)[1])
			}
			else if(r.get(0)==2) 
			{
				w = r.get(1)
			}
			else if(r.get(0)==3)
			{
				s4.add(r.get(1)[0], r.get(1)[1])
				minX = Math.min((double)minX, (double)r.get(1)[0])
				maxX = Math.max((double)maxX, (double)r.get(1)[0])
			}
		}
		minX-=1
		maxX+=1
		minY-=1
		maxY+=1
		if(w[2]==0)
		{
			s3.add(0, minY)
			s3.add(0, maxY)
		}
		else
		{
			s3.add(minX, -((1*w[0]+minX*w[1]))/(double)w[2])
			s3.add(maxX, -((1*w[0]+maxX*w[1]))/(double)w[2])
		}
		return my_data_series
	}
	
	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createScatterPlot("Scatter Plot Demo 1",
				"X", "Y", dataset, PlotOrientation.VERTICAL, true, false, false);
 
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setNoDataMessage("NO DATA");
		plot.setDomainZeroBaselineVisible(true);
		plot.setRangeZeroBaselineVisible(true);
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setSeriesOutlinePaint(0, Color.black);
		renderer.setUseOutlinePaint(true);
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setAutoRangeIncludesZero(false);
		domainAxis.setTickMarkInsideLength(2.0f);
		domainAxis.setTickMarkOutsideLength(0.0f);
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setTickMarkInsideLength(2.0f);
		rangeAxis.setTickMarkOutsideLength(0.0f);
		
		XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
		xylineandshaperenderer.setSeriesLinesVisible(0, false);
		xylineandshaperenderer.setSeriesShapesVisible(0, true);
		xylineandshaperenderer.setSeriesLinesVisible(1, false);
		xylineandshaperenderer.setSeriesShapesVisible(1, true);
		xylineandshaperenderer.setSeriesLinesVisible(2, true);
		xylineandshaperenderer.setSeriesShapesVisible(2, false);
		xylineandshaperenderer.setSeriesLinesVisible(3, false);
		xylineandshaperenderer.setSeriesShapesVisible(3, true);
		plot.setRenderer(xylineandshaperenderer)
		return chart;
	}
	
	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args  ignored.
	 */
	public static void main(String[] args) {
		List<JT> datas = new ArrayList<JT>()
		datas.add(new JT(0, [1,7]))
		datas.add(new JT(0, [1,4]))
		datas.add(new JT(0, [3,-2]))
		datas.add(new JT(0, [2.5, -15]))
		datas.add(new JT(1, [-1,3]))
		datas.add(new JT(1, [-4,-2]))
		datas.add(new JT(1, [-3,2]))
		datas.add(new JT(1, [-2, -11]))
		datas.add(new JT(2, [3, 10.5, 0]))
		
		DataInXYChart demo = new DataInXYChart("Scatter Plot Demo 1", datas);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
}

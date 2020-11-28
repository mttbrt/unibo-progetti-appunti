package marketanalytics

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control.{CheckMenuItem, Menu, MenuBar, MenuItem, Tab, TabPane}
import scalafx.scene.layout.{BorderPane, Pane}

object Plot extends JFXApp {

  val CHART_WIDTH = 1500
  val CHART_HEIGHT = 750
  val INDICATORS = List("PRICE", "RSI", "OBV", "ADX", "BB_LOW", "BB_HIGH", "BB_MEAN", "MAC_NORMAL", "MAX_SIGNAL")

  val spark: SparkSession = SparkSession.builder()
                            .master("local[*]")
                            .appName("Stock Market Technical Analysis")
                            .getOrCreate()

  val stocks = FileSystem.
              get(new Configuration()).
              listStatus(new Path("src/main/resources/Plot/")). // RDD with all data to plot
              sortBy(_.getPath.getName).
              zipWithIndex.
              map(x => (x._2, x._1.getPath.getName)).
              toList

  stage = new PrimaryStage {
    title = "Stock Price Line Chart"
    scene = new Scene(CHART_WIDTH, CHART_HEIGHT) {
      stylesheets.add("style.css")

      val stockTabs = new TabPane

      for (i <- stocks.indices) {
        val indicators =  FileSystem.
                          get(new Configuration()).
                          listStatus(new Path("src/main/resources/Plot/" + stocks(i)._2 + "/")). // RDD with all data to plot
                          sortBy(_.getPath.getName).
                          zipWithIndex.
                          map(x => (x._2, x._1.getPath.getName)).
                          toList

        val xAxis = new NumberAxis()
        xAxis.label = "Days"

        val yAxis = new NumberAxis()
        yAxis.label = "Values"

        val chart = new LineChart(xAxis, yAxis)
        chart.title = "Stock \"" + stocks(i)._2.toUpperCase + "\" Price Chart"
        chart.minWidth = CHART_WIDTH * 0.98
        chart.minHeight = CHART_HEIGHT * 0.95

        for (j <- indicators.indices) {
          if (INDICATORS contains indicators(j)._2.dropRight(4)) {
            val dataT =  spark.sparkContext.textFile("src/main/resources/Plot/" + stocks(i)._2 + "/" + indicators(j)._2). // Load file to RDD
              map(_.split(",")). // Split the values by comma character
              map { case Array(x, y) => (x.toDouble, y.toDouble) }

            val max = dataT.reduce((acc, value) => { if (acc._2 < value._2) value else acc })
            val min = dataT.reduce((acc, value) => { if (acc._2 > value._2) value else acc })
            val data = dataT.map(x => (x._1, (x._2 - min._2) / (max._2 - min._2))).collect.toSeq // Move data in range [0, 1]
            //        val data = dataT.map(x => (x._1, 2 * ((x._2 - min._2) / (max._2 - min._2)) - 1)).collect.toSeq // Move data in range [-1, 1]

            chart.getData.add(
              XYChart.Series[Number, Number](indicators(j)._2.dropRight(4), ObservableBuffer(data.map(td => XYChart.Data[Number, Number](td._1, td._2)):_*))
            )
          }
        }

        val chartPane = new Pane()
        chartPane.children = Seq(chart)

        val tab = new Tab
        tab.text = stocks(i)._2.toUpperCase
        tab.closable = false
        tab.content = chartPane

        stockTabs += tab
      }

      val rootPane = new BorderPane
      rootPane.top = stockTabs
      root = rootPane

    }
  }
}

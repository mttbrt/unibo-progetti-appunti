package marketanalytics

import java.io.File

import marketanalytics.indicators.{IndicatorADX, IndicatorBB, IndicatorMACD, IndicatorOBV, IndicatorRSI}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime

case class Record (date: DateTime, open: Double, high: Double, low: Double, close: Double, volume: Long) {
  override def toString: String = s"$date: ($open, $high, $low, $close) - $volume"
}

trait IndicatorValue {
  def date: DateTime
}

case class LongIndicatorValue(override val date: DateTime, value: Long) extends IndicatorValue {
  override def toString: String = s"$date: ($value)"
  def + (that: LongIndicatorValue): LongIndicatorValue = LongIndicatorValue(date, this.value + that.value)
}

case class DoubleIndicatorValue(override val date: DateTime, value: Double) extends IndicatorValue {
  override def toString: String = s"$date: ($value)"
  def + (that: DoubleIndicatorValue): DoubleIndicatorValue = DoubleIndicatorValue(date, this.value + that.value)
}

// A simple partitioner that puts each row in the partition we specify by the key
class StraightPartitioner(p: Int) extends org.apache.spark.Partitioner {
  def numPartitions = p
  def getPartition(key: Any) = key.asInstanceOf[Int]
}


object MainApp {

  final val spark: SparkSession = SparkSession.builder()
                                  .master("local[*]")
                                  .appName("Stock Market Technical Analysis")
                                  .getOrCreate()

  def main(args: Array[String]) {
    spark.sparkContext.setLogLevel("ERROR")

    val PATH = "src/main/resources/Stocks/"
    val PERIOD = 14
    val FORECAST = 42
    val BEGIN = DateTime.parse("2004-10-31")
    val END = DateTime.parse("2006-12-01")
    val STOCKS = Array(1, 2)

    val stocks =  FileSystem.
                  get(new Configuration()).
                  listStatus(new Path(PATH)). // RDD with all stocks file names
                  sortBy(_.getPath.getName).
                  zipWithIndex.
                  filter(STOCKS contains _._2) // Filter selected stocks
                  .map(x => (x._2, x._1.getPath.getName))
                  .toList

    stocks.par.foreach { stock =>
      println(stock._2.toUpperCase())
      val rdd = spark.sparkContext.textFile(PATH + stock._2). // Load file to RDD
        mapPartitionsWithIndex{(idx, iter) => if (idx == 0) iter.drop(1) else iter}. // Remove header
        map(_.split(",")). // Split the values by comma character
        map { // Cast rows to Record
          case Array(date, open, high, low, close, volume, openInt) =>
            Record(DateTime.parse(date), open.toDouble, high.toDouble, low.toDouble, close.toDouble, volume.toLong)
        }.
        filter(record => record.date.isAfter(BEGIN) && record.date.isBefore(END)). // Get data in the chosen time window (begin and end excluded)
        zipWithIndex // Give and index to each element
        .map(record => (record._2, record._1)) // Set index as the key

      if (!rdd.isEmpty()) {
        // Create stock directory if does not exist
        val directory = new File("src/main/resources/Plot/" + stock._2.dropRight(4))
        if(!directory.exists)
          directory.mkdir

        // Plot stock price
        Utils.writeToCSV(stock._2.dropRight(4) + "/PRICE.csv", rdd.map(x => (x._1, x._2.close)))

        // ---------- INDICATORS ----------

        // OBV Indicator
        IndicatorOBV.computeOBV(stock._2.dropRight(4), rdd)

        // RSI Indicator
        IndicatorRSI.computeRSI(stock._2.dropRight(4), rdd, PERIOD)

        // MACD Indicator
        IndicatorMACD.computeMACD(stock._2.dropRight(4), rdd.map(_._2))

        // ADX Indicator
        IndicatorADX.computeADX(stock._2.dropRight(4), rdd.map(_._2))

        // BB Indicator
        IndicatorBB.computeBB(stock._2.dropRight(4), rdd.map(_._2))

        // ---------- FORECAST ----------

        // RSI Forecast
//        IndicatorRSI.forecastRSI(rdd, PERIOD, FORECAST)

        // MACD Forecast
//        IndicatorMACD.forecastMACD(rdd.map(_._2), PERIOD, FORECAST)

        // ADX Forecast
//        IndicatorADX.forecastADX(rdd.map(_._2), PERIOD, FORECAST)

        // BB Forecast
//        IndicatorBB.forecastBB(rdd.map(_._2), PERIOD, FORECAST)
      } else
        println("No data in " +
          (PATH + stock._2) + " within time-window [" +
          BEGIN.dayOfMonth().get() + "/" + BEGIN.monthOfYear().get() + "/" + BEGIN.year().get() + " - " +
          END.dayOfMonth().get() + "/" + END.monthOfYear().get() + "/" + END.year().get() + "]")
    }
  }

}

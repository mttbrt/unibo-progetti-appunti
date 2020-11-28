package marketanalytics.indicators

import marketanalytics.MainApp.spark
import marketanalytics.{DoubleIndicatorValue, Record, StraightPartitioner, Utils}
import org.apache.spark.rdd.RDD

object IndicatorRSI {

  def computeRSI(stockName: String, inputData: RDD[(Long, Record)], period: Int): Unit = {
    Utils.writeToCSV(stockName + "/RSI.csv", getRSI(inputData, period).map(x => (x._1.toDouble, x._2.value)))
  }

  // Function to compute Relative Strength Index (RSI) Indicator
  def getRSI(inputData: RDD[(Long, Record)], period: Int): RDD[(Long, DoubleIndicatorValue)] = {
    val rdd = inputData.
      map(record => (record._1 - 1, record._2)). // Slide back input data
      join(inputData). // Couple two days
      map { record => // Compute close values differences
        val current = record._2._1
        val previous = record._2._2

        (record._1, DoubleIndicatorValue(current.date, current.close - previous.close))
      }

    // Create the data with the first period - 1 rows copied to the previous partition
    val partitioned = rdd.sortByKey().mapPartitionsWithIndex((i, it) => {
      val overlap = it.take(period - 1).toArray
      val spill = overlap.iterator.map((i - 1, _))
      val keep = (overlap.iterator ++ it).map((i, _))
      if (i == 0) keep else keep ++ spill
    }).partitionBy(new StraightPartitioner(rdd.partitions.length)).values

    // Calculate indicator on each partition
    partitioned.mapPartitions(p => {
      val sequence = p.toSeq
      val olds = sequence.iterator
      val news = sequence.iterator

      var gain =  news.
        take(period - 1).
        filter(_._2.value >= 0D).
        map(_._2.value).
        sum
      var loss =  sequence.iterator.
        take(period - 1).
        filter(_._2.value < 0D).
        map(_._2.value).
        sum
      loss = if (loss < 0D) -loss else loss

      // At each iteration, a new element is added, and the last one removed, thus the window "moves" forward
      (olds zip news).map({
        case (o, n) => {
          if (n._2.value >= 0D)
            gain += n._2.value
          else
            loss += (if (n._2.value < 0D) -n._2.value else n._2.value)

          val RS =  if (gain > 0D)
            if (loss > 0D)
              (gain / period) / (loss / period)
            else -1D
          else 0D
          val RSI = if (RS > -1D)
            100 - (100 / (1 + RS))
          else -1D

          if (o._2.value >= 0D)
            gain -= o._2.value
          else
            loss -= (if (o._2.value < 0D) -o._2.value else o._2.value)

          (o._1 + (period + 1), DoubleIndicatorValue(n._2.date, RSI))
        }
      }).filter(_._2.value > -1D)
    })
  }

  // Function to check RSI forecast reliability
  def forecastRSI(inputData: RDD[(Long, Record)], period: Int, forecast: Int): Unit = {
    val rsiData = getRSI(inputData, period)

    val baseData =  rsiData.
      map(record => (record._1 - 1, record._2)). // Slide back input data
      join(rsiData) // Couple two days

    // Overbought
    val overboughtIndices = baseData.
      filter( x =>
        x._2._2.value > -1 &&
          x._2._2.value <= 70 && // Previous value
          x._2._1.value > 70). // Filter out just the values that change from <= 70 to > 70
      map(x => (x._1 + 1, x._2._1)).
      collect()

    val overbought = overboughtIndices.map { i =>
      // Get "forecast" elements and convert to RDD
      val subset = spark.sparkContext.parallelize(inputData.filter(x => x._1 >= i._1).take(forecast))
      if (!subset.isEmpty()) {
        val res = !Utils.linearRegression(subset)
        (i._1, if (res) 1D else 0D)
      } else
        (i._1, -1D)
    }.filter(_._2 != -1D)

    val overboughtReliability = overbought.map(_._2.toDouble).sum

    // Oversold
    val oversoldIndices = baseData.
      filter( x =>
        x._2._2.value > -1 &&
          x._2._2.value >= 30 && // Previous value
          x._2._1.value < 30). // Filter out just the values that change from >= 30 to < 30
      map(x => (x._1 + 1, x._2._1)).
      collect()

    val oversold = oversoldIndices.map { i =>
      // Get "forecast" elements and convert to RDD
      val subset = spark.sparkContext.parallelize(inputData.filter(x => x._1 >= i._1).take(forecast))
      if (!subset.isEmpty()) {
        val res = Utils.linearRegression(subset)
        (i._1, if (res) 1D else 0D)
      } else
        (i._1, -1D)
    }.filter(_._2 != -1D)

    val oversoldReliability = oversold.map(_._2.toDouble).sum

    val rsiReliability = Utils.roundToDecimal((overboughtReliability + oversoldReliability) / (overbought.length.toDouble + oversold.length.toDouble) * 100D)
    println("RSI Reliability = " + rsiReliability + "%")
  }

}

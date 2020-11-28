package marketanalytics.indicators

import marketanalytics.MainApp.spark
import marketanalytics.{LongIndicatorValue, Record, Utils}
import org.apache.spark.rdd.RDD

object IndicatorOBV {

  // Function to compute On Balance Volume (OBV) Indicator
  def computeOBV(stockName: String, inputData: RDD[(Long, Record)]): Unit = {
    val rdd = inputData.
      map(record => (record._1 - 1, record._2)). // Slide back input data
      join(inputData). // Couple two days
      map { row => // Compute volume values to add or subtract
        val current = row._2._1
        val previous = row._2._2

        if (current.close > previous.close)
          (row._1, LongIndicatorValue(current.date, current.volume))
        else if (current.close < previous.close)
          (row._1, LongIndicatorValue(current.date, -current.volume))
        else
          (row._1, LongIndicatorValue(current.date, 0L))
      }.sortByKey() // Sort each partition by key

    // Volume starting value
    val firstDate = inputData.first()._2.date
    val firstVolume = inputData.first()._2.volume

    // Compute partial results for each partition
    val partials = rdd.mapPartitionsWithIndex((i, iter) => {
      val (keys, values) = iter.toSeq.unzip
      val partialSums = if (i == 0)
        values.scanLeft(LongIndicatorValue(firstDate, firstVolume))((A, B) => LongIndicatorValue(B.date, A.value + B.value))
      else
        values.scanLeft(LongIndicatorValue(firstDate, 0L))((A, B) => LongIndicatorValue(B.date, A.value + B.value))
      Iterator((keys.zip(partialSums.tail), partialSums.last))
    })

    // Broadcast cumulative sum over partitions
    val sumMap = spark.sparkContext.broadcast(
      rdd.partitions.indices
        .zip(partials.values.collect.scanLeft(LongIndicatorValue(firstDate, 0L)) ((A, B) => LongIndicatorValue(B.date, A.value + B.value)))
        .toMap
    )

    // Compute final results
    val output = partials.keys.mapPartitionsWithIndex((i, iter) => {
      if (iter.isEmpty)
        Iterator()
      else
        iter.next.map {
          case (k, v) => (k, LongIndicatorValue(v.date, v.value + sumMap.value(i).value))
        }.toIterator
    }).sortByKey(numPartitions = 1)

    // Print results out
    Utils.writeToCSV(stockName + "/OBV.csv", output.map(x => (x._1, x._2.value)))
  }

}

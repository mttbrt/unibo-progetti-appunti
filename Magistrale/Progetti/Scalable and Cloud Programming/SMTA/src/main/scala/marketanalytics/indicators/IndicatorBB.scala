package marketanalytics.indicators

import marketanalytics.MainApp.spark
import marketanalytics.{MainApp, Record, Utils}
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime

object IndicatorBB {

  private var final_BB: Array[(DateTime, (Double, Double,Double,Double))] = _

  def computeBB(stockName: String, inputData: RDD[Record]): Unit = {
    val bollinger_iterator = inputData
      .mapPartitions(iter => {
        val slideRecords = iter.sliding(20,1).toArray
        slideRecords.iterator
      }).collect()

    //Iterate over the container of iterators and get list of means and st.dev
    val mean_lists = bollinger_iterator.clone().map(container_of_sequences => {
      simpleMeanAverage(container_of_sequences)
    })

    val stdev_list = bollinger_iterator.clone().map(container_of_sequences => {
      simpleStDev(container_of_sequences)
    })

    val element_to_drop = inputData.count()-mean_lists.length
    val closure_values_list = inputData.zipWithIndex().filter(_._2 >= element_to_drop).keys
      .map(record => record.close).collect()

    val bollinger_bands = (mean_lists,closure_values_list,stdev_list).zipped.toArray.map(pair => (Utils.roundToDecimal(pair._1 - pair._3), pair._1, pair._2, Utils.roundToDecimal(pair._1 + pair._3)))

    val number_days_not_BB = inputData.count()-bollinger_bands.length
    val days_BB = inputData.zipWithIndex().filter(record_index => record_index._2>=number_days_not_BB)
      .keys.map(record => record.date).collect()

    this.final_BB = (days_BB,bollinger_bands).zipped.toArray
    // WRITE CSVs
    Utils.writeToCSV(stockName + "/BB_LOW.csv", MainApp.spark.sparkContext.parallelize(bollinger_bands.zipWithIndex.map(x => (x._2.toDouble, x._1._1)).toSeq))
    Utils.writeToCSV(stockName + "/BB_MEAN.csv", MainApp.spark.sparkContext.parallelize(bollinger_bands.zipWithIndex.map(x => (x._2.toDouble, x._1._2)).toSeq))
    Utils.writeToCSV(stockName + "/BB_HIGH.csv", MainApp.spark.sparkContext.parallelize(bollinger_bands.zipWithIndex.map(x => (x._2.toDouble, x._1._4)).toSeq))
  }

  def simpleStDev(arrayOfRecords: Seq[Record]): Double = {
    val rdd_traditional_values = arrayOfRecords.map(record => (record.close + record.high + record.low)/3)
    val mean = simpleMeanAverage(arrayOfRecords)
    val st_dev = math sqrt rdd_traditional_values.map(tv => math pow(tv - mean,2)).sum/rdd_traditional_values.length
    Utils.roundToDecimal(st_dev)
  }

  def simpleMeanAverage(arrayOfRecords: Seq[Record]): Double = {
    val rdd_traditional_values = arrayOfRecords.map(record => (record.close + record.high + record.low)/3)
    val rdd_dim = rdd_traditional_values.size
    val sum_values_for_mean = rdd_traditional_values.sum
    Utils.roundToDecimal(sum_values_for_mean/rdd_dim)
  }

  def forecastBB(rdd:RDD[Record], period:Int, forecast:Int):Unit = {
    val final_BB = this.final_BB
    val array_rdd = rdd.collect()
    val critical_moments_BB = List(final_BB.filter(record => record._2._3 <= record._2._1),
      final_BB.filter(record => record._2._3 >= record._2._4))
    val accuracy_BB = critical_moments_BB.map(crit_moments => {
      val accuracy_critical = crit_moments.map(record => {
        val days_before_last_adx = spark.sparkContext.parallelize(array_rdd)
          .filter(record_inner => record_inner.date.isBefore(record._1))
          .zipWithIndex()
          .map(key_record => (key_record._2, key_record._1))
          .sortByKey(ascending = false)
          .take(forecast)
          .map(l_rec => l_rec._2)
        val days_after_last_adx = spark.sparkContext.parallelize(array_rdd)
          .filter(record_inner => record_inner.date.isBefore(record._1))
          .zipWithIndex()
          .map(key_record => (key_record._2, key_record._1))
          .sortByKey(ascending = true)
          .take(forecast)
          .map(l_rec => l_rec._2)

        //linear regression over last 14 days before the last ADX
        val lr_over_last_days = Utils.linearRegression(spark.sparkContext.
          parallelize((0L to (days_before_last_adx.length - 1).toLong) zip days_before_last_adx))
        //linear regression over next 14 days after the last ADX
        val lr_over_next_days = Utils.linearRegression(spark.sparkContext.
          parallelize((0L to (days_after_last_adx.length - 1).toLong) zip days_after_last_adx))
        //CASES
        if (record._2._3 >= record._2._4) {//close is greater than high band
          //if the next trend is positive, the predict was wrong
          if (lr_over_last_days != lr_over_next_days) {
            if(!lr_over_next_days) 1
            else 0
          } else 0
        } else if(record._2._3 <= record._2._1) { //close is greater than high band
          //if the next trend is positive, the predict was wrong
          if (lr_over_last_days != lr_over_next_days) {
            if (!lr_over_next_days) 0
            else 1
          } else 0
        }else{ //the close is within the bands
          //the trend has to be stable
          if (lr_over_last_days == lr_over_next_days) 1
          else 0
        }
      }).sum
      accuracy_critical.toDouble / crit_moments.length.toDouble
    })
    println("BB Reliability = " + Utils.roundToDecimal(accuracy_BB.sum/accuracy_BB.length)*100 +"%")
  }

}

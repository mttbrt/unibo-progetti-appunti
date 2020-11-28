package marketanalytics.indicators

import marketanalytics.MainApp.spark
import marketanalytics.{MainApp, Record, Utils}
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime

object IndicatorMACD {

  private val timePeriodDays : (Int,Int,Int) = (12,26,9)
  private val multiplierForSmoothing12:Double = Utils.roundToDecimal(2.0/(timePeriodDays._1.toDouble+1.0))
  private val multiplierForSmoothing26:Double = Utils.roundToDecimal(2.0/(timePeriodDays._2.toDouble+1.0))
  private val multiplierForSmoothing9:Double = Utils.roundToDecimal(2.0/(timePeriodDays._3.toDouble+1.0))
  private var final_macd: Array[(DateTime, Double,Double,Double)] = _

  def computeMACD(stockName: String, inputData: RDD[Record]): Unit = {
    //CALCULATE ALL EMAs
    val zipped_rdd_record = inputData.zipWithIndex()

    //CALCULATE ALL 12 periods EMA
    val rdd_first12_days = zipped_rdd_record.filter(_._2 < 12).keys.map(record => record.close)
    val rdd_remaining_12 = zipped_rdd_record.filter(_._2 >= 12).keys.map(record => record.close)
    val first_EMA_12 = SMA(rdd_first12_days.collect()) //calculate first EMA
    val EMA_values_period_12 = rdd_remaining_12.collect().scanLeft(first_EMA_12)((old_EMA, actual_value_close) => EMA(old_EMA, actual_value_close, multiplierForSmoothing12))

    //CALCULATE ALL 26 periods EMA
    val rdd_first26_days = zipped_rdd_record.filter(_._2 < 26).keys.map(record => record.close)
    val rdd_remaining_26 = zipped_rdd_record.filter(_._2 >= 26).keys.map(record => record.close)
    val first_EMA_26 = SMA(rdd_first26_days.collect()) //calculate first EMA
    val EMA_values_period_26 = rdd_remaining_26.collect().scanLeft(first_EMA_26)((old_EMA, actual_value_close) => EMA(old_EMA, actual_value_close, multiplierForSmoothing26)) //calculate all EMAs
    val MACD_values = (EMA_values_period_26 zip EMA_values_period_12).map(pair => Utils.roundToDecimal(pair._2 - pair._1))

    //CALCULATE ALL 9 PERIODS EMA OF MACD TO GET SIGNAL LINE
    val first_9_MACD_values = MACD_values.take(9)
    val remaining_9_MACD_values = MACD_values.takeRight(MACD_values.length-9)
    val first_MACD_EMA = SMA(first_9_MACD_values)
    val MACD_EMA_values_period_9 = remaining_9_MACD_values.scanLeft(first_MACD_EMA)((old_EMA, actual_value_MACD) => EMA(old_EMA, actual_value_MACD, multiplierForSmoothing9)) //calculate all EMAs

    //GET DAYS: removing 35 because of 26 to calculate MACD and 9 to calculate EMA for signal line
    val days_removing_first_35 = zipped_rdd_record.filter(_._2 >= 34).keys.map(record => record.date).collect()
    val close_removing_first_35 = zipped_rdd_record.filter(_._2 >= 34).keys.map(record => record.close).collect()
    val MACD_values_removing_first_9 = MACD_values.takeRight(MACD_values.length-9)

    //BAD CODE BUT THERE'S NO ALTERNATIVE
    val macd_list = ((days_removing_first_35, close_removing_first_35, MACD_EMA_values_period_9).zipped.toArray,
      MACD_values_removing_first_9).zipped.toArray.map(total_MACD => (total_MACD._1._1,total_MACD._1._2,total_MACD._1._3,total_MACD._2))
    this.final_macd = macd_list
    //WRITE CSVs
    Utils.writeToCSV(stockName + "/MACD_NORMAL.csv", MainApp.spark.sparkContext.parallelize(macd_list.zipWithIndex.map(x => (x._2.toDouble, x._1._3)).toSeq))
    Utils.writeToCSV(stockName + "/MACD_SIGNAL.csv", MainApp.spark.sparkContext.parallelize(macd_list.zipWithIndex.map(x => (x._2.toDouble, x._1._4)).toSeq))
  }

  private def SMA(rddFiltered: Array[Double]): Double = {
    Utils.roundToDecimal(rddFiltered.sum / rddFiltered.length)
  }

  private def EMA(oldEMA:Double, newValueClose: Double, multiplierForSmoothing: Double): Double = {
    Utils.roundToDecimal((newValueClose*multiplierForSmoothing) + (oldEMA*(1-multiplierForSmoothing)))
  }

  def forecastMACD(rdd:RDD[Record], period:Int, forecast:Int):Unit = {
    val final_macd = this.final_macd
    val array_rdd = rdd.collect()
    val critical_moments_BB = final_macd.filter(record => record._3 == record._4)
    val accuracy_MACD = critical_moments_BB.map(record => {
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
      //record._3 is forced to be equal to record._4 by the filter
      if (lr_over_last_days != lr_over_next_days) 1 else 0
    })
    println("MACD Reliability = " + Utils.roundToDecimal(accuracy_MACD.sum.toDouble/accuracy_MACD.length.toDouble)*100 +"%")
  }

}

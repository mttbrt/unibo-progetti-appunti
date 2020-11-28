package marketanalytics.indicators
import marketanalytics.MainApp.spark

import marketanalytics.{MainApp, Record, Utils}
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime

object IndicatorADX {

  private var final_adx: Array[(DateTime, Double, Double)] = _

  def computeADX(stockName: String, inputData: RDD[Record]): Unit = {
    //MAIN
    val rdd_array = inputData.collect()
    val start_date = rdd_array(0).date
    val end_date = inputData.collect()(rdd_array.length-1).date
    val adx_list = averageDirectionalIndex(inputData, start_date, end_date)
    // get all dates binded to adx
    val zipped_rdd_record = inputData.zipWithIndex()
    val date_without_adx = rdd_array.length-adx_list.length
    val dates = zipped_rdd_record.filter(_._2 >= date_without_adx).keys.map(record => record.date).collect()
    //get all close for those dates
    val closes = inputData.filter(record => record.date.isBefore(dates(dates.length-1)) && record.date.isAfter(dates(0))).
      map(rec => rec.close).collect()
    this.final_adx = (dates,adx_list,closes).zipped.toArray

    // WRITE TO CSV
    Utils.writeToCSV(stockName + "/ADX.csv", MainApp.spark.sparkContext.parallelize(this.final_adx.zipWithIndex.map(x => (x._2.toDouble, x._1._2)).toSeq))
    //Utils.writeToCSV(stockName + "/ADX_CLOSE.csv", MainApp.spark.sparkContext.parallelize(this.final_adx.zipWithIndex.map(x => (x._2.toDouble, x._1._3)).toSeq))
  }

  private def smoothedAverageTrueIndex(arrayOfRecords: Array[Record], previousATR:Double): Double = {
    val slided_records_for_previous_days_Close = arrayOfRecords.clone().map(records => records.close).dropRight(1) //this is common to both cases
    val slided_records_for_current_days_High = arrayOfRecords.clone().map(records => records.high).drop(1)
    val slided_records_for_current_days_Low = arrayOfRecords.clone().map(records => records.low).drop(1)
    // current high - current low
    val first_values = arrayOfRecords.dropRight(1).map(record => Utils.roundToDecimal(record.high - record.low))
    // |current high - previous close|
    val second_values = (slided_records_for_current_days_High zip slided_records_for_previous_days_Close)
      .map(pair => Utils.roundToDecimal(math.abs(pair._1 - pair._2)))
    // | current low - previous close|
    val third_values = (slided_records_for_current_days_Low zip slided_records_for_previous_days_Close)
      .map(pair => Utils.roundToDecimal(math.abs(pair._1 - pair._2)))
    //catch greater
    val greaters = (first_values zip second_values zip third_values).map(
      first_couple => math.max(first_couple._2, math.max(first_couple._1._1, first_couple._1._2)))
    //DIFFERENCE IN BASE CASE AND INDUCTIVE CASE
    if (previousATR == -1.0) { //caso base
      Utils.roundToDecimal(greaters.sum / greaters.length) //return smoothed
    } else { //caso induttivo
      assert(arrayOfRecords.length == 2) //dopo il primo ATR, bisogna passare solamente il record corrente e quello precedente
      Utils.roundToDecimal(((previousATR * 13) + greaters(0))/ 14)
    }
  }

  private def positiveNegativeSmoothedDirectionalMovement(arrayOfRecords: Array[Record]): (Double,Double) = {
    //questa formula è generalizzabile, non ha un caso base o induttivo e ha sempre almeno due record
    assert(arrayOfRecords.length >=2) //test size
    val slided_records_for_previous_days_High =  arrayOfRecords.clone().map(records => records.high).dropRight(1)
    val slided_records_for_previous_days_Low =  arrayOfRecords.clone().map(records => records.low).dropRight(1)
    val slided_records_for_current_days_High =  arrayOfRecords.clone().map(records => records.high).drop(1)
    val slided_records_for_current_days_Low =  arrayOfRecords.clone().map(records => records.low).drop(1)
    //calculate upmove and downmove
    val up_move = (slided_records_for_current_days_High zip slided_records_for_previous_days_High).map(pair => Utils.roundToDecimal(pair._1-pair._2))
    val down_move = (slided_records_for_current_days_Low zip slided_records_for_previous_days_Low).map(pair => Utils.roundToDecimal(pair._1-pair._2))
    //directional movements
    val pos_DM = (up_move zip down_move).map(tuple => if (tuple._1 > tuple._2 && tuple._1 > 0.0) tuple._1 else 0.0) //optimistic approach
    val neg_DM = (up_move zip down_move).map(tuple => if (tuple._1 < tuple._2 && tuple._2 > 0.0) tuple._2 else 0.0)
    //smoothed directional movements
    val pos_smoothed_DM = pos_DM.sum - (pos_DM.sum/pos_DM.length) + pos_DM(pos_DM.length-1)
    val neg_smoothed_DM = neg_DM.sum - (neg_DM.sum/neg_DM.length) + neg_DM(neg_DM.length-1)
    (Utils.roundToDecimal(pos_smoothed_DM), Utils.roundToDecimal(neg_smoothed_DM))
  }

  private def positiveNegativeDirectionalIndex(directionalMovements: (Double,Double), averageTrueRange:Double): (Double,Double) = {
    (Utils.roundToDecimal(directionalMovements._1/averageTrueRange*100), Utils.roundToDecimal(directionalMovements._2/averageTrueRange*100))
  }

  private def directionalMovementIndex(positiveNegativeDirectionalIndex: (Double, Double)) = {
    Utils.roundToDecimal(math.abs(positiveNegativeDirectionalIndex._1-positiveNegativeDirectionalIndex._2)/
      math.abs(positiveNegativeDirectionalIndex._1+positiveNegativeDirectionalIndex._2) * 100).toInt
  }

  private def getFirstADXAndLastATR(rddFiltered: RDD[Record]): (Double,Double) = {
    //FILTER FIRST 29 days to warmup and consider others
    val zipped_rdd_record = rddFiltered.zipWithIndex()
    val rdd_first14_days = zipped_rdd_record.filter(_._2 < 15).keys
    val rdd_days_between_2_and_29_first_month = zipped_rdd_record.filter(record_index =>record_index._2 < 30 && record_index._2 > 1).keys
    val rdd_days_between_15_and_29_first_month = zipped_rdd_record.filter(record_index =>record_index._2 < 30 && record_index._2 > 14).keys
    val first_14_ATR_value = smoothedAverageTrueIndex(rdd_first14_days.collect(),-1)
    //Create ITERATOR TO CALCULATE ATR WARMUP VALUES
    val RECORD_ITERATOR_FOR_ATR_WARMUP = rdd_days_between_15_and_29_first_month.mapPartitions(//to create windows of 15 days after the warmup
      iter =>
        iter.sliding(2,1).toArray.iterator
    ).collect()
    //ATR values to calculate first ADX
    val ATR_VALUES = RECORD_ITERATOR_FOR_ATR_WARMUP.scanLeft(first_14_ATR_value)(
      (prev_ATR,records) => smoothedAverageTrueIndex(records.toArray,prev_ATR))
    //println("ATR LENGTH ", ATR_VALUES.length)
    //ATR_VALUES.foreach(println)
    //CREATE ITERATOR TO CALCULATE FIRST DX VALUES, after getting the first 14 days from iterator
    val RECORD_ITERATOR_FOR_DX_WARMUP = rdd_days_between_2_and_29_first_month
      .mapPartitions(//to create windows of 15 days after the warmup
        iter =>
          iter.sliding(15,1).toArray.iterator
      ).collect()
    //println("Lunghezza iteratore DX WARMUP ",RECORD_ITERATOR_FOR_DX_WARMUP.length)
    //CALCULATE FIRST DX VALUES AND GET FIRST ADX
    val first_14_DX_values = (RECORD_ITERATOR_FOR_DX_WARMUP zip ATR_VALUES).map(record_and_atr =>
      directionalMovementIndex(positiveNegativeDirectionalIndex(
        positiveNegativeSmoothedDirectionalMovement(record_and_atr._1.toArray), record_and_atr._2))
    )
    //FIRST ADX
    val first_ADX = Utils.roundToDecimal(first_14_DX_values.sum / first_14_DX_values.length)
    //println(first_ADX)
    (first_ADX,ATR_VALUES(ATR_VALUES.length-1))
  }

  private def getRemainingADX(rddFiltered: RDD[Record], first_ADX: Double, last_ATR: Double): Array[Double] = {
    val zipped_rdd_record = rddFiltered.zipWithIndex()
    val rdd_remaining = zipped_rdd_record.filter(_._2 >= 30).keys
    //CASO INDUTTIVO
    //Create ITERATOR TO CALCULATE ATR WARMUP VALUES
    val RECORD_ITERATOR_FOR_ATR_TOTAL = rdd_remaining
      .mapPartitions(
        iter =>
          iter.sliding(2,1).toArray.iterator
      ).collect()
    //CALCULATE ALL ATR VALUES, the last ATR is that calculated in the 29 day
    val ATR_VALUES_TOTAL = RECORD_ITERATOR_FOR_ATR_TOTAL.scanLeft(last_ATR)(
      (prev_ATR,records) => smoothedAverageTrueIndex(records.toArray,prev_ATR))
    //println("ATR LENGTH TOTAL", ATR_VALUES_TOTAL.length)
    //ATR_VALUES.foreach(println)
    //CREATE ITERATOR TO CALCULATE FIRST DX VALUES, after getting the first 14 days from iterator
    val RECORD_ITERATOR_FOR_DX_TOTAL = rdd_remaining
      .mapPartitions(//to create windows of 15 days after the warmup
        iter =>
          iter.sliding(15,1).toArray.iterator
      ).collect()
    //println("Lunghezza iteratore DX TOTAL ",RECORD_ITERATOR_FOR_DX_TOTAL.length)
    //CALCULATE FIRST DX VALUES AND GET FIRST ADX
    val DX_values_TOTAL = (RECORD_ITERATOR_FOR_DX_TOTAL zip ATR_VALUES_TOTAL).map(record_and_atr =>
      directionalMovementIndex(positiveNegativeDirectionalIndex(positiveNegativeSmoothedDirectionalMovement(record_and_atr._1.toArray),record_and_atr._2))
    )
    val ADX_VALUES = DX_values_TOTAL.scanLeft(first_ADX)((prev_ADX, curr_DX) => Utils.roundToDecimal(((prev_ADX*13)+curr_DX)/14))
    //ADX_VALUES.foreach(println)
    ADX_VALUES
  }

  def averageDirectionalIndex(rdd: RDD[Record], start: DateTime, endTime: DateTime): Array[Double] = {
    //WARMUP
    //get filtered RDD basing on period
    val rdd_filtered = rdd.filter(record => record.date.isAfter(start) && record.date.isBefore(endTime))
    assert(rdd_filtered.count()>=29) //this index require at least 29 days
    val first_ADX_last_ATR = getFirstADXAndLastATR(rdd_filtered)
    val all_ADX = getRemainingADX(rdd_filtered,first_ADX_last_ATR._1, first_ADX_last_ATR._2)
    all_ADX
  }

  def forecastADX(rdd:RDD[Record], period:Int, forecast:Int):Unit = {
    val final_adx = this.final_adx
    val array_rdd = rdd.collect()
    val critical_moments_ADX = List(final_adx.filter(record => record._2 < 20),final_adx.filter(record => record._2 > 30))
    val accuracy = critical_moments_ADX.map(crit_moments => {
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

        if (record._2 < 20 && (lr_over_last_days != lr_over_next_days)) 1
        else if (record._2 > 30 && (lr_over_last_days == lr_over_next_days)) 1
        else 0
      }).sum
      accuracy_critical.toDouble / crit_moments.length.toDouble
    })
    println("ADX Reliability = " + Utils.roundToDecimal(accuracy.sum/accuracy.length)*100 +"%")

  }

}

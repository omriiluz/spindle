///////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2014 Adobe Systems Incorporated. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
///////////////////////////////////////////////////////////////////////////

package com.adobe

// Spark.
import org.apache.spark.{SparkConf,SparkContext,SparkEnv}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import com.datastax.spark.connector._
import org.apache.spark.sql.cassandra.CassandraSQLContext

// Java.
import java.io.{StringWriter,PrintWriter}

class AdhocQueryHandler(sc: SparkContext) {
  //val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  import sqlContext.createSchemaRDD
  val sqlContext = new CassandraSQLContext(sc)
  sqlContext.setKeyspace("px")

  def handle(sqlQuery: String, numResults: Int, days: Seq[String]): String = {
    try {
      /* val separatedData = days.map{ day =>
        val dayData = sqlContext.parquetFile(
          "hdfs://hdfs_master_address:8020/spindle-sample-data/" + day
        )
        dayData.registerAsTable("data_" + day.replace("-","_"))
        dayData
      }
      val allData = separatedData.reduce(_.unionAll(_))
      allData.registerAsTable("all_data") */
      //case class Req(id: java.util.UUID, ts: java.util.UUID, url: String, referrer: String) extends serializable
      //val rdd = sc.cassandraTable[(String, String, String, String)]("test", "requests")
      //val requestsRDD = sc.cassandraTable[(String,String,String,String)]("test", "requests")
      // The schema is encoded in a string
      //val schemaString = "id ts url referrer"
      // Import Spark SQL data types and Row.
      import org.apache.spark.sql._
      // Generate the schema based on the string of schema
      //val schema =
      //  StructType(
      //    schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))
      //    val requestsSchemaRDD = sqlContext.applySchema(requestsRDD, schema)
      //    requestsSchemaRDD.registerTempTable("requests")
      val unescapedQuery = sqlQuery.replace(".EQ.","=")
        .replace(".LT.","<").replace(".GT.",">")

      sqlContext.sql(unescapedQuery).take(numResults).mkString("\n")
    } catch {
      case e: Exception =>
        val sw = new StringWriter()
        val pw = new PrintWriter(sw, true)
        e.printStackTrace(pw)
        "Exception occurred. Unable to process query.\n\n" +
          "Query: " + sqlQuery + "\n\n" +
          sw.getBuffer.toString
    }
  }
}

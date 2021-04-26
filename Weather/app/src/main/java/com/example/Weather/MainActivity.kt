package com.example.Weather

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallAPILoginAsyncTask("hyun","1234").execute()
    }

    private inner class CallAPILoginAsyncTask(val username:String ,val password:String): AsyncTask<Any, Void, String>(){

        private lateinit var customProgressDialog:Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result:String
            var connection:HttpURLConnection?=null
            try{
                val url= URL("https://run.mocky.io/v3/6cf759f7-4692-442b-95cb-effe80a6df36")
                connection=url.openConnection() as HttpURLConnection
                connection.doInput=true
                connection.doOutput=true

                connection.instanceFollowRedirects=true
                connection.requestMethod="POST"
                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")

                connection.useCaches=false

                val writeDataOutputStream=DataOutputStream(connection.outputStream)
                val jsonRequest=JSONObject()
                jsonRequest.put("username",username)
                jsonRequest.put("password",password)

                writeDataOutputStream.writeBytes(jsonRequest.toString())
                writeDataOutputStream.flush()
                writeDataOutputStream.close()


                val httpResult:Int = connection.responseCode
                if(httpResult==HttpURLConnection.HTTP_OK){
                    val inputStream=connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder=StringBuilder()
                    var line:String?
                    try{
                        while(reader.readLine().also{line=it}!=null){
                            stringBuilder.append(line+"\n")

                        }
                    } catch(e:IOException){
                      e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                    }
                    result=stringBuilder.toString()
                }else{
                    result=connection.responseMessage
                }
            }catch (e:SocketTimeoutException){
                result="Connection Timeout"
            }catch (e:Exception){
                result="Error: "+e.message
            }finally {
                connection?.disconnect()
            }
            return result

        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            cancelProgressDialog()

            Log.i("JSON RESPONSE RESULT",result)

            val responseDate=Gson().fromJson(result,ResponseData::class.java)

            Log.i("Id: ","$responseDate.id")
            Log.i("Name: ",responseDate.name)

            Log.i("Completed: ","${responseDate.profile_detail.completed}")
            Log.i("Rating: ","${responseDate.profile_detail.rating}")


            for(item in responseDate.data_list.indices){
                Log.i("Value $item","${responseDate.data_list[item]}")
                Log.i("Id: ","${responseDate.data_list[item].id}")
                Log.i("Value: ",responseDate.data_list[item].value)
            }


            /*
            val jsonObject=JSONObject(result)
            val id=jsonObject.optInt("id")
            val name=jsonObject.optString("name")
            Log.i("Id: ","$id")
            Log.i("Name: ",name)

            val profileDetailObject=jsonObject.optJSONObject("profile_detail")
            val isProfileCompleted=profileDetailObject.optBoolean("completed")
            val rating=profileDetailObject.optDouble("rating")
            Log.i("Completed: ","$isProfileCompleted")
            Log.i("Rating: ","$rating")

            val dataListArray=jsonObject.optJSONArray("data_list")
            for(item in 0 until dataListArray.length()){
                Log.i("Value $item","${dataListArray[item]}")

                val dataItemObject:JSONObject=dataListArray[item] as JSONObject

                val id=dataItemObject.optInt("id")
                val value=dataItemObject.optString("value")
                Log.i("Id: ","$id")
                Log.i("Value: ",value)
            }

             */


        }

        private fun showProgressDialog(){
            customProgressDialog= Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }
    }

















}
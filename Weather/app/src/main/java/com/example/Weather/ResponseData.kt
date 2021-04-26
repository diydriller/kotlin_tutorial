package com.example.Weather;

data class ResponseData (
        val id:Int,
        val name:String,
        val profile_detail:ProfileDetail,
        val data_list:List<DataListDetail>
)

data class ProfileDetail(
        val completed:Boolean,
        val rating:Double
)

data class DataListDetail(
        val id:Int,
        val value:String
)
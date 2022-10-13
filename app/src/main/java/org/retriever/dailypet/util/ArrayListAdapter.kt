package org.retriever.dailypet.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.retriever.dailypet.model.main.Care
import org.retriever.dailypet.model.main.CheckList

class ArrayListAdapter {
    @ToJson
    fun stringListToJson(list: ArrayList<String>): List<String> = list
    @FromJson
    fun stringListFromJson(list: List<String>): ArrayList<String> = ArrayList(list)
    @ToJson
    fun checkListToJson(list: ArrayList<CheckList>): List<CheckList> = list
    @FromJson
    fun checkListFromJson(list: List<CheckList>): ArrayList<CheckList> = ArrayList(list)
    @ToJson
    fun careListToJson(list: ArrayList<Care>): List<Care> = list
    @FromJson
    fun careListFromJson(list: List<Care>): ArrayList<Care> = ArrayList(list)
}
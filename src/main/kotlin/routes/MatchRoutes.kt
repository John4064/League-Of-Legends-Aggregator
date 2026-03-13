package routes

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import logic.AggregationLogicImpl
import logic.IngestionLogicImpl
import logic.RetrievalServiceImpl
import logic.interfaces.IngestionLogic
import logic.interfaces.RetrievalService
import logic.interfaces.AggregationLogic
import models.MatchDto

fun Route.matchRoutes(){
    val logger = KotlinLogging.logger {}

    val retrLogic: RetrievalService = RetrievalServiceImpl()
    val ingLogic: IngestionLogic = IngestionLogicImpl()
    val aggLogic: AggregationLogic = AggregationLogicImpl()
    val username ="Tidal"
    val tagline="RCS"
    route("match"){
        get("/gatherMatches"){
            try{

                val user = call.request.queryParameters["name"] ?: ""
                val tag = call.request.queryParameters["tag"]?: "na1"
                val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 1
                //Grab from riot games API, ingest new ones
                if(user != ""){
                    val matches: ArrayList<MatchDto> = aggLogic.gatherRankedGames(user,tag,count);
                    for (match in matches){
                        ingLogic.insertMatchData(match,username,tagline)
                    }
                    call.respondText { "Found Matches: ${matches.size}" }
                }else{
                    //Todo: Develop proper error handling
                    call.respondText { "Invalid username or tagline" }
                }


            }catch(e: Exception){
                logger.error { e.toString() }
                call.respondText(e.toString())

            }

        }
        get("/findMatches/"){
            try{
                val user = call.request.queryParameters["name"] ?: ""
                val tag = call.request.queryParameters["tag"]?: "na1"
                val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 20

                call.respondText("Hello Dad")


            }catch(e: Exception){
                call.respondText{e.toString()}
            }
        }
    }
}


//in the DB have checks made for existing data to reduce # of api calls for puuid and summonerID
//    val aggLogic: AggregationLogic = AggregationLogicImpl()

//    val username ="Tidal"
//    val tagline="RCS"
//    try{
////        val matches: ArrayList<MatchDto> = aggLogic.gatherRankedGames(username,tagline,922)
////        for (match in matches){
////            ingLogic.insertMatchData(match,username,tagline)
////        }
//        println("ABC")
//    }catch(e: Exception) {
//        logger.error { "Generic Catch All Error" }
//        logger.error { e.toString() }
//        e.printStackTrace()
//
//    }
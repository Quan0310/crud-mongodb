package com.example.demo.utils;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

public class ElasticsearchUtil {

	 private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchUtil.class);
	 
   
   public static Supplier<Query> supplierWithNameField3rd(String fieldValue) {
   	LOG.info("FuzzyQuery iss:"+ matchQueryWithNameField(fieldValue));
   	 Supplier<Query> supplier = () -> Query.of(q -> q.match(matchQueryWithNameField(fieldValue)));
        return supplier;
   }
   
   public static MatchQuery matchQueryWithNameField(String fieldValue){
   	MatchQuery.Builder matchQueryBuilder = new MatchQuery.Builder();
       return matchQueryBuilder.field("name").query(fieldValue).fuzziness("AUTO").build();
   }
   
}
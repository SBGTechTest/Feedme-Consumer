package com.sbg.feedmetechconsumer.service.serviceImpl;


import com.sbg.feedmetechconsumer.service.IConsumerService;
import com.sbg.feedmetechconsumer.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ConsumerServiceImpl implements IConsumerService {
	private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	MongoTemplate mongoTemplate;

	@Transactional
	@Override
	public void saveDataInDatabase(JSONObject msg) {
		if(msg.optString(Constants.operation).equalsIgnoreCase("create")) {
			addData(msg);
		}
		else if(msg.optString(Constants.operation).equalsIgnoreCase("update")){ //added in else if as there might be some more operation types in future
			updateData(msg);
		}

	}

	private void updateData(JSONObject msg) {
		if (msg.optString("type").equalsIgnoreCase("event")) {
			updateEvent(msg);

		} else if (msg.optString("type").equalsIgnoreCase( "market")) {
			updateMarket(msg);

		} else if (msg.optString("type").equalsIgnoreCase("outcome")) {
			updateOutcome(msg);
		}
	}

	private void updateOutcome(JSONObject msg) {
		String result=mongoTemplate.findOne(new Query(Criteria.where("market.marketId").is(msg.optString(Constants.marketId))),String.class,Constants.repoName);
		List<Document> doc = new ArrayList<>();

		if(result!=null&&!result.isEmpty()) {
			JSONObject event= new JSONObject(result);
			JSONArray marketArr = event.optJSONArray("market");

			for (int i = 0; i < marketArr.length(); i++) {

				JSONObject updateOutcomeObj = marketArr.getJSONObject(i);
				if (updateOutcomeObj.optString(Constants.marketId).equalsIgnoreCase(msg.optString(Constants.marketId))) {
					JSONArray outcome= updateOutcomeObj.optJSONArray("outcome");
					for (int j = 0; j < outcome.length(); j++) {
						if(outcome.getJSONObject(i).optString(Constants.outcomeId).equalsIgnoreCase(Constants.outcomeId)){
							outcome.put(i, msg);

						}
						doc.add(Document.parse(updateOutcomeObj.toString()));

					}
				}

			}

			mongoTemplate.updateFirst(
					new Query(Criteria.where(Constants.eventId).is(event.optString(Constants.eventId))),
					new Update().set("market",doc), Constants.repoName);
		}
		log.info("successfully updated outcome");
	}

	private void updateMarket(JSONObject msg) {
		mongoTemplate.updateFirst(
				new Query(Criteria.where("market.marketId").is(msg.optString(Constants.marketId))),
				new Update()
						.set(Constants.name, msg.optString(Constants.name))
						.set(Constants.type, msg.optString(Constants.type))
						.set(Constants.displayed, msg.optBoolean(Constants.displayed))
						.set(Constants.suspended, msg.optBoolean(Constants.suspended))
						.set(Constants.operation, msg.optString(Constants.operation))
						.set(Constants.timestamp, msg.optInt(Constants.timestamp)),Constants.repoName);
		log.info("successfully updated market");
	}

	private void updateEvent(JSONObject msg) {
		mongoTemplate.updateFirst(
				new Query(Criteria.where(Constants.eventId).is(msg.optString(Constants.eventId))),
				new Update().set(Constants.subCategory, msg.optString(Constants.subCategory))
						.set(Constants.subCategory, msg.optString(Constants.subCategory))
						.set(Constants.name, msg.optString(Constants.name))
						.set(Constants.startTime, msg.optLong(Constants.startTime))
						.set(Constants.displayed, msg.optBoolean(Constants.displayed))
						.set(Constants.suspended, msg.optBoolean(Constants.suspended))
						.set(Constants.operation, msg.optString(Constants.operation))
						.set(Constants.timestamp, msg.optLong(Constants.timestamp))
				,Constants.repoName);
		log.info("successfully updated event");
	}

	private void addData(JSONObject msg) {
		if (msg.optString(Constants.type).equalsIgnoreCase("event")) {
			mongoTemplate.getCollection(Constants.repoName).insertOne(Document.parse(msg.toString()));
			log.info("successfully added event");
		} else if (msg.optString(Constants.type).equalsIgnoreCase("market")) {
			addMarket(msg);
		} else if (msg.optString(Constants.type).equalsIgnoreCase("outcome")) {

			addOutcome(msg);


		}
	}

	private void addOutcome(JSONObject msg) {
		List<Document> doc = new ArrayList<>();
		String result=mongoTemplate.findOne(new Query(Criteria.where("market.marketId").is(msg.optString(Constants.marketId))),String.class,Constants.repoName);
		if(result!=null&&!result.isEmpty()) {
			JSONObject event= new JSONObject(result);
			JSONArray marketArr = event.optJSONArray("market");
			for (int i = 0; i < marketArr.length(); i++) {

				JSONObject addOutcomeObj = marketArr.getJSONObject(i);
				if (addOutcomeObj.optString("marketId").equalsIgnoreCase(msg.optString(Constants.marketId))) {
					addOutcomeObj.put("outcome", new JSONArray().put(msg));
				}

				doc.add(Document.parse(addOutcomeObj.toString()));
			}

			mongoTemplate.updateFirst(
					new Query(Criteria.where(Constants.eventId).is(event.optString(Constants.eventId))),
					new Update().set("market",doc),Constants.repoName);
		}
		log.info("successfully added outcome");
	}

	private void addMarket(JSONObject msg) {
		Query query = new Query(Criteria.where(Constants.eventId).is(msg.optString(Constants.eventId)));

		Update update = new Update();
		update.addToSet("market",Document.parse(msg.toString()));
		mongoTemplate.updateFirst(query, update, "sbgRepository");
		log.info("successfully added market");
	}

}

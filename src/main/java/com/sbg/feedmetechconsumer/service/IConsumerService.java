package com.sbg.feedmetechconsumer.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

public interface IConsumerService {

	void saveDataInDatabase(JSONObject msg);
	

}

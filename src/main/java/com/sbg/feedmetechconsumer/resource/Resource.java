package com.sbg.feedmetechconsumer.resource;
import com.sbg.feedmetechconsumer.service.IConsumerService;
import com.sbg.feedmetechconsumer.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resource {
        private final Log log = LogFactory.getLog(this.getClass());

        private  IConsumerService iConsumerService;

        public Resource(IConsumerService iConsumerService) {
                this.iConsumerService = iConsumerService;
        }


        @KafkaListener(topics = Constants.kafkaTopic, groupId = Constants.consumerGroupId)
        public void consumeFromKafka(String message) {
                JSONObject msg = new JSONObject(message);
                iConsumerService.saveDataInDatabase(msg);
                log.info("successfully implemented consumeFromKafka");
        }


}

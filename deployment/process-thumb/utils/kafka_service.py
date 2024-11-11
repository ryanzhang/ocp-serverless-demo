from kafka import KafkaConsumer, KafkaProducer
import json
from datetime import datetime


class KafkaConsumerService:
    def __init__(self, bootstrap_servers, topic, group_id, store_service, thumb_service,  process_thumb_event_topic):
        self.bootstrap_servers = bootstrap_servers
        self.topic = topic
        self.group_id = group_id
        self.store_service = store_service
        self.thumb_service = thumb_service
        self.process_thumb_event_topic = process_thumb_event_topic

        self.consumer = KafkaConsumer(
            self.topic,
            bootstrap_servers=self.bootstrap_servers,
            group_id=self.group_id,
            auto_offset_reset='latest',
            enable_auto_commit=True,
            value_deserializer=lambda m: json.loads(m.decode('utf-8'))
        )

        self.producer = KafkaProducer(
            bootstrap_servers=self.bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode('utf-8')
        )

    def consume(self):
        print(f"Starting Kafka Consumer for topic: {self.topic}")
        for message in self.consumer:
            event = message.value
            print(f"Received event: {event}")
            # Check if the event has already been processed
            self.process_upload_complete_event(event)

    def process_upload_complete_event(self, event):
        try:
            file_name = event['fileName']
            file_url = event['fileUrl']
            file_size = event['fileSize']
            upload_time = event['uploadTime']

            # Download the image from S3
            image_bytes = self.store_service.download_image(file_url)

            # Resize the image to create a thumbnail
            thumb_bytes, thumb_size = self.thumb_service.resize_image(image_bytes)

            # Upload the thumbnail via REST API
            thumb_file_name = self.thumb_service.generate_thumbnail_filename(file_name)
            thumb_upload_response = self.store_service.upload_thumbnail(thumb_file_name, thumb_bytes)

            # Create and send the processThumbsComplete event
            process_event = {
                "eventType": "processThumbsComplete",
                "fileName": file_name,
                "fileUrl": file_url,
                "fileSize": file_size,
                "uploadTime": upload_time,
                # "thumbUrl": thumb_upload_response.get('fileUrl'),
                "thumbSize": thumb_size,
                "processTime": datetime.utcnow().isoformat()
            }

            self.producer.send(self.process_thumb_event_topic, process_event)
            self.producer.flush()
            print(f"Published processThumbsComplete event: {process_event}")

        except Exception as e:
            print(f"Error processing uploadComplete event: {e}")
            # Optionally, implement retry logic or send to a dead-letter queue

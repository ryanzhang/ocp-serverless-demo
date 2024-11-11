from flask import Flask
from flask_restful import Api
import yaml
import threading
from utils.kafka_service import KafkaConsumerService
from utils.store_service import StoreService
from utils.thumb_service import ThumbService
import os

app = Flask(__name__)
api = Api(app)

# Load configuration
with open('config.yaml', 'r') as f:
    config = yaml.safe_load(f)

# Initialize Services
store_service = StoreService(config['store_service'])
thumb_service = ThumbService(config['thumbnail'])
kafka_consumer = KafkaConsumerService(
    bootstrap_servers=config['kafka']['bootstrap_servers'],
    topic=config['kafka']['topic_upload_event'],
    group_id=config['kafka']['consumer_group_id'],
    store_service=store_service,
    thumb_service=thumb_service,
    process_thumb_event_topic=config['kafka']['topic_process_event']
)

# Start Kafka Consumer in a separate thread
def start_consumer():
    kafka_consumer.consume()

consumer_thread = threading.Thread(target=start_consumer)
consumer_thread.start()

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))  # Default to 5000 if PORT is not set
    app.run(host='0.0.0.0', port=port)

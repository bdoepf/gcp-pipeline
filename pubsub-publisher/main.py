from google.cloud import pubsub_v1
import time
import json
import os

project_id = os.environ['GCP_PROJECT']
topic_name = os.environ['TOPIC_NAME']

publisher = pubsub_v1.PublisherClient()
topic_path = publisher.topic_path(project_id, topic_name)


def callback(message_future):
    # When timeout is unspecified, the exception method waits indefinitely.
    if message_future.exception(timeout=30):
        print('Publishing message on {} threw an Exception {}.'.format(
            topic_name, message_future.exception()))
    else:
        print(message_future.result())


for n in range(1, 100):
    data_obj = {'a': n, 'b': 'The {} msg'.format(n)}
    data = json.dumps(data_obj)
    # Data must be a bytestring
    data = data.encode('utf-8')
    # When you publish a message, the client returns a Future.
    message_future = publisher.publish(topic_path, data=data)
    message_future.add_done_callback(callback)
    time.sleep(1)

print('Published message IDs:')

# We must keep the main thread from exiting to allow it to process
# messages in the background.
time.sleep(60)
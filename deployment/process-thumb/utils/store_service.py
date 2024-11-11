import requests
from urllib.parse import urlparse

class StoreService:
    def __init__(self, config):
        # Store the store-service URL from the config
        self.store_service_url = config['rest_api']
        self.upload_folder = config['upload_folder']

    def download_image(self, file_url):
        """
        Download an image from a public URL.
        """
        try:
            print(f"Downloading image from public URL: {file_url}")
            response = requests.get(file_url)
            response.raise_for_status()  # Raise an error for bad responses

            print(f"Downloaded {len(response.content)} bytes.")
            return response.content  # Return the image bytes

        except requests.exceptions.RequestException as e:
            print(f"Error downloading image: {e}")
            return None  # Or raise an exception if you prefer

    def upload_thumbnail(self, thumb_file_name, thumb_bytes):
        """
        Upload the thumbnail to the store service.
        """
        upload_url = f"{self.store_service_url}"  # Construct the URL with the folder name

        files = {'file': (thumb_file_name, thumb_bytes)}
        params = {
            'uploadFolder': self.upload_folder
        }
        try:
            print(f"Uploading thumbnail via REST API to {upload_url}")
            response = requests.post(upload_url, files=files, params=params)
            response.raise_for_status()  # Raise an error for bad responses

            print(f"Thumbnail uploaded successfully: {response.text}")
            # Assuming the API returns JSON with the file URL
            response_data = response.text
            return response_data

        except requests.exceptions.RequestException as e:
            print(f"Error uploading thumbnail: {e}")
            return None  # Or raise an exception if you prefer

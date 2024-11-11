from PIL import Image
import io

class ThumbService:
    def __init__(self, thumbnail_config):
        self.width = thumbnail_config['width']
        self.height = thumbnail_config['height']

    def resize_image(self, image_bytes):
        print(f"Resizing image to {self.width}x{self.height}")
        image = Image.open(io.BytesIO(image_bytes))
        image.thumbnail((self.width, self.height))
        thumb_io = io.BytesIO()
        image.save(thumb_io, format=image.format)
        thumb_bytes = thumb_io.getvalue()
        thumb_size = len(thumb_bytes)
        print(f"Resized image size: {thumb_size} bytes.")
        return thumb_bytes, thumb_size

    def generate_thumbnail_filename(self, original_filename):
        name, ext = original_filename.rsplit('.', 1)
        thumb_filename = f"{name}_thumb.{ext}"
        print(f"Generated thumbnail filename: {thumb_filename}")
        return thumb_filename

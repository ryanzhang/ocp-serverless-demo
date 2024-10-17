<template>
  <div>
    <h1>My Image Repo</h1>
    <table>
      <tbody>
        <tr v-for="(row, rowIndex) in imageRows" :key="rowIndex">
          <td v-for="(image, colIndex) in row" :key="colIndex">
            <img :src="image.url" :alt="image.name" @error="handleImageError($event, image.name)" />
            <span v-if="image.showName">{{ image.name }}</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// Ref for the image rows
const imageRows = ref([])

// Function to fetch images from the API
const fetchImages = async () => {
  try {
    const apiUrl = window.env.VITE_API_URL || import.meta.env.VITE_API_URL;
    const response = await fetch(apiUrl + '/fetch_thumbs')
    // const response = await fetch('http://localhost:8080/fetch_thumbs')

    // Check if the response is OK (status code 200-299)
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status} - ${response.statusText}`)
    }

    let data;
    try {
      // Attempt to parse the JSON data
      data = await response.json()
    } catch (jsonError) {
      throw new Error('Failed to parse JSON: ' + jsonError.message)
    }

    console.log('Fetched image data:', data)

    // Assuming the data is in the format of { name: "image_name", url: "image_url" }
    const images = data.map(image => ({
      name: image.name,
      url: image.url,
      showName: false // Flag to control if name should be shown in case of error
    }))

    // Create rows of 5 images each
    for (let i = 0; i < images.length; i += 5) {
      imageRows.value.push(images.slice(i, i + 5))
    }

  } catch (error) {
    console.error('Error fetching images:', error.message)
    // alert('Failed to load images. Please try again later.')
  }
}


// Function to handle image loading error
const handleImageError = (event, imageName) => {
  // Hide the broken image and display the image name
  event.target.style.display = 'none' // Hide the broken image
  const image = imageRows.value.flat().find(img => img.name === imageName)
  if (image) {
    image.showName = true // Show the name as fallback
  }
}

// Call fetchImages when the component is mounted
onMounted(() => {
  fetchImages()
})
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

td {
  padding: 10px;
  text-align: center;
}

img {
  max-width: 100px; /* Adjust as necessary */
  max-height: 100px; /* Adjust as necessary */
}

span {
  font-size: 12px; /* Style for fallback text */
  color: #555;
}
</style>
